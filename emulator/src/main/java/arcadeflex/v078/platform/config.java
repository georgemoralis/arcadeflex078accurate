/**
 * ported to 0.78
 */
package arcadeflex.v078.platform;

//platform imports
import static arcadeflex.v078.platform.fronthlp.*;
import static arcadeflex.v078.platform.rc.*;
import static arcadeflex.v078.platform.rcH.*;
//common imports
import static common.libc.cstdio.*;
import static common.libc.cstring.*;

//just for testing!!!! remove it
import static arcadeflex.v078.AAdummy.driver.*;
import static arcadeflex.v078.mame.mame.options;

public class config {

    /*TODO*///static FILE *logfile;
/*TODO*///static int maxlogsize;
/*TODO*///static int curlogsize;
/*TODO*///static int errorlog;
/*TODO*///static int erroroslog;
    static int showconfig;
    /*TODO*///static int showusage;
/*TODO*///static int readconfig;
/*TODO*///static int createconfig;
/*TODO*///extern int verbose;
/*TODO*///
    static rc_struct rc;
    /*TODO*///
/*TODO*////* fix me - need to have the core call osd_set_mastervolume with this value */
/*TODO*////* instead of relying on the name of an osd variable */
/*TODO*///extern int attenuation;
/*TODO*///
/*TODO*///static char *debugres;
/*TODO*///static char *playbackname;
/*TODO*///static char *recordname;
    static String gamename;
    static String rompath_extra;
    /*TODO*///
/*TODO*///static float f_beam;
/*TODO*///static float f_flicker;
/*TODO*///static float f_intensity;
/*TODO*///
/*TODO*///static int enable_sound = 1;
/*TODO*///
/*TODO*///static int use_artwork = 1;
/*TODO*///static int use_backdrops = -1;
/*TODO*///static int use_overlays = -1;
/*TODO*///static int use_bezels = -1;
/*TODO*///
/*TODO*///static int video_norotate = 0;
/*TODO*///static int video_flipy = 0;
/*TODO*///static int video_flipx = 0;
/*TODO*///static int video_ror = 0;
/*TODO*///static int video_rol = 0;
/*TODO*///static int video_autoror = 0;
/*TODO*///static int video_autorol = 0;
/*TODO*///
/*TODO*///static char *win_basename(char *filename);
/*TODO*///static char *win_dirname(char *filename);
/*TODO*///static char *win_strip_extension(char *filename);
/*TODO*///
/*TODO*///
/*TODO*///static int video_set_beam(struct rc_option *option, const char *arg, int priority)
/*TODO*///{
/*TODO*///	options.beam = (int)(f_beam * 0x00010000);
/*TODO*///	if (options.beam < 0x00010000)
/*TODO*///		options.beam = 0x00010000;
/*TODO*///	if (options.beam > 0x00100000)
/*TODO*///		options.beam = 0x00100000;
/*TODO*///	option->priority = priority;
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///static int video_set_flicker(struct rc_option *option, const char *arg, int priority)
/*TODO*///{
/*TODO*///	options.vector_flicker = (int)(f_flicker * 2.55);
/*TODO*///	if (options.vector_flicker < 0)
/*TODO*///		options.vector_flicker = 0;
/*TODO*///	if (options.vector_flicker > 255)
/*TODO*///		options.vector_flicker = 255;
/*TODO*///	option->priority = priority;
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///static int video_set_intensity(struct rc_option *option, const char *arg, int priority)
/*TODO*///{
/*TODO*///	options.vector_intensity = f_intensity;
/*TODO*///	option->priority = priority;
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///static int video_set_debugres(struct rc_option *option, const char *arg, int priority)
/*TODO*///{
/*TODO*///	if (!strcmp(arg, "auto"))
/*TODO*///	{
/*TODO*///		options.debug_width = options.debug_height = 0;
/*TODO*///	}
/*TODO*///	else if(sscanf(arg, "%dx%d", &options.debug_width, &options.debug_height) != 2)
/*TODO*///	{
/*TODO*///		options.debug_width = options.debug_height = 0;
/*TODO*///		fprintf(stderr, "error: invalid value for debugres: %s\n", arg);
/*TODO*///		return -1;
/*TODO*///	}
/*TODO*///	option->priority = priority;
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///static int init_errorlog(struct rc_option *option, const char *arg, int priority)
/*TODO*///{
/*TODO*///	/* provide errorlog from here on */
/*TODO*///	if (errorlog && !logfile)
/*TODO*///	{
/*TODO*///		logfile = fopen("error.log","wa");
/*TODO*///		curlogsize = 0;
/*TODO*///		if (!logfile)
/*TODO*///		{
/*TODO*///			perror("unable to open log file\n");
/*TODO*///			exit (1);
/*TODO*///		}
/*TODO*///	}
/*TODO*///	option->priority = priority;
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///

