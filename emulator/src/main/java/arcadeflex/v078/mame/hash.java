/**
 * ported to 0.78
 */
package arcadeflex.v078.mame;

//common imports
import static common.libc.cstring.*;

public class hash {

    /*TODO*////*
/*TODO*/// * DONE:	
/*TODO*/// *
/*TODO*/// * hash.c/h: New files, implement the new hashing engine with flexible
/*TODO*/// *    support for more functions (for now, CRC, SHA1 and MD5).
/*TODO*/// *
/*TODO*/// * common.h: transparently support the new RomModule structure through 
/*TODO*/// *    ROM_* macros, so that old the legacy code still work
/*TODO*/// *
/*TODO*/// * common.c: updated ROM loading engine to support the new hash engine,
/*TODO*/// *    using it to verify ROM integrity at load-time. Updated printromlist()
/*TODO*/// *    (-listroms) to dump all the available checksums, and if a ROM is
/*TODO*/// *    known to be bad or not.
/*TODO*/// *
/*TODO*/// * info.c: -listinfo now supports any hashing function correctly
/*TODO*/// *    (both text and XML mode). Notice that XML header should be
/*TODO*/// *    rewritten to automatically define the new tags when new
/*TODO*/// *    functions are added, but I couldn't be bothered for now.
/*TODO*/// *    It also displays informations about baddump/nodump
/*TODO*/// *
/*TODO*/// * audit.c/h: Updated audit engine (-verifyroms) to use the new
/*TODO*/// *    hash functions.
/*TODO*/// *
/*TODO*/// * fileio.c/h: Updated file engine to use the new hash functions.
/*TODO*/// *    It is now possible to load by any specified checksum (in case
/*TODO*/// *    later we support other archivers with SHA1 signatures or
/*TODO*/// *    equivalent). If the file is open with flag VERIFY_ONLY and
/*TODO*/// *    the file is within an archive (zip), only the checksums
/*TODO*/// *    available in the archive header are used.
/*TODO*/// *
/*TODO*/// * windows/fronthlp.c:  Updated -identrom to the new hash engine, now 
/*TODO*/// *    support any hash function available.
/*TODO*/// *    Added -crconly to disable advanced integrity checks.
/*TODO*/// *    This should be needed for people with very slow computers
/*TODO*/// *    whose loading time is affected too much by the new hashing
/*TODO*/// *    calculations (hello, stephh).
/*TODO*/// *    This also means that for -identrom MAME will not have to 
/*TODO*/// *    decompress the ROM from the ZIP to calculate checksum 
/*TODO*/// *    informations, since the CRC will be extracted from the header.
/*TODO*/// *    Added -listsha1 and -listmd5. It would be possible to add
/*TODO*/// *    also a -listbad now, to list bad dumps (ROMS we need a
/*TODO*/// *    redump for)
/*TODO*/// *    Updated -listdupcrc to check for all the available checksums.
/*TODO*/// *    The output is also a bit more useful and readable, even if it
/*TODO*/// *    is still not optimal.
/*TODO*/// *    Update -listwrongmerge to check for all the available checksums.
/*TODO*/// *
/*TODO*/// * mame.h: Added new field crc_only to struct GameOptions.
/*TODO*/// *
/*TODO*/// * windows/config.c: Added new option -crconly
/*TODO*/// *
/*TODO*/// * windows/fileio.c: Removed check for FILE_TYPE_NOCRC (does not exist
/*TODO*/// *    anymore).
/*TODO*/// *
/*TODO*/// *
/*TODO*/// *
/*TODO*/// * Technical details:
/*TODO*/// *
/*TODO*/// * Checksum informations are now stored inside a string. They are 
/*TODO*/// * stored in "printable hex format", which means that they use
/*TODO*/// * more memory than before (since a CRC needs 8 characters to
/*TODO*/// * be printed, instead of 4 bytes of raw information). In the
/*TODO*/// * driver, they are defined with handy macros which rely on 
/*TODO*/// * automatic string pasting. 
/*TODO*/// *
/*TODO*/// * Additional flags can also be stored in the string: for now we
/*TODO*/// * support NO_DUMP and BAD_DUMP, which replace, respectively, 
/*TODO*/// * a CRC of 0 and a bit-inverted CRC.
/*TODO*/// *
/*TODO*/// * All the code that handles hash data is in hash.c. The rest of
/*TODO*/// * the core treats the data as an 'opaque type', so that the 
/*TODO*/// * pointers are just passed along through functions but no 
/*TODO*/// * operation is performed on the data outside hash.c. This
/*TODO*/// * is important in case we want to change the string 
/*TODO*/// * representation later in the future.
/*TODO*/// *
/*TODO*/// * When loading a ROM, MAME will calculate and compare the 
/*TODO*/// * checksum using any function for which the driver has declared
/*TODO*/// * an expected checksum. This happens because it would be useless
/*TODO*/// * to calculate a checksum if we cannot verify its correctness.
/*TODO*/// * For developers, it also means that MAME will not compute the
/*TODO*/// * SHA1 for you unless you specify a bogus one in the driver 
/*TODO*/// * (like SHA1(0)). 
/*TODO*/// * 
/*TODO*/// * When verifying a ROM, MAME will use only the checksums available
/*TODO*/// * in the archive header (if zip, CRC). This is by design because
/*TODO*/// * -verifyroms has always been very fast. It is feasible to add
/*TODO*/// * a -fullverifyroms at a later moment, which will decompress the
/*TODO*/// * files and compute every checksum that has been declared in the
/*TODO*/// * driver. 
/*TODO*/// *
/*TODO*/// * I have also prepared a little tool (SHA1Merger) which takes care
/*TODO*/// * of the following tasks:
/*TODO*/// *
/*TODO*/// * - Given an existing driver in old syntax (0.66 compatible), it will
/*TODO*/// *   convert all the existing ROM_LOAD entries in the new format, and
/*TODO*/// *   it will automatically compute and add SHA1 checksum for you if
/*TODO*/// *   it can find the romset. 
/*TODO*/// *
/*TODO*/// * - Given a romset (ZIP file), it will prepare a ROM definition 
/*TODO*/// *   skeleton for a driver, containing already rom names, lengths, and 
/*TODO*/// *   checksums (both CRC and SHA1). 
/*TODO*/// *
/*TODO*/// * The tool is available on www.mame.net as platform-independent source code
/*TODO*/// * (in Python), or win32 standalone executable.
/*TODO*/// *
/*TODO*/// */
/*TODO*///
/*TODO*///#include <stddef.h>
/*TODO*///#include <ctype.h>
/*TODO*///#include <string.h>
/*TODO*///#include <stdlib.h>
/*TODO*///#include <zlib.h>
/*TODO*///#include "hash.h"
/*TODO*///#include "md5.h"
/*TODO*///#include "sha1.h"
/*TODO*///#include "osd_cpu.h"
/*TODO*///#include "mame.h"
/*TODO*///#include "common.h"
/*TODO*///
/*TODO*///#define ASSERT(x)
/*TODO*///
/*TODO*///#ifndef TRUE
/*TODO*///#define TRUE    1
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifndef FALSE
/*TODO*///#define FALSE   0
/*TODO*///#endif
/*TODO*///
/*TODO*///typedef struct 
/*TODO*///{
/*TODO*///	const char* name;           // human-readable name
/*TODO*///	char code;                  // single-char code used within the hash string
/*TODO*///	unsigned int size;          // checksum size in bytes
/*TODO*///	
/*TODO*///	// Functions used to calculate the hash of a memory block
/*TODO*///	void (*calculate_begin)(void);
/*TODO*///	void (*calculate_buffer)(const void* mem, unsigned long len);
/*TODO*///	void (*calculate_end)(UINT8* bin_chksum);
/*TODO*///
/*TODO*///} hash_function_desc;
/*TODO*///
/*TODO*///static void h_crc_begin(void);
/*TODO*///static void h_crc_buffer(const void* mem, unsigned long len);
/*TODO*///static void h_crc_end(UINT8* chksum);
/*TODO*///
/*TODO*///static void h_sha1_begin(void);
/*TODO*///static void h_sha1_buffer(const void* mem, unsigned long len);
/*TODO*///static void h_sha1_end(UINT8* chksum);
/*TODO*///
/*TODO*///static void h_md5_begin(void);
/*TODO*///static void h_md5_buffer(const void* mem, unsigned long len);
/*TODO*///static void h_md5_end(UINT8* chksum);
/*TODO*///
/*TODO*///static hash_function_desc hash_descs[HASH_NUM_FUNCTIONS] =
/*TODO*///{
/*TODO*///	{
/*TODO*///		"crc", 'c', 4,
/*TODO*///		h_crc_begin,
/*TODO*///		h_crc_buffer,
/*TODO*///		h_crc_end
/*TODO*///	},
/*TODO*///
/*TODO*///	{
/*TODO*///		"sha1", 's', 20,
/*TODO*///		h_sha1_begin,
/*TODO*///		h_sha1_buffer,
/*TODO*///		h_sha1_end
/*TODO*///	},
/*TODO*///
/*TODO*///	{
/*TODO*///		"md5", 'm', 16,
/*TODO*///		h_md5_begin,
/*TODO*///		h_md5_buffer,
/*TODO*///		h_md5_end
/*TODO*///	},
/*TODO*///};
/*TODO*///
    static String info_strings[]
            = {
                "NO_DUMP",/*"$ND$", */ // No dump
                "BAD_DUMP"/*"$BD$"  */ // Bad dump
            };

