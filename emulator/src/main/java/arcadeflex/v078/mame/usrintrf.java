/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

public class usrintrf {
/*TODO*////*********************************************************************
/*TODO*///
/*TODO*///	usrintrf.c
/*TODO*///
/*TODO*///	Functions used to handle MAME's user interface.
/*TODO*///
/*TODO*///*********************************************************************/
/*TODO*///
/*TODO*///#include "driver.h"
/*TODO*///#include "info.h"
/*TODO*///#include "vidhrdw/vector.h"
/*TODO*///#include "datafile.h"
/*TODO*///#include <stdarg.h>
/*TODO*///#include <math.h>
/*TODO*///#include "ui_text.h"
/*TODO*///#include "state.h"
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///  #include "mess.h"
/*TODO*///#include "mesintrf.h"
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Externals
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////* Variables for stat menu */
/*TODO*///extern char build_version[];
/*TODO*///extern unsigned int dispensed_tickets;
/*TODO*///extern unsigned int coins[COIN_COUNTERS];
/*TODO*///extern unsigned int coinlockedout[COIN_COUNTERS];
/*TODO*///
/*TODO*////* MARTINEZ.F 990207 Memory Card */
/*TODO*///#ifndef MESS
/*TODO*///#ifndef TINY_COMPILE
/*TODO*///#ifndef CPSMAME
/*TODO*///#ifndef MMSND
/*TODO*///int 		memcard_menu(struct mame_bitmap *bitmap, int);
/*TODO*///extern int	mcd_action;
/*TODO*///extern int	mcd_number;
/*TODO*///extern int	memcard_status;
/*TODO*///extern int	memcard_number;
/*TODO*///extern int	memcard_manager;
/*TODO*///extern struct GameDriver driver_neogeo;
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///
/*TODO*///#if defined(__sgi) && !defined(MESS)
/*TODO*///static int game_paused = 0; /* not zero if the game is paused */
/*TODO*///#endif
/*TODO*///
/*TODO*///extern int neogeo_memcard_load(int);
/*TODO*///extern void neogeo_memcard_save(void);
/*TODO*///extern void neogeo_memcard_eject(void);
/*TODO*///extern int neogeo_memcard_create(int);
/*TODO*////* MARTINEZ.F 990207 Memory Card End */
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Local variables
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///static struct GfxElement *uirotfont;
/*TODO*///
/*TODO*////* raw coordinates, relative to the real scrbitmap */
/*TODO*///static struct rectangle uirawbounds;
/*TODO*///static int uirawcharwidth, uirawcharheight;
/*TODO*///
/*TODO*////* rotated coordinates, easier to deal with */
/*TODO*///static struct rectangle uirotbounds;
/*TODO*///static int uirotwidth, uirotheight;
/*TODO*///int uirotcharwidth, uirotcharheight;
/*TODO*///
/*TODO*///static int setup_selected;
/*TODO*///static int osd_selected;
/*TODO*///static int jukebox_selected;
/*TODO*///static int single_step;
/*TODO*///
/*TODO*///static int showfps;
/*TODO*///static int showfpstemp;
/*TODO*///
/*TODO*///static int show_profiler;
/*TODO*///
/*TODO*///UINT8 ui_dirty;
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Font data
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///static const UINT8 uifontdata[] =
/*TODO*///{
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,	/* [ 0- 1] */
/*TODO*///	0x7c,0x80,0x98,0x90,0x80,0xbc,0x80,0x7c,0xf8,0x04,0x64,0x44,0x04,0xf4,0x04,0xf8,	/* [ 2- 3] tape pos 1 */
/*TODO*///	0x7c,0x80,0x98,0x88,0x80,0xbc,0x80,0x7c,0xf8,0x04,0x64,0x24,0x04,0xf4,0x04,0xf8,	/* [ 4- 5] tape pos 2 */
/*TODO*///	0x7c,0x80,0x88,0x98,0x80,0xbc,0x80,0x7c,0xf8,0x04,0x24,0x64,0x04,0xf4,0x04,0xf8,	/* [ 6- 7] tape pos 3 */
/*TODO*///	0x7c,0x80,0x90,0x98,0x80,0xbc,0x80,0x7c,0xf8,0x04,0x44,0x64,0x04,0xf4,0x04,0xf8,	/* [ 8- 9] tape pos 3 */
/*TODO*///	0x30,0x48,0x84,0xb4,0xb4,0x84,0x48,0x30,0x30,0x48,0x84,0x84,0x84,0x84,0x48,0x30,	/* [10-11] */
/*TODO*///	0x00,0xfc,0x84,0x8c,0xd4,0xa4,0xfc,0x00,0x00,0xfc,0x84,0x84,0x84,0x84,0xfc,0x00,	/* [12-13] */
/*TODO*///	0x00,0x38,0x7c,0x7c,0x7c,0x38,0x00,0x00,0x00,0x30,0x68,0x78,0x78,0x30,0x00,0x00,	/* [14-15] circle & bullet */
/*TODO*///	0x80,0xc0,0xe0,0xf0,0xe0,0xc0,0x80,0x00,0x04,0x0c,0x1c,0x3c,0x1c,0x0c,0x04,0x00,	/* [16-17] R/L triangle */
/*TODO*///	0x20,0x70,0xf8,0x20,0x20,0xf8,0x70,0x20,0x48,0x48,0x48,0x48,0x48,0x00,0x48,0x00,
/*TODO*///	0x00,0x00,0x30,0x68,0x78,0x30,0x00,0x00,0x00,0x30,0x68,0x78,0x78,0x30,0x00,0x00,
/*TODO*///	0x70,0xd8,0xe8,0xe8,0xf8,0xf8,0x70,0x00,0x1c,0x7c,0x74,0x44,0x44,0x4c,0xcc,0xc0,
/*TODO*///	0x20,0x70,0xf8,0x70,0x70,0x70,0x70,0x00,0x70,0x70,0x70,0x70,0xf8,0x70,0x20,0x00,
/*TODO*///	0x00,0x10,0xf8,0xfc,0xf8,0x10,0x00,0x00,0x00,0x20,0x7c,0xfc,0x7c,0x20,0x00,0x00,
/*TODO*///	0xb0,0x54,0xb8,0xb8,0x54,0xb0,0x00,0x00,0x00,0x28,0x6c,0xfc,0x6c,0x28,0x00,0x00,
/*TODO*///	0x00,0x30,0x30,0x78,0x78,0xfc,0x00,0x00,0xfc,0x78,0x78,0x30,0x30,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x20,0x20,0x20,0x20,0x20,0x00,0x20,0x00,
/*TODO*///	0x50,0x50,0x50,0x00,0x00,0x00,0x00,0x00,0x00,0x50,0xf8,0x50,0xf8,0x50,0x00,0x00,
/*TODO*///	0x20,0x70,0xc0,0x70,0x18,0xf0,0x20,0x00,0x40,0xa4,0x48,0x10,0x20,0x48,0x94,0x08,
/*TODO*///	0x60,0x90,0xa0,0x40,0xa8,0x90,0x68,0x00,0x10,0x20,0x40,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x20,0x40,0x40,0x40,0x40,0x40,0x20,0x00,0x10,0x08,0x08,0x08,0x08,0x08,0x10,0x00,
/*TODO*///	0x20,0xa8,0x70,0xf8,0x70,0xa8,0x20,0x00,0x00,0x20,0x20,0xf8,0x20,0x20,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x30,0x30,0x60,0x00,0x00,0x00,0xf8,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x30,0x30,0x00,0x00,0x08,0x10,0x20,0x40,0x80,0x00,0x00,
/*TODO*///	0x70,0x88,0x88,0x88,0x88,0x88,0x70,0x00,0x10,0x30,0x10,0x10,0x10,0x10,0x10,0x00,
/*TODO*///	0x70,0x88,0x08,0x10,0x20,0x40,0xf8,0x00,0x70,0x88,0x08,0x30,0x08,0x88,0x70,0x00,
/*TODO*///	0x10,0x30,0x50,0x90,0xf8,0x10,0x10,0x00,0xf8,0x80,0xf0,0x08,0x08,0x88,0x70,0x00,
/*TODO*///	0x70,0x80,0xf0,0x88,0x88,0x88,0x70,0x00,0xf8,0x08,0x08,0x10,0x20,0x20,0x20,0x00,
/*TODO*///	0x70,0x88,0x88,0x70,0x88,0x88,0x70,0x00,0x70,0x88,0x88,0x88,0x78,0x08,0x70,0x00,
/*TODO*///	0x00,0x00,0x30,0x30,0x00,0x30,0x30,0x00,0x00,0x00,0x30,0x30,0x00,0x30,0x30,0x60,
/*TODO*///	0x10,0x20,0x40,0x80,0x40,0x20,0x10,0x00,0x00,0x00,0xf8,0x00,0xf8,0x00,0x00,0x00,
/*TODO*///	0x40,0x20,0x10,0x08,0x10,0x20,0x40,0x00,0x70,0x88,0x08,0x10,0x20,0x00,0x20,0x00,
/*TODO*///	0x30,0x48,0x94,0xa4,0xa4,0x94,0x48,0x30,0x70,0x88,0x88,0xf8,0x88,0x88,0x88,0x00,
/*TODO*///	0xf0,0x88,0x88,0xf0,0x88,0x88,0xf0,0x00,0x70,0x88,0x80,0x80,0x80,0x88,0x70,0x00,
/*TODO*///	0xf0,0x88,0x88,0x88,0x88,0x88,0xf0,0x00,0xf8,0x80,0x80,0xf0,0x80,0x80,0xf8,0x00,
/*TODO*///	0xf8,0x80,0x80,0xf0,0x80,0x80,0x80,0x00,0x70,0x88,0x80,0x98,0x88,0x88,0x70,0x00,
/*TODO*///	0x88,0x88,0x88,0xf8,0x88,0x88,0x88,0x00,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x00,
/*TODO*///	0x08,0x08,0x08,0x08,0x88,0x88,0x70,0x00,0x88,0x90,0xa0,0xc0,0xa0,0x90,0x88,0x00,
/*TODO*///	0x80,0x80,0x80,0x80,0x80,0x80,0xf8,0x00,0x88,0xd8,0xa8,0x88,0x88,0x88,0x88,0x00,
/*TODO*///	0x88,0xc8,0xa8,0x98,0x88,0x88,0x88,0x00,0x70,0x88,0x88,0x88,0x88,0x88,0x70,0x00,
/*TODO*///	0xf0,0x88,0x88,0xf0,0x80,0x80,0x80,0x00,0x70,0x88,0x88,0x88,0x88,0x88,0x70,0x08,
/*TODO*///	0xf0,0x88,0x88,0xf0,0x88,0x88,0x88,0x00,0x70,0x88,0x80,0x70,0x08,0x88,0x70,0x00,
/*TODO*///	0xf8,0x20,0x20,0x20,0x20,0x20,0x20,0x00,0x88,0x88,0x88,0x88,0x88,0x88,0x70,0x00,
/*TODO*///	0x88,0x88,0x88,0x88,0x88,0x50,0x20,0x00,0x88,0x88,0x88,0x88,0xa8,0xd8,0x88,0x00,
/*TODO*///	0x88,0x50,0x20,0x20,0x20,0x50,0x88,0x00,0x88,0x88,0x88,0x50,0x20,0x20,0x20,0x00,
/*TODO*///	0xf8,0x08,0x10,0x20,0x40,0x80,0xf8,0x00,0x30,0x20,0x20,0x20,0x20,0x20,0x30,0x00,
/*TODO*///	0x40,0x40,0x20,0x20,0x10,0x10,0x08,0x08,0x30,0x10,0x10,0x10,0x10,0x10,0x30,0x00,
/*TODO*///	0x20,0x50,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xfc,
/*TODO*///	0x40,0x20,0x10,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x70,0x08,0x78,0x88,0x78,0x00,
/*TODO*///	0x80,0x80,0xf0,0x88,0x88,0x88,0xf0,0x00,0x00,0x00,0x70,0x88,0x80,0x80,0x78,0x00,
/*TODO*///	0x08,0x08,0x78,0x88,0x88,0x88,0x78,0x00,0x00,0x00,0x70,0x88,0xf8,0x80,0x78,0x00,
/*TODO*///	0x18,0x20,0x70,0x20,0x20,0x20,0x20,0x00,0x00,0x00,0x78,0x88,0x88,0x78,0x08,0x70,
/*TODO*///	0x80,0x80,0xf0,0x88,0x88,0x88,0x88,0x00,0x20,0x00,0x20,0x20,0x20,0x20,0x20,0x00,
/*TODO*///	0x20,0x00,0x20,0x20,0x20,0x20,0x20,0xc0,0x80,0x80,0x90,0xa0,0xe0,0x90,0x88,0x00,
/*TODO*///	0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x00,0x00,0x00,0xf0,0xa8,0xa8,0xa8,0xa8,0x00,
/*TODO*///	0x00,0x00,0xb0,0xc8,0x88,0x88,0x88,0x00,0x00,0x00,0x70,0x88,0x88,0x88,0x70,0x00,
/*TODO*///	0x00,0x00,0xf0,0x88,0x88,0xf0,0x80,0x80,0x00,0x00,0x78,0x88,0x88,0x78,0x08,0x08,
/*TODO*///	0x00,0x00,0xb0,0xc8,0x80,0x80,0x80,0x00,0x00,0x00,0x78,0x80,0x70,0x08,0xf0,0x00,
/*TODO*///	0x20,0x20,0x70,0x20,0x20,0x20,0x18,0x00,0x00,0x00,0x88,0x88,0x88,0x98,0x68,0x00,
/*TODO*///	0x00,0x00,0x88,0x88,0x88,0x50,0x20,0x00,0x00,0x00,0xa8,0xa8,0xa8,0xa8,0x50,0x00,
/*TODO*///	0x00,0x00,0x88,0x50,0x20,0x50,0x88,0x00,0x00,0x00,0x88,0x88,0x88,0x78,0x08,0x70,
/*TODO*///	0x00,0x00,0xf8,0x10,0x20,0x40,0xf8,0x00,0x08,0x10,0x10,0x20,0x10,0x10,0x08,0x00,
/*TODO*///	0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x40,0x20,0x20,0x10,0x20,0x20,0x40,0x00,
/*TODO*///	0x00,0x68,0xb0,0x00,0x00,0x00,0x00,0x00,0x20,0x50,0x20,0x50,0xa8,0x50,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x20,0x20,0x40,0x0c,0x10,0x38,0x10,0x20,0x20,0xc0,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x28,0x28,0x50,0x00,0x00,0x00,0x00,0x00,0x00,0xa8,0x00,
/*TODO*///	0x70,0xa8,0xf8,0x20,0x20,0x20,0x20,0x00,0x70,0xa8,0xf8,0x20,0x20,0xf8,0xa8,0x70,
/*TODO*///	0x20,0x50,0x88,0x00,0x00,0x00,0x00,0x00,0x44,0xa8,0x50,0x20,0x68,0xd4,0x28,0x00,
/*TODO*///	0x88,0x70,0x88,0x60,0x30,0x88,0x70,0x00,0x00,0x10,0x20,0x40,0x20,0x10,0x00,0x00,
/*TODO*///	0x78,0xa0,0xa0,0xb0,0xa0,0xa0,0x78,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x10,0x20,0x20,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x10,0x10,0x20,0x00,0x00,0x00,0x00,0x00,0x28,0x50,0x50,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x28,0x28,0x50,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x30,0x78,0x78,0x30,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x78,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xfc,0x00,0x00,0x00,0x00,
/*TODO*///	0x68,0xb0,0x00,0x00,0x00,0x00,0x00,0x00,0xf4,0x5c,0x54,0x54,0x00,0x00,0x00,0x00,
/*TODO*///	0x88,0x70,0x78,0x80,0x70,0x08,0xf0,0x00,0x00,0x40,0x20,0x10,0x20,0x40,0x00,0x00,
/*TODO*///	0x00,0x00,0x70,0xa8,0xb8,0xa0,0x78,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x50,0x88,0x88,0x50,0x20,0x20,0x20,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x20,0x00,0x20,0x20,0x20,0x20,0x20,0x00,
/*TODO*///	0x00,0x20,0x70,0xa8,0xa0,0xa8,0x70,0x20,0x30,0x48,0x40,0xe0,0x40,0x48,0xf0,0x00,
/*TODO*///	0x00,0x48,0x30,0x48,0x48,0x30,0x48,0x00,0x88,0x88,0x50,0xf8,0x20,0xf8,0x20,0x00,
/*TODO*///	0x20,0x20,0x20,0x00,0x20,0x20,0x20,0x00,0x78,0x80,0x70,0x88,0x70,0x08,0xf0,0x00,
/*TODO*///	0xd8,0xd8,0x00,0x00,0x00,0x00,0x00,0x00,0x30,0x48,0x94,0xa4,0xa4,0x94,0x48,0x30,
/*TODO*///	0x60,0x10,0x70,0x90,0x70,0x00,0x00,0x00,0x00,0x28,0x50,0xa0,0x50,0x28,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0xf8,0x08,0x00,0x00,0x00,0x00,0x00,0x00,0x78,0x00,0x00,0x00,0x00,
/*TODO*///	0x30,0x48,0xb4,0xb4,0xa4,0xb4,0x48,0x30,0x7c,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
/*TODO*///	0x60,0x90,0x90,0x60,0x00,0x00,0x00,0x00,0x20,0x20,0xf8,0x20,0x20,0x00,0xf8,0x00,
/*TODO*///	0x60,0x90,0x20,0x40,0xf0,0x00,0x00,0x00,0x60,0x90,0x20,0x90,0x60,0x00,0x00,0x00,
/*TODO*///	0x10,0x20,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x88,0x88,0x88,0xc8,0xb0,0x80,
/*TODO*///	0x78,0xd0,0xd0,0xd0,0x50,0x50,0x50,0x00,0x00,0x00,0x00,0x30,0x30,0x00,0x00,0x00,
/*TODO*///	0x00,0x00,0x00,0x00,0x00,0x10,0x20,0x00,0x20,0x60,0x20,0x20,0x70,0x00,0x00,0x00,
/*TODO*///	0x20,0x50,0x20,0x00,0x00,0x00,0x00,0x00,0x00,0xa0,0x50,0x28,0x50,0xa0,0x00,0x00,
/*TODO*///	0x40,0x48,0x50,0x28,0x58,0xa8,0x38,0x08,0x40,0x48,0x50,0x28,0x44,0x98,0x20,0x3c,
/*TODO*///	0xc0,0x28,0xd0,0x28,0xd8,0xa8,0x38,0x08,0x20,0x00,0x20,0x40,0x80,0x88,0x70,0x00,
/*TODO*///	0x40,0x20,0x70,0x88,0xf8,0x88,0x88,0x00,0x10,0x20,0x70,0x88,0xf8,0x88,0x88,0x00,
/*TODO*///	0x70,0x00,0x70,0x88,0xf8,0x88,0x88,0x00,0x68,0xb0,0x70,0x88,0xf8,0x88,0x88,0x00,
/*TODO*///	0x50,0x00,0x70,0x88,0xf8,0x88,0x88,0x00,0x20,0x50,0x70,0x88,0xf8,0x88,0x88,0x00,
/*TODO*///	0x78,0xa0,0xa0,0xf0,0xa0,0xa0,0xb8,0x00,0x70,0x88,0x80,0x80,0x88,0x70,0x08,0x70,
/*TODO*///	0x40,0x20,0xf8,0x80,0xf0,0x80,0xf8,0x00,0x10,0x20,0xf8,0x80,0xf0,0x80,0xf8,0x00,
/*TODO*///	0x70,0x00,0xf8,0x80,0xf0,0x80,0xf8,0x00,0x50,0x00,0xf8,0x80,0xf0,0x80,0xf8,0x00,
/*TODO*///	0x40,0x20,0x70,0x20,0x20,0x20,0x70,0x00,0x10,0x20,0x70,0x20,0x20,0x20,0x70,0x00,
/*TODO*///	0x70,0x00,0x70,0x20,0x20,0x20,0x70,0x00,0x50,0x00,0x70,0x20,0x20,0x20,0x70,0x00,
/*TODO*///	0x70,0x48,0x48,0xe8,0x48,0x48,0x70,0x00,0x68,0xb0,0x88,0xc8,0xa8,0x98,0x88,0x00,
/*TODO*///	0x40,0x20,0x70,0x88,0x88,0x88,0x70,0x00,0x10,0x20,0x70,0x88,0x88,0x88,0x70,0x00,
/*TODO*///	0x70,0x00,0x70,0x88,0x88,0x88,0x70,0x00,0x68,0xb0,0x70,0x88,0x88,0x88,0x70,0x00,
/*TODO*///	0x50,0x00,0x70,0x88,0x88,0x88,0x70,0x00,0x00,0x88,0x50,0x20,0x50,0x88,0x00,0x00,
/*TODO*///	0x00,0x74,0x88,0x90,0xa8,0x48,0xb0,0x00,0x40,0x20,0x88,0x88,0x88,0x88,0x70,0x00,
/*TODO*///	0x10,0x20,0x88,0x88,0x88,0x88,0x70,0x00,0x70,0x00,0x88,0x88,0x88,0x88,0x70,0x00,
/*TODO*///	0x50,0x00,0x88,0x88,0x88,0x88,0x70,0x00,0x10,0xa8,0x88,0x50,0x20,0x20,0x20,0x00,
/*TODO*///	0x00,0x80,0xf0,0x88,0x88,0xf0,0x80,0x80,0x60,0x90,0x90,0xb0,0x88,0x88,0xb0,0x00,
/*TODO*///	0x40,0x20,0x70,0x08,0x78,0x88,0x78,0x00,0x10,0x20,0x70,0x08,0x78,0x88,0x78,0x00,
/*TODO*///	0x70,0x00,0x70,0x08,0x78,0x88,0x78,0x00,0x68,0xb0,0x70,0x08,0x78,0x88,0x78,0x00,
/*TODO*///	0x50,0x00,0x70,0x08,0x78,0x88,0x78,0x00,0x20,0x50,0x70,0x08,0x78,0x88,0x78,0x00,
/*TODO*///	0x00,0x00,0xf0,0x28,0x78,0xa0,0x78,0x00,0x00,0x00,0x70,0x88,0x80,0x78,0x08,0x70,
/*TODO*///	0x40,0x20,0x70,0x88,0xf8,0x80,0x70,0x00,0x10,0x20,0x70,0x88,0xf8,0x80,0x70,0x00,
/*TODO*///	0x70,0x00,0x70,0x88,0xf8,0x80,0x70,0x00,0x50,0x00,0x70,0x88,0xf8,0x80,0x70,0x00,
/*TODO*///	0x40,0x20,0x00,0x60,0x20,0x20,0x70,0x00,0x10,0x20,0x00,0x60,0x20,0x20,0x70,0x00,
/*TODO*///	0x20,0x50,0x00,0x60,0x20,0x20,0x70,0x00,0x50,0x00,0x00,0x60,0x20,0x20,0x70,0x00,
/*TODO*///	0x50,0x60,0x10,0x78,0x88,0x88,0x70,0x00,0x68,0xb0,0x00,0xf0,0x88,0x88,0x88,0x00,
/*TODO*///	0x40,0x20,0x00,0x70,0x88,0x88,0x70,0x00,0x10,0x20,0x00,0x70,0x88,0x88,0x70,0x00,
/*TODO*///	0x20,0x50,0x00,0x70,0x88,0x88,0x70,0x00,0x68,0xb0,0x00,0x70,0x88,0x88,0x70,0x00,
/*TODO*///	0x00,0x50,0x00,0x70,0x88,0x88,0x70,0x00,0x00,0x20,0x00,0xf8,0x00,0x20,0x00,0x00,
/*TODO*///	0x00,0x00,0x68,0x90,0xa8,0x48,0xb0,0x00,0x40,0x20,0x88,0x88,0x88,0x98,0x68,0x00,
/*TODO*///	0x10,0x20,0x88,0x88,0x88,0x98,0x68,0x00,0x70,0x00,0x88,0x88,0x88,0x98,0x68,0x00,
/*TODO*///	0x50,0x00,0x88,0x88,0x88,0x98,0x68,0x00,0x10,0x20,0x88,0x88,0x88,0x78,0x08,0x70,
/*TODO*///	0x80,0xf0,0x88,0x88,0xf0,0x80,0x80,0x80,0x50,0x00,0x88,0x88,0x88,0x78,0x08,0x70
/*TODO*///};
/*TODO*///
/*TODO*///static const struct GfxLayout uifontlayout =
/*TODO*///{
/*TODO*///	6,8,
/*TODO*///	256,
/*TODO*///	1,
/*TODO*///	{ 0 },
/*TODO*///	{ 0, 1, 2, 3, 4, 5, 6, 7 },
/*TODO*///	{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
/*TODO*///	8*8
/*TODO*///};
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark UTILITIES
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	ui_markdirty - mark a raw rectangle dirty
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///INLINE void ui_markdirty(const struct rectangle *rect)
/*TODO*///{
/*TODO*///	artwork_mark_ui_dirty(rect->min_x, rect->min_y, rect->max_x, rect->max_y);
/*TODO*///	ui_dirty = 5;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	ui_raw2rot_rect - convert a rect from raw
/*TODO*///	coordinates to rotated coordinates
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void ui_raw2rot_rect(struct rectangle *rect)
/*TODO*///{
/*TODO*///	int temp, w, h;
/*TODO*///
/*TODO*///	/* get the effective screen size, including artwork */
/*TODO*///	artwork_get_screensize(&w, &h);
/*TODO*///
/*TODO*///	/* apply X flip */
/*TODO*///	if (Machine->ui_orientation & ORIENTATION_FLIP_X)
/*TODO*///	{
/*TODO*///		temp = w - rect->min_x - 1;
/*TODO*///		rect->min_x = w - rect->max_x - 1;
/*TODO*///		rect->max_x = temp;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* apply Y flip */
/*TODO*///	if (Machine->ui_orientation & ORIENTATION_FLIP_Y)
/*TODO*///	{
/*TODO*///		temp = h - rect->min_y - 1;
/*TODO*///		rect->min_y = h - rect->max_y - 1;
/*TODO*///		rect->max_y = temp;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* apply X/Y swap first */
/*TODO*///	if (Machine->ui_orientation & ORIENTATION_SWAP_XY)
/*TODO*///	{
/*TODO*///		temp = rect->min_x; rect->min_x = rect->min_y; rect->min_y = temp;
/*TODO*///		temp = rect->max_x; rect->max_x = rect->max_y; rect->max_y = temp;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	ui_rot2raw_rect - convert a rect from rotated
/*TODO*///	coordinates to raw coordinates
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void ui_rot2raw_rect(struct rectangle *rect)
/*TODO*///{
/*TODO*///	int temp, w, h;
/*TODO*///
/*TODO*///	/* get the effective screen size, including artwork */
/*TODO*///	artwork_get_screensize(&w, &h);
/*TODO*///
/*TODO*///	/* apply X/Y swap first */
/*TODO*///	if (Machine->ui_orientation & ORIENTATION_SWAP_XY)
/*TODO*///	{
/*TODO*///		temp = rect->min_x; rect->min_x = rect->min_y; rect->min_y = temp;
/*TODO*///		temp = rect->max_x; rect->max_x = rect->max_y; rect->max_y = temp;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* apply X flip */
/*TODO*///	if (Machine->ui_orientation & ORIENTATION_FLIP_X)
/*TODO*///	{
/*TODO*///		temp = w - rect->min_x - 1;
/*TODO*///		rect->min_x = w - rect->max_x - 1;
/*TODO*///		rect->max_x = temp;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* apply Y flip */
/*TODO*///	if (Machine->ui_orientation & ORIENTATION_FLIP_Y)
/*TODO*///	{
/*TODO*///		temp = h - rect->min_y - 1;
/*TODO*///		rect->min_y = h - rect->max_y - 1;
/*TODO*///		rect->max_y = temp;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	set_ui_visarea - called by the OSD code to
/*TODO*///	set the UI's domain
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void set_ui_visarea(int xmin, int ymin, int xmax, int ymax)
/*TODO*///{
/*TODO*///	/* fill in the rect */
/*TODO*///	uirawbounds.min_x = xmin;
/*TODO*///	uirawbounds.min_y = ymin;
/*TODO*///	uirawbounds.max_x = xmax;
/*TODO*///	uirawbounds.max_y = ymax;
/*TODO*///
/*TODO*///	/* orient it */
/*TODO*///	uirotbounds = uirawbounds;
/*TODO*///	ui_raw2rot_rect(&uirotbounds);
/*TODO*///
/*TODO*///	/* make some easier-to-access globals */
/*TODO*///	uirotwidth = uirotbounds.max_x - uirotbounds.min_x + 1;
/*TODO*///	uirotheight = uirotbounds.max_y - uirotbounds.min_y + 1;
/*TODO*///
/*TODO*///	/* remove me */
/*TODO*///	Machine->uiwidth = uirotbounds.max_x - uirotbounds.min_x + 1;
/*TODO*///	Machine->uiheight = uirotbounds.max_y - uirotbounds.min_y + 1;
/*TODO*///	Machine->uixmin = uirotbounds.min_x;
/*TODO*///	Machine->uiymin = uirotbounds.min_y;
/*TODO*///
/*TODO*///	/* rebuild the font */
/*TODO*///	builduifont();
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	erase_screen - erase the screen
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void erase_screen(struct mame_bitmap *bitmap)
/*TODO*///{
/*TODO*///	fillbitmap(bitmap, get_black_pen(), NULL);
/*TODO*///	schedule_full_refresh();
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark -
/*TODO*///#pragma mark FONTS & TEXT
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	builduifont - build the user interface fonts
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///struct GfxElement *builduifont(void)
/*TODO*///{
/*TODO*///	struct GfxLayout layout = uifontlayout;
/*TODO*///	UINT32 tempoffset[MAX_GFX_SIZE];
/*TODO*///	struct GfxElement *font;
/*TODO*///	int temp, i;
/*TODO*///
/*TODO*///	/* free any existing fonts */
/*TODO*///	if (Machine->uifont)
/*TODO*///		freegfx(Machine->uifont);
/*TODO*///	if (uirotfont)
/*TODO*///		freegfx(uirotfont);
/*TODO*///
/*TODO*///	/* first decode a straight on version for games */
/*TODO*///	Machine->uifont = font = decodegfx(uifontdata, &layout);
/*TODO*///	Machine->uifontwidth = layout.width;
/*TODO*///	Machine->uifontheight = layout.height;
/*TODO*///
/*TODO*///	/* pixel double horizontally */
/*TODO*///	if (uirotwidth >= 420)
/*TODO*///	{
/*TODO*///		memcpy(tempoffset, layout.xoffset, sizeof(tempoffset));
/*TODO*///		for (i = 0; i < layout.width; i++)
/*TODO*///			layout.xoffset[i*2+0] = layout.xoffset[i*2+1] = tempoffset[i];
/*TODO*///		layout.width *= 2;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* pixel double vertically */
/*TODO*///	if (uirotheight >= 420)
/*TODO*///	{
/*TODO*///		memcpy(tempoffset, layout.yoffset, sizeof(tempoffset));
/*TODO*///		for (i = 0; i < layout.height; i++)
/*TODO*///			layout.yoffset[i*2+0] = layout.yoffset[i*2+1] = tempoffset[i];
/*TODO*///		layout.height *= 2;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* apply swappage */
/*TODO*///	if (Machine->ui_orientation & ORIENTATION_SWAP_XY)
/*TODO*///	{
/*TODO*///		memcpy(tempoffset, layout.xoffset, sizeof(tempoffset));
/*TODO*///		memcpy(layout.xoffset, layout.yoffset, sizeof(layout.xoffset));
/*TODO*///		memcpy(layout.yoffset, tempoffset, sizeof(layout.yoffset));
/*TODO*///
/*TODO*///		temp = layout.width;
/*TODO*///		layout.width = layout.height;
/*TODO*///		layout.height = temp;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* apply xflip */
/*TODO*///	if (Machine->ui_orientation & ORIENTATION_FLIP_X)
/*TODO*///	{
/*TODO*///		memcpy(tempoffset, layout.xoffset, sizeof(tempoffset));
/*TODO*///		for (i = 0; i < layout.width; i++)
/*TODO*///			layout.xoffset[i] = tempoffset[layout.width - 1 - i];
/*TODO*///	}
/*TODO*///
/*TODO*///	/* apply yflip */
/*TODO*///	if (Machine->ui_orientation & ORIENTATION_FLIP_Y)
/*TODO*///	{
/*TODO*///		memcpy(tempoffset, layout.yoffset, sizeof(tempoffset));
/*TODO*///		for (i = 0; i < layout.height; i++)
/*TODO*///			layout.yoffset[i] = tempoffset[layout.height - 1 - i];
/*TODO*///	}
/*TODO*///
/*TODO*///	/* decode rotated font */
/*TODO*///	uirotfont = decodegfx(uifontdata, &layout);
/*TODO*///
/*TODO*///	/* set the raw and rotated character width/height */
/*TODO*///	uirawcharwidth = layout.width;
/*TODO*///	uirawcharheight = layout.height;
/*TODO*///	uirotcharwidth = (Machine->ui_orientation & ORIENTATION_SWAP_XY) ? layout.height : layout.width;
/*TODO*///	uirotcharheight = (Machine->ui_orientation & ORIENTATION_SWAP_XY) ? layout.width : layout.height;
/*TODO*///
/*TODO*///	/* set up the bogus colortable */
/*TODO*///	if (font)
/*TODO*///	{
/*TODO*///		static pen_t colortable[2*2];
/*TODO*///
/*TODO*///		/* colortable will be set at run time */
/*TODO*///		font->colortable = colortable;
/*TODO*///		font->total_colors = 2;
/*TODO*///		uirotfont->colortable = colortable;
/*TODO*///		uirotfont->total_colors = 2;
/*TODO*///	}
/*TODO*///
/*TODO*///	return font;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	ui_drawchar - draw a rotated character
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void ui_drawchar(struct mame_bitmap *dest, int ch, int color, int sx, int sy)
/*TODO*///{
/*TODO*///	struct rectangle bounds;
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///	extern int skip_this_frame;
/*TODO*///	skip_this_frame = 0;
/*TODO*///#endif /* MESS */
/*TODO*///
/*TODO*///	/* construct a rectangle in rotated coordinates, then transform it */
/*TODO*///	bounds.min_x = sx + uirotbounds.min_x;
/*TODO*///	bounds.min_y = sy + uirotbounds.min_y;
/*TODO*///	bounds.max_x = bounds.min_x + uirotcharwidth - 1;
/*TODO*///	bounds.max_y = bounds.min_y + uirotcharheight - 1;
/*TODO*///	ui_rot2raw_rect(&bounds);
/*TODO*///
/*TODO*///	/* now render */
/*TODO*///	drawgfx(dest, uirotfont, ch, color, 0, 0, bounds.min_x, bounds.min_y, &uirawbounds, TRANSPARENCY_NONE, 0);
/*TODO*///
/*TODO*///	/* mark dirty */
/*TODO*///	ui_markdirty(&bounds);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	ui_text_ex - draw a string to the screen
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void ui_text_ex(struct mame_bitmap *bitmap, const char *buf_begin, const char *buf_end, int x, int y, int color)
/*TODO*///{
/*TODO*///	for ( ; buf_begin != buf_end; ++buf_begin)
/*TODO*///	{
/*TODO*///		ui_drawchar(bitmap, *buf_begin, color, x, y);
/*TODO*///		x += uirotcharwidth;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	ui_text_ex - draw a string to the screen
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void ui_text(struct mame_bitmap *bitmap, const char *buf, int x, int y)
/*TODO*///{
/*TODO*///	ui_text_ex(bitmap, buf, buf + strlen(buf), x, y, UI_COLOR_NORMAL);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	displaytext - display a series of text lines
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void displaytext(struct mame_bitmap *bitmap, const struct DisplayText *dt)
/*TODO*///{
/*TODO*///   /* loop until we run out of descriptors */
/*TODO*///   for ( ; dt->text; dt++)
/*TODO*///   {
/*TODO*///      ui_text_ex(bitmap, dt->text, dt->text + strlen(dt->text), dt->x, dt->y, dt->color);
/*TODO*///   }
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	multiline_extract - extract one line from a
/*TODO*///	multiline buffer; return the number of
/*TODO*///	characters in the line; pbegin points to the
/*TODO*///	start of the next line
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static unsigned multiline_extract(const char **pbegin, const char *end, unsigned maxchars)
/*TODO*///{
/*TODO*///	const char *begin = *pbegin;
/*TODO*///	unsigned numchars = 0;
/*TODO*///
/*TODO*///	/* loop until we hit the end or max out */
/*TODO*///	while (begin != end && numchars < maxchars)
/*TODO*///	{
/*TODO*///		/* if we hit an EOL, strip it and return the current count */
/*TODO*///		if (*begin == '\n')
/*TODO*///		{
/*TODO*///			*pbegin = begin + 1; /* strip final space */
/*TODO*///			return numchars;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* if we hit a space, word wrap */
/*TODO*///		else if (*begin == ' ')
/*TODO*///		{
/*TODO*///			/* find the end of this word */
/*TODO*///			const char* word_end = begin + 1;
/*TODO*///			while (word_end != end && *word_end != ' ' && *word_end != '\n')
/*TODO*///				++word_end;
/*TODO*///
/*TODO*///			/* if that pushes us past the max, truncate here */
/*TODO*///			if (numchars + word_end - begin > maxchars)
/*TODO*///			{
/*TODO*///				/* if we have at least one character, strip the space */
/*TODO*///				if (numchars)
/*TODO*///				{
/*TODO*///					*pbegin = begin + 1;
/*TODO*///					return numchars;
/*TODO*///				}
/*TODO*///
/*TODO*///				/* otherwise, take as much as we can */
/*TODO*///				else
/*TODO*///				{
/*TODO*///					*pbegin = begin + maxchars;
/*TODO*///					return maxchars;
/*TODO*///				}
/*TODO*///			}
/*TODO*///
/*TODO*///			/* advance to the end of this word */
/*TODO*///			numchars += word_end - begin;
/*TODO*///			begin = word_end;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* for all other chars, just increment */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			++numchars;
/*TODO*///			++begin;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* make sure we always make forward progress */
/*TODO*///	if (begin != end && (*begin == '\n' || *begin == ' '))
/*TODO*///		++begin;
/*TODO*///	*pbegin = begin;
/*TODO*///	return numchars;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	multiline_size - compute the output size of a
/*TODO*///	multiline string
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void multiline_size(int *dx, int *dy, const char *begin, const char *end, unsigned maxchars)
/*TODO*///{
/*TODO*///	unsigned rows = 0;
/*TODO*///	unsigned cols = 0;
/*TODO*///
/*TODO*///	/* extract lines until the end, counting rows and tracking the max columns */
/*TODO*///	while (begin != end)
/*TODO*///	{
/*TODO*///		unsigned len;
/*TODO*///		len = multiline_extract(&begin, end, maxchars);
/*TODO*///		if (len > cols)
/*TODO*///			cols = len;
/*TODO*///		++rows;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* return the final result scaled by the char size */
/*TODO*///	*dx = cols * uirotcharwidth;
/*TODO*///	*dy = (rows - 1) * 3*uirotcharheight/2 + uirotcharheight;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	multilinebox_size - compute the output size of
/*TODO*///	a multiline string with box
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void multilinebox_size(int *dx, int *dy, const char *begin, const char *end, unsigned maxchars)
/*TODO*///{
/*TODO*///	/* standard computation, plus an extra char width and height */
/*TODO*///	multiline_size(dx, dy, begin, end, maxchars);
/*TODO*///	*dx += uirotcharwidth;
/*TODO*///	*dy += uirotcharheight;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	ui_multitext_ex - display a multiline string
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void ui_multitext_ex(struct mame_bitmap *bitmap, const char *begin, const char *end, unsigned maxchars, int x, int y, int color)
/*TODO*///{
/*TODO*///	/* extract lines until the end */
/*TODO*///	while (begin != end)
/*TODO*///	{
/*TODO*///		const char *line_begin = begin;
/*TODO*///		unsigned len = multiline_extract(&begin, end, maxchars);
/*TODO*///		ui_text_ex(bitmap, line_begin, line_begin + len, x, y, color);
/*TODO*///		y += 3*uirotcharheight/2;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	ui_multitextbox_ex - display a multiline
/*TODO*///	string with box
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void ui_multitextbox_ex(struct mame_bitmap *bitmap, const char *begin, const char *end, unsigned maxchars, int x, int y, int dx, int dy, int color)
/*TODO*///{
/*TODO*///	/* draw the box first */
/*TODO*///	ui_drawbox(bitmap, x, y, dx, dy);
/*TODO*///
/*TODO*///	/* indent by half a character */
/*TODO*///	x += uirotcharwidth/2;
/*TODO*///	y += uirotcharheight/2;
/*TODO*///
/*TODO*///	/* draw the text */
/*TODO*///	ui_multitext_ex(bitmap, begin, end, maxchars, x, y, color);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark -
/*TODO*///#pragma mark BOXES & LINES
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	ui_drawbox - draw a black box with white border
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void ui_drawbox(struct mame_bitmap *bitmap, int leftx, int topy, int width, int height)
/*TODO*///{
/*TODO*///	struct rectangle bounds, tbounds;
/*TODO*///	pen_t black, white;
/*TODO*///
/*TODO*///	/* make a rect and clip it */
/*TODO*///	bounds.min_x = uirotbounds.min_x + leftx;
/*TODO*///	bounds.min_y = uirotbounds.min_y + topy;
/*TODO*///	bounds.max_x = bounds.min_x + width - 1;
/*TODO*///	bounds.max_y = bounds.min_y + height - 1;
/*TODO*///	sect_rect(&bounds, &uirotbounds);
/*TODO*///
/*TODO*///	/* pick colors from the colortable */
/*TODO*///	black = uirotfont->colortable[0];
/*TODO*///	white = uirotfont->colortable[1];
/*TODO*///
/*TODO*///	/* top edge */
/*TODO*///	tbounds = bounds;
/*TODO*///	tbounds.max_y = tbounds.min_y;
/*TODO*///	ui_rot2raw_rect(&tbounds);
/*TODO*///	fillbitmap(bitmap, white, &tbounds);
/*TODO*///
/*TODO*///	/* bottom edge */
/*TODO*///	tbounds = bounds;
/*TODO*///	tbounds.min_y = tbounds.max_y;
/*TODO*///	ui_rot2raw_rect(&tbounds);
/*TODO*///	fillbitmap(bitmap, white, &tbounds);
/*TODO*///
/*TODO*///	/* left edge */
/*TODO*///	tbounds = bounds;
/*TODO*///	tbounds.max_x = tbounds.min_x;
/*TODO*///	ui_rot2raw_rect(&tbounds);
/*TODO*///	fillbitmap(bitmap, white, &tbounds);
/*TODO*///
/*TODO*///	/* right edge */
/*TODO*///	tbounds = bounds;
/*TODO*///	tbounds.min_x = tbounds.max_x;
/*TODO*///	ui_rot2raw_rect(&tbounds);
/*TODO*///	fillbitmap(bitmap, white, &tbounds);
/*TODO*///
/*TODO*///	/* fill in the middle with black */
/*TODO*///	tbounds = bounds;
/*TODO*///	tbounds.min_x++;
/*TODO*///	tbounds.min_y++;
/*TODO*///	tbounds.max_x--;
/*TODO*///	tbounds.max_y--;
/*TODO*///	ui_rot2raw_rect(&tbounds);
/*TODO*///	fillbitmap(bitmap, black, &tbounds);
/*TODO*///
/*TODO*///	/* mark things dirty */
/*TODO*///	ui_rot2raw_rect(&bounds);
/*TODO*///	ui_markdirty(&bounds);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	drawbar - draw a thermometer bar
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void drawbar(struct mame_bitmap *bitmap, int leftx, int topy, int width, int height, int percentage, int default_percentage)
/*TODO*///{
/*TODO*///	struct rectangle bounds, tbounds;
/*TODO*///	UINT32 black, white;
/*TODO*///
/*TODO*///	/* make a rect and orient/clip it */
/*TODO*///	bounds.min_x = uirotbounds.min_x + leftx;
/*TODO*///	bounds.min_y = uirotbounds.min_y + topy;
/*TODO*///	bounds.max_x = bounds.min_x + width - 1;
/*TODO*///	bounds.max_y = bounds.min_y + height - 1;
/*TODO*///	sect_rect(&bounds, &uirotbounds);
/*TODO*///
/*TODO*///	/* pick colors from the colortable */
/*TODO*///	black = uirotfont->colortable[0];
/*TODO*///	white = uirotfont->colortable[1];
/*TODO*///
/*TODO*///	/* draw the top default percentage marker */
/*TODO*///	tbounds = bounds;
/*TODO*///	tbounds.min_x += (width - 1) * default_percentage / 100;
/*TODO*///	tbounds.max_x = tbounds.min_x;
/*TODO*///	tbounds.max_y = tbounds.min_y + height / 8;
/*TODO*///	ui_rot2raw_rect(&tbounds);
/*TODO*///	fillbitmap(bitmap, white, &tbounds);
/*TODO*///
/*TODO*///	/* draw the bottom default percentage marker */
/*TODO*///	tbounds = bounds;
/*TODO*///	tbounds.min_x += (width - 1) * default_percentage / 100;
/*TODO*///	tbounds.max_x = tbounds.min_x;
/*TODO*///	tbounds.min_y = tbounds.max_y - height / 8;
/*TODO*///	ui_rot2raw_rect(&tbounds);
/*TODO*///	fillbitmap(bitmap, white, &tbounds);
/*TODO*///
/*TODO*///	/* draw the top line of the bar */
/*TODO*///	tbounds = bounds;
/*TODO*///	tbounds.min_y += height / 8;
/*TODO*///	tbounds.max_y = tbounds.min_y;
/*TODO*///	ui_rot2raw_rect(&tbounds);
/*TODO*///	fillbitmap(bitmap, white, &tbounds);
/*TODO*///
/*TODO*///	/* draw the bottom line of the bar */
/*TODO*///	tbounds = bounds;
/*TODO*///	tbounds.max_y -= height / 8;
/*TODO*///	tbounds.min_y = tbounds.max_y;
/*TODO*///	ui_rot2raw_rect(&tbounds);
/*TODO*///	fillbitmap(bitmap, white, &tbounds);
/*TODO*///
/*TODO*///	/* fill in the percentage */
/*TODO*///	tbounds = bounds;
/*TODO*///	tbounds.max_x = tbounds.min_x + (width - 1) * percentage / 100;
/*TODO*///	tbounds.min_y += height / 8;
/*TODO*///	tbounds.max_y -= height / 8;
/*TODO*///	ui_rot2raw_rect(&tbounds);
/*TODO*///	fillbitmap(bitmap, white, &tbounds);
/*TODO*///
/*TODO*///	/* mark things dirty */
/*TODO*///	ui_rot2raw_rect(&bounds);
/*TODO*///	ui_markdirty(&bounds);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark -
/*TODO*///#pragma mark BOXES & LINES
/*TODO*///#endif
/*TODO*///
/*TODO*///void ui_displaymenu(struct mame_bitmap *bitmap,const char **items,const char **subitems,char *flag,int selected,int arrowize_subitem)
/*TODO*///{
/*TODO*///	struct DisplayText dt[256];
/*TODO*///	int curr_dt;
/*TODO*///	const char *lefthilight = ui_getstring (UI_lefthilight);
/*TODO*///	const char *righthilight = ui_getstring (UI_righthilight);
/*TODO*///	const char *uparrow = ui_getstring (UI_uparrow);
/*TODO*///	const char *downarrow = ui_getstring (UI_downarrow);
/*TODO*///	const char *leftarrow = ui_getstring (UI_leftarrow);
/*TODO*///	const char *rightarrow = ui_getstring (UI_rightarrow);
/*TODO*///	int i,count,len,maxlen,highlen;
/*TODO*///	int leftoffs,topoffs,visible,topitem;
/*TODO*///	int selected_long;
/*TODO*///
/*TODO*///
/*TODO*///	i = 0;
/*TODO*///	maxlen = 0;
/*TODO*///	highlen = uirotwidth / uirotcharwidth;
/*TODO*///	while (items[i])
/*TODO*///	{
/*TODO*///		len = 3 + strlen(items[i]);
/*TODO*///		if (subitems && subitems[i])
/*TODO*///			len += 2 + strlen(subitems[i]);
/*TODO*///		if (len > maxlen && len <= highlen)
/*TODO*///			maxlen = len;
/*TODO*///		i++;
/*TODO*///	}
/*TODO*///	count = i;
/*TODO*///
/*TODO*///	visible = uirotheight / (3 * uirotcharheight / 2) - 1;
/*TODO*///	topitem = 0;
/*TODO*///	if (visible > count) visible = count;
/*TODO*///	else
/*TODO*///	{
/*TODO*///		topitem = selected - visible / 2;
/*TODO*///		if (topitem < 0) topitem = 0;
/*TODO*///		if (topitem > count - visible) topitem = count - visible;
/*TODO*///	}
/*TODO*///
/*TODO*///	leftoffs = (uirotwidth - maxlen * uirotcharwidth) / 2;
/*TODO*///	topoffs = (uirotheight - (3 * visible + 1) * uirotcharheight / 2) / 2;
/*TODO*///
/*TODO*///	/* black background */
/*TODO*///	ui_drawbox(bitmap,leftoffs,topoffs,maxlen * uirotcharwidth,(3 * visible + 1) * uirotcharheight / 2);
/*TODO*///
/*TODO*///	selected_long = 0;
/*TODO*///	curr_dt = 0;
/*TODO*///	for (i = 0;i < visible;i++)
/*TODO*///	{
/*TODO*///		int item = i + topitem;
/*TODO*///
/*TODO*///		if (i == 0 && item > 0)
/*TODO*///		{
/*TODO*///			dt[curr_dt].text = uparrow;
/*TODO*///			dt[curr_dt].color = UI_COLOR_NORMAL;
/*TODO*///			dt[curr_dt].x = (uirotwidth - uirotcharwidth * strlen(uparrow)) / 2;
/*TODO*///			dt[curr_dt].y = topoffs + (3*i+1)*uirotcharheight/2;
/*TODO*///			curr_dt++;
/*TODO*///		}
/*TODO*///		else if (i == visible - 1 && item < count - 1)
/*TODO*///		{
/*TODO*///			dt[curr_dt].text = downarrow;
/*TODO*///			dt[curr_dt].color = UI_COLOR_NORMAL;
/*TODO*///			dt[curr_dt].x = (uirotwidth - uirotcharwidth * strlen(downarrow)) / 2;
/*TODO*///			dt[curr_dt].y = topoffs + (3*i+1)*uirotcharheight/2;
/*TODO*///			curr_dt++;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			if (subitems && subitems[item])
/*TODO*///			{
/*TODO*///				int sublen;
/*TODO*///				len = strlen(items[item]);
/*TODO*///				dt[curr_dt].text = items[item];
/*TODO*///				dt[curr_dt].color = UI_COLOR_NORMAL;
/*TODO*///				dt[curr_dt].x = leftoffs + 3*uirotcharwidth/2;
/*TODO*///				dt[curr_dt].y = topoffs + (3*i+1)*uirotcharheight/2;
/*TODO*///				curr_dt++;
/*TODO*///				sublen = strlen(subitems[item]);
/*TODO*///				if (sublen > maxlen-5-len)
/*TODO*///				{
/*TODO*///					dt[curr_dt].text = "...";
/*TODO*///					sublen = strlen(dt[curr_dt].text);
/*TODO*///					if (item == selected)
/*TODO*///						selected_long = 1;
/*TODO*///				} else {
/*TODO*///					dt[curr_dt].text = subitems[item];
/*TODO*///				}
/*TODO*///				/* If this item is flagged, draw it in inverse print */
/*TODO*///				dt[curr_dt].color = (flag && flag[item]) ? UI_COLOR_INVERSE : UI_COLOR_NORMAL;
/*TODO*///				dt[curr_dt].x = leftoffs + uirotcharwidth * (maxlen-1-sublen) - uirotcharwidth/2;
/*TODO*///				dt[curr_dt].y = topoffs + (3*i+1)*uirotcharheight/2;
/*TODO*///				curr_dt++;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				dt[curr_dt].text = items[item];
/*TODO*///				dt[curr_dt].color = UI_COLOR_NORMAL;
/*TODO*///				dt[curr_dt].x = (uirotwidth - uirotcharwidth * strlen(items[item])) / 2;
/*TODO*///				dt[curr_dt].y = topoffs + (3*i+1)*uirotcharheight/2;
/*TODO*///				curr_dt++;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	i = selected - topitem;
/*TODO*///	if (subitems && subitems[selected] && arrowize_subitem)
/*TODO*///	{
/*TODO*///		if (arrowize_subitem & 1)
/*TODO*///		{
/*TODO*///			int sublen;
/*TODO*///
/*TODO*///			len = strlen(items[selected]);
/*TODO*///
/*TODO*///			dt[curr_dt].text = leftarrow;
/*TODO*///			dt[curr_dt].color = UI_COLOR_NORMAL;
/*TODO*///
/*TODO*///			sublen = strlen(subitems[selected]);
/*TODO*///			if (sublen > maxlen-5-len)
/*TODO*///				sublen = strlen("...");
/*TODO*///
/*TODO*///			dt[curr_dt].x = leftoffs + uirotcharwidth * (maxlen-2 - sublen) - uirotcharwidth/2 - 1;
/*TODO*///			dt[curr_dt].y = topoffs + (3*i+1)*uirotcharheight/2;
/*TODO*///			curr_dt++;
/*TODO*///		}
/*TODO*///		if (arrowize_subitem & 2)
/*TODO*///		{
/*TODO*///			dt[curr_dt].text = rightarrow;
/*TODO*///			dt[curr_dt].color = UI_COLOR_NORMAL;
/*TODO*///			dt[curr_dt].x = leftoffs + uirotcharwidth * (maxlen-1) - uirotcharwidth/2;
/*TODO*///			dt[curr_dt].y = topoffs + (3*i+1)*uirotcharheight/2;
/*TODO*///			curr_dt++;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		dt[curr_dt].text = righthilight;
/*TODO*///		dt[curr_dt].color = UI_COLOR_NORMAL;
/*TODO*///		dt[curr_dt].x = leftoffs + uirotcharwidth * (maxlen-1) - uirotcharwidth/2;
/*TODO*///		dt[curr_dt].y = topoffs + (3*i+1)*uirotcharheight/2;
/*TODO*///		curr_dt++;
/*TODO*///	}
/*TODO*///	dt[curr_dt].text = lefthilight;
/*TODO*///	dt[curr_dt].color = UI_COLOR_NORMAL;
/*TODO*///	dt[curr_dt].x = leftoffs + uirotcharwidth/2;
/*TODO*///	dt[curr_dt].y = topoffs + (3*i+1)*uirotcharheight/2;
/*TODO*///	curr_dt++;
/*TODO*///
/*TODO*///	dt[curr_dt].text = 0;	/* terminate array */
/*TODO*///
/*TODO*///	displaytext(bitmap,dt);
/*TODO*///
/*TODO*///	if (selected_long)
/*TODO*///	{
/*TODO*///		int long_dx;
/*TODO*///		int long_dy;
/*TODO*///		int long_x;
/*TODO*///		int long_y;
/*TODO*///		unsigned long_max;
/*TODO*///
/*TODO*///		long_max = (uirotwidth / uirotcharwidth) - 2;
/*TODO*///		multilinebox_size(&long_dx,&long_dy,subitems[selected],subitems[selected] + strlen(subitems[selected]), long_max);
/*TODO*///
/*TODO*///		long_x = uirotwidth - long_dx;
/*TODO*///		long_y = topoffs + (i+1) * 3*uirotcharheight/2;
/*TODO*///
/*TODO*///		/* if too low display up */
/*TODO*///		if (long_y + long_dy > uirotheight)
/*TODO*///			long_y = topoffs + i * 3*uirotcharheight/2 - long_dy;
/*TODO*///
/*TODO*///		ui_multitextbox_ex(bitmap,subitems[selected],subitems[selected] + strlen(subitems[selected]), long_max, long_x,long_y,long_dx,long_dy, UI_COLOR_NORMAL);
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///void ui_displaymessagewindow(struct mame_bitmap *bitmap,const char *text)
/*TODO*///{
/*TODO*///	struct DisplayText dt[256];
/*TODO*///	int curr_dt;
/*TODO*///	char *c,*c2;
/*TODO*///	int i,len,maxlen,lines;
/*TODO*///	char textcopy[2048];
/*TODO*///	int leftoffs,topoffs;
/*TODO*///	int maxcols,maxrows;
/*TODO*///
/*TODO*///	maxcols = (uirotwidth / uirotcharwidth) - 1;
/*TODO*///	maxrows = (2 * uirotheight - uirotcharheight) / (3 * uirotcharheight);
/*TODO*///
/*TODO*///	/* copy text, calculate max len, count lines, wrap long lines and crop height to fit */
/*TODO*///	maxlen = 0;
/*TODO*///	lines = 0;
/*TODO*///	c = (char *)text;
/*TODO*///	c2 = textcopy;
/*TODO*///	while (*c)
/*TODO*///	{
/*TODO*///		len = 0;
/*TODO*///		while (*c && *c != '\n')
/*TODO*///		{
/*TODO*///			*c2++ = *c++;
/*TODO*///			len++;
/*TODO*///			if (len == maxcols && *c != '\n')
/*TODO*///			{
/*TODO*///				/* attempt word wrap */
/*TODO*///				char *csave = c, *c2save = c2;
/*TODO*///				int lensave = len;
/*TODO*///
/*TODO*///				/* back up to last space or beginning of line */
/*TODO*///				while (*c != ' ' && *c != '\n' && c > text)
/*TODO*///					--c, --c2, --len;
/*TODO*///
/*TODO*///				/* if no space was found, hard wrap instead */
/*TODO*///				if (*c != ' ')
/*TODO*///					c = csave, c2 = c2save, len = lensave;
/*TODO*///				else
/*TODO*///					c++;
/*TODO*///
/*TODO*///				*c2++ = '\n'; /* insert wrap */
/*TODO*///				break;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		if (*c == '\n')
/*TODO*///			*c2++ = *c++;
/*TODO*///
/*TODO*///		if (len > maxlen) maxlen = len;
/*TODO*///
/*TODO*///		lines++;
/*TODO*///		if (lines == maxrows)
/*TODO*///			break;
/*TODO*///	}
/*TODO*///	*c2 = '\0';
/*TODO*///
/*TODO*///	maxlen += 1;
/*TODO*///
/*TODO*///	leftoffs = (uirotwidth - uirotcharwidth * maxlen) / 2;
/*TODO*///	if (leftoffs < 0) leftoffs = 0;
/*TODO*///	topoffs = (uirotheight - (3 * lines + 1) * uirotcharheight / 2) / 2;
/*TODO*///
/*TODO*///	/* black background */
/*TODO*///	ui_drawbox(bitmap,leftoffs,topoffs,maxlen * uirotcharwidth,(3 * lines + 1) * uirotcharheight / 2);
/*TODO*///
/*TODO*///	curr_dt = 0;
/*TODO*///	c = textcopy;
/*TODO*///	i = 0;
/*TODO*///	while (*c)
/*TODO*///	{
/*TODO*///		c2 = c;
/*TODO*///		while (*c && *c != '\n')
/*TODO*///			c++;
/*TODO*///
/*TODO*///		if (*c == '\n')
/*TODO*///		{
/*TODO*///			*c = '\0';
/*TODO*///			c++;
/*TODO*///		}
/*TODO*///
/*TODO*///		if (*c2 == '\t')    /* center text */
/*TODO*///		{
/*TODO*///			c2++;
/*TODO*///			dt[curr_dt].x = (uirotwidth - uirotcharwidth * (c - c2)) / 2;
/*TODO*///		}
/*TODO*///		else
/*TODO*///			dt[curr_dt].x = leftoffs + uirotcharwidth/2;
/*TODO*///
/*TODO*///		dt[curr_dt].text = c2;
/*TODO*///		dt[curr_dt].color = UI_COLOR_NORMAL;
/*TODO*///		dt[curr_dt].y = topoffs + (3*i+1)*uirotcharheight/2;
/*TODO*///		curr_dt++;
/*TODO*///
/*TODO*///		i++;
/*TODO*///	}
/*TODO*///
/*TODO*///	dt[curr_dt].text = 0;	/* terminate array */
/*TODO*///
/*TODO*///	displaytext(bitmap,dt);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///static void showcharset(struct mame_bitmap *bitmap)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	char buf[80];
/*TODO*///	int mode,bank,color,firstdrawn;
/*TODO*///	int palpage;
/*TODO*///	int changed;
/*TODO*///	int total_colors = 0;
/*TODO*///	pen_t *colortable = NULL;
/*TODO*///	int cpx=0,cpy,skip_chars=0,skip_tmap=0;
/*TODO*///	int tilemap_xpos = 0;
/*TODO*///	int tilemap_ypos = 0;
/*TODO*///
/*TODO*///	mode = 0;
/*TODO*///	bank = 0;
/*TODO*///	color = 0;
/*TODO*///	firstdrawn = 0;
/*TODO*///	palpage = 0;
/*TODO*///
/*TODO*///	changed = 1;
/*TODO*///
/*TODO*///	do
/*TODO*///	{
/*TODO*///		static const struct rectangle fullrect = { 0, 10000, 0, 10000 };
/*TODO*///
/*TODO*///		/* mark the whole thing dirty */
/*TODO*///		ui_markdirty(&fullrect);
/*TODO*///
/*TODO*///		switch (mode)
/*TODO*///		{
/*TODO*///			case 0: /* palette or clut */
/*TODO*///			{
/*TODO*///				if (bank == 0)	/* palette */
/*TODO*///				{
/*TODO*///					total_colors = Machine->drv->total_colors;
/*TODO*///					colortable = Machine->pens;
/*TODO*///					strcpy(buf,"PALETTE");
/*TODO*///				}
/*TODO*///				else if (bank == 1)	/* clut */
/*TODO*///				{
/*TODO*///					total_colors = Machine->drv->color_table_len;
/*TODO*///					colortable = Machine->remapped_colortable;
/*TODO*///					strcpy(buf,"CLUT");
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					buf[0] = 0;
/*TODO*///					total_colors = 0;
/*TODO*///					colortable = 0;
/*TODO*///				}
/*TODO*///
/*TODO*///				/*if (changed) -- temporary */
/*TODO*///				{
/*TODO*///					erase_screen(bitmap);
/*TODO*///
/*TODO*///					if (total_colors)
/*TODO*///					{
/*TODO*///						int sx,sy,colors;
/*TODO*///						int column_heading_max;
/*TODO*///						struct bounds;
/*TODO*///
/*TODO*///						colors = total_colors - 256 * palpage;
/*TODO*///						if (colors > 256) colors = 256;
/*TODO*///
/*TODO*///						/* min(colors, 16) */
/*TODO*///						if (colors < 16)
/*TODO*///							column_heading_max = colors;
/*TODO*///						else
/*TODO*///							column_heading_max = 16;
/*TODO*///
/*TODO*///						for (i = 0;i < column_heading_max;i++)
/*TODO*///						{
/*TODO*///							char bf[40];
/*TODO*///
/*TODO*///							sx = 3*uirotcharwidth + (uirotcharwidth*4/3)*(i % 16);
/*TODO*///							sprintf(bf,"%X",i);
/*TODO*///							ui_text(bitmap,bf,sx,2*uirotcharheight);
/*TODO*///							if (16*i < colors)
/*TODO*///							{
/*TODO*///								sy = 3*uirotcharheight + (uirotcharheight)*(i % 16);
/*TODO*///								sprintf(bf,"%3X",i+16*palpage);
/*TODO*///								ui_text(bitmap,bf,0,sy);
/*TODO*///							}
/*TODO*///						}
/*TODO*///
/*TODO*///						for (i = 0;i < colors;i++)
/*TODO*///						{
/*TODO*///							struct rectangle bounds;
/*TODO*///							bounds.min_x = uirotbounds.min_x + 3*uirotcharwidth + (uirotcharwidth*4/3)*(i % 16);
/*TODO*///							bounds.min_y = uirotbounds.min_y + 2*uirotcharheight + (uirotcharheight)*(i / 16) + uirotcharheight;
/*TODO*///							bounds.max_x = bounds.min_x + uirotcharwidth*4/3 - 1;
/*TODO*///							bounds.max_y = bounds.min_y + uirotcharheight - 1;
/*TODO*///							ui_rot2raw_rect(&bounds);
/*TODO*///							fillbitmap(bitmap, colortable[i + 256*palpage], &bounds);
/*TODO*///						}
/*TODO*///					}
/*TODO*///					else
/*TODO*///						ui_text(bitmap,"N/A",3*uirotcharwidth,2*uirotcharheight);
/*TODO*///
/*TODO*///					ui_text(bitmap,buf,0,0);
/*TODO*///					changed = 0;
/*TODO*///				}
/*TODO*///
/*TODO*///				break;
/*TODO*///			}
/*TODO*///			case 1: /* characters */
/*TODO*///			{
/*TODO*///				int crotwidth = (Machine->ui_orientation & ORIENTATION_SWAP_XY) ? Machine->gfx[bank]->height : Machine->gfx[bank]->width;
/*TODO*///				int crotheight = (Machine->ui_orientation & ORIENTATION_SWAP_XY) ? Machine->gfx[bank]->width : Machine->gfx[bank]->height;
/*TODO*///				cpx = uirotwidth / crotwidth;
/*TODO*///				if (cpx == 0) cpx = 1;
/*TODO*///				cpy = (uirotheight - uirotcharheight) / crotheight;
/*TODO*///				if (cpy == 0) cpy = 1;
/*TODO*///				skip_chars = cpx * cpy;
/*TODO*///				/*if (changed) -- temporary */
/*TODO*///				{
/*TODO*///					int flipx,flipy;
/*TODO*///					int lastdrawn=0;
/*TODO*///
/*TODO*///					erase_screen(bitmap);
/*TODO*///
/*TODO*///					/* validity check after char bank change */
/*TODO*///					if (firstdrawn >= Machine->gfx[bank]->total_elements)
/*TODO*///					{
/*TODO*///						firstdrawn = Machine->gfx[bank]->total_elements - skip_chars;
/*TODO*///						if (firstdrawn < 0) firstdrawn = 0;
/*TODO*///					}
/*TODO*///
/*TODO*///					flipx = 0;
/*TODO*///					flipy = 0;
/*TODO*///
/*TODO*///					for (i = 0; i+firstdrawn < Machine->gfx[bank]->total_elements && i<cpx*cpy; i++)
/*TODO*///					{
/*TODO*///						struct rectangle bounds;
/*TODO*///						bounds.min_x = (i % cpx) * crotwidth + uirotbounds.min_x;
/*TODO*///						bounds.min_y = uirotcharheight + (i / cpx) * crotheight + uirotbounds.min_y;
/*TODO*///						bounds.max_x = bounds.min_x + crotwidth - 1;
/*TODO*///						bounds.max_y = bounds.min_y + crotheight - 1;
/*TODO*///						ui_rot2raw_rect(&bounds);
/*TODO*///
/*TODO*///						drawgfx(bitmap,Machine->gfx[bank],
/*TODO*///								i+firstdrawn,color,  /*sprite num, color*/
/*TODO*///								flipx,flipy,bounds.min_x,bounds.min_y,
/*TODO*///								0,Machine->gfx[bank]->colortable ? TRANSPARENCY_NONE : TRANSPARENCY_NONE_RAW,0);
/*TODO*///
/*TODO*///						lastdrawn = i+firstdrawn;
/*TODO*///					}
/*TODO*///
/*TODO*///					sprintf(buf,"GFXSET %d COLOR %2X CODE %X-%X",bank,color,firstdrawn,lastdrawn);
/*TODO*///					ui_text(bitmap,buf,0,0);
/*TODO*///					changed = 0;
/*TODO*///				}
/*TODO*///
/*TODO*///				break;
/*TODO*///			}
/*TODO*///			case 2: /* Tilemaps */
/*TODO*///			{
/*TODO*///				/*if (changed) -- temporary */
/*TODO*///				{
/*TODO*///					UINT32 tilemap_width, tilemap_height;
/*TODO*///					tilemap_nb_size (bank, &tilemap_width, &tilemap_height);
/*TODO*///					while (tilemap_xpos < 0)
/*TODO*///						tilemap_xpos += tilemap_width;
/*TODO*///					tilemap_xpos %= tilemap_width;
/*TODO*///
/*TODO*///					while (tilemap_ypos < 0)
/*TODO*///						tilemap_ypos += tilemap_height;
/*TODO*///					tilemap_ypos %= tilemap_height;
/*TODO*///
/*TODO*///					erase_screen(bitmap);
/*TODO*///					tilemap_nb_draw (bitmap, bank, tilemap_xpos, tilemap_ypos);
/*TODO*///					sprintf(buf, "TILEMAP %d (%dx%d)  X:%d  Y:%d", bank, tilemap_width, tilemap_height, tilemap_xpos, tilemap_ypos);
/*TODO*///					ui_text(bitmap,buf,0,0);
/*TODO*///					changed = 0;
/*TODO*///					skip_tmap = 0;
/*TODO*///				}
/*TODO*///				break;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		update_video_and_audio();
/*TODO*///
/*TODO*///		if (code_pressed(KEYCODE_LCONTROL) || code_pressed(KEYCODE_RCONTROL))
/*TODO*///		{
/*TODO*///			skip_chars = cpx;
/*TODO*///			skip_tmap = 8;
/*TODO*///		}
/*TODO*///		if (code_pressed(KEYCODE_LSHIFT) || code_pressed(KEYCODE_RSHIFT))
/*TODO*///		{
/*TODO*///			skip_chars = 1;
/*TODO*///			skip_tmap = 1;
/*TODO*///		}
/*TODO*///
/*TODO*///
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_RIGHT,8))
/*TODO*///		{
/*TODO*///			int next_bank, next_mode;
/*TODO*///			int jumped;
/*TODO*///
/*TODO*///			next_mode = mode;
/*TODO*///			next_bank = bank+1;
/*TODO*///			do {
/*TODO*///				jumped = 0;
/*TODO*///				switch (next_mode)
/*TODO*///				{
/*TODO*///					case 0:
/*TODO*///						if (next_bank == 2 || Machine->drv->color_table_len == 0)
/*TODO*///						{
/*TODO*///							jumped = 1;
/*TODO*///							next_mode++;
/*TODO*///							next_bank = 0;
/*TODO*///						}
/*TODO*///						break;
/*TODO*///					case 1:
/*TODO*///						if (next_bank == MAX_GFX_ELEMENTS || !Machine->gfx[next_bank])
/*TODO*///						{
/*TODO*///							jumped = 1;
/*TODO*///							next_mode++;
/*TODO*///							next_bank = 0;
/*TODO*///						}
/*TODO*///						break;
/*TODO*///					case 2:
/*TODO*///						if (next_bank == tilemap_count())
/*TODO*///							next_mode = -1;
/*TODO*///						break;
/*TODO*///				}
/*TODO*///			}	while (jumped);
/*TODO*///			if (next_mode != -1 )
/*TODO*///			{
/*TODO*///				bank = next_bank;
/*TODO*///				mode = next_mode;
/*TODO*/////				firstdrawn = 0;
/*TODO*///				changed = 1;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_LEFT,8))
/*TODO*///		{
/*TODO*///			int next_bank, next_mode;
/*TODO*///
/*TODO*///			next_mode = mode;
/*TODO*///			next_bank = bank-1;
/*TODO*///			while(next_bank < 0 && next_mode >= 0)
/*TODO*///			{
/*TODO*///				next_mode = next_mode - 1;
/*TODO*///				switch (next_mode)
/*TODO*///				{
/*TODO*///					case 0:
/*TODO*///						if (Machine->drv->color_table_len == 0)
/*TODO*///							next_bank = 0;
/*TODO*///						else
/*TODO*///							next_bank = 1;
/*TODO*///						break;
/*TODO*///					case 1:
/*TODO*///						next_bank = MAX_GFX_ELEMENTS-1;
/*TODO*///						while (next_bank >= 0 && !Machine->gfx[next_bank])
/*TODO*///							next_bank--;
/*TODO*///						break;
/*TODO*///					case 2:
/*TODO*///						next_bank = tilemap_count() - 1;
/*TODO*///						break;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			if (next_mode != -1 )
/*TODO*///			{
/*TODO*///				bank = next_bank;
/*TODO*///				mode = next_mode;
/*TODO*/////				firstdrawn = 0;
/*TODO*///				changed = 1;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		if (code_pressed_memory_repeat(KEYCODE_PGDN,4))
/*TODO*///		{
/*TODO*///			switch (mode)
/*TODO*///			{
/*TODO*///				case 0:
/*TODO*///				{
/*TODO*///					if (256 * (palpage + 1) < total_colors)
/*TODO*///					{
/*TODO*///						palpage++;
/*TODO*///						changed = 1;
/*TODO*///					}
/*TODO*///					break;
/*TODO*///				}
/*TODO*///				case 1:
/*TODO*///				{
/*TODO*///					if (firstdrawn + skip_chars < Machine->gfx[bank]->total_elements)
/*TODO*///					{
/*TODO*///						firstdrawn += skip_chars;
/*TODO*///						changed = 1;
/*TODO*///					}
/*TODO*///					break;
/*TODO*///				}
/*TODO*///				case 2:
/*TODO*///				{
/*TODO*///					if (skip_tmap)
/*TODO*///						tilemap_ypos -= skip_tmap;
/*TODO*///					else
/*TODO*///						tilemap_ypos -= bitmap->height/4;
/*TODO*///					changed = 1;
/*TODO*///					break;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		if (code_pressed_memory_repeat(KEYCODE_PGUP,4))
/*TODO*///		{
/*TODO*///			switch (mode)
/*TODO*///			{
/*TODO*///				case 0:
/*TODO*///				{
/*TODO*///					if (palpage > 0)
/*TODO*///					{
/*TODO*///						palpage--;
/*TODO*///						changed = 1;
/*TODO*///					}
/*TODO*///					break;
/*TODO*///				}
/*TODO*///				case 1:
/*TODO*///				{
/*TODO*///					firstdrawn -= skip_chars;
/*TODO*///					if (firstdrawn < 0) firstdrawn = 0;
/*TODO*///					changed = 1;
/*TODO*///					break;
/*TODO*///				}
/*TODO*///				case 2:
/*TODO*///				{
/*TODO*///					if (skip_tmap)
/*TODO*///						tilemap_ypos += skip_tmap;
/*TODO*///					else
/*TODO*///						tilemap_ypos += bitmap->height/4;
/*TODO*///					changed = 1;
/*TODO*///					break;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		if (code_pressed_memory_repeat(KEYCODE_D,4))
/*TODO*///		{
/*TODO*///			switch (mode)
/*TODO*///			{
/*TODO*///				case 2:
/*TODO*///				{
/*TODO*///					if (skip_tmap)
/*TODO*///						tilemap_xpos -= skip_tmap;
/*TODO*///					else
/*TODO*///						tilemap_xpos -= bitmap->width/4;
/*TODO*///					changed = 1;
/*TODO*///					break;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		if (code_pressed_memory_repeat(KEYCODE_G,4))
/*TODO*///		{
/*TODO*///			switch (mode)
/*TODO*///			{
/*TODO*///				case 2:
/*TODO*///				{
/*TODO*///					if (skip_tmap)
/*TODO*///						tilemap_xpos += skip_tmap;
/*TODO*///					else
/*TODO*///						tilemap_xpos += bitmap->width/4;
/*TODO*///					changed = 1;
/*TODO*///					break;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_UP,6))
/*TODO*///		{
/*TODO*///			switch (mode)
/*TODO*///			{
/*TODO*///				case 1:
/*TODO*///				{
/*TODO*///					if (color < Machine->gfx[bank]->total_colors - 1)
/*TODO*///					{
/*TODO*///						color++;
/*TODO*///						changed = 1;
/*TODO*///					}
/*TODO*///					break;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_DOWN,6))
/*TODO*///		{
/*TODO*///			switch (mode)
/*TODO*///			{
/*TODO*///				case 0:
/*TODO*///					break;
/*TODO*///				case 1:
/*TODO*///				{
/*TODO*///					if (color > 0)
/*TODO*///					{
/*TODO*///						color--;
/*TODO*///						changed = 1;
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_SNAPSHOT))
/*TODO*///			save_screen_snapshot(bitmap);
/*TODO*///	} while (!input_ui_pressed(IPT_UI_SHOW_GFX) &&
/*TODO*///			!input_ui_pressed(IPT_UI_CANCEL));
/*TODO*///
/*TODO*///	schedule_full_refresh();
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///static int switchmenu(struct mame_bitmap *bitmap, int selected, UINT32 switch_name, UINT32 switch_setting)
/*TODO*///{
/*TODO*///	const char *menu_item[128];
/*TODO*///	const char *menu_subitem[128];
/*TODO*///	struct InputPort *entry[128];
/*TODO*///	char flag[40];
/*TODO*///	int i,sel;
/*TODO*///	struct InputPort *in;
/*TODO*///	int total;
/*TODO*///	int arrowize;
/*TODO*///
/*TODO*///
/*TODO*///	sel = selected - 1;
/*TODO*///
/*TODO*///
/*TODO*///	in = Machine->input_ports;
/*TODO*///
/*TODO*///	total = 0;
/*TODO*///	while (in->type != IPT_END)
/*TODO*///	{
/*TODO*///		if ((in->type & ~IPF_MASK) == switch_name && input_port_name(in) != 0 &&
/*TODO*///				(in->type & IPF_UNUSED) == 0 &&
/*TODO*///				!(!options.cheat && (in->type & IPF_CHEAT)))
/*TODO*///		{
/*TODO*///			entry[total] = in;
/*TODO*///			menu_item[total] = input_port_name(in);
/*TODO*///
/*TODO*///			total++;
/*TODO*///		}
/*TODO*///
/*TODO*///		in++;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (total == 0) return 0;
/*TODO*///
/*TODO*///	menu_item[total] = ui_getstring (UI_returntomain);
/*TODO*///	menu_item[total + 1] = 0;	/* terminate array */
/*TODO*///	total++;
/*TODO*///
/*TODO*///
/*TODO*///	for (i = 0;i < total;i++)
/*TODO*///	{
/*TODO*///		flag[i] = 0; /* TODO: flag the dip if it's not the real default */
/*TODO*///		if (i < total - 1)
/*TODO*///		{
/*TODO*///			in = entry[i] + 1;
/*TODO*///			while ((in->type & ~IPF_MASK) == switch_setting &&
/*TODO*///					in->default_value != entry[i]->default_value)
/*TODO*///				in++;
/*TODO*///
/*TODO*///			if ((in->type & ~IPF_MASK) != switch_setting)
/*TODO*///				menu_subitem[i] = ui_getstring (UI_INVALID);
/*TODO*///			else menu_subitem[i] = input_port_name(in);
/*TODO*///		}
/*TODO*///		else menu_subitem[i] = 0;	/* no subitem */
/*TODO*///	}
/*TODO*///
/*TODO*///	arrowize = 0;
/*TODO*///	if (sel < total - 1)
/*TODO*///	{
/*TODO*///		in = entry[sel] + 1;
/*TODO*///		while ((in->type & ~IPF_MASK) == switch_setting &&
/*TODO*///				in->default_value != entry[sel]->default_value)
/*TODO*///			in++;
/*TODO*///
/*TODO*///		if ((in->type & ~IPF_MASK) != switch_setting)
/*TODO*///			/* invalid setting: revert to a valid one */
/*TODO*///			arrowize |= 1;
/*TODO*///		else
/*TODO*///		{
/*TODO*///			if (((in-1)->type & ~IPF_MASK) == switch_setting &&
/*TODO*///					!(!options.cheat && ((in-1)->type & IPF_CHEAT)))
/*TODO*///				arrowize |= 1;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	if (sel < total - 1)
/*TODO*///	{
/*TODO*///		in = entry[sel] + 1;
/*TODO*///		while ((in->type & ~IPF_MASK) == switch_setting &&
/*TODO*///				in->default_value != entry[sel]->default_value)
/*TODO*///			in++;
/*TODO*///
/*TODO*///		if ((in->type & ~IPF_MASK) != switch_setting)
/*TODO*///			/* invalid setting: revert to a valid one */
/*TODO*///			arrowize |= 2;
/*TODO*///		else
/*TODO*///		{
/*TODO*///			if (((in+1)->type & ~IPF_MASK) == switch_setting &&
/*TODO*///					!(!options.cheat && ((in+1)->type & IPF_CHEAT)))
/*TODO*///				arrowize |= 2;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	ui_displaymenu(bitmap,menu_item,menu_subitem,flag,sel,arrowize);
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
/*TODO*///		sel = (sel + 1) % total;
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_UP,8))
/*TODO*///		sel = (sel + total - 1) % total;
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_RIGHT,8))
/*TODO*///	{
/*TODO*///		if (sel < total - 1)
/*TODO*///		{
/*TODO*///			in = entry[sel] + 1;
/*TODO*///			while ((in->type & ~IPF_MASK) == switch_setting &&
/*TODO*///					in->default_value != entry[sel]->default_value)
/*TODO*///				in++;
/*TODO*///
/*TODO*///			if ((in->type & ~IPF_MASK) != switch_setting)
/*TODO*///				/* invalid setting: revert to a valid one */
/*TODO*///				entry[sel]->default_value = (entry[sel]+1)->default_value & entry[sel]->mask;
/*TODO*///			else
/*TODO*///			{
/*TODO*///				if (((in+1)->type & ~IPF_MASK) == switch_setting &&
/*TODO*///						!(!options.cheat && ((in+1)->type & IPF_CHEAT)))
/*TODO*///					entry[sel]->default_value = (in+1)->default_value & entry[sel]->mask;
/*TODO*///			}
/*TODO*///
/*TODO*///			/* tell updatescreen() to clean after us (in case the window changes size) */
/*TODO*///			schedule_full_refresh();
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_LEFT,8))
/*TODO*///	{
/*TODO*///		if (sel < total - 1)
/*TODO*///		{
/*TODO*///			in = entry[sel] + 1;
/*TODO*///			while ((in->type & ~IPF_MASK) == switch_setting &&
/*TODO*///					in->default_value != entry[sel]->default_value)
/*TODO*///				in++;
/*TODO*///
/*TODO*///			if ((in->type & ~IPF_MASK) != switch_setting)
/*TODO*///				/* invalid setting: revert to a valid one */
/*TODO*///				entry[sel]->default_value = (entry[sel]+1)->default_value & entry[sel]->mask;
/*TODO*///			else
/*TODO*///			{
/*TODO*///				if (((in-1)->type & ~IPF_MASK) == switch_setting &&
/*TODO*///						!(!options.cheat && ((in-1)->type & IPF_CHEAT)))
/*TODO*///					entry[sel]->default_value = (in-1)->default_value & entry[sel]->mask;
/*TODO*///			}
/*TODO*///
/*TODO*///			/* tell updatescreen() to clean after us (in case the window changes size) */
/*TODO*///			schedule_full_refresh();
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*///	{
/*TODO*///		if (sel == total - 1) sel = -1;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///		sel = -1;
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///		sel = -2;
/*TODO*///
/*TODO*///	if (sel == -1 || sel == -2)
/*TODO*///	{
/*TODO*///		schedule_full_refresh();
/*TODO*///	}
/*TODO*///
/*TODO*///	return sel + 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///static int setdipswitches(struct mame_bitmap *bitmap, int selected)
/*TODO*///{
/*TODO*///	return switchmenu(bitmap, selected, IPT_DIPSWITCH_NAME, IPT_DIPSWITCH_SETTING);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///static int setconfiguration(struct mame_bitmap *bitmap, int selected)
/*TODO*///{
/*TODO*///	return switchmenu(bitmap, selected, IPT_CONFIG_NAME, IPT_CONFIG_SETTING);
/*TODO*///}
/*TODO*///#endif /* MESS */
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////* This flag is used for record OR sequence of key/joy */
/*TODO*////* when is !=0 the first sequence is record, otherwise the first free */
/*TODO*////* it's used byt setdefkeysettings, setdefjoysettings, setkeysettings, setjoysettings */
/*TODO*///static int record_first_insert = 1;
/*TODO*///
/*TODO*///static char menu_subitem_buffer[500][96];
/*TODO*///
/*TODO*///static int setdefcodesettings(struct mame_bitmap *bitmap,int selected)
/*TODO*///{
/*TODO*///	const char *menu_item[500];
/*TODO*///	const char *menu_subitem[500];
/*TODO*///	struct ipd *entry[500];
/*TODO*///	char flag[500];
/*TODO*///	int i,sel;
/*TODO*///	struct ipd *in;
/*TODO*///	int total;
/*TODO*///	extern struct ipd inputport_defaults[];
/*TODO*///
/*TODO*///	sel = selected - 1;
/*TODO*///
/*TODO*///
/*TODO*///	if (Machine->input_ports == 0)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	in = inputport_defaults;
/*TODO*///
/*TODO*///	total = 0;
/*TODO*///	while (in->type != IPT_END)
/*TODO*///	{
/*TODO*///		if (in->name != 0  && (in->type & ~IPF_MASK) != IPT_UNKNOWN && (in->type & ~IPF_MASK) != IPT_OSD_RESERVED && (in->type & IPF_UNUSED) == 0
/*TODO*///			&& !(!options.cheat && (in->type & IPF_CHEAT)))
/*TODO*///		{
/*TODO*///			entry[total] = in;
/*TODO*///			menu_item[total] = in->name;
/*TODO*///
/*TODO*///			total++;
/*TODO*///		}
/*TODO*///
/*TODO*///		in++;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (total == 0) return 0;
/*TODO*///
/*TODO*///	menu_item[total] = ui_getstring (UI_returntomain);
/*TODO*///	menu_item[total + 1] = 0;	/* terminate array */
/*TODO*///	total++;
/*TODO*///
/*TODO*///	for (i = 0;i < total;i++)
/*TODO*///	{
/*TODO*///		if (i < total - 1)
/*TODO*///		{
/*TODO*///			seq_name(&entry[i]->seq,menu_subitem_buffer[i],sizeof(menu_subitem_buffer[0]));
/*TODO*///			menu_subitem[i] = menu_subitem_buffer[i];
/*TODO*///		} else
/*TODO*///			menu_subitem[i] = 0;	/* no subitem */
/*TODO*///		flag[i] = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (sel > SEL_MASK)   /* are we waiting for a new key? */
/*TODO*///	{
/*TODO*///		int ret;
/*TODO*///
/*TODO*///		menu_subitem[sel & SEL_MASK] = "    ";
/*TODO*///		ui_displaymenu(bitmap,menu_item,menu_subitem,flag,sel & SEL_MASK,3);
/*TODO*///
/*TODO*///		ret = seq_read_async(&entry[sel & SEL_MASK]->seq,record_first_insert);
/*TODO*///
/*TODO*///		if (ret >= 0)
/*TODO*///		{
/*TODO*///			sel &= SEL_MASK;
/*TODO*///
/*TODO*///			if (ret > 0 || seq_get_1(&entry[sel]->seq) == CODE_NONE)
/*TODO*///			{
/*TODO*///				seq_set_1(&entry[sel]->seq,CODE_NONE);
/*TODO*///				ret = 1;
/*TODO*///			}
/*TODO*///
/*TODO*///			/* tell updatescreen() to clean after us (in case the window changes size) */
/*TODO*///			schedule_full_refresh();
/*TODO*///
/*TODO*///			record_first_insert = ret != 0;
/*TODO*///		}
/*TODO*///
/*TODO*///		init_analog_seq();
/*TODO*///
/*TODO*///		return sel + 1;
/*TODO*///	}
/*TODO*///
/*TODO*///
/*TODO*///	ui_displaymenu(bitmap,menu_item,menu_subitem,flag,sel,0);
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
/*TODO*///	{
/*TODO*///		sel = (sel + 1) % total;
/*TODO*///		record_first_insert = 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_UP,8))
/*TODO*///	{
/*TODO*///		sel = (sel + total - 1) % total;
/*TODO*///		record_first_insert = 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*///	{
/*TODO*///		if (sel == total - 1) sel = -1;
/*TODO*///		else
/*TODO*///		{
/*TODO*///			seq_read_async_start();
/*TODO*///
/*TODO*///			sel |= 1 << SEL_BITS;	/* we'll ask for a key */
/*TODO*///
/*TODO*///			/* tell updatescreen() to clean after us (in case the window changes size) */
/*TODO*///			schedule_full_refresh();
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///		sel = -1;
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///		sel = -2;
/*TODO*///
/*TODO*///	if (sel == -1 || sel == -2)
/*TODO*///	{
/*TODO*///		/* tell updatescreen() to clean after us */
/*TODO*///		schedule_full_refresh();
/*TODO*///
/*TODO*///		record_first_insert = 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	return sel + 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///static int setcodesettings(struct mame_bitmap *bitmap,int selected)
/*TODO*///{
/*TODO*///	const char *menu_item[500];
/*TODO*///	const char *menu_subitem[500];
/*TODO*///	struct InputPort *entry[500];
/*TODO*///	char flag[500];
/*TODO*///	int i,sel;
/*TODO*///	struct InputPort *in;
/*TODO*///	int total;
/*TODO*///
/*TODO*///
/*TODO*///	sel = selected - 1;
/*TODO*///
/*TODO*///
/*TODO*///	if (Machine->input_ports == 0)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	in = Machine->input_ports;
/*TODO*///
/*TODO*///	total = 0;
/*TODO*///	while (in->type != IPT_END)
/*TODO*///	{
/*TODO*///		if (input_port_name(in) != 0 && seq_get_1(&in->seq) != CODE_NONE && (in->type & ~IPF_MASK) != IPT_UNKNOWN && (in->type & ~IPF_MASK) != IPT_OSD_RESERVED)
/*TODO*///		{
/*TODO*///			entry[total] = in;
/*TODO*///			menu_item[total] = input_port_name(in);
/*TODO*///
/*TODO*///			total++;
/*TODO*///		}
/*TODO*///
/*TODO*///		in++;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (total == 0) return 0;
/*TODO*///
/*TODO*///	menu_item[total] = ui_getstring (UI_returntomain);
/*TODO*///	menu_item[total + 1] = 0;	/* terminate array */
/*TODO*///	total++;
/*TODO*///
/*TODO*///	for (i = 0;i < total;i++)
/*TODO*///	{
/*TODO*///		if (i < total - 1)
/*TODO*///		{
/*TODO*///			seq_name(input_port_seq(entry[i]),menu_subitem_buffer[i],sizeof(menu_subitem_buffer[0]));
/*TODO*///			menu_subitem[i] = menu_subitem_buffer[i];
/*TODO*///
/*TODO*///			/* If the key isn't the default, flag it */
/*TODO*///			if (seq_get_1(&entry[i]->seq) != CODE_DEFAULT)
/*TODO*///				flag[i] = 1;
/*TODO*///			else
/*TODO*///				flag[i] = 0;
/*TODO*///
/*TODO*///		} else
/*TODO*///			menu_subitem[i] = 0;	/* no subitem */
/*TODO*///	}
/*TODO*///
/*TODO*///	if (sel > SEL_MASK)   /* are we waiting for a new key? */
/*TODO*///	{
/*TODO*///		int ret;
/*TODO*///
/*TODO*///		menu_subitem[sel & SEL_MASK] = "    ";
/*TODO*///		ui_displaymenu(bitmap,menu_item,menu_subitem,flag,sel & SEL_MASK,3);
/*TODO*///
/*TODO*///		ret = seq_read_async(&entry[sel & SEL_MASK]->seq,record_first_insert);
/*TODO*///
/*TODO*///		if (ret >= 0)
/*TODO*///		{
/*TODO*///			sel &= SEL_MASK;
/*TODO*///
/*TODO*///			if (ret > 0 || seq_get_1(&entry[sel]->seq) == CODE_NONE)
/*TODO*///			{
/*TODO*///				seq_set_1(&entry[sel]->seq, CODE_DEFAULT);
/*TODO*///				ret = 1;
/*TODO*///			}
/*TODO*///
/*TODO*///			/* tell updatescreen() to clean after us (in case the window changes size) */
/*TODO*///			schedule_full_refresh();
/*TODO*///
/*TODO*///			record_first_insert = ret != 0;
/*TODO*///		}
/*TODO*///
/*TODO*///		init_analog_seq();
/*TODO*///
/*TODO*///		return sel + 1;
/*TODO*///	}
/*TODO*///
/*TODO*///
/*TODO*///	ui_displaymenu(bitmap,menu_item,menu_subitem,flag,sel,0);
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
/*TODO*///	{
/*TODO*///		sel = (sel + 1) % total;
/*TODO*///		record_first_insert = 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_UP,8))
/*TODO*///	{
/*TODO*///		sel = (sel + total - 1) % total;
/*TODO*///		record_first_insert = 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*///	{
/*TODO*///		if (sel == total - 1) sel = -1;
/*TODO*///		else
/*TODO*///		{
/*TODO*///			seq_read_async_start();
/*TODO*///
/*TODO*///			sel |= 1 << SEL_BITS;	/* we'll ask for a key */
/*TODO*///
/*TODO*///			/* tell updatescreen() to clean after us (in case the window changes size) */
/*TODO*///			schedule_full_refresh();
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///		sel = -1;
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///		sel = -2;
/*TODO*///
/*TODO*///	if (sel == -1 || sel == -2)
/*TODO*///	{
/*TODO*///		schedule_full_refresh();
/*TODO*///
/*TODO*///		record_first_insert = 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	return sel + 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static int calibratejoysticks(struct mame_bitmap *bitmap,int selected)
/*TODO*///{
/*TODO*///	const char *msg;
/*TODO*///	static char buf[2048];
/*TODO*///	int sel;
/*TODO*///	static int calibration_started = 0;
/*TODO*///
/*TODO*///	sel = selected - 1;
/*TODO*///
/*TODO*///	if (calibration_started == 0)
/*TODO*///	{
/*TODO*///		osd_joystick_start_calibration();
/*TODO*///		calibration_started = 1;
/*TODO*///		strcpy (buf, "");
/*TODO*///	}
/*TODO*///
/*TODO*///	if (sel > SEL_MASK) /* Waiting for the user to acknowledge joystick movement */
/*TODO*///	{
/*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///		{
/*TODO*///			calibration_started = 0;
/*TODO*///			sel = -1;
/*TODO*///		}
/*TODO*///		else if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*///		{
/*TODO*///			osd_joystick_calibrate();
/*TODO*///			sel &= SEL_MASK;
/*TODO*///		}
/*TODO*///
/*TODO*///		ui_displaymessagewindow(bitmap,buf);
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		msg = osd_joystick_calibrate_next();
/*TODO*///		schedule_full_refresh();
/*TODO*///		if (msg == 0)
/*TODO*///		{
/*TODO*///			calibration_started = 0;
/*TODO*///			osd_joystick_end_calibration();
/*TODO*///			sel = -1;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			strcpy (buf, msg);
/*TODO*///			ui_displaymessagewindow(bitmap,buf);
/*TODO*///			sel |= 1 << SEL_BITS;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///		sel = -2;
/*TODO*///
/*TODO*///	if (sel == -1 || sel == -2)
/*TODO*///	{
/*TODO*///		schedule_full_refresh();
/*TODO*///	}
/*TODO*///
/*TODO*///	return sel + 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static int settraksettings(struct mame_bitmap *bitmap,int selected)
/*TODO*///{
/*TODO*///	const char *menu_item[40];
/*TODO*///	const char *menu_subitem[40];
/*TODO*///	struct InputPort *entry[40];
/*TODO*///	int i,sel;
/*TODO*///	struct InputPort *in;
/*TODO*///	int total,total2;
/*TODO*///	int arrowize;
/*TODO*///
/*TODO*///
/*TODO*///	sel = selected - 1;
/*TODO*///
/*TODO*///
/*TODO*///	if (Machine->input_ports == 0)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	in = Machine->input_ports;
/*TODO*///
/*TODO*///	/* Count the total number of analog controls */
/*TODO*///	total = 0;
/*TODO*///	while (in->type != IPT_END)
/*TODO*///	{
/*TODO*///		if (((in->type & 0xff) > IPT_ANALOG_START) && ((in->type & 0xff) < IPT_ANALOG_END)
/*TODO*///				&& !(!options.cheat && (in->type & IPF_CHEAT)))
/*TODO*///		{
/*TODO*///			entry[total] = in;
/*TODO*///			total++;
/*TODO*///		}
/*TODO*///		in++;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (total == 0) return 0;
/*TODO*///
/*TODO*///	/* Each analog control has 3 entries - key & joy delta, reverse, sensitivity */
/*TODO*///
/*TODO*///#define ENTRIES 3
/*TODO*///
/*TODO*///	total2 = total * ENTRIES;
/*TODO*///
/*TODO*///	menu_item[total2] = ui_getstring (UI_returntomain);
/*TODO*///	menu_item[total2 + 1] = 0;	/* terminate array */
/*TODO*///	total2++;
/*TODO*///
/*TODO*///	arrowize = 0;
/*TODO*///	for (i = 0;i < total2;i++)
/*TODO*///	{
/*TODO*///		if (i < total2 - 1)
/*TODO*///		{
/*TODO*///			char label[30][40];
/*TODO*///			char setting[30][40];
/*TODO*///			int sensitivity,delta;
/*TODO*///			int reverse;
/*TODO*///
/*TODO*///			strcpy (label[i], input_port_name(entry[i/ENTRIES]));
/*TODO*///			sensitivity = IP_GET_SENSITIVITY(entry[i/ENTRIES]);
/*TODO*///			delta = IP_GET_DELTA(entry[i/ENTRIES]);
/*TODO*///			reverse = (entry[i/ENTRIES]->type & IPF_REVERSE);
/*TODO*///
/*TODO*///			strcat (label[i], " ");
/*TODO*///			switch (i%ENTRIES)
/*TODO*///			{
/*TODO*///				case 0:
/*TODO*///					strcat (label[i], ui_getstring (UI_keyjoyspeed));
/*TODO*///					sprintf(setting[i],"%d",delta);
/*TODO*///					if (i == sel) arrowize = 3;
/*TODO*///					break;
/*TODO*///				case 1:
/*TODO*///					strcat (label[i], ui_getstring (UI_reverse));
/*TODO*///					if (reverse)
/*TODO*///						strcpy(setting[i],ui_getstring (UI_on));
/*TODO*///					else
/*TODO*///						strcpy(setting[i],ui_getstring (UI_off));
/*TODO*///					if (i == sel) arrowize = 3;
/*TODO*///					break;
/*TODO*///				case 2:
/*TODO*///					strcat (label[i], ui_getstring (UI_sensitivity));
/*TODO*///					sprintf(setting[i],"%3d%%",sensitivity);
/*TODO*///					if (i == sel) arrowize = 3;
/*TODO*///					break;
/*TODO*///			}
/*TODO*///
/*TODO*///			menu_item[i] = label[i];
/*TODO*///			menu_subitem[i] = setting[i];
/*TODO*///
/*TODO*///			in++;
/*TODO*///		}
/*TODO*///		else menu_subitem[i] = 0;	/* no subitem */
/*TODO*///	}
/*TODO*///
/*TODO*///	ui_displaymenu(bitmap,menu_item,menu_subitem,0,sel,arrowize);
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
/*TODO*///		sel = (sel + 1) % total2;
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_UP,8))
/*TODO*///		sel = (sel + total2 - 1) % total2;
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_LEFT,8))
/*TODO*///	{
/*TODO*///		if(sel != total2 - 1)
/*TODO*///		{
/*TODO*///			if ((sel % ENTRIES) == 0)
/*TODO*///			/* keyboard/joystick delta */
/*TODO*///			{
/*TODO*///				int val = IP_GET_DELTA(entry[sel/ENTRIES]);
/*TODO*///
/*TODO*///				val --;
/*TODO*///				if (val < 1) val = 1;
/*TODO*///				IP_SET_DELTA(entry[sel/ENTRIES],val);
/*TODO*///			}
/*TODO*///			else if ((sel % ENTRIES) == 1)
/*TODO*///			/* reverse */
/*TODO*///			{
/*TODO*///				int reverse = entry[sel/ENTRIES]->type & IPF_REVERSE;
/*TODO*///				if (reverse)
/*TODO*///					reverse=0;
/*TODO*///				else
/*TODO*///					reverse=IPF_REVERSE;
/*TODO*///				entry[sel/ENTRIES]->type &= ~IPF_REVERSE;
/*TODO*///				entry[sel/ENTRIES]->type |= reverse;
/*TODO*///			}
/*TODO*///			else if ((sel % ENTRIES) == 2)
/*TODO*///			/* sensitivity */
/*TODO*///			{
/*TODO*///				int val = IP_GET_SENSITIVITY(entry[sel/ENTRIES]);
/*TODO*///
/*TODO*///				val --;
/*TODO*///				if (val < 1) val = 1;
/*TODO*///				IP_SET_SENSITIVITY(entry[sel/ENTRIES],val);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_RIGHT,8))
/*TODO*///	{
/*TODO*///		if(sel != total2 - 1)
/*TODO*///		{
/*TODO*///			if ((sel % ENTRIES) == 0)
/*TODO*///			/* keyboard/joystick delta */
/*TODO*///			{
/*TODO*///				int val = IP_GET_DELTA(entry[sel/ENTRIES]);
/*TODO*///
/*TODO*///				val ++;
/*TODO*///				if (val > 255) val = 255;
/*TODO*///				IP_SET_DELTA(entry[sel/ENTRIES],val);
/*TODO*///			}
/*TODO*///			else if ((sel % ENTRIES) == 1)
/*TODO*///			/* reverse */
/*TODO*///			{
/*TODO*///				int reverse = entry[sel/ENTRIES]->type & IPF_REVERSE;
/*TODO*///				if (reverse)
/*TODO*///					reverse=0;
/*TODO*///				else
/*TODO*///					reverse=IPF_REVERSE;
/*TODO*///				entry[sel/ENTRIES]->type &= ~IPF_REVERSE;
/*TODO*///				entry[sel/ENTRIES]->type |= reverse;
/*TODO*///			}
/*TODO*///			else if ((sel % ENTRIES) == 2)
/*TODO*///			/* sensitivity */
/*TODO*///			{
/*TODO*///				int val = IP_GET_SENSITIVITY(entry[sel/ENTRIES]);
/*TODO*///
/*TODO*///				val ++;
/*TODO*///				if (val > 255) val = 255;
/*TODO*///				IP_SET_SENSITIVITY(entry[sel/ENTRIES],val);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*///	{
/*TODO*///		if (sel == total2 - 1) sel = -1;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///		sel = -1;
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///		sel = -2;
/*TODO*///
/*TODO*///	if (sel == -1 || sel == -2)
/*TODO*///	{
/*TODO*///		schedule_full_refresh();
/*TODO*///	}
/*TODO*///
/*TODO*///	return sel + 1;
/*TODO*///}
/*TODO*///
/*TODO*///#ifndef MESS
/*TODO*///static int mame_stats(struct mame_bitmap *bitmap,int selected)
/*TODO*///{
/*TODO*///	char temp[10];
/*TODO*///	char buf[2048];
/*TODO*///	int sel, i;
/*TODO*///
/*TODO*///
/*TODO*///	sel = selected - 1;
/*TODO*///
/*TODO*///	buf[0] = 0;
/*TODO*///
/*TODO*///	if (dispensed_tickets)
/*TODO*///	{
/*TODO*///		strcat(buf, ui_getstring (UI_tickets));
/*TODO*///		strcat(buf, ": ");
/*TODO*///		sprintf(temp, "%d\n\n", dispensed_tickets);
/*TODO*///		strcat(buf, temp);
/*TODO*///	}
/*TODO*///
/*TODO*///	for (i=0; i<COIN_COUNTERS; i++)
/*TODO*///	{
/*TODO*///		strcat(buf, ui_getstring (UI_coin));
/*TODO*///		sprintf(temp, " %c: ", i+'A');
/*TODO*///		strcat(buf, temp);
/*TODO*///		if (!coins[i])
/*TODO*///			strcat (buf, ui_getstring (UI_NA));
/*TODO*///		else
/*TODO*///		{
/*TODO*///			sprintf (temp, "%d", coins[i]);
/*TODO*///			strcat (buf, temp);
/*TODO*///		}
/*TODO*///		if (coinlockedout[i])
/*TODO*///		{
/*TODO*///			strcat(buf, " ");
/*TODO*///			strcat(buf, ui_getstring (UI_locked));
/*TODO*///			strcat(buf, "\n");
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			strcat(buf, "\n");
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	{
/*TODO*///		/* menu system, use the normal menu keys */
/*TODO*///		strcat(buf,"\n\t");
/*TODO*///		strcat(buf,ui_getstring (UI_lefthilight));
/*TODO*///		strcat(buf," ");
/*TODO*///		strcat(buf,ui_getstring (UI_returntomain));
/*TODO*///		strcat(buf," ");
/*TODO*///		strcat(buf,ui_getstring (UI_righthilight));
/*TODO*///
/*TODO*///		ui_displaymessagewindow(bitmap,buf);
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*///			sel = -1;
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///			sel = -1;
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///			sel = -2;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (sel == -1 || sel == -2)
/*TODO*///	{
/*TODO*///		schedule_full_refresh();
/*TODO*///	}
/*TODO*///
/*TODO*///	return sel + 1;
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///int showcopyright(struct mame_bitmap *bitmap)
/*TODO*///{
/*TODO*///	int done;
/*TODO*///	char buf[1000];
/*TODO*///	char buf2[256];
/*TODO*///
/*TODO*///	strcpy (buf, ui_getstring(UI_copyright1));
/*TODO*///	strcat (buf, "\n\n");
/*TODO*///	sprintf(buf2, ui_getstring(UI_copyright2), Machine->gamedrv->description);
/*TODO*///	strcat (buf, buf2);
/*TODO*///	strcat (buf, "\n\n");
/*TODO*///	strcat (buf, ui_getstring(UI_copyright3));
/*TODO*///
/*TODO*///	setup_selected = -1;////
/*TODO*///	done = 0;
/*TODO*///
/*TODO*///	do
/*TODO*///	{
/*TODO*///		erase_screen(bitmap);
/*TODO*///		ui_drawbox(bitmap,0,0,uirotwidth,uirotheight);
/*TODO*///		ui_displaymessagewindow(bitmap,buf);
/*TODO*///
/*TODO*///		update_video_and_audio();
/*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///		{
/*TODO*///			setup_selected = 0;////
/*TODO*///			return 1;
/*TODO*///		}
/*TODO*///		if (keyboard_pressed_memory(KEYCODE_O) ||
/*TODO*///				input_ui_pressed(IPT_UI_LEFT))
/*TODO*///			done = 1;
/*TODO*///		if (done == 1 && (keyboard_pressed_memory(KEYCODE_K) ||
/*TODO*///				input_ui_pressed(IPT_UI_RIGHT)))
/*TODO*///			done = 2;
/*TODO*///	} while (done < 2);
/*TODO*///
/*TODO*///	setup_selected = 0;////
/*TODO*///	erase_screen(bitmap);
/*TODO*///	update_video_and_audio();
/*TODO*///
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///static int displaygameinfo(struct mame_bitmap *bitmap,int selected)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	char buf[2048];
/*TODO*///	char buf2[32];
/*TODO*///	int sel;
/*TODO*///
/*TODO*///
/*TODO*///	sel = selected - 1;
/*TODO*///
/*TODO*///
/*TODO*///	sprintf(buf,"%s\n%s %s\n\n%s:\n",Machine->gamedrv->description,Machine->gamedrv->year,Machine->gamedrv->manufacturer,
/*TODO*///		ui_getstring (UI_cpu));
/*TODO*///	i = 0;
/*TODO*///	while (i < MAX_CPU && Machine->drv->cpu[i].cpu_type)
/*TODO*///	{
/*TODO*///
/*TODO*///		if (Machine->drv->cpu[i].cpu_clock >= 1000000)
/*TODO*///			sprintf(&buf[strlen(buf)],"%s %d.%06d MHz",
/*TODO*///					cputype_name(Machine->drv->cpu[i].cpu_type),
/*TODO*///					Machine->drv->cpu[i].cpu_clock / 1000000,
/*TODO*///					Machine->drv->cpu[i].cpu_clock % 1000000);
/*TODO*///		else
/*TODO*///			sprintf(&buf[strlen(buf)],"%s %d.%03d kHz",
/*TODO*///					cputype_name(Machine->drv->cpu[i].cpu_type),
/*TODO*///					Machine->drv->cpu[i].cpu_clock / 1000,
/*TODO*///					Machine->drv->cpu[i].cpu_clock % 1000);
/*TODO*///
/*TODO*///		if (Machine->drv->cpu[i].cpu_flags & CPU_AUDIO_CPU)
/*TODO*///		{
/*TODO*///			sprintf (buf2, " (%s)", ui_getstring (UI_sound_lc));
/*TODO*///			strcat(buf, buf2);
/*TODO*///		}
/*TODO*///
/*TODO*///		strcat(buf,"\n");
/*TODO*///
/*TODO*///		i++;
/*TODO*///	}
/*TODO*///
/*TODO*///	sprintf (buf2, "\n%s", ui_getstring (UI_sound));
/*TODO*///	strcat (buf, buf2);
/*TODO*///	if (Machine->drv->sound_attributes & SOUND_SUPPORTS_STEREO)
/*TODO*///		sprintf(&buf[strlen(buf)]," (%s)", ui_getstring (UI_stereo));
/*TODO*///	strcat(buf,":\n");
/*TODO*///
/*TODO*///	i = 0;
/*TODO*///	while (i < MAX_SOUND && Machine->drv->sound[i].sound_type)
/*TODO*///	{
/*TODO*///		if (sound_num(&Machine->drv->sound[i]))
/*TODO*///			sprintf(&buf[strlen(buf)],"%dx",sound_num(&Machine->drv->sound[i]));
/*TODO*///
/*TODO*///		sprintf(&buf[strlen(buf)],"%s",sound_name(&Machine->drv->sound[i]));
/*TODO*///
/*TODO*///		if (sound_clock(&Machine->drv->sound[i]))
/*TODO*///		{
/*TODO*///			if (sound_clock(&Machine->drv->sound[i]) >= 1000000)
/*TODO*///				sprintf(&buf[strlen(buf)]," %d.%06d MHz",
/*TODO*///						sound_clock(&Machine->drv->sound[i]) / 1000000,
/*TODO*///						sound_clock(&Machine->drv->sound[i]) % 1000000);
/*TODO*///			else
/*TODO*///				sprintf(&buf[strlen(buf)]," %d.%03d kHz",
/*TODO*///						sound_clock(&Machine->drv->sound[i]) / 1000,
/*TODO*///						sound_clock(&Machine->drv->sound[i]) % 1000);
/*TODO*///		}
/*TODO*///
/*TODO*///		strcat(buf,"\n");
/*TODO*///
/*TODO*///		i++;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (Machine->drv->video_attributes & VIDEO_TYPE_VECTOR)
/*TODO*///		sprintf(&buf[strlen(buf)],"\n%s\n", ui_getstring (UI_vectorgame));
/*TODO*///	else
/*TODO*///	{
/*TODO*///		sprintf(&buf[strlen(buf)],"\n%s:\n", ui_getstring (UI_screenres));
/*TODO*///		sprintf(&buf[strlen(buf)],"%d x %d (%s) %f Hz\n",
/*TODO*///				Machine->visible_area.max_x - Machine->visible_area.min_x + 1,
/*TODO*///				Machine->visible_area.max_y - Machine->visible_area.min_y + 1,
/*TODO*///				(Machine->gamedrv->flags & ORIENTATION_SWAP_XY) ? "V" : "H",
/*TODO*///				Machine->drv->frames_per_second);
/*TODO*///#if 0
/*TODO*///		{
/*TODO*///			int pixelx,pixely,tmax,tmin,rem;
/*TODO*///
/*TODO*///			pixelx = 4 * (Machine->visible_area.max_y - Machine->visible_area.min_y + 1);
/*TODO*///			pixely = 3 * (Machine->visible_area.max_x - Machine->visible_area.min_x + 1);
/*TODO*///
/*TODO*///			/* calculate MCD */
/*TODO*///			if (pixelx >= pixely)
/*TODO*///			{
/*TODO*///				tmax = pixelx;
/*TODO*///				tmin = pixely;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				tmax = pixely;
/*TODO*///				tmin = pixelx;
/*TODO*///			}
/*TODO*///			while ( (rem = tmax % tmin) )
/*TODO*///			{
/*TODO*///				tmax = tmin;
/*TODO*///				tmin = rem;
/*TODO*///			}
/*TODO*///			/* tmin is now the MCD */
/*TODO*///
/*TODO*///			pixelx /= tmin;
/*TODO*///			pixely /= tmin;
/*TODO*///
/*TODO*///			sprintf(&buf[strlen(buf)],"pixel aspect ratio %d:%d\n",
/*TODO*///					pixelx,pixely);
/*TODO*///		}
/*TODO*///		sprintf(&buf[strlen(buf)],"%d colors ",Machine->drv->total_colors);
/*TODO*///#endif
/*TODO*///	}
/*TODO*///
/*TODO*///
/*TODO*///	if (sel == -1)
/*TODO*///	{
/*TODO*///		/* startup info, print MAME version and ask for any key */
/*TODO*///
/*TODO*///		sprintf (buf2, "\n\t%s ", ui_getstring (UI_mame));	/* \t means that the line will be centered */
/*TODO*///		strcat(buf, buf2);
/*TODO*///
/*TODO*///		strcat(buf,build_version);
/*TODO*///		sprintf (buf2, "\n\t%s", ui_getstring (UI_anykey));
/*TODO*///		strcat(buf,buf2);
/*TODO*///		ui_drawbox(bitmap,0,0,uirotwidth,uirotheight);
/*TODO*///		ui_displaymessagewindow(bitmap,buf);
/*TODO*///
/*TODO*///		sel = 0;
/*TODO*///		if (code_read_async() != CODE_NONE)
/*TODO*///			sel = -1;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		/* menu system, use the normal menu keys */
/*TODO*///		strcat(buf,"\n\t");
/*TODO*///		strcat(buf,ui_getstring (UI_lefthilight));
/*TODO*///		strcat(buf," ");
/*TODO*///		strcat(buf,ui_getstring (UI_returntomain));
/*TODO*///		strcat(buf," ");
/*TODO*///		strcat(buf,ui_getstring (UI_righthilight));
/*TODO*///
/*TODO*///		ui_displaymessagewindow(bitmap,buf);
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*///			sel = -1;
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///			sel = -1;
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///			sel = -2;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (sel == -1 || sel == -2)
/*TODO*///	{
/*TODO*///		schedule_full_refresh();
/*TODO*///	}
/*TODO*///
/*TODO*///	return sel + 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///int showgamewarnings(struct mame_bitmap *bitmap)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	char buf[2048];
/*TODO*///
/*TODO*///	if (Machine->gamedrv->flags &
/*TODO*///			(GAME_NOT_WORKING | GAME_UNEMULATED_PROTECTION | GAME_WRONG_COLORS | GAME_IMPERFECT_COLORS |
/*TODO*///			  GAME_NO_SOUND | GAME_IMPERFECT_SOUND | GAME_IMPERFECT_GRAPHICS | GAME_NO_COCKTAIL))
/*TODO*///	{
/*TODO*///		int done;
/*TODO*///
/*TODO*///		strcpy(buf, ui_getstring (UI_knownproblems));
/*TODO*///		strcat(buf, "\n\n");
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///		if (Machine->gamedrv->flags & GAME_COMPUTER)
/*TODO*///		{
/*TODO*///			strcpy(buf, ui_getstring (UI_comp1));
/*TODO*///			strcat(buf, "\n\n");
/*TODO*///			strcat(buf, ui_getstring (UI_comp2));
/*TODO*///			strcat(buf, "\n");
/*TODO*///		}
/*TODO*///#endif
/*TODO*///
/*TODO*///		if (Machine->gamedrv->flags & GAME_IMPERFECT_COLORS)
/*TODO*///		{
/*TODO*///			strcat(buf, ui_getstring (UI_imperfectcolors));
/*TODO*///			strcat(buf, "\n");
/*TODO*///		}
/*TODO*///
/*TODO*///		if (Machine->gamedrv->flags & GAME_WRONG_COLORS)
/*TODO*///		{
/*TODO*///			strcat(buf, ui_getstring (UI_wrongcolors));
/*TODO*///			strcat(buf, "\n");
/*TODO*///		}
/*TODO*///
/*TODO*///		if (Machine->gamedrv->flags & GAME_IMPERFECT_GRAPHICS)
/*TODO*///		{
/*TODO*///			strcat(buf, ui_getstring (UI_imperfectgraphics));
/*TODO*///			strcat(buf, "\n");
/*TODO*///		}
/*TODO*///
/*TODO*///		if (Machine->gamedrv->flags & GAME_IMPERFECT_SOUND)
/*TODO*///		{
/*TODO*///			strcat(buf, ui_getstring (UI_imperfectsound));
/*TODO*///			strcat(buf, "\n");
/*TODO*///		}
/*TODO*///
/*TODO*///		if (Machine->gamedrv->flags & GAME_NO_SOUND)
/*TODO*///		{
/*TODO*///			strcat(buf, ui_getstring (UI_nosound));
/*TODO*///			strcat(buf, "\n");
/*TODO*///		}
/*TODO*///
/*TODO*///		if (Machine->gamedrv->flags & GAME_NO_COCKTAIL)
/*TODO*///		{
/*TODO*///			strcat(buf, ui_getstring (UI_nococktail));
/*TODO*///			strcat(buf, "\n");
/*TODO*///		}
/*TODO*///
/*TODO*///		if (Machine->gamedrv->flags & (GAME_NOT_WORKING | GAME_UNEMULATED_PROTECTION))
/*TODO*///		{
/*TODO*///			const struct GameDriver *maindrv;
/*TODO*///			int foundworking;
/*TODO*///
/*TODO*///			if (Machine->gamedrv->flags & GAME_NOT_WORKING)
/*TODO*///			{
/*TODO*///				strcpy(buf, ui_getstring (UI_brokengame));
/*TODO*///				strcat(buf, "\n");
/*TODO*///			}
/*TODO*///			if (Machine->gamedrv->flags & GAME_UNEMULATED_PROTECTION)
/*TODO*///			{
/*TODO*///				strcat(buf, ui_getstring (UI_brokenprotection));
/*TODO*///				strcat(buf, "\n");
/*TODO*///			}
/*TODO*///
/*TODO*///			if (Machine->gamedrv->clone_of && !(Machine->gamedrv->clone_of->flags & NOT_A_DRIVER))
/*TODO*///				maindrv = Machine->gamedrv->clone_of;
/*TODO*///			else maindrv = Machine->gamedrv;
/*TODO*///
/*TODO*///			foundworking = 0;
/*TODO*///			i = 0;
/*TODO*///			while (drivers[i])
/*TODO*///			{
/*TODO*///				if (drivers[i] == maindrv || drivers[i]->clone_of == maindrv)
/*TODO*///				{
/*TODO*///					if ((drivers[i]->flags & (GAME_NOT_WORKING | GAME_UNEMULATED_PROTECTION)) == 0)
/*TODO*///					{
/*TODO*///						if (foundworking == 0)
/*TODO*///						{
/*TODO*///							strcat(buf,"\n\n");
/*TODO*///							strcat(buf, ui_getstring (UI_workingclones));
/*TODO*///							strcat(buf,"\n\n");
/*TODO*///						}
/*TODO*///						foundworking = 1;
/*TODO*///
/*TODO*///						sprintf(&buf[strlen(buf)],"%s\n",drivers[i]->name);
/*TODO*///					}
/*TODO*///				}
/*TODO*///				i++;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		strcat(buf,"\n\n");
/*TODO*///		strcat(buf,ui_getstring (UI_typeok));
/*TODO*///
/*TODO*///		done = 0;
/*TODO*///		do
/*TODO*///		{
/*TODO*///			erase_screen(bitmap);
/*TODO*///			ui_drawbox(bitmap,0,0,uirotwidth,uirotheight);
/*TODO*///			ui_displaymessagewindow(bitmap,buf);
/*TODO*///
/*TODO*///			update_video_and_audio();
/*TODO*///			if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///				return 1;
/*TODO*///			if (code_pressed_memory(KEYCODE_O) ||
/*TODO*///					input_ui_pressed(IPT_UI_LEFT))
/*TODO*///				done = 1;
/*TODO*///			if (done == 1 && (code_pressed_memory(KEYCODE_K) ||
/*TODO*///					input_ui_pressed(IPT_UI_RIGHT)))
/*TODO*///				done = 2;
/*TODO*///		} while (done < 2);
/*TODO*///	}
/*TODO*///
/*TODO*///	erase_screen(bitmap);
/*TODO*///	update_video_and_audio();
/*TODO*///
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///int showgameinfo(struct mame_bitmap *bitmap)
/*TODO*///{
/*TODO*///	/* clear the input memory */
/*TODO*///	while (code_read_async() != CODE_NONE) {};
/*TODO*///
/*TODO*///	while (displaygameinfo(bitmap,0) == 1)
/*TODO*///	{
/*TODO*///		update_video_and_audio();
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef MESS
/*TODO*///	while (displayimageinfo(bitmap,0) == 1)
/*TODO*///	{
/*TODO*///		update_video_and_audio();
/*TODO*///	}
/*TODO*///	#endif
/*TODO*///
/*TODO*///	erase_screen(bitmap);
/*TODO*///	/* make sure that the screen is really cleared, in case autoframeskip kicked in */
/*TODO*///	update_video_and_audio();
/*TODO*///	update_video_and_audio();
/*TODO*///	update_video_and_audio();
/*TODO*///	update_video_and_audio();
/*TODO*///
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*////* Word-wraps the text in the specified buffer to fit in maxwidth characters per line.
/*TODO*///   The contents of the buffer are modified.
/*TODO*///   Known limitations: Words longer than maxwidth cause the function to fail. */
/*TODO*///static void wordwrap_text_buffer (char *buffer, int maxwidth)
/*TODO*///{
/*TODO*///	int width = 0;
/*TODO*///
/*TODO*///	while (*buffer)
/*TODO*///	{
/*TODO*///		if (*buffer == '\n')
/*TODO*///		{
/*TODO*///			buffer++;
/*TODO*///			width = 0;
/*TODO*///			continue;
/*TODO*///		}
/*TODO*///
/*TODO*///		width++;
/*TODO*///
/*TODO*///		if (width > maxwidth)
/*TODO*///		{
/*TODO*///			/* backtrack until a space is found */
/*TODO*///			while (*buffer != ' ')
/*TODO*///			{
/*TODO*///				buffer--;
/*TODO*///				width--;
/*TODO*///			}
/*TODO*///			if (width < 1) return;	/* word too long */
/*TODO*///
/*TODO*///			/* replace space with a newline */
/*TODO*///			*buffer = '\n';
/*TODO*///		}
/*TODO*///		else
/*TODO*///			buffer++;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///static int count_lines_in_buffer (char *buffer)
/*TODO*///{
/*TODO*///	int lines = 0;
/*TODO*///	char c;
/*TODO*///
/*TODO*///	while ( (c = *buffer++) )
/*TODO*///		if (c == '\n') lines++;
/*TODO*///
/*TODO*///	return lines;
/*TODO*///}
/*TODO*///
/*TODO*////* Display lines from buffer, starting with line 'scroll', in a width x height text window */
/*TODO*///static void display_scroll_message (struct mame_bitmap *bitmap, int *scroll, int width, int height, char *buf)
/*TODO*///{
/*TODO*///	struct DisplayText dt[256];
/*TODO*///	int curr_dt = 0;
/*TODO*///	const char *uparrow = ui_getstring (UI_uparrow);
/*TODO*///	const char *downarrow = ui_getstring (UI_downarrow);
/*TODO*///	char textcopy[2048];
/*TODO*///	char *copy;
/*TODO*///	int leftoffs,topoffs;
/*TODO*///	int first = *scroll;
/*TODO*///	int buflines,showlines;
/*TODO*///	int i;
/*TODO*///
/*TODO*///
/*TODO*///	/* draw box */
/*TODO*///	leftoffs = (uirotwidth - uirotcharwidth * (width + 1)) / 2;
/*TODO*///	if (leftoffs < 0) leftoffs = 0;
/*TODO*///	topoffs = (uirotheight - (3 * height + 1) * uirotcharheight / 2) / 2;
/*TODO*///	ui_drawbox(bitmap,leftoffs,topoffs,(width + 1) * uirotcharwidth,(3 * height + 1) * uirotcharheight / 2);
/*TODO*///
/*TODO*///	buflines = count_lines_in_buffer (buf);
/*TODO*///	if (first > 0)
/*TODO*///	{
/*TODO*///		if (buflines <= height)
/*TODO*///			first = 0;
/*TODO*///		else
/*TODO*///		{
/*TODO*///			height--;
/*TODO*///			if (first > (buflines - height))
/*TODO*///				first = buflines - height;
/*TODO*///		}
/*TODO*///		*scroll = first;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (first != 0)
/*TODO*///	{
/*TODO*///		/* indicate that scrolling upward is possible */
/*TODO*///		dt[curr_dt].text = uparrow;
/*TODO*///		dt[curr_dt].color = UI_COLOR_NORMAL;
/*TODO*///		dt[curr_dt].x = (uirotwidth - uirotcharwidth * strlen(uparrow)) / 2;
/*TODO*///		dt[curr_dt].y = topoffs + (3*curr_dt+1)*uirotcharheight/2;
/*TODO*///		curr_dt++;
/*TODO*///	}
/*TODO*///
/*TODO*///	if ((buflines - first) > height)
/*TODO*///		showlines = height - 1;
/*TODO*///	else
/*TODO*///		showlines = height;
/*TODO*///
/*TODO*///	/* skip to first line */
/*TODO*///	while (first > 0)
/*TODO*///	{
/*TODO*///		char c;
/*TODO*///
/*TODO*///		while ( (c = *buf++) )
/*TODO*///		{
/*TODO*///			if (c == '\n')
/*TODO*///			{
/*TODO*///				first--;
/*TODO*///				break;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* copy 'showlines' lines from buffer, starting with line 'first' */
/*TODO*///	copy = textcopy;
/*TODO*///	for (i = 0; i < showlines; i++)
/*TODO*///	{
/*TODO*///		char *copystart = copy;
/*TODO*///
/*TODO*///		while (*buf && *buf != '\n')
/*TODO*///		{
/*TODO*///			*copy = *buf;
/*TODO*///			copy++;
/*TODO*///			buf++;
/*TODO*///		}
/*TODO*///		*copy = '\0';
/*TODO*///		copy++;
/*TODO*///		if (*buf == '\n')
/*TODO*///			buf++;
/*TODO*///
/*TODO*///		if (*copystart == '\t') /* center text */
/*TODO*///		{
/*TODO*///			copystart++;
/*TODO*///			dt[curr_dt].x = (uirotwidth - uirotcharwidth * (copy - copystart)) / 2;
/*TODO*///		}
/*TODO*///		else
/*TODO*///			dt[curr_dt].x = leftoffs + uirotcharwidth/2;
/*TODO*///
/*TODO*///		dt[curr_dt].text = copystart;
/*TODO*///		dt[curr_dt].color = UI_COLOR_NORMAL;
/*TODO*///		dt[curr_dt].y = topoffs + (3*curr_dt+1)*uirotcharheight/2;
/*TODO*///		curr_dt++;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (showlines == (height - 1))
/*TODO*///	{
/*TODO*///		/* indicate that scrolling downward is possible */
/*TODO*///		dt[curr_dt].text = downarrow;
/*TODO*///		dt[curr_dt].color = UI_COLOR_NORMAL;
/*TODO*///		dt[curr_dt].x = (uirotwidth - uirotcharwidth * strlen(downarrow)) / 2;
/*TODO*///		dt[curr_dt].y = topoffs + (3*curr_dt+1)*uirotcharheight/2;
/*TODO*///		curr_dt++;
/*TODO*///	}
/*TODO*///
/*TODO*///	dt[curr_dt].text = 0;	/* terminate array */
/*TODO*///
/*TODO*///	displaytext(bitmap,dt);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////* Display text entry for current driver from history.dat and mameinfo.dat. */
/*TODO*///static int displayhistory (struct mame_bitmap *bitmap, int selected)
/*TODO*///{
/*TODO*///	static int scroll = 0;
/*TODO*///	static char *buf = 0;
/*TODO*///	int maxcols,maxrows;
/*TODO*///	int sel;
/*TODO*///	int bufsize = 256 * 1024; // 256KB of history.dat buffer, enough for everything
/*TODO*///
/*TODO*///	sel = selected - 1;
/*TODO*///
/*TODO*///
/*TODO*///	maxcols = (uirotwidth / uirotcharwidth) - 1;
/*TODO*///	maxrows = (2 * uirotheight - uirotcharheight) / (3 * uirotcharheight);
/*TODO*///	maxcols -= 2;
/*TODO*///	maxrows -= 8;
/*TODO*///
/*TODO*///	if (!buf)
/*TODO*///	{
/*TODO*///		/* allocate a buffer for the text */
/*TODO*///		buf = malloc (bufsize);
/*TODO*///
/*TODO*///		if (buf)
/*TODO*///		{
/*TODO*///			/* try to load entry */
/*TODO*///			if (load_driver_history (Machine->gamedrv, buf, bufsize) == 0)
/*TODO*///			{
/*TODO*///				scroll = 0;
/*TODO*///				wordwrap_text_buffer (buf, maxcols);
/*TODO*///				strcat(buf,"\n\t");
/*TODO*///				strcat(buf,ui_getstring (UI_lefthilight));
/*TODO*///				strcat(buf," ");
/*TODO*///				strcat(buf,ui_getstring (UI_returntomain));
/*TODO*///				strcat(buf," ");
/*TODO*///				strcat(buf,ui_getstring (UI_righthilight));
/*TODO*///				strcat(buf,"\n");
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				free (buf);
/*TODO*///				buf = 0;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	{
/*TODO*///		if (buf)
/*TODO*///			display_scroll_message (bitmap, &scroll, maxcols, maxrows, buf);
/*TODO*///		else
/*TODO*///		{
/*TODO*///			char msg[80];
/*TODO*///
/*TODO*///			strcpy(msg,"\t");
/*TODO*///			strcat(msg,ui_getstring(UI_historymissing));
/*TODO*///			strcat(msg,"\n\n\t");
/*TODO*///			strcat(msg,ui_getstring (UI_lefthilight));
/*TODO*///			strcat(msg," ");
/*TODO*///			strcat(msg,ui_getstring (UI_returntomain));
/*TODO*///			strcat(msg," ");
/*TODO*///			strcat(msg,ui_getstring (UI_righthilight));
/*TODO*///			ui_displaymessagewindow(bitmap,msg);
/*TODO*///		}
/*TODO*///
/*TODO*///		if ((scroll > 0) && input_ui_pressed_repeat(IPT_UI_UP,4))
/*TODO*///		{
/*TODO*///			if (scroll == 2) scroll = 0;	/* 1 would be the same as 0, but with arrow on top */
/*TODO*///			else scroll--;
/*TODO*///		}
/*TODO*///
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_DOWN,4))
/*TODO*///		{
/*TODO*///			if (scroll == 0) scroll = 2;	/* 1 would be the same as 0, but with arrow on top */
/*TODO*///			else scroll++;
/*TODO*///		}
/*TODO*///
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_PAN_UP, 4))
/*TODO*///		{
/*TODO*///			scroll -= maxrows - 2;
/*TODO*///			if (scroll < 0) scroll = 0;
/*TODO*///		}
/*TODO*///
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_PAN_DOWN, 4))
/*TODO*///		{
/*TODO*///			scroll += maxrows - 2;
/*TODO*///		}
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*///			sel = -1;
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///			sel = -1;
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///			sel = -2;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (sel == -1 || sel == -2)
/*TODO*///	{
/*TODO*///		schedule_full_refresh();
/*TODO*///
/*TODO*///		/* force buffer to be recreated */
/*TODO*///		if (buf)
/*TODO*///		{
/*TODO*///			free (buf);
/*TODO*///			buf = 0;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	return sel + 1;
/*TODO*///
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///#ifndef MESS
/*TODO*///#ifndef TINY_COMPILE
/*TODO*///#ifndef CPSMAME
/*TODO*///#ifndef MMSND
/*TODO*///int memcard_menu(struct mame_bitmap *bitmap, int selection)
/*TODO*///{
/*TODO*///	int sel;
/*TODO*///	int menutotal = 0;
/*TODO*///	const char *menuitem[10];
/*TODO*///	char buf[256];
/*TODO*///	char buf2[256];
/*TODO*///
/*TODO*///	sel = selection - 1 ;
/*TODO*///
/*TODO*///	sprintf(buf, "%s %03d", ui_getstring (UI_loadcard), mcd_number);
/*TODO*///	menuitem[menutotal++] = buf;
/*TODO*///	menuitem[menutotal++] = ui_getstring (UI_ejectcard);
/*TODO*///	menuitem[menutotal++] = ui_getstring (UI_createcard);
/*TODO*///#ifdef MESS
/*TODO*///	menuitem[menutotal++] = ui_getstring (UI_resetcard);
/*TODO*///#endif
/*TODO*///	menuitem[menutotal++] = ui_getstring (UI_returntomain);
/*TODO*///	menuitem[menutotal] = 0;
/*TODO*///
/*TODO*///	if (mcd_action!=0)
/*TODO*///	{
/*TODO*///		strcpy (buf2, "\n");
/*TODO*///
/*TODO*///		switch(mcd_action)
/*TODO*///		{
/*TODO*///			case 1:
/*TODO*///				strcat (buf2, ui_getstring (UI_loadfailed));
/*TODO*///				break;
/*TODO*///			case 2:
/*TODO*///				strcat (buf2, ui_getstring (UI_loadok));
/*TODO*///				break;
/*TODO*///			case 3:
/*TODO*///				strcat (buf2, ui_getstring (UI_cardejected));
/*TODO*///				break;
/*TODO*///			case 4:
/*TODO*///				strcat (buf2, ui_getstring (UI_cardcreated));
/*TODO*///				break;
/*TODO*///			case 5:
/*TODO*///				strcat (buf2, ui_getstring (UI_cardcreatedfailed));
/*TODO*///				strcat (buf2, "\n");
/*TODO*///				strcat (buf2, ui_getstring (UI_cardcreatedfailed2));
/*TODO*///				break;
/*TODO*///			default:
/*TODO*///				strcat (buf2, ui_getstring (UI_carderror));
/*TODO*///				break;
/*TODO*///		}
/*TODO*///
/*TODO*///		strcat (buf2, "\n\n");
/*TODO*///		ui_displaymessagewindow(bitmap,buf2);
/*TODO*///		if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*///			mcd_action = 0;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		ui_displaymenu(bitmap,menuitem,0,0,sel,0);
/*TODO*///
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_RIGHT,8))
/*TODO*///			mcd_number = (mcd_number + 1) % 1000;
/*TODO*///
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_LEFT,8))
/*TODO*///			mcd_number = (mcd_number + 999) % 1000;
/*TODO*///
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
/*TODO*///			sel = (sel + 1) % menutotal;
/*TODO*///
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_UP,8))
/*TODO*///			sel = (sel + menutotal - 1) % menutotal;
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*///		{
/*TODO*///			switch(sel)
/*TODO*///			{
/*TODO*///			case 0:
/*TODO*///				neogeo_memcard_eject();
/*TODO*///				if (neogeo_memcard_load(mcd_number))
/*TODO*///				{
/*TODO*///					memcard_status=1;
/*TODO*///					memcard_number=mcd_number;
/*TODO*///					mcd_action = 2;
/*TODO*///				}
/*TODO*///				else
/*TODO*///					mcd_action = 1;
/*TODO*///				break;
/*TODO*///			case 1:
/*TODO*///				neogeo_memcard_eject();
/*TODO*///				mcd_action = 3;
/*TODO*///				break;
/*TODO*///			case 2:
/*TODO*///				if (neogeo_memcard_create(mcd_number))
/*TODO*///					mcd_action = 4;
/*TODO*///				else
/*TODO*///					mcd_action = 5;
/*TODO*///				break;
/*TODO*///#ifdef MESS
/*TODO*///			case 3:
/*TODO*///				memcard_manager=1;
/*TODO*///				sel=-2;
/*TODO*///				machine_reset();
/*TODO*///				break;
/*TODO*///			case 4:
/*TODO*///				sel=-1;
/*TODO*///				break;
/*TODO*///#else
/*TODO*///			case 3:
/*TODO*///				sel=-1;
/*TODO*///				break;
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///			sel = -1;
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///			sel = -2;
/*TODO*///
/*TODO*///		if (sel == -1 || sel == -2)
/*TODO*///		{
/*TODO*///			schedule_full_refresh();
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	return sel + 1;
/*TODO*///}
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///#ifndef MESS
/*TODO*///enum { UI_SWITCH = 0,UI_DEFCODE,UI_CODE,UI_ANALOG,UI_CALIBRATE,
/*TODO*///		UI_STATS,UI_GAMEINFO, UI_HISTORY,
/*TODO*///		UI_CHEAT,UI_RESET,UI_MEMCARD,UI_RAPIDFIRE,UI_EXIT };
/*TODO*///#else
/*TODO*///enum { UI_SWITCH = 0,UI_DEFCODE,UI_CODE,UI_ANALOG,UI_CALIBRATE,
/*TODO*///		UI_GAMEINFO, UI_IMAGEINFO,UI_FILEMANAGER,UI_TAPECONTROL,
/*TODO*///		UI_HISTORY,UI_CHEAT,UI_RESET,UI_MEMCARD,UI_RAPIDFIRE,UI_EXIT,
/*TODO*///		UI_CONFIGURATION };
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///#ifdef XMAME
/*TODO*///extern int setrapidfire(struct mame_bitmap *bitmap, int selected);
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///#define MAX_SETUPMENU_ITEMS 20
/*TODO*///static const char *menu_item[MAX_SETUPMENU_ITEMS];
/*TODO*///static int menu_action[MAX_SETUPMENU_ITEMS];
/*TODO*///static int menu_total;
/*TODO*///
/*TODO*///
/*TODO*///static void setup_menu_init(void)
/*TODO*///{
/*TODO*///	menu_total = 0;
/*TODO*///
/*TODO*///	menu_item[menu_total] = ui_getstring (UI_inputgeneral); menu_action[menu_total++] = UI_DEFCODE;
/*TODO*///	menu_item[menu_total] = ui_getstring (UI_inputspecific); menu_action[menu_total++] = UI_CODE;
/*TODO*///#ifdef MESS
/*TODO*///	menu_item[menu_total] = ui_getstring (UI_configuration); menu_action[menu_total++] = UI_CONFIGURATION;
/*TODO*///#endif /* MESS */
/*TODO*///
/*TODO*///	/* Determine if there are any dip switches */
/*TODO*///	{
/*TODO*///		struct InputPort *in;
/*TODO*///		int num;
/*TODO*///
/*TODO*///		in = Machine->input_ports;
/*TODO*///
/*TODO*///		num = 0;
/*TODO*///		while (in->type != IPT_END)
/*TODO*///		{
/*TODO*///			if ((in->type & ~IPF_MASK) == IPT_DIPSWITCH_NAME && input_port_name(in) != 0 &&
/*TODO*///					(in->type & IPF_UNUSED) == 0 &&	!(!options.cheat && (in->type & IPF_CHEAT)))
/*TODO*///				num++;
/*TODO*///			in++;
/*TODO*///		}
/*TODO*///
/*TODO*///		if (num != 0)
/*TODO*///		{
/*TODO*///			menu_item[menu_total] = ui_getstring (UI_dipswitches); menu_action[menu_total++] = UI_SWITCH;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///#ifdef XMAME
/*TODO*///	{
/*TODO*///		extern int rapidfire_enable;
/*TODO*///
/*TODO*///		if (rapidfire_enable != 0)
/*TODO*///		{
/*TODO*///			menu_item[menu_total] = "Rapid Fire";
/*TODO*///			menu_action[menu_total++] = UI_RAPIDFIRE;
/*TODO*///		}
/*TODO*///	}
/*TODO*///#endif
/*TODO*///
/*TODO*///	/* Determine if there are any analog controls */
/*TODO*///	{
/*TODO*///		struct InputPort *in;
/*TODO*///		int num;
/*TODO*///
/*TODO*///		in = Machine->input_ports;
/*TODO*///
/*TODO*///		num = 0;
/*TODO*///		while (in->type != IPT_END)
/*TODO*///		{
/*TODO*///			if (((in->type & 0xff) > IPT_ANALOG_START) && ((in->type & 0xff) < IPT_ANALOG_END)
/*TODO*///					&& !(!options.cheat && (in->type & IPF_CHEAT)))
/*TODO*///				num++;
/*TODO*///			in++;
/*TODO*///		}
/*TODO*///
/*TODO*///		if (num != 0)
/*TODO*///		{
/*TODO*///			menu_item[menu_total] = ui_getstring (UI_analogcontrols); menu_action[menu_total++] = UI_ANALOG;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* Joystick calibration possible? */
/*TODO*///	if ((osd_joystick_needs_calibration()) != 0)
/*TODO*///	{
/*TODO*///		menu_item[menu_total] = ui_getstring (UI_calibrate); menu_action[menu_total++] = UI_CALIBRATE;
/*TODO*///	}
/*TODO*///
/*TODO*///#ifndef MESS
/*TODO*///	menu_item[menu_total] = ui_getstring (UI_bookkeeping); menu_action[menu_total++] = UI_STATS;
/*TODO*///	menu_item[menu_total] = ui_getstring (UI_gameinfo); menu_action[menu_total++] = UI_GAMEINFO;
/*TODO*///	menu_item[menu_total] = ui_getstring (UI_history); menu_action[menu_total++] = UI_HISTORY;
/*TODO*///#else
/*TODO*///	menu_item[menu_total] = ui_getstring (UI_imageinfo); menu_action[menu_total++] = UI_IMAGEINFO;
/*TODO*///	menu_item[menu_total] = ui_getstring (UI_filemanager); menu_action[menu_total++] = UI_FILEMANAGER;
/*TODO*///#if HAS_WAVE
/*TODO*///	menu_item[menu_total] = ui_getstring (UI_tapecontrol); menu_action[menu_total++] = UI_TAPECONTROL;
/*TODO*///#endif
/*TODO*///	menu_item[menu_total] = ui_getstring (UI_history); menu_action[menu_total++] = UI_HISTORY;
/*TODO*///#endif
/*TODO*///
/*TODO*///	if (options.cheat)
/*TODO*///	{
/*TODO*///		menu_item[menu_total] = ui_getstring (UI_cheat); menu_action[menu_total++] = UI_CHEAT;
/*TODO*///	}
/*TODO*///
/*TODO*///#ifndef MESS
/*TODO*///#ifndef TINY_COMPILE
/*TODO*///#ifndef CPSMAME
/*TODO*///#ifndef MMSND
/*TODO*///	if (Machine->gamedrv->clone_of == &driver_neogeo ||
/*TODO*///			(Machine->gamedrv->clone_of &&
/*TODO*///				Machine->gamedrv->clone_of->clone_of == &driver_neogeo))
/*TODO*///	{
/*TODO*///		menu_item[menu_total] = ui_getstring (UI_memorycard); menu_action[menu_total++] = UI_MEMCARD;
/*TODO*///	}
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///
/*TODO*///	menu_item[menu_total] = ui_getstring (UI_resetgame); menu_action[menu_total++] = UI_RESET;
/*TODO*///	menu_item[menu_total] = ui_getstring (UI_returntogame); menu_action[menu_total++] = UI_EXIT;
/*TODO*///	menu_item[menu_total] = 0; /* terminate array */
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static int setup_menu(struct mame_bitmap *bitmap, int selected)
/*TODO*///{
/*TODO*///	int sel,res=-1;
/*TODO*///	static int menu_lastselected = 0;
/*TODO*///
/*TODO*///
/*TODO*///	if (selected == -1)
/*TODO*///		sel = menu_lastselected;
/*TODO*///	else sel = selected - 1;
/*TODO*///
/*TODO*///	if (sel > SEL_MASK)
/*TODO*///	{
/*TODO*///		switch (menu_action[sel & SEL_MASK])
/*TODO*///		{
/*TODO*///#ifdef XMAME
/*TODO*///			case UI_RAPIDFIRE:
/*TODO*///				res = setrapidfire(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///#endif
/*TODO*///			case UI_SWITCH:
/*TODO*///				res = setdipswitches(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///			case UI_DEFCODE:
/*TODO*///				res = setdefcodesettings(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///			case UI_CODE:
/*TODO*///				res = setcodesettings(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///			case UI_ANALOG:
/*TODO*///				res = settraksettings(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///			case UI_CALIBRATE:
/*TODO*///				res = calibratejoysticks(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///#ifndef MESS
/*TODO*///			case UI_STATS:
/*TODO*///				res = mame_stats(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///			case UI_GAMEINFO:
/*TODO*///				res = displaygameinfo(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///#endif
/*TODO*///#ifdef MESS
/*TODO*///			case UI_IMAGEINFO:
/*TODO*///				res = displayimageinfo(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///			case UI_FILEMANAGER:
/*TODO*///				res = filemanager(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///#if HAS_WAVE
/*TODO*///			case UI_TAPECONTROL:
/*TODO*///				res = tapecontrol(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///#endif /* HAS_WAVE */
/*TODO*///			case UI_CONFIGURATION:
/*TODO*///				res = setconfiguration(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///#endif /* MESS */
/*TODO*///			case UI_HISTORY:
/*TODO*///				res = displayhistory(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///			case UI_CHEAT:
/*TODO*///				res = cheat_menu(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///#ifndef MESS
/*TODO*///#ifndef TINY_COMPILE
/*TODO*///#ifndef CPSMAME
/*TODO*///#ifndef MMSND
/*TODO*///			case UI_MEMCARD:
/*TODO*///				res = memcard_menu(bitmap, sel >> SEL_BITS);
/*TODO*///				break;
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///		}
/*TODO*///
/*TODO*///		if (res == -1)
/*TODO*///		{
/*TODO*///			menu_lastselected = sel;
/*TODO*///			sel = -1;
/*TODO*///		}
/*TODO*///		else
/*TODO*///			sel = (sel & SEL_MASK) | (res << SEL_BITS);
/*TODO*///
/*TODO*///		return sel + 1;
/*TODO*///	}
/*TODO*///
/*TODO*///
/*TODO*///	ui_displaymenu(bitmap,menu_item,0,0,sel,0);
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
/*TODO*///		sel = (sel + 1) % menu_total;
/*TODO*///
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_UP,8))
/*TODO*///		sel = (sel + menu_total - 1) % menu_total;
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*///	{
/*TODO*///		switch (menu_action[sel])
/*TODO*///		{
/*TODO*///#ifdef XMAME
/*TODO*///			case UI_RAPIDFIRE:
/*TODO*///#endif
/*TODO*///			case UI_SWITCH:
/*TODO*///			case UI_DEFCODE:
/*TODO*///			case UI_CODE:
/*TODO*///			case UI_ANALOG:
/*TODO*///			case UI_CALIBRATE:
/*TODO*///			#ifndef MESS
/*TODO*///			case UI_STATS:
/*TODO*///			case UI_GAMEINFO:
/*TODO*///			#else
/*TODO*///			case UI_GAMEINFO:
/*TODO*///			case UI_IMAGEINFO:
/*TODO*///			case UI_FILEMANAGER:
/*TODO*///			case UI_TAPECONTROL:
/*TODO*///			case UI_CONFIGURATION:
/*TODO*///#endif /* !MESS */
/*TODO*///			case UI_HISTORY:
/*TODO*///			case UI_CHEAT:
/*TODO*///			case UI_MEMCARD:
/*TODO*///				sel |= 1 << SEL_BITS;
/*TODO*///				schedule_full_refresh();
/*TODO*///				break;
/*TODO*///
/*TODO*///			case UI_RESET:
/*TODO*///				machine_reset();
/*TODO*///				break;
/*TODO*///
/*TODO*///			case UI_EXIT:
/*TODO*///				menu_lastselected = 0;
/*TODO*///				sel = -1;
/*TODO*///				break;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_CANCEL) ||
/*TODO*///			input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///	{
/*TODO*///		menu_lastselected = sel;
/*TODO*///		sel = -1;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (sel == -1)
/*TODO*///	{
/*TODO*///		schedule_full_refresh();
/*TODO*///	}
/*TODO*///
/*TODO*///	return sel + 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*********************************************************************
/*TODO*///
/*TODO*///  start of On Screen Display handling
/*TODO*///
/*TODO*///*********************************************************************/
/*TODO*///
/*TODO*///static void displayosd(struct mame_bitmap *bitmap,const char *text,int percentage,int default_percentage)
/*TODO*///{
/*TODO*///	struct DisplayText dt[2];
/*TODO*///	int avail;
/*TODO*///
/*TODO*///
/*TODO*///	avail = (uirotwidth / uirotcharwidth) * 19 / 20;
/*TODO*///
/*TODO*///	ui_drawbox(bitmap,(uirotwidth - uirotcharwidth * avail) / 2,
/*TODO*///			(uirotheight - 7*uirotcharheight/2),
/*TODO*///			avail * uirotcharwidth,
/*TODO*///			3*uirotcharheight);
/*TODO*///
/*TODO*///	avail--;
/*TODO*///
/*TODO*///	drawbar(bitmap,(uirotwidth - uirotcharwidth * avail) / 2,
/*TODO*///			(uirotheight - 3*uirotcharheight),
/*TODO*///			avail * uirotcharwidth,
/*TODO*///			uirotcharheight,
/*TODO*///			percentage,default_percentage);
/*TODO*///
/*TODO*///	dt[0].text = text;
/*TODO*///	dt[0].color = UI_COLOR_NORMAL;
/*TODO*///	dt[0].x = (uirotwidth - uirotcharwidth * strlen(text)) / 2;
/*TODO*///	dt[0].y = (uirotheight - 2*uirotcharheight) + 2;
/*TODO*///	dt[1].text = 0; /* terminate array */
/*TODO*///	displaytext(bitmap,dt);
/*TODO*///}
/*TODO*///
/*TODO*////* K.Wilkins Feb2003 Additional of Disrete Sound System ADJUSTMENT sliders */
/*TODO*///#if HAS_DISCRETE
/*TODO*///static void onscrd_discrete(struct mame_bitmap *bitmap,int increment,int arg)
/*TODO*///{
/*TODO*///	int ourval,initial;
/*TODO*///	char buf[40];
/*TODO*///	struct discrete_sh_adjuster adjuster;
/*TODO*///
/*TODO*///	ourval=0;
/*TODO*///	initial=0;
/*TODO*///	strcpy(buf,"ADJUSTER ERROR");
/*TODO*///
/*TODO*///	/* Use ARG to select correct DISCRETE_ADJUST in sound subsystem */
/*TODO*///	if(discrete_sh_adjuster_get(arg,&adjuster)==-1)
/*TODO*///	{
/*TODO*///		/* Serious error, init has setup a non-existant slider, should NEVER happen */
/*TODO*///		logerror("onscrd_discrete() - osd_menu_init has setup invalid slider No %d",arg);
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		if(adjuster.islogscale)
/*TODO*///		{
/*TODO*///			double loginc,logspan,logval,logmin,loginit;
/*TODO*///			logspan=log10(adjuster.max)-log10(adjuster.min);
/*TODO*///			loginit=log10(adjuster.initial);
/*TODO*///			logmin=log10(adjuster.min);
/*TODO*///			logval=log10(adjuster.value);
/*TODO*///			loginc=(logspan/100)*increment;
/*TODO*///			logval+=loginc;
/*TODO*///			adjuster.value=pow(10,logval);
/*TODO*///
/*TODO*///			/* Keep within sensible bounds */
/*TODO*///			if(adjuster.value > adjuster.max)
/*TODO*///			{
/*TODO*///				adjuster.value=adjuster.max;
/*TODO*///				ourval=100;
/*TODO*///			}
/*TODO*///			if(adjuster.value < adjuster.min)
/*TODO*///			{
/*TODO*///				adjuster.value=adjuster.min;
/*TODO*///				ourval=0;
/*TODO*///			}
/*TODO*///
/*TODO*///			ourval=(int) (100.0*((logval-logmin)/logspan));
/*TODO*///			initial=(int) (100.0*((loginit-logmin)/logspan));
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			double finc;
/*TODO*///			finc=((adjuster.max-adjuster.min)/100)*increment;
/*TODO*///			adjuster.value+=finc;
/*TODO*///
/*TODO*///			/* Keep within sensible bounds */
/*TODO*///			if(adjuster.value > adjuster.max) adjuster.value=adjuster.max;
/*TODO*///			if(adjuster.value < adjuster.min) adjuster.value=adjuster.min;
/*TODO*///
/*TODO*///			ourval=(int) (100.0*((adjuster.value-adjuster.min)/(adjuster.max-adjuster.min)));
/*TODO*///			initial=(int) (100.0*((adjuster.initial-adjuster.min)/(adjuster.max-adjuster.min)));
/*TODO*///		}
/*TODO*///
/*TODO*///		/* Update the system */
/*TODO*///		discrete_sh_adjuster_set(arg,&adjuster);
/*TODO*///
/*TODO*///		sprintf(buf,"%s %d%%",adjuster.name,ourval);
/*TODO*///	}
/*TODO*///	displayosd(bitmap,buf,ourval,initial);
/*TODO*///}
/*TODO*///#endif /* HAS_DISCRETE */
/*TODO*////* K.Wilkins Feb2003 Additional of Disrete Sound System ADJUSTMENT sliders */
/*TODO*///
/*TODO*///static void onscrd_volume(struct mame_bitmap *bitmap,int increment,int arg)
/*TODO*///{
/*TODO*///	char buf[20];
/*TODO*///	int attenuation;
/*TODO*///
/*TODO*///	if (increment)
/*TODO*///	{
/*TODO*///		attenuation = osd_get_mastervolume();
/*TODO*///		attenuation += increment;
/*TODO*///		if (attenuation > 0) attenuation = 0;
/*TODO*///		if (attenuation < -32) attenuation = -32;
/*TODO*///		osd_set_mastervolume(attenuation);
/*TODO*///	}
/*TODO*///	attenuation = osd_get_mastervolume();
/*TODO*///
/*TODO*///	sprintf(buf,"%s %3ddB", ui_getstring (UI_volume), attenuation);
/*TODO*///	displayosd(bitmap,buf,100 * (attenuation + 32) / 32,100);
/*TODO*///}
/*TODO*///
/*TODO*///static void onscrd_mixervol(struct mame_bitmap *bitmap,int increment,int arg)
/*TODO*///{
/*TODO*///	static void *driver = 0;
/*TODO*///	char buf[40];
/*TODO*///	int volume,ch;
/*TODO*///	int doallchannels = 0;
/*TODO*///	int proportional = 0;
/*TODO*///
/*TODO*///
/*TODO*///	if (code_pressed(KEYCODE_LSHIFT) || code_pressed(KEYCODE_RSHIFT))
/*TODO*///		doallchannels = 1;
/*TODO*///	if (!code_pressed(KEYCODE_LCONTROL) && !code_pressed(KEYCODE_RCONTROL))
/*TODO*///		increment *= 5;
/*TODO*///	if (code_pressed(KEYCODE_LALT) || code_pressed(KEYCODE_RALT))
/*TODO*///		proportional = 1;
/*TODO*///
/*TODO*///	if (increment)
/*TODO*///	{
/*TODO*///		if (proportional)
/*TODO*///		{
/*TODO*///			static int old_vol[MIXER_MAX_CHANNELS];
/*TODO*///			float ratio = 1.0;
/*TODO*///			int overflow = 0;
/*TODO*///
/*TODO*///			if (driver != Machine->drv)
/*TODO*///			{
/*TODO*///				driver = (void *)Machine->drv;
/*TODO*///				for (ch = 0; ch < MIXER_MAX_CHANNELS; ch++)
/*TODO*///					old_vol[ch] = mixer_get_mixing_level(ch);
/*TODO*///			}
/*TODO*///
/*TODO*///			volume = mixer_get_mixing_level(arg);
/*TODO*///			if (old_vol[arg])
/*TODO*///				ratio = (float)(volume + increment) / (float)old_vol[arg];
/*TODO*///
/*TODO*///			for (ch = 0; ch < MIXER_MAX_CHANNELS; ch++)
/*TODO*///			{
/*TODO*///				if (mixer_get_name(ch) != 0)
/*TODO*///				{
/*TODO*///					volume = ratio * old_vol[ch];
/*TODO*///					if (volume < 0 || volume > 100)
/*TODO*///						overflow = 1;
/*TODO*///				}
/*TODO*///			}
/*TODO*///
/*TODO*///			if (!overflow)
/*TODO*///			{
/*TODO*///				for (ch = 0; ch < MIXER_MAX_CHANNELS; ch++)
/*TODO*///				{
/*TODO*///					volume = ratio * old_vol[ch];
/*TODO*///					mixer_set_mixing_level(ch,volume);
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			driver = 0; /* force reset of saved volumes */
/*TODO*///
/*TODO*///			volume = mixer_get_mixing_level(arg);
/*TODO*///			volume += increment;
/*TODO*///			if (volume > 100) volume = 100;
/*TODO*///			if (volume < 0) volume = 0;
/*TODO*///
/*TODO*///			if (doallchannels)
/*TODO*///			{
/*TODO*///				for (ch = 0;ch < MIXER_MAX_CHANNELS;ch++)
/*TODO*///					mixer_set_mixing_level(ch,volume);
/*TODO*///			}
/*TODO*///			else
/*TODO*///				mixer_set_mixing_level(arg,volume);
/*TODO*///		}
/*TODO*///	}
/*TODO*///	volume = mixer_get_mixing_level(arg);
/*TODO*///
/*TODO*///	if (proportional)
/*TODO*///		sprintf(buf,"%s %s %3d%%", ui_getstring (UI_allchannels), ui_getstring (UI_relative), volume);
/*TODO*///	else if (doallchannels)
/*TODO*///		sprintf(buf,"%s %s %3d%%", ui_getstring (UI_allchannels), ui_getstring (UI_volume), volume);
/*TODO*///	else
/*TODO*///		sprintf(buf,"%s %s %3d%%",mixer_get_name(arg), ui_getstring (UI_volume), volume);
/*TODO*///	displayosd(bitmap,buf,volume,mixer_get_default_mixing_level(arg));
/*TODO*///}
/*TODO*///
/*TODO*///static void onscrd_brightness(struct mame_bitmap *bitmap,int increment,int arg)
/*TODO*///{
/*TODO*///	char buf[20];
/*TODO*///	double brightness;
/*TODO*///
/*TODO*///
/*TODO*///	if (increment)
/*TODO*///	{
/*TODO*///		brightness = palette_get_global_brightness();
/*TODO*///		brightness += 0.05 * increment;
/*TODO*///		if (brightness < 0.1) brightness = 0.1;
/*TODO*///		if (brightness > 1.0) brightness = 1.0;
/*TODO*///		palette_set_global_brightness(brightness);
/*TODO*///	}
/*TODO*///	brightness = palette_get_global_brightness();
/*TODO*///
/*TODO*///	sprintf(buf,"%s %3d%%", ui_getstring (UI_brightness), (int)(brightness * 100));
/*TODO*///	displayosd(bitmap,buf,brightness*100,100);
/*TODO*///}
/*TODO*///
/*TODO*///static void onscrd_gamma(struct mame_bitmap *bitmap,int increment,int arg)
/*TODO*///{
/*TODO*///	char buf[20];
/*TODO*///	double gamma_correction;
/*TODO*///
/*TODO*///	if (increment)
/*TODO*///	{
/*TODO*///		gamma_correction = palette_get_global_gamma();
/*TODO*///
/*TODO*///		gamma_correction += 0.05 * increment;
/*TODO*///		if (gamma_correction < 0.5) gamma_correction = 0.5;
/*TODO*///		if (gamma_correction > 2.0) gamma_correction = 2.0;
/*TODO*///
/*TODO*///		palette_set_global_gamma(gamma_correction);
/*TODO*///	}
/*TODO*///	gamma_correction = palette_get_global_gamma();
/*TODO*///
/*TODO*///	sprintf(buf,"%s %1.2f", ui_getstring (UI_gamma), gamma_correction);
/*TODO*///	displayosd(bitmap,buf,100*(gamma_correction-0.5)/(2.0-0.5),100*(1.0-0.5)/(2.0-0.5));
/*TODO*///}
/*TODO*///
/*TODO*///static void onscrd_vector_flicker(struct mame_bitmap *bitmap,int increment,int arg)
/*TODO*///{
/*TODO*///	char buf[1000];
/*TODO*///	float flicker_correction;
/*TODO*///
/*TODO*///	if (!code_pressed(KEYCODE_LCONTROL) && !code_pressed(KEYCODE_RCONTROL))
/*TODO*///		increment *= 5;
/*TODO*///
/*TODO*///	if (increment)
/*TODO*///	{
/*TODO*///		flicker_correction = vector_get_flicker();
/*TODO*///
/*TODO*///		flicker_correction += increment;
/*TODO*///		if (flicker_correction < 0.0) flicker_correction = 0.0;
/*TODO*///		if (flicker_correction > 100.0) flicker_correction = 100.0;
/*TODO*///
/*TODO*///		vector_set_flicker(flicker_correction);
/*TODO*///	}
/*TODO*///	flicker_correction = vector_get_flicker();
/*TODO*///
/*TODO*///	sprintf(buf,"%s %1.2f", ui_getstring (UI_vectorflicker), flicker_correction);
/*TODO*///	displayosd(bitmap,buf,flicker_correction,0);
/*TODO*///}
/*TODO*///
/*TODO*///static void onscrd_vector_intensity(struct mame_bitmap *bitmap,int increment,int arg)
/*TODO*///{
/*TODO*///	char buf[30];
/*TODO*///	float intensity_correction;
/*TODO*///
/*TODO*///	if (increment)
/*TODO*///	{
/*TODO*///		intensity_correction = vector_get_intensity();
/*TODO*///
/*TODO*///		intensity_correction += 0.05 * increment;
/*TODO*///		if (intensity_correction < 0.5) intensity_correction = 0.5;
/*TODO*///		if (intensity_correction > 3.0) intensity_correction = 3.0;
/*TODO*///
/*TODO*///		vector_set_intensity(intensity_correction);
/*TODO*///	}
/*TODO*///	intensity_correction = vector_get_intensity();
/*TODO*///
/*TODO*///	sprintf(buf,"%s %1.2f", ui_getstring (UI_vectorintensity), intensity_correction);
/*TODO*///	displayosd(bitmap,buf,100*(intensity_correction-0.5)/(3.0-0.5),100*(1.5-0.5)/(3.0-0.5));
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void onscrd_overclock(struct mame_bitmap *bitmap,int increment,int arg)
/*TODO*///{
/*TODO*///	char buf[30];
/*TODO*///	double overclock;
/*TODO*///	int cpu, doallcpus = 0, oc;
/*TODO*///
/*TODO*///	if (code_pressed(KEYCODE_LSHIFT) || code_pressed(KEYCODE_RSHIFT))
/*TODO*///		doallcpus = 1;
/*TODO*///	if (!code_pressed(KEYCODE_LCONTROL) && !code_pressed(KEYCODE_RCONTROL))
/*TODO*///		increment *= 5;
/*TODO*///	if( increment )
/*TODO*///	{
/*TODO*///		overclock = timer_get_overclock(arg);
/*TODO*///		overclock += 0.01 * increment;
/*TODO*///		if (overclock < 0.01) overclock = 0.01;
/*TODO*///		if (overclock > 2.0) overclock = 2.0;
/*TODO*///		if( doallcpus )
/*TODO*///			for( cpu = 0; cpu < cpu_gettotalcpu(); cpu++ )
/*TODO*///				timer_set_overclock(cpu, overclock);
/*TODO*///		else
/*TODO*///			timer_set_overclock(arg, overclock);
/*TODO*///	}
/*TODO*///
/*TODO*///	oc = 100 * timer_get_overclock(arg) + 0.5;
/*TODO*///
/*TODO*///	if( doallcpus )
/*TODO*///		sprintf(buf,"%s %s %3d%%", ui_getstring (UI_allcpus), ui_getstring (UI_overclock), oc);
/*TODO*///	else
/*TODO*///		sprintf(buf,"%s %s%d %3d%%", ui_getstring (UI_overclock), ui_getstring (UI_cpu), arg, oc);
/*TODO*///	displayosd(bitmap,buf,oc/2,100/2);
/*TODO*///}
/*TODO*///
/*TODO*///#define MAX_OSD_ITEMS 30
/*TODO*///static void (*onscrd_fnc[MAX_OSD_ITEMS])(struct mame_bitmap *bitmap,int increment,int arg);
/*TODO*///static int onscrd_arg[MAX_OSD_ITEMS];
/*TODO*///static int onscrd_total_items;
/*TODO*///
/*TODO*///static void onscrd_init(void)
/*TODO*///{
/*TODO*///	int item,ch;
/*TODO*///#if HAS_DISCRETE
/*TODO*///	int soundnum;
/*TODO*///#endif /* HAS_DISCRETE */
/*TODO*///
/*TODO*///
/*TODO*///	item = 0;
/*TODO*///
/*TODO*///	if (Machine->sample_rate)
/*TODO*///	{
/*TODO*///		onscrd_fnc[item] = onscrd_volume;
/*TODO*///		onscrd_arg[item] = 0;
/*TODO*///		item++;
/*TODO*///
/*TODO*///		for (ch = 0;ch < MIXER_MAX_CHANNELS;ch++)
/*TODO*///		{
/*TODO*///			if (mixer_get_name(ch) != 0)
/*TODO*///			{
/*TODO*///				onscrd_fnc[item] = onscrd_mixervol;
/*TODO*///				onscrd_arg[item] = ch;
/*TODO*///				item++;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* K.Wilkins Feb2003 Additional of Disrete Sound System ADJUSTMENT sliders */
/*TODO*///#if HAS_DISCRETE
/*TODO*///		/* See if there is a discrete sound sub-system present */
/*TODO*///		for (soundnum = 0; soundnum < MAX_SOUND; soundnum++)
/*TODO*///		{
/*TODO*///			if (Machine->drv->sound[soundnum].sound_type == SOUND_DISCRETE)
/*TODO*///			{
/*TODO*///				/* For each DISCRETE_ADJUST node then there is a slider, there can only be one SOUND_DISCRETE */
/*TODO*///				/* in the machinbe sound delcaration so this WONT trigger more than once                      */
/*TODO*///				{
/*TODO*///					int count;
/*TODO*///					count=discrete_sh_adjuster_count((struct discrete_sound_block*)Machine->drv->sound[soundnum].sound_interface);
/*TODO*///
/*TODO*///					for(ch=0;ch<count;ch++)
/*TODO*///					{
/*TODO*///						onscrd_fnc[item] = onscrd_discrete;
/*TODO*///						onscrd_arg[item] = ch;
/*TODO*///						item++;
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///#endif /* HAS_DISCRETE */
/*TODO*///		/* K.Wilkins Feb2003 Additional of Disrete Sound System ADJUSTMENT sliders */
/*TODO*///	}
/*TODO*///
/*TODO*///	if (options.cheat)
/*TODO*///	{
/*TODO*///		for (ch = 0;ch < cpu_gettotalcpu();ch++)
/*TODO*///		{
/*TODO*///			onscrd_fnc[item] = onscrd_overclock;
/*TODO*///			onscrd_arg[item] = ch;
/*TODO*///			item++;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	onscrd_fnc[item] = onscrd_brightness;
/*TODO*///	onscrd_arg[item] = 0;
/*TODO*///	item++;
/*TODO*///
/*TODO*///	onscrd_fnc[item] = onscrd_gamma;
/*TODO*///	onscrd_arg[item] = 0;
/*TODO*///	item++;
/*TODO*///
/*TODO*///	if (Machine->drv->video_attributes & VIDEO_TYPE_VECTOR)
/*TODO*///	{
/*TODO*///		onscrd_fnc[item] = onscrd_vector_flicker;
/*TODO*///		onscrd_arg[item] = 0;
/*TODO*///		item++;
/*TODO*///
/*TODO*///		onscrd_fnc[item] = onscrd_vector_intensity;
/*TODO*///		onscrd_arg[item] = 0;
/*TODO*///		item++;
/*TODO*///	}
/*TODO*///
/*TODO*///	onscrd_total_items = item;
/*TODO*///}
/*TODO*///
/*TODO*///static int on_screen_display(struct mame_bitmap *bitmap, int selected)
/*TODO*///{
/*TODO*///	int increment,sel;
/*TODO*///	static int lastselected = 0;
/*TODO*///
/*TODO*///
/*TODO*///	if (selected == -1)
/*TODO*///		sel = lastselected;
/*TODO*///	else sel = selected - 1;
/*TODO*///
/*TODO*///	increment = 0;
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_LEFT,8))
/*TODO*///		increment = -1;
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_RIGHT,8))
/*TODO*///		increment = 1;
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
/*TODO*///		sel = (sel + 1) % onscrd_total_items;
/*TODO*///	if (input_ui_pressed_repeat(IPT_UI_UP,8))
/*TODO*///		sel = (sel + onscrd_total_items - 1) % onscrd_total_items;
/*TODO*///
/*TODO*///	(*onscrd_fnc[sel])(bitmap,increment,onscrd_arg[sel]);
/*TODO*///
/*TODO*///	lastselected = sel;
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_ON_SCREEN_DISPLAY))
/*TODO*///	{
/*TODO*///		sel = -1;
/*TODO*///
/*TODO*///		schedule_full_refresh();
/*TODO*///	}
/*TODO*///
/*TODO*///	return sel + 1;
/*TODO*///}
/*TODO*///
/*TODO*////*********************************************************************
/*TODO*///
/*TODO*///  end of On Screen Display handling
/*TODO*///
/*TODO*///*********************************************************************/
/*TODO*///
/*TODO*///
/*TODO*///static void displaymessage(struct mame_bitmap *bitmap,const char *text)
/*TODO*///{
/*TODO*///	struct DisplayText dt[2];
/*TODO*///	int avail;
/*TODO*///
/*TODO*///
/*TODO*///	if (uirotwidth < uirotcharwidth * strlen(text))
/*TODO*///	{
/*TODO*///		ui_displaymessagewindow(bitmap,text);
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	avail = strlen(text)+2;
/*TODO*///
/*TODO*///	ui_drawbox(bitmap,(uirotwidth - uirotcharwidth * avail) / 2,
/*TODO*///			uirotheight - 3*uirotcharheight,
/*TODO*///			avail * uirotcharwidth,
/*TODO*///			2*uirotcharheight);
/*TODO*///
/*TODO*///	dt[0].text = text;
/*TODO*///	dt[0].color = UI_COLOR_NORMAL;
/*TODO*///	dt[0].x = (uirotwidth - uirotcharwidth * strlen(text)) / 2;
/*TODO*///	dt[0].y = uirotheight - 5*uirotcharheight/2;
/*TODO*///	dt[1].text = 0; /* terminate array */
/*TODO*///	displaytext(bitmap,dt);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static char messagetext[200];
/*TODO*///static int messagecounter;
/*TODO*///
/*TODO*///void CLIB_DECL usrintf_showmessage(const char *text,...)
/*TODO*///{
/*TODO*///	va_list arg;
/*TODO*///	va_start(arg,text);
/*TODO*///	vsprintf(messagetext,text,arg);
/*TODO*///	va_end(arg);
/*TODO*///	messagecounter = 2 * Machine->drv->frames_per_second;
/*TODO*///}
/*TODO*///
/*TODO*///void CLIB_DECL usrintf_showmessage_secs(int seconds, const char *text,...)
/*TODO*///{
/*TODO*///	va_list arg;
/*TODO*///	va_start(arg,text);
/*TODO*///	vsprintf(messagetext,text,arg);
/*TODO*///	va_end(arg);
/*TODO*///	messagecounter = seconds * Machine->drv->frames_per_second;
/*TODO*///}
/*TODO*///
/*TODO*///void do_loadsave(struct mame_bitmap *bitmap, int request_loadsave)
/*TODO*///{
/*TODO*///	int file = 0;
/*TODO*///
/*TODO*///	mame_pause(1);
/*TODO*///
/*TODO*///	do
/*TODO*///	{
/*TODO*///		InputCode code;
/*TODO*///
/*TODO*///		if (request_loadsave == LOADSAVE_SAVE)
/*TODO*///			displaymessage(bitmap, "Select position to save to");
/*TODO*///		else
/*TODO*///			displaymessage(bitmap, "Select position to load from");
/*TODO*///
/*TODO*///		update_video_and_audio();
/*TODO*///		reset_partial_updates();
/*TODO*///
/*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///			break;
/*TODO*///
/*TODO*///		code = code_read_async();
/*TODO*///		if (code != CODE_NONE)
/*TODO*///		{
/*TODO*///			if (code >= KEYCODE_A && code <= KEYCODE_Z)
/*TODO*///				file = 'a' + (code - KEYCODE_A);
/*TODO*///			else if (code >= KEYCODE_0 && code <= KEYCODE_9)
/*TODO*///				file = '0' + (code - KEYCODE_0);
/*TODO*///			else if (code >= KEYCODE_0_PAD && code <= KEYCODE_9_PAD)
/*TODO*///				file = '0' + (code - KEYCODE_0);
/*TODO*///		}
/*TODO*///	}
/*TODO*///	while (!file);
/*TODO*///
/*TODO*///	mame_pause(0);
/*TODO*///
/*TODO*///	if (file > 0)
/*TODO*///	{
/*TODO*///		if (request_loadsave == LOADSAVE_SAVE)
/*TODO*///			usrintf_showmessage("Save to position %c", file);
/*TODO*///		else
/*TODO*///			usrintf_showmessage("Load from position %c", file);
/*TODO*///		cpu_loadsave_schedule(request_loadsave, file);
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		if (request_loadsave == LOADSAVE_SAVE)
/*TODO*///			usrintf_showmessage("Save cancelled");
/*TODO*///		else
/*TODO*///			usrintf_showmessage("Load cancelled");
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///void ui_show_fps_temp(double seconds)
/*TODO*///{
/*TODO*///	if (!showfps)
/*TODO*///		showfpstemp = (int)(seconds * Machine->drv->frames_per_second);
/*TODO*///}
/*TODO*///
/*TODO*///void ui_show_fps_set(int show)
/*TODO*///{
/*TODO*///	if (show)
/*TODO*///	{
/*TODO*///		showfps = 1;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		showfps = 0;
/*TODO*///		showfpstemp = 0;
/*TODO*///		schedule_full_refresh();
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///int ui_show_fps_get(void)
/*TODO*///{
/*TODO*///	return showfps || showfpstemp;
/*TODO*///}
/*TODO*///
/*TODO*///void ui_show_profiler_set(int show)
/*TODO*///{
/*TODO*///	if (show)
/*TODO*///	{
/*TODO*///		show_profiler = 1;
/*TODO*///		profiler_start();
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		show_profiler = 0;
/*TODO*///		profiler_stop();
/*TODO*///		schedule_full_refresh();
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///int ui_show_profiler_get(void)
/*TODO*///{
/*TODO*///	return show_profiler;
/*TODO*///}
/*TODO*///
/*TODO*///void ui_display_fps(struct mame_bitmap *bitmap)
/*TODO*///{
/*TODO*///	const char *text, *end;
/*TODO*///	char textbuf[256];
/*TODO*///	int done = 0;
/*TODO*///	int y = 0;
/*TODO*///
/*TODO*///	/* if we're not currently displaying, skip it */
/*TODO*///	if (!showfps && !showfpstemp)
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* get the current FPS text */
/*TODO*///	text = osd_get_fps_text(mame_get_performance_info());
/*TODO*///
/*TODO*///	/* loop over lines */
/*TODO*///	while (!done)
/*TODO*///	{
/*TODO*///		/* find the end of this line and copy it to the text buf */
/*TODO*///		end = strchr(text, '\n');
/*TODO*///		if (end)
/*TODO*///		{
/*TODO*///			memcpy(textbuf, text, end - text);
/*TODO*///			textbuf[end - text] = 0;
/*TODO*///			text = end + 1;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			strcpy(textbuf, text);
/*TODO*///			done = 1;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* render */
/*TODO*///		ui_text(bitmap, textbuf, uirotwidth - strlen(textbuf) * uirotcharwidth, y);
/*TODO*///		y += uirotcharheight;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* update the temporary FPS display state */
/*TODO*///	if (showfpstemp)
/*TODO*///	{
/*TODO*///		showfpstemp--;
/*TODO*///		if (!showfps && showfpstemp == 0)
/*TODO*///			schedule_full_refresh();
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///int handle_user_interface(struct mame_bitmap *bitmap)
/*TODO*///{
/*TODO*///#ifdef MESS
/*TODO*///	extern int mess_pause_for_ui;
/*TODO*///#endif
/*TODO*///
/*TODO*///	/* if the user pressed F12, save the screen to a file */
/*TODO*///	if (input_ui_pressed(IPT_UI_SNAPSHOT))
/*TODO*///		save_screen_snapshot(bitmap);
/*TODO*///
/*TODO*///	/* This call is for the cheat, it must be called once a frame */
/*TODO*///	if (options.cheat) DoCheat(bitmap);
/*TODO*///
/*TODO*///	/* if the user pressed ESC, stop the emulation */
/*TODO*///	/* but don't quit if the setup menu is on screen */
/*TODO*///	if (setup_selected == 0 && input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	if (setup_selected == 0 && input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///	{
/*TODO*///		setup_selected = -1;
/*TODO*///		if (osd_selected != 0)
/*TODO*///		{
/*TODO*///			osd_selected = 0;	/* disable on screen display */
/*TODO*///			schedule_full_refresh();
/*TODO*///		}
/*TODO*///#ifdef XMAME
/*TODO*///		update_video_and_audio(); /* for rapid-fire support */
/*TODO*///#endif
/*TODO*///	}
/*TODO*///	if (setup_selected != 0) setup_selected = setup_menu(bitmap, setup_selected);
/*TODO*///
/*TODO*///#ifdef MAME_DEBUG
/*TODO*///	if (!mame_debug)
/*TODO*///#endif
/*TODO*///		if (osd_selected == 0 && input_ui_pressed(IPT_UI_ON_SCREEN_DISPLAY))
/*TODO*///		{
/*TODO*///			osd_selected = -1;
/*TODO*///			if (setup_selected != 0)
/*TODO*///			{
/*TODO*///				setup_selected = 0; /* disable setup menu */
/*TODO*///				schedule_full_refresh();
/*TODO*///			}
/*TODO*///		}
/*TODO*///	if (osd_selected != 0) osd_selected = on_screen_display(bitmap, osd_selected);
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///	if (keyboard_pressed_memory(KEYCODE_BACKSPACE))
/*TODO*///	{
/*TODO*///		if (jukebox_selected != -1)
/*TODO*///		{
/*TODO*///			jukebox_selected = -1;
/*TODO*///			cpu_halt(0,1);
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			jukebox_selected = 0;
/*TODO*///			cpu_halt(0,0);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	if (jukebox_selected != -1)
/*TODO*///	{
/*TODO*///		char buf[40];
/*TODO*///		watchdog_reset_w(0,0);
/*TODO*///		if (keyboard_pressed_memory(KEYCODE_LCONTROL))
/*TODO*///		{
/*TODO*///#include "cpu/z80/z80.h"
/*TODO*///			soundlatch_w(0,jukebox_selected);
/*TODO*///			cpu_set_irq_line(1,IRQ_LINE_NMI,PULSE_LINE);
/*TODO*///		}
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_RIGHT,8))
/*TODO*///		{
/*TODO*///			jukebox_selected = (jukebox_selected + 1) & 0xff;
/*TODO*///		}
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_LEFT,8))
/*TODO*///		{
/*TODO*///			jukebox_selected = (jukebox_selected - 1) & 0xff;
/*TODO*///		}
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_UP,8))
/*TODO*///		{
/*TODO*///			jukebox_selected = (jukebox_selected + 16) & 0xff;
/*TODO*///		}
/*TODO*///		if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
/*TODO*///		{
/*TODO*///			jukebox_selected = (jukebox_selected - 16) & 0xff;
/*TODO*///		}
/*TODO*///		sprintf(buf,"sound cmd %02x",jukebox_selected);
/*TODO*///		displaymessage(buf);
/*TODO*///	}
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///	/* if the user pressed F3, reset the emulation */
/*TODO*///	if (input_ui_pressed(IPT_UI_RESET_MACHINE))
/*TODO*///		machine_reset();
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_SAVE_STATE))
/*TODO*///		do_loadsave(bitmap, LOADSAVE_SAVE);
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_LOAD_STATE))
/*TODO*///		do_loadsave(bitmap, LOADSAVE_LOAD);
/*TODO*///
/*TODO*///#ifndef MESS
/*TODO*///	if (single_step || input_ui_pressed(IPT_UI_PAUSE)) /* pause the game */
/*TODO*///	{
/*TODO*///#else
/*TODO*///	if (setup_selected)
/*TODO*///		mess_pause_for_ui = 1;
/*TODO*///
/*TODO*///	if (single_step || input_ui_pressed(IPT_UI_PAUSE) || mess_pause_for_ui) /* pause the game */
/*TODO*///	{
/*TODO*///#endif
/*TODO*////*		osd_selected = 0;	   disable on screen display, since we are going   */
/*TODO*///							/* to change parameters affected by it */
/*TODO*///
/*TODO*///		if (single_step == 0)
/*TODO*///			mame_pause(1);
/*TODO*///
/*TODO*///		while (!input_ui_pressed(IPT_UI_PAUSE))
/*TODO*///		{
/*TODO*///#ifdef MAME_NET
/*TODO*///			osd_net_sync();
/*TODO*///#endif /* MAME_NET */
/*TODO*///			profiler_mark(PROFILER_VIDEO);
/*TODO*///			if (osd_skip_this_frame() == 0)
/*TODO*///			{
/*TODO*///				/* keep calling vh_screenrefresh() while paused so we can stuff */
/*TODO*///				/* debug code in there */
/*TODO*///				draw_screen();
/*TODO*///			}
/*TODO*///			profiler_mark(PROFILER_END);
/*TODO*///
/*TODO*///			if (input_ui_pressed(IPT_UI_SNAPSHOT))
/*TODO*///				save_screen_snapshot(bitmap);
/*TODO*///
/*TODO*///
/*TODO*///			if (input_ui_pressed(IPT_UI_SAVE_STATE))
/*TODO*///				do_loadsave(bitmap, LOADSAVE_SAVE);
/*TODO*///
/*TODO*///			if (input_ui_pressed(IPT_UI_LOAD_STATE))
/*TODO*///				do_loadsave(bitmap, LOADSAVE_LOAD);
/*TODO*///
/*TODO*///			/* if the user pressed F4, show the character set */
/*TODO*///			if (input_ui_pressed(IPT_UI_SHOW_GFX))
/*TODO*///				showcharset(bitmap);
/*TODO*///
/*TODO*///			if (setup_selected == 0 && input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///				return 1;
/*TODO*///
/*TODO*///			if (setup_selected == 0 && input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///			{
/*TODO*///				setup_selected = -1;
/*TODO*///				if (osd_selected != 0)
/*TODO*///				{
/*TODO*///					osd_selected = 0;	/* disable on screen display */
/*TODO*///					schedule_full_refresh();
/*TODO*///				}
/*TODO*///			}
/*TODO*///			if (setup_selected != 0) setup_selected = setup_menu(bitmap, setup_selected);
/*TODO*///
/*TODO*///#ifdef MAME_DEBUG
/*TODO*///			if (!mame_debug)
/*TODO*///#endif
/*TODO*///				if (osd_selected == 0 && input_ui_pressed(IPT_UI_ON_SCREEN_DISPLAY))
/*TODO*///				{
/*TODO*///					osd_selected = -1;
/*TODO*///					if (setup_selected != 0)
/*TODO*///					{
/*TODO*///						setup_selected = 0; /* disable setup menu */
/*TODO*///						schedule_full_refresh();
/*TODO*///					}
/*TODO*///				}
/*TODO*///			if (osd_selected != 0) osd_selected = on_screen_display(bitmap, osd_selected);
/*TODO*///
/*TODO*///			if (options.cheat) DisplayWatches(bitmap);
/*TODO*///
/*TODO*///			/* show popup message if any */
/*TODO*///			if (messagecounter > 0)
/*TODO*///			{
/*TODO*///				displaymessage(bitmap, messagetext);
/*TODO*///
/*TODO*///				if (--messagecounter == 0)
/*TODO*///					schedule_full_refresh();
/*TODO*///			}
/*TODO*///
/*TODO*///			update_video_and_audio();
/*TODO*///			reset_partial_updates();
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///			if (!setup_selected && mess_pause_for_ui)
/*TODO*///			{
/*TODO*///				mess_pause_for_ui = 0;
/*TODO*///				break;
/*TODO*///			}
/*TODO*///#endif /* MESS */
/*TODO*///		}
/*TODO*///
/*TODO*///		if (code_pressed(KEYCODE_LSHIFT) || code_pressed(KEYCODE_RSHIFT))
/*TODO*///			single_step = 1;
/*TODO*///		else
/*TODO*///		{
/*TODO*///			single_step = 0;
/*TODO*///			mame_pause(0);
/*TODO*///		}
/*TODO*///
/*TODO*///		schedule_full_refresh();
/*TODO*///	}
/*TODO*///
/*TODO*///#if defined(__sgi) && !defined(MESS)
/*TODO*///	game_paused = 0;
/*TODO*///#endif
/*TODO*///
/*TODO*///	/* show popup message if any */
/*TODO*///	if (messagecounter > 0)
/*TODO*///	{
/*TODO*///		displaymessage(bitmap, messagetext);
/*TODO*///
/*TODO*///		if (--messagecounter == 0)
/*TODO*///			schedule_full_refresh();
/*TODO*///	}
/*TODO*///
/*TODO*///
/*TODO*///	if (input_ui_pressed(IPT_UI_SHOW_PROFILER))
/*TODO*///	{
/*TODO*///		ui_show_profiler_set(!ui_show_profiler_get());
/*TODO*///	}
/*TODO*///
/*TODO*///	if (show_profiler) profiler_show(bitmap);
/*TODO*///
/*TODO*///
/*TODO*///	/* show FPS display? */
/*TODO*///	if (input_ui_pressed(IPT_UI_SHOW_FPS))
/*TODO*///	{
/*TODO*///		/* toggle fps */
/*TODO*///		ui_show_fps_set(!ui_show_fps_get());
/*TODO*///	}
/*TODO*///
/*TODO*///
/*TODO*///	/* if the user pressed F4, show the character set */
/*TODO*///	if (input_ui_pressed(IPT_UI_SHOW_GFX))
/*TODO*///	{
/*TODO*///		osd_sound_enable(0);
/*TODO*///
/*TODO*///		showcharset(bitmap);
/*TODO*///
/*TODO*///		osd_sound_enable(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* if the user pressed F1 and this is a lightgun game, toggle the crosshair */
/*TODO*///	if (input_ui_pressed(IPT_UI_TOGGLE_CROSSHAIR))
/*TODO*///	{
/*TODO*///		drawgfx_toggle_crosshair();
/*TODO*///	}
/*TODO*///
/*TODO*///	/* add the FPS counter */
/*TODO*///	ui_display_fps(bitmap);
/*TODO*///
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///void init_user_interface(void)
/*TODO*///{
/*TODO*///	extern int snapno;	/* in common.c */
/*TODO*///
/*TODO*///	snapno = 0; /* reset snapshot counter */
/*TODO*///
/*TODO*///	/* clear the input memory */
/*TODO*///	while (code_read_async() != CODE_NONE) {};
/*TODO*///
/*TODO*///	setup_menu_init();
/*TODO*///	setup_selected = 0;
/*TODO*///
/*TODO*///	onscrd_init();
/*TODO*///	osd_selected = 0;
/*TODO*///
/*TODO*///	jukebox_selected = -1;
/*TODO*///
/*TODO*///	single_step = 0;
/*TODO*///}
/*TODO*///
/*TODO*///int onscrd_active(void)
/*TODO*///{
/*TODO*///	return osd_selected;
/*TODO*///}
/*TODO*///
/*TODO*///int setup_active(void)
/*TODO*///{
/*TODO*///	return setup_selected;
/*TODO*///}
/*TODO*///
/*TODO*///#if defined(__sgi) && !defined(MESS)
/*TODO*///
/*TODO*////* Return if the game is paused or not */
/*TODO*///int is_game_paused(void)
/*TODO*///{
/*TODO*///	return game_paused;
/*TODO*///}
/*TODO*///
/*TODO*///#endif
/*TODO*///    
}