    static RcAssignFuncHandlerPtr assign_skip_disclaimer = new RcAssignFuncHandlerPtr() {
        @Override
        public void handler(int value) {
            options.skip_disclaimer = value;
        }
    };
    static RcAssignFuncHandlerPtr assign_showconfig = new RcAssignFuncHandlerPtr() {
        @Override
        public void handler(int value) {
            showconfig = value;
        }
    };
    static RcAssignFuncHandlerPtr assign_samplerate = new RcAssignFuncHandlerPtr() {
        @Override
        public void handler(int value) {
            options.samplerate = value;
        }
    };
    /* struct definitions */
    static rc_option opts[] = {
        /* name, shortname, type, dest, deflt, min, max, func, help */
        new rc_option(null, null, rc_link, frontend_opts, null, 0, 0, null, null),
        /*TODO*///	{ NULL, NULL, rc_link, fileio_opts, NULL, 0, 0, NULL, NULL },
        /*TODO*///	{ NULL, NULL, rc_link, video_opts, NULL, 0,	0, NULL, NULL },
        /*TODO*///	{ NULL, NULL, rc_link, sound_opts, NULL, 0,	0, NULL, NULL },
        /*TODO*///	{ NULL, NULL, rc_link, input_opts, NULL, 0,	0, NULL, NULL },
        /*TODO*///	/* options supported by the mame core */
        /*TODO*///	/* video */
        new rc_option("Mame CORE video options", null, rc_seperator, null, null, 0, 0, null, null),
        /*TODO*///	{ "norotate", NULL, rc_bool, &video_norotate, "0", 0, 0, NULL, "do not apply rotation" },
        /*TODO*///	{ "ror", NULL, rc_bool, &video_ror, "0", 0, 0, NULL, "rotate screen clockwise" },
        /*TODO*///	{ "rol", NULL, rc_bool, &video_rol, "0", 0, 0, NULL, "rotate screen anti-clockwise" },
        /*TODO*///	{ "autoror", NULL, rc_bool, &video_autoror, "0", 0, 0, NULL, "automatically rotate screen clockwise for vertical " GAMESNOUN },
        /*TODO*///	{ "autorol", NULL, rc_bool, &video_autorol, "0", 0, 0, NULL, "automatically rotate screen anti-clockwise for vertical " GAMESNOUN },
        /*TODO*///	{ "flipx", NULL, rc_bool, &video_flipx, "0", 0, 0, NULL, "flip screen upside-down" },
        /*TODO*///	{ "flipy", NULL, rc_bool, &video_flipy, "0", 0, 0, NULL, "flip screen left-right" },
        /*TODO*///	{ "debug_resolution", "dr", rc_string, &debugres, "auto", 0, 0, video_set_debugres, "set resolution for debugger window" },
        /*TODO*///	{ "gamma", NULL, rc_float, &options.gamma, "1.0", 0.5, 2.0, NULL, "gamma correction"},
        /*TODO*///	{ "brightness", "bright", rc_float, &options.brightness, "1.0", 0.5, 2.0, NULL, "brightness correction"},
        /*TODO*///	{ "pause_brightness", NULL, rc_float, &options.pause_bright, "0.65", 0.5, 2.0, NULL, "additional pause brightness"},
        /*TODO*///
        /*TODO*///	/* vector */
        new rc_option("Mame CORE vector game options", null, rc_seperator, null, null, 0, 0, null, null),
        /*TODO*///	{ "antialias", "aa", rc_bool, &options.antialias, "1", 0, 0, NULL, "draw antialiased vectors" },
        /*TODO*///	{ "translucency", "tl", rc_bool, &options.translucency, "1", 0, 0, NULL, "draw translucent vectors" },
        /*TODO*///	{ "beam", NULL, rc_float, &f_beam, "1.0", 1.0, 16.0, video_set_beam, "set beam width in vector " GAMESNOUN },
        /*TODO*///	{ "flicker", NULL, rc_float, &f_flicker, "0.0", 0.0, 100.0, video_set_flicker, "set flickering in vector " GAMESNOUN },
        /*TODO*///	{ "intensity", NULL, rc_float, &f_intensity, "1.5", 0.5, 3.0, video_set_intensity, "set intensity in vector " GAMESNOUN },
        /*TODO*///
        /*TODO*///	/* sound */
        new rc_option("Mame CORE sound options", null, rc_seperator, null, null, 0, 0, null, null),
        new rc_option("samplerate", "sr", rc_int, assign_samplerate, "44100", 5000, 50000, null, "set samplerate"),
        /*TODO*///	{ "samples", NULL, rc_bool, &options.use_samples, "1", 0, 0, NULL, "use samples" },
        /*TODO*///	{ "resamplefilter", NULL, rc_bool, &options.use_filter, "1", 0, 0, NULL, "resample if samplerate does not match" },
        /*TODO*///	{ "sound", NULL, rc_bool, &enable_sound, "1", 0, 0, NULL, "enable/disable sound and sound CPUs" },
        /*TODO*///	{ "volume", "vol", rc_int, &attenuation, "0", -32, 0, NULL, "volume (range [-32,0])" },
        /*TODO*///
        /*TODO*///	/* misc */
        new rc_option("Mame CORE misc options", null, rc_seperator, null, null, 0, 0, null, null),
        /*TODO*///	{ "artwork", "art", rc_bool, &use_artwork, "1", 0, 0, NULL, "use additional game artwork (sets default for specific options below)" },
        /*TODO*///	{ "use_backdrops", "backdrop", rc_bool, &use_backdrops, "1", 0, 0, NULL, "use backdrop artwork" },
        /*TODO*///	{ "use_overlays", "overlay", rc_bool, &use_overlays, "1", 0, 0, NULL, "use overlay artwork" },
        /*TODO*///	{ "use_bezels", "bezel", rc_bool, &use_bezels, "1", 0, 0, NULL, "use bezel artwork" },
        /*TODO*///	{ "artwork_crop", "artcrop", rc_bool, &options.artwork_crop, "0", 0, 0, NULL, "crop artwork to game screen only" },
        /*TODO*///	{ "artwork_resolution", "artres", rc_int, &options.artwork_res, "0", 0, 0, NULL, "artwork resolution (0 for auto)" },
        /*TODO*///	{ "cheat", "c", rc_bool, &options.cheat, "0", 0, 0, NULL, "enable/disable cheat subsystem" },
        /*TODO*///	{ "debug", "d", rc_bool, &options.mame_debug, "0", 0, 0, NULL, "enable/disable debugger (only if available)" },
        /*TODO*///	{ "playback", "pb", rc_string, &playbackname, NULL, 0, 0, NULL, "playback an input file" },
        /*TODO*///	{ "record", "rec", rc_string, &recordname, NULL, 0, 0, NULL, "record an input file" },
        /*TODO*///	{ "log", NULL, rc_bool, &errorlog, "0", 0, 0, init_errorlog, "generate error.log" },
        /*TODO*///	{ "maxlogsize", NULL, rc_int, &maxlogsize, "10000", 1, 2000000, NULL, "maximum error.log size (in KB)" },
        /*TODO*///	{ "oslog", NULL, rc_bool, &erroroslog, "0", 0, 0, NULL, "output error log to debugger" },
        new rc_option("skip_disclaimer", null, rc_bool, assign_skip_disclaimer, "0", 0, 0, null, "skip displaying the disclaimer screen"),
        /*TODO*///	{ "skip_gameinfo", NULL, rc_bool, &options.skip_gameinfo, "0", 0, 0, NULL, "skip displaying the game info screen" },
        /*TODO*///	{ "crconly", NULL, rc_bool, &options.crc_only, "0", 0, 0, NULL, "use only CRC for all integrity checks" },
        /*TODO*///	{ "bios", NULL, rc_string, &options.bios, "default", 0, 14, NULL, "change system bios" },
        /*TODO*///
        /*TODO*///	/* config options */
        /*TODO*///	{ "Configuration options", NULL, rc_seperator, NULL, NULL, 0, 0, NULL, NULL },
        /*TODO*///	{ "createconfig", "cc", rc_set_int, &createconfig, NULL, 1, 0, NULL, "create the default configuration file" },
        new rc_option("showconfig", "sc", rc_set_int, assign_showconfig, null, 1, 0, null, "display running parameters in rc style"),
        /*TODO*///	{ "showusage", "su", rc_set_int, &showusage, NULL, 1, 0, NULL, "show this help" },
        /*TODO*///	{ "readconfig",	"rc", rc_bool, &readconfig, "1", 0, 0, NULL, "enable/disable loading of configfiles" },
        /*TODO*///	{ "verbose", "v", rc_bool, &verbose, "0", 0, 0, NULL, "display additional diagnostic information" },
        new rc_option(null, null, rc_end, null, null, 0, 0, null, null)
    };

