/**
 * ported to 0.78
 */
package arcadeflex.v078.mame;

//generic imports
import static arcadeflex.v078.generic.funcPtr.*;
//mame imports
import static arcadeflex.v078.mame.commonH.*;
import static arcadeflex.v078.mame.inptportH.*;

public class driverH {

    /*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Macros for declaring common callbacks
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///#define DRIVER_INIT(name)		void init_##name(void)
/*TODO*///
/*TODO*///#define INTERRUPT_GEN(func)		void func(void)
/*TODO*///
/*TODO*///#define MACHINE_INIT(name)		void machine_init_##name(void)
/*TODO*///#define MACHINE_STOP(name)		void machine_stop_##name(void)
/*TODO*///
/*TODO*///#define NVRAM_HANDLER(name)		void nvram_handler_##name(mame_file *file, int read_or_write)
/*TODO*///
/*TODO*///#define PALETTE_INIT(name)		void palette_init_##name(UINT16 *colortable, const UINT8 *color_prom)
/*TODO*///
/*TODO*///#define VIDEO_START(name)		int video_start_##name(void)
/*TODO*///#define VIDEO_STOP(name)		void video_stop_##name(void)
/*TODO*///#define VIDEO_EOF(name)			void video_eof_##name(void)
/*TODO*///#define VIDEO_UPDATE(name)		void video_update_##name(struct mame_bitmap *bitmap, const struct rectangle *cliprect)
/*TODO*///
/*TODO*////* NULL versions */
/*TODO*///#define init_NULL				NULL
/*TODO*///#define machine_init_NULL 		NULL
/*TODO*///#define nvram_handler_NULL 		NULL
/*TODO*///#define palette_init_NULL		NULL
/*TODO*///#define video_start_NULL 		NULL
/*TODO*///#define video_stop_NULL 		NULL
/*TODO*///#define video_eof_NULL 			NULL
/*TODO*///#define video_update_NULL 		NULL
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Core MAME includes
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///#include "osd_cpu.h"
/*TODO*///#include "memory.h"
/*TODO*///#include "mamedbg.h"
/*TODO*///#include "osdepend.h"
/*TODO*///#include "mame.h"
/*TODO*///#include "common.h"
/*TODO*///#include "drawgfx.h"
/*TODO*///#include "palette.h"
/*TODO*///#include "cpuintrf.h"
/*TODO*///#include "cpuexec.h"
/*TODO*///#include "cpuint.h"
/*TODO*///#include "sndintrf.h"
/*TODO*///#include "input.h"
/*TODO*///#include "inptport.h"
/*TODO*///#include "usrintrf.h"
/*TODO*///#include "cheat.h"
/*TODO*///#include "tilemap.h"
/*TODO*///#include "profiler.h"
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///#include "messdrv.h"
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifdef MAME_NET
/*TODO*///#include "network.h"
/*TODO*///#endif /* MAME_NET */
/*TODO*///
/*TODO*///#ifdef MMSND
/*TODO*///#include "mmsnd/mmsnd.h"
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Macros for building machine drivers
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////* use this to declare external references to a machine driver */
/*TODO*///#define MACHINE_DRIVER_EXTERN(game)										\
/*TODO*///	void construct_##game(struct InternalMachineDriver *machine)		\
/*TODO*///
/*TODO*///
/*TODO*////* start/end tags for the machine driver */
/*TODO*///#define MACHINE_DRIVER_START(game) 										\
/*TODO*///	void construct_##game(struct InternalMachineDriver *machine)		\
/*TODO*///	{																	\
/*TODO*///		struct MachineCPU *cpu = NULL;									\
/*TODO*///		(void)cpu;														\
/*TODO*///
/*TODO*///#define MACHINE_DRIVER_END 												\
/*TODO*///	}																	\
/*TODO*///
/*TODO*///
/*TODO*////* importing data from other machine drivers */
/*TODO*///#define MDRV_IMPORT_FROM(game) 											\
/*TODO*///	construct_##game(machine); 											\
/*TODO*///
/*TODO*///
/*TODO*////* add/modify/remove/replace CPUs */
/*TODO*///#define MDRV_CPU_ADD_TAG(tag, type, clock)								\
/*TODO*///	cpu = machine_add_cpu(machine, (tag), CPU_##type, (clock));			\
/*TODO*///
/*TODO*///#define MDRV_CPU_ADD(type, clock)										\
/*TODO*///	MDRV_CPU_ADD_TAG(NULL, type, clock)									\
/*TODO*///
/*TODO*///#define MDRV_CPU_MODIFY(tag)											\
/*TODO*///	cpu = machine_find_cpu(machine, tag);								\
/*TODO*///
/*TODO*///#define MDRV_CPU_REMOVE(tag)											\
/*TODO*///	machine_remove_cpu(machine, tag);									\
/*TODO*///	cpu = NULL;															\
/*TODO*///
/*TODO*///#define MDRV_CPU_REPLACE(tag, type, clock)								\
/*TODO*///	cpu = machine_find_cpu(machine, tag);								\
/*TODO*///	if (cpu)															\
/*TODO*///	{																	\
/*TODO*///		cpu->cpu_type = (CPU_##type);									\
/*TODO*///		cpu->cpu_clock = (clock);										\
/*TODO*///	}																	\
/*TODO*///
/*TODO*///
/*TODO*////* CPU parameters */
/*TODO*///#define MDRV_CPU_FLAGS(flags)											\
/*TODO*///	if (cpu)															\
/*TODO*///		cpu->cpu_flags = (flags);										\
/*TODO*///
/*TODO*///#define MDRV_CPU_CONFIG(config)											\
/*TODO*///	if (cpu)															\
/*TODO*///		cpu->reset_param = &(config);									\
/*TODO*///
/*TODO*///#define MDRV_CPU_MEMORY(readmem, writemem)								\
/*TODO*///	if (cpu)															\
/*TODO*///	{																	\
/*TODO*///		cpu->memory_read = (readmem);									\
/*TODO*///		cpu->memory_write = (writemem);									\
/*TODO*///	}																	\
/*TODO*///
/*TODO*///#define MDRV_CPU_PORTS(readport, writeport)								\
/*TODO*///	if (cpu)															\
/*TODO*///	{																	\
/*TODO*///		cpu->port_read = (readport);									\
/*TODO*///		cpu->port_write = (writeport);									\
/*TODO*///	}																	\
/*TODO*///
/*TODO*///#define MDRV_CPU_VBLANK_INT(func, rate)									\
/*TODO*///	if (cpu)															\
/*TODO*///	{																	\
/*TODO*///		cpu->vblank_interrupt = func;									\
/*TODO*///		cpu->vblank_interrupts_per_frame = (rate);						\
/*TODO*///	}																	\
/*TODO*///
/*TODO*///#define MDRV_CPU_PERIODIC_INT(func, rate)								\
/*TODO*///	if (cpu)															\
/*TODO*///	{																	\
/*TODO*///		cpu->timed_interrupt = func;									\
/*TODO*///		cpu->timed_interrupts_per_second = (rate);						\
/*TODO*///	}																	\
/*TODO*///
/*TODO*///
/*TODO*////* core parameters */
/*TODO*///#define MDRV_FRAMES_PER_SECOND(rate)									\
/*TODO*///	machine->frames_per_second = (rate);								\
/*TODO*///
/*TODO*///#define MDRV_VBLANK_DURATION(duration)									\
/*TODO*///	machine->vblank_duration = (duration);								\
/*TODO*///
/*TODO*///#define MDRV_INTERLEAVE(interleave)										\
/*TODO*///	machine->cpu_slices_per_frame = (interleave);						\
/*TODO*///
/*TODO*///
/*TODO*////* core functions */
/*TODO*///#define MDRV_MACHINE_INIT(name)											\
/*TODO*///	machine->machine_init = machine_init_##name;						\
/*TODO*///
/*TODO*///#define MDRV_MACHINE_STOP(name)											\
/*TODO*///	machine->machine_stop = machine_stop_##name;						\
/*TODO*///
/*TODO*///#define MDRV_NVRAM_HANDLER(name)										\
/*TODO*///	machine->nvram_handler = nvram_handler_##name;						\
/*TODO*///
/*TODO*///
/*TODO*////* core video parameters */
/*TODO*///#define MDRV_VIDEO_ATTRIBUTES(flags)									\
/*TODO*///	machine->video_attributes = (flags);								\
/*TODO*///
/*TODO*///#define MDRV_ASPECT_RATIO(num, den)										\
/*TODO*///	machine->aspect_x = (num);											\
/*TODO*///	machine->aspect_y = (den);											\
/*TODO*///
/*TODO*///#define MDRV_SCREEN_SIZE(width, height)									\
/*TODO*///	machine->screen_width = (width);									\
/*TODO*///	machine->screen_height = (height);									\
/*TODO*///
/*TODO*///#define MDRV_VISIBLE_AREA(minx, maxx, miny, maxy)						\
/*TODO*///	machine->default_visible_area.min_x = (minx);						\
/*TODO*///	machine->default_visible_area.max_x = (maxx);						\
/*TODO*///	machine->default_visible_area.min_y = (miny);						\
/*TODO*///	machine->default_visible_area.max_y = (maxy);						\
/*TODO*///
/*TODO*///#define MDRV_GFXDECODE(gfx)												\
/*TODO*///	machine->gfxdecodeinfo = (gfx);										\
/*TODO*///
/*TODO*///#define MDRV_PALETTE_LENGTH(length)										\
/*TODO*///	machine->total_colors = (length);									\
/*TODO*///
/*TODO*///#define MDRV_COLORTABLE_LENGTH(length)									\
/*TODO*///	machine->color_table_len = (length);								\
/*TODO*///
/*TODO*///
/*TODO*////* core video functions */
/*TODO*///#define MDRV_PALETTE_INIT(name)											\
/*TODO*///	machine->init_palette = palette_init_##name;						\
/*TODO*///
/*TODO*///#define MDRV_VIDEO_START(name)											\
/*TODO*///	machine->video_start = video_start_##name;							\
/*TODO*///
/*TODO*///#define MDRV_VIDEO_STOP(name)											\
/*TODO*///	machine->video_stop = video_stop_##name;							\
/*TODO*///
/*TODO*///#define MDRV_VIDEO_EOF(name)											\
/*TODO*///	machine->video_eof = video_eof_##name;								\
/*TODO*///
/*TODO*///#define MDRV_VIDEO_UPDATE(name)											\
/*TODO*///	machine->video_update = video_update_##name;						\
/*TODO*///
/*TODO*///
/*TODO*////* core sound parameters */
/*TODO*///#define MDRV_SOUND_ATTRIBUTES(flags)									\
/*TODO*///	machine->sound_attributes = (flags);								\
/*TODO*///
/*TODO*///
/*TODO*////* add/remove/replace sounds */
/*TODO*///#define MDRV_SOUND_ADD_TAG(tag, type, interface)						\
/*TODO*///	machine_add_sound(machine, (tag), SOUND_##type, &(interface));		\
/*TODO*///
/*TODO*///#define MDRV_SOUND_ADD(type, interface)									\
/*TODO*///	MDRV_SOUND_ADD_TAG(NULL, type, interface)							\
/*TODO*///
/*TODO*///#define MDRV_SOUND_REMOVE(tag)											\
/*TODO*///	machine_remove_sound(machine, tag);									\
/*TODO*///
/*TODO*///#define MDRV_SOUND_REPLACE(tag, type, interface)						\
/*TODO*///	{																	\
/*TODO*///		struct MachineSound *sound = machine_find_sound(machine, tag);	\
/*TODO*///		if (sound)														\
/*TODO*///		{																\
/*TODO*///			sound->sound_type = SOUND_##type;							\
/*TODO*///			sound->sound_interface = &(interface);						\
/*TODO*///		}																\
/*TODO*///	}																	\
/*TODO*///
/*TODO*///
/*TODO*///struct MachineCPU *machine_add_cpu(struct InternalMachineDriver *machine, const char *tag, int type, int cpuclock);
/*TODO*///struct MachineCPU *machine_find_cpu(struct InternalMachineDriver *machine, const char *tag);
/*TODO*///void machine_remove_cpu(struct InternalMachineDriver *machine, const char *tag);
/*TODO*///
/*TODO*///struct MachineSound *machine_add_sound(struct InternalMachineDriver *machine, const char *tag, int type, void *sndintf);
/*TODO*///struct MachineSound *machine_find_sound(struct InternalMachineDriver *machine, const char *tag);
/*TODO*///void machine_remove_sound(struct InternalMachineDriver *machine, const char *tag);
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Internal representation of a machine driver, built from the constructor
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///#define MAX_CPU 8	/* MAX_CPU is the maximum number of CPUs which cpuintrf.c */
/*TODO*///					/* can run at the same time. Currently, 8 is enough. */
/*TODO*///
/*TODO*///#define MAX_SOUND 5	/* MAX_SOUND is the maximum number of sound subsystems */
/*TODO*///					/* which can run at the same time. Currently, 5 is enough. */
/*TODO*///
    public static class InternalMachineDriver {

