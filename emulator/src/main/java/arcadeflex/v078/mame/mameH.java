/*
 * ported to v0.78
 * using automatic conversion tool v0.01
 */
package arcadeflex.v078.mame;

//mame imports
import static arcadeflex.v078.mame.driverH.*;

public class mameH {

    /*TODO*///	/***************************************************************************
/*TODO*///	
/*TODO*///		Parameters
/*TODO*///	
/*TODO*///	***************************************************************************/
/*TODO*///	
/*TODO*///	#define MAX_GFX_ELEMENTS 32
/*TODO*///	#define MAX_MEMORY_REGIONS 32
/*TODO*///	
/*TODO*///	#ifndef MESS
/*TODO*///	#define APPNAME					"MAME"
/*TODO*///	#define APPLONGNAME				"M.A.M.E."
/*TODO*///	#define GAMENOUN				"game"
/*TODO*///	#define GAMESNOUN				"games"
/*TODO*///	#else
/*TODO*///	#define APPNAME					"MESS"
/*TODO*///	#define APPLONGNAME				"M.E.S.S."
/*TODO*///	#define GAMENOUN				"system"
/*TODO*///	#define GAMESNOUN				"systems"
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/***************************************************************************
/*TODO*///	
/*TODO*///		Core description of the currently-running machine
/*TODO*///	
/*TODO*///	***************************************************************************/
/*TODO*///	
/*TODO*///	struct RegionInfo
/*TODO*///	{
/*TODO*///		UINT8 *		base;
/*TODO*///		size_t		length;
/*TODO*///		UINT32		type;
/*TODO*///		UINT32		flags;
/*TODO*///	};
    public static class RunningMachine {

        /* ----- game-related information ----- */

 /* points to the definition of the game machine */
        public GameDriver gamedrv;
        /* points to the constructed MachineDriver */
        public InternalMachineDriver drv;
        /*TODO*///		/* array of memory regions */
/*TODO*///		struct RegionInfo		memory_region[MAX_MEMORY_REGIONS];
/*TODO*///	
/*TODO*///	
/*TODO*///		/* ----- video-related information ----- */
/*TODO*///	
/*TODO*///		/* array of pointers to graphic sets (chars, sprites) */
/*TODO*///		struct GfxElement *		gfx[MAX_GFX_ELEMENTS];
/*TODO*///	
/*TODO*///		/* main bitmap to render to (but don't do it directly!) */
/*TODO*///		struct mame_bitmap *	scrbitmap;
/*TODO*///	
/*TODO*///		/* current visible area, and a prerotated one adjusted for orientation */
/*TODO*///		struct rectangle 		visible_area;
/*TODO*///		struct rectangle		absolute_visible_area;
/*TODO*///	
/*TODO*///		/* remapped palette pen numbers. When you write directly to a bitmap in a
/*TODO*///		   non-paletteized mode, use this array to look up the pen number. For example,
/*TODO*///		   if you want to use color #6 in the palette, use pens[6] instead of just 6. */
/*TODO*///		pen_t *					pens;
/*TODO*///	
/*TODO*///		/* lookup table used to map gfx pen numbers to color numbers */
/*TODO*///		UINT16 *				game_colortable;
/*TODO*///	
/*TODO*///		/* the above, already remapped through Machine->pens */
/*TODO*///		pen_t *					remapped_colortable;

        /* video color depth: 16, 15 or 32 */
        public int color_depth;
        /* video orientation; obsolete; always set to 0 */
        public int orientation;

        /* ----- audio-related information ----- */
 /* the digital audio sample rate; 0 if sound is disabled. */
        public int sample_rate;

