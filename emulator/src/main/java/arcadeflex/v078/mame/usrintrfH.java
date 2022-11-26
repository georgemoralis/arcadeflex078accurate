/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

public class usrintrfH {
/*TODO*////*********************************************************************
/*TODO*///
/*TODO*///  usrintrf.h
/*TODO*///
/*TODO*///  Functions used to handle MAME's crude user interface.
/*TODO*///
/*TODO*///*********************************************************************/
/*TODO*///
/*TODO*///#ifndef USRINTRF_H
/*TODO*///#define USRINTRF_H
/*TODO*///
/*TODO*///struct DisplayText
/*TODO*///{
/*TODO*///	const char *text;	/* 0 marks the end of the array */
/*TODO*///	int color;	/* see #defines below */
/*TODO*///	int x;
/*TODO*///	int y;
/*TODO*///};
/*TODO*///
/*TODO*///#define UI_COLOR_NORMAL 0	/* white on black text */
/*TODO*///#define UI_COLOR_INVERSE 1	/* black on white text */
/*TODO*///
/*TODO*///#define SEL_BITS 12		/* main menu selection mask */
/*TODO*///#define SEL_BITS2 4		/* submenu selection masks */
/*TODO*///#define SEL_MASK ((1<<SEL_BITS)-1)
/*TODO*///#define SEL_MASK2 ((1<<SEL_BITS2)-1)
/*TODO*///
/*TODO*///extern UINT8 ui_dirty;
/*TODO*///
/*TODO*///struct GfxElement *builduifont(void);
/*TODO*///void pick_uifont_colors(void);
/*TODO*///void displaytext(struct mame_bitmap *bitmap,const struct DisplayText *dt);
/*TODO*///
/*TODO*///void ui_drawchar(struct mame_bitmap *dest, int ch, int color, int sx, int sy);
/*TODO*///void ui_text(struct mame_bitmap *bitmap,const char *buf,int x,int y);
/*TODO*///void ui_drawbox(struct mame_bitmap *bitmap,int leftx,int topy,int width,int height);
/*TODO*///void ui_displaymessagewindow(struct mame_bitmap *bitmap,const char *text);
/*TODO*///void ui_displaymenu(struct mame_bitmap *bitmap,const char **items,const char **subitems,char *flag,int selected,int arrowize_subitem);
/*TODO*///void ui_display_fps(struct mame_bitmap *bitmap);
/*TODO*///int showcopyright(struct mame_bitmap *bitmap);
/*TODO*///int showgamewarnings(struct mame_bitmap *bitmap);
/*TODO*///int showgameinfo(struct mame_bitmap *bitmap);
/*TODO*///void set_ui_visarea (int xmin, int ymin, int xmax, int ymax);
/*TODO*///
/*TODO*///void init_user_interface(void);
/*TODO*///int handle_user_interface(struct mame_bitmap *bitmap);
/*TODO*///
/*TODO*///void ui_show_fps_temp(double seconds);
/*TODO*///void ui_show_fps_set(int show);
/*TODO*///int ui_show_fps_get(void);
/*TODO*///
/*TODO*///void ui_show_profiler_set(int show);
/*TODO*///int ui_show_profiler_get(void);
/*TODO*///
/*TODO*///int onscrd_active(void);
/*TODO*///int setup_active(void);
/*TODO*///
/*TODO*///#if defined(__sgi) && ! defined(MESS)
/*TODO*///int is_game_paused(void);
/*TODO*///#endif
/*TODO*///
/*TODO*///void switch_ui_orientation(struct mame_bitmap *bitmap);
/*TODO*///void switch_debugger_orientation(struct mame_bitmap *bitmap);
/*TODO*///void switch_true_orientation(struct mame_bitmap *bitmap);
/*TODO*///
/*TODO*///#ifdef __GNUC__
/*TODO*///void CLIB_DECL usrintf_showmessage(const char *text,...)
/*TODO*///      __attribute__ ((format (printf, 1, 2)));
/*TODO*///
/*TODO*///void CLIB_DECL usrintf_showmessage_secs(int seconds, const char *text,...)
/*TODO*///      __attribute__ ((format (printf, 2, 3)));
/*TODO*///#else
/*TODO*///void CLIB_DECL usrintf_showmessage(const char *text,...);
/*TODO*///void CLIB_DECL usrintf_showmessage_secs(int seconds, const char *text,...);
/*TODO*///#endif
/*TODO*///
/*TODO*///#endif
/*TODO*///    
}