        /*TODO*///	struct MachineCPU cpu[MAX_CPU];
/*TODO*///	float frames_per_second;
/*TODO*///	int vblank_duration;
/*TODO*///	UINT32 cpu_slices_per_frame;
/*TODO*///
/*TODO*///	void (*machine_init)(void);
/*TODO*///	void (*machine_stop)(void);
/*TODO*///	void (*nvram_handler)(mame_file *file, int read_or_write);
/*TODO*///
        public int /*UINT32*/ video_attributes;
        /*TODO*///	UINT32 aspect_x, aspect_y;
/*TODO*///	int screen_width,screen_height;
/*TODO*///	struct rectangle default_visible_area;
/*TODO*///	struct GfxDecodeInfo *gfxdecodeinfo;
/*TODO*///	UINT32 total_colors;
/*TODO*///	UINT32 color_table_len;
/*TODO*///
/*TODO*///	void (*init_palette)(UINT16 *colortable,const UINT8 *color_prom);
/*TODO*///	int (*video_start)(void);
/*TODO*///	void (*video_stop)(void);
/*TODO*///	void (*video_eof)(void);
/*TODO*///	void (*video_update)(struct mame_bitmap *bitmap,const struct rectangle *cliprect);
/*TODO*///
/*TODO*///	UINT32 sound_attributes;
/*TODO*///	struct MachineSound sound[MAX_SOUND];
    }

