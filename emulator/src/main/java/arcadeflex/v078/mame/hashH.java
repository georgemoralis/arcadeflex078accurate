/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

public class hashH {
/*TODO*////*********************************************************************
/*TODO*///
/*TODO*///	hash.h
/*TODO*///
/*TODO*///	Function to handle hash functions (checksums)
/*TODO*///
/*TODO*///*********************************************************************/
/*TODO*///
/*TODO*///#ifndef HASH_H
/*TODO*///#define HASH_H
/*TODO*///
/*TODO*///#ifdef __cplusplus
/*TODO*///extern "C" {
/*TODO*///#endif
/*TODO*///
/*TODO*///#define HASH_INFO_NO_DUMP	0
/*TODO*///#define HASH_INFO_BAD_DUMP	1
/*TODO*///
/*TODO*///#define HASH_CRC    (1 << 0)
/*TODO*///#define HASH_SHA1   (1 << 1)
/*TODO*///#define HASH_MD5    (1 << 2)
/*TODO*///
/*TODO*///#define HASH_NUM_FUNCTIONS  3
/*TODO*///
/*TODO*///// Standard size of a hash data buffer, all the manipulated buffers
/*TODO*/////  must respect this size
/*TODO*///#define HASH_BUF_SIZE       256
/*TODO*///
/*TODO*///// Get function name of the specified function
/*TODO*///const char* hash_function_name(unsigned int function);
/*TODO*///
/*TODO*///// Check if const char* contains the checksum for a specific function
/*TODO*///int hash_data_has_checksum(const char* d, unsigned int function);
/*TODO*///
/*TODO*///// Extract the binary or printable checksum of a specific function from a hash data. If the checksum information
/*TODO*/////  is not available, the functions return 0. If the pointer to the output buffer is NULL, the function will
/*TODO*/////  return the minimum size of the output buffer required to store the informations. Otherwise, the buffer
/*TODO*/////  will be filled and the function will return 1 as success code.
/*TODO*///int hash_data_extract_binary_checksum(const char* d, unsigned int function, unsigned char* checksum);
/*TODO*///int hash_data_extract_printable_checksum(const char* d, unsigned int function, char* checksum);
/*TODO*///
/*TODO*///// Insert an already computed binary checksum inside a hash data. This is useful when we already have
/*TODO*/////  checksum informations (e.g, from archive headers) and we want to prepare a hash data to compare
/*TODO*/////  with another const char* (e.g. the expected checksums). Returns 0 in case of error, 1 if the checksum
/*TODO*/////  was added correctly, 2 if the checksum was added overwriting a previously existing checksum for the
/*TODO*/////  the same function
/*TODO*///int hash_data_insert_binary_checksum(char* d, unsigned int function, unsigned char* checksum);
/*TODO*///
/*TODO*///// Check if the hash data contains the requested info
/*TODO*///int hash_data_has_info(const char* d, unsigned int info);
/*TODO*///
/*TODO*///// Compare two hash data to check if they are the same. 'functions' can be either a combination of the
/*TODO*/////  hash function bits (HASH_CRC, etc) or zero to ask to check for all the available checksums
/*TODO*///int hash_data_is_equal(const char* d1, const char* d2, unsigned int functions);
/*TODO*///
/*TODO*///// Print hash data informations in a standard format. 'functions' can be either a combination of the
/*TODO*/////  hash function bits (HASH_CRC, etc) or zero to ask to print all the available checksums
/*TODO*///void hash_data_print(const char* d, unsigned int functions, char* buffer);
/*TODO*///
/*TODO*///// Copy hash data informations
/*TODO*///void hash_data_copy(char* dst, const char* src);
/*TODO*///
/*TODO*///// Clear hash data informations
/*TODO*///void hash_data_clear(char* dst);
/*TODO*///
/*TODO*///// Check which functions we have a checksum of inside the data
/*TODO*///unsigned int hash_data_used_functions(const char* d);
/*TODO*///
/*TODO*///// Compute hash of a data chunk in memory. Parameter 'functions' specifies which hashing functions
/*TODO*/////  we want the checksum of.
/*TODO*///void hash_compute(char* dst, const unsigned char* data, unsigned long length, unsigned int functions);
/*TODO*///
/*TODO*///// Verifies that a hash string is valid
/*TODO*///int hash_verify_string(const char *hash);
/*TODO*///
/*TODO*///#ifdef __cplusplus
/*TODO*///}
/*TODO*///#endif
/*TODO*///	
/*TODO*///#endif
/*TODO*///    
}