    /*TODO*///
/*TODO*////*
/*TODO*/// * Penalty string compare, the result _should_ be a measure on
/*TODO*/// * how "close" two strings ressemble each other.
/*TODO*/// * The implementation is way too simple, but it sort of suits the
/*TODO*/// * purpose.
/*TODO*/// * This used to be called fuzzy matching, but there's no randomness
/*TODO*/// * involved and it is in fact a penalty method.
/*TODO*/// */
/*TODO*///
/*TODO*///int penalty_compare (const char *s, const char *l)
/*TODO*///{
/*TODO*///	int gaps = 0;
/*TODO*///	int match = 0;
/*TODO*///	int last = 1;
/*TODO*///
/*TODO*///	for (; *s && *l; l++)
/*TODO*///	{
/*TODO*///		if (*s == *l)
/*TODO*///			match = 1;
/*TODO*///		else if (*s >= 'a' && *s <= 'z' && (*s - 'a') == (*l - 'A'))
/*TODO*///			match = 1;
/*TODO*///		else if (*s >= 'A' && *s <= 'Z' && (*s - 'A') == (*l - 'a'))
/*TODO*///			match = 1;
/*TODO*///		else
/*TODO*///			match = 0;
/*TODO*///
/*TODO*///		if (match)
/*TODO*///			s++;
/*TODO*///
/*TODO*///		if (match != last)
/*TODO*///		{
/*TODO*///			last = match;
/*TODO*///			if (!match)
/*TODO*///				gaps++;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* penalty if short string does not completely fit in */
/*TODO*///	for (; *s; s++)
/*TODO*///		gaps++;
/*TODO*///
/*TODO*///	return gaps;
/*TODO*///}
/*TODO*///
/*TODO*////*
/*TODO*/// * We compare the game name given on the CLI against the long and
/*TODO*/// * the short game names supported
/*TODO*/// */
/*TODO*///void show_approx_matches(void)
/*TODO*///{
/*TODO*///	struct { int penalty; int index; } topten[10];
/*TODO*///	int i,j;
/*TODO*///	int penalty; /* best fuzz factor so far */
/*TODO*///
/*TODO*///	for (i = 0; i < 10; i++)
/*TODO*///	{
/*TODO*///		topten[i].penalty = 9999;
/*TODO*///		topten[i].index = -1;
/*TODO*///	}
/*TODO*///
/*TODO*///	for (i = 0; (drivers[i] != 0); i++)
/*TODO*///	{
/*TODO*///		int tmp;
/*TODO*///
/*TODO*///		penalty = penalty_compare (gamename, drivers[i]->description);
/*TODO*///		tmp = penalty_compare (gamename, drivers[i]->name);
/*TODO*///		if (tmp < penalty) penalty = tmp;
/*TODO*///
/*TODO*///		/* eventually insert into table of approximate matches */
/*TODO*///		for (j = 0; j < 10; j++)
/*TODO*///		{
/*TODO*///			if (penalty >= topten[j].penalty) break;
/*TODO*///			if (j > 0)
/*TODO*///			{
/*TODO*///				topten[j-1].penalty = topten[j].penalty;
/*TODO*///				topten[j-1].index = topten[j].index;
/*TODO*///			}
/*TODO*///			topten[j].index = i;
/*TODO*///			topten[j].penalty = penalty;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	for (i = 9; i >= 0; i--)
/*TODO*///	{
/*TODO*///		if (topten[i].index != -1)
/*TODO*///			fprintf (stderr, "%-10s%s\n", drivers[topten[i].index]->name, drivers[topten[i].index]->description);
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*////*
/*TODO*/// * gamedrv  = NULL --> parse named configfile
/*TODO*/// * gamedrv != NULL --> parse gamename.ini and all parent.ini's (recursively)
/*TODO*/// * return 0 --> no problem
/*TODO*/// * return 1 --> something went wrong
/*TODO*/// */
/*TODO*///int parse_config (const char* filename, const struct GameDriver *gamedrv)
/*TODO*///{
/*TODO*///	mame_file *f;
/*TODO*///	char buffer[128];
/*TODO*///	int retval = 0;
/*TODO*///
/*TODO*///	if (!readconfig) return 0;
/*TODO*///
/*TODO*///	if (gamedrv)
/*TODO*///	{
/*TODO*///		if (gamedrv->clone_of && strlen(gamedrv->clone_of->name))
/*TODO*///		{
/*TODO*///			retval = parse_config (NULL, gamedrv->clone_of);
/*TODO*///			if (retval)
/*TODO*///				return retval;
/*TODO*///		}
/*TODO*///		sprintf(buffer, "%s.ini", gamedrv->name);
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		sprintf(buffer, "%s", filename);
/*TODO*///	}
/*TODO*///
/*TODO*///	if (verbose)
/*TODO*///		fprintf(stderr, "parsing %s...", buffer);
/*TODO*///
/*TODO*///	f = mame_fopen (buffer, NULL, FILETYPE_INI, 0);
/*TODO*///	if (f)
/*TODO*///	{
/*TODO*///		if(osd_rc_read(rc, f, buffer, 1, 1))
/*TODO*///		{
/*TODO*///			if (verbose)
/*TODO*///				fprintf (stderr, "problem parsing %s\n", buffer);
/*TODO*///			retval = 1;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			if (verbose)
/*TODO*///				fprintf (stderr, "OK.\n");
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		if (verbose)
/*TODO*///			fprintf (stderr, "N/A\n");
/*TODO*///	}
/*TODO*///
/*TODO*///	if (f)
/*TODO*///		mame_fclose (f);
/*TODO*///
/*TODO*///	return retval;
/*TODO*///}
    public static rc_struct cli_rc_create() {
        rc_struct result;

        result = rc_create();
        if (result == null) {
            return null;
        }

        if (rc_register(result, opts) != 0) {
            rc_destroy(result);
            return null;
        }

        return result;
    }