        /*TODO*///		/* samples loaded from disk */
/*TODO*///		struct GameSamples *	samples;
/*TODO*///	
/*TODO*///	
/*TODO*///		/* ----- input-related information ----- */
/*TODO*///	
/*TODO*///		/* the input ports definition from the driver is copied here and modified */
/*TODO*///		struct InputPort *		input_ports;
/*TODO*///	
/*TODO*///		/* original input_ports without modifications */
/*TODO*///		struct InputPort *		input_ports_default;
/*TODO*///	
/*TODO*///	
/*TODO*///		/* ----- user interface-related information ----- */
/*TODO*///	
/*TODO*///		/* font used by the user interface */
/*TODO*///		struct GfxElement *		uifont;
/*TODO*///	
/*TODO*///		/* font parameters */
/*TODO*///		int 					uifontwidth, uifontheight;
/*TODO*///	
/*TODO*///		/* user interface visible area */
/*TODO*///		int 					uixmin, uiymin;
/*TODO*///		int 					uiwidth, uiheight;
/*TODO*///	
/*TODO*///		/* user interface orientation */
        public int ui_orientation;
        /*TODO*///	
/*TODO*///	
/*TODO*///		/* ----- debugger-related information ----- */
/*TODO*///	
/*TODO*///		/* bitmap where the debugger is rendered */
/*TODO*///		struct mame_bitmap *	debug_bitmap;
/*TODO*///	
/*TODO*///		/* pen array for the debugger, analagous to the pens above */
/*TODO*///		pen_t *					debug_pens;
/*TODO*///	
/*TODO*///		/* colortable mapped through the pens, as for the game */
/*TODO*///		pen_t *					debug_remapped_colortable;
/*TODO*///	
/*TODO*///		/* font used by the debugger */
/*TODO*///		struct GfxElement *		debugger_font;
    }

    /*TODO*///	/***************************************************************************
/*TODO*///	
/*TODO*///		Options passed from the frontend to the main core
/*TODO*///	
/*TODO*///	***************************************************************************/
/*TODO*///	
/*TODO*///	#define ARTWORK_USE_ALL			(~0)
/*TODO*///	#define ARTWORK_USE_NONE		(0)
/*TODO*///	#define ARTWORK_USE_BACKDROPS	0x01
/*TODO*///	#define ARTWORK_USE_OVERLAYS	0x02
/*TODO*///	#define ARTWORK_USE_BEZELS		0x04
/*TODO*///	
/*TODO*///	
/*TODO*///	#ifdef MESS
/*TODO*///	#define MAX_IMAGES	32
/*TODO*///	/*
/*TODO*///	 * This is a filename and it's associated peripheral type
/*TODO*///	 * The types are defined in mess.h (IO_...)
/*TODO*///	 */
/*TODO*///	struct ImageFile
/*TODO*///	{
/*TODO*///		const char *name;
/*TODO*///		int type;
/*TODO*///	};
/*TODO*///	#endif
    /* The host platform should fill these fields with the preferences specified in the GUI */
 /* or on the commandline. */
    public static class GameOptions {

        /*TODO*///		mame_file *	record;			/* handle to file to record input to */
/*TODO*///		mame_file *	playback;		/* handle to file to playback input from */
/*TODO*///		mame_file *	language_file;	/* handle to file for localization */
/*TODO*///	
/*TODO*///		int		mame_debug;		/* 1 to enable debugging */
        public int cheat;/* 1 to enable cheating */
        public int gui_host;/* 1 to tweak some UI-related things for better GUI integration */
        public int skip_disclaimer;/* 1 to skip the disclaimer screen at startup */
        public int skip_gameinfo;/* 1 to skip the game info screen at startup */

        public int samplerate;/* sound sample playback rate, in Hz */
        public int use_samples;/* 1 to enable external .wav samples */
        public int use_filter;/* 1 to enable FIR filter on final mixer output */

        public float brightness;/* brightness of the display */
        public float pause_bright;/* additional brightness when in pause */
        public float gamma;/* gamma correction of the display */
        public int color_depth;/* 15, 16, or 32, any other value means auto */
        public int vector_width;/* requested width for vector games; 0 means default (640) */
        public int vector_height;/* requested height for vector games; 0 means default (480) */
        public int ui_orientation;/* orientation of the UI relative to the video */

        public int beam;/* vector beam width */
        public float vector_flicker;/* vector beam flicker effect control */
        public float vector_intensity;/* vector beam intensity */
        public int translucency;/* 1 to enable translucency on vectors */
        public int antialias;/* 1 to enable antialiasing on vectors */

