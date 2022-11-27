/**
 * ported to 0.78
 */
package arcadeflex.v078.mame;

//generic imports
import static arcadeflex.v078.generic.funcPtr.*;
//mame imports
import static arcadeflex.v078.mame.common.*;
import static arcadeflex.v078.mame.driverH.*;
import static arcadeflex.v078.mame.mameH.*;
import static arcadeflex.v078.mame.cpuexecH.*;
//platform imports
import static arcadeflex.v078.platform.config.*;
//common imports
import static common.libc.cstdio.*;

import static arcadeflex.v078.AAdummy.driver.drivers;

public class mame {

    /*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	mame.c
/*TODO*///
/*TODO*///	Controls execution of the core MAME system.
/*TODO*///
/*TODO*///****************************************************************************
/*TODO*///
/*TODO*///	Since there has been confusion in the past over the order of
/*TODO*///	initialization and other such things, here it is, all spelled out
/*TODO*///	as of May, 2002:
/*TODO*///
/*TODO*///	main()
/*TODO*///		- does platform-specific init
/*TODO*///		- calls run_game()
/*TODO*///
/*TODO*///		run_game()
/*TODO*///			- constructs the machine driver
/*TODO*///			- calls init_game_options()
/*TODO*///
/*TODO*///			init_game_options()
/*TODO*///				- determines color depth from the options
/*TODO*///				- computes orientation from the options
/*TODO*///
/*TODO*///			- initializes the savegame system
/*TODO*///			- calls osd_init() to do platform-specific initialization
/*TODO*///			- calls init_machine()
/*TODO*///
/*TODO*///			init_machine()
/*TODO*///				- initializes the localized strings
/*TODO*///				- initializes the input system
/*TODO*///				- parses and allocates the game's input ports
/*TODO*///				- initializes the hard disk system
/*TODO*///				- loads the game's ROMs
/*TODO*///				- resets the timer system
/*TODO*///				- starts the refresh timer
/*TODO*///				- initializes the CPUs
/*TODO*///				- loads the configuration file
/*TODO*///				- initializes the memory system for the game
/*TODO*///				- calls the driver's DRIVER_INIT callback
/*TODO*///
/*TODO*///			- calls run_machine()
/*TODO*///
/*TODO*///			run_machine()
/*TODO*///				- calls vh_open()
/*TODO*///
/*TODO*///				vh_open()
/*TODO*///					- allocates the palette
/*TODO*///					- decodes the graphics
/*TODO*///					- computes vector game resolution
/*TODO*///					- sets up the artwork
/*TODO*///					- calls osd_create_display() to init the display
/*TODO*///					- allocates the scrbitmap
/*TODO*///					- sets the initial visible_area
/*TODO*///					- sets up buffered spriteram
/*TODO*///					- creates the user interface font
/*TODO*///					- creates the debugger bitmap and font
/*TODO*///					- finishes palette initialization
/*TODO*///
/*TODO*///				- initializes the tilemap system
/*TODO*///				- calls the driver's VIDEO_START callback
/*TODO*///				- starts the audio system
/*TODO*///				- disposes of regions marked as disposable
/*TODO*///				- calls run_machine_core()
/*TODO*///
/*TODO*///				run_machine_core()
/*TODO*///					- shows the copyright screen
/*TODO*///					- shows the game warnings
/*TODO*///					- initializes the user interface
/*TODO*///					- initializes the cheat system
/*TODO*///					- calls the driver's NVRAM_HANDLER
/*TODO*///
/*TODO*///	--------------( at this point, we're up and running )---------------------------
/*TODO*///
/*TODO*///					- calls the driver's NVRAM_HANDLER
/*TODO*///					- tears down the cheat system
/*TODO*///					- saves the game's configuration
/*TODO*///
/*TODO*///				- stops the audio system
/*TODO*///				- calls the driver's VIDEO_STOP callback
/*TODO*///				- tears down the tilemap system
/*TODO*///				- calls vh_close()
/*TODO*///
/*TODO*///				vh_close()
/*TODO*///					- frees the decoded graphics
/*TODO*///					- frees the fonts
/*TODO*///					- calls osd_close_display() to shut down the display
/*TODO*///					- tears down the artwork
/*TODO*///					- tears down the palette system
/*TODO*///
/*TODO*///			- calls shutdown_machine()
/*TODO*///
/*TODO*///			shutdown_machine()
/*TODO*///				- tears down the memory system
/*TODO*///				- frees all the memory regions
/*TODO*///				- tears down the hard disks
/*TODO*///				- tears down the CPU system
/*TODO*///				- releases the input ports
/*TODO*///				- tears down the input system
/*TODO*///				- tears down the localized strings
/*TODO*///				- resets the saved state system
/*TODO*///
/*TODO*///			- calls osd_exit() to do platform-specific cleanup
/*TODO*///
/*TODO*///		- exits the program
/*TODO*///
/*TODO*///***************************************************************************/
    /**
     * *************************************************************************
     *
     * Constants
     *
     **************************************************************************
     */
    public static final int FRAMES_PER_FPS_UPDATE = 12;

    /**
     * *************************************************************************
     *
     * Global variables
     *
     **************************************************************************
     */

    /*TODO*////* handy globals for other parts of the system */
/*TODO*///void *record;	/* for -record */
/*TODO*///void *playback; /* for -playback */
    static int bailing;/* set to 1 if the startup is aborted to prevent multiple error messages */

 /* the active machine */
    public static RunningMachine active_machine;
    public static RunningMachine Machine = active_machine;

    /* the active game driver */
    static GameDriver gamedrv;
    static InternalMachineDriver internal_drv;

    /* various game options filled in by the OSD */
    public static GameOptions options = new GameOptions();

    /*TODO*////* the active video display */
/*TODO*///static struct mame_display current_display;
/*TODO*///static UINT8 visible_area_changed;
/*TODO*///
/*TODO*////* video updating */
/*TODO*///static UINT8 full_refresh_pending;
/*TODO*///static int last_partial_scanline;
/*TODO*///
/*TODO*////* speed computation */
/*TODO*///static cycles_t last_fps_time;
/*TODO*///static int frames_since_last_fps;
/*TODO*///static int rendered_frames_since_last_fps;
/*TODO*///static int vfcount;
/*TODO*///static struct performance_info performance;
/*TODO*///
/*TODO*////* misc other statics */
/*TODO*///static int settingsloaded;
/*TODO*///static int leds_status;
/*TODO*///
/*TODO*////* artwork callbacks */
/*TODO*///#ifndef MESS
/*TODO*///static struct artwork_callbacks mame_artwork_callbacks =
/*TODO*///{
/*TODO*///	NULL,
/*TODO*///	artwork_load_artwork_file
/*TODO*///};
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Hard disk interface prototype
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///static struct chd_interface mame_chd_interface =
/*TODO*///{
/*TODO*///	mame_chd_open,
/*TODO*///	mame_chd_close,
/*TODO*///	mame_chd_read,
/*TODO*///	mame_chd_write,
/*TODO*///	mame_chd_length
/*TODO*///};
    /**
     * *************************************************************************
     *
     * Inline functions
     *
     **************************************************************************
     */