    public static int cli_frontend_init(int argc, String[] argv) {
        /*TODO*///	struct InternalMachineDriver drv;
/*TODO*///	char buffer[128];
        String cmd_name;
        int game_index;
        int i;

        gamename = null;
        game_index = -1;

        /* create the rc object */
        rc = cli_rc_create();
        if (rc == null) {
            fprintf(stderr, "error on rc creation\n");
            System.exit(1);
        }

        /* parse the commandline */
        if (rc_parse_commandline(rc, argc, argv, 2, config_handle_arg) != 0) {
            fprintf(stderr, "error while parsing cmdline\n");
            System.exit(1);
        }

        /*TODO*///	/* determine global configfile name */
/*TODO*///	cmd_name = win_strip_extension(win_basename(argv[0]));
/*TODO*///	if (!cmd_name)
/*TODO*///	{
/*TODO*///		fprintf (stderr, "who am I? cannot determine the name I was called with\n");
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	sprintf (buffer, "%s.ini", cmd_name);
/*TODO*///
/*TODO*///	/* parse mame.ini/mess.ini even if called with another name */
/*TODO*///#ifdef MESS
/*TODO*///	if (strcmp(cmd_name, "mess") != 0)
/*TODO*///	{
/*TODO*///		if (parse_config ("mess.ini", NULL))
/*TODO*///			exit(1);
/*TODO*///	}
/*TODO*///#else
/*TODO*///	if (strcmp(cmd_name, "mame") != 0)
/*TODO*///	{
/*TODO*///		if (parse_config ("mame.ini", NULL))
/*TODO*///			exit(1);
/*TODO*///	}
/*TODO*///#endif
/*TODO*///
/*TODO*///	/* parse cmd_name.ini */
/*TODO*///	if (parse_config (buffer, NULL))
/*TODO*///		exit(1);
/*TODO*///
/*TODO*///#ifdef MAME_DEBUG
/*TODO*///	if (parse_config( "debug.ini", NULL))
/*TODO*///		exit(1);
/*TODO*///#endif
/*TODO*///
/*TODO*///	/* if requested, write out cmd_name.ini (normally "mame.ini") */
/*TODO*///	if (createconfig)
/*TODO*///	{
/*TODO*///		rc_save(rc, buffer, 0);
/*TODO*///		exit(0);
/*TODO*///	}
/*TODO*///
/*TODO*///	if (showconfig)
/*TODO*///	{
/*TODO*///		sprintf (buffer, " %s running parameters", cmd_name);
/*TODO*///		rc_write(rc, stdout, buffer);
/*TODO*///		exit(0);
/*TODO*///	}
/*TODO*///
/*TODO*///	if (showusage)
/*TODO*///	{
/*TODO*///		fprintf(stdout, "Usage: %s [" game "] [options]\n" "Options:\n", cmd_name);
/*TODO*///
/*TODO*///		/* actual help message */
/*TODO*///		rc_print_help(rc, stdout);
/*TODO*///		exit(0);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* no longer needed */
/*TODO*///	free(cmd_name);
/*TODO*///
/*TODO*///	/* handle playback */
/*TODO*///	if (playbackname != NULL)
/*TODO*///	{
/*TODO*///        options.playback = mame_fopen(playbackname,0,FILETYPE_INPUTLOG,0);
/*TODO*///		if (!options.playback)
/*TODO*///		{
/*TODO*///			fprintf(stderr, "failed to open %s for playback\n", playbackname);
/*TODO*///			exit(1);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* check for game name embedded in .inp header */
/*TODO*///	if (options.playback)
/*TODO*///	{
/*TODO*///		INP_HEADER inp_header;
/*TODO*///
/*TODO*///		/* read playback header */
/*TODO*///		mame_fread(options.playback, &inp_header, sizeof(INP_HEADER));
/*TODO*///
/*TODO*///		if (!isalnum(inp_header.name[0])) /* If first byte is not alpha-numeric */
/*TODO*///			mame_fseek(options.playback, 0, SEEK_SET); /* old .inp file - no header */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			for (i = 0; (drivers[i] != 0); i++) /* find game and play it */
/*TODO*///			{
/*TODO*///				if (strcmp(drivers[i]->name, inp_header.name) == 0)
/*TODO*///				{
/*TODO*///					game_index = i;
/*TODO*///					gamename = (char *)drivers[i]->name;
/*TODO*///					printf("Playing back previously recorded " game " %s (%s) [press return]\n",
/*TODO*///							drivers[game_index]->name,drivers[game_index]->description);
/*TODO*///					getchar();
/*TODO*///					break;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* check for frontend options, horrible 1234 hack */
        if (frontend_help(gamename) != 1234) {
            System.exit(0);
        }
        /*TODO*///
/*TODO*///	gamename = win_basename(gamename);
/*TODO*///	gamename = win_strip_extension(gamename);

        /* if not given by .inp file yet */
        if (game_index == -1) {
            /* do we have a driver for this? */
            for (i = 0; drivers[i] != null; i++) {
                if (stricmp(gamename, drivers[i].name) == 0) {
                    game_index = i;
                    break;
                }
            }
        }
        /*TODO*///
/*TODO*///
/*TODO*///	/* we give up. print a few approximate matches */
/*TODO*///	if (game_index == -1)
/*TODO*///	{
/*TODO*///		fprintf(stderr, "\n\"%s\" approximately matches the following\n"
/*TODO*///				"supported " GAMESNOUN " (best match first):\n\n", gamename);
/*TODO*///		show_approx_matches();
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ok, got a gamename */
/*TODO*///
/*TODO*///	/* if this is a vector game, parse vector.ini first */
/*TODO*///	expand_machine_driver(drivers[game_index]->drv, &drv);
/*TODO*///	if (drv.video_attributes & VIDEO_TYPE_VECTOR)
/*TODO*///		if (parse_config ("vector.ini", NULL))
/*TODO*///			exit(1);
/*TODO*///
/*TODO*///	/* nice hack: load source_file.ini (omit if referenced later any) */
/*TODO*///	{
/*TODO*///		const struct GameDriver *tmp_gd;
/*TODO*///
/*TODO*///		sprintf(buffer, "%s", drivers[game_index]->source_file+12);
/*TODO*///		buffer[strlen(buffer) - 2] = 0;
/*TODO*///
/*TODO*///		tmp_gd = drivers[game_index];
/*TODO*///		while (tmp_gd != NULL)
/*TODO*///		{
/*TODO*///			if (strcmp(tmp_gd->name, buffer) == 0) break;
/*TODO*///			tmp_gd = tmp_gd->clone_of;
/*TODO*///		}
/*TODO*///
/*TODO*///		if (tmp_gd == NULL)
/*TODO*///		/* not referenced later, so load it here */
/*TODO*///		{
/*TODO*///			strcat(buffer, ".ini");
/*TODO*///			if (parse_config (buffer, NULL))
/*TODO*///				exit(1);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* now load gamename.ini */
/*TODO*///	/* this possibly checks for clonename.ini recursively! */
/*TODO*///	if (parse_config (NULL, drivers[game_index]))
/*TODO*///		exit(1);
/*TODO*///
/*TODO*///	/* handle record option */
/*TODO*///	if (recordname)
/*TODO*///	{
/*TODO*///		options.record = mame_fopen(recordname,0,FILETYPE_INPUTLOG,1);
/*TODO*///		if (!options.record)
/*TODO*///		{
/*TODO*///			fprintf(stderr, "failed to open %s for recording\n", recordname);
/*TODO*///			exit(1);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	if (options.record)
/*TODO*///	{
/*TODO*///		INP_HEADER inp_header;
/*TODO*///
/*TODO*///		memset(&inp_header, '\0', sizeof(INP_HEADER));
/*TODO*///		strcpy(inp_header.name, drivers[game_index]->name);
/*TODO*///		/* MAME32 stores the MAME version numbers at bytes 9 - 11
/*TODO*///		 * MAME DOS keeps this information in a string, the
/*TODO*///		 * Windows code defines them in the Makefile.
/*TODO*///		 */
/*TODO*///		/*
/*TODO*///		   inp_header.version[0] = 0;
/*TODO*///		   inp_header.version[1] = VERSION;
/*TODO*///		   inp_header.version[2] = BETA_VERSION;
/*TODO*///		 */
/*TODO*///		mame_fwrite(options.record, &inp_header, sizeof(INP_HEADER));
/*TODO*///	}
/*TODO*///
/*TODO*///	/* need a decent default for debug width/height */
/*TODO*///	if (options.debug_width == 0)
/*TODO*///		options.debug_width = 640;
/*TODO*///	if (options.debug_height == 0)
/*TODO*///		options.debug_height = 480;
/*TODO*///	options.debug_depth = 8;
/*TODO*///
/*TODO*///	/* no sound is indicated by a 0 samplerate */
/*TODO*///	if (!enable_sound)
/*TODO*///		options.samplerate = 0;
/*TODO*///
/*TODO*///	/* set the artwork options */
/*TODO*///	options.use_artwork = ARTWORK_USE_ALL;
/*TODO*///	if (use_backdrops == 0)
/*TODO*///		options.use_artwork &= ~ARTWORK_USE_BACKDROPS;
/*TODO*///	if (use_overlays == 0)
/*TODO*///		options.use_artwork &= ~ARTWORK_USE_OVERLAYS;
/*TODO*///	if (use_bezels == 0)
/*TODO*///		options.use_artwork &= ~ARTWORK_USE_BEZELS;
/*TODO*///	if (!use_artwork)
/*TODO*///		options.use_artwork = ARTWORK_USE_NONE;
/*TODO*///
/*TODO*///{
/*TODO*///	/* first start with the game's built in orientation */
/*TODO*///	int orientation = drivers[game_index]->flags & ORIENTATION_MASK;
/*TODO*///	options.ui_orientation = orientation;
/*TODO*///
/*TODO*///	if (options.ui_orientation & ORIENTATION_SWAP_XY)
/*TODO*///	{
/*TODO*///		/* if only one of the components is inverted, switch them */
/*TODO*///		if ((options.ui_orientation & ROT180) == ORIENTATION_FLIP_X ||
/*TODO*///				(options.ui_orientation & ROT180) == ORIENTATION_FLIP_Y)
/*TODO*///			options.ui_orientation ^= ROT180;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* override if no rotation requested */
/*TODO*///	if (video_norotate)
/*TODO*///		orientation = options.ui_orientation = ROT0;
/*TODO*///
/*TODO*///	/* rotate right */
/*TODO*///	if (video_ror)
/*TODO*///	{
/*TODO*///		/* if only one of the components is inverted, switch them */
/*TODO*///		if ((orientation & ROT180) == ORIENTATION_FLIP_X ||
/*TODO*///				(orientation & ROT180) == ORIENTATION_FLIP_Y)
/*TODO*///			orientation ^= ROT180;
/*TODO*///
/*TODO*///		orientation ^= ROT90;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* rotate left */
/*TODO*///	if (video_rol)
/*TODO*///	{
/*TODO*///		/* if only one of the components is inverted, switch them */
/*TODO*///		if ((orientation & ROT180) == ORIENTATION_FLIP_X ||
/*TODO*///				(orientation & ROT180) == ORIENTATION_FLIP_Y)
/*TODO*///			orientation ^= ROT180;
/*TODO*///
/*TODO*///		orientation ^= ROT270;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* auto-rotate right (e.g. for rotating lcds), based on original orientation */
/*TODO*///	if (video_autoror && (drivers[game_index]->flags & ORIENTATION_SWAP_XY) )
/*TODO*///	{
/*TODO*///		/* if only one of the components is inverted, switch them */
/*TODO*///		if ((orientation & ROT180) == ORIENTATION_FLIP_X ||
/*TODO*///				(orientation & ROT180) == ORIENTATION_FLIP_Y)
/*TODO*///			orientation ^= ROT180;
/*TODO*///
/*TODO*///		orientation ^= ROT90;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* auto-rotate left (e.g. for rotating lcds), based on original orientation */
/*TODO*///	if (video_autorol && (drivers[game_index]->flags & ORIENTATION_SWAP_XY) )
/*TODO*///	{
/*TODO*///		/* if only one of the components is inverted, switch them */
/*TODO*///		if ((orientation & ROT180) == ORIENTATION_FLIP_X ||
/*TODO*///				(orientation & ROT180) == ORIENTATION_FLIP_Y)
/*TODO*///			orientation ^= ROT180;
/*TODO*///
/*TODO*///		orientation ^= ROT270;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* flip X/Y */
/*TODO*///	if (video_flipx)
/*TODO*///		orientation ^= ORIENTATION_FLIP_X;
/*TODO*///	if (video_flipy)
/*TODO*///		orientation ^= ORIENTATION_FLIP_Y;
/*TODO*///
/*TODO*///	blit_flipx = ((orientation & ORIENTATION_FLIP_X) != 0);
/*TODO*///	blit_flipy = ((orientation & ORIENTATION_FLIP_Y) != 0);
/*TODO*///	blit_swapxy = ((orientation & ORIENTATION_SWAP_XY) != 0);
/*TODO*///
/*TODO*///	if( options.vector_width == 0 && options.vector_height == 0 )
/*TODO*///	{
/*TODO*///		options.vector_width = 640;
/*TODO*///		options.vector_height = 480;
/*TODO*///	}
/*TODO*///	if( blit_swapxy )
/*TODO*///	{
/*TODO*///		int temp;
/*TODO*///		temp = options.vector_width;
/*TODO*///		options.vector_width = options.vector_height;
/*TODO*///		options.vector_height = temp;
/*TODO*///	}
/*TODO*///}
/*TODO*///
        return game_index;
    }

