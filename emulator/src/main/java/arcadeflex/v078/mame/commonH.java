/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

import java.util.ArrayList;

public class commonH {

    /*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Type definitions
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
    public static class mame_bitmap {
        /*TODO*///	int width,height;	/* width and height of the bitmap */
/*TODO*///	int depth;			/* bits per pixel */
/*TODO*///	void **line;		/* pointers to the start of each line - can be UINT8 **, UINT16 ** or UINT32 ** */
/*TODO*///
/*TODO*///	/* alternate way of accessing the pixels */
/*TODO*///	void *base;			/* pointer to pixel (0,0) (adjusted for padding) */
/*TODO*///	int rowpixels;		/* pixels per row (including padding) */
/*TODO*///	int rowbytes;		/* bytes per row (including padding) */
/*TODO*///
/*TODO*///	/* functions to render in the correct orientation */
/*TODO*///	void (*plot)(struct mame_bitmap *bitmap,int x,int y,pen_t pen);
/*TODO*///	pen_t (*read)(struct mame_bitmap *bitmap,int x,int y);
/*TODO*///	void (*plot_box)(struct mame_bitmap *bitmap,int x,int y,int width,int height,pen_t pen);
    }

    public static class RomModule {

        public RomModule(String _name, int _offset, int _length, int _flags, String _hashdata) {
            this._name = _name;
            this._offset = _offset;
            this._length = _length;
            this._flags = _flags;
            this._hashdata = _hashdata;
        }

        public String _name;/* name of the file to load */
        public int/*UINT32*/ _offset;/* offset to load it to */
        public int/*UINT32*/ _length;/* length of the file */
        public int/*UINT32*/ _flags;/* flags */
        public String _hashdata;/* alternate verification, MD5 or SHA */
    }

    public static class GameSample {

        public GameSample() {
            data = new byte[1];
        }

        public GameSample(int len) {
            data = new byte[len];
        }
        public int length;
        public int smpfreq;
        public int resolution;
        public byte data[];/* extendable */
    }

    /*TODO*///
/*TODO*///
/*TODO*///struct SystemBios
/*TODO*///{
/*TODO*///	int value;			/* value of mask to apply to ROM_BIOSFLAGS is chosen */
/*TODO*///	const char *_name;	/* name of the bios, e.g "default","japan" */
/*TODO*///	const char *_description;	/* long name of the bios, e.g "Europe MVS (Ver. 2)" */
/*TODO*///};
/*TODO*///
/*TODO*///
/*TODO*///struct rom_load_data
/*TODO*///{
/*TODO*///	int warnings;				/* warning count during processing */
/*TODO*///	int errors;				/* error count during processing */
/*TODO*///
/*TODO*///	int romsloaded;				/* current ROMs loaded count */
/*TODO*///	int romstotal;				/* total number of ROMs to read */
/*TODO*///
/*TODO*///	void * file;				/* current file */
/*TODO*///
/*TODO*///	UINT8 *	regionbase;			/* base of current region */
/*TODO*///	UINT32 regionlength;			/* length of current region */
/*TODO*///
/*TODO*///	char errorbuf[4096];			/* accumulated errors */
/*TODO*///	UINT8 tempbuf[65536];			/* temporary buffer */
/*TODO*///};
/*TODO*///
/*TODO*///
    public static class GameSamples {

        public GameSamples() {
            sample = new GameSample[1];
            sample[0] = new GameSample();
        }

        public GameSamples(int size) {
            sample = new GameSample[size];
            for (int i = 0; i < size; i++) {
                sample[i] = new GameSample(1);
            }
        }
        public int total;/* total number of samples */
        public GameSample sample[];/* extendable */
    }

    /**
     * *************************************************************************
     *
     * Constants and macros
     *
     **************************************************************************
     */
    public static final int REGION_INVALID = 0x80;
    public static final int REGION_CPU1 = 0x81;
    public static final int REGION_CPU2 = 0x82;
    public static final int REGION_CPU3 = 0x83;
    public static final int REGION_CPU4 = 0x84;
    public static final int REGION_CPU5 = 0x85;
    public static final int REGION_CPU6 = 0x86;
    public static final int REGION_CPU7 = 0x87;
    public static final int REGION_CPU8 = 0x88;
    public static final int REGION_GFX1 = 0x89;
    public static final int REGION_GFX2 = 0x8A;
    public static final int REGION_GFX3 = 0x8B;
    public static final int REGION_GFX4 = 0x8C;
    public static final int REGION_GFX5 = 0x8D;
    public static final int REGION_GFX6 = 0x8E;
    public static final int REGION_GFX7 = 0x8F;
    public static final int REGION_GFX8 = 0x90;
    public static final int REGION_PROMS = 0x91;
    public static final int REGION_SOUND1 = 0x92;
    public static final int REGION_SOUND2 = 0x93;
    public static final int REGION_SOUND3 = 0x94;
    public static final int REGION_SOUND4 = 0x95;
    public static final int REGION_SOUND5 = 0x96;
    public static final int REGION_SOUND6 = 0x97;
    public static final int REGION_SOUND7 = 0x98;
    public static final int REGION_SOUND8 = 0x99;
    public static final int REGION_USER1 = 0x9A;
    public static final int REGION_USER2 = 0x9B;
    public static final int REGION_USER3 = 0x9C;
    public static final int REGION_USER4 = 0x9D;
    public static final int REGION_USER5 = 0x9E;
    public static final int REGION_USER6 = 0x9F;
    public static final int REGION_USER7 = 0x100;
    public static final int REGION_USER8 = 0x101;
    public static final int REGION_DISKS = 0x102;
    public static final int REGION_MAX = 0x103;

    /*TODO*///#define BADCRC( crc ) (~(crc))
