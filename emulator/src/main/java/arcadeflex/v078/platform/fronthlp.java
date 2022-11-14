/**
 * ported to 0.78
 */
package arcadeflex.v078.platform;

//mame imports
import static arcadeflex.v078.AAdummy.driver.drivers;
import static arcadeflex.v078.mame.common.*;
import static arcadeflex.v078.mame.driverH.NOT_A_DRIVER;
import static arcadeflex.v078.mame.version.*;
//platform imports
import static arcadeflex.v078.platform.rcH.*;
//common imports
import static common.libc.cstdio.*;
import static common.libc.cstring.strchr;
import static common.libc.cstring.strncmp;
import static common.libc.cstring.strstr;

public class fronthlp {

    public static final int LIST_SHORT = 1;
    public static final int LIST_INFO = 2;
    public static final int LIST_XML = 3;
    public static final int LIST_FULL = 4;
    public static final int LIST_SAMDIR = 5;
    public static final int LIST_ROMS = 6;
    public static final int LIST_SAMPLES = 7;
    public static final int LIST_LMR = 8;
    public static final int LIST_DETAILS = 9;
    public static final int LIST_GAMELIST = 10;
    public static final int LIST_GAMES = 11;
    public static final int LIST_CLONES = 12;
    public static final int LIST_WRONGORIENTATION = 13;
    public static final int LIST_WRONGFPS = 14;
    public static final int LIST_CRC = 15;
    public static final int LIST_SHA1 = 16;
    public static final int LIST_MD5 = 17;
    public static final int LIST_DUPCRC = 18;
    public static final int LIST_WRONGMERGE = 19;
    public static final int LIST_ROMSIZE = 20;
    public static final int LIST_ROMDISTRIBUTION = 21;
    public static final int LIST_ROMNUMBER = 22;
    public static final int LIST_PALETTESIZE = 23;
    public static final int LIST_CPU = 24;
    public static final int LIST_CPUCLASS = 25;
    public static final int LIST_NOSOUND = 26;
    public static final int LIST_SOUND = 27;
    public static final int LIST_NVRAM = 28;
    public static final int LIST_SOURCEFILE = 29;
    public static final int LIST_GAMESPERSOURCEFILE = 30;

    public static final int VERIFY_ROMS = 0x00000001;
    public static final int VERIFY_SAMPLES = 0x00000002;
    public static final int VERIFY_VERBOSE = 0x00000004;
    public static final int VERIFY_TERSE = 0x00000008;

    public static final int KNOWN_START = 0;
    public static final int KNOWN_ALL = 1;
    public static final int KNOWN_NONE = 2;
    public static final int KNOWN_SOME = 3;

    public static final int YEAR_BEGIN = 1975;
    public static final int YEAR_END = 2000;

    static int list = 0;
    static int listclones = 1;
    static int verify = 0;
    static int ident = 0;
    static int help = 0;
    static int sortby = 0;

    static RcAssignFuncHandlerPtr assign_list = new RcAssignFuncHandlerPtr() {
        @Override
        public void handler(int value) {
            list = value;
        }
    };

    static RcAssignFuncHandlerPtr assign_clones = new RcAssignFuncHandlerPtr() {
        @Override
        public void handler(int value) {
            listclones = value;
        }
    };

    static RcAssignFuncHandlerPtr assign_verify = new RcAssignFuncHandlerPtr() {
        @Override
        public void handler(int value) {
            verify = value;
        }
    };

    static RcAssignFuncHandlerPtr assign_ident = new RcAssignFuncHandlerPtr() {
        @Override
        public void handler(int value) {
            ident = value;
        }
    };

    static RcAssignFuncHandlerPtr assign_help = new RcAssignFuncHandlerPtr() {
        @Override
        public void handler(int value) {
            help = value;
        }
    };

    static RcAssignFuncHandlerPtr assign_sortby = new RcAssignFuncHandlerPtr() {
        @Override
        public void handler(int value) {
            sortby = value;
        }
    };

    public static rc_option frontend_opts[] = {
        new rc_option("Frontend Related", null, rc_seperator, null, null, 0, 0, null, null),
        new rc_option("help", "h", rc_set_int, assign_help, null, 1, 0, null, "show help message"),
        new rc_option("?", null, rc_set_int, assign_help, null, 1, 0, null, "show help message"),
        /*TODO*///
        /*TODO*///	/* list options follow */
        new rc_option("list", "ls", rc_set_int, assign_list, null, LIST_SHORT, 0, null, "List supported games matching gamename, or all, gamename may contain * and ? wildcards"),
        new rc_option("listfull", "ll", rc_set_int, assign_list, null, LIST_FULL, 0, null, "short name, full name"),
        new rc_option("listgames", null, rc_set_int, assign_list, null, LIST_GAMES, 0, null, "year, manufacturer and full name"),
        new rc_option("listdetails", null, rc_set_int, assign_list, null, LIST_DETAILS, 0, null, "detailed info"),
        new rc_option("gamelist", null, rc_set_int, assign_list, null, LIST_GAMELIST, 0, null, "output gamelist.txt main body"),
        new rc_option("listsourcefile", null, rc_set_int, assign_list, null, LIST_SOURCEFILE, 0, null, "driver sourcefile"),
        new rc_option("listgamespersourcefile", null, rc_set_int, assign_list, null, LIST_GAMESPERSOURCEFILE, 0, null, "games per sourcefile"),
        new rc_option("listinfo", "li", rc_set_int, assign_list, null, LIST_INFO, 0, null, "all available info on driver"),
        new rc_option("listxml", "lx", rc_set_int, assign_list, null, LIST_XML, 0, null, "all available info on driver in XML format"),
        new rc_option("listclones", "lc", rc_set_int, assign_list, null, LIST_CLONES, 0, null, "show clones"),
        new rc_option("listsamdir", null, rc_set_int, assign_list, null, LIST_SAMDIR, 0, null, "shared sample directory"),
        new rc_option("listcrc", null, rc_set_int, assign_list, null, LIST_CRC, 0, null, "CRC-32s"),
        new rc_option("listsha1", null, rc_set_int, assign_list, null, LIST_SHA1, 0, null, "SHA-1s"),
        new rc_option("listmd5", null, rc_set_int, assign_list, null, LIST_MD5, 0, null, "MD5s"),
        new rc_option("listdupcrc", null, rc_set_int, assign_list, null, LIST_DUPCRC, 0, null, "duplicate crc's"),
        new rc_option("listwrongmerge", "lwm", rc_set_int, assign_list, null, LIST_WRONGMERGE, 0, null, "wrong merge attempts"),
        new rc_option("listromsize", null, rc_set_int, assign_list, null, LIST_ROMSIZE, 0, null, "rom size"),
        new rc_option("listromdistribution", null, rc_set_int, assign_list, null, LIST_ROMDISTRIBUTION, 0, null, "rom distribution"),
        new rc_option("listromnumber", null, rc_set_int, assign_list, null, LIST_ROMNUMBER, 0, null, "rom size"),
        new rc_option("listpalettesize", "lps", rc_set_int, assign_list, null, LIST_PALETTESIZE, 0, null, "palette size"),
        new rc_option("listcpu", null, rc_set_int, assign_list, null, LIST_CPU, 0, null, "cpu's used"),
        new rc_option("listcpuclass", null, rc_set_int, assign_list, null, LIST_CPUCLASS, 0, null, "class of cpu's used by year"),
        new rc_option("listnosound", null, rc_set_int, assign_list, null, LIST_NOSOUND, 0, null, "drivers missing sound support"),
        new rc_option("listsound", null, rc_set_int, assign_list, null, LIST_SOUND, 0, null, "sound chips used"),
        new rc_option("listnvram", null, rc_set_int, assign_list, null, LIST_NVRAM, 0, null, "games with nvram"),
        new rc_option("wrongorientation", null, rc_set_int, assign_list, null, LIST_WRONGORIENTATION, 0, null, "wrong orientation"),
        new rc_option("wrongfps", null, rc_set_int, assign_list, null, LIST_WRONGFPS, 0, null, "wrong fps"),
        /*TODO*///        new rc_option("clones", null, rc_bool, assign_clones, "1", 0, 0, null, "enable/disable clones"),
        new rc_option("listroms", null, rc_set_int, assign_list, null, LIST_ROMS, 0, null, "list required roms for a driver"),
        new rc_option("listsamples", null, rc_set_int, assign_list, null, LIST_SAMPLES, 0, null, "list optional samples for a driver"),
        new rc_option("verifyroms", null, rc_set_int, assign_verify, null, VERIFY_ROMS, 0, null, "report romsets that have problems"),
        new rc_option("verifysets", null, rc_set_int, assign_verify, null, VERIFY_ROMS | VERIFY_VERBOSE | VERIFY_TERSE, 0, null, "verify checksums of romsets (terse)"),
        new rc_option("vset", null, rc_set_int, assign_verify, null, VERIFY_ROMS | VERIFY_VERBOSE, 0, null, "verify checksums of a romset (verbose)"),
        new rc_option("verifysamples", null, rc_set_int, assign_verify, null, VERIFY_SAMPLES | VERIFY_VERBOSE, 0, null, "report samplesets that have problems"),
        new rc_option("vsam", null, rc_set_int, assign_verify, null, VERIFY_SAMPLES | VERIFY_VERBOSE, 0, null, "verify a sampleset"),
        new rc_option("romident", null, rc_set_int, assign_ident, null, 1, 0, null, "compare files with known MAME roms"),
        new rc_option("isknown", null, rc_set_int, assign_ident, null, 2, 0, null, "compare files with known MAME roms (brief)"),
        new rc_option("sortname", null, rc_set_int, assign_sortby, null, 1, 0, null, "sort by descriptive name"),
        new rc_option("sortdriver", null, rc_set_int, assign_sortby, null, 2, 0, null, "sort by driver"),
        new rc_option(null, null, rc_end, null, null, 0, 0, null, null)
    };

    static int silentident, knownstatus;