    /*TODO*///
/*TODO*///void cli_frontend_exit(void)
/*TODO*///{
/*TODO*///	/* close open files */
/*TODO*///	if (logfile) fclose(logfile);
/*TODO*///
/*TODO*///	if (options.playback) mame_fclose(options.playback);
/*TODO*///	if (options.record)   mame_fclose(options.record);
/*TODO*///	if (options.language_file) mame_fclose(options.language_file);
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///	if (win_write_config)
/*TODO*///		write_config(NULL, Machine->gamedrv);
/*TODO*///#endif /* MESS */
/*TODO*///}
/*TODO*///
    static int got_gamename = 0;
    public static ArgCallbackHandlerPtr config_handle_arg = new ArgCallbackHandlerPtr() {
        @Override
        public int handler(String arg) {


            /* notice: for MESS game means system */
            if (got_gamename != 0) {
                fprintf(stderr, "error: duplicate gamename: %s\n", arg);
                return -1;
            }

            rompath_extra = win_dirname(arg);

            if (rompath_extra != null && strlen(rompath_extra) == 0) {
                rompath_extra = null;
            }

            gamename = arg;

            if (gamename == null || strlen(gamename) == 0) {
                fprintf(stderr, "error: no gamename given in %s\n", arg);
                return -1;
            }

            got_gamename = 1;
            return 0;
        }
    };