/*TODO*///
/*TODO*///#define ROMMD5(md5) ("MD5" #md5)
/*TODO*///
/*TODO*///
    /**
     * *************************************************************************
     *
     * Core macros for the ROM loading system
     *
     **************************************************************************
     */
    /* ----- per-entry constants ----- */
//arcadeflex note : use big numbers in case game has rom named "1" to "6" this has been found in wiping driver
    public static final int ROMENTRYTYPE_REGION = 1001;/* this entry marks the start of a region */
    public static final int ROMENTRYTYPE_END = 1002;/* this entry marks the end of a region */
    public static final int ROMENTRYTYPE_RELOAD = 1003;/* this entry reloads the previous ROM */
    public static final int ROMENTRYTYPE_CONTINUE = 1004;/* this entry continues loading the previous ROM */
    public static final int ROMENTRYTYPE_FILL = 1005;/* this entry fills an area with a constant value */
    public static final int ROMENTRYTYPE_COPY = 1006;/* this entry copies data from another region/offset */
    public static final int ROMENTRYTYPE_COUNT = 1007;

    public static final String ROMENTRY_REGION = "1001";
    public static final String ROMENTRY_END = "1002";
    public static final String ROMENTRY_RELOAD = "1003";
    public static final String ROMENTRY_CONTINUE = "1004";
    public static final String ROMENTRY_FILL = "1005";
    public static final String ROMENTRY_COPY = "1006";

    /* ----- per-entry macros ----- */
    public static int ROMENTRY_GETTYPE(RomModule[] romp, int romp_ptr) {
        //((FPTR)(r)->_name)
        int result;
        try {
            result = Integer.parseInt(romp[romp_ptr]._name);//possible values 1001-1006
        } catch (NumberFormatException e) {
            result = 1015; //random value just not to be something between 1001-1006
        }
        return result;
    }

    public static boolean ROMENTRY_ISSPECIAL(RomModule[] romp, int romp_ptr) {
        return (ROMENTRY_GETTYPE(romp, romp_ptr) < ROMENTRYTYPE_COUNT);
    }

    public static boolean ROMENTRY_ISFILE(RomModule[] romp, int romp_ptr) {
        return (!ROMENTRY_ISSPECIAL(romp, romp_ptr));
    }

    public static boolean ROMENTRY_ISREGION(RomModule[] romp, int romp_ptr) {
        return romp[romp_ptr]._name.matches(ROMENTRY_REGION);
    }

    public static boolean ROMENTRY_ISEND(RomModule[] romp, int romp_ptr) {
        return romp[romp_ptr]._name.matches(ROMENTRY_END);
    }

    public static boolean ROMENTRY_ISRELOAD(RomModule[] romp, int romp_ptr) {
        return romp[romp_ptr]._name.matches(ROMENTRY_RELOAD);
    }

    public static boolean ROMENTRY_ISCONTINUE(RomModule[] romp, int romp_ptr) {
        return romp[romp_ptr]._name.matches(ROMENTRY_CONTINUE);
    }

    public static boolean ROMENTRY_ISFILL(RomModule[] romp, int romp_ptr) {
        return romp[romp_ptr]._name.matches(ROMENTRY_FILL);
    }

    public static boolean ROMENTRY_ISCOPY(RomModule[] romp, int romp_ptr) {
        return romp[romp_ptr]._name.matches(ROMENTRY_COPY);
    }

    public static boolean ROMENTRY_ISREGIONEND(RomModule[] romp, int romp_ptr) {
        return (ROMENTRY_ISREGION(romp, romp_ptr) || ROMENTRY_ISEND(romp, romp_ptr));
    }

    /*TODO*///