    /*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Machine driver constants and flags
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////* VBlank is the period when the video beam is outside of the visible area and */
/*TODO*////* returns from the bottom to the top of the screen to prepare for a new video frame. */
/*TODO*////* VBlank duration is an important factor in how the game renders itself. MAME */
/*TODO*////* generates the vblank_interrupt, lets the game run for vblank_duration microseconds, */
/*TODO*////* and then updates the screen. This faithfully reproduces the behaviour of the real */
/*TODO*////* hardware. In many cases, the game does video related operations both in its vblank */
/*TODO*////* interrupt, and in the normal game code; it is therefore important to set up */
/*TODO*////* vblank_duration accurately to have everything properly in sync. An example of this */
/*TODO*////* is Commando: if you set vblank_duration to 0, therefore redrawing the screen BEFORE */
/*TODO*////* the vblank interrupt is executed, sprites will be misaligned when the screen scrolls. */
/*TODO*///
/*TODO*////* Here are some predefined, TOTALLY ARBITRARY values for vblank_duration, which should */
/*TODO*////* be OK for most cases. I have NO IDEA how accurate they are compared to the real */
/*TODO*////* hardware, they could be completely wrong. */
/*TODO*///#define DEFAULT_60HZ_VBLANK_DURATION 0
/*TODO*///#define DEFAULT_30HZ_VBLANK_DURATION 0
/*TODO*////* If you use IPT_VBLANK, you need a duration different from 0. */
/*TODO*///#define DEFAULT_REAL_60HZ_VBLANK_DURATION 2500
/*TODO*///#define DEFAULT_REAL_30HZ_VBLANK_DURATION 2500
/*TODO*///
/*TODO*///
/*TODO*////* ----- flags for video_attributes ----- */
/*TODO*///
/*TODO*////* bit 0 of the video attributes indicates raster or vector video hardware */
/*TODO*///#define	VIDEO_TYPE_RASTER			0x0000
/*TODO*///#define	VIDEO_TYPE_VECTOR			0x0001
/*TODO*///
/*TODO*////* bit 3 of the video attributes indicates that the game's palette has 6 or more bits */
/*TODO*////*       per gun, and would therefore require a 24-bit display. This is entirely up to */
/*TODO*////*       the OS dependant layer, the bitmap will still be 16-bit. */
/*TODO*///#define VIDEO_NEEDS_6BITS_PER_GUN	0x0008
/*TODO*///
/*TODO*////* ASG 980417 - added: */
/*TODO*////* bit 4 of the video attributes indicates that the driver wants its refresh after */
/*TODO*////*       the VBLANK instead of before. */
/*TODO*///#define	VIDEO_UPDATE_BEFORE_VBLANK	0x0000
/*TODO*///#define	VIDEO_UPDATE_AFTER_VBLANK	0x0010
/*TODO*///
/*TODO*////* In most cases we assume pixels are square (1:1 aspect ratio) but some games need */
/*TODO*////* different proportions, e.g. 1:2 for Blasteroids */
/*TODO*///#define VIDEO_PIXEL_ASPECT_RATIO_MASK 0x0060
/*TODO*///#define VIDEO_PIXEL_ASPECT_RATIO_1_1 0x0000
/*TODO*///#define VIDEO_PIXEL_ASPECT_RATIO_1_2 0x0020
/*TODO*///#define VIDEO_PIXEL_ASPECT_RATIO_2_1 0x0040
/*TODO*///
/*TODO*///#define VIDEO_DUAL_MONITOR			0x0080
/*TODO*///
/*TODO*////* Mish 181099:  See comments in vidhrdw/generic.c for details */
/*TODO*///#define VIDEO_BUFFERS_SPRITERAM		0x0100
/*TODO*///
    /* game wants to use a hicolor or truecolor bitmap (e.g. for alpha blending) */
    public static final int VIDEO_RGB_DIRECT = 0x0200;