    /*-------------------------------------------------
	bail_and_print - set the bailing flag and
	print a message if one hasn't already been
	printed
    -------------------------------------------------*/
    public static void bail_and_print(String message) {
        if (bailing == 0) {
            bailing = 1;
            printf("%s\n", message);
        }
    }

    /**
     * *************************************************************************
     *
     * Core system management
     *
     **************************************************************************
     */

    /*-------------------------------------------------
	run_game - run the given game in a session
    -------------------------------------------------*/
    public static int run_game(int game) {
        int err = 1;
        begin_resource_tracking();

        /* initialize the driver-related variables in the Machine */
        Machine.gamedrv = gamedrv = drivers[game];
        expand_machine_driver(gamedrv.drv, internal_drv);
        Machine.drv = internal_drv;

        /* initialize the game options */
        if (init_game_options() != 0) {
            return 1;
        }

        /*TODO*///	/* if we're coming in with a savegame request, process it now */
/*TODO*///	if (options.savegame)
/*TODO*///		cpu_loadsave_schedule(LOADSAVE_LOAD, options.savegame);
/*TODO*///	else
/*TODO*///		cpu_loadsave_reset();

        /* here's the meat of it all */
        bailing = 0;

        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///
/*TODO*///	/* let the OSD layer start up first */
/*TODO*///	if (osd_init())
/*TODO*///		bail_and_print("Unable to initialize system");
/*TODO*///	else
/*TODO*///	{
/*TODO*///		begin_resource_tracking();
/*TODO*///
/*TODO*///		/* then finish setting up our local machine */
/*TODO*///		if (init_machine())
/*TODO*///			bail_and_print("Unable to initialize machine emulation");
/*TODO*///		else
/*TODO*///		{
/*TODO*///			/* then run it */
/*TODO*///			if (run_machine())
/*TODO*///				bail_and_print("Unable to start machine emulation");
/*TODO*///			else
/*TODO*///				err = 0;
/*TODO*///
/*TODO*///			/* shutdown the local machine */
/*TODO*///			shutdown_machine();
/*TODO*///		}
/*TODO*///
/*TODO*///		/* stop tracking resources and exit the OSD layer */
/*TODO*///		end_resource_tracking();
/*TODO*///		osd_exit();
/*TODO*///	}
/*TODO*///
/*TODO*///	end_resource_tracking();
/*TODO*///	return err;
    }