/*TODO*///
/*TODO*////* ----- per-region constants ----- */
/*TODO*///#define ROMREGION_WIDTHMASK			0x00000003			/* native width of region, as power of 2 */
/*TODO*///#define		ROMREGION_8BIT			0x00000000			/*    (non-CPU regions only) */
/*TODO*///#define		ROMREGION_16BIT			0x00000001
/*TODO*///#define		ROMREGION_32BIT			0x00000002
/*TODO*///#define		ROMREGION_64BIT			0x00000003
/*TODO*///
/*TODO*///#define ROMREGION_ENDIANMASK		0x00000004			/* endianness of the region */
/*TODO*///#define		ROMREGION_LE			0x00000000			/*    (non-CPU regions only) */
/*TODO*///#define		ROMREGION_BE			0x00000004
/*TODO*///
/*TODO*///#define ROMREGION_INVERTMASK		0x00000008			/* invert the bits of the region */
/*TODO*///#define		ROMREGION_NOINVERT		0x00000000
/*TODO*///#define		ROMREGION_INVERT		0x00000008
/*TODO*///
/*TODO*///#define ROMREGION_DISPOSEMASK		0x00000010			/* dispose of the region after init */
/*TODO*///#define		ROMREGION_NODISPOSE		0x00000000
/*TODO*///#define		ROMREGION_DISPOSE		0x00000010
/*TODO*///
/*TODO*///#define ROMREGION_SOUNDONLYMASK		0x00000020			/* load only if sound is enabled */
/*TODO*///#define		ROMREGION_NONSOUND		0x00000000
/*TODO*///#define		ROMREGION_SOUNDONLY		0x00000020
/*TODO*///
/*TODO*///#define ROMREGION_LOADUPPERMASK		0x00000040			/* load into the upper part of CPU space */
/*TODO*///#define		ROMREGION_LOADLOWER		0x00000000			/*     (CPU regions only) */
/*TODO*///#define		ROMREGION_LOADUPPER		0x00000040
/*TODO*///
/*TODO*///#define ROMREGION_ERASEMASK			0x00000080			/* erase the region before loading */
/*TODO*///#define		ROMREGION_NOERASE		0x00000000
/*TODO*///#define		ROMREGION_ERASE			0x00000080
/*TODO*///
/*TODO*///#define ROMREGION_ERASEVALMASK		0x0000ff00			/* value to erase the region to */
/*TODO*///#define		ROMREGION_ERASEVAL(x)	((((x) & 0xff) << 8) | ROMREGION_ERASE)
/*TODO*///#define		ROMREGION_ERASE00		ROMREGION_ERASEVAL(0)
/*TODO*///#define		ROMREGION_ERASEFF		ROMREGION_ERASEVAL(0xff)
/*TODO*///
    public static final int ROMREGION_DATATYPEMASK = 0x00010000;/* inherit all flags from previous definition */
    public static final int ROMREGION_DATATYPEROM = 0x00000000;
    public static final int ROMREGION_DATATYPEDISK = 0x00010000;

    /*TODO*////* ----- per-region macros ----- */
    public static int ROMREGION_GETTYPE(RomModule[] romp, int romp_ptr) {
        return Integer.parseInt(romp[romp_ptr]._hashdata);
    }

    /*TODO*///#define ROMREGION_GETLENGTH(r)		((r)->_length)
    public static int ROMREGION_GETFLAGS(RomModule[] romp, int romp_ptr) {
        return romp[romp_ptr]._flags;
    }

    /*TODO*///#define ROMREGION_GETWIDTH(r)		(8 << (ROMREGION_GETFLAGS(r) & ROMREGION_WIDTHMASK))
