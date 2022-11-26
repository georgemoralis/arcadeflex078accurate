/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

public class ui_textH {
/*TODO*////*********************************************************************
/*TODO*///
/*TODO*///  ui_text.h
/*TODO*///
/*TODO*///  Functions used to retrieve text used by MAME, to aid in
/*TODO*///  translation.
/*TODO*///
/*TODO*///*********************************************************************/
/*TODO*///
/*TODO*///#ifndef UI_TEXT_H
/*TODO*///#define UI_TEXT_H
/*TODO*///
/*TODO*///#include "driver.h"
/*TODO*///
/*TODO*////* Important: this must match the default_text list in ui_text.c! */
/*TODO*///enum
/*TODO*///{
/*TODO*///	UI_mame = 0,
/*TODO*///
/*TODO*///	/* copyright stuff */
/*TODO*///	UI_copyright1,
/*TODO*///	UI_copyright2,
/*TODO*///	UI_copyright3,
/*TODO*///
/*TODO*///	/* misc menu stuff */
/*TODO*///	UI_returntomain,
/*TODO*///	UI_returntoprior,
/*TODO*///	UI_anykey,
/*TODO*///	UI_on,
/*TODO*///	UI_off,
/*TODO*///	UI_NA,
/*TODO*///	UI_OK,
/*TODO*///	UI_INVALID,
/*TODO*///	UI_none,
/*TODO*///	UI_cpu,
/*TODO*///	UI_address,
/*TODO*///	UI_value,
/*TODO*///	UI_sound,
/*TODO*///	UI_sound_lc, /* lower-case version */
/*TODO*///	UI_stereo,
/*TODO*///	UI_vectorgame,
/*TODO*///	UI_screenres,
/*TODO*///	UI_text,
/*TODO*///	UI_volume,
/*TODO*///	UI_relative,
/*TODO*///	UI_allchannels,
/*TODO*///	UI_brightness,
/*TODO*///	UI_gamma,
/*TODO*///	UI_vectorflicker,
/*TODO*///	UI_vectorintensity,
/*TODO*///	UI_overclock,
/*TODO*///	UI_allcpus,
/*TODO*///	UI_historymissing,
/*TODO*///
/*TODO*///	/* special characters */
/*TODO*///	UI_leftarrow,
/*TODO*///	UI_rightarrow,
/*TODO*///	UI_uparrow,
/*TODO*///	UI_downarrow,
/*TODO*///	UI_lefthilight,
/*TODO*///	UI_righthilight,
/*TODO*///
/*TODO*///	/* warnings */
/*TODO*///	UI_knownproblems,
/*TODO*///	UI_imperfectcolors,
/*TODO*///	UI_wrongcolors,
/*TODO*///	UI_imperfectgraphics,
/*TODO*///	UI_imperfectsound,
/*TODO*///	UI_nosound,
/*TODO*///	UI_nococktail,
/*TODO*///	UI_brokengame,
/*TODO*///	UI_brokenprotection,
/*TODO*///	UI_workingclones,
/*TODO*///	UI_typeok,
/*TODO*///
/*TODO*///	/* main menu */
/*TODO*///	UI_inputgeneral,
/*TODO*///	UI_dipswitches,
/*TODO*///	UI_analogcontrols,
/*TODO*///	UI_calibrate,
/*TODO*///	UI_bookkeeping,
/*TODO*///	UI_inputspecific,
/*TODO*///	UI_gameinfo,
/*TODO*///	UI_history,
/*TODO*///	UI_resetgame,
/*TODO*///	UI_returntogame,
/*TODO*///	UI_cheat,
/*TODO*///	UI_memorycard,
/*TODO*///
/*TODO*///	/* input stuff */
/*TODO*///	UI_keyjoyspeed,
/*TODO*///	UI_reverse,
/*TODO*///	UI_sensitivity,
/*TODO*///
/*TODO*///	/* stats */
/*TODO*///	UI_tickets,
/*TODO*///	UI_coin,
/*TODO*///	UI_locked,
/*TODO*///
/*TODO*///	/* memory card */
/*TODO*///	UI_loadcard,
/*TODO*///	UI_ejectcard,
/*TODO*///	UI_createcard,
/*TODO*///	UI_loadfailed,
/*TODO*///	UI_loadok,
/*TODO*///	UI_cardejected,
/*TODO*///	UI_cardcreated,
/*TODO*///	UI_cardcreatedfailed,
/*TODO*///	UI_cardcreatedfailed2,
/*TODO*///	UI_carderror,
/*TODO*///
/*TODO*///	/* cheat stuff */
/*TODO*///	UI_enablecheat,
/*TODO*///	UI_addeditcheat,
/*TODO*///	UI_startcheat,
/*TODO*///	UI_continuesearch,
/*TODO*///	UI_viewresults,
/*TODO*///	UI_restoreresults,
/*TODO*///	UI_memorywatch,
/*TODO*///	UI_generalhelp,
/*TODO*///	UI_options,
/*TODO*///	UI_reloaddatabase,
/*TODO*///	UI_watchpoint,
/*TODO*///	UI_disabled,
/*TODO*///	UI_cheats,
/*TODO*///	UI_watchpoints,
/*TODO*///	UI_moreinfo,
/*TODO*///	UI_moreinfoheader,
/*TODO*///	UI_cheatname,
/*TODO*///	UI_cheatdescription,
/*TODO*///	UI_cheatactivationkey,
/*TODO*///	UI_code,
/*TODO*///	UI_max,
/*TODO*///	UI_set,
/*TODO*///	UI_conflict_found,
/*TODO*///	UI_no_help_available,
/*TODO*///
/*TODO*///	/* watchpoint stuff */
/*TODO*///	UI_watchlength,
/*TODO*///	UI_watchdisplaytype,
/*TODO*///	UI_watchlabeltype,
/*TODO*///	UI_watchlabel,
/*TODO*///	UI_watchx,
/*TODO*///	UI_watchy,
/*TODO*///	UI_watch,
/*TODO*///
/*TODO*///	UI_hex,
/*TODO*///	UI_decimal,
/*TODO*///	UI_binary,
/*TODO*///
/*TODO*///	/* search stuff */
/*TODO*///	UI_search_lives,
/*TODO*///	UI_search_timers,
/*TODO*///	UI_search_energy,
/*TODO*///	UI_search_status,
/*TODO*///	UI_search_slow,
/*TODO*///	UI_search_speed,
/*TODO*///	UI_search_speed_fast,
/*TODO*///	UI_search_speed_medium,
/*TODO*///	UI_search_speed_slow,
/*TODO*///	UI_search_speed_veryslow,
/*TODO*///	UI_search_speed_allmemory,
/*TODO*///	UI_search_select_memory_areas,
/*TODO*///	UI_search_matches_found,
/*TODO*///	UI_search_noinit,
/*TODO*///	UI_search_nosave,
/*TODO*///	UI_search_done,
/*TODO*///	UI_search_OK,
/*TODO*///	UI_search_select_value,
/*TODO*///	UI_search_all_values_saved,
/*TODO*///	UI_search_one_match_found_added,
/*TODO*///
/*TODO*///	UI_last_mame_entry
/*TODO*///};
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///#include "mui_text.h"
/*TODO*///#endif
/*TODO*///
/*TODO*///struct lang_struct
/*TODO*///{
/*TODO*///	int version;
/*TODO*///	int multibyte;			/* UNUSED: 1 if this is a multibyte font/language */
/*TODO*///	UINT8 *fontdata;		/* pointer to the raw font data to be decoded */
/*TODO*///	UINT16 fontglyphs;		/* total number of glyps in the external font - 1 */
/*TODO*///	char langname[255];
/*TODO*///	char fontname[255];
/*TODO*///	char author[255];
/*TODO*///};
/*TODO*///
/*TODO*///extern struct lang_struct lang;
/*TODO*///
/*TODO*///int uistring_init (mame_file *language_file);
/*TODO*///
/*TODO*///const char * ui_getstring (int string_num);
/*TODO*///
/*TODO*///#endif /* UI_TEXT_H */
/*TODO*///
/*TODO*///
}