    /*TODO*///static const struct GameDriver *gamedrv;
/*TODO*///
/*TODO*////* compare string[8] using standard(?) DOS wildchars ('?' & '*')      */
/*TODO*////* for this to work correctly, the shells internal wildcard expansion */
/*TODO*////* mechanism has to be disabled. Look into msdos.c */
/*TODO*///
    static int strwildcmp(String sp1, String sp2) {
        return 0; //TODO FIX ME 
        /*TODO*///	char s1[9], s2[9];
/*TODO*///	int i, l1, l2;
/*TODO*///	char *p;
/*TODO*///
/*TODO*///	strncpy(s1, sp1, 8); s1[8] = 0; if (s1[0] == 0) strcpy(s1, "*");
/*TODO*///
/*TODO*///	strncpy(s2, sp2, 8); s2[8] = 0; if (s2[0] == 0) strcpy(s2, "*");
/*TODO*///
/*TODO*///	p = strchr(s1, '*');
/*TODO*///	if (p)
/*TODO*///	{
/*TODO*///		for (i = p - s1; i < 8; i++) s1[i] = '?';
/*TODO*///		s1[8] = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	p = strchr(s2, '*');
/*TODO*///	if (p)
/*TODO*///	{
/*TODO*///		for (i = p - s2; i < 8; i++) s2[i] = '?';
/*TODO*///		s2[8] = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	l1 = strlen(s1);
/*TODO*///	if (l1 < 8)
/*TODO*///	{
/*TODO*///		for (i = l1 + 1; i < 8; i++) s1[i] = ' ';
/*TODO*///		s1[8] = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	l2 = strlen(s2);
/*TODO*///	if (l2 < 8)
/*TODO*///	{
/*TODO*///		for (i = l2 + 1; i < 8; i++) s2[i] = ' ';
/*TODO*///		s2[8] = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	for (i = 0; i < 8; i++)
/*TODO*///	{
/*TODO*///		if (s1[i] == '?' && s2[i] != '?') s1[i] = s2[i];
/*TODO*///		if (s2[i] == '?' && s1[i] != '?') s2[i] = s1[i];
/*TODO*///	}
/*TODO*///
/*TODO*///	return stricmp(s1, s2);
    }

    static String namecopy(String name_ref, String desc) {
        String name = "";

        name = desc;

        /* remove details in parenthesis */
        if (strstr(name, " (") != -1) {
            name = name.substring(0, strstr(name, " ("));
        }

        /* Move leading "The" to the end */
        if (strncmp(name.toCharArray(), "The ", 4) == false) {
            name_ref = sprintf("%s, The", name + 4);
        } else {
            name_ref = sprintf("%s", name);
        }

        return name_ref;
    }