/*TODO*///#define ROMREGION_ISLITTLEENDIAN(r)	((ROMREGION_GETFLAGS(r) & ROMREGION_ENDIANMASK) == ROMREGION_LE)
/*TODO*///#define ROMREGION_ISBIGENDIAN(r)	((ROMREGION_GETFLAGS(r) & ROMREGION_ENDIANMASK) == ROMREGION_BE)
/*TODO*///#define ROMREGION_ISINVERTED(r)		((ROMREGION_GETFLAGS(r) & ROMREGION_INVERTMASK) == ROMREGION_INVERT)
/*TODO*///#define ROMREGION_ISDISPOSE(r)		((ROMREGION_GETFLAGS(r) & ROMREGION_DISPOSEMASK) == ROMREGION_DISPOSE)
/*TODO*///#define ROMREGION_ISSOUNDONLY(r)	((ROMREGION_GETFLAGS(r) & ROMREGION_SOUNDONLYMASK) == ROMREGION_SOUNDONLY)
/*TODO*///#define ROMREGION_ISLOADUPPER(r)	((ROMREGION_GETFLAGS(r) & ROMREGION_LOADUPPERMASK) == ROMREGION_LOADUPPER)
/*TODO*///#define ROMREGION_ISERASE(r)		((ROMREGION_GETFLAGS(r) & ROMREGION_ERASEMASK) == ROMREGION_ERASE)
/*TODO*///#define ROMREGION_GETERASEVAL(r)	((ROMREGION_GETFLAGS(r) & ROMREGION_ERASEVALMASK) >> 8)
    public static int ROMREGION_GETDATATYPE(RomModule[] romp, int romp_ptr) {
        return ROMREGION_GETFLAGS(romp, romp_ptr) & ROMREGION_DATATYPEMASK;
    }

    public static boolean ROMREGION_ISROMDATA(RomModule[] romp, int romp_ptr) {
        return (ROMREGION_GETDATATYPE(romp, romp_ptr) == ROMREGION_DATATYPEROM);
    }

    /*TODO*///#define ROMREGION_ISDISKDATA(r)		(ROMREGION_GETDATATYPE(r) == ROMREGION_DATATYPEDISK)
/*TODO*///
/*TODO*///
/*TODO*////* ----- per-ROM constants ----- */
/*TODO*///#define DISK_READONLYMASK			0x00000400			/* is the disk read-only? */
/*TODO*///#define		DISK_READWRITE			0x00000000
/*TODO*///#define		DISK_READONLY			0x00000400
/*TODO*///
/*TODO*///#define ROM_OPTIONALMASK			0x00000800			/* optional - won't hurt if it's not there */
/*TODO*///#define		ROM_REQUIRED			0x00000000
/*TODO*///#define		ROM_OPTIONAL			0x00000800
/*TODO*///
/*TODO*///#define ROM_GROUPMASK				0x0000f000			/* load data in groups of this size + 1 */
/*TODO*///#define		ROM_GROUPSIZE(n)		((((n) - 1) & 15) << 12)
/*TODO*///#define		ROM_GROUPBYTE			ROM_GROUPSIZE(1)
/*TODO*///#define		ROM_GROUPWORD			ROM_GROUPSIZE(2)
/*TODO*///#define		ROM_GROUPDWORD			ROM_GROUPSIZE(4)
/*TODO*///
/*TODO*///#define ROM_SKIPMASK				0x000f0000			/* skip this many bytes after each group */
/*TODO*///#define		ROM_SKIP(n)				(((n) & 15) << 16)
/*TODO*///#define		ROM_NOSKIP				ROM_SKIP(0)
/*TODO*///
/*TODO*///#define ROM_REVERSEMASK				0x00100000			/* reverse the byte order within a group */
/*TODO*///#define		ROM_NOREVERSE			0x00000000
/*TODO*///#define		ROM_REVERSE				0x00100000
/*TODO*///
/*TODO*///#define ROM_BITWIDTHMASK			0x00e00000			/* width of data in bits */
/*TODO*///#define		ROM_BITWIDTH(n)			(((n) & 7) << 21)
/*TODO*///#define		ROM_NIBBLE				ROM_BITWIDTH(4)
/*TODO*///#define		ROM_FULLBYTE			ROM_BITWIDTH(8)
/*TODO*///
/*TODO*///#define ROM_BITSHIFTMASK			0x07000000			/* left-shift count for the bits */
/*TODO*///#define		ROM_BITSHIFT(n)			(((n) & 7) << 24)
/*TODO*///#define		ROM_NOSHIFT				ROM_BITSHIFT(0)
/*TODO*///#define		ROM_SHIFT_NIBBLE_LO		ROM_BITSHIFT(0)
/*TODO*///#define		ROM_SHIFT_NIBBLE_HI		ROM_BITSHIFT(4)
/*TODO*///
/*TODO*///#define ROM_INHERITFLAGSMASK		0x08000000			/* inherit all flags from previous definition */
/*TODO*///#define		ROM_INHERITFLAGS		0x08000000
/*TODO*///
/*TODO*///#define ROM_BIOSFLAGSMASK			0xf0000000			/* only loaded if value matches global bios value */
/*TODO*///#define 	ROM_BIOS(n)				(((n) & 15) << 28)
/*TODO*///
/*TODO*///#define ROM_INHERITEDFLAGS			(ROM_GROUPMASK | ROM_SKIPMASK | ROM_REVERSEMASK | ROM_BITWIDTHMASK | ROM_BITSHIFTMASK | ROM_BIOSFLAGSMASK)
/*TODO*///
/* ----- per-ROM macros ----- */
    public static String ROM_GETNAME(RomModule[] romp, int romp_ptr) {
        return romp[romp_ptr]._name;
    }

    /*TODO*///#define ROM_SAFEGETNAME(r)			(ROMENTRY_ISFILL(r) ? "fill" : ROMENTRY_ISCOPY(r) ? "copy" : ROM_GETNAME(r))