    /*TODO*///
/*TODO*///static const char* binToStr = "0123456789abcdef";
/*TODO*///
/*TODO*///
/*TODO*///static hash_function_desc* hash_get_function_desc(unsigned int function)
/*TODO*///{
/*TODO*///	unsigned int idx = 0;
/*TODO*///
/*TODO*///	// Calling with zero in here is mostly an internal error
/*TODO*///	ASSERT(function != 0);
/*TODO*///
/*TODO*///	// Compute the index of only one function
/*TODO*///	while (!(function & 1))
/*TODO*///	{
/*TODO*///		idx++;
/*TODO*///		function >>= 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	// Specify only one bit or die
/*TODO*///	ASSERT(function == 1);
/*TODO*///
/*TODO*///	return &hash_descs[idx];
/*TODO*///}
/*TODO*///
/*TODO*///const char* hash_function_name(unsigned int function)
/*TODO*///{
/*TODO*///	hash_function_desc* info = hash_get_function_desc(function);
/*TODO*///
/*TODO*///	return info->name;
/*TODO*///}
/*TODO*///
/*TODO*///int hash_data_has_checksum(const char* data, unsigned int function)
/*TODO*///{
/*TODO*///	hash_function_desc* info = hash_get_function_desc(function);
/*TODO*///	char str[3];
/*TODO*///	const char* res;
/*TODO*///
/*TODO*///	str[0] = info->code; 
/*TODO*///	str[1] = ':'; 
/*TODO*///	str[2] = '\0';
/*TODO*///
/*TODO*///	// Check if the specified hash function is used within this data
/*TODO*///	res = strstr(data, str);
/*TODO*///
/*TODO*///	if (!res)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	// Return the offset within the string where the checksum begins
/*TODO*///	return (res - data + 2);
/*TODO*///}
/*TODO*///
/*TODO*///static int hash_data_add_binary_checksum(char* d, unsigned int function, UINT8* checksum)
/*TODO*///{
/*TODO*///	hash_function_desc* desc = hash_get_function_desc(function);
/*TODO*///	char* start = d;
/*TODO*///	unsigned i;
/*TODO*///	
/*TODO*///	*d++ = desc->code;
/*TODO*///	*d++ = ':';
/*TODO*///
/*TODO*///	for (i=0;i<desc->size;i++)
/*TODO*///	{
/*TODO*///		UINT8 c = *checksum++;
/*TODO*///
/*TODO*///		*d++ = binToStr[(c >> 4) & 0xF];
/*TODO*///		*d++ = binToStr[(c >> 0) & 0xF];
/*TODO*///	}
/*TODO*///	
/*TODO*///	*d++ = '#';
/*TODO*///
/*TODO*///	// Return the number of written bytes
/*TODO*///	return (d - start);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static int hash_compare_checksum(const char* chk1, const char* chk2, int length)
/*TODO*///{
/*TODO*///	char c1, c2;
/*TODO*///
/*TODO*///	// The printable format is twice as longer
/*TODO*///	length *= 2;
/*TODO*///
/*TODO*///	// This is basically a case-insensitive string compare
/*TODO*///	while (length--)
/*TODO*///	{
/*TODO*///		c1 = *chk1++;
/*TODO*///		c2 = *chk2++;
/*TODO*///
/*TODO*///		if (tolower(c1) != tolower(c2))
/*TODO*///			return 0;
/*TODO*///		if (!c1)
/*TODO*///			return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///// Compare two hashdata
/*TODO*///int hash_data_is_equal(const char* d1, const char* d2, unsigned int functions)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	char incomplete = 0;
/*TODO*///	char ok = 0;
/*TODO*///	
/*TODO*///	// If no function is specified, it means we need to check for all
/*TODO*///	//  of them
/*TODO*///	if (!functions)
/*TODO*///		functions = ~functions;
/*TODO*///
/*TODO*///	for (i=1; i != (1<<HASH_NUM_FUNCTIONS); i<<=1)
/*TODO*///		if (functions & i)
/*TODO*///		{
/*TODO*///			int offs1, offs2;
/*TODO*///
/*TODO*///			// Check if both hashdata contain the current function's checksum
/*TODO*///			offs1 = hash_data_has_checksum(d1, i);
/*TODO*///			offs2 = hash_data_has_checksum(d2, i);
/*TODO*///
/*TODO*///			if (offs1 && offs2)
/*TODO*///			{
/*TODO*///				hash_function_desc* info = hash_get_function_desc(i);
/*TODO*///
/*TODO*///				if (!hash_compare_checksum(d1+offs1, d2+offs2, info->size))
/*TODO*///					return 0;
/*TODO*///
/*TODO*///				ok = 1;
/*TODO*///			}
/*TODO*///			// If the function was contained only in one, remember that our comparison
/*TODO*///			//  is incomplete
/*TODO*///			else if (offs1 || offs2)
/*TODO*///			{
/*TODO*///				incomplete = 1;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///	// If we could not compare any function, return error
/*TODO*///	if (!ok)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	// Return success code
/*TODO*///	return (incomplete ? 2 : 1);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///int hash_data_extract_printable_checksum(const char* data, unsigned int function, char* checksum)
/*TODO*///{
/*TODO*///	unsigned int i;
/*TODO*///	hash_function_desc* info;
/*TODO*///	int offs;
/*TODO*///	
/*TODO*///	// Check if the hashdata contains the requested function
/*TODO*///	offs = hash_data_has_checksum(data, function);
/*TODO*///	
/*TODO*///	if (!offs)
/*TODO*///		return 0;
/*TODO*///	
/*TODO*///	// Move to the beginning of the checksum
/*TODO*///	data += offs;
/*TODO*///	
/*TODO*///	info = hash_get_function_desc(function);
/*TODO*///
/*TODO*///	// Return the number of required bytes
/*TODO*///	if (!checksum)
/*TODO*///		return info->size*2+1;
/*TODO*///
/*TODO*///	// If the terminator is not found at the right position,
/*TODO*///	//  return a full-zero checksum and warn about it. This is mainly
/*TODO*///	//  for developers putting checksums of '0' or '1' to ask MAME
/*TODO*///	//  to compute the correct values for them.
/*TODO*///	if (data[info->size*2] != '#')
/*TODO*///	{
/*TODO*///		memset(checksum, '0', info->size*2);
/*TODO*///		checksum[info->size*2] = '\0';
/*TODO*///		return 2;
/*TODO*///	}
/*TODO*///
/*TODO*///	// If it contains invalid hexadecimal characters,
/*TODO*///	//  treat the checksum as zero and return warning
/*TODO*///	for (i=0;i<info->size*2;i++)
/*TODO*///		if (!(data[i]>='0' && data[i]<='9') &&
/*TODO*///			!(data[i]>='a' && data[i]<='f') &&
/*TODO*///			!(data[i]>='A' && data[i]<='F'))
/*TODO*///		{
/*TODO*///			memset(checksum, '0', info->size*2);
/*TODO*///			checksum[info->size*2] = '\0';
/*TODO*///			return 2;
/*TODO*///		}
/*TODO*///	
/*TODO*///	// Copy the checksum (and make it lowercase)
/*TODO*///	for (i=0;i<info->size*2;i++)
/*TODO*///		checksum[i] = tolower(data[i]);
/*TODO*///
/*TODO*///	checksum[info->size*2] = '\0';
/*TODO*///
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///int hash_data_extract_binary_checksum(const char* data, unsigned int function, unsigned char* checksum)
/*TODO*///{
/*TODO*///	unsigned int i;
/*TODO*///	hash_function_desc* info;
/*TODO*///	int offs;
/*TODO*///	
/*TODO*///	// Check if the hashdata contains the requested function
/*TODO*///	offs = hash_data_has_checksum(data, function);
/*TODO*///
/*TODO*///	if (!offs)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	// Move to the beginning of the checksum
/*TODO*///	data += offs;
/*TODO*///
/*TODO*///	info = hash_get_function_desc(function);
/*TODO*///
/*TODO*///	// Return the number of required bytes
/*TODO*///	if (!checksum)
/*TODO*///		return info->size;
/*TODO*///
/*TODO*///	// Clear the checksum array
/*TODO*///	memset(checksum, 0, info->size);
/*TODO*///
/*TODO*///	// If the terminator is not found at the right position,
/*TODO*///	//  return a full-zero checksum and warn about it. This is mainly
/*TODO*///	//  for developers putting checksums of '0' or '1' to ask MAME
/*TODO*///	//  to compute the correct values for them.
/*TODO*///	if (data[info->size*2] != '#')
/*TODO*///	{
/*TODO*///		memset(checksum, '\0', info->size);
/*TODO*///		return 2;
/*TODO*///	}
/*TODO*///	
/*TODO*///	// Convert hex string into binary
/*TODO*///	for (i=0;i<info->size*2;i++)
/*TODO*///	{
/*TODO*///		char c = tolower(*data++);
/*TODO*///		
/*TODO*///		if (c >= '0' && c <= '9')
/*TODO*///			c -= '0';
/*TODO*///		else if (c >= 'a' && c <= 'f')
/*TODO*///			c -= 'a' - 10;
/*TODO*///		else if (c >= 'A' && c <= 'F')
/*TODO*///			c -= 'A' - 10;
/*TODO*///		else
/*TODO*///		{
/*TODO*///			// Invalid character: the checksum is treated as zero,
/*TODO*///			//  and a warning is returned
/*TODO*///			memset(checksum, '\0', info->size);
/*TODO*///			return 2;
/*TODO*///		}
/*TODO*///
/*TODO*///		if (i % 2 == 0)
/*TODO*///			checksum[i / 2] = c * 16;
/*TODO*///		else
/*TODO*///			checksum[i / 2] += c;
/*TODO*///	}
/*TODO*///
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
    public static int hash_data_has_info(String data, int info) {
        int res = strstr(data, info_strings[info]);

        if (res == -1) {
            return 0;
        }

        return 1;
    }