    /*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	init_machine - initialize the emulated machine
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int init_machine(void)
/*TODO*///{
/*TODO*///	/* load the localization file */
/*TODO*///	if (uistring_init(options.language_file) != 0)
/*TODO*///	{
/*TODO*///		logerror("uistring_init failed\n");
/*TODO*///		goto cant_load_language_file;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* initialize the input system */
/*TODO*///	if (code_init() != 0)
/*TODO*///	{
/*TODO*///		logerror("code_init failed\n");
/*TODO*///		goto cant_init_input;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* if we have inputs, process them now */
/*TODO*///	if (gamedrv->input_ports)
/*TODO*///	{
/*TODO*///		/* allocate input ports */
/*TODO*///		Machine->input_ports = input_port_allocate(gamedrv->input_ports);
/*TODO*///		if (!Machine->input_ports)
/*TODO*///		{
/*TODO*///			logerror("could not allocate Machine->input_ports\n");
/*TODO*///			goto cant_allocate_input_ports;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* allocate default input ports */
/*TODO*///		Machine->input_ports_default = input_port_allocate(gamedrv->input_ports);
/*TODO*///		if (!Machine->input_ports_default)
/*TODO*///		{
/*TODO*///			logerror("could not allocate Machine->input_ports_default\n");
/*TODO*///			goto cant_allocate_input_ports_default;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* init the hard drive interface now, before attempting to load */
/*TODO*///	chd_set_interface(&mame_chd_interface);
/*TODO*///
/*TODO*///	/* load the ROMs if we have some */
/*TODO*///	if (gamedrv->rom && rom_load(gamedrv->rom) != 0)
/*TODO*///	{
/*TODO*///		logerror("readroms failed\n");
/*TODO*///		goto cant_load_roms;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* first init the timers; some CPUs have built-in timers and will need */
/*TODO*///	/* to allocate them up front */
/*TODO*///	timer_init();
/*TODO*///	cpu_init_refresh_timer();
/*TODO*///
/*TODO*///	/* now set up all the CPUs */
/*TODO*///	cpu_init();
/*TODO*///
/*TODO*///	/* load input ports settings (keys, dip switches, and so on) */
/*TODO*///	settingsloaded = load_input_port_settings();
/*TODO*///
/*TODO*///	/* multi-session safety - set spriteram size to zero before memory map is set up */
/*TODO*///	spriteram_size = spriteram_2_size = 0;
/*TODO*///
/*TODO*///	/* initialize the memory system for this game */
/*TODO*///	if (!memory_init())
/*TODO*///	{
/*TODO*///		logerror("memory_init failed\n");
/*TODO*///		goto cant_init_memory;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* call the game driver's init function */
/*TODO*///	if (gamedrv->driver_init)
/*TODO*///		(*gamedrv->driver_init)();
/*TODO*///
/*TODO*///
/*TODO*///	return 0;
/*TODO*///
/*TODO*///cant_init_memory:
/*TODO*///cant_load_roms:
/*TODO*///	input_port_free(Machine->input_ports_default);
/*TODO*///	Machine->input_ports_default = 0;
/*TODO*///cant_allocate_input_ports_default:
/*TODO*///	input_port_free(Machine->input_ports);
/*TODO*///	Machine->input_ports = 0;
/*TODO*///cant_allocate_input_ports:
/*TODO*///	code_close();
/*TODO*///cant_init_input:
/*TODO*///cant_load_language_file:
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	run_machine - start the various subsystems
/*TODO*///	and the CPU emulation; returns non zero in
/*TODO*///	case of error
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int run_machine(void)
/*TODO*///{
/*TODO*///	int res = 1;
/*TODO*///
/*TODO*///	/* start the video hardware */
/*TODO*///	if (vh_open())
/*TODO*///		bail_and_print("Unable to start video emulation");
/*TODO*///	else
/*TODO*///	{
/*TODO*///		/* initialize tilemaps */
/*TODO*///		tilemap_init();
/*TODO*///
/*TODO*///		/* start up the driver's video */
/*TODO*///		if (Machine->drv->video_start && (*Machine->drv->video_start)())
/*TODO*///			bail_and_print("Unable to start video emulation");
/*TODO*///		else
/*TODO*///		{
/*TODO*///			/* start the audio system */
/*TODO*///			if (sound_start())
/*TODO*///				bail_and_print("Unable to start audio emulation");
/*TODO*///			else
/*TODO*///			{
/*TODO*///				int region;
/*TODO*///
/*TODO*///				/* free memory regions allocated with REGIONFLAG_DISPOSE (typically gfx roms) */
/*TODO*///				for (region = 0; region < MAX_MEMORY_REGIONS; region++)
/*TODO*///					if (Machine->memory_region[region].flags & ROMREGION_DISPOSE)
/*TODO*///					{
/*TODO*///						int i;
/*TODO*///
/*TODO*///						/* invalidate contents to avoid subtle bugs */
/*TODO*///						for (i = 0; i < memory_region_length(region); i++)
/*TODO*///							memory_region(region)[i] = rand();
/*TODO*///						free(Machine->memory_region[region].base);
/*TODO*///						Machine->memory_region[region].base = 0;
/*TODO*///					}
/*TODO*///
/*TODO*///				/* now do the core execution */
/*TODO*///				run_machine_core();
/*TODO*///				res = 0;
/*TODO*///
/*TODO*///				/* store the sound system */
/*TODO*///				sound_stop();
/*TODO*///			}
/*TODO*///
/*TODO*///			/* shut down the driver's video and kill and artwork */
/*TODO*///			if (Machine->drv->video_stop)
/*TODO*///				(*Machine->drv->video_stop)();
/*TODO*///		}
/*TODO*///
/*TODO*///		/* close down the tilemap and video systems */
/*TODO*///		tilemap_close();
/*TODO*///		vh_close();
/*TODO*///	}
/*TODO*///
/*TODO*///	return res;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	run_machine_core - core execution loop
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void run_machine_core(void)
/*TODO*///{
/*TODO*///	/* disable artwork for the start */
/*TODO*///	artwork_enable(0);
/*TODO*///
/*TODO*///	/* if we didn't find a settings file, show the disclaimer */
/*TODO*///	if (settingsloaded || options.skip_disclaimer || showcopyright(artwork_get_ui_bitmap()) == 0)
/*TODO*///	{
/*TODO*///		/* show info about incorrect behaviour (wrong colors etc.) */
/*TODO*///		if (showgamewarnings(artwork_get_ui_bitmap()) == 0)
/*TODO*///		{
/*TODO*///			/* show info about the game */
/*TODO*///			if (options.skip_gameinfo || showgameinfo(artwork_get_ui_bitmap()) == 0)
/*TODO*///			{
/*TODO*///				init_user_interface();
/*TODO*///
/*TODO*///				/* enable artwork now */
/*TODO*///				artwork_enable(1);
/*TODO*///
/*TODO*///				/* disable cheat if no roms */
/*TODO*///				if (!gamedrv->rom)
/*TODO*///					options.cheat = 0;
/*TODO*///
/*TODO*///				/* start the cheat engine */
/*TODO*///				if (options.cheat)
/*TODO*///					InitCheat();
/*TODO*///
/*TODO*///				/* load the NVRAM now */
/*TODO*///				if (Machine->drv->nvram_handler)
/*TODO*///				{
/*TODO*///					mame_file *nvram_file = mame_fopen(Machine->gamedrv->name, 0, FILETYPE_NVRAM, 0);
/*TODO*///					(*Machine->drv->nvram_handler)(nvram_file, 0);
/*TODO*///					if (nvram_file)
/*TODO*///						mame_fclose(nvram_file);
/*TODO*///				}
/*TODO*///
/*TODO*///				/* run the emulation! */
/*TODO*///				cpu_run();
/*TODO*///
/*TODO*///				/* save the NVRAM */
/*TODO*///				if (Machine->drv->nvram_handler)
/*TODO*///				{
/*TODO*///					mame_file *nvram_file = mame_fopen(Machine->gamedrv->name, 0, FILETYPE_NVRAM, 1);
/*TODO*///					if (nvram_file != NULL)
/*TODO*///					{
/*TODO*///						(*Machine->drv->nvram_handler)(nvram_file, 1);
/*TODO*///						mame_fclose(nvram_file);
/*TODO*///					}
/*TODO*///				}
/*TODO*///
/*TODO*///				/* stop the cheat engine */
/*TODO*///				if (options.cheat)
/*TODO*///					StopCheat();
/*TODO*///
/*TODO*///				/* save input ports settings */
/*TODO*///				save_input_port_settings();
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	shutdown_machine - tear down the emulated
/*TODO*///	machine
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void shutdown_machine(void)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///	/* close down any devices */
/*TODO*///	devices_exit();
/*TODO*///#endif
/*TODO*///
/*TODO*///	/* release any allocated memory */
/*TODO*///	memory_shutdown();
/*TODO*///
/*TODO*///	/* free the memory allocated for various regions */
/*TODO*///	for (i = 0; i < MAX_MEMORY_REGIONS; i++)
/*TODO*///		free_memory_region(i);
/*TODO*///
/*TODO*///	/* close all hard drives */
/*TODO*///	chd_close_all();
/*TODO*///
/*TODO*///	/* reset the CPU system */
/*TODO*///	cpu_exit();
/*TODO*///
/*TODO*///	/* free the memory allocated for input ports definition */
/*TODO*///	input_port_free(Machine->input_ports);
/*TODO*///	input_port_free(Machine->input_ports_default);
/*TODO*///
/*TODO*///	/* close down the input system */
/*TODO*///	code_close();
/*TODO*///
/*TODO*///	/* reset the saved states */
/*TODO*///	state_save_reset();
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	mame_pause - pause or resume the system
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void mame_pause(int pause)
/*TODO*///{
/*TODO*///	osd_pause(pause);
/*TODO*///	osd_sound_enable(!pause);
/*TODO*///	palette_set_global_brightness_adjust(pause ? options.pause_bright : 1.00);
/*TODO*///	schedule_full_refresh();
/*TODO*///}

    /*-------------------------------------------------
	expand_machine_driver - construct a machine
	driver from the macroized state
    -------------------------------------------------*/
    public static void expand_machine_driver(MachineHandlerPtr constructor, InternalMachineDriver output) {
        /* keeping this function allows us to pre-init the driver before constructing it */
        if (output == null) {
            output = new InternalMachineDriver();
        }
        (constructor).handler(output);
    }