    /*TODO*///
/*TODO*///
/*TODO*////* Identifies a rom from from this checksum */
/*TODO*///static void match_roms(const struct GameDriver *driver,const char* hash,int *found)
/*TODO*///{
/*TODO*///	const struct RomModule *region, *rom;
/*TODO*///
/*TODO*///	for (region = rom_first_region(driver); region; region = rom_next_region(region))
/*TODO*///	{
/*TODO*///		for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
/*TODO*///		{
/*TODO*///			if (hash_data_is_equal(hash, ROM_GETHASHDATA(rom), 0))
/*TODO*///			{
/*TODO*///				char baddump = hash_data_has_info(ROM_GETHASHDATA(rom), HASH_INFO_BAD_DUMP);
/*TODO*///
/*TODO*///				if (!silentident)
/*TODO*///				{
/*TODO*///					if (*found != 0)
/*TODO*///						printf("             ");
/*TODO*///					printf("= %s%-12s  %s\n",baddump ? "(BAD) " : "",ROM_GETNAME(rom),driver->description);
/*TODO*///				}
/*TODO*///				(*found)++;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///void identify_rom(const char* name, const char* hash, int length)
/*TODO*///{
/*TODO*///	int found = 0;
/*TODO*///
/*TODO*///	/* remove directory name */
/*TODO*///	int i;
/*TODO*///	for (i = strlen(name)-1;i >= 0;i--)
/*TODO*///	{
/*TODO*///		if (name[i] == '/' || name[i] == '\\')
/*TODO*///		{
/*TODO*///			i++;
/*TODO*///			break;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	if (!silentident)
/*TODO*///		printf("%s ",&name[0]);
/*TODO*///
/*TODO*///	for (i = 0; drivers[i]; i++)
/*TODO*///		match_roms(drivers[i],hash,&found);
/*TODO*///
/*TODO*///	for (i = 0; test_drivers[i]; i++)
/*TODO*///		match_roms(test_drivers[i],hash,&found);
/*TODO*///
/*TODO*///	if (found == 0)
/*TODO*///	{
/*TODO*///		unsigned size = length;
/*TODO*///		while (size && (size & 1) == 0) size >>= 1;
/*TODO*///		if (size & ~1)
/*TODO*///		{
/*TODO*///			if (!silentident)
/*TODO*///				printf("NOT A ROM\n");
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			if (!silentident)
/*TODO*///				printf("NO MATCH\n");
/*TODO*///			if (knownstatus == KNOWN_START)
/*TODO*///				knownstatus = KNOWN_NONE;
/*TODO*///			else if (knownstatus == KNOWN_ALL)
/*TODO*///				knownstatus = KNOWN_SOME;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		if (knownstatus == KNOWN_START)
/*TODO*///			knownstatus = KNOWN_ALL;
/*TODO*///		else if (knownstatus == KNOWN_NONE)
/*TODO*///			knownstatus = KNOWN_SOME;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*////* Identifies a file from this checksum */
/*TODO*///void identify_file(const char* name)
/*TODO*///{
/*TODO*///	FILE *f;
/*TODO*///	int length;
/*TODO*///	char* data;
/*TODO*///	char hash[HASH_BUF_SIZE];
/*TODO*///
/*TODO*///	f = fopen(name,"rb");
/*TODO*///	if (!f) {
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* determine length of file */
/*TODO*///	if (fseek (f, 0L, SEEK_END)!=0)	{
/*TODO*///		fclose(f);
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	length = ftell(f);
/*TODO*///	if (length == -1L) {
/*TODO*///		fclose(f);
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* empty file */
/*TODO*///	if (!length) {
/*TODO*///		fclose(f);
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* allocate space for entire file */
/*TODO*///	data = (char*)malloc(length);
/*TODO*///	if (!data) {
/*TODO*///		fclose(f);
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (fseek (f, 0L, SEEK_SET)!=0) {
/*TODO*///		free(data);
/*TODO*///		fclose(f);
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (fread(data, 1, length, f) != length) {
/*TODO*///		free(data);
/*TODO*///		fclose(f);
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	fclose(f);
/*TODO*///
/*TODO*///	/* Compute checksum of all the available functions. Since MAME for
/*TODO*///	   now carries inforamtions only for CRC and SHA1, we compute only
/*TODO*///	   these */
/*TODO*///	if (options.crc_only)
/*TODO*///		hash_compute(hash, data, length, HASH_CRC);
/*TODO*///	else
/*TODO*///		hash_compute(hash, data, length, HASH_CRC|HASH_SHA1);
/*TODO*///
/*TODO*///	/* Try to identify the ROM */
/*TODO*///	identify_rom(name, hash, length);
/*TODO*///
/*TODO*///	free(data);
/*TODO*///}
/*TODO*///
/*TODO*///void identify_zip(const char* zipname)
/*TODO*///{
/*TODO*///	struct zipent* ent;
/*TODO*///
/*TODO*///	ZIP* zip = openzip( FILETYPE_RAW, 0, zipname );
/*TODO*///	if (!zip)
/*TODO*///		return;
/*TODO*///
/*TODO*///	while ((ent = readzip(zip))) {
/*TODO*///		/* Skip empty file and directory */
/*TODO*///		if (ent->uncompressed_size!=0) {
/*TODO*///			char* buf = (char*)malloc(strlen(zipname)+1+strlen(ent->name)+1);
/*TODO*///			char hash[HASH_BUF_SIZE];
/*TODO*///			UINT8 crcs[4];
/*TODO*///
/*TODO*/////			sprintf(buf,"%s/%s",zipname,ent->name);
/*TODO*///			sprintf(buf,"%-12s",ent->name);
/*TODO*///
/*TODO*///			/* Decompress the ROM from the ZIP, and compute all the needed
/*TODO*///			   checksums. Since MAME for now carries informations only for CRC and
/*TODO*///			   SHA1, we compute only these (actually, CRC is extracted from the
/*TODO*///			   ZIP header) */
/*TODO*///			hash_data_clear(hash);
/*TODO*///
/*TODO*///			if (!options.crc_only)
/*TODO*///			{
/*TODO*///				UINT8* data =  (UINT8*)malloc(ent->uncompressed_size);
/*TODO*///				readuncompresszip(zip, ent, data);
/*TODO*///				hash_compute(hash, data, ent->uncompressed_size, HASH_SHA1);
/*TODO*///				free(data);
/*TODO*///			}
/*TODO*///
/*TODO*///			crcs[0] = (UINT8)(ent->crc32 >> 24);
/*TODO*///			crcs[1] = (UINT8)(ent->crc32 >> 16);
/*TODO*///			crcs[2] = (UINT8)(ent->crc32 >> 8);
/*TODO*///			crcs[3] = (UINT8)(ent->crc32 >> 0);
/*TODO*///			hash_data_insert_binary_checksum(hash, HASH_CRC, crcs);
/*TODO*///
/*TODO*///			/* Try to identify the ROM */
/*TODO*///			identify_rom(buf, hash, ent->uncompressed_size);
/*TODO*///
/*TODO*///			free(buf);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	closezip(zip);
/*TODO*///}
/*TODO*///
/*TODO*///void romident(const char* name, int enter_dirs);
/*TODO*///
/*TODO*///void identify_dir(const char* dirname)
/*TODO*///{
/*TODO*///	DIR *dir;
/*TODO*///	struct dirent *ent;
/*TODO*///
/*TODO*///	dir = opendir(dirname);
/*TODO*///	if (!dir) {
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	ent = readdir(dir);
/*TODO*///	while (ent) {
/*TODO*///		/* Skip special files */
/*TODO*///		if (ent->d_name[0]!='.') {
/*TODO*///			char* buf = (char*)malloc(strlen(dirname)+1+strlen(ent->d_name)+1);
/*TODO*///			sprintf(buf,"%s/%s",dirname,ent->d_name);
/*TODO*///			romident(buf,0);
/*TODO*///			free(buf);
/*TODO*///		}
/*TODO*///
/*TODO*///		ent = readdir(dir);
/*TODO*///	}
/*TODO*///	closedir(dir);
/*TODO*///}
/*TODO*///
/*TODO*///void romident(const char* name,int enter_dirs) {
/*TODO*///	struct stat s;
/*TODO*///
/*TODO*///	if (stat(name,&s) != 0)	{
/*TODO*///		printf("%s: %s\n",name,strerror(errno));
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (S_ISDIR(s.st_mode)) {
/*TODO*///		if (enter_dirs)
/*TODO*///			identify_dir(name);
/*TODO*///	} else {
/*TODO*///		unsigned l = strlen(name);
/*TODO*///		if (l>=4 && stricmp(name+l-4,".zip")==0)
/*TODO*///			identify_zip(name);
/*TODO*///		else
/*TODO*///			identify_file(name);
/*TODO*///		return;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///void CLIB_DECL terse_printf(const char *fmt,...)
/*TODO*///{
/*TODO*///	/* no-op */
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///int CLIB_DECL compare_names(const void *elem1, const void *elem2)
/*TODO*///{
/*TODO*///	struct GameDriver *drv1 = *(struct GameDriver **)elem1;
/*TODO*///	struct GameDriver *drv2 = *(struct GameDriver **)elem2;
/*TODO*///	char name1[200],name2[200];
/*TODO*///	namecopy(name1,drv1->description);
/*TODO*///	namecopy(name2,drv2->description);
/*TODO*///	return strcmp(name1,name2);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///int CLIB_DECL compare_driver_names(const void *elem1, const void *elem2)
/*TODO*///{
/*TODO*///	struct GameDriver *drv1 = *(struct GameDriver **)elem1;
/*TODO*///	struct GameDriver *drv2 = *(struct GameDriver **)elem2;
/*TODO*///	return strcmp(drv1->name, drv2->name);
/*TODO*///}
/*TODO*///
/*TODO*///
    public static int frontend_help(String gamename) {
        /*TODO*///	struct InternalMachineDriver drv;
        int i, j;
        /*TODO*///	const char *all_games = "*";
/*TODO*///
        /* display help unless a game or an utility are specified */
        if (gamename == null && help == 0 && list == 0 && ident == 0 && verify == 0) {
            help = 1;
        }
        if (help != 0) /* brief help - useful to get current version info */ {
            printf("M.A.M.E. v%s - Multiple Arcade Machine Emulator\n"
                    + "Copyright (C) 1997-2003 by Nicola Salmoria and the MAME Team\n\n", build_version
            );
            showdisclaimer();
            printf("Usage:  MAME gamename [options]\n\n"
                    + "        MAME -list         for a brief list of supported games\n"
                    + "        MAME -listfull     for a full list of supported games\n"
                    + "        MAME -showusage    for a brief list of options\n"
                    + "        MAME -showconfig   for a list of configuration options\n"
                    + "        MAME -createconfig to create a mame.ini\n\n"
                    + "For usage instructions, please consult the corresponding readme.\n\n"
                    + "MS-DOS:   msdos.txt\n"
                    + "Windows:  windows.txt\n");
            return 0;
        }
        /*TODO*///
/*TODO*///	/* HACK: some options REQUIRE gamename field to work: default to "*" */
/*TODO*///	if (!gamename || (strlen(gamename) == 0))
/*TODO*///		gamename = all_games;
/*TODO*///
/*TODO*///	/* sort the list if requested */
/*TODO*///	if (sortby)
/*TODO*///	{
/*TODO*///		int count = 0;
/*TODO*///
/*TODO*///		/* first count the drivers */
/*TODO*///		while (drivers[count]) count++;
/*TODO*///
/*TODO*///		/* qsort as appropriate */
/*TODO*///		if (sortby == 1)
/*TODO*///			qsort(drivers, count, sizeof(drivers[0]), compare_names);
/*TODO*///		else if (sortby == 2)
/*TODO*///			qsort(drivers, count, sizeof(drivers[0]), compare_driver_names);
/*TODO*///	}
/*TODO*///
        switch (list) /* front-end utilities ;) */ {
            case LIST_SHORT:
                /* simple games list */
                printf("\nMAME currently supports the following games:\n\n");
                for (i = j = 0; drivers[i] != null; i++) {
                    if ((listclones != 0 || drivers[i].clone_of == null
                            || (drivers[i].clone_of.flags & NOT_A_DRIVER) != 0) && strwildcmp(gamename, drivers[i].name) == 0) {
                        printf("%-8s", drivers[i].name);
                        j++;
                        if ((j % 8) == 0) {
                            printf("\n");
                        } else {
                            printf("  ");
                        }
                    }
                }
                if ((j % 8) != 0) {
                    printf("\n");
                }
                printf("\n");
                if (j != i) {
                    printf("Total ROM sets displayed: %4d - ", j);
                }
                printf("Total ROM sets supported: %4d\n", i);
                return 0;
            case LIST_FULL:
                /* games list with descriptions */
                printf("Name:     Description:\n");
                for (i = j = 0; drivers[i] != null; i++) {
                    if ((listclones != 0 || drivers[i].clone_of == null
                            || (drivers[i].clone_of.flags & NOT_A_DRIVER) != 0) && strwildcmp(gamename, drivers[i].name) == 0) {
                        String name = "";

                        printf("%-10s", drivers[i].name);

                        name = namecopy(name, drivers[i].description);
                        printf("\"%s", name);

                        /* print the additional description only if we are listing clones */
                        if (listclones != 0) {
                            if (strchr(drivers[i].description, '(') != null) {
                                printf(" %s", strchr(drivers[i].description, '('));
                            }
                        }
                        printf("\"\n");
                    }
                }
                return 0;
            /*TODO*///			break;
/*TODO*///
/*TODO*///		case LIST_SAMDIR: /* games list with samples directories */
/*TODO*///			printf("Name:     Samples dir:\n");
/*TODO*///			for (i = 0; drivers[i]; i++)
/*TODO*///				if ((listclones || drivers[i]->clone_of == 0
/*TODO*///						|| (drivers[i]->clone_of->flags & NOT_A_DRIVER)
/*TODO*///						) && !strwildcmp(gamename, drivers[i]->name))
/*TODO*///				{
/*TODO*///					expand_machine_driver(drivers[i]->drv, &drv);
/*TODO*///#if (HAS_SAMPLES || HAS_VLM5030)
/*TODO*///					for( j = 0; drv.sound[j].sound_type && j < MAX_SOUND; j++ )
/*TODO*///					{
/*TODO*///						const char **samplenames = NULL;
/*TODO*///#if (HAS_SAMPLES)
/*TODO*///						if( drv.sound[j].sound_type == SOUND_SAMPLES )
/*TODO*///							samplenames = ((struct Samplesinterface *)drv.sound[j].sound_interface)->samplenames;
/*TODO*///#endif
/*TODO*///						if (samplenames != 0 && samplenames[0] != 0)
/*TODO*///						{
/*TODO*///							printf("%-10s",drivers[i]->name);
/*TODO*///							if (samplenames[0][0] == '*')
/*TODO*///								printf("%s\n",samplenames[0]+1);
/*TODO*///							else
/*TODO*///								printf("%s\n",drivers[i]->name);
/*TODO*///						}
/*TODO*///					}
/*TODO*///#endif
/*TODO*///				}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///		case LIST_ROMS: /* game roms list or */
/*TODO*///		case LIST_SAMPLES: /* game samples list */
/*TODO*///			j = 0;
/*TODO*///			while (drivers[j] && (stricmp(gamename,drivers[j]->name) != 0))
/*TODO*///				j++;
/*TODO*///			if (drivers[j] == 0)
/*TODO*///			{
/*TODO*///				printf("Game \"%s\" not supported!\n",gamename);
/*TODO*///				return 1;
/*TODO*///			}
/*TODO*///			gamedrv = drivers[j];
/*TODO*///			if (list == LIST_ROMS)
/*TODO*///				printromlist(gamedrv->rom,gamename);
/*TODO*///			else
/*TODO*///			{
/*TODO*///#if (HAS_SAMPLES || HAS_VLM5030)
/*TODO*///				int k;
/*TODO*///				expand_machine_driver(gamedrv->drv, &drv);
/*TODO*///				for( k = 0; drv.sound[k].sound_type && k < MAX_SOUND; k++ )
/*TODO*///				{
/*TODO*///					const char **samplenames = NULL;
/*TODO*///#if (HAS_SAMPLES)
/*TODO*///					if( drv.sound[k].sound_type == SOUND_SAMPLES )
/*TODO*///							samplenames = ((struct Samplesinterface *)drv.sound[k].sound_interface)->samplenames;
/*TODO*///#endif
/*TODO*///					if (samplenames != 0 && samplenames[0] != 0)
/*TODO*///					{
/*TODO*///						i = 0;
/*TODO*///						while (samplenames[i] != 0)
/*TODO*///						{
/*TODO*///							printf("%s\n",samplenames[i]);
/*TODO*///							i++;
/*TODO*///						}
/*TODO*///					}
/*TODO*///                }
/*TODO*///#endif
/*TODO*///			}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///		case LIST_LMR:
/*TODO*///			{
/*TODO*///				int total;
/*TODO*///
/*TODO*///				total = 0;
/*TODO*///				for (i = 0; drivers[i]; i++)
/*TODO*///						total++;
/*TODO*///				for (i = 0; drivers[i]; i++)
/*TODO*///				{
/*TODO*///					static int first_missing = 1;
/*TODO*/////					get_rom_sample_path (argc, argv, i, NULL);
/*TODO*///					if (RomsetMissing (i))
/*TODO*///					{
/*TODO*///						if (first_missing)
/*TODO*///						{
/*TODO*///							first_missing = 0;
/*TODO*///							printf ("game      clone of  description\n");
/*TODO*///							printf ("--------  --------  -----------\n");
/*TODO*///						}
/*TODO*///						printf ("%-10s%-10s%s\n",
/*TODO*///								drivers[i]->name,
/*TODO*///								(drivers[i]->clone_of) ? drivers[i]->clone_of->name : "",
/*TODO*///								drivers[i]->description);
/*TODO*///					}
/*TODO*///					fprintf(stderr,"%d%%\r",100 * (i+1) / total);
/*TODO*///				}
/*TODO*///			}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///		case LIST_DETAILS: /* A detailed MAMELIST.TXT type roms lister */
/*TODO*///
/*TODO*///			/* First, we shall print the header */
/*TODO*///
/*TODO*///			printf(" romname driver     ");
/*TODO*///			for(j=0;j<MAX_CPU;j++) printf("cpu %d    ",j+1);
/*TODO*///			for(j=0;j<MAX_SOUND;j++) printf("sound %d     ",j+1);
/*TODO*///			printf("name\n");
/*TODO*///			printf("-------- ---------- ");
/*TODO*///			for(j=0;j<MAX_CPU;j++) printf("-------- ");
/*TODO*///			for(j=0;j<MAX_SOUND;j++) printf("----------- ");
/*TODO*///			printf("--------------------------\n");
/*TODO*///
/*TODO*///			/* Let's cycle through the drivers */
/*TODO*///
/*TODO*///			for (i = 0; drivers[i]; i++)
/*TODO*///				if ((listclones || drivers[i]->clone_of == 0
/*TODO*///						|| (drivers[i]->clone_of->flags & NOT_A_DRIVER)
/*TODO*///						) && !strwildcmp(gamename, drivers[i]->name))
/*TODO*///				{
/*TODO*///					/* Dummy structs to fetch the information from */
/*TODO*///
/*TODO*///					const struct MachineCPU *x_cpu;
/*TODO*///					const struct MachineSound *x_sound;
/*TODO*///					struct InternalMachineDriver x_driver;
/*TODO*///
/*TODO*///					expand_machine_driver(drivers[i]->drv, &x_driver);
/*TODO*///					x_cpu = x_driver.cpu;
/*TODO*///					x_sound = x_driver.sound;
/*TODO*///
/*TODO*///					/* First, the rom name */
/*TODO*///
/*TODO*///					printf("%-8s ",drivers[i]->name);
/*TODO*///
/*TODO*///					#ifndef MESS
/*TODO*///					/* source file (skip the leading "src/drivers/" */
/*TODO*///					printf("%-10s ",&drivers[i]->source_file[12]);
/*TODO*///					#else
/*TODO*///					/* source file (skip the leading "src/mess/systems/" */
/*TODO*///					printf("%-10s ",&drivers[i]->source_file[17]);
/*TODO*///					#endif
/*TODO*///
/*TODO*///					/* Then, cpus */
/*TODO*///
/*TODO*///					for(j=0;j<MAX_CPU;j++)
/*TODO*///					{
/*TODO*///						if (x_cpu[j].cpu_flags & CPU_AUDIO_CPU)
/*TODO*///							printf("[%-6s] ",cputype_name(x_cpu[j].cpu_type));
/*TODO*///						else
/*TODO*///							printf("%-8s ",cputype_name(x_cpu[j].cpu_type));
/*TODO*///					}
/*TODO*///
/*TODO*///					/* Then, sound chips */
/*TODO*///
/*TODO*///					for(j=0;j<MAX_SOUND;j++)
/*TODO*///					{
/*TODO*///						if (sound_num(&x_sound[j]))
/*TODO*///						{
/*TODO*///							printf("%dx",sound_num(&x_sound[j]));
/*TODO*///							printf("%-9s ",sound_name(&x_sound[j]));
/*TODO*///						}
/*TODO*///						else
/*TODO*///							printf("%-11s ",sound_name(&x_sound[j]));
/*TODO*///					}
/*TODO*///
/*TODO*///					/* Lastly, the name of the game and a \newline */
/*TODO*///
/*TODO*///					printf("%s\n",drivers[i]->description);
/*TODO*///				}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///		case LIST_GAMELIST: /* GAMELIST.TXT */
/*TODO*///			printf("This is the complete list of games supported by MAME %s.\n",build_version);
/*TODO*///			if (!listclones)
/*TODO*///				printf("Variants of the same game are not included, you can use the -listclones command\n"
/*TODO*///					"to get a list of the alternate versions of a given game.\n");
/*TODO*///			printf("\n"
/*TODO*///				"This list is generated automatically and is not 100%% accurate (particularly in\n"
/*TODO*///				"the Screen Flip column). Please let us know of any errors so we can correct\n"
/*TODO*///				"them.\n"
/*TODO*///				"\n"
/*TODO*///				"Here are the meanings of the columns:\n"
/*TODO*///				"\n"
/*TODO*///				"Working\n"
/*TODO*///				"=======\n"
/*TODO*///				"  NO: Emulation is still in progress; the game does not work correctly. This\n"
/*TODO*///				"  means anything from major problems to a black screen.\n"
/*TODO*///				"\n"
/*TODO*///				"Correct Colors\n"
/*TODO*///				"==============\n"
/*TODO*///				"    YES: Colors should be identical to the original.\n"
/*TODO*///				"  CLOSE: Colors are nearly correct.\n"
/*TODO*///				"     NO: Colors are completely wrong. \n"
/*TODO*///				"  \n"
/*TODO*///				"  Note: In some cases, the color PROMs for some games are not yet available.\n"
/*TODO*///				"  This causes a NO GOOD DUMP KNOWN message on startup (and, of course, the game\n"
/*TODO*///				"  has wrong colors). The game will still say YES in this column, however,\n"
/*TODO*///				"  because the code to handle the color PROMs has been added to the driver. When\n"
/*TODO*///				"  the PROMs are available, the colors will be correct.\n"
/*TODO*///				"\n"
/*TODO*///				"Sound\n"
/*TODO*///				"=====\n"
/*TODO*///				"  PARTIAL: Sound support is incomplete or not entirely accurate. \n"
/*TODO*///				"\n"
/*TODO*///				"  Note: Some original games contain analog sound circuitry, which is difficult\n"
/*TODO*///				"  to emulate. Therefore, these emulated sounds may be significantly different.\n"
/*TODO*///				"\n"
/*TODO*///				"Screen Flip\n"
/*TODO*///				"===========\n"
/*TODO*///				"  Many games were offered in cocktail-table models, allowing two players to sit\n"
/*TODO*///				"  across from each other; the game's image flips 180 degrees for each player's\n"
/*TODO*///				"  turn. Some games also have a \"Flip Screen\" DIP switch setting to turn the\n"
/*TODO*///				"  picture (particularly useful with vertical games).\n"
/*TODO*///				"  In many cases, this feature has not yet been emulated.\n"
/*TODO*///				"\n"
/*TODO*///				"Internal Name\n"
/*TODO*///				"=============\n"
/*TODO*///				"  This is the unique name that must be used when running the game from a\n"
/*TODO*///				"  command line.\n"
/*TODO*///				"\n"
/*TODO*///				"  Note: Each game's ROM set must be placed in the ROM path, either in a .zip\n"
/*TODO*///				"  file or in a subdirectory with the game's Internal Name. The former is\n"
/*TODO*///				"  suggested, because the files will be identified by their CRC instead of\n"
/*TODO*///				"  requiring specific names.\n\n");
/*TODO*///			printf("+----------------------------------+-------+-------+-------+-------+----------+\n");
/*TODO*///			printf("|                                  |       |Correct|       |Screen | Internal |\n");
/*TODO*///			printf("| Game Name                        |Working|Colors | Sound | Flip  |   Name   |\n");
/*TODO*///			printf("+----------------------------------+-------+-------+-------+-------+----------+\n");
/*TODO*///
/*TODO*///			for (i = 0; drivers[i]; i++)
/*TODO*///				if ((listclones || drivers[i]->clone_of == 0
/*TODO*///						|| (drivers[i]->clone_of->flags & NOT_A_DRIVER)
/*TODO*///						) && !strwildcmp(gamename, drivers[i]->name))
/*TODO*///				{
/*TODO*///					char name_ref[200];
/*TODO*///
/*TODO*///					namecopy(name_ref,drivers[i]->description);
/*TODO*///
/*TODO*///					strcat(name_ref," ");
/*TODO*///
/*TODO*///					/* print the additional description only if we are listing clones */
/*TODO*///					if (listclones)
/*TODO*///					{
/*TODO*///						if (strchr(drivers[i]->description,'('))
/*TODO*///							strcat(name_ref,strchr(drivers[i]->description,'('));
/*TODO*///					}
/*TODO*///
/*TODO*///					printf("| %-33.33s",name_ref);
/*TODO*///
/*TODO*///					if (drivers[i]->flags & (GAME_NOT_WORKING | GAME_UNEMULATED_PROTECTION))
/*TODO*///					{
/*TODO*///						const struct GameDriver *maindrv;
/*TODO*///						int foundworking;
/*TODO*///
/*TODO*///						if (drivers[i]->clone_of && !(drivers[i]->clone_of->flags & NOT_A_DRIVER))
/*TODO*///							maindrv = drivers[i]->clone_of;
/*TODO*///						else maindrv = drivers[i];
/*TODO*///
/*TODO*///						foundworking = 0;
/*TODO*///						j = 0;
/*TODO*///						while (drivers[j])
/*TODO*///						{
/*TODO*///							if (drivers[j] == maindrv || drivers[j]->clone_of == maindrv)
/*TODO*///							{
/*TODO*///								if ((drivers[j]->flags & (GAME_NOT_WORKING | GAME_UNEMULATED_PROTECTION)) == 0)
/*TODO*///								{
/*TODO*///									foundworking = 1;
/*TODO*///									break;
/*TODO*///								}
/*TODO*///							}
/*TODO*///							j++;
/*TODO*///						}
/*TODO*///
/*TODO*///						if (foundworking)
/*TODO*///							printf("| No(1) ");
/*TODO*///						else
/*TODO*///							printf("|   No  ");
/*TODO*///					}
/*TODO*///					else
/*TODO*///						printf("|  Yes  ");
/*TODO*///
/*TODO*///					if (drivers[i]->flags & GAME_WRONG_COLORS)
/*TODO*///						printf("|   No  ");
/*TODO*///					else if (drivers[i]->flags & GAME_IMPERFECT_COLORS)
/*TODO*///						printf("| Close ");
/*TODO*///					else
/*TODO*///						printf("|  Yes  ");
/*TODO*///
/*TODO*///					{
/*TODO*///						const char **samplenames = NULL;
/*TODO*///						expand_machine_driver(drivers[i]->drv, &drv);
/*TODO*///#if (HAS_SAMPLES || HAS_VLM5030)
/*TODO*///						for (j = 0;drv.sound[j].sound_type && j < MAX_SOUND; j++)
/*TODO*///						{
/*TODO*///#if (HAS_SAMPLES)
/*TODO*///							if (drv.sound[j].sound_type == SOUND_SAMPLES)
/*TODO*///							{
/*TODO*///								samplenames = ((struct Samplesinterface *)drv.sound[j].sound_interface)->samplenames;
/*TODO*///								break;
/*TODO*///							}
/*TODO*///#endif
/*TODO*///						}
/*TODO*///#endif
/*TODO*///						if (drivers[i]->flags & GAME_NO_SOUND)
/*TODO*///							printf("|   No  ");
/*TODO*///						else if (drivers[i]->flags & GAME_IMPERFECT_SOUND)
/*TODO*///						{
/*TODO*///							if (samplenames)
/*TODO*///								printf("|Part(2)");
/*TODO*///							else
/*TODO*///								printf("|Partial");
/*TODO*///						}
/*TODO*///						else
/*TODO*///						{
/*TODO*///							if (samplenames)
/*TODO*///								printf("| Yes(2)");
/*TODO*///							else
/*TODO*///								printf("|  Yes  ");
/*TODO*///						}
/*TODO*///					}
/*TODO*///
/*TODO*///					if (drivers[i]->flags & GAME_NO_COCKTAIL)
/*TODO*///						printf("|   No  ");
/*TODO*///					else
/*TODO*///						printf("|  Yes  ");
/*TODO*///
/*TODO*///					printf("| %-8s |\n",drivers[i]->name);
/*TODO*///				}
/*TODO*///
/*TODO*///			printf("+----------------------------------+-------+-------+-------+-------+----------+\n\n");
/*TODO*///			printf("(1) There are variants of the game (usually bootlegs) that work correctly\n");
/*TODO*///#if (HAS_SAMPLES)
/*TODO*///			printf("(2) Needs samples provided separately\n");
/*TODO*///#endif
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
            case LIST_GAMES:
                /* list games, production year, manufacturer */
                for (i = 0; drivers[i] != null; i++) {
                    if ((listclones != 0 || drivers[i].clone_of == null
                            || (drivers[i].clone_of.flags & NOT_A_DRIVER) != 0) && strwildcmp(gamename, drivers[i].description) == 0) {
                        String name = "";

                        printf("%-5s%-36s ", drivers[i].year, drivers[i].manufacturer);

                        name = namecopy(name, drivers[i].description);
                        printf("%s", name);

                        /* print the additional description only if we are listing clones */
                        if (listclones != 0) {
                            if (strchr(drivers[i].description, '(') != null) {
                                printf(" %s", strchr(drivers[i].description, '('));
                            }
                        }
                        printf("\n");
                    }
                }
                return 0;

            case LIST_CLONES:
                /* list clones */
                printf("Name:    Clone of:\n");
                for (i = 0; drivers[i] != null; i++) {
                    if (drivers[i].clone_of != null && (drivers[i].clone_of.flags & NOT_A_DRIVER) == 0
                            && (strwildcmp(gamename, drivers[i].name) == 0
                            || strwildcmp(gamename, drivers[i].clone_of.name) == 0)) {
                        printf("%-8s %-8s\n", drivers[i].name, drivers[i].clone_of.name);
                    }
                }
                return 0;
            /*TODO*///
/*TODO*///		case LIST_WRONGORIENTATION: /* list drivers which incorrectly use the orientation and visible area fields */
/*TODO*///			for (i = 0; drivers[i]; i++)
/*TODO*///			{
/*TODO*///				expand_machine_driver(drivers[i]->drv, &drv);
/*TODO*///				if ((drv.video_attributes & VIDEO_TYPE_VECTOR) == 0 &&
/*TODO*///						(drivers[i]->clone_of == 0
/*TODO*///								|| (drivers[i]->clone_of->flags & NOT_A_DRIVER)) &&
/*TODO*///						drv.default_visible_area.max_x - drv.default_visible_area.min_x + 1 <=
/*TODO*///						drv.default_visible_area.max_y - drv.default_visible_area.min_y + 1)
/*TODO*///				{
/*TODO*///					if (strcmp(drivers[i]->name,"crater") &&
/*TODO*///						strcmp(drivers[i]->name,"mpatrol") &&
/*TODO*///						strcmp(drivers[i]->name,"troangel") &&
/*TODO*///						strcmp(drivers[i]->name,"travrusa") &&
/*TODO*///						strcmp(drivers[i]->name,"kungfum") &&
/*TODO*///						strcmp(drivers[i]->name,"battroad") &&
/*TODO*///						strcmp(drivers[i]->name,"vigilant") &&
/*TODO*///						strcmp(drivers[i]->name,"sonson") &&
/*TODO*///						strcmp(drivers[i]->name,"brkthru") &&
/*TODO*///						strcmp(drivers[i]->name,"darwin") &&
/*TODO*///						strcmp(drivers[i]->name,"exprraid") &&
/*TODO*///						strcmp(drivers[i]->name,"sidetrac") &&
/*TODO*///						strcmp(drivers[i]->name,"targ") &&
/*TODO*///						strcmp(drivers[i]->name,"spectar") &&
/*TODO*///						strcmp(drivers[i]->name,"venture") &&
/*TODO*///						strcmp(drivers[i]->name,"mtrap") &&
/*TODO*///						strcmp(drivers[i]->name,"pepper2") &&
/*TODO*///						strcmp(drivers[i]->name,"hardhat") &&
/*TODO*///						strcmp(drivers[i]->name,"fax") &&
/*TODO*///						strcmp(drivers[i]->name,"circus") &&
/*TODO*///						strcmp(drivers[i]->name,"robotbwl") &&
/*TODO*///						strcmp(drivers[i]->name,"crash") &&
/*TODO*///						strcmp(drivers[i]->name,"ripcord") &&
/*TODO*///						strcmp(drivers[i]->name,"starfire") &&
/*TODO*///						strcmp(drivers[i]->name,"fireone") &&
/*TODO*///						strcmp(drivers[i]->name,"renegade") &&
/*TODO*///						strcmp(drivers[i]->name,"battlane") &&
/*TODO*///						strcmp(drivers[i]->name,"megatack") &&
/*TODO*///						strcmp(drivers[i]->name,"killcom") &&
/*TODO*///						strcmp(drivers[i]->name,"challeng") &&
/*TODO*///						strcmp(drivers[i]->name,"kaos") &&
/*TODO*///						strcmp(drivers[i]->name,"formatz") &&
/*TODO*///						strcmp(drivers[i]->name,"bankp") &&
/*TODO*///						strcmp(drivers[i]->name,"liberatr") &&
/*TODO*///						strcmp(drivers[i]->name,"toki") &&
/*TODO*///						strcmp(drivers[i]->name,"stactics") &&
/*TODO*///						strcmp(drivers[i]->name,"sprint1") &&
/*TODO*///						strcmp(drivers[i]->name,"sprint2") &&
/*TODO*///						strcmp(drivers[i]->name,"nitedrvr") &&
/*TODO*///						strcmp(drivers[i]->name,"punchout") &&
/*TODO*///						strcmp(drivers[i]->name,"spnchout") &&
/*TODO*///						strcmp(drivers[i]->name,"armwrest") &&
/*TODO*///						strcmp(drivers[i]->name,"route16") &&
/*TODO*///						strcmp(drivers[i]->name,"stratvox") &&
/*TODO*///						strcmp(drivers[i]->name,"irobot") &&
/*TODO*///						strcmp(drivers[i]->name,"leprechn") &&
/*TODO*///						strcmp(drivers[i]->name,"starcrus") &&
/*TODO*///						strcmp(drivers[i]->name,"astrof") &&
/*TODO*///						strcmp(drivers[i]->name,"tomahawk") &&
/*TODO*///						1)
/*TODO*///						printf("%s %dx%d\n",drivers[i]->name,
/*TODO*///								drv.default_visible_area.max_x - drv.default_visible_area.min_x + 1,
/*TODO*///								drv.default_visible_area.max_y - drv.default_visible_area.min_y + 1);
/*TODO*///				}
/*TODO*///			}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///		case LIST_WRONGFPS: /* list drivers with too high frame rate */
/*TODO*///			for (i = 0; drivers[i]; i++)
/*TODO*///			{
/*TODO*///				expand_machine_driver(drivers[i]->drv, &drv);
/*TODO*///				if ((drv.video_attributes & VIDEO_TYPE_VECTOR) == 0 &&
/*TODO*///						(drivers[i]->clone_of == 0
/*TODO*///								|| (drivers[i]->clone_of->flags & NOT_A_DRIVER)) &&
/*TODO*///						drv.frames_per_second > 57 &&
/*TODO*///						drv.default_visible_area.max_y - drv.default_visible_area.min_y + 1 > 244 &&
/*TODO*///						drv.default_visible_area.max_y - drv.default_visible_area.min_y + 1 <= 256)
/*TODO*///				{
/*TODO*///					printf("%s %dx%d %fHz\n",drivers[i]->name,
/*TODO*///							drv.default_visible_area.max_x - drv.default_visible_area.min_x + 1,
/*TODO*///							drv.default_visible_area.max_y - drv.default_visible_area.min_y + 1,
/*TODO*///							drv.frames_per_second);
/*TODO*///				}
/*TODO*///			}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
            case LIST_SOURCEFILE:
                for (i = 0; drivers[i] != null; i++) {
                    if (strwildcmp(gamename, drivers[i].name) == 0) {
                        printf("%-8s %s\n", drivers[i].name, drivers[i].source_file);
                    }
                }
                return 0;

            case LIST_GAMESPERSOURCEFILE: {
                int MAXCOUNT = 8;

                int[] numcount = new int[MAXCOUNT];
                int[] gamescount = new int[MAXCOUNT];

                for (i = 0; i < MAXCOUNT; i++) {
                    numcount[i] = gamescount[i] = 0;
                }

                for (i = 0; drivers[i] != null; i++) {
                    if (drivers[i].clone_of == null
                            || ((drivers[i].clone_of.flags & NOT_A_DRIVER) != 0)) {
                        String sf = drivers[i].source_file;
                        int total = 0;

                        for (j = 0; drivers[j] != null; j++) {
                            if (drivers[j].clone_of == null
                                    || ((drivers[j].clone_of.flags & NOT_A_DRIVER) != 0)) {
                                if (drivers[j].source_file == sf) {
                                    if (j < i) {
                                        break;
                                    }

                                    total++;
                                }
                            }
                        }

                        if (total != 0) {
                            if (total == 1) {
                                numcount[0]++;
                                gamescount[0] += total;
                            } else if (total >= 2 && total <= 3) {
                                numcount[1]++;
                                gamescount[1] += total;
                            } else if (total >= 4 && total <= 7) {
                                numcount[2]++;
                                gamescount[2] += total;
                            } else if (total >= 8 && total <= 15) {
                                numcount[3]++;
                                gamescount[3] += total;
                            } else if (total >= 16 && total <= 31) {
                                numcount[4]++;
                                gamescount[4] += total;
                            } else if (total >= 32 && total <= 63) {
                                numcount[5]++;
                                gamescount[5] += total;
                            } else if (total >= 64) {
                                numcount[6]++;
                                gamescount[6] += total;
                            }
                        }
                    }
                }

                printf("1\t%d\t%d\n", numcount[0], gamescount[0]);
                printf("2-3\t%d\t%d\n", numcount[1], gamescount[1]);
                printf("4-7\t%d\t%d\n", numcount[2], gamescount[2]);
                printf("8-15\t%d\t%d\n", numcount[3], gamescount[3]);
                printf("16-31\t%d\t%d\n", numcount[4], gamescount[4]);
                printf("32-63\t%d\t%d\n", numcount[5], gamescount[5]);
                printf("64+\t%d\t%d\n", numcount[6], gamescount[6]);

            }
            return 0;
            /*TODO*///
/*TODO*///		case LIST_CRC: /* list all crc-32 */
/*TODO*///		case LIST_SHA1: /* list all sha-1 */
/*TODO*///		case LIST_MD5:  /* list all md5 */
/*TODO*///
/*TODO*///			if (list == LIST_SHA1)
/*TODO*///				j = HASH_SHA1;
/*TODO*///			else if (list == LIST_MD5)
/*TODO*///				j = HASH_MD5;
/*TODO*///			else
/*TODO*///				j = HASH_CRC;
/*TODO*///
/*TODO*///			for (i = 0; drivers[i]; i++)
/*TODO*///			{
/*TODO*///				const struct RomModule *region, *rom;
/*TODO*///
/*TODO*///				for (region = rom_first_region(drivers[i]); region; region = rom_next_region(region))
/*TODO*///					for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
/*TODO*///					{
/*TODO*///						char chksum[256];
/*TODO*///
/*TODO*///						if (hash_data_extract_printable_checksum(ROM_GETHASHDATA(rom), j, chksum))
/*TODO*///							printf("%s %-12s %s\n",chksum,ROM_GETNAME(rom),drivers[i]->description);
/*TODO*///					}
/*TODO*///			}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///		case LIST_DUPCRC: /* list duplicate crc-32 (with different ROM name) */
/*TODO*///			for (i = 0; drivers[i]; i++)
/*TODO*///			{
/*TODO*///				const struct RomModule *region, *rom;
/*TODO*///
/*TODO*///				for (region = rom_first_region(drivers[i]); region; region = rom_next_region(region))
/*TODO*///					for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
/*TODO*///						/* compare all the ROMS that we have a dump for */
/*TODO*///						if (!hash_data_has_info(ROM_GETHASHDATA(rom), HASH_INFO_NO_DUMP))
/*TODO*///						{
/*TODO*///							char first_match = 1;
/*TODO*///
/*TODO*///							for (j = i + 1; drivers[j]; j++)
/*TODO*///							{
/*TODO*///								const struct RomModule *region1, *rom1;
/*TODO*///
/*TODO*///								for (region1 = rom_first_region(drivers[j]); region1; region1 = rom_next_region(region1))
/*TODO*///									for (rom1 = rom_first_file(region1); rom1; rom1 = rom_next_file(rom1))
/*TODO*///										if (strcmp(ROM_GETNAME(rom), ROM_GETNAME(rom1)) && hash_data_is_equal(ROM_GETHASHDATA(rom), ROM_GETHASHDATA(rom1), 0))
/*TODO*///										{
/*TODO*///											/* Dump checksum infos only on the first match for a given
/*TODO*///											   ROM. This reduces the output size and makes it more
/*TODO*///											   readable. */
/*TODO*///											if (first_match)
/*TODO*///										{
/*TODO*///												char buf[512];
/*TODO*///
/*TODO*///												first_match = 0;
/*TODO*///
/*TODO*///												hash_data_print(ROM_GETHASHDATA(rom), 0, buf);
/*TODO*///												printf("%s\n", buf);
/*TODO*///												printf("    %-12s %-8s\n", ROM_GETNAME(rom),drivers[i]->name);
/*TODO*///
/*TODO*///											}
/*TODO*///
/*TODO*///											printf("    %-12s %-8s\n", ROM_GETNAME(rom1),drivers[j]->name);
/*TODO*///										}
/*TODO*///										}
/*TODO*///							}
/*TODO*///			}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///
/*TODO*///		case LIST_WRONGMERGE:	/* list duplicate crc-32 with different ROM name */
/*TODO*///								/* and different crc-32 with duplicate ROM name */
/*TODO*///								/* in clone sets */
/*TODO*///			for (i = 0; drivers[i]; i++)
/*TODO*///			{
/*TODO*///				const struct RomModule *region, *rom;
/*TODO*///
/*TODO*///				for (region = rom_first_region(drivers[i]); region; region = rom_next_region(region))
/*TODO*///				{
/*TODO*///					for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
/*TODO*///					{
/*TODO*///						if (!hash_data_has_info(ROM_GETHASHDATA(rom), HASH_INFO_NO_DUMP))
/*TODO*///						{
/*TODO*///							for (j = 0; drivers[j]; j++)
/*TODO*///							{
/*TODO*///								if (j != i &&
/*TODO*///									drivers[j]->clone_of &&
/*TODO*///									(drivers[j]->clone_of->flags & NOT_A_DRIVER) == 0 &&
/*TODO*///									(drivers[j]->clone_of == drivers[i] ||
/*TODO*///									(i < j && drivers[j]->clone_of == drivers[i]->clone_of)))
/*TODO*///								{
/*TODO*///									const struct RomModule *region1, *rom1;
/*TODO*///									int match = 0;
/*TODO*///
/*TODO*///									for (region1 = rom_first_region(drivers[j]); region1; region1 = rom_next_region(region1))
/*TODO*///									{
/*TODO*///										for (rom1 = rom_first_file(region1); rom1; rom1 = rom_next_file(rom1))
/*TODO*///										{
/*TODO*///											if (!strcmp(ROM_GETNAME(rom), ROM_GETNAME(rom1)))
/*TODO*///											{
/*TODO*///												if (!hash_data_has_info(ROM_GETHASHDATA(rom1), HASH_INFO_NO_DUMP) &&
/*TODO*///													!hash_data_is_equal(ROM_GETHASHDATA(rom), ROM_GETHASHDATA(rom1), 0))
/*TODO*///												{
/*TODO*///													char temp[512];
/*TODO*///
/*TODO*///													/* Print only the checksums available for both the roms */
/*TODO*///													unsigned int functions =
/*TODO*///														hash_data_used_functions(ROM_GETHASHDATA(rom)) &
/*TODO*///														hash_data_used_functions(ROM_GETHASHDATA(rom1));
/*TODO*///
/*TODO*///													printf("%s:\n", ROM_GETNAME(rom));
/*TODO*///
/*TODO*///													hash_data_print(ROM_GETHASHDATA(rom), functions, temp);
/*TODO*///													printf("  %-8s: %s\n", drivers[i]->name, temp);
/*TODO*///
/*TODO*///													hash_data_print(ROM_GETHASHDATA(rom1), functions, temp);
/*TODO*///													printf("  %-8s: %s\n", drivers[j]->name, temp);
/*TODO*///												}
/*TODO*///												else
/*TODO*///													match = 1;
/*TODO*///											}
/*TODO*///										}
/*TODO*///									}
/*TODO*///
/*TODO*///									if (match == 0)
/*TODO*///									{
/*TODO*///										for (region1 = rom_first_region(drivers[j]); region1; region1 = rom_next_region(region1))
/*TODO*///										{
/*TODO*///											for (rom1 = rom_first_file(region1); rom1; rom1 = rom_next_file(rom1))
/*TODO*///											{
/*TODO*///												if (strcmp(ROM_GETNAME(rom), ROM_GETNAME(rom1)) &&
/*TODO*///													hash_data_is_equal(ROM_GETHASHDATA(rom), ROM_GETHASHDATA(rom1), 0))
/*TODO*///												{
/*TODO*///													char temp[512];
/*TODO*///
/*TODO*///													/* Print only the checksums available for both the roms */
/*TODO*///													unsigned int functions =
/*TODO*///														hash_data_used_functions(ROM_GETHASHDATA(rom)) &
/*TODO*///														hash_data_used_functions(ROM_GETHASHDATA(rom1));
/*TODO*///
/*TODO*///													hash_data_print(ROM_GETHASHDATA(rom), functions, temp);
/*TODO*///													printf("%s\n", temp);
/*TODO*///													printf("  %-12s %-8s\n", ROM_GETNAME(rom), drivers[i]->name);
/*TODO*///													printf("  %-12s %-8s\n", ROM_GETNAME(rom1),drivers[j]->name);
/*TODO*///												}
/*TODO*///											}
/*TODO*///										}
/*TODO*///									}
/*TODO*///								}
/*TODO*///							}
/*TODO*///						}
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///		case LIST_ROMSIZE: /* I used this for statistical analysis */
/*TODO*///			for (i = 0; drivers[i]; i++)
/*TODO*///			{
/*TODO*///				if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
/*TODO*///				{
/*TODO*///					const struct RomModule *region, *rom, *chunk;
/*TODO*///					int romtotal = 0,romcpu = 0,romgfx = 0,romsound = 0;
/*TODO*///
/*TODO*///					for (region = rom_first_region(drivers[i]); region; region = rom_next_region(region))
/*TODO*///					{
/*TODO*///						int type = ROMREGION_GETTYPE(region);
/*TODO*///
/*TODO*///						for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
/*TODO*///						{
/*TODO*///							for (chunk = rom_first_chunk(rom); chunk; chunk = rom_next_chunk(chunk))
/*TODO*///							{
/*TODO*///								romtotal += ROM_GETLENGTH(chunk);
/*TODO*///								if (type >= REGION_CPU1 && type <= REGION_CPU8) romcpu += ROM_GETLENGTH(chunk);
/*TODO*///								if (type >= REGION_GFX1 && type <= REGION_GFX8) romgfx += ROM_GETLENGTH(chunk);
/*TODO*///								if (type >= REGION_SOUND1 && type <= REGION_SOUND8) romsound += ROM_GETLENGTH(chunk);
/*TODO*///							}
/*TODO*///						}
/*TODO*///					}
/*TODO*///
/*TODO*/////					printf("%-8s\t%-5s\t%u\t%u\t%u\t%u\n",drivers[i]->name,drivers[i]->year,romtotal,romcpu,romgfx,romsound);
/*TODO*///					printf("%-8s\t%-5s\t%u\n",drivers[i]->name,drivers[i]->year,romtotal);
/*TODO*///				}
/*TODO*///			}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///		case LIST_ROMDISTRIBUTION: /* I used this for statistical analysis */
/*TODO*///			{
/*TODO*///				int year;
/*TODO*///
/*TODO*///				for (year = 1975;year <= 2000;year++)
/*TODO*///				{
/*TODO*///					int gamestotal = 0,romcpu = 0,romgfx = 0,romsound = 0;
/*TODO*///
/*TODO*///					for (i = 0; drivers[i]; i++)
/*TODO*///					{
/*TODO*///						if (atoi(drivers[i]->year) == year)
/*TODO*///						{
/*TODO*///							if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
/*TODO*///							{
/*TODO*///								const struct RomModule *region, *rom, *chunk;
/*TODO*///
/*TODO*///								gamestotal++;
/*TODO*///
/*TODO*///								for (region = rom_first_region(drivers[i]); region; region = rom_next_region(region))
/*TODO*///								{
/*TODO*///									int type = ROMREGION_GETTYPE(region);
/*TODO*///
/*TODO*///									for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
/*TODO*///									{
/*TODO*///										for (chunk = rom_first_chunk(rom); chunk; chunk = rom_next_chunk(chunk))
/*TODO*///										{
/*TODO*///											if (type >= REGION_CPU1 && type <= REGION_CPU8) romcpu += ROM_GETLENGTH(chunk);
/*TODO*///											if (type >= REGION_GFX1 && type <= REGION_GFX8) romgfx += ROM_GETLENGTH(chunk);
/*TODO*///											if (type >= REGION_SOUND1 && type <= REGION_SOUND8) romsound += ROM_GETLENGTH(chunk);
/*TODO*///										}
/*TODO*///									}
/*TODO*///								}
/*TODO*///							}
/*TODO*///						}
/*TODO*///					}
/*TODO*///
/*TODO*///					printf("%-5d\t%u\t%u\t%u\t%u\n",year,gamestotal,romcpu,romgfx,romsound);
/*TODO*///				}
/*TODO*///			}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///		case LIST_ROMNUMBER: /* I used this for statistical analysis */
/*TODO*///			{
/*TODO*///				#define MAXCOUNT 100
/*TODO*///
/*TODO*///				int numcount[MAXCOUNT];
/*TODO*///
/*TODO*///				for (i = 0;i < MAXCOUNT;i++) numcount[i] = 0;
/*TODO*///
/*TODO*///				for (i = 0; drivers[i]; i++)
/*TODO*///				{
/*TODO*///					if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
/*TODO*///					{
/*TODO*///						const struct RomModule *region, *rom;
/*TODO*///						int romnum = 0;
/*TODO*///
/*TODO*///						for (region = rom_first_region(drivers[i]); region; region = rom_next_region(region))
/*TODO*///						{
/*TODO*///							for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
/*TODO*///							{
/*TODO*///								romnum++;
/*TODO*///							}
/*TODO*///						}
/*TODO*///
/*TODO*///						if (romnum)
/*TODO*///						{
/*TODO*///							if (romnum > MAXCOUNT) romnum = MAXCOUNT;
/*TODO*///							numcount[romnum-1]++;
/*TODO*///						}
/*TODO*///					}
/*TODO*///				}
/*TODO*///
/*TODO*///				for (i = 0;i < MAXCOUNT;i++)
/*TODO*///					printf("%d\t%d\n",i+1,numcount[i]);
/*TODO*///
/*TODO*///				#undef MAXCOUNT
/*TODO*///			}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///		case LIST_PALETTESIZE: /* I used this for statistical analysis */
/*TODO*///			for (i = 0; drivers[i]; i++)
/*TODO*///				if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
/*TODO*///				{
/*TODO*///					expand_machine_driver(drivers[i]->drv, &drv);
/*TODO*///					printf("%-8s\t%-5s\t%u\n",drivers[i]->name,drivers[i]->year,drv.total_colors);
/*TODO*///				}
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///		case LIST_CPU: /* I used this for statistical analysis */
/*TODO*///			{
/*TODO*///				int type;
/*TODO*///
/*TODO*///				for (type = 1;type < CPU_COUNT;type++)
/*TODO*///				{
/*TODO*///					int count_main = 0,count_slave = 0;
/*TODO*///
/*TODO*///					i = 0;
/*TODO*///					while (drivers[i])
/*TODO*///					{
/*TODO*///						if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
/*TODO*///						{
/*TODO*///							struct InternalMachineDriver x_driver;
/*TODO*///							const struct MachineCPU *x_cpu;
/*TODO*///
/*TODO*///							expand_machine_driver(drivers[i]->drv, &x_driver);
/*TODO*///							x_cpu = x_driver.cpu;
/*TODO*///
/*TODO*///							for (j = 0;j < MAX_CPU;j++)
/*TODO*///							{
/*TODO*///								if (x_cpu[j].cpu_type == type)
/*TODO*///								{
/*TODO*///									if (j == 0) count_main++;
/*TODO*///									else count_slave++;
/*TODO*///									break;
/*TODO*///								}
/*TODO*///							}
/*TODO*///						}
/*TODO*///
/*TODO*///						i++;
/*TODO*///					}
/*TODO*///
/*TODO*///					printf("%s\t%d\n",cputype_name(type),count_main+count_slave);
/*TODO*/////					printf("%s\t%d\t%d\n",cputype_name(type),count_main,count_slave);
/*TODO*///				}
/*TODO*///			}
/*TODO*///
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///
/*TODO*///		case LIST_CPUCLASS: /* I used this for statistical analysis */
/*TODO*///			{
/*TODO*///				int year;
/*TODO*///
/*TODO*/////				for (j = 1;j < CPU_COUNT;j++)
/*TODO*/////					printf("\t%s",cputype_name(j));
/*TODO*///				for (j = 0;j < 3;j++)
/*TODO*///					printf("\t%d",8<<j);
/*TODO*///				printf("\n");
/*TODO*///
/*TODO*///				for (year = YEAR_BEGIN;year <= YEAR_END;year++)
/*TODO*///				{
/*TODO*///					int count[CPU_COUNT];
/*TODO*///					int count_buswidth[3];
/*TODO*///
/*TODO*///					for (j = 0;j < CPU_COUNT;j++)
/*TODO*///						count[j] = 0;
/*TODO*///					for (j = 0;j < 3;j++)
/*TODO*///						count_buswidth[j] = 0;
/*TODO*///
/*TODO*///					i = 0;
/*TODO*///					while (drivers[i])
/*TODO*///					{
/*TODO*///						if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
/*TODO*///						{
/*TODO*///							struct InternalMachineDriver x_driver;
/*TODO*///							const struct MachineCPU *x_cpu;
/*TODO*///
/*TODO*///							expand_machine_driver(drivers[i]->drv, &x_driver);
/*TODO*///							x_cpu = x_driver.cpu;
/*TODO*///
/*TODO*///							if (atoi(drivers[i]->year) == year)
/*TODO*///							{
/*TODO*/////								for (j = 0;j < MAX_CPU;j++)
/*TODO*///j = 0;	// count only the main cpu
/*TODO*///								{
/*TODO*///									count[x_cpu[j].cpu_type]++;
/*TODO*///									switch(cputype_databus_width(x_cpu[j].cpu_type))
/*TODO*///									{
/*TODO*///										case  8: count_buswidth[0]++; break;
/*TODO*///										case 16: count_buswidth[1]++; break;
/*TODO*///										case 32: count_buswidth[2]++; break;
/*TODO*///									}
/*TODO*///								}
/*TODO*///							}
/*TODO*///						}
/*TODO*///
/*TODO*///						i++;
/*TODO*///					}
/*TODO*///
/*TODO*///					printf("%d",year);
/*TODO*/////					for (j = 1;j < CPU_COUNT;j++)
/*TODO*/////						printf("\t%d",count[j]);
/*TODO*///					for (j = 0;j < 3;j++)
/*TODO*///						printf("\t%d",count_buswidth[j]);
/*TODO*///					printf("\n");
/*TODO*///				}
/*TODO*///			}
/*TODO*///
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///
/*TODO*///		case LIST_NOSOUND: /* I used this for statistical analysis */
/*TODO*///			{
/*TODO*///				int year;
/*TODO*///
/*TODO*///				for (year = 1975;year <= 2000;year++)
/*TODO*///				{
/*TODO*///					int games=0,nosound=0;
/*TODO*///
/*TODO*///					i = 0;
/*TODO*///					while (drivers[i])
/*TODO*///					{
/*TODO*///						if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
/*TODO*///						{
/*TODO*///							if (atoi(drivers[i]->year) == year)
/*TODO*///							{
/*TODO*///								games++;
/*TODO*///								if (drivers[i]->flags & GAME_NO_SOUND) nosound++;
/*TODO*///							}
/*TODO*///						}
/*TODO*///
/*TODO*///						i++;
/*TODO*///					}
/*TODO*///
/*TODO*///					printf("%d\t%d\t%d\n",year,nosound,games);
/*TODO*///				}
/*TODO*///			}
/*TODO*///
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///
/*TODO*///		case LIST_SOUND: /* I used this for statistical analysis */
/*TODO*///			{
/*TODO*///				int type;
/*TODO*///
/*TODO*///				for (type = 1;type < SOUND_COUNT;type++)
/*TODO*///				{
/*TODO*///					int count = 0,minyear = 3000,maxyear = 0;
/*TODO*///
/*TODO*///					i = 0;
/*TODO*///					while (drivers[i])
/*TODO*///					{
/*TODO*///						if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
/*TODO*///						{
/*TODO*///							struct InternalMachineDriver x_driver;
/*TODO*///							const struct MachineSound *x_sound;
/*TODO*///
/*TODO*///							expand_machine_driver(drivers[i]->drv, &x_driver);
/*TODO*///							x_sound = x_driver.sound;
/*TODO*///
/*TODO*///							for (j = 0;j < MAX_SOUND;j++)
/*TODO*///							{
/*TODO*///								if (x_sound[j].sound_type == type)
/*TODO*///								{
/*TODO*///									int year = atoi(drivers[i]->year);
/*TODO*///
/*TODO*///									count++;
/*TODO*///
/*TODO*///									if (year > 1900)
/*TODO*///									{
/*TODO*///										if (year > maxyear) maxyear = year;
/*TODO*///										if (year < minyear) minyear = year;
/*TODO*///									}
/*TODO*///								}
/*TODO*///							}
/*TODO*///						}
/*TODO*///
/*TODO*///						i++;
/*TODO*///					}
/*TODO*///
/*TODO*///					if (count)
/*TODO*/////						printf("%s (%d-%d)\t%d\n",soundtype_name(type),minyear,maxyear,count);
/*TODO*///						printf("%s\t%d\n",soundtype_name(type),count);
/*TODO*///				}
/*TODO*///			}
/*TODO*///
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///
/*TODO*///		case LIST_NVRAM: /* I used this for statistical analysis */
/*TODO*///			{
/*TODO*///				int year;
/*TODO*///
/*TODO*///				for (year = 1975;year <= 2000;year++)
/*TODO*///				{
/*TODO*///					int games=0,nvram=0;
/*TODO*///
/*TODO*///					i = 0;
/*TODO*///					while (drivers[i])
/*TODO*///					{
/*TODO*///						if (drivers[i]->clone_of == 0 || (drivers[i]->clone_of->flags & NOT_A_DRIVER))
/*TODO*///						{
/*TODO*///							struct InternalMachineDriver x_driver;
/*TODO*///
/*TODO*///							expand_machine_driver(drivers[i]->drv, &x_driver);
/*TODO*///
/*TODO*///							if (atoi(drivers[i]->year) == year)
/*TODO*///							{
/*TODO*///								games++;
/*TODO*///								if (x_driver.nvram_handler) nvram++;
/*TODO*///							}
/*TODO*///						}
/*TODO*///
/*TODO*///						i++;
/*TODO*///					}
/*TODO*///
/*TODO*///					printf("%d\t%d\t%d\n",year,nvram,games);
/*TODO*///				}
/*TODO*///			}
/*TODO*///
/*TODO*///			return 0;
/*TODO*///			break;
/*TODO*///
/*TODO*///
/*TODO*///		case LIST_INFO: /* list all info */
/*TODO*///			print_mame_info( stdout, drivers );
/*TODO*///			return 0;
/*TODO*///
/*TODO*///		case LIST_XML: /* list all info */
/*TODO*///			print_mame_xml( stdout, drivers );
/*TODO*///			return 0;
        }
        /*TODO*///
/*TODO*///	if (verify)  /* "verify" utilities */
/*TODO*///	{
/*TODO*///		int err = 0;
/*TODO*///		int correct = 0;
/*TODO*///		int incorrect = 0;
/*TODO*///		int res = 0;
/*TODO*///		int total = 0;
/*TODO*///		int checked = 0;
/*TODO*///		int notfound = 0;
/*TODO*///
/*TODO*///
/*TODO*///		for (i = 0; drivers[i]; i++)
/*TODO*///		{
/*TODO*///			if (!strwildcmp(gamename, drivers[i]->name))
/*TODO*///				total++;
/*TODO*///		}
/*TODO*///
/*TODO*///		for (i = 0; drivers[i]; i++)
/*TODO*///		{
/*TODO*///			if (strwildcmp(gamename, drivers[i]->name))
/*TODO*///				continue;
/*TODO*///
/*TODO*///			/* set rom and sample path correctly */
/*TODO*/////			get_rom_sample_path (argc, argv, i, NULL);
/*TODO*///
/*TODO*///			if (verify & VERIFY_ROMS)
/*TODO*///			{
/*TODO*///				res = VerifyRomSet (i,(verify & VERIFY_TERSE) ? terse_printf : (verify_printf_proc)printf);
/*TODO*///
/*TODO*///				if (res == CLONE_NOTFOUND || res == NOTFOUND)
/*TODO*///				{
/*TODO*///					notfound++;
/*TODO*///					goto nextloop;
/*TODO*///				}
/*TODO*///
/*TODO*///				if (res == INCORRECT || res == BEST_AVAILABLE || (verify & VERIFY_VERBOSE))
/*TODO*///				{
/*TODO*///					printf ("romset %s ", drivers[i]->name);
/*TODO*///					if (drivers[i]->clone_of && !(drivers[i]->clone_of->flags & NOT_A_DRIVER))
/*TODO*///						printf ("[%s] ", drivers[i]->clone_of->name);
/*TODO*///				}
/*TODO*///			}
/*TODO*///			if (verify & VERIFY_SAMPLES)
/*TODO*///			{
/*TODO*///				const char **samplenames = NULL;
/*TODO*///				expand_machine_driver(drivers[i]->drv, &drv);
/*TODO*///#if (HAS_SAMPLES || HAS_VLM5030)
/*TODO*/// 				for( j = 0; drv.sound[j].sound_type && j < MAX_SOUND; j++ )
/*TODO*///				{
/*TODO*///#if (HAS_SAMPLES)
/*TODO*/// 					if( drv.sound[j].sound_type == SOUND_SAMPLES )
/*TODO*/// 						samplenames = ((struct Samplesinterface *)drv.sound[j].sound_interface)->samplenames;
/*TODO*///#endif
/*TODO*///				}
/*TODO*///#endif
/*TODO*///				/* ignore games that need no samples */
/*TODO*///				if (samplenames == 0 || samplenames[0] == 0)
/*TODO*///					goto nextloop;
/*TODO*///
/*TODO*///				res = VerifySampleSet (i,(verify_printf_proc)printf);
/*TODO*///				if (res == NOTFOUND)
/*TODO*///				{
/*TODO*///					notfound++;
/*TODO*///					goto nextloop;
/*TODO*///				}
/*TODO*///				printf ("sampleset %s ", drivers[i]->name);
/*TODO*///			}
/*TODO*///
/*TODO*///			if (res == NOTFOUND)
/*TODO*///			{
/*TODO*///				printf ("oops, should never come along here\n");
/*TODO*///			}
/*TODO*///			else if (res == INCORRECT)
/*TODO*///			{
/*TODO*///				printf ("is bad\n");
/*TODO*///				incorrect++;
/*TODO*///			}
/*TODO*///			else if (res == CORRECT)
/*TODO*///			{
/*TODO*///				if (verify & VERIFY_VERBOSE)
/*TODO*///					printf ("is good\n");
/*TODO*///				correct++;
/*TODO*///			}
/*TODO*///			else if (res == BEST_AVAILABLE)
/*TODO*///			{
/*TODO*///				printf ("is best available\n");
/*TODO*///				correct++;
/*TODO*///			}
/*TODO*///			if (res)
/*TODO*///				err = res;
/*TODO*///
/*TODO*///nextloop:
/*TODO*///			checked++;
/*TODO*///			fprintf(stderr,"%d%%\r",100 * checked / total);
/*TODO*///		}
/*TODO*///
/*TODO*///		if (correct+incorrect == 0)
/*TODO*///		{
/*TODO*///			printf ("%s ", (verify & VERIFY_ROMS) ? "romset" : "sampleset" );
/*TODO*///			if (notfound > 0)
/*TODO*///				printf("\"%8s\" not found!\n",gamename);
/*TODO*///			else
/*TODO*///				printf("\"%8s\" not supported!\n",gamename);
/*TODO*///			return 1;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			printf("%d %s found, %d were OK.\n", correct+incorrect,
/*TODO*///					(verify & VERIFY_ROMS)? "romsets" : "samplesets", correct);
/*TODO*///			if (incorrect > 0)
/*TODO*///				return 2;
/*TODO*///			else
/*TODO*///				return 0;
/*TODO*///		}
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///	if (ident)
/*TODO*///	{
/*TODO*///		if (ident == 2) silentident = 1;
/*TODO*///		else silentident = 0;
/*TODO*///
/*TODO*///		knownstatus = KNOWN_START;
/*TODO*///		romident(gamename,1);
/*TODO*///		if (ident == 2)
/*TODO*///		{
/*TODO*///			switch (knownstatus)
/*TODO*///			{
/*TODO*///				case KNOWN_START: printf("ERROR     %s\n",gamename); break;
/*TODO*///				case KNOWN_ALL:   printf("KNOWN     %s\n",gamename); break;
/*TODO*///				case KNOWN_NONE:  printf("UNKNOWN   %s\n",gamename); break;
/*TODO*///				case KNOWN_SOME:  printf("PARTKNOWN %s\n",gamename); break;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///
        /* FIXME: horrible hack to tell that no frontend option was used */

        return 1234;
    }
}