    /*TODO*///
/*TODO*////* automatically extend the palette creating a darker copy for shadows */
/*TODO*///#define VIDEO_HAS_SHADOWS			0x0400
/*TODO*///
/*TODO*////* automatically extend the palette creating a brighter copy for highlights */
/*TODO*///#define VIDEO_HAS_HIGHLIGHTS		0x0800
/*TODO*///
/*TODO*///
/*TODO*////* ----- flags for sound_attributes ----- */
/*TODO*///#define	SOUND_SUPPORTS_STEREO		0x0001
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Game driver structure
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
    public static class GameDriver {

        //this is used instead of GAME macro
        public GameDriver(String year, String name, String source, RomLoadHandlerPtr romload, GameDriver parent, MachineHandlerPtr drv, InputPortHandlerPtr input, DriverInitHandlerPtr init, int monitor, String manufacture, String fullname) {
            this.year = year;
            this.source_file = source;
            this.clone_of = parent;
            this.name = name;
            this.description = fullname;
            this.manufacturer = manufacture;
            this.drv = drv;
            //inputports
            this.driver_init = init;
            romload.handler();//load the rom
            input.handler();//load input
            this.input_ports = input_macro;//copy input macro to input ports
            this.rom = rommodule_macro; //copy rommodule_macro to rom
            this.flags = monitor;
        }