    /*TODO*////*-------------------------------------------------
/*TODO*///	vh_open - start up the video system
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int vh_open(void)
/*TODO*///{
/*TODO*///	struct osd_create_params params;
/*TODO*///	struct artwork_callbacks *artcallbacks;
/*TODO*///	int bmwidth = Machine->drv->screen_width;
/*TODO*///	int bmheight = Machine->drv->screen_height;
/*TODO*///
/*TODO*///	/* first allocate the necessary palette structures */
/*TODO*///	if (palette_start())
/*TODO*///		goto cant_start_palette;
/*TODO*///
/*TODO*///	/* convert the gfx ROMs into character sets. This is done BEFORE calling the driver's */
/*TODO*///	/* palette_init() routine because it might need to check the Machine->gfx[] data */
/*TODO*///	if (Machine->drv->gfxdecodeinfo)
/*TODO*///		if (decode_graphics(Machine->drv->gfxdecodeinfo))
/*TODO*///			goto cant_decode_graphics;
/*TODO*///
/*TODO*///	/* if we're a vector game, override the screen width and height */
/*TODO*///	if (Machine->drv->video_attributes & VIDEO_TYPE_VECTOR)
/*TODO*///		scale_vectorgames(options.vector_width, options.vector_height, &bmwidth, &bmheight);
/*TODO*///
/*TODO*///	/* compute the visible area for raster games */
/*TODO*///	if (!(Machine->drv->video_attributes & VIDEO_TYPE_VECTOR))
/*TODO*///	{
/*TODO*///		params.width = Machine->drv->default_visible_area.max_x - Machine->drv->default_visible_area.min_x + 1;
/*TODO*///		params.height = Machine->drv->default_visible_area.max_y - Machine->drv->default_visible_area.min_y + 1;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		params.width = bmwidth;
/*TODO*///		params.height = bmheight;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* fill in the rest of the display parameters */
/*TODO*///	compute_aspect_ratio(Machine->drv, &params.aspect_x, &params.aspect_y);
/*TODO*///	params.depth = Machine->color_depth;
/*TODO*///	params.colors = palette_get_total_colors_with_ui();
/*TODO*///	params.fps = Machine->drv->frames_per_second;
/*TODO*///	params.video_attributes = Machine->drv->video_attributes;
/*TODO*///	params.orientation = Machine->orientation;
/*TODO*///
/*TODO*///	artcallbacks = &mame_artwork_callbacks;
/*TODO*///
/*TODO*///	/* initialize the display through the artwork (and eventually the OSD) layer */
/*TODO*///	if (artwork_create_display(&params, direct_rgb_components, artcallbacks))
/*TODO*///		goto cant_create_display;
/*TODO*///
/*TODO*///	/* the create display process may update the vector width/height, so recompute */
/*TODO*///	if (Machine->drv->video_attributes & VIDEO_TYPE_VECTOR)
/*TODO*///		scale_vectorgames(options.vector_width, options.vector_height, &bmwidth, &bmheight);
/*TODO*///
/*TODO*///	/* now allocate the screen bitmap */
/*TODO*///	Machine->scrbitmap = auto_bitmap_alloc_depth(bmwidth, bmheight, Machine->color_depth);
/*TODO*///	if (!Machine->scrbitmap)
/*TODO*///		goto cant_create_scrbitmap;
/*TODO*///
/*TODO*///	/* set the default visible area */
/*TODO*///	set_visible_area(0,1,0,1);	// make sure everything is recalculated on multiple runs
/*TODO*///	set_visible_area(
/*TODO*///			Machine->drv->default_visible_area.min_x,
/*TODO*///			Machine->drv->default_visible_area.max_x,
/*TODO*///			Machine->drv->default_visible_area.min_y,
/*TODO*///			Machine->drv->default_visible_area.max_y);
/*TODO*///
/*TODO*///	/* create spriteram buffers if necessary */
/*TODO*///	if (Machine->drv->video_attributes & VIDEO_BUFFERS_SPRITERAM)
/*TODO*///		if (init_buffered_spriteram())
/*TODO*///			goto cant_init_buffered_spriteram;
/*TODO*///
/*TODO*///	/* build our private user interface font */
/*TODO*///	/* This must be done AFTER osd_create_display() so the function knows the */
/*TODO*///	/* resolution we are running at and can pick a different font depending on it. */
/*TODO*///	/* It must be done BEFORE palette_init() because that will also initialize */
/*TODO*///	/* (through osd_allocate_colors()) the uifont colortable. */
/*TODO*///	Machine->uifont = builduifont();
/*TODO*///	if (Machine->uifont == NULL)
/*TODO*///		goto cant_build_uifont;
/*TODO*///
/*TODO*///	/* initialize the palette - must be done after osd_create_display() */
/*TODO*///	if (palette_init())
/*TODO*///		goto cant_init_palette;
/*TODO*///
/*TODO*///	/* force the first update to be full */
/*TODO*///	set_vh_global_attribute(NULL, 0);
/*TODO*///
/*TODO*///	/* reset performance data */
/*TODO*///	last_fps_time = osd_cycles();
/*TODO*///	rendered_frames_since_last_fps = frames_since_last_fps = 0;
/*TODO*///	performance.game_speed_percent = 100;
/*TODO*///	performance.frames_per_second = Machine->drv->frames_per_second;
/*TODO*///	performance.vector_updates_last_second = 0;
/*TODO*///
/*TODO*///	/* reset video statics and get out of here */
/*TODO*///	pdrawgfx_shadow_lowpri = 0;
/*TODO*///	leds_status = 0;
/*TODO*///
/*TODO*///	return 0;
/*TODO*///
/*TODO*///cant_init_palette:
/*TODO*///cant_build_uifont:
/*TODO*///cant_init_buffered_spriteram:
/*TODO*///cant_create_scrbitmap:
/*TODO*///cant_create_display:
/*TODO*///cant_decode_graphics:
/*TODO*///cant_start_palette:
/*TODO*///	vh_close();
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	vh_close - close down the video system
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void vh_close(void)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	/* free all the graphics elements */
/*TODO*///	for (i = 0; i < MAX_GFX_ELEMENTS; i++)
/*TODO*///	{
/*TODO*///		freegfx(Machine->gfx[i]);
/*TODO*///		Machine->gfx[i] = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* free the font elements */
/*TODO*///	if (Machine->uifont)
/*TODO*///	{
/*TODO*///		freegfx(Machine->uifont);
/*TODO*///		Machine->uifont = NULL;
/*TODO*///	}
/*TODO*///	if (Machine->debugger_font)
/*TODO*///	{
/*TODO*///		freegfx(Machine->debugger_font);
/*TODO*///		Machine->debugger_font = NULL;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* close down the OSD layer's display */
/*TODO*///	osd_close_display();
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	compute_aspect_ratio - determine the aspect
/*TODO*///	ratio encoded in the video attributes
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void compute_aspect_ratio(const struct InternalMachineDriver *drv, int *aspect_x, int *aspect_y)
/*TODO*///{
/*TODO*///	/* if it's explicitly specified, use it */
/*TODO*///	if (drv->aspect_x && drv->aspect_y)
/*TODO*///	{
/*TODO*///		*aspect_x = drv->aspect_x;
/*TODO*///		*aspect_y = drv->aspect_y;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* otherwise, attempt to deduce the result */
/*TODO*///	else if (!(drv->video_attributes & VIDEO_DUAL_MONITOR))
/*TODO*///	{
/*TODO*///		*aspect_x = 4;
/*TODO*///		*aspect_y = (drv->video_attributes & VIDEO_DUAL_MONITOR) ? 6 : 3;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///