/*TODO*///#define ROM_GETOFFSET(r)			((r)->_offset)
    public static int ROM_GETLENGTH(RomModule[] romp, int romp_ptr) {
        return romp[romp_ptr]._length;
    }

    /*TODO*///#define ROM_GETFLAGS(r)				((r)->_flags)
    public static String ROM_GETHASHDATA(RomModule[] romp, int romp_ptr) {
        return romp[romp_ptr]._hashdata;
    }
    /*TODO*///#define ROM_ISOPTIONAL(r)			((ROM_GETFLAGS(r) & ROM_OPTIONALMASK) == ROM_OPTIONAL)
/*TODO*///#define ROM_GETGROUPSIZE(r)			(((ROM_GETFLAGS(r) & ROM_GROUPMASK) >> 12) + 1)
/*TODO*///#define ROM_GETSKIPCOUNT(r)			((ROM_GETFLAGS(r) & ROM_SKIPMASK) >> 16)
/*TODO*///#define ROM_ISREVERSED(r)			((ROM_GETFLAGS(r) & ROM_REVERSEMASK) == ROM_REVERSE)
/*TODO*///#define ROM_GETBITWIDTH(r)			(((ROM_GETFLAGS(r) & ROM_BITWIDTHMASK) >> 21) + 8 * ((ROM_GETFLAGS(r) & ROM_BITWIDTHMASK) == 0))
/*TODO*///#define ROM_GETBITSHIFT(r)			((ROM_GETFLAGS(r) & ROM_BITSHIFTMASK) >> 24)
/*TODO*///#define ROM_INHERITSFLAGS(r)		((ROM_GETFLAGS(r) & ROM_INHERITFLAGSMASK) == ROM_INHERITFLAGS)
/*TODO*///#define ROM_GETBIOSFLAGS(r)			((ROM_GETFLAGS(r) & ROM_BIOSFLAGSMASK) >> 28)
/*TODO*///#define ROM_NOGOODDUMP(r)			(hash_data_has_info((r)->_hashdata, HASH_INFO_NO_DUMP))
/*TODO*///
/*TODO*////* ----- per-disk macros ----- */
/*TODO*///#define DISK_GETINDEX(r)			((r)->_offset)
/*TODO*///#define DISK_ISREADONLY(r)			((ROM_GETFLAGS(r) & DISK_READONLYMASK) == DISK_READONLY)
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Derived macros for the ROM loading system
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////* ----- start/stop macros ----- */
    public static RomModule[] rommodule_macro = null;
    public static ArrayList<RomModule> arload = new ArrayList<>();

    public static void ROM_END() {
        arload.add(new RomModule(ROMENTRY_END, 0, 0, 0, null));
        rommodule_macro = arload.toArray(new RomModule[arload.size()]);
        arload.clear();
    }

    /* ----- ROM region macros ----- */
    public static void ROM_REGION(int length, int type, int flags) {
        arload.add(new RomModule(ROMENTRY_REGION, 0, length, flags, Integer.toString(type)));
    }

    /*TODO*///#define ROM_REGION16_LE(length,type,flags)			ROM_REGION(length, type, (flags) | ROMREGION_16BIT | ROMREGION_LE)