        public int use_artwork;/* bitfield indicating which artwork pieces to use */
        public int artwork_res;/* 1 for 1x game scaling, 2 for 2x */
        public int artwork_crop;/* 1 to crop artwork to the game screen */
 /*TODO*///	
/*TODO*///		char	savegame;		/* character representing a savegame to load */
/*TODO*///		int     crc_only;       /* specify if only CRC should be used as checksum */
/*TODO*///		char *	bios;			/* specify system bios (if used), 0 is default */
/*TODO*///	
/*TODO*///		int		debug_width;	/* requested width of debugger bitmap */
/*TODO*///		int		debug_height;	/* requested height of debugger bitmap */
/*TODO*///		int		debug_depth;	/* requested depth of debugger bitmap */
/*TODO*///	
    }
    /*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/***************************************************************************
/*TODO*///	
/*TODO*///		Display state passed to the OSD layer for rendering
/*TODO*///	
/*TODO*///	***************************************************************************/
/*TODO*///	
/*TODO*///	/* these flags are set in the mame_display struct to indicate that */
/*TODO*///	/* a particular piece of state has changed since the last call to */
/*TODO*///	/* osd_update_video_and_audio() */
/*TODO*///	#define GAME_BITMAP_CHANGED			0x00000001
/*TODO*///	#define GAME_PALETTE_CHANGED		0x00000002
/*TODO*///	#define GAME_VISIBLE_AREA_CHANGED	0x00000004
/*TODO*///	#define VECTOR_PIXELS_CHANGED		0x00000008
/*TODO*///	#define DEBUG_BITMAP_CHANGED		0x00000010
/*TODO*///	#define DEBUG_PALETTE_CHANGED		0x00000020
/*TODO*///	#define DEBUG_FOCUS_CHANGED			0x00000040
/*TODO*///	#define LED_STATE_CHANGED			0x00000080
/*TODO*///	
/*TODO*///	
/*TODO*///	/* the main mame_display structure, containing the current state of the */
/*TODO*///	/* video display */
/*TODO*///	struct mame_display
/*TODO*///	{
/*TODO*///	    /* bitfield indicating which states have changed */
/*TODO*///	    UINT32					changed_flags;
/*TODO*///	
/*TODO*///	    /* game bitmap and display information */
/*TODO*///	    struct mame_bitmap *	game_bitmap;			/* points to game's bitmap */
/*TODO*///	    struct rectangle		game_bitmap_update;		/* bounds that need to be updated */
/*TODO*///	    const rgb_t *			game_palette;			/* points to game's adjusted palette */
/*TODO*///	    UINT32					game_palette_entries;	/* number of palette entries in game's palette */
/*TODO*///	    UINT32 *				game_palette_dirty;		/* points to game's dirty palette bitfield */
/*TODO*///	    struct rectangle 		game_visible_area;		/* the game's visible area */
/*TODO*///	    void *					vector_dirty_pixels;	/* points to X,Y pairs of dirty vector pixels */
/*TODO*///	
/*TODO*///	    /* debugger bitmap and display information */
/*TODO*///	    struct mame_bitmap *	debug_bitmap;			/* points to debugger's bitmap */
/*TODO*///	    const rgb_t *			debug_palette;			/* points to debugger's palette */
/*TODO*///	    UINT32					debug_palette_entries;	/* number of palette entries in debugger's palette */
/*TODO*///	    UINT8					debug_focus;			/* set to 1 if debugger has focus */
/*TODO*///	
/*TODO*///	    /* other misc information */
/*TODO*///	    UINT8					led_state;				/* bitfield of current LED states */
/*TODO*///	};
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/***************************************************************************
/*TODO*///	
/*TODO*///		Performance data
/*TODO*///	
/*TODO*///	***************************************************************************/
/*TODO*///	
/*TODO*///	struct performance_info
/*TODO*///	{
/*TODO*///		double					game_speed_percent;		/* % of full speed */
/*TODO*///		double					frames_per_second;		/* actual rendered fps */
/*TODO*///		int						vector_updates_last_second; /* # of vector updates last second */
/*TODO*///		int						partial_updates_this_frame; /* # of partial updates last frame */
/*TODO*///	};
/*TODO*///	
/*TODO*///	
/*TODO*///	
}