    /*-------------------------------------------------
	init_game_options - initialize the various
	game options
-------------------------------------------------*/
    static int init_game_options() {
        /* copy some settings into easier-to-handle variables */
 /*TODO*///	record	   = options.record;
/*TODO*///	playback   = options.playback;

        /* determine the color depth */
        Machine.color_depth = 16;
        /*TODO*///	alpha_active = 0;
        if ((Machine.drv.video_attributes & VIDEO_RGB_DIRECT) != 0) {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///		/* first pick a default */
/*TODO*///		if (Machine->drv->video_attributes & VIDEO_NEEDS_6BITS_PER_GUN)
/*TODO*///			Machine->color_depth = 32;
/*TODO*///		else
/*TODO*///			Machine->color_depth = 15;
/*TODO*///
/*TODO*///		/* now allow overrides */
/*TODO*///		if (options.color_depth == 15 || options.color_depth == 32)
/*TODO*///			Machine->color_depth = options.color_depth;
/*TODO*///
/*TODO*///		/* enable alpha for direct video modes */
/*TODO*///		alpha_active = 1;
/*TODO*///		alpha_init();
        }

        /* update the vector width/height with defaults */
        if (options.vector_width == 0) {
            options.vector_width = 640;
        }
        if (options.vector_height == 0) {
            options.vector_height = 480;
        }

        /* initialize the samplerate */
        Machine.sample_rate = options.samplerate;

        /* get orientation right */
        Machine.orientation = ROT0;
        Machine.ui_orientation = options.ui_orientation;

        return 0;
    }

    /*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	decode_graphics - decode the graphics
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int decode_graphics(const struct GfxDecodeInfo *gfxdecodeinfo)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	/* loop over all elements */
/*TODO*///	for (i = 0; i < MAX_GFX_ELEMENTS && gfxdecodeinfo[i].memory_region != -1; i++)
/*TODO*///	{
/*TODO*///		int region_length = 8 * memory_region_length(gfxdecodeinfo[i].memory_region);
/*TODO*///		UINT8 *region_base = memory_region(gfxdecodeinfo[i].memory_region);
/*TODO*///		struct GfxLayout glcopy;
/*TODO*///		int j;
/*TODO*///
/*TODO*///		/* make a copy of the layout */
/*TODO*///		glcopy = *gfxdecodeinfo[i].gfxlayout;
/*TODO*///
/*TODO*///		/* if the character count is a region fraction, compute the effective total */
/*TODO*///		if (IS_FRAC(glcopy.total))
/*TODO*///			glcopy.total = region_length / glcopy.charincrement * FRAC_NUM(glcopy.total) / FRAC_DEN(glcopy.total);
/*TODO*///
/*TODO*///		/* loop over all the planes, converting fractions */
/*TODO*///		for (j = 0; j < MAX_GFX_PLANES; j++)
/*TODO*///		{
/*TODO*///			int value = glcopy.planeoffset[j];
/*TODO*///			if (IS_FRAC(value))
/*TODO*///				glcopy.planeoffset[j] = FRAC_OFFSET(value) + region_length * FRAC_NUM(value) / FRAC_DEN(value);
/*TODO*///		}
/*TODO*///
/*TODO*///		/* loop over all the X/Y offsets, converting fractions */
/*TODO*///		for (j = 0; j < MAX_GFX_SIZE; j++)
/*TODO*///		{
/*TODO*///			int value = glcopy.xoffset[j];
/*TODO*///			if (IS_FRAC(value))
/*TODO*///				glcopy.xoffset[j] = FRAC_OFFSET(value) + region_length * FRAC_NUM(value) / FRAC_DEN(value);
/*TODO*///
/*TODO*///			value = glcopy.yoffset[j];
/*TODO*///			if (IS_FRAC(value))
/*TODO*///				glcopy.yoffset[j] = FRAC_OFFSET(value) + region_length * FRAC_NUM(value) / FRAC_DEN(value);
/*TODO*///		}
/*TODO*///
/*TODO*///		/* some games increment on partial tile boundaries; to handle this without reading */
/*TODO*///		/* past the end of the region, we may need to truncate the count */
/*TODO*///		/* an example is the games in metro.c */
/*TODO*///		if (glcopy.planeoffset[0] == GFX_RAW)
/*TODO*///		{
/*TODO*///			int base = gfxdecodeinfo[i].start;
/*TODO*///			int end = region_length/8;
/*TODO*///			while (glcopy.total > 0)
/*TODO*///			{
/*TODO*///				int elementbase = base + (glcopy.total - 1) * glcopy.charincrement / 8;
/*TODO*///				int lastpixelbase = elementbase + glcopy.height * glcopy.yoffset[0] / 8 - 1;
/*TODO*///				if (lastpixelbase < end)
/*TODO*///					break;
/*TODO*///				glcopy.total--;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* now decode the actual graphics */
/*TODO*///		if ((Machine->gfx[i] = decodegfx(region_base + gfxdecodeinfo[i].start, &glcopy)) == 0)
/*TODO*///		{
/*TODO*///			bailing = 1;
/*TODO*///			printf("Out of memory decoding gfx\n");
/*TODO*///			return 1;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* if we have a remapped colortable, point our local colortable to it */
/*TODO*///		if (Machine->remapped_colortable)
/*TODO*///			Machine->gfx[i]->colortable = &Machine->remapped_colortable[gfxdecodeinfo[i].color_codes_start];
/*TODO*///		Machine->gfx[i]->total_colors = gfxdecodeinfo[i].total_color_codes;
/*TODO*///	}
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	scale_vectorgames - scale the vector games
/*TODO*///	to a given resolution
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void scale_vectorgames(int gfx_width, int gfx_height, int *width, int *height)
/*TODO*///{
/*TODO*///	double x_scale, y_scale, scale;
/*TODO*///
/*TODO*///	/* compute the scale values */
/*TODO*///	x_scale = (double)gfx_width / (double)(*width);
/*TODO*///	y_scale = (double)gfx_height / (double)(*height);
/*TODO*///
/*TODO*///	/* pick the smaller scale factor */
/*TODO*///	scale = (x_scale < y_scale) ? x_scale : y_scale;
/*TODO*///
/*TODO*///	/* compute the new size */
/*TODO*///	*width = (int)((double)*width * scale);
/*TODO*///	*height = (int)((double)*height * scale);
/*TODO*///
/*TODO*///	/* round to the nearest 4 pixel value */
/*TODO*///	*width &= ~3;
/*TODO*///	*height &= ~3;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	init_buffered_spriteram - initialize the
/*TODO*///	double-buffered spriteram
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int init_buffered_spriteram(void)
/*TODO*///{
/*TODO*///	/* make sure we have a valid size */
/*TODO*///	if (spriteram_size == 0)
/*TODO*///	{
/*TODO*///		logerror("vh_open():  Video buffers spriteram but spriteram_size is 0\n");
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* allocate memory for the back buffer */
/*TODO*///	buffered_spriteram = auto_malloc(spriteram_size);
/*TODO*///	if (!buffered_spriteram)
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* register for saving it */
/*TODO*///	state_save_register_UINT8("generic_video", 0, "buffered_spriteram", buffered_spriteram, spriteram_size);
/*TODO*///
/*TODO*///	/* do the same for the secon back buffer, if present */
/*TODO*///	if (spriteram_2_size)
/*TODO*///	{
/*TODO*///		/* allocate memory */
/*TODO*///		buffered_spriteram_2 = auto_malloc(spriteram_2_size);
/*TODO*///		if (!buffered_spriteram_2)
/*TODO*///			return 1;
/*TODO*///
/*TODO*///		/* register for saving it */
/*TODO*///		state_save_register_UINT8("generic_video", 0, "buffered_spriteram_2", buffered_spriteram_2, spriteram_2_size);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* make 16-bit and 32-bit pointer variants */
/*TODO*///	buffered_spriteram16 = (data16_t *)buffered_spriteram;
/*TODO*///	buffered_spriteram32 = (data32_t *)buffered_spriteram;
/*TODO*///	buffered_spriteram16_2 = (data16_t *)buffered_spriteram_2;
/*TODO*///	buffered_spriteram32_2 = (data32_t *)buffered_spriteram_2;
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Screen rendering and management.
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	set_visible_area - adjusts the visible portion
/*TODO*///	of the bitmap area dynamically
/*TODO*///-------------------------------------------------*/
/*TODO*///
    public static void set_visible_area(int min_x, int max_x, int min_y, int max_y) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	if (       Machine->visible_area.min_x == min_x
/*TODO*///			&& Machine->visible_area.max_x == max_x
/*TODO*///			&& Machine->visible_area.min_y == min_y
/*TODO*///			&& Machine->visible_area.max_y == max_y)
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* "dirty" the area for the next display update */
/*TODO*///	visible_area_changed = 1;
/*TODO*///
/*TODO*///	/* set the new values in the Machine struct */
/*TODO*///	Machine->visible_area.min_x = min_x;
/*TODO*///	Machine->visible_area.max_x = max_x;
/*TODO*///	Machine->visible_area.min_y = min_y;
/*TODO*///	Machine->visible_area.max_y = max_y;
/*TODO*///
/*TODO*///	/* vector games always use the whole bitmap */
/*TODO*///	if (Machine->drv->video_attributes & VIDEO_TYPE_VECTOR)
/*TODO*///	{
/*TODO*///		Machine->absolute_visible_area.min_x = 0;
/*TODO*///		Machine->absolute_visible_area.max_x = Machine->scrbitmap->width - 1;
/*TODO*///		Machine->absolute_visible_area.min_y = 0;
/*TODO*///		Machine->absolute_visible_area.max_y = Machine->scrbitmap->height - 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* raster games need to use the visible area */
/*TODO*///	else
/*TODO*///		Machine->absolute_visible_area = Machine->visible_area;
/*TODO*///
/*TODO*///	/* recompute scanline timing */
/*TODO*///	cpu_compute_scanline_timing();
    }