/*TODO*///#define ROM_REGION16_BE(length,type,flags)			ROM_REGION(length, type, (flags) | ROMREGION_16BIT | ROMREGION_BE)
/*TODO*///#define ROM_REGION32_LE(length,type,flags)			ROM_REGION(length, type, (flags) | ROMREGION_32BIT | ROMREGION_LE)
/*TODO*///#define ROM_REGION32_BE(length,type,flags)			ROM_REGION(length, type, (flags) | ROMREGION_32BIT | ROMREGION_BE)
/*TODO*///
/*TODO*////* ----- core ROM loading macros ----- */
/*TODO*///#define ROMMD5_LOAD(name,offset,length,hash,flags)   { name, offset, length, flags, hash },
    public static void ROMX_LOAD(String name, int offset, int length, String hash, int flags) {
        arload.add(new RomModule(name, offset, length, flags, hash));
    }

    public static void ROM_LOAD(String name, int offset, int length, String hash) {
        ROMX_LOAD(name, offset, length, hash, 0);
    }
    /*TODO*///#define ROM_LOAD_OPTIONAL(name,offset,length,hash)   ROMX_LOAD(name, offset, length, hash, ROM_OPTIONAL)
/*TODO*///#define ROM_CONTINUE(offset,length)					ROMX_LOAD(ROMENTRY_CONTINUE, offset, length, 0, ROM_INHERITFLAGS)
/*TODO*///#define ROM_RELOAD(offset,length)					ROMX_LOAD(ROMENTRY_RELOAD, offset, length, 0, ROM_INHERITFLAGS)
/*TODO*///#define ROM_FILL(offset,length,value)                ROM_LOAD(ROMENTRY_FILL, offset, length, (const char*)value)
/*TODO*///#define ROM_COPY(rgn,srcoffset,offset,length)        ROMX_LOAD(ROMENTRY_COPY, offset, length, (const char*)srcoffset, (rgn) << 24)
/*TODO*///
/*TODO*////* ----- nibble loading macros ----- */
/*TODO*///#define ROM_LOAD_NIB_HIGH(name,offset,length,hash)   ROMX_LOAD(name, offset, length, hash, ROM_NIBBLE | ROM_SHIFT_NIBBLE_HI)
/*TODO*///#define ROM_LOAD_NIB_LOW(name,offset,length,hash)    ROMX_LOAD(name, offset, length, hash, ROM_NIBBLE | ROM_SHIFT_NIBBLE_LO)
/*TODO*///
/*TODO*////* ----- new-style 16-bit loading macros ----- */
/*TODO*///#define ROM_LOAD16_BYTE(name,offset,length,hash)     ROMX_LOAD(name, offset, length, hash, ROM_SKIP(1))
/*TODO*///#define ROM_LOAD16_WORD(name,offset,length,hash)     ROM_LOAD(name, offset, length, hash)
/*TODO*///#define ROM_LOAD16_WORD_SWAP(name,offset,length,hash)ROMX_LOAD(name, offset, length, hash, ROM_GROUPWORD | ROM_REVERSE)
/*TODO*///
/*TODO*////* ----- new-style 32-bit loading macros ----- */
/*TODO*///#define ROM_LOAD32_BYTE(name,offset,length,hash)     ROMX_LOAD(name, offset, length, hash, ROM_SKIP(3))
/*TODO*///#define ROM_LOAD32_WORD(name,offset,length,hash)     ROMX_LOAD(name, offset, length, hash, ROM_GROUPWORD | ROM_SKIP(2))
/*TODO*///#define ROM_LOAD32_WORD_SWAP(name,offset,length,hash)ROMX_LOAD(name, offset, length, hash, ROM_GROUPWORD | ROM_REVERSE | ROM_SKIP(2))
/*TODO*///#define ROM_LOAD32_DWORD(name,offset,length,hash)    ROMX_LOAD(name, offset, length, hash, ROM_GROUPDWORD)
/*TODO*///
/*TODO*////* ----- disk loading macros ----- */
/*TODO*///#define DISK_REGION(type)							ROM_REGION(1, type, ROMREGION_DATATYPEDISK)
/*TODO*///#define DISK_IMAGE(name,idx,hash)                    ROMMD5_LOAD(name, idx, 0, hash, DISK_READWRITE)
/*TODO*///#define DISK_IMAGE_READONLY(name,idx,hash)           ROMMD5_LOAD(name, idx, 0, hash, DISK_READONLY)
/*TODO*///
/*TODO*////* ----- hash macros ----- */
/*TODO*///#define CRC(x)                                       "c:" #x "#"
/*TODO*///#define SHA1(x)                                      "s:" #x "#"
/*TODO*///#define MD5(x)                                       "m:" #x "#"
/*TODO*///#define NO_DUMP                                      "$ND$"
/*TODO*///#define BAD_DUMP                                     "$BD$"
/*TODO*///
/*TODO*///// @@@ FF: Remove this when we use the final SHA1Merger
/*TODO*///#define NOT_DUMPED NO_DUMP
/*TODO*///#define BADROM BAD_DUMP
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Derived macros for the alternate BIOS loading system
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///#define BIOSENTRY_ISEND(b)		((b)->_name == NULL)
/*TODO*///
/*TODO*////* ----- start/stop macros ----- */
/*TODO*///#define SYSTEM_BIOS_START(name)			static const struct SystemBios system_bios_##name[] = {
/*TODO*///#define SYSTEM_BIOS_END					{ 0, NULL } };
/*TODO*///
/*TODO*////* ----- ROM region macros ----- */
/*TODO*///#define SYSTEM_BIOS_ADD(value,name,description)		{ (int)value, (const char*)name, (const char*)description },
/*TODO*///#define BIOS_DEFAULT			"default"
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Function prototypes
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///void showdisclaimer(void);
/*TODO*///
/*TODO*////* helper function that reads samples from disk - this can be used by other */
/*TODO*////* drivers as well (e.g. a sound chip emulator needing drum samples) */
/*TODO*///struct GameSamples *readsamples(const char **samplenames,const char *name);
/*TODO*///#define freesamples(samps)
/*TODO*///
/*TODO*////* return a pointer to the specified memory region - num can be either an absolute */
/*TODO*////* number, or one of the REGION_XXX identifiers defined above */
/*TODO*///UINT8 *memory_region(int num);
/*TODO*///size_t memory_region_length(int num);
/*TODO*///
/*TODO*////* allocate a new memory region - num can be either an absolute */
/*TODO*////* number, or one of the REGION_XXX identifiers defined above */
/*TODO*///int new_memory_region(int num, size_t length, UINT32 flags);
/*TODO*///void free_memory_region(int num);
/*TODO*///
/*TODO*////* common coin counter helpers */
/*TODO*///#define COIN_COUNTERS	8	/* total # of coin counters */
/*TODO*///void coin_counter_w(int num,int on);
/*TODO*///void coin_lockout_w(int num,int on);
/*TODO*///void coin_lockout_global_w(int on);  /* Locks out all coin inputs */
/*TODO*///
/*TODO*////* generic NVRAM handler */
/*TODO*///extern size_t generic_nvram_size;
/*TODO*///extern data8_t *generic_nvram;
/*TODO*///extern void nvram_handler_generic_0fill(mame_file *file, int read_or_write);
/*TODO*///extern void nvram_handler_generic_1fill(mame_file *file, int read_or_write);
/*TODO*///
/*TODO*////* bitmap allocation */
/*TODO*///struct mame_bitmap *bitmap_alloc(int width,int height);
/*TODO*///struct mame_bitmap *bitmap_alloc_depth(int width,int height,int depth);
/*TODO*///void bitmap_free(struct mame_bitmap *bitmap);
/*TODO*///
/*TODO*////* automatic resource management */
/*TODO*///void begin_resource_tracking(void);
/*TODO*///void end_resource_tracking(void);
/*TODO*///INLINE int get_resource_tag(void)
/*TODO*///{
/*TODO*///	extern int resource_tracking_tag;
/*TODO*///	return resource_tracking_tag;
/*TODO*///}
/*TODO*///
/*TODO*////* automatically-freeing memory */
/*TODO*///void *auto_malloc(size_t size);
/*TODO*///char *auto_strdup(const char *str);
/*TODO*///struct mame_bitmap *auto_bitmap_alloc(int width,int height);
/*TODO*///struct mame_bitmap *auto_bitmap_alloc_depth(int width,int height,int depth);
/*TODO*///
/*TODO*////*
/*TODO*///  Save a screen shot of the game display. It is suggested to use the core
/*TODO*///  function save_screen_snapshot() or save_screen_snapshot_as(), so the format
/*TODO*///  of the screen shots will be consistent across ports. This hook is provided
/*TODO*///  only to allow the display of a file requester to let the user choose the
/*TODO*///  file name. This isn't scrictly necessary, so you can just call
/*TODO*///  save_screen_snapshot() to let the core automatically pick a default name.
/*TODO*///*/
/*TODO*///void save_screen_snapshot_as(mame_file *fp, struct mame_bitmap *bitmap);
/*TODO*///void save_screen_snapshot(struct mame_bitmap *bitmap);
/*TODO*///
/*TODO*////* disk handling */
/*TODO*///struct chd_file *get_disk_handle(int diskindex);
/*TODO*///
/*TODO*////* ROM processing */
/*TODO*///int rom_load(const struct RomModule *romp);
/*TODO*///const struct RomModule *rom_first_region(const struct GameDriver *drv);
/*TODO*///const struct RomModule *rom_next_region(const struct RomModule *romp);
/*TODO*///const struct RomModule *rom_first_file(const struct RomModule *romp);
/*TODO*///const struct RomModule *rom_next_file(const struct RomModule *romp);
/*TODO*///const struct RomModule *rom_first_chunk(const struct RomModule *romp);
/*TODO*///const struct RomModule *rom_next_chunk(const struct RomModule *romp);
/*TODO*///
/*TODO*///void printromlist(const struct RomModule *romp,const char *name);
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Useful macros to deal with bit shuffling encryptions
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///#define BIT(x,n) (((x)>>(n))&1)
/*TODO*///
/*TODO*///#define BITSWAP8(val,B7,B6,B5,B4,B3,B2,B1,B0) \
/*TODO*///		((BIT(val,B7) << 7) | \
/*TODO*///		 (BIT(val,B6) << 6) | \
/*TODO*///		 (BIT(val,B5) << 5) | \
/*TODO*///		 (BIT(val,B4) << 4) | \
/*TODO*///		 (BIT(val,B3) << 3) | \
/*TODO*///		 (BIT(val,B2) << 2) | \
/*TODO*///		 (BIT(val,B1) << 1) | \
/*TODO*///		 (BIT(val,B0) << 0))
/*TODO*///
/*TODO*///#define BITSWAP16(val,B15,B14,B13,B12,B11,B10,B9,B8,B7,B6,B5,B4,B3,B2,B1,B0) \
/*TODO*///		((BIT(val,B15) << 15) | \
/*TODO*///		 (BIT(val,B14) << 14) | \
/*TODO*///		 (BIT(val,B13) << 13) | \
/*TODO*///		 (BIT(val,B12) << 12) | \
/*TODO*///		 (BIT(val,B11) << 11) | \
/*TODO*///		 (BIT(val,B10) << 10) | \
/*TODO*///		 (BIT(val, B9) <<  9) | \
/*TODO*///		 (BIT(val, B8) <<  8) | \
/*TODO*///		 (BIT(val, B7) <<  7) | \
/*TODO*///		 (BIT(val, B6) <<  6) | \
/*TODO*///		 (BIT(val, B5) <<  5) | \
/*TODO*///		 (BIT(val, B4) <<  4) | \
/*TODO*///		 (BIT(val, B3) <<  3) | \
/*TODO*///		 (BIT(val, B2) <<  2) | \
/*TODO*///		 (BIT(val, B1) <<  1) | \
/*TODO*///		 (BIT(val, B0) <<  0))
/*TODO*///
/*TODO*///#define BITSWAP24(val,B23,B22,B21,B20,B19,B18,B17,B16,B15,B14,B13,B12,B11,B10,B9,B8,B7,B6,B5,B4,B3,B2,B1,B0) \
/*TODO*///		((BIT(val,B23) << 23) | \
/*TODO*///		 (BIT(val,B22) << 22) | \
/*TODO*///		 (BIT(val,B21) << 21) | \
/*TODO*///		 (BIT(val,B20) << 20) | \
/*TODO*///		 (BIT(val,B19) << 19) | \
/*TODO*///		 (BIT(val,B18) << 18) | \
/*TODO*///		 (BIT(val,B17) << 17) | \
/*TODO*///		 (BIT(val,B16) << 16) | \
/*TODO*///		 (BIT(val,B15) << 15) | \
/*TODO*///		 (BIT(val,B14) << 14) | \
/*TODO*///		 (BIT(val,B13) << 13) | \
/*TODO*///		 (BIT(val,B12) << 12) | \
/*TODO*///		 (BIT(val,B11) << 11) | \
/*TODO*///		 (BIT(val,B10) << 10) | \
/*TODO*///		 (BIT(val, B9) <<  9) | \
/*TODO*///		 (BIT(val, B8) <<  8) | \
/*TODO*///		 (BIT(val, B7) <<  7) | \
/*TODO*///		 (BIT(val, B6) <<  6) | \
/*TODO*///		 (BIT(val, B5) <<  5) | \
/*TODO*///		 (BIT(val, B4) <<  4) | \
/*TODO*///		 (BIT(val, B3) <<  3) | \
/*TODO*///		 (BIT(val, B2) <<  2) | \
/*TODO*///		 (BIT(val, B1) <<  1) | \
/*TODO*///		 (BIT(val, B0) <<  0))
/*TODO*///
/*TODO*///
/*TODO*///#ifdef __cplusplus
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#endif
/*TODO*///    
}