        public String source_file;/* set this to __FILE__ */
        public GameDriver clone_of;/* if this is a clone, point to the main version of the game */
        public String name;
        /*TODO*///	const struct SystemBios *bios;	/* if this system has alternate bios roms use this structure to list names and ROM_BIOSFLAGS. */
        public String description;
        public String year;
        public String manufacturer;
        public MachineHandlerPtr drv;
        public InputPortTiny[] input_ports;
        public DriverInitHandlerPtr driver_init;/* optional function to be called during initialization This is called ONCE, unlike Machine->init_machine which is called every time the game is reset. */
        public RomModule[] rom;
        public int flags;/* orientation and other flags; see defines below */
    }

    /**
     * *************************************************************************
     *
     * Game driver flags
     *
     **************************************************************************
     */

    /* ----- values for the flags field ----- */
    public static final int ORIENTATION_MASK = 0x0007;
    public static final int ORIENTATION_FLIP_X = 0x0001;/* mirror everything in the X direction */
    public static final int ORIENTATION_FLIP_Y = 0x0002;/* mirror everything in the Y direction */
    public static final int ORIENTATION_SWAP_XY = 0x0004;/* mirror along the top-left/bottom-right diagonal */

    public static final int GAME_NOT_WORKING = 0x0008;
    public static final int GAME_UNEMULATED_PROTECTION = 0x0010;/* game's protection not fully emulated */
    public static final int GAME_WRONG_COLORS = 0x0020;/* colors are totally wrong */
    public static final int GAME_IMPERFECT_COLORS = 0x0040;/* colors are not 100% accurate, but close */
    public static final int GAME_IMPERFECT_GRAPHICS = 0x0080;/* graphics are wrong/incomplete */
    public static final int GAME_NO_COCKTAIL = 0x0100;/* screen flip support is missing */
    public static final int GAME_NO_SOUND = 0x0200;/* sound is missing */
    public static final int GAME_IMPERFECT_SOUND = 0x0400;/* sound is known to be wrong */
    public static final int NOT_A_DRIVER = 0x4000;/* set by the fake "root" driver_0 and by "containers" */