    /*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	schedule_full_refresh - force a full erase
/*TODO*///	and refresh the next frame
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void schedule_full_refresh(void)
/*TODO*///{
/*TODO*///	full_refresh_pending = 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	reset_partial_updates - reset the partial
/*TODO*///	updating mechanism for a new frame
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void reset_partial_updates(void)
/*TODO*///{
/*TODO*///	last_partial_scanline = 0;
/*TODO*///	performance.partial_updates_this_frame = 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	force_partial_update - perform a partial
/*TODO*///	update from the last scanline up to and
/*TODO*///	including the specified scanline
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void force_partial_update(int scanline)
/*TODO*///{
/*TODO*///	struct rectangle clip = Machine->visible_area;
/*TODO*///
/*TODO*///	/* if skipping this frame, bail */
/*TODO*///	if (osd_skip_this_frame())
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* skip if less than the lowest so far */
/*TODO*///	if (scanline < last_partial_scanline)
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* if there's a dirty bitmap and we didn't do any partial updates yet, handle it now */
/*TODO*///	if (full_refresh_pending && last_partial_scanline == 0)
/*TODO*///	{
/*TODO*///		fillbitmap(Machine->scrbitmap, get_black_pen(), NULL);
/*TODO*///		full_refresh_pending = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* set the start/end scanlines */
/*TODO*///	if (last_partial_scanline > clip.min_y)
/*TODO*///		clip.min_y = last_partial_scanline;
/*TODO*///	if (scanline < clip.max_y)
/*TODO*///		clip.max_y = scanline;
/*TODO*///
/*TODO*///	/* render if necessary */
/*TODO*///	if (clip.min_y <= clip.max_y)
/*TODO*///	{
/*TODO*///		profiler_mark(PROFILER_VIDEO);
/*TODO*///		(*Machine->drv->video_update)(Machine->scrbitmap, &clip);
/*TODO*///		performance.partial_updates_this_frame++;
/*TODO*///		profiler_mark(PROFILER_END);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* remember where we left off */
/*TODO*///	last_partial_scanline = scanline + 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	draw_screen - render the final screen bitmap
/*TODO*///	and update any artwork
/*TODO*///-------------------------------------------------*/
/*TODO*///int gbPriorityBitmapIsDirty;
/*TODO*///
/*TODO*///void draw_screen(void)
/*TODO*///{
/*TODO*///	/* finish updating the screen */
/*TODO*///	force_partial_update(Machine->visible_area.max_y);
/*TODO*///	if( gbPriorityBitmapIsDirty )
/*TODO*///	{
/*TODO*///		fillbitmap( priority_bitmap, 0x00, NULL );
/*TODO*///		gbPriorityBitmapIsDirty = 0;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	update_video_and_audio - actually call the
/*TODO*///	OSD layer to perform an update
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void update_video_and_audio(void)
/*TODO*///{
/*TODO*///	int skipped_it = osd_skip_this_frame();
/*TODO*///
/*TODO*///	/* fill in our portion of the display */
/*TODO*///	current_display.changed_flags = 0;
/*TODO*///
/*TODO*///	/* set the main game bitmap */
/*TODO*///	current_display.game_bitmap = Machine->scrbitmap;
/*TODO*///	current_display.game_bitmap_update = Machine->absolute_visible_area;
/*TODO*///	if (!skipped_it)
/*TODO*///		current_display.changed_flags |= GAME_BITMAP_CHANGED;
/*TODO*///
/*TODO*///	/* set the visible area */
/*TODO*///	current_display.game_visible_area = Machine->absolute_visible_area;
/*TODO*///	if (visible_area_changed)
/*TODO*///		current_display.changed_flags |= GAME_VISIBLE_AREA_CHANGED;
/*TODO*///
/*TODO*///	/* set the vector dirty list */
/*TODO*///	if (Machine->drv->video_attributes & VIDEO_TYPE_VECTOR)
/*TODO*///		if (!full_refresh_pending && !ui_dirty && !skipped_it)
/*TODO*///		{
/*TODO*///			current_display.vector_dirty_pixels = vector_dirty_list;
/*TODO*///			current_display.changed_flags |= VECTOR_PIXELS_CHANGED;
/*TODO*///		}
/*TODO*///	/* set the LED status */
/*TODO*///	if (leds_status != current_display.led_state)
/*TODO*///	{
/*TODO*///		current_display.led_state = leds_status;
/*TODO*///		current_display.changed_flags |= LED_STATE_CHANGED;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* update with data from other parts of the system */
/*TODO*///	palette_update_display(&current_display);
/*TODO*///
/*TODO*///	/* render */
/*TODO*///	artwork_update_video_and_audio(&current_display);
/*TODO*///
/*TODO*///	/* update FPS */
/*TODO*///	recompute_fps(skipped_it);
/*TODO*///
/*TODO*///	/* reset dirty flags */
/*TODO*///	visible_area_changed = 0;
/*TODO*///	if (ui_dirty) ui_dirty--;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	recompute_fps - recompute the frame rate
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void recompute_fps(int skipped_it)
/*TODO*///{
/*TODO*///	/* increment the frame counters */
/*TODO*///	frames_since_last_fps++;
/*TODO*///	if (!skipped_it)
/*TODO*///		rendered_frames_since_last_fps++;
/*TODO*///
/*TODO*///	/* if we didn't skip this frame, we may be able to compute a new FPS */
/*TODO*///	if (!skipped_it && frames_since_last_fps >= FRAMES_PER_FPS_UPDATE)
/*TODO*///	{
/*TODO*///		cycles_t cps = osd_cycles_per_second();
/*TODO*///		cycles_t curr = osd_cycles();
/*TODO*///		double seconds_elapsed = (double)(curr - last_fps_time) * (1.0 / (double)cps);
/*TODO*///		double frames_per_sec = (double)frames_since_last_fps / seconds_elapsed;
/*TODO*///
/*TODO*///		/* compute the performance data */
/*TODO*///		performance.game_speed_percent = 100.0 * frames_per_sec / Machine->drv->frames_per_second;
/*TODO*///		performance.frames_per_second = (double)rendered_frames_since_last_fps / seconds_elapsed;
/*TODO*///
/*TODO*///		/* reset the info */
/*TODO*///		last_fps_time = curr;
/*TODO*///		frames_since_last_fps = 0;
/*TODO*///		rendered_frames_since_last_fps = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* for vector games, compute the vector update count once/second */
/*TODO*///	vfcount++;
/*TODO*///	if (vfcount >= (int)Machine->drv->frames_per_second)
/*TODO*///	{
/*TODO*///#ifndef MESS
/*TODO*///		/* from vidhrdw/avgdvg.c */
/*TODO*///		extern int vector_updates;
/*TODO*///
/*TODO*///		performance.vector_updates_last_second = vector_updates;
/*TODO*///		vector_updates = 0;
/*TODO*///#endif
/*TODO*///		vfcount -= (int)Machine->drv->frames_per_second;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	updatescreen - handle frameskipping and UI,
/*TODO*///	plus updating the screen during normal
/*TODO*///	operations
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///int updatescreen(void)
/*TODO*///{
/*TODO*///	/* update sound */
/*TODO*///	sound_update();
/*TODO*///
/*TODO*///	/* if we're not skipping this frame, draw the screen */
/*TODO*///	if (osd_skip_this_frame() == 0)
/*TODO*///	{
/*TODO*///		profiler_mark(PROFILER_VIDEO);
/*TODO*///		draw_screen();
/*TODO*///		profiler_mark(PROFILER_END);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* the user interface must be called between vh_update() and osd_update_video_and_audio(), */
/*TODO*///	/* to allow it to overlay things on the game display. We must call it even */
/*TODO*///	/* if the frame is skipped, to keep a consistent timing. */
/*TODO*///	if (handle_user_interface(artwork_get_ui_bitmap()))
/*TODO*///		/* quit if the user asked to */
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* blit to the screen */
/*TODO*///	update_video_and_audio();
/*TODO*///
/*TODO*///	/* call the end-of-frame callback */
/*TODO*///	if (Machine->drv->video_eof)
/*TODO*///	{
/*TODO*///		profiler_mark(PROFILER_VIDEO);
/*TODO*///		(*Machine->drv->video_eof)();
/*TODO*///		profiler_mark(PROFILER_END);
/*TODO*///	}
/*TODO*///
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Miscellaneous bits & pieces
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	mame_highscore_enabled - return 1 if high
/*TODO*///	scores are enabled
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///int mame_highscore_enabled(void)
/*TODO*///{
/*TODO*///	/* disable high score when record/playback is on */
/*TODO*///	if (record != 0 || playback != 0)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* disable high score when cheats are used */
/*TODO*///	if (he_did_cheat != 0)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* disable high score when playing network game */
/*TODO*///	/* (this forces all networked machines to start from the same state!) */
/*TODO*///#ifdef MAME_NET
/*TODO*///	if (net_active())
/*TODO*///		return 0;
/*TODO*///#elif defined XMAME_NET
/*TODO*///	if (osd_net_active())
/*TODO*///		return 0;
/*TODO*///#endif
/*TODO*///
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	set_led_status - set the state of a given LED
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void set_led_status(int num, int on)
/*TODO*///{
/*TODO*///	if (on)
/*TODO*///		leds_status |=	(1 << num);
/*TODO*///	else
/*TODO*///		leds_status &= ~(1 << num);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	mame_get_performance_info - return performance
/*TODO*///	info
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///const struct performance_info *mame_get_performance_info(void)
/*TODO*///{
/*TODO*///	return &performance;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	mame_find_cpu_index - return the index of the
/*TODO*///	given CPU, or -1 if not found
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///int mame_find_cpu_index(const char *tag)
/*TODO*///{
/*TODO*///	int cpunum;
/*TODO*///
/*TODO*///	for (cpunum = 0; cpunum < MAX_CPU; cpunum++)
/*TODO*///		if (Machine->drv->cpu[cpunum].tag && strcmp(Machine->drv->cpu[cpunum].tag, tag) == 0)
/*TODO*///			return cpunum;
/*TODO*///
/*TODO*///	return -1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*-------------------------------------------------
 	machine_add_cpu - add a CPU during machine
 	driver expansion
    -------------------------------------------------*/
    public static MachineCPU machine_add_cpu(InternalMachineDriver machine, String tag, int type, int cpuclock) {
        int cpunum;

        for (cpunum = 0; cpunum < MAX_CPU; cpunum++) {
            if (machine.cpu[cpunum].cpu_type == 0) {
                machine.cpu[cpunum].tag = tag;
                machine.cpu[cpunum].cpu_type = type;
                machine.cpu[cpunum].cpu_clock = cpuclock;
                return machine.cpu[cpunum];
            }
        }

        logerror("Out of CPU's!\n");
        return null;
    }
    /*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	machine_find_cpu - find a tagged CPU during
/*TODO*///	machine driver expansion
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///struct MachineCPU *machine_find_cpu(struct InternalMachineDriver *machine, const char *tag)
/*TODO*///{
/*TODO*///	int cpunum;
/*TODO*///
/*TODO*///	for (cpunum = 0; cpunum < MAX_CPU; cpunum++)
/*TODO*///		if (machine->cpu[cpunum].tag && strcmp(machine->cpu[cpunum].tag, tag) == 0)
/*TODO*///			return &machine->cpu[cpunum];
/*TODO*///
/*TODO*///	logerror("Can't find CPU '%s'!\n", tag);
/*TODO*///	return NULL;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	machine_remove_cpu - remove a tagged CPU
/*TODO*///	during machine driver expansion
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void machine_remove_cpu(struct InternalMachineDriver *machine, const char *tag)
/*TODO*///{
/*TODO*///	int cpunum;
/*TODO*///
/*TODO*///	for (cpunum = 0; cpunum < MAX_CPU; cpunum++)
/*TODO*///		if (machine->cpu[cpunum].tag && strcmp(machine->cpu[cpunum].tag, tag) == 0)
/*TODO*///		{
/*TODO*///			memmove(&machine->cpu[cpunum], &machine->cpu[cpunum + 1], sizeof(machine->cpu[0]) * (MAX_CPU - cpunum - 1));
/*TODO*///			memset(&machine->cpu[MAX_CPU - 1], 0, sizeof(machine->cpu[0]));
/*TODO*///			return;
/*TODO*///		}
/*TODO*///
/*TODO*///	logerror("Can't find CPU '%s'!\n", tag);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	machine_add_sound - add a sound system during
/*TODO*///	machine driver expansion
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///struct MachineSound *machine_add_sound(struct InternalMachineDriver *machine, const char *tag, int type, void *sndintf)
/*TODO*///{
/*TODO*///	int soundnum;
/*TODO*///
/*TODO*///	for (soundnum = 0; soundnum < MAX_SOUND; soundnum++)
/*TODO*///		if (machine->sound[soundnum].sound_type == 0)
/*TODO*///		{
/*TODO*///			machine->sound[soundnum].tag = tag;
/*TODO*///			machine->sound[soundnum].sound_type = type;
/*TODO*///			machine->sound[soundnum].sound_interface = sndintf;
/*TODO*///			return &machine->sound[soundnum];
/*TODO*///		}
/*TODO*///
/*TODO*///	logerror("Out of sounds!\n");
/*TODO*///	return NULL;
/*TODO*///
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	machine_find_sound - find a tagged sound
/*TODO*///	system during machine driver expansion
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///struct MachineSound *machine_find_sound(struct InternalMachineDriver *machine, const char *tag)
/*TODO*///{
/*TODO*///	int soundnum;
/*TODO*///
/*TODO*///	for (soundnum = 0; soundnum < MAX_SOUND; soundnum++)
/*TODO*///		if (machine->sound[soundnum].tag && strcmp(machine->sound[soundnum].tag, tag) == 0)
/*TODO*///			return &machine->sound[soundnum];
/*TODO*///
/*TODO*///	logerror("Can't find sound '%s'!\n", tag);
/*TODO*///	return NULL;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	machine_remove_sound - remove a tagged sound
/*TODO*///	system during machine driver expansion
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void machine_remove_sound(struct InternalMachineDriver *machine, const char *tag)
/*TODO*///{
/*TODO*///	int soundnum;
/*TODO*///
/*TODO*///	for (soundnum = 0; soundnum < MAX_SOUND; soundnum++)
/*TODO*///		if (machine->sound[soundnum].tag && strcmp(machine->sound[soundnum].tag, tag) == 0)
/*TODO*///		{
/*TODO*///			memmove(&machine->sound[soundnum], &machine->sound[soundnum + 1], sizeof(machine->sound[0]) * (MAX_SOUND - soundnum - 1));
/*TODO*///			memset(&machine->sound[MAX_SOUND - 1], 0, sizeof(machine->sound[0]));
/*TODO*///			return;
/*TODO*///		}
/*TODO*///
/*TODO*///	logerror("Can't find sound '%s'!\n", tag);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	mame_chd_open - interface for opening
/*TODO*///	a hard disk image
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///struct chd_interface_file *mame_chd_open(const char *filename, const char *mode)
/*TODO*///{
/*TODO*///	/* look for read-only drives first in the ROM path */
/*TODO*///	if (mode[0] == 'r' && !strchr(mode, '+'))
/*TODO*///	{
/*TODO*///		const struct GameDriver *drv;
/*TODO*///
/*TODO*///		/* attempt reading up the chain through the parents */
/*TODO*///		for (drv = Machine->gamedrv; drv != NULL; drv = drv->clone_of)
/*TODO*///		{
/*TODO*///			void* file = mame_fopen(drv->name, filename, FILETYPE_IMAGE, 0);
/*TODO*///
/*TODO*///			if (file != NULL)
/*TODO*///				return file;
/*TODO*///		}
/*TODO*///
/*TODO*///		return NULL;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* look for read/write drives in the diff area */
/*TODO*///	return (struct chd_interface_file *)mame_fopen(NULL, filename, FILETYPE_IMAGE_DIFF, 1);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	mame_chd_close - interface for closing
/*TODO*///	a hard disk image
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void mame_chd_close(struct chd_interface_file *file)
/*TODO*///{
/*TODO*///	mame_fclose((mame_file *)file);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	mame_chd_read - interface for reading
/*TODO*///	from a hard disk image
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///UINT32 mame_chd_read(struct chd_interface_file *file, UINT64 offset, UINT32 count, void *buffer)
/*TODO*///{
/*TODO*///	mame_fseek((mame_file *)file, offset, SEEK_SET);
/*TODO*///	return mame_fread((mame_file *)file, buffer, count);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	mame_chd_write - interface for writing
/*TODO*///	to a hard disk image
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///UINT32 mame_chd_write(struct chd_interface_file *file, UINT64 offset, UINT32 count, const void *buffer)
/*TODO*///{
/*TODO*///	mame_fseek((mame_file *)file, offset, SEEK_SET);
/*TODO*///	return mame_fwrite((mame_file *)file, buffer, count);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	mame_chd_length - interface for getting
/*TODO*///	the length a hard disk image
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///UINT64 mame_chd_length(struct chd_interface_file *file)
/*TODO*///{
/*TODO*///	return mame_fsize((mame_file *)file);
/*TODO*///}
/*TODO*///
}