    /*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	vlogerror
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static void vlogerror(const char *text, va_list arg)
/*TODO*///{
/*TODO*///	if (errorlog && logfile)
/*TODO*///	{
/*TODO*///		curlogsize += vfprintf(logfile, text, arg);
/*TODO*///		if (curlogsize > maxlogsize * 1024)
/*TODO*///		{
/*TODO*///			fclose(logfile);
/*TODO*///			logfile = NULL;
/*TODO*///			exit(1);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	if (erroroslog)
/*TODO*///	{
/*TODO*///		//extern int vsnprintf(char *s, size_t maxlen, const char *fmt, va_list _arg);
/*TODO*///		char buffer[2048];
/*TODO*///		_vsnprintf(buffer, sizeof(buffer) / sizeof(buffer[0]), text, arg);
/*TODO*///		OutputDebugString(buffer);
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	logerror
/*TODO*/////============================================================
/*TODO*///
    public static void logerror(String str, Object... arguments) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	va_list arg;
/*TODO*///
/*TODO*///	/* standard vfprintf stuff here */
/*TODO*///	va_start(arg, text);
/*TODO*///	vlogerror(text, arg);
/*TODO*///	va_end(arg);
    }

    /*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	osd_die
/*TODO*/////============================================================
/*TODO*///
/*TODO*///void CLIB_DECL osd_die(const char *text,...)
/*TODO*///{
/*TODO*///	va_list arg;
/*TODO*///
/*TODO*///	/* standard vfprintf stuff here */
/*TODO*///	va_start(arg, text);
/*TODO*///	vlogerror(text, arg);
/*TODO*///	va_end(arg);
/*TODO*///
/*TODO*///	exit(-1);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	win_basename
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static char *win_basename(char *filename)
/*TODO*///{
/*TODO*///	char *c;
/*TODO*///
/*TODO*///	// NULL begets NULL
/*TODO*///	if (!filename)
/*TODO*///		return NULL;
/*TODO*///
/*TODO*///	// start at the end and return when we hit a slash or colon
/*TODO*///	for (c = filename + strlen(filename) - 1; c >= filename; c--)
/*TODO*///		if (*c == '\\' || *c == '/' || *c == ':')
/*TODO*///			return c + 1;
/*TODO*///
/*TODO*///	// otherwise, return the whole thing
/*TODO*///	return filename;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	win_dirname
/*TODO*/////============================================================
/*TODO*///
    static String win_dirname(String filename) {
        String dirname;
        /*TODO*///	char *c;
/*TODO*///
        // NULL begets NULL
        if (filename == null) {
            return null;
        }

        /*TODO*///	// allocate space for it
/*TODO*///	dirname = malloc(strlen(filename) + 1);
/*TODO*///	if (!dirname)
/*TODO*///	{
/*TODO*///		fprintf(stderr, "error: malloc failed in win_dirname\n");
/*TODO*///		return NULL;
/*TODO*///	}
/*TODO*///
        // copy in the name
        dirname = filename;
        /*TODO*///
/*TODO*///	// search backward for a slash or a colon
/*TODO*///	for (c = dirname + strlen(dirname) - 1; c >= dirname; c--)
/*TODO*///		if (*c == '\\' || *c == '/' || *c == ':')
/*TODO*///		{
/*TODO*///			// found it: NULL terminate and return
/*TODO*///			*(c + 1) = 0;
/*TODO*///			return dirname;
/*TODO*///		}
/*TODO*///
/*TODO*///	// otherwise, return an empty string
/*TODO*///	dirname[0] = 0;
        return dirname;
    }
    /*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	win_strip_extension
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static char *win_strip_extension(char *filename)
/*TODO*///{
/*TODO*///	char *newname;
/*TODO*///	char *c;
/*TODO*///
/*TODO*///	// NULL begets NULL
/*TODO*///	if (!filename)
/*TODO*///		return NULL;
/*TODO*///
/*TODO*///	// allocate space for it
/*TODO*///	newname = malloc(strlen(filename) + 1);
/*TODO*///	if (!newname)
/*TODO*///	{
/*TODO*///		fprintf(stderr, "error: malloc failed in win_strip_extension\n");
/*TODO*///		return NULL;
/*TODO*///	}
/*TODO*///
/*TODO*///	// copy in the name
/*TODO*///	strcpy(newname, filename);
/*TODO*///
/*TODO*///	// search backward for a period, failing if we hit a slash or a colon
/*TODO*///	for (c = newname + strlen(newname) - 1; c >= newname; c--)
/*TODO*///	{
/*TODO*///		// if we hit a period, NULL terminate and break
/*TODO*///		if (*c == '.')
/*TODO*///		{
/*TODO*///			*c = 0;
/*TODO*///			break;
/*TODO*///		}
/*TODO*///
/*TODO*///		// if we hit a slash or colon just stop
/*TODO*///		if (*c == '\\' || *c == '/' || *c == ':')
/*TODO*///			break;
/*TODO*///	}
/*TODO*///
/*TODO*///	return newname;
/*TODO*///}
/*TODO*///
}