    /*TODO*///
/*TODO*///void hash_data_copy(char* dst, const char* src)
/*TODO*///{
/*TODO*///	// Copying string is enough
/*TODO*///	strcpy(dst, src);
/*TODO*///}
/*TODO*///
/*TODO*///void hash_data_clear(char* dst)
/*TODO*///{
/*TODO*///	// Clear the buffer
/*TODO*///	memset(dst, 0, HASH_BUF_SIZE);
/*TODO*///}
/*TODO*///
/*TODO*///unsigned int hash_data_used_functions(const char* data)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	unsigned int res = 0;
/*TODO*///
/*TODO*///	if (!data)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	for (i=0;i<HASH_NUM_FUNCTIONS;i++)
/*TODO*///		if (hash_data_has_checksum(data, 1<<i))
/*TODO*///			res |= 1<<i;
/*TODO*///
/*TODO*///	return res;
/*TODO*///}
/*TODO*///
/*TODO*///int hash_data_insert_binary_checksum(char* d, unsigned int function, UINT8* checksum)
/*TODO*///{
/*TODO*///	int offset;
/*TODO*///	
/*TODO*///	offset = hash_data_has_checksum(d, function);
/*TODO*///
/*TODO*///	if (!offset)
/*TODO*///	{
/*TODO*///		d += strlen(d);
/*TODO*///		d += hash_data_add_binary_checksum(d, function, checksum);
/*TODO*///		*d = '\0';
/*TODO*///
/*TODO*///		return 1;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		// Move to the start of the whole checksum signature, not only to the checksum
/*TODO*///		// itself
/*TODO*///		d += offset - 2;
/*TODO*///		
/*TODO*///		// Overwrite previous checksum with new one
/*TODO*///		hash_data_add_binary_checksum(d, function, checksum);
/*TODO*///
/*TODO*///		return 2;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///void hash_compute(char* dst, const unsigned char* data, unsigned long length, unsigned int functions)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	hash_data_clear(dst);	
/*TODO*///
/*TODO*///	// Zero means use all the functions
/*TODO*///	if (functions == 0)
/*TODO*///		functions = ~functions;
/*TODO*///
/*TODO*///	for (i=0;i<HASH_NUM_FUNCTIONS;i++)
/*TODO*///	{
/*TODO*///		unsigned func = 1 << i;
/*TODO*///		
/*TODO*///		if (functions & func)
/*TODO*///		{
/*TODO*///			hash_function_desc* desc = hash_get_function_desc(func);
/*TODO*///			UINT8 chksum[256];
/*TODO*///
/*TODO*///			desc->calculate_begin();
/*TODO*///			desc->calculate_buffer(data, length);
/*TODO*///			desc->calculate_end(chksum);
/*TODO*///
/*TODO*///			dst += hash_data_add_binary_checksum(dst, func, chksum);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	*dst = '\0';
/*TODO*///}
/*TODO*///
    public static String hash_data_print(String data, int functions) {
        data = data.replace("BAD_DUMP", "");//remove BAD_DUMP it is already printed
        return data;//TODO fixed it better
/*TODO*///	int i, j;
/*TODO*///	char first = 1;
/*TODO*///
/*TODO*///	if (functions == 0)
/*TODO*///		functions = ~functions;
/*TODO*///
/*TODO*///	buffer[0] = '\0';
/*TODO*///
/*TODO*///	for (i=0;i<HASH_NUM_FUNCTIONS;i++)
/*TODO*///	{
/*TODO*///		unsigned func = 1 << i;
/*TODO*///
/*TODO*///		if ((functions & func) && hash_data_has_checksum(data, func))
/*TODO*///		{
/*TODO*///			char temp[256];
/*TODO*///
/*TODO*///			if (!first)
/*TODO*///				strcat(buffer, " ");
/*TODO*///			first = 0;
/*TODO*///			
/*TODO*///			strcpy(temp, hash_function_name(func));
/*TODO*///			for (j = 0; temp[j]; j++)
/*TODO*///				temp[j] = toupper(temp[j]);
/*TODO*///			strcat(buffer, temp);
/*TODO*///			strcat(buffer, "(");
/*TODO*///            
/*TODO*///			hash_data_extract_printable_checksum(data, func, temp);
/*TODO*///			strcat(buffer, temp);
/*TODO*///			strcat(buffer, ")");
/*TODO*///		}	
/*TODO*///	}
    }
    /*TODO*///
/*TODO*///int hash_verify_string(const char *hash)
/*TODO*///{
/*TODO*///	int len, i;
/*TODO*///
/*TODO*///	if (!hash)
/*TODO*///		return FALSE;
/*TODO*///
/*TODO*///	while(*hash)
/*TODO*///	{
/*TODO*///		if (*hash == '$')
/*TODO*///		{
/*TODO*///			if (memcmp(hash, NO_DUMP, 4) && memcmp(hash, BAD_DUMP, 4))
/*TODO*///				return FALSE;
/*TODO*///			hash += 4;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			/* first make sure that the next char is a colon */
/*TODO*///			if (hash[1] != ':')
/*TODO*///				return FALSE;
/*TODO*///
/*TODO*///			/* search for a hash function for this code */
/*TODO*///			for (i = 0; i < sizeof(hash_descs) / sizeof(hash_descs[0]); i++)
/*TODO*///			{
/*TODO*///				if (*hash == hash_descs[i].code)
/*TODO*///					break;
/*TODO*///			}
/*TODO*///			if (i >= sizeof(hash_descs) / sizeof(hash_descs[0]))
/*TODO*///				return FALSE;
/*TODO*///
/*TODO*///			/* we have a proper code */
/*TODO*///			len = hash_descs[i].size * 2;
/*TODO*///			hash += 2;
/*TODO*///			
/*TODO*///			for (i = 0; (hash[i] != '#') && (i < len); i++)
/*TODO*///			{
/*TODO*///				if (!isxdigit(hash[i]))
/*TODO*///					return FALSE;
/*TODO*///			}
/*TODO*///			if (hash[i] != '#')
/*TODO*///				return FALSE;
/*TODO*///
/*TODO*///			hash += i+1;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return TRUE;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*********************************************************************
/*TODO*///	Hash functions - Wrappers
/*TODO*/// *********************************************************************/
/*TODO*///
/*TODO*///static UINT32 crc;
/*TODO*///
/*TODO*///static void h_crc_begin(void)
/*TODO*///{
/*TODO*///	crc = 0;
/*TODO*///}
/*TODO*///
/*TODO*///static void h_crc_buffer(const void* mem, unsigned long len)
/*TODO*///{
/*TODO*///	crc = crc32(crc, (UINT8*)mem, len);
/*TODO*///}
/*TODO*///
/*TODO*///static void h_crc_end(UINT8* bin_chksum)
/*TODO*///{
/*TODO*///	bin_chksum[0] = (UINT8)(crc >> 24);
/*TODO*///	bin_chksum[1] = (UINT8)(crc >> 16);
/*TODO*///	bin_chksum[2] = (UINT8)(crc >> 8);
/*TODO*///	bin_chksum[3] = (UINT8)(crc >> 0);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///struct sha1_ctx sha1ctx;
/*TODO*///
/*TODO*///static void h_sha1_begin(void)
/*TODO*///{
/*TODO*///	sha1_init(&sha1ctx);
/*TODO*///}
/*TODO*///
/*TODO*///static void h_sha1_buffer(const void* mem, unsigned long len)
/*TODO*///{
/*TODO*///	sha1_update(&sha1ctx, len, (UINT8*)mem);
/*TODO*///}
/*TODO*///
/*TODO*///static void h_sha1_end(UINT8* bin_chksum)
/*TODO*///{
/*TODO*///	sha1_final(&sha1ctx);
/*TODO*///	sha1_digest(&sha1ctx, 20, bin_chksum);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static struct MD5Context md5_ctx;
/*TODO*///
/*TODO*///static void h_md5_begin(void)
/*TODO*///{
/*TODO*///	MD5Init(&md5_ctx);		
/*TODO*///}
/*TODO*///
/*TODO*///static void h_md5_buffer(const void* mem, unsigned long len)
/*TODO*///{
/*TODO*///	MD5Update(&md5_ctx, (md5byte*)mem, len);
/*TODO*///}
/*TODO*///
/*TODO*///static void h_md5_end(UINT8* bin_chksum)
/*TODO*///{
/*TODO*///	MD5Final(bin_chksum, &md5_ctx);
/*TODO*///}
/*TODO*///    
}