 /*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Macros for building game drivers
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///
/*TODO*///#define GAMEX(YEAR,NAME,PARENT,MACHINE,INPUT,INIT,MONITOR,COMPANY,FULLNAME,FLAGS)	\
/*TODO*///extern const struct GameDriver driver_##PARENT;	\
/*TODO*///const struct GameDriver driver_##NAME =		\
/*TODO*///{											\
/*TODO*///	__FILE__,								\
/*TODO*///	&driver_##PARENT,						\
/*TODO*///	#NAME,									\
/*TODO*///	system_bios_0,							\
/*TODO*///	FULLNAME,								\
/*TODO*///	#YEAR,									\
/*TODO*///	COMPANY,								\
/*TODO*///	construct_##MACHINE,					\
/*TODO*///	input_ports_##INPUT,					\
/*TODO*///	init_##INIT,							\
/*TODO*///	rom_##NAME,								\
/*TODO*///	(MONITOR)|(FLAGS)						\
/*TODO*///};
/*TODO*///
/*TODO*///#define GAMEB(YEAR,NAME,PARENT,BIOS,MACHINE,INPUT,INIT,MONITOR,COMPANY,FULLNAME)	\
/*TODO*///extern const struct GameDriver driver_##PARENT;	\
/*TODO*///const struct GameDriver driver_##NAME =		\
/*TODO*///{											\
/*TODO*///	__FILE__,								\
/*TODO*///	&driver_##PARENT,						\
/*TODO*///	#NAME,									\
/*TODO*///	system_bios_##BIOS,						\
/*TODO*///	FULLNAME,								\
/*TODO*///	#YEAR,									\
/*TODO*///	COMPANY,								\
/*TODO*///	construct_##MACHINE,					\
/*TODO*///	input_ports_##INPUT,					\
/*TODO*///	init_##INIT,							\
/*TODO*///	rom_##NAME,								\
/*TODO*///	MONITOR									\
/*TODO*///};
/*TODO*///
/*TODO*///#define GAMEBX(YEAR,NAME,PARENT,BIOS,MACHINE,INPUT,INIT,MONITOR,COMPANY,FULLNAME,FLAGS)	\
/*TODO*///extern const struct GameDriver driver_##PARENT;	\
/*TODO*///const struct GameDriver driver_##NAME =		\
/*TODO*///{											\
/*TODO*///	__FILE__,								\
/*TODO*///	&driver_##PARENT,						\
/*TODO*///	#NAME,									\
/*TODO*///	system_bios_##BIOS,						\
/*TODO*///	FULLNAME,								\
/*TODO*///	#YEAR,									\
/*TODO*///	COMPANY,								\
/*TODO*///	construct_##MACHINE,					\
/*TODO*///	input_ports_##INPUT,					\
/*TODO*///	init_##INIT,							\
/*TODO*///	rom_##NAME,								\
/*TODO*///	(MONITOR)|(FLAGS)						\
/*TODO*///};

    /* monitor parameters to be used with the GAME() macro */
    public static final int ROT0 = 0;
    public static final int ROT90 = (ORIENTATION_SWAP_XY | ORIENTATION_FLIP_X);/* rotate clockwise 90 degrees */
    public static final int ROT180 = (ORIENTATION_FLIP_X | ORIENTATION_FLIP_Y);/* rotate 180 degrees */
    public static final int ROT270 = (ORIENTATION_SWAP_XY | ORIENTATION_FLIP_Y);/* rotate counter-clockwise 90 degrees */

 /*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Global variables
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///extern const struct GameDriver *drivers[];
/*TODO*///extern const struct GameDriver *test_drivers[];
/*TODO*///
/*TODO*///#endif
/*TODO*///    
}
