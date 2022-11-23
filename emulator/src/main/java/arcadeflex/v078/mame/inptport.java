/**
 * ported to 0.78
 */
package arcadeflex.v078.mame;

//java imports
import java.util.HashMap;

public class inptport {

    /*TODO*////***************************************************************************
/*TODO*///
/*TODO*///  inptport.c
/*TODO*///
/*TODO*///  Input ports handling
/*TODO*///
/*TODO*///TODO:	remove the 1 analog device per port limitation
/*TODO*///		support for inputports producing interrupts
/*TODO*///		support for extra "real" hardware (PC throttle's, spinners etc)
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///#include <math.h>
/*TODO*///#include "driver.h"
/*TODO*///#include "config.h"
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///#include "inputx.h"
/*TODO*///#endif
/*TODO*///
/*TODO*////* MAMEnet support */
/*TODO*///#if defined MAME_NET || defined XMAME_NET
/*TODO*///#include "network.h"
/*TODO*///
/*TODO*///static unsigned short input_port_defaults[MAX_INPUT_PORTS];
/*TODO*///static int default_player;
/*TODO*///static int analog_player_port[MAX_INPUT_PORTS];
/*TODO*///#endif /* MAME_NET */
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Extern declarations
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///extern void *record;
/*TODO*///extern void *playback;
/*TODO*///
/*TODO*///extern unsigned int dispensed_tickets;
/*TODO*///extern unsigned int coins[COIN_COUNTERS];
/*TODO*///extern unsigned int lastcoin[COIN_COUNTERS];
/*TODO*///extern unsigned int coinlockedout[COIN_COUNTERS];
/*TODO*///
/*TODO*///static unsigned short input_port_value[MAX_INPUT_PORTS];
/*TODO*///static unsigned short input_vblank[MAX_INPUT_PORTS];
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Local variables
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////* Assuming a maxium of one analog input device per port BW 101297 */
/*TODO*///static struct InputPort *input_analog[MAX_INPUT_PORTS];
/*TODO*///static int input_analog_current_value[MAX_INPUT_PORTS],input_analog_previous_value[MAX_INPUT_PORTS];
/*TODO*///static int input_analog_init[MAX_INPUT_PORTS];
/*TODO*///static int input_analog_scale[MAX_INPUT_PORTS];
/*TODO*///
/*TODO*///static InputCode analogjoy_input[OSD_MAX_JOY_ANALOG][MAX_ANALOG_AXES];	/* [player#][mame axis#] array */
/*TODO*///
/*TODO*///static int mouse_delta_axis[OSD_MAX_JOY_ANALOG][MAX_ANALOG_AXES];
/*TODO*///static int lightgun_delta_axis[OSD_MAX_JOY_ANALOG][MAX_ANALOG_AXES];
/*TODO*///static int analog_current_axis[OSD_MAX_JOY_ANALOG][MAX_ANALOG_AXES];
/*TODO*///static int analog_previous_axis[OSD_MAX_JOY_ANALOG][MAX_ANALOG_AXES];
/*TODO*///
/*TODO*///#if 0
/*TODO*///static int mouse_delta_x[OSD_MAX_JOY_ANALOG], mouse_delta_y[OSD_MAX_JOY_ANALOG];			/* replaced by mouse_delta_axis[][] */
/*TODO*///static int lightgun_delta_x[OSD_MAX_JOY_ANALOG], lightgun_delta_y[OSD_MAX_JOY_ANALOG];			/* replaced by lightgun_delta_axis[][] */
/*TODO*///static int analog_current_x[OSD_MAX_JOY_ANALOG], analog_current_y[OSD_MAX_JOY_ANALOG];		/* replaced by analog_current_axis[][] */
/*TODO*///static int analog_previous_x[OSD_MAX_JOY_ANALOG], analog_previous_y[OSD_MAX_JOY_ANALOG];	/* replaced by analog_previous_axis[][] */
/*TODO*///#endif
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///  Configuration load/save
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
    public static HashMap<String, String> ipdn_defaultstrings = new HashMap<String, String>() {
        {
            put("Off", "Off");
            put("On", "On");
            put("No", "No");
            put("Yes", "Yes");
            put("Lives", "Lives");
            put("Bonus_Life", "Bonus Life");
            put("Difficulty", "Difficulty");
            put("Demo_Sounds", "Demo Sounds");
            put("Coinage", "Coinage");
            put("9C_1C", "9 Coins/1 Credit");
            put("8C_1C", "8 Coins/1 Credit");
            put("7C_1C", "7 Coins/1 Credit");
            put("6C_1C", "6 Coins/1 Credit");
            put("5C_1C", "5 Coins/1 Credit");
            put("4C_1C", "4 Coins/1 Credit");
            put("3C_1C", "3 Coins/1 Credit");
            put("8C_3C", "8 Coins/3 Credits");
            put("4C_2C", "4 Coins/2 Credits");
            put("2C_1C", "2 Coins/1 Credit");
            put("5C_3C", "5 Coins/3 Credits");
            put("3C_2C", "3 Coins/2 Credits");
            put("4C_3C", "4 Coins/3 Credits");
            put("4C_4C", "4 Coins/4 Credits");
            put("3C_3C", "3 Coins/3 Credits");
            put("2C_2C", "2 Coins/2 Credits");
            put("1C_1C", "1 Coin/1 Credit");
            put("4C_5C", "4 Coins/5 Credits");
            put("3C_4C", "3 Coins/4 Credits");
            put("2C_3C", "2 Coins/3 Credits");
            put("4C_7C", "4 Coins/7 Credits");
            put("2C_4C", "2 Coins/4 Credits");
            put("1C_2C", "1 Coin/2 Credits");
            put("2C_5C", "2 Coins/5 Credits");
            put("2C_6C", "2 Coins/6 Credits");
            put("1C_3C", "1 Coin/3 Credits");
            put("2C_7C", "2 Coins/7 Credits");
            put("2C_8C", "2 Coins/8 Credits");
            put("1C_4C", "1 Coin/4 Credits");
            put("1C_5C", "1 Coin/5 Credits");
            put("1C_6C", "1 Coin/6 Credits");
            put("1C_7C", "1 Coin/7 Credits");
            put("1C_8C", "1 Coin/8 Credits");
            put("1C_9C", "1 Coin/9 Credits");
            put("Free_Play", "Free Play");
            put("Cabinet", "Cabinet");
            put("Upright", "Upright");
            put("Cocktail", "Cocktail");
            put("Flip_Screen", "Flip Screen");
            put("Service_Mode", "Service Mode");
            put("Unused", "Unused");
            put("Unknown", "Unknown");
        }
    };
    /*TODO*///
/*TODO*///struct ipd inputport_defaults[] =
/*TODO*///{
/*TODO*///	{ IPT_UI_CONFIGURE,         "Config Menu",			SEQ_DEF_1(KEYCODE_TAB) },
/*TODO*///	{ IPT_UI_ON_SCREEN_DISPLAY, "On Screen Display",	SEQ_DEF_1(KEYCODE_TILDE) },
/*TODO*///	{ IPT_UI_PAUSE,             "Pause",				SEQ_DEF_1(KEYCODE_P) },
/*TODO*///	{ IPT_UI_RESET_MACHINE,     "Reset Game",			SEQ_DEF_1(KEYCODE_F3) },
/*TODO*///	{ IPT_UI_SHOW_GFX,          "Show Gfx",				SEQ_DEF_1(KEYCODE_F4) },
/*TODO*///	{ IPT_UI_FRAMESKIP_DEC,     "Frameskip Dec",		SEQ_DEF_1(KEYCODE_F8) },
/*TODO*///	{ IPT_UI_FRAMESKIP_INC,     "Frameskip Inc",		SEQ_DEF_1(KEYCODE_F9) },
/*TODO*///	{ IPT_UI_THROTTLE,          "Throttle",				SEQ_DEF_1(KEYCODE_F10) },
/*TODO*///	{ IPT_UI_SHOW_FPS,          "Show FPS",				SEQ_DEF_5(KEYCODE_F11, CODE_NOT, KEYCODE_LCONTROL, CODE_NOT, KEYCODE_LSHIFT) },
/*TODO*///	{ IPT_UI_SHOW_PROFILER,     "Show Profiler",		SEQ_DEF_2(KEYCODE_F11, KEYCODE_LSHIFT) },
/*TODO*///#ifdef MESS
/*TODO*///	{ IPT_UI_TOGGLE_UI,         "UI Toggle",			SEQ_DEF_1(KEYCODE_SCRLOCK) },
/*TODO*///#endif
/*TODO*///	{ IPT_UI_SNAPSHOT,          "Save Snapshot",		SEQ_DEF_1(KEYCODE_F12) },
/*TODO*///	{ IPT_UI_TOGGLE_CHEAT,      "Toggle Cheat",			SEQ_DEF_1(KEYCODE_F6) },
/*TODO*///	{ IPT_UI_UP,                "UI Up",				SEQ_DEF_3(KEYCODE_UP, CODE_OR, JOYCODE_1_UP) },
/*TODO*///	{ IPT_UI_DOWN,              "UI Down",				SEQ_DEF_3(KEYCODE_DOWN, CODE_OR, JOYCODE_1_DOWN) },
/*TODO*///	{ IPT_UI_LEFT,              "UI Left",				SEQ_DEF_3(KEYCODE_LEFT, CODE_OR, JOYCODE_1_LEFT) },
/*TODO*///	{ IPT_UI_RIGHT,             "UI Right",				SEQ_DEF_3(KEYCODE_RIGHT, CODE_OR, JOYCODE_1_RIGHT) },
/*TODO*///	{ IPT_UI_SELECT,            "UI Select",			SEQ_DEF_3(KEYCODE_ENTER, CODE_OR, JOYCODE_1_BUTTON1) },
/*TODO*///	{ IPT_UI_CANCEL,            "UI Cancel",			SEQ_DEF_1(KEYCODE_ESC) },
/*TODO*///	{ IPT_UI_PAN_UP,            "Pan Up",				SEQ_DEF_3(KEYCODE_PGUP, CODE_NOT, KEYCODE_LSHIFT) },
/*TODO*///	{ IPT_UI_PAN_DOWN,          "Pan Down",				SEQ_DEF_3(KEYCODE_PGDN, CODE_NOT, KEYCODE_LSHIFT) },
/*TODO*///	{ IPT_UI_PAN_LEFT,          "Pan Left",				SEQ_DEF_2(KEYCODE_PGUP, KEYCODE_LSHIFT) },
/*TODO*///	{ IPT_UI_PAN_RIGHT,         "Pan Right",			SEQ_DEF_2(KEYCODE_PGDN, KEYCODE_LSHIFT) },
/*TODO*///	{ IPT_UI_TOGGLE_DEBUG,      "Toggle Debugger",		SEQ_DEF_1(KEYCODE_F5) },
/*TODO*///	{ IPT_UI_SAVE_STATE,        "Save State",			SEQ_DEF_2(KEYCODE_F7, KEYCODE_LSHIFT) },
/*TODO*///	{ IPT_UI_LOAD_STATE,        "Load State",			SEQ_DEF_3(KEYCODE_F7, CODE_NOT, KEYCODE_LSHIFT) },
/*TODO*///	{ IPT_UI_ADD_CHEAT,			"Add Cheat",			SEQ_DEF_1(KEYCODE_A) },
/*TODO*///	{ IPT_UI_DELETE_CHEAT,		"Delete Cheat",			SEQ_DEF_1(KEYCODE_D) },
/*TODO*///	{ IPT_UI_SAVE_CHEAT,		"Save Cheat",			SEQ_DEF_1(KEYCODE_S) },
/*TODO*///	{ IPT_UI_WATCH_VALUE,		"Watch Value",			SEQ_DEF_1(KEYCODE_W) },
/*TODO*///	{ IPT_UI_EDIT_CHEAT,		"Edit Cheat",			SEQ_DEF_1(KEYCODE_E) },
/*TODO*///	{ IPT_UI_TOGGLE_CROSSHAIR,	"Toggle Crosshair",		SEQ_DEF_1(KEYCODE_F1) },
/*TODO*///	{ IPT_START1, "1 Player Start",  SEQ_DEF_3(KEYCODE_1, CODE_OR, JOYCODE_1_START) },
/*TODO*///	{ IPT_START2, "2 Players Start", SEQ_DEF_3(KEYCODE_2, CODE_OR, JOYCODE_2_START) },
/*TODO*///	{ IPT_START3, "3 Players Start", SEQ_DEF_3(KEYCODE_3, CODE_OR, JOYCODE_3_START) },
/*TODO*///	{ IPT_START4, "4 Players Start", SEQ_DEF_3(KEYCODE_4, CODE_OR, JOYCODE_4_START) },
/*TODO*///	{ IPT_START5, "5 Players Start", SEQ_DEF_0 },
/*TODO*///	{ IPT_START6, "6 Players Start", SEQ_DEF_0 },
/*TODO*///	{ IPT_START7, "7 Players Start", SEQ_DEF_0 },
/*TODO*///	{ IPT_START8, "8 Players Start", SEQ_DEF_0 },
/*TODO*///	{ IPT_COIN1,  "Coin 1",          SEQ_DEF_3(KEYCODE_5, CODE_OR, JOYCODE_1_SELECT) },
/*TODO*///	{ IPT_COIN2,  "Coin 2",          SEQ_DEF_3(KEYCODE_6, CODE_OR, JOYCODE_2_SELECT) },
/*TODO*///	{ IPT_COIN3,  "Coin 3",          SEQ_DEF_3(KEYCODE_7, CODE_OR, JOYCODE_3_SELECT) },
/*TODO*///	{ IPT_COIN4,  "Coin 4",          SEQ_DEF_3(KEYCODE_8, CODE_OR, JOYCODE_4_SELECT) },
/*TODO*///	{ IPT_COIN5,  "Coin 5",          SEQ_DEF_0 },
/*TODO*///	{ IPT_COIN6,  "Coin 6",          SEQ_DEF_0 },
/*TODO*///	{ IPT_COIN7,  "Coin 7",          SEQ_DEF_0 },
/*TODO*///	{ IPT_COIN8,  "Coin 8",          SEQ_DEF_0 },
/*TODO*///	{ IPT_SERVICE1, "Service 1",     SEQ_DEF_1(KEYCODE_9) },
/*TODO*///	{ IPT_SERVICE2, "Service 2",     SEQ_DEF_1(KEYCODE_0) },
/*TODO*///	{ IPT_SERVICE3, "Service 3",     SEQ_DEF_1(KEYCODE_MINUS) },
/*TODO*///	{ IPT_SERVICE4, "Service 4",     SEQ_DEF_1(KEYCODE_EQUALS) },
/*TODO*///#ifndef MESS
/*TODO*///	{ IPT_TILT,   "Tilt",            SEQ_DEF_1(KEYCODE_T) },
/*TODO*///#else
/*TODO*///	{ IPT_TILT,   "Tilt",            SEQ_DEF_0 },
/*TODO*///#endif
/*TODO*///
/*TODO*///	{ IPT_JOYSTICK_UP         | IPF_PLAYER1, "P1 Up",          SEQ_DEF_3(KEYCODE_UP, CODE_OR, JOYCODE_1_UP)    },
/*TODO*///	{ IPT_JOYSTICK_DOWN       | IPF_PLAYER1, "P1 Down",        SEQ_DEF_3(KEYCODE_DOWN, CODE_OR, JOYCODE_1_DOWN)  },
/*TODO*///	{ IPT_JOYSTICK_LEFT       | IPF_PLAYER1, "P1 Left",        SEQ_DEF_3(KEYCODE_LEFT, CODE_OR, JOYCODE_1_LEFT)  },
/*TODO*///	{ IPT_JOYSTICK_RIGHT      | IPF_PLAYER1, "P1 Right",       SEQ_DEF_3(KEYCODE_RIGHT, CODE_OR, JOYCODE_1_RIGHT) },
/*TODO*///	{ IPT_BUTTON1             | IPF_PLAYER1, "P1 Button 1",    SEQ_DEF_5(KEYCODE_LCONTROL, CODE_OR, JOYCODE_1_BUTTON1, CODE_OR, JOYCODE_MOUSE_1_BUTTON1) },
/*TODO*///	{ IPT_BUTTON2             | IPF_PLAYER1, "P1 Button 2",    SEQ_DEF_5(KEYCODE_LALT, CODE_OR, JOYCODE_1_BUTTON2, CODE_OR, JOYCODE_MOUSE_1_BUTTON3) },
/*TODO*///	{ IPT_BUTTON3             | IPF_PLAYER1, "P1 Button 3",    SEQ_DEF_5(KEYCODE_SPACE, CODE_OR, JOYCODE_1_BUTTON3, CODE_OR, JOYCODE_MOUSE_1_BUTTON2) },
/*TODO*///	{ IPT_BUTTON4             | IPF_PLAYER1, "P1 Button 4",    SEQ_DEF_3(KEYCODE_LSHIFT, CODE_OR, JOYCODE_1_BUTTON4) },
/*TODO*///	{ IPT_BUTTON5             | IPF_PLAYER1, "P1 Button 5",    SEQ_DEF_3(KEYCODE_Z, CODE_OR, JOYCODE_1_BUTTON5) },
/*TODO*///	{ IPT_BUTTON6             | IPF_PLAYER1, "P1 Button 6",    SEQ_DEF_3(KEYCODE_X, CODE_OR, JOYCODE_1_BUTTON6) },
/*TODO*///	{ IPT_BUTTON7             | IPF_PLAYER1, "P1 Button 7",    SEQ_DEF_3(KEYCODE_C, CODE_OR, JOYCODE_1_BUTTON7) },
/*TODO*///	{ IPT_BUTTON8             | IPF_PLAYER1, "P1 Button 8",    SEQ_DEF_3(KEYCODE_V, CODE_OR, JOYCODE_1_BUTTON8) },
/*TODO*///	{ IPT_BUTTON9             | IPF_PLAYER1, "P1 Button 9",    SEQ_DEF_3(KEYCODE_B, CODE_OR, JOYCODE_1_BUTTON9) },
/*TODO*///	{ IPT_BUTTON10            | IPF_PLAYER1, "P1 Button 10",   SEQ_DEF_3(KEYCODE_N, CODE_OR, JOYCODE_1_BUTTON10) },
/*TODO*///	{ IPT_JOYSTICKRIGHT_UP    | IPF_PLAYER1, "P1 Right/Up",    SEQ_DEF_3(KEYCODE_I, CODE_OR, JOYCODE_1_BUTTON2) },
/*TODO*///	{ IPT_JOYSTICKRIGHT_DOWN  | IPF_PLAYER1, "P1 Right/Down",  SEQ_DEF_3(KEYCODE_K, CODE_OR, JOYCODE_1_BUTTON3) },
/*TODO*///	{ IPT_JOYSTICKRIGHT_LEFT  | IPF_PLAYER1, "P1 Right/Left",  SEQ_DEF_3(KEYCODE_J, CODE_OR, JOYCODE_1_BUTTON1) },
/*TODO*///	{ IPT_JOYSTICKRIGHT_RIGHT | IPF_PLAYER1, "P1 Right/Right", SEQ_DEF_3(KEYCODE_L, CODE_OR, JOYCODE_1_BUTTON4) },
/*TODO*///	{ IPT_JOYSTICKLEFT_UP     | IPF_PLAYER1, "P1 Left/Up",     SEQ_DEF_3(KEYCODE_E, CODE_OR, JOYCODE_1_UP) },
/*TODO*///	{ IPT_JOYSTICKLEFT_DOWN   | IPF_PLAYER1, "P1 Left/Down",   SEQ_DEF_3(KEYCODE_D, CODE_OR, JOYCODE_1_DOWN) },
/*TODO*///	{ IPT_JOYSTICKLEFT_LEFT   | IPF_PLAYER1, "P1 Left/Left",   SEQ_DEF_3(KEYCODE_S, CODE_OR, JOYCODE_1_LEFT) },
/*TODO*///	{ IPT_JOYSTICKLEFT_RIGHT  | IPF_PLAYER1, "P1 Left/Right",  SEQ_DEF_3(KEYCODE_F, CODE_OR, JOYCODE_1_RIGHT) },
/*TODO*///
/*TODO*///	{ IPT_JOYSTICK_UP         | IPF_PLAYER2, "P2 Up",          SEQ_DEF_3(KEYCODE_R, CODE_OR, JOYCODE_2_UP)    },
/*TODO*///	{ IPT_JOYSTICK_DOWN       | IPF_PLAYER2, "P2 Down",        SEQ_DEF_3(KEYCODE_F, CODE_OR, JOYCODE_2_DOWN)  },
/*TODO*///	{ IPT_JOYSTICK_LEFT       | IPF_PLAYER2, "P2 Left",        SEQ_DEF_3(KEYCODE_D, CODE_OR, JOYCODE_2_LEFT)  },
/*TODO*///	{ IPT_JOYSTICK_RIGHT      | IPF_PLAYER2, "P2 Right",       SEQ_DEF_3(KEYCODE_G, CODE_OR, JOYCODE_2_RIGHT) },
/*TODO*///	{ IPT_BUTTON1             | IPF_PLAYER2, "P2 Button 1",    SEQ_DEF_3(KEYCODE_A, CODE_OR, JOYCODE_2_BUTTON1) },
/*TODO*///	{ IPT_BUTTON2             | IPF_PLAYER2, "P2 Button 2",    SEQ_DEF_3(KEYCODE_S, CODE_OR, JOYCODE_2_BUTTON2) },
/*TODO*///	{ IPT_BUTTON3             | IPF_PLAYER2, "P2 Button 3",    SEQ_DEF_3(KEYCODE_Q, CODE_OR, JOYCODE_2_BUTTON3) },
/*TODO*///	{ IPT_BUTTON4             | IPF_PLAYER2, "P2 Button 4",    SEQ_DEF_3(KEYCODE_W, CODE_OR, JOYCODE_2_BUTTON4) },
/*TODO*///	{ IPT_BUTTON5             | IPF_PLAYER2, "P2 Button 5",    SEQ_DEF_1(JOYCODE_2_BUTTON5) },
/*TODO*///	{ IPT_BUTTON6             | IPF_PLAYER2, "P2 Button 6",    SEQ_DEF_1(JOYCODE_2_BUTTON6) },
/*TODO*///	{ IPT_BUTTON7             | IPF_PLAYER2, "P2 Button 7",    SEQ_DEF_1(JOYCODE_2_BUTTON7) },
/*TODO*///	{ IPT_BUTTON8             | IPF_PLAYER2, "P2 Button 8",    SEQ_DEF_1(JOYCODE_2_BUTTON8) },
/*TODO*///	{ IPT_BUTTON9             | IPF_PLAYER2, "P2 Button 9",    SEQ_DEF_1(JOYCODE_2_BUTTON9) },
/*TODO*///	{ IPT_BUTTON10            | IPF_PLAYER2, "P2 Button 10",   SEQ_DEF_1(JOYCODE_2_BUTTON10) },
/*TODO*///	{ IPT_JOYSTICKRIGHT_UP    | IPF_PLAYER2, "P2 Right/Up",    SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_DOWN  | IPF_PLAYER2, "P2 Right/Down",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_LEFT  | IPF_PLAYER2, "P2 Right/Left",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_RIGHT | IPF_PLAYER2, "P2 Right/Right", SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_UP     | IPF_PLAYER2, "P2 Left/Up",     SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_DOWN   | IPF_PLAYER2, "P2 Left/Down",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_LEFT   | IPF_PLAYER2, "P2 Left/Left",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_RIGHT  | IPF_PLAYER2, "P2 Left/Right",  SEQ_DEF_0 },
/*TODO*///
/*TODO*///	{ IPT_JOYSTICK_UP         | IPF_PLAYER3, "P3 Up",          SEQ_DEF_3(KEYCODE_I, CODE_OR, JOYCODE_3_UP)    },
/*TODO*///	{ IPT_JOYSTICK_DOWN       | IPF_PLAYER3, "P3 Down",        SEQ_DEF_3(KEYCODE_K, CODE_OR, JOYCODE_3_DOWN)  },
/*TODO*///	{ IPT_JOYSTICK_LEFT       | IPF_PLAYER3, "P3 Left",        SEQ_DEF_3(KEYCODE_J, CODE_OR, JOYCODE_3_LEFT)  },
/*TODO*///	{ IPT_JOYSTICK_RIGHT      | IPF_PLAYER3, "P3 Right",       SEQ_DEF_3(KEYCODE_L, CODE_OR, JOYCODE_3_RIGHT) },
/*TODO*///	{ IPT_BUTTON1             | IPF_PLAYER3, "P3 Button 1",    SEQ_DEF_3(KEYCODE_RCONTROL, CODE_OR, JOYCODE_3_BUTTON1) },
/*TODO*///	{ IPT_BUTTON2             | IPF_PLAYER3, "P3 Button 2",    SEQ_DEF_3(KEYCODE_RSHIFT, CODE_OR, JOYCODE_3_BUTTON2) },
/*TODO*///	{ IPT_BUTTON3             | IPF_PLAYER3, "P3 Button 3",    SEQ_DEF_3(KEYCODE_ENTER, CODE_OR, JOYCODE_3_BUTTON3) },
/*TODO*///	{ IPT_BUTTON4             | IPF_PLAYER3, "P3 Button 4",    SEQ_DEF_1(JOYCODE_3_BUTTON4) },
/*TODO*///	{ IPT_BUTTON5             | IPF_PLAYER3, "P3 Button 5",    SEQ_DEF_1(JOYCODE_3_BUTTON5) },
/*TODO*///	{ IPT_BUTTON6             | IPF_PLAYER3, "P3 Button 6",    SEQ_DEF_1(JOYCODE_3_BUTTON6) },
/*TODO*///	{ IPT_BUTTON7             | IPF_PLAYER3, "P3 Button 7",    SEQ_DEF_1(JOYCODE_3_BUTTON7) },
/*TODO*///	{ IPT_BUTTON8             | IPF_PLAYER3, "P3 Button 8",    SEQ_DEF_1(JOYCODE_3_BUTTON8) },
/*TODO*///	{ IPT_BUTTON9             | IPF_PLAYER3, "P3 Button 9",    SEQ_DEF_1(JOYCODE_3_BUTTON9) },
/*TODO*///	{ IPT_BUTTON10            | IPF_PLAYER3, "P3 Button 10",   SEQ_DEF_1(JOYCODE_3_BUTTON10) },
/*TODO*///	{ IPT_JOYSTICKRIGHT_UP    | IPF_PLAYER3, "P3 Right/Up",    SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_DOWN  | IPF_PLAYER3, "P3 Right/Down",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_LEFT  | IPF_PLAYER3, "P3 Right/Left",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_RIGHT | IPF_PLAYER3, "P3 Right/Right", SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_UP     | IPF_PLAYER3, "P3 Left/Up",     SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_DOWN   | IPF_PLAYER3, "P3 Left/Down",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_LEFT   | IPF_PLAYER3, "P3 Left/Left",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_RIGHT  | IPF_PLAYER3, "P3 Left/Right",  SEQ_DEF_0 },
/*TODO*///
/*TODO*///	{ IPT_JOYSTICK_UP         | IPF_PLAYER4, "P4 Up",          SEQ_DEF_1(JOYCODE_4_UP) },
/*TODO*///	{ IPT_JOYSTICK_DOWN       | IPF_PLAYER4, "P4 Down",        SEQ_DEF_1(JOYCODE_4_DOWN) },
/*TODO*///	{ IPT_JOYSTICK_LEFT       | IPF_PLAYER4, "P4 Left",        SEQ_DEF_1(JOYCODE_4_LEFT) },
/*TODO*///	{ IPT_JOYSTICK_RIGHT      | IPF_PLAYER4, "P4 Right",       SEQ_DEF_1(JOYCODE_4_RIGHT) },
/*TODO*///	{ IPT_BUTTON1             | IPF_PLAYER4, "P4 Button 1",    SEQ_DEF_1(JOYCODE_4_BUTTON1) },
/*TODO*///	{ IPT_BUTTON2             | IPF_PLAYER4, "P4 Button 2",    SEQ_DEF_1(JOYCODE_4_BUTTON2) },
/*TODO*///	{ IPT_BUTTON3             | IPF_PLAYER4, "P4 Button 3",    SEQ_DEF_1(JOYCODE_4_BUTTON3) },
/*TODO*///	{ IPT_BUTTON4             | IPF_PLAYER4, "P4 Button 4",    SEQ_DEF_1(JOYCODE_4_BUTTON4) },
/*TODO*///	{ IPT_BUTTON5             | IPF_PLAYER4, "P4 Button 5",    SEQ_DEF_1(JOYCODE_4_BUTTON5) },
/*TODO*///	{ IPT_BUTTON6             | IPF_PLAYER4, "P4 Button 6",    SEQ_DEF_1(JOYCODE_4_BUTTON6) },
/*TODO*///	{ IPT_BUTTON7             | IPF_PLAYER4, "P4 Button 7",    SEQ_DEF_1(JOYCODE_4_BUTTON7) },
/*TODO*///	{ IPT_BUTTON8             | IPF_PLAYER4, "P4 Button 8",    SEQ_DEF_1(JOYCODE_4_BUTTON8) },
/*TODO*///	{ IPT_BUTTON9             | IPF_PLAYER4, "P4 Button 9",    SEQ_DEF_1(JOYCODE_4_BUTTON9) },
/*TODO*///	{ IPT_BUTTON10            | IPF_PLAYER4, "P4 Button 10",   SEQ_DEF_1(JOYCODE_4_BUTTON10) },
/*TODO*///	{ IPT_JOYSTICKRIGHT_UP    | IPF_PLAYER4, "P4 Right/Up",    SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_DOWN  | IPF_PLAYER4, "P4 Right/Down",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_LEFT  | IPF_PLAYER4, "P4 Right/Left",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_RIGHT | IPF_PLAYER4, "P4 Right/Right", SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_UP     | IPF_PLAYER4, "P4 Left/Up",     SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_DOWN   | IPF_PLAYER4, "P4 Left/Down",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_LEFT   | IPF_PLAYER4, "P4 Left/Left",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_RIGHT  | IPF_PLAYER4, "P4 Left/Right",  SEQ_DEF_0 },
/*TODO*///
/*TODO*///	{ IPT_JOYSTICK_UP         | IPF_PLAYER5, "P5 Up",          SEQ_DEF_1(JOYCODE_5_UP) },
/*TODO*///	{ IPT_JOYSTICK_DOWN       | IPF_PLAYER5, "P5 Down",        SEQ_DEF_1(JOYCODE_5_DOWN) },
/*TODO*///	{ IPT_JOYSTICK_LEFT       | IPF_PLAYER5, "P5 Left",        SEQ_DEF_1(JOYCODE_5_LEFT) },
/*TODO*///	{ IPT_JOYSTICK_RIGHT      | IPF_PLAYER5, "P5 Right",       SEQ_DEF_1(JOYCODE_5_RIGHT) },
/*TODO*///	{ IPT_BUTTON1             | IPF_PLAYER5, "P5 Button 1",    SEQ_DEF_1(JOYCODE_5_BUTTON1) },
/*TODO*///	{ IPT_BUTTON2             | IPF_PLAYER5, "P5 Button 2",    SEQ_DEF_1(JOYCODE_5_BUTTON2) },
/*TODO*///	{ IPT_BUTTON3             | IPF_PLAYER5, "P5 Button 3",    SEQ_DEF_1(JOYCODE_5_BUTTON3) },
/*TODO*///	{ IPT_BUTTON4             | IPF_PLAYER5, "P5 Button 4",    SEQ_DEF_1(JOYCODE_5_BUTTON4) },
/*TODO*///	{ IPT_BUTTON5             | IPF_PLAYER5, "P5 Button 5",    SEQ_DEF_1(JOYCODE_5_BUTTON5) },
/*TODO*///	{ IPT_BUTTON6             | IPF_PLAYER5, "P5 Button 6",    SEQ_DEF_1(JOYCODE_5_BUTTON6) },
/*TODO*///	{ IPT_BUTTON7             | IPF_PLAYER5, "P5 Button 7",    SEQ_DEF_1(JOYCODE_5_BUTTON7) },
/*TODO*///	{ IPT_BUTTON8             | IPF_PLAYER5, "P5 Button 8",    SEQ_DEF_1(JOYCODE_5_BUTTON8) },
/*TODO*///	{ IPT_BUTTON9             | IPF_PLAYER5, "P5 Button 9",    SEQ_DEF_1(JOYCODE_5_BUTTON9) },
/*TODO*///	{ IPT_BUTTON10            | IPF_PLAYER5, "P5 Button 10",   SEQ_DEF_1(JOYCODE_5_BUTTON10) },
/*TODO*///	{ IPT_JOYSTICKRIGHT_UP    | IPF_PLAYER5, "P5 Right/Up",    SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_DOWN  | IPF_PLAYER5, "P5 Right/Down",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_LEFT  | IPF_PLAYER5, "P5 Right/Left",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_RIGHT | IPF_PLAYER5, "P5 Right/Right", SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_UP     | IPF_PLAYER5, "P5 Left/Up",     SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_DOWN   | IPF_PLAYER5, "P5 Left/Down",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_LEFT   | IPF_PLAYER5, "P5 Left/Left",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_RIGHT  | IPF_PLAYER5, "P5 Left/Right",  SEQ_DEF_0 },
/*TODO*///
/*TODO*///	{ IPT_JOYSTICK_UP         | IPF_PLAYER6, "P6 Up",          SEQ_DEF_1(JOYCODE_6_UP) },
/*TODO*///	{ IPT_JOYSTICK_DOWN       | IPF_PLAYER6, "P6 Down",        SEQ_DEF_1(JOYCODE_6_DOWN) },
/*TODO*///	{ IPT_JOYSTICK_LEFT       | IPF_PLAYER6, "P6 Left",        SEQ_DEF_1(JOYCODE_6_LEFT) },
/*TODO*///	{ IPT_JOYSTICK_RIGHT      | IPF_PLAYER6, "P6 Right",       SEQ_DEF_1(JOYCODE_6_RIGHT) },
/*TODO*///	{ IPT_BUTTON1             | IPF_PLAYER6, "P6 Button 1",    SEQ_DEF_1(JOYCODE_6_BUTTON1) },
/*TODO*///	{ IPT_BUTTON2             | IPF_PLAYER6, "P6 Button 2",    SEQ_DEF_1(JOYCODE_6_BUTTON2) },
/*TODO*///	{ IPT_BUTTON3             | IPF_PLAYER6, "P6 Button 3",    SEQ_DEF_1(JOYCODE_6_BUTTON3) },
/*TODO*///	{ IPT_BUTTON4             | IPF_PLAYER6, "P6 Button 4",    SEQ_DEF_1(JOYCODE_6_BUTTON4) },
/*TODO*///	{ IPT_BUTTON5             | IPF_PLAYER6, "P6 Button 5",    SEQ_DEF_1(JOYCODE_6_BUTTON5) },
/*TODO*///	{ IPT_BUTTON6             | IPF_PLAYER6, "P6 Button 6",    SEQ_DEF_1(JOYCODE_6_BUTTON6) },
/*TODO*///	{ IPT_BUTTON7             | IPF_PLAYER6, "P6 Button 7",    SEQ_DEF_1(JOYCODE_6_BUTTON7) },
/*TODO*///	{ IPT_BUTTON8             | IPF_PLAYER6, "P6 Button 8",    SEQ_DEF_1(JOYCODE_6_BUTTON8) },
/*TODO*///	{ IPT_BUTTON9             | IPF_PLAYER6, "P6 Button 9",    SEQ_DEF_1(JOYCODE_6_BUTTON9) },
/*TODO*///	{ IPT_BUTTON10            | IPF_PLAYER6, "P6 Button 10",   SEQ_DEF_1(JOYCODE_6_BUTTON10) },
/*TODO*///	{ IPT_JOYSTICKRIGHT_UP    | IPF_PLAYER6, "P6 Right/Up",    SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_DOWN  | IPF_PLAYER6, "P6 Right/Down",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_LEFT  | IPF_PLAYER6, "P6 Right/Left",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_RIGHT | IPF_PLAYER6, "P6 Right/Right", SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_UP     | IPF_PLAYER6, "P6 Left/Up",     SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_DOWN   | IPF_PLAYER6, "P6 Left/Down",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_LEFT   | IPF_PLAYER6, "P6 Left/Left",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_RIGHT  | IPF_PLAYER6, "P6 Left/Right",  SEQ_DEF_0 },
/*TODO*///
/*TODO*///	{ IPT_JOYSTICK_UP         | IPF_PLAYER7, "P7 Up",          SEQ_DEF_1(JOYCODE_7_UP) },
/*TODO*///	{ IPT_JOYSTICK_DOWN       | IPF_PLAYER7, "P7 Down",        SEQ_DEF_1(JOYCODE_7_DOWN) },
/*TODO*///	{ IPT_JOYSTICK_LEFT       | IPF_PLAYER7, "P7 Left",        SEQ_DEF_1(JOYCODE_7_LEFT) },
/*TODO*///	{ IPT_JOYSTICK_RIGHT      | IPF_PLAYER7, "P7 Right",       SEQ_DEF_1(JOYCODE_7_RIGHT) },
/*TODO*///	{ IPT_BUTTON1             | IPF_PLAYER7, "P7 Button 1",    SEQ_DEF_1(JOYCODE_7_BUTTON1) },
/*TODO*///	{ IPT_BUTTON2             | IPF_PLAYER7, "P7 Button 2",    SEQ_DEF_1(JOYCODE_7_BUTTON2) },
/*TODO*///	{ IPT_BUTTON3             | IPF_PLAYER7, "P7 Button 3",    SEQ_DEF_1(JOYCODE_7_BUTTON3) },
/*TODO*///	{ IPT_BUTTON4             | IPF_PLAYER7, "P7 Button 4",    SEQ_DEF_1(JOYCODE_7_BUTTON4) },
/*TODO*///	{ IPT_BUTTON5             | IPF_PLAYER7, "P7 Button 5",    SEQ_DEF_1(JOYCODE_7_BUTTON5) },
/*TODO*///	{ IPT_BUTTON6             | IPF_PLAYER7, "P7 Button 6",    SEQ_DEF_1(JOYCODE_7_BUTTON6) },
/*TODO*///	{ IPT_BUTTON7             | IPF_PLAYER7, "P7 Button 7",    SEQ_DEF_1(JOYCODE_7_BUTTON7) },
/*TODO*///	{ IPT_BUTTON8             | IPF_PLAYER7, "P7 Button 8",    SEQ_DEF_1(JOYCODE_7_BUTTON8) },
/*TODO*///	{ IPT_BUTTON9             | IPF_PLAYER7, "P7 Button 9",    SEQ_DEF_1(JOYCODE_7_BUTTON9) },
/*TODO*///	{ IPT_BUTTON10            | IPF_PLAYER7, "P7 Button 10",   SEQ_DEF_1(JOYCODE_7_BUTTON10) },
/*TODO*///	{ IPT_JOYSTICKRIGHT_UP    | IPF_PLAYER7, "P7 Right/Up",    SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_DOWN  | IPF_PLAYER7, "P7 Right/Down",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_LEFT  | IPF_PLAYER7, "P7 Right/Left",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_RIGHT | IPF_PLAYER7, "P7 Right/Right", SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_UP     | IPF_PLAYER7, "P7 Left/Up",     SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_DOWN   | IPF_PLAYER7, "P7 Left/Down",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_LEFT   | IPF_PLAYER7, "P7 Left/Left",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_RIGHT  | IPF_PLAYER7, "P7 Left/Right",  SEQ_DEF_0 },
/*TODO*///
/*TODO*///	{ IPT_JOYSTICK_UP         | IPF_PLAYER8, "P8 Up",          SEQ_DEF_1(JOYCODE_8_UP) },
/*TODO*///	{ IPT_JOYSTICK_DOWN       | IPF_PLAYER8, "P8 Down",        SEQ_DEF_1(JOYCODE_8_DOWN) },
/*TODO*///	{ IPT_JOYSTICK_LEFT       | IPF_PLAYER8, "P8 Left",        SEQ_DEF_1(JOYCODE_8_LEFT) },
/*TODO*///	{ IPT_JOYSTICK_RIGHT      | IPF_PLAYER8, "P8 Right",       SEQ_DEF_1(JOYCODE_8_RIGHT) },
/*TODO*///	{ IPT_BUTTON1             | IPF_PLAYER8, "P8 Button 1",    SEQ_DEF_1(JOYCODE_8_BUTTON1) },
/*TODO*///	{ IPT_BUTTON2             | IPF_PLAYER8, "P8 Button 2",    SEQ_DEF_1(JOYCODE_8_BUTTON2) },
/*TODO*///	{ IPT_BUTTON3             | IPF_PLAYER8, "P8 Button 3",    SEQ_DEF_1(JOYCODE_8_BUTTON3) },
/*TODO*///	{ IPT_BUTTON4             | IPF_PLAYER8, "P8 Button 4",    SEQ_DEF_1(JOYCODE_8_BUTTON4) },
/*TODO*///	{ IPT_BUTTON5             | IPF_PLAYER8, "P8 Button 5",    SEQ_DEF_1(JOYCODE_8_BUTTON5) },
/*TODO*///	{ IPT_BUTTON6             | IPF_PLAYER8, "P8 Button 6",    SEQ_DEF_1(JOYCODE_8_BUTTON6) },
/*TODO*///	{ IPT_BUTTON7             | IPF_PLAYER8, "P8 Button 7",    SEQ_DEF_1(JOYCODE_8_BUTTON7) },
/*TODO*///	{ IPT_BUTTON8             | IPF_PLAYER8, "P8 Button 8",    SEQ_DEF_1(JOYCODE_8_BUTTON8) },
/*TODO*///	{ IPT_BUTTON9             | IPF_PLAYER8, "P8 Button 9",    SEQ_DEF_1(JOYCODE_8_BUTTON9) },
/*TODO*///	{ IPT_BUTTON10            | IPF_PLAYER8, "P8 Button 10",   SEQ_DEF_1(JOYCODE_8_BUTTON10) },
/*TODO*///	{ IPT_JOYSTICKRIGHT_UP    | IPF_PLAYER8, "P8 Right/Up",    SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_DOWN  | IPF_PLAYER8, "P8 Right/Down",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_LEFT  | IPF_PLAYER8, "P8 Right/Left",  SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKRIGHT_RIGHT | IPF_PLAYER8, "P8 Right/Right", SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_UP     | IPF_PLAYER8, "P8 Left/Up",     SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_DOWN   | IPF_PLAYER8, "P8 Left/Down",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_LEFT   | IPF_PLAYER8, "P8 Left/Left",   SEQ_DEF_0 },
/*TODO*///	{ IPT_JOYSTICKLEFT_RIGHT  | IPF_PLAYER8, "P8 Left/Right",  SEQ_DEF_0 },
/*TODO*///
/*TODO*///	{ IPT_PEDAL                 | IPF_PLAYER1, "P1 Pedal 1",     SEQ_DEF_3(KEYCODE_LCONTROL, CODE_OR, JOYCODE_1_BUTTON1) },
/*TODO*///	{ (IPT_PEDAL+IPT_EXTENSION) | IPF_PLAYER1, "P1 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL                 | IPF_PLAYER2, "P2 Pedal 1",     SEQ_DEF_3(KEYCODE_A, CODE_OR, JOYCODE_2_BUTTON1) },
/*TODO*///	{ (IPT_PEDAL+IPT_EXTENSION) | IPF_PLAYER2, "P2 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL                 | IPF_PLAYER3, "P3 Pedal 1",     SEQ_DEF_3(KEYCODE_RCONTROL, CODE_OR, JOYCODE_3_BUTTON1) },
/*TODO*///	{ (IPT_PEDAL+IPT_EXTENSION) | IPF_PLAYER3, "P3 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL                 | IPF_PLAYER4, "P4 Pedal 1",     SEQ_DEF_1(JOYCODE_4_BUTTON1) },
/*TODO*///	{ (IPT_PEDAL+IPT_EXTENSION) | IPF_PLAYER4, "P4 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL                 | IPF_PLAYER5, "P5 Pedal 1",     SEQ_DEF_1(JOYCODE_5_BUTTON1) },
/*TODO*///	{ (IPT_PEDAL+IPT_EXTENSION) | IPF_PLAYER5, "P5 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL                 | IPF_PLAYER6, "P6 Pedal 1",     SEQ_DEF_1(JOYCODE_6_BUTTON1) },
/*TODO*///	{ (IPT_PEDAL+IPT_EXTENSION) | IPF_PLAYER6, "P6 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL                 | IPF_PLAYER7, "P7 Pedal 1",     SEQ_DEF_1(JOYCODE_7_BUTTON1) },
/*TODO*///	{ (IPT_PEDAL+IPT_EXTENSION) | IPF_PLAYER7, "P7 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL                 | IPF_PLAYER8, "P8 Pedal 1",     SEQ_DEF_1(JOYCODE_8_BUTTON1) },
/*TODO*///	{ (IPT_PEDAL+IPT_EXTENSION) | IPF_PLAYER8, "P8 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///
/*TODO*///	{ IPT_PEDAL2                 | IPF_PLAYER1, "P1 Pedal 2",     SEQ_DEF_1(JOYCODE_1_DOWN) },
/*TODO*///	{ (IPT_PEDAL2+IPT_EXTENSION) | IPF_PLAYER1, "P1 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL2                 | IPF_PLAYER2, "P2 Pedal 2",     SEQ_DEF_1(JOYCODE_2_DOWN) },
/*TODO*///	{ (IPT_PEDAL2+IPT_EXTENSION) | IPF_PLAYER2, "P2 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL2                 | IPF_PLAYER3, "P3 Pedal 2",     SEQ_DEF_1(JOYCODE_3_DOWN) },
/*TODO*///	{ (IPT_PEDAL2+IPT_EXTENSION) | IPF_PLAYER3, "P3 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL2                 | IPF_PLAYER4, "P4 Pedal 2",     SEQ_DEF_1(JOYCODE_4_DOWN) },
/*TODO*///	{ (IPT_PEDAL2+IPT_EXTENSION) | IPF_PLAYER4, "P4 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL2                 | IPF_PLAYER5, "P5 Pedal 2",     SEQ_DEF_1(JOYCODE_5_DOWN) },
/*TODO*///	{ (IPT_PEDAL2+IPT_EXTENSION) | IPF_PLAYER5, "P5 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL2                 | IPF_PLAYER6, "P6 Pedal 2",     SEQ_DEF_1(JOYCODE_6_DOWN) },
/*TODO*///	{ (IPT_PEDAL2+IPT_EXTENSION) | IPF_PLAYER6, "P6 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL2                 | IPF_PLAYER7, "P7 Pedal 2",     SEQ_DEF_1(JOYCODE_7_DOWN) },
/*TODO*///	{ (IPT_PEDAL2+IPT_EXTENSION) | IPF_PLAYER7, "P7 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///	{ IPT_PEDAL2                 | IPF_PLAYER8, "P8 Pedal 2",     SEQ_DEF_1(JOYCODE_8_DOWN) },
/*TODO*///	{ (IPT_PEDAL2+IPT_EXTENSION) | IPF_PLAYER8, "P8 Auto Release <Y/N>", SEQ_DEF_1(KEYCODE_Y) },
/*TODO*///
/*TODO*///	{ IPT_PADDLE | IPF_PLAYER1,  "Paddle",        SEQ_DEF_3(KEYCODE_LEFT, CODE_OR, JOYCODE_1_LEFT) },
/*TODO*///	{ (IPT_PADDLE | IPF_PLAYER1)+IPT_EXTENSION,             "Paddle",        SEQ_DEF_3(KEYCODE_RIGHT, CODE_OR, JOYCODE_1_RIGHT)  },
/*TODO*///	{ IPT_PADDLE | IPF_PLAYER2,  "Paddle 2",      SEQ_DEF_3(KEYCODE_D, CODE_OR, JOYCODE_2_LEFT) },
/*TODO*///	{ (IPT_PADDLE | IPF_PLAYER2)+IPT_EXTENSION,             "Paddle 2",      SEQ_DEF_3(KEYCODE_G, CODE_OR, JOYCODE_2_RIGHT) },
/*TODO*///	{ IPT_PADDLE | IPF_PLAYER3,  "Paddle 3",      SEQ_DEF_3(KEYCODE_J, CODE_OR, JOYCODE_3_LEFT) },
/*TODO*///	{ (IPT_PADDLE | IPF_PLAYER3)+IPT_EXTENSION,             "Paddle 3",      SEQ_DEF_3(KEYCODE_L, CODE_OR, JOYCODE_3_RIGHT) },
/*TODO*///	{ IPT_PADDLE | IPF_PLAYER4,  "Paddle 4",      SEQ_DEF_1(JOYCODE_4_LEFT) },
/*TODO*///	{ (IPT_PADDLE | IPF_PLAYER4)+IPT_EXTENSION,             "Paddle 4",      SEQ_DEF_1(JOYCODE_4_RIGHT) },
/*TODO*///	{ IPT_PADDLE | IPF_PLAYER5,  "Paddle 5",      SEQ_DEF_1(JOYCODE_5_LEFT) },
/*TODO*///	{ (IPT_PADDLE | IPF_PLAYER5)+IPT_EXTENSION,             "Paddle 5",      SEQ_DEF_1(JOYCODE_5_RIGHT) },
/*TODO*///	{ IPT_PADDLE | IPF_PLAYER6,  "Paddle 6",      SEQ_DEF_1(JOYCODE_6_LEFT) },
/*TODO*///	{ (IPT_PADDLE | IPF_PLAYER6)+IPT_EXTENSION,             "Paddle 6",      SEQ_DEF_1(JOYCODE_6_RIGHT) },
/*TODO*///	{ IPT_PADDLE | IPF_PLAYER7,  "Paddle 7",      SEQ_DEF_1(JOYCODE_7_LEFT) },
/*TODO*///	{ (IPT_PADDLE | IPF_PLAYER7)+IPT_EXTENSION,             "Paddle 7",      SEQ_DEF_1(JOYCODE_7_RIGHT) },
/*TODO*///	{ IPT_PADDLE | IPF_PLAYER8,  "Paddle 8",      SEQ_DEF_1(JOYCODE_8_LEFT) },
/*TODO*///	{ (IPT_PADDLE | IPF_PLAYER8)+IPT_EXTENSION,             "Paddle 8",      SEQ_DEF_1(JOYCODE_8_RIGHT) },
/*TODO*///
/*TODO*///	{ IPT_PADDLE_V | IPF_PLAYER1,  "Paddle V",          SEQ_DEF_3(KEYCODE_UP, CODE_OR, JOYCODE_1_UP) },
/*TODO*///	{ (IPT_PADDLE_V | IPF_PLAYER1)+IPT_EXTENSION,             "Paddle V",        SEQ_DEF_3(KEYCODE_DOWN, CODE_OR, JOYCODE_1_DOWN) },
/*TODO*///	{ IPT_PADDLE_V | IPF_PLAYER2,  "Paddle V 2",        SEQ_DEF_3(KEYCODE_R, CODE_OR, JOYCODE_2_UP) },
/*TODO*///	{ (IPT_PADDLE_V | IPF_PLAYER2)+IPT_EXTENSION,             "Paddle V 2",      SEQ_DEF_3(KEYCODE_F, CODE_OR, JOYCODE_2_DOWN) },
/*TODO*///	{ IPT_PADDLE_V | IPF_PLAYER3,  "Paddle V 3",        SEQ_DEF_3(KEYCODE_I, CODE_OR, JOYCODE_3_UP) },
/*TODO*///	{ (IPT_PADDLE_V | IPF_PLAYER3)+IPT_EXTENSION,             "Paddle V 3",      SEQ_DEF_3(KEYCODE_K, CODE_OR, JOYCODE_3_DOWN) },
/*TODO*///	{ IPT_PADDLE_V | IPF_PLAYER4,  "Paddle V 4",        SEQ_DEF_1(JOYCODE_4_UP) },
/*TODO*///	{ (IPT_PADDLE_V | IPF_PLAYER4)+IPT_EXTENSION,             "Paddle V 4",      SEQ_DEF_1(JOYCODE_4_DOWN) },
/*TODO*///	{ IPT_PADDLE_V | IPF_PLAYER5,  "Paddle V 5",        SEQ_DEF_1(JOYCODE_5_UP) },
/*TODO*///	{ (IPT_PADDLE_V | IPF_PLAYER5)+IPT_EXTENSION,             "Paddle V 5",      SEQ_DEF_1(JOYCODE_5_DOWN) },
/*TODO*///	{ IPT_PADDLE_V | IPF_PLAYER6,  "Paddle V 6",        SEQ_DEF_1(JOYCODE_6_UP) },
/*TODO*///	{ (IPT_PADDLE_V | IPF_PLAYER6)+IPT_EXTENSION,             "Paddle V 6",      SEQ_DEF_1(JOYCODE_6_DOWN) },
/*TODO*///	{ IPT_PADDLE_V | IPF_PLAYER7,  "Paddle V 7",        SEQ_DEF_1(JOYCODE_7_UP) },
/*TODO*///	{ (IPT_PADDLE_V | IPF_PLAYER7)+IPT_EXTENSION,             "Paddle V 7",      SEQ_DEF_1(JOYCODE_7_DOWN) },
/*TODO*///	{ IPT_PADDLE_V | IPF_PLAYER8,  "Paddle V 8",        SEQ_DEF_1(JOYCODE_8_UP) },
/*TODO*///	{ (IPT_PADDLE_V | IPF_PLAYER8)+IPT_EXTENSION,             "Paddle V 8",      SEQ_DEF_1(JOYCODE_8_DOWN) },
/*TODO*///
/*TODO*///	{ IPT_DIAL | IPF_PLAYER1,    "Dial",          SEQ_DEF_3(KEYCODE_LEFT, CODE_OR, JOYCODE_1_LEFT) },
/*TODO*///	{ (IPT_DIAL | IPF_PLAYER1)+IPT_EXTENSION,               "Dial",          SEQ_DEF_3(KEYCODE_RIGHT, CODE_OR, JOYCODE_1_RIGHT) },
/*TODO*///	{ IPT_DIAL | IPF_PLAYER2,    "Dial 2",        SEQ_DEF_3(KEYCODE_D, CODE_OR, JOYCODE_2_LEFT) },
/*TODO*///	{ (IPT_DIAL | IPF_PLAYER2)+IPT_EXTENSION,               "Dial 2",      SEQ_DEF_3(KEYCODE_G, CODE_OR, JOYCODE_2_RIGHT) },
/*TODO*///	{ IPT_DIAL | IPF_PLAYER3,    "Dial 3",        SEQ_DEF_3(KEYCODE_J, CODE_OR, JOYCODE_3_LEFT) },
/*TODO*///	{ (IPT_DIAL | IPF_PLAYER3)+IPT_EXTENSION,               "Dial 3",      SEQ_DEF_3(KEYCODE_L, CODE_OR, JOYCODE_3_RIGHT) },
/*TODO*///	{ IPT_DIAL | IPF_PLAYER4,    "Dial 4",        SEQ_DEF_1(JOYCODE_4_LEFT) },
/*TODO*///	{ (IPT_DIAL | IPF_PLAYER4)+IPT_EXTENSION,               "Dial 4",      SEQ_DEF_1(JOYCODE_4_RIGHT) },
/*TODO*///	{ IPT_DIAL | IPF_PLAYER5,    "Dial 5",        SEQ_DEF_1(JOYCODE_5_LEFT) },
/*TODO*///	{ (IPT_DIAL | IPF_PLAYER5)+IPT_EXTENSION,               "Dial 5",      SEQ_DEF_1(JOYCODE_5_RIGHT) },
/*TODO*///	{ IPT_DIAL | IPF_PLAYER6,    "Dial 6",        SEQ_DEF_1(JOYCODE_6_LEFT) },
/*TODO*///	{ (IPT_DIAL | IPF_PLAYER6)+IPT_EXTENSION,               "Dial 6",      SEQ_DEF_1(JOYCODE_6_RIGHT) },
/*TODO*///	{ IPT_DIAL | IPF_PLAYER7,    "Dial 7",        SEQ_DEF_1(JOYCODE_7_LEFT) },
/*TODO*///	{ (IPT_DIAL | IPF_PLAYER7)+IPT_EXTENSION,               "Dial 7",      SEQ_DEF_1(JOYCODE_7_RIGHT) },
/*TODO*///	{ IPT_DIAL | IPF_PLAYER8,    "Dial 8",        SEQ_DEF_1(JOYCODE_8_LEFT) },
/*TODO*///	{ (IPT_DIAL | IPF_PLAYER8)+IPT_EXTENSION,               "Dial 8",      SEQ_DEF_1(JOYCODE_8_RIGHT) },
/*TODO*///
/*TODO*///	{ IPT_DIAL_V | IPF_PLAYER1,  "Dial V",          SEQ_DEF_3(KEYCODE_UP, CODE_OR, JOYCODE_1_UP) },
/*TODO*///	{ (IPT_DIAL_V | IPF_PLAYER1)+IPT_EXTENSION,             "Dial V",        SEQ_DEF_3(KEYCODE_DOWN, CODE_OR, JOYCODE_1_DOWN) },
/*TODO*///	{ IPT_DIAL_V | IPF_PLAYER2,  "Dial V 2",        SEQ_DEF_3(KEYCODE_R, CODE_OR, JOYCODE_2_UP) },
/*TODO*///	{ (IPT_DIAL_V | IPF_PLAYER2)+IPT_EXTENSION,             "Dial V 2",      SEQ_DEF_3(KEYCODE_F, CODE_OR, JOYCODE_2_DOWN) },
/*TODO*///	{ IPT_DIAL_V | IPF_PLAYER3,  "Dial V 3",        SEQ_DEF_3(KEYCODE_I, CODE_OR, JOYCODE_3_UP) },
/*TODO*///	{ (IPT_DIAL_V | IPF_PLAYER3)+IPT_EXTENSION,             "Dial V 3",      SEQ_DEF_3(KEYCODE_K, CODE_OR, JOYCODE_3_DOWN) },
/*TODO*///	{ IPT_DIAL_V | IPF_PLAYER4,  "Dial V 4",        SEQ_DEF_1(JOYCODE_4_UP) },
/*TODO*///	{ (IPT_DIAL_V | IPF_PLAYER4)+IPT_EXTENSION,             "Dial V 4",      SEQ_DEF_1(JOYCODE_4_DOWN) },
/*TODO*///	{ IPT_DIAL_V | IPF_PLAYER5,  "Dial V 5",        SEQ_DEF_1(JOYCODE_5_UP) },
/*TODO*///	{ (IPT_DIAL_V | IPF_PLAYER5)+IPT_EXTENSION,             "Dial V 5",      SEQ_DEF_1(JOYCODE_5_DOWN) },
/*TODO*///	{ IPT_DIAL_V | IPF_PLAYER6,  "Dial V 6",        SEQ_DEF_1(JOYCODE_6_UP) },
/*TODO*///	{ (IPT_DIAL_V | IPF_PLAYER6)+IPT_EXTENSION,             "Dial V 6",      SEQ_DEF_1(JOYCODE_6_DOWN) },
/*TODO*///	{ IPT_DIAL_V | IPF_PLAYER7,  "Dial V 7",        SEQ_DEF_1(JOYCODE_7_UP) },
/*TODO*///	{ (IPT_DIAL_V | IPF_PLAYER7)+IPT_EXTENSION,             "Dial V 7",      SEQ_DEF_1(JOYCODE_7_DOWN) },
/*TODO*///	{ IPT_DIAL_V | IPF_PLAYER8,  "Dial V 8",        SEQ_DEF_1(JOYCODE_8_UP) },
/*TODO*///	{ (IPT_DIAL_V | IPF_PLAYER8)+IPT_EXTENSION,             "Dial V 8",      SEQ_DEF_1(JOYCODE_8_DOWN) },
/*TODO*///
/*TODO*///	{ IPT_TRACKBALL_X | IPF_PLAYER1, "Track X",   SEQ_DEF_3(KEYCODE_LEFT, CODE_OR, JOYCODE_1_LEFT) },
/*TODO*///	{ (IPT_TRACKBALL_X | IPF_PLAYER1)+IPT_EXTENSION,                 "Track X",   SEQ_DEF_3(KEYCODE_RIGHT, CODE_OR, JOYCODE_1_RIGHT) },
/*TODO*///	{ IPT_TRACKBALL_X | IPF_PLAYER2, "Track X 2", SEQ_DEF_3(KEYCODE_D, CODE_OR, JOYCODE_2_LEFT) },
/*TODO*///	{ (IPT_TRACKBALL_X | IPF_PLAYER2)+IPT_EXTENSION,                 "Track X 2", SEQ_DEF_3(KEYCODE_G, CODE_OR, JOYCODE_2_RIGHT) },
/*TODO*///	{ IPT_TRACKBALL_X | IPF_PLAYER3, "Track X 3", SEQ_DEF_3(KEYCODE_J, CODE_OR, JOYCODE_3_LEFT) },
/*TODO*///	{ (IPT_TRACKBALL_X | IPF_PLAYER3)+IPT_EXTENSION,                 "Track X 3", SEQ_DEF_3(KEYCODE_L, CODE_OR, JOYCODE_3_RIGHT) },
/*TODO*///	{ IPT_TRACKBALL_X | IPF_PLAYER4, "Track X 4", SEQ_DEF_1(JOYCODE_4_LEFT) },
/*TODO*///	{ (IPT_TRACKBALL_X | IPF_PLAYER4)+IPT_EXTENSION,                 "Track X 4", SEQ_DEF_1(JOYCODE_4_RIGHT) },
/*TODO*///	{ IPT_TRACKBALL_X | IPF_PLAYER5, "Track X 5", SEQ_DEF_1(JOYCODE_5_LEFT) },
/*TODO*///	{ (IPT_TRACKBALL_X | IPF_PLAYER5)+IPT_EXTENSION,                 "Track X 5", SEQ_DEF_1(JOYCODE_5_RIGHT) },
/*TODO*///	{ IPT_TRACKBALL_X | IPF_PLAYER6, "Track X 6", SEQ_DEF_1(JOYCODE_6_LEFT) },
/*TODO*///	{ (IPT_TRACKBALL_X | IPF_PLAYER6)+IPT_EXTENSION,                 "Track X 6", SEQ_DEF_1(JOYCODE_6_RIGHT) },
/*TODO*///	{ IPT_TRACKBALL_X | IPF_PLAYER7, "Track X 7", SEQ_DEF_1(JOYCODE_7_LEFT) },
/*TODO*///	{ (IPT_TRACKBALL_X | IPF_PLAYER7)+IPT_EXTENSION,                 "Track X 7", SEQ_DEF_1(JOYCODE_7_RIGHT) },
/*TODO*///	{ IPT_TRACKBALL_X | IPF_PLAYER8, "Track X 8", SEQ_DEF_1(JOYCODE_8_LEFT) },
/*TODO*///	{ (IPT_TRACKBALL_X | IPF_PLAYER8)+IPT_EXTENSION,                 "Track X 8", SEQ_DEF_1(JOYCODE_8_RIGHT) },
/*TODO*///
/*TODO*///	{ IPT_TRACKBALL_Y | IPF_PLAYER1, "Track Y",   SEQ_DEF_3(KEYCODE_UP, CODE_OR, JOYCODE_1_UP) },
/*TODO*///	{ (IPT_TRACKBALL_Y | IPF_PLAYER1)+IPT_EXTENSION,                 "Track Y",   SEQ_DEF_3(KEYCODE_DOWN, CODE_OR, JOYCODE_1_DOWN) },
/*TODO*///	{ IPT_TRACKBALL_Y | IPF_PLAYER2, "Track Y 2", SEQ_DEF_3(KEYCODE_R, CODE_OR, JOYCODE_2_UP) },
/*TODO*///	{ (IPT_TRACKBALL_Y | IPF_PLAYER2)+IPT_EXTENSION,                 "Track Y 2", SEQ_DEF_3(KEYCODE_F, CODE_OR, JOYCODE_2_DOWN) },
/*TODO*///	{ IPT_TRACKBALL_Y | IPF_PLAYER3, "Track Y 3", SEQ_DEF_3(KEYCODE_I, CODE_OR, JOYCODE_3_UP) },
/*TODO*///	{ (IPT_TRACKBALL_Y | IPF_PLAYER3)+IPT_EXTENSION,                 "Track Y 3", SEQ_DEF_3(KEYCODE_K, CODE_OR, JOYCODE_3_DOWN) },
/*TODO*///	{ IPT_TRACKBALL_Y | IPF_PLAYER4, "Track Y 4", SEQ_DEF_1(JOYCODE_4_UP) },
/*TODO*///	{ (IPT_TRACKBALL_Y | IPF_PLAYER4)+IPT_EXTENSION,                 "Track Y 4", SEQ_DEF_1(JOYCODE_4_DOWN) },
/*TODO*///	{ IPT_TRACKBALL_Y | IPF_PLAYER5, "Track Y 5", SEQ_DEF_1(JOYCODE_5_UP) },
/*TODO*///	{ (IPT_TRACKBALL_Y | IPF_PLAYER5)+IPT_EXTENSION,                 "Track Y 5", SEQ_DEF_1(JOYCODE_5_DOWN) },
/*TODO*///	{ IPT_TRACKBALL_Y | IPF_PLAYER6, "Track Y 6", SEQ_DEF_1(JOYCODE_6_UP) },
/*TODO*///	{ (IPT_TRACKBALL_Y | IPF_PLAYER6)+IPT_EXTENSION,                 "Track Y 6", SEQ_DEF_1(JOYCODE_6_DOWN) },
/*TODO*///	{ IPT_TRACKBALL_Y | IPF_PLAYER7, "Track Y 7", SEQ_DEF_1(JOYCODE_7_UP) },
/*TODO*///	{ (IPT_TRACKBALL_Y | IPF_PLAYER7)+IPT_EXTENSION,                 "Track Y 7", SEQ_DEF_1(JOYCODE_7_DOWN) },
/*TODO*///	{ IPT_TRACKBALL_Y | IPF_PLAYER8, "Track Y 8", SEQ_DEF_1(JOYCODE_8_UP) },
/*TODO*///	{ (IPT_TRACKBALL_Y | IPF_PLAYER8)+IPT_EXTENSION,                 "Track Y 8", SEQ_DEF_1(JOYCODE_8_DOWN) },
/*TODO*///
/*TODO*///	{ IPT_AD_STICK_X | IPF_PLAYER1, "AD Stick X",   SEQ_DEF_3(KEYCODE_LEFT, CODE_OR, JOYCODE_1_LEFT) },
/*TODO*///	{ (IPT_AD_STICK_X | IPF_PLAYER1)+IPT_EXTENSION,                "AD Stick X",   SEQ_DEF_3(KEYCODE_RIGHT, CODE_OR, JOYCODE_1_RIGHT) },
/*TODO*///	{ IPT_AD_STICK_X | IPF_PLAYER2, "AD Stick X 2", SEQ_DEF_3(KEYCODE_D, CODE_OR, JOYCODE_2_LEFT) },
/*TODO*///	{ (IPT_AD_STICK_X | IPF_PLAYER2)+IPT_EXTENSION,                "AD Stick X 2", SEQ_DEF_3(KEYCODE_G, CODE_OR, JOYCODE_2_RIGHT) },
/*TODO*///	{ IPT_AD_STICK_X | IPF_PLAYER3, "AD Stick X 3", SEQ_DEF_3(KEYCODE_J, CODE_OR, JOYCODE_3_LEFT) },
/*TODO*///	{ (IPT_AD_STICK_X | IPF_PLAYER3)+IPT_EXTENSION,                "AD Stick X 3", SEQ_DEF_3(KEYCODE_L, CODE_OR, JOYCODE_3_RIGHT) },
/*TODO*///	{ IPT_AD_STICK_X | IPF_PLAYER4, "AD Stick X 4", SEQ_DEF_1(JOYCODE_4_LEFT) },
/*TODO*///	{ (IPT_AD_STICK_X | IPF_PLAYER4)+IPT_EXTENSION,                "AD Stick X 4", SEQ_DEF_1(JOYCODE_4_RIGHT) },
/*TODO*///	{ IPT_AD_STICK_X | IPF_PLAYER5, "AD Stick X 5", SEQ_DEF_1(JOYCODE_5_LEFT) },
/*TODO*///	{ (IPT_AD_STICK_X | IPF_PLAYER5)+IPT_EXTENSION,                "AD Stick X 5", SEQ_DEF_1(JOYCODE_5_RIGHT) },
/*TODO*///	{ IPT_AD_STICK_X | IPF_PLAYER6, "AD Stick X 6", SEQ_DEF_1(JOYCODE_6_LEFT) },
/*TODO*///	{ (IPT_AD_STICK_X | IPF_PLAYER6)+IPT_EXTENSION,                "AD Stick X 6", SEQ_DEF_1(JOYCODE_6_RIGHT) },
/*TODO*///	{ IPT_AD_STICK_X | IPF_PLAYER7, "AD Stick X 7", SEQ_DEF_1(JOYCODE_7_LEFT) },
/*TODO*///	{ (IPT_AD_STICK_X | IPF_PLAYER7)+IPT_EXTENSION,                "AD Stick X 7", SEQ_DEF_1(JOYCODE_7_RIGHT) },
/*TODO*///	{ IPT_AD_STICK_X | IPF_PLAYER8, "AD Stick X 8", SEQ_DEF_1(JOYCODE_8_LEFT) },
/*TODO*///	{ (IPT_AD_STICK_X | IPF_PLAYER8)+IPT_EXTENSION,                "AD Stick X 8", SEQ_DEF_1(JOYCODE_8_RIGHT) },
/*TODO*///
/*TODO*///	{ IPT_AD_STICK_Y | IPF_PLAYER1, "AD Stick Y",   SEQ_DEF_3(KEYCODE_UP, CODE_OR, JOYCODE_1_UP) },
/*TODO*///	{ (IPT_AD_STICK_Y | IPF_PLAYER1)+IPT_EXTENSION,                "AD Stick Y",   SEQ_DEF_3(KEYCODE_DOWN, CODE_OR, JOYCODE_1_DOWN) },
/*TODO*///	{ IPT_AD_STICK_Y | IPF_PLAYER2, "AD Stick Y 2", SEQ_DEF_3(KEYCODE_R, CODE_OR, JOYCODE_2_UP) },
/*TODO*///	{ (IPT_AD_STICK_Y | IPF_PLAYER2)+IPT_EXTENSION,                "AD Stick Y 2", SEQ_DEF_3(KEYCODE_F, CODE_OR, JOYCODE_2_DOWN) },
/*TODO*///	{ IPT_AD_STICK_Y | IPF_PLAYER3, "AD Stick Y 3", SEQ_DEF_3(KEYCODE_I, CODE_OR, JOYCODE_3_UP) },
/*TODO*///	{ (IPT_AD_STICK_Y | IPF_PLAYER3)+IPT_EXTENSION,                "AD Stick Y 3", SEQ_DEF_3(KEYCODE_K, CODE_OR, JOYCODE_3_DOWN) },
/*TODO*///	{ IPT_AD_STICK_Y | IPF_PLAYER4, "AD Stick Y 4", SEQ_DEF_1(JOYCODE_4_UP) },
/*TODO*///	{ (IPT_AD_STICK_Y | IPF_PLAYER4)+IPT_EXTENSION,                "AD Stick Y 4", SEQ_DEF_1(JOYCODE_4_DOWN) },
/*TODO*///	{ IPT_AD_STICK_Y | IPF_PLAYER5, "AD Stick Y 5", SEQ_DEF_1(JOYCODE_5_UP) },
/*TODO*///	{ (IPT_AD_STICK_Y | IPF_PLAYER5)+IPT_EXTENSION,                "AD Stick Y 5", SEQ_DEF_1(JOYCODE_5_DOWN) },
/*TODO*///	{ IPT_AD_STICK_Y | IPF_PLAYER6, "AD Stick Y 6", SEQ_DEF_1(JOYCODE_6_UP) },
/*TODO*///	{ (IPT_AD_STICK_Y | IPF_PLAYER6)+IPT_EXTENSION,                "AD Stick Y 6", SEQ_DEF_1(JOYCODE_6_DOWN) },
/*TODO*///	{ IPT_AD_STICK_Y | IPF_PLAYER7, "AD Stick Y 7", SEQ_DEF_1(JOYCODE_7_UP) },
/*TODO*///	{ (IPT_AD_STICK_Y | IPF_PLAYER7)+IPT_EXTENSION,                "AD Stick Y 7", SEQ_DEF_1(JOYCODE_7_DOWN) },
/*TODO*///	{ IPT_AD_STICK_Y | IPF_PLAYER8, "AD Stick Y 8", SEQ_DEF_1(JOYCODE_8_UP) },
/*TODO*///	{ (IPT_AD_STICK_Y | IPF_PLAYER8)+IPT_EXTENSION,                "AD Stick Y 8", SEQ_DEF_1(JOYCODE_8_DOWN) },
/*TODO*///
/*TODO*///	{ IPT_AD_STICK_Z | IPF_PLAYER1, "AD Stick Z",   SEQ_DEF_0 },
/*TODO*///	{ (IPT_AD_STICK_Z | IPF_PLAYER1)+IPT_EXTENSION,                "AD Stick Z",   SEQ_DEF_0 },
/*TODO*///	{ IPT_AD_STICK_Z | IPF_PLAYER2, "AD Stick Z 2", SEQ_DEF_0 },
/*TODO*///	{ (IPT_AD_STICK_Z | IPF_PLAYER2)+IPT_EXTENSION,                "AD Stick Z 2", SEQ_DEF_0 },
/*TODO*///	{ IPT_AD_STICK_Z | IPF_PLAYER3, "AD Stick Z 3", SEQ_DEF_0 },
/*TODO*///	{ (IPT_AD_STICK_Z | IPF_PLAYER3)+IPT_EXTENSION,                "AD Stick Z 3", SEQ_DEF_0 },
/*TODO*///	{ IPT_AD_STICK_Z | IPF_PLAYER4, "AD Stick Z 4", SEQ_DEF_0 },
/*TODO*///	{ (IPT_AD_STICK_Z | IPF_PLAYER4)+IPT_EXTENSION,                "AD Stick Z 4", SEQ_DEF_0 },
/*TODO*///	{ IPT_AD_STICK_Z | IPF_PLAYER5, "AD Stick Z 5", SEQ_DEF_0 },
/*TODO*///	{ (IPT_AD_STICK_Z | IPF_PLAYER5)+IPT_EXTENSION,                "AD Stick Z 5", SEQ_DEF_0 },
/*TODO*///	{ IPT_AD_STICK_Z | IPF_PLAYER6, "AD Stick Z 6", SEQ_DEF_0 },
/*TODO*///	{ (IPT_AD_STICK_Z | IPF_PLAYER6)+IPT_EXTENSION,                "AD Stick Z 6", SEQ_DEF_0 },
/*TODO*///	{ IPT_AD_STICK_Z | IPF_PLAYER7, "AD Stick Z 7", SEQ_DEF_0 },
/*TODO*///	{ (IPT_AD_STICK_Z | IPF_PLAYER7)+IPT_EXTENSION,                "AD Stick Z 7", SEQ_DEF_0 },
/*TODO*///	{ IPT_AD_STICK_Z | IPF_PLAYER8, "AD Stick Z 8", SEQ_DEF_0 },
/*TODO*///	{ (IPT_AD_STICK_Z | IPF_PLAYER8)+IPT_EXTENSION,                "AD Stick Z 8", SEQ_DEF_0 },
/*TODO*///
/*TODO*///	{ IPT_LIGHTGUN_X | IPF_PLAYER1, "Lightgun X",   SEQ_DEF_3(KEYCODE_LEFT, CODE_OR, JOYCODE_1_LEFT) },
/*TODO*///	{ (IPT_LIGHTGUN_X | IPF_PLAYER1)+IPT_EXTENSION,                "Lightgun X",   SEQ_DEF_3(KEYCODE_RIGHT, CODE_OR, JOYCODE_1_RIGHT) },
/*TODO*///	{ IPT_LIGHTGUN_X | IPF_PLAYER2, "Lightgun X 2", SEQ_DEF_3(KEYCODE_D, CODE_OR, JOYCODE_2_LEFT) },
/*TODO*///	{ (IPT_LIGHTGUN_X | IPF_PLAYER2)+IPT_EXTENSION,                "Lightgun X 2", SEQ_DEF_3(KEYCODE_G, CODE_OR, JOYCODE_2_RIGHT) },
/*TODO*///	{ IPT_LIGHTGUN_X | IPF_PLAYER3, "Lightgun X 3", SEQ_DEF_3(KEYCODE_J, CODE_OR, JOYCODE_3_LEFT) },
/*TODO*///	{ (IPT_LIGHTGUN_X | IPF_PLAYER3)+IPT_EXTENSION,                "Lightgun X 3", SEQ_DEF_3(KEYCODE_L, CODE_OR, JOYCODE_3_RIGHT) },
/*TODO*///	{ IPT_LIGHTGUN_X | IPF_PLAYER4, "Lightgun X 4", SEQ_DEF_1(JOYCODE_4_LEFT) },
/*TODO*///	{ (IPT_LIGHTGUN_X | IPF_PLAYER4)+IPT_EXTENSION,                "Lightgun X 4", SEQ_DEF_1(JOYCODE_4_RIGHT) },
/*TODO*///	{ IPT_LIGHTGUN_X | IPF_PLAYER5, "Lightgun X 5", SEQ_DEF_1(JOYCODE_5_LEFT) },
/*TODO*///	{ (IPT_LIGHTGUN_X | IPF_PLAYER5)+IPT_EXTENSION,                "Lightgun X 5", SEQ_DEF_1(JOYCODE_5_RIGHT) },
/*TODO*///	{ IPT_LIGHTGUN_X | IPF_PLAYER6, "Lightgun X 6", SEQ_DEF_1(JOYCODE_6_LEFT) },
/*TODO*///	{ (IPT_LIGHTGUN_X | IPF_PLAYER6)+IPT_EXTENSION,                "Lightgun X 6", SEQ_DEF_1(JOYCODE_6_RIGHT) },
/*TODO*///	{ IPT_LIGHTGUN_X | IPF_PLAYER7, "Lightgun X 7", SEQ_DEF_1(JOYCODE_7_LEFT) },
/*TODO*///	{ (IPT_LIGHTGUN_X | IPF_PLAYER7)+IPT_EXTENSION,                "Lightgun X 7", SEQ_DEF_1(JOYCODE_7_RIGHT) },
/*TODO*///	{ IPT_LIGHTGUN_X | IPF_PLAYER8, "Lightgun X 8", SEQ_DEF_1(JOYCODE_8_LEFT) },
/*TODO*///	{ (IPT_LIGHTGUN_X | IPF_PLAYER8)+IPT_EXTENSION,                "Lightgun X 8", SEQ_DEF_1(JOYCODE_8_RIGHT) },
/*TODO*///
/*TODO*///	{ IPT_LIGHTGUN_Y | IPF_PLAYER1, "Lightgun Y",   SEQ_DEF_3(KEYCODE_UP, CODE_OR, JOYCODE_1_UP) },
/*TODO*///	{ (IPT_LIGHTGUN_Y | IPF_PLAYER1)+IPT_EXTENSION,                "Lightgun Y",   SEQ_DEF_3(KEYCODE_DOWN, CODE_OR, JOYCODE_1_DOWN) },
/*TODO*///	{ IPT_LIGHTGUN_Y | IPF_PLAYER2, "Lightgun Y 2", SEQ_DEF_3(KEYCODE_R, CODE_OR, JOYCODE_2_UP) },
/*TODO*///	{ (IPT_LIGHTGUN_Y | IPF_PLAYER2)+IPT_EXTENSION,                "Lightgun Y 2", SEQ_DEF_3(KEYCODE_F, CODE_OR, JOYCODE_2_DOWN) },
/*TODO*///	{ IPT_LIGHTGUN_Y | IPF_PLAYER3, "Lightgun Y 3", SEQ_DEF_3(KEYCODE_I, CODE_OR, JOYCODE_3_UP) },
/*TODO*///	{ (IPT_LIGHTGUN_Y | IPF_PLAYER3)+IPT_EXTENSION,                "Lightgun Y 3", SEQ_DEF_3(KEYCODE_K, CODE_OR, JOYCODE_3_DOWN) },
/*TODO*///	{ IPT_LIGHTGUN_Y | IPF_PLAYER4, "Lightgun Y 4", SEQ_DEF_1(JOYCODE_4_UP) },
/*TODO*///	{ (IPT_LIGHTGUN_Y | IPF_PLAYER4)+IPT_EXTENSION,                "Lightgun Y 4", SEQ_DEF_1(JOYCODE_4_DOWN) },
/*TODO*///	{ IPT_LIGHTGUN_Y | IPF_PLAYER5, "Lightgun Y 5", SEQ_DEF_1(JOYCODE_5_UP) },
/*TODO*///	{ (IPT_LIGHTGUN_Y | IPF_PLAYER5)+IPT_EXTENSION,                "Lightgun Y 5", SEQ_DEF_1(JOYCODE_5_DOWN) },
/*TODO*///	{ IPT_LIGHTGUN_Y | IPF_PLAYER6, "Lightgun Y 6", SEQ_DEF_1(JOYCODE_6_UP) },
/*TODO*///	{ (IPT_LIGHTGUN_Y | IPF_PLAYER6)+IPT_EXTENSION,                "Lightgun Y 6", SEQ_DEF_1(JOYCODE_6_DOWN) },
/*TODO*///	{ IPT_LIGHTGUN_Y | IPF_PLAYER7, "Lightgun Y 7", SEQ_DEF_1(JOYCODE_7_UP) },
/*TODO*///	{ (IPT_LIGHTGUN_Y | IPF_PLAYER7)+IPT_EXTENSION,                "Lightgun Y 7", SEQ_DEF_1(JOYCODE_7_DOWN) },
/*TODO*///	{ IPT_LIGHTGUN_Y | IPF_PLAYER8, "Lightgun Y 8", SEQ_DEF_1(JOYCODE_8_UP) },
/*TODO*///	{ (IPT_LIGHTGUN_Y | IPF_PLAYER8)+IPT_EXTENSION,                "Lightgun Y 8", SEQ_DEF_1(JOYCODE_8_DOWN) },
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///	{ IPT_MOUSE_X | IPF_PLAYER1, "MOUSE X",   SEQ_DEF_3(KEYCODE_LEFT, CODE_OR, JOYCODE_1_LEFT) },
/*TODO*///	{ (IPT_MOUSE_X | IPF_PLAYER1)+IPT_EXTENSION,                "MOUSE X",   SEQ_DEF_3(KEYCODE_RIGHT, CODE_OR, JOYCODE_1_RIGHT) },
/*TODO*///	{ IPT_MOUSE_X | IPF_PLAYER2, "MOUSE X 2", SEQ_DEF_3(KEYCODE_D, CODE_OR, JOYCODE_2_LEFT) },
/*TODO*///	{ (IPT_MOUSE_X | IPF_PLAYER2)+IPT_EXTENSION,                "MOUSE X 2", SEQ_DEF_3(KEYCODE_G, CODE_OR, JOYCODE_2_RIGHT) },
/*TODO*///	{ IPT_MOUSE_X | IPF_PLAYER3, "MOUSE X 3", SEQ_DEF_3(KEYCODE_J, CODE_OR, JOYCODE_3_LEFT) },
/*TODO*///	{ (IPT_MOUSE_X | IPF_PLAYER3)+IPT_EXTENSION,                "MOUSE X 3", SEQ_DEF_3(KEYCODE_L, CODE_OR, JOYCODE_3_RIGHT) },
/*TODO*///	{ IPT_MOUSE_X | IPF_PLAYER4, "MOUSE X 4", SEQ_DEF_1(JOYCODE_4_LEFT) },
/*TODO*///	{ (IPT_MOUSE_X | IPF_PLAYER4)+IPT_EXTENSION,                "MOUSE X 4", SEQ_DEF_1(JOYCODE_4_RIGHT) },
/*TODO*///	{ IPT_MOUSE_X | IPF_PLAYER5, "MOUSE X 5", SEQ_DEF_0 },
/*TODO*///	{ (IPT_MOUSE_X | IPF_PLAYER5)+IPT_EXTENSION,                "MOUSE X 5", SEQ_DEF_0 },
/*TODO*///	{ IPT_MOUSE_X | IPF_PLAYER6, "MOUSE X 6", SEQ_DEF_0 },
/*TODO*///	{ (IPT_MOUSE_X | IPF_PLAYER6)+IPT_EXTENSION,                "MOUSE X 6", SEQ_DEF_0 },
/*TODO*///	{ IPT_MOUSE_X | IPF_PLAYER7, "MOUSE X 7", SEQ_DEF_0 },
/*TODO*///	{ (IPT_MOUSE_X | IPF_PLAYER7)+IPT_EXTENSION,                "MOUSE X 7", SEQ_DEF_0 },
/*TODO*///	{ IPT_MOUSE_X | IPF_PLAYER8, "MOUSE X 8", SEQ_DEF_0 },
/*TODO*///	{ (IPT_MOUSE_X | IPF_PLAYER8)+IPT_EXTENSION,                "MOUSE X 8", SEQ_DEF_0 },
/*TODO*///
/*TODO*///	{ IPT_MOUSE_Y | IPF_PLAYER1, "MOUSE Y",   SEQ_DEF_3(KEYCODE_UP, CODE_OR, JOYCODE_1_UP) },
/*TODO*///	{ (IPT_MOUSE_Y | IPF_PLAYER1)+IPT_EXTENSION,                "MOUSE Y",   SEQ_DEF_3(KEYCODE_DOWN, CODE_OR, JOYCODE_1_DOWN) },
/*TODO*///	{ IPT_MOUSE_Y | IPF_PLAYER2, "MOUSE Y 2", SEQ_DEF_3(KEYCODE_R, CODE_OR, JOYCODE_2_UP) },
/*TODO*///	{ (IPT_MOUSE_Y | IPF_PLAYER2)+IPT_EXTENSION,                "MOUSE Y 2", SEQ_DEF_3(KEYCODE_F, CODE_OR, JOYCODE_2_DOWN) },
/*TODO*///	{ IPT_MOUSE_Y | IPF_PLAYER3, "MOUSE Y 3", SEQ_DEF_3(KEYCODE_I, CODE_OR, JOYCODE_3_UP) },
/*TODO*///	{ (IPT_MOUSE_Y | IPF_PLAYER3)+IPT_EXTENSION,                "MOUSE Y 3", SEQ_DEF_3(KEYCODE_K, CODE_OR, JOYCODE_3_DOWN) },
/*TODO*///	{ IPT_MOUSE_Y | IPF_PLAYER4, "MOUSE Y 4", SEQ_DEF_1(JOYCODE_4_UP) },
/*TODO*///	{ (IPT_MOUSE_Y | IPF_PLAYER4)+IPT_EXTENSION,                "MOUSE Y 4", SEQ_DEF_1(JOYCODE_4_DOWN) },
/*TODO*///	{ IPT_MOUSE_Y | IPF_PLAYER5, "MOUSE Y 5", SEQ_DEF_0 },
/*TODO*///	{ (IPT_MOUSE_Y | IPF_PLAYER5)+IPT_EXTENSION,                "MOUSE Y 5", SEQ_DEF_0 },
/*TODO*///	{ IPT_MOUSE_Y | IPF_PLAYER6, "MOUSE Y 6", SEQ_DEF_0 },
/*TODO*///	{ (IPT_MOUSE_Y | IPF_PLAYER6)+IPT_EXTENSION,                "MOUSE Y 6", SEQ_DEF_0 },
/*TODO*///	{ IPT_MOUSE_Y | IPF_PLAYER7, "MOUSE Y 7", SEQ_DEF_0 },
/*TODO*///	{ (IPT_MOUSE_Y | IPF_PLAYER7)+IPT_EXTENSION,                "MOUSE Y 7", SEQ_DEF_0 },
/*TODO*///	{ IPT_MOUSE_Y | IPF_PLAYER8, "MOUSE Y 8", SEQ_DEF_0 },
/*TODO*///	{ (IPT_MOUSE_Y | IPF_PLAYER8)+IPT_EXTENSION,                "MOUSE Y 8", SEQ_DEF_0 },
/*TODO*///
/*TODO*///	{ IPT_START | IPF_PLAYER1, "P1 Start",        SEQ_DEF_3(KEYCODE_1, CODE_OR, JOYCODE_1_START) },
/*TODO*///	{ IPT_START | IPF_PLAYER2, "P2 Start",        SEQ_DEF_3(KEYCODE_2, CODE_OR, JOYCODE_2_START) },
/*TODO*///	{ IPT_START | IPF_PLAYER3, "P3 Start",        SEQ_DEF_3(KEYCODE_3, CODE_OR, JOYCODE_3_START) },
/*TODO*///	{ IPT_START | IPF_PLAYER4, "P4 Start",        SEQ_DEF_3(KEYCODE_4, CODE_OR, JOYCODE_4_START) },
/*TODO*///	{ IPT_START | IPF_PLAYER5, "P5 Start",        SEQ_DEF_0 },
/*TODO*///	{ IPT_START | IPF_PLAYER6, "P6 Start",        SEQ_DEF_0 },
/*TODO*///	{ IPT_START | IPF_PLAYER7, "P7 Start",        SEQ_DEF_0 },
/*TODO*///	{ IPT_START | IPF_PLAYER8, "P8 Start",        SEQ_DEF_0 },
/*TODO*///	{ IPT_SELECT | IPF_PLAYER1, "P1 Select",      SEQ_DEF_3(KEYCODE_5, CODE_OR, JOYCODE_1_SELECT) },
/*TODO*///	{ IPT_SELECT | IPF_PLAYER2, "P2 Select",      SEQ_DEF_3(KEYCODE_6, CODE_OR, JOYCODE_2_SELECT) },
/*TODO*///	{ IPT_SELECT | IPF_PLAYER3, "P3 Select",      SEQ_DEF_3(KEYCODE_7, CODE_OR, JOYCODE_3_SELECT) },
/*TODO*///	{ IPT_SELECT | IPF_PLAYER4, "P4 Select",      SEQ_DEF_3(KEYCODE_8, CODE_OR, JOYCODE_4_SELECT) },
/*TODO*///	{ IPT_SELECT | IPF_PLAYER5, "P5 Select",      SEQ_DEF_0 },
/*TODO*///	{ IPT_SELECT | IPF_PLAYER6, "P6 Select",      SEQ_DEF_0 },
/*TODO*///	{ IPT_SELECT | IPF_PLAYER7, "P7 Select",      SEQ_DEF_0 },
/*TODO*///	{ IPT_SELECT | IPF_PLAYER8, "P8 Select",      SEQ_DEF_0 },
/*TODO*///#endif
/*TODO*///
/*TODO*///	{ IPT_UNKNOWN,             "UNKNOWN",         SEQ_DEF_0 },
/*TODO*///	{ IPT_OSD_RESERVED,        "",                SEQ_DEF_0 },
/*TODO*///	{ IPT_OSD_RESERVED,        "",                SEQ_DEF_0 },
/*TODO*///	{ IPT_OSD_RESERVED,        "",                SEQ_DEF_0 },
/*TODO*///	{ IPT_OSD_RESERVED,        "",                SEQ_DEF_0 },
/*TODO*///	{ IPT_END,                 0,                 SEQ_DEF_0 }	/* returned when there is no match */
/*TODO*///};
/*TODO*///
/*TODO*///struct ipd inputport_defaults_backup[sizeof(inputport_defaults)/sizeof(struct ipd)];
/*TODO*///
/*TODO*///
/*TODO*///struct ik *osd_input_keywords = NULL;
/*TODO*///
/*TODO*///struct ik input_keywords[] =
/*TODO*///{
/*TODO*///	{ "KEYCODE_A",		   		IKT_STD,		KEYCODE_A },
/*TODO*///	{ "KEYCODE_B",		   		IKT_STD,		KEYCODE_B },
/*TODO*///	{ "KEYCODE_C",		   		IKT_STD,		KEYCODE_C },
/*TODO*///	{ "KEYCODE_D",		   		IKT_STD,		KEYCODE_D },
/*TODO*///	{ "KEYCODE_E",		   		IKT_STD,		KEYCODE_E },
/*TODO*///	{ "KEYCODE_F",		   		IKT_STD,		KEYCODE_F },
/*TODO*///	{ "KEYCODE_G",		   		IKT_STD,		KEYCODE_G },
/*TODO*///	{ "KEYCODE_H",		   		IKT_STD,		KEYCODE_H },
/*TODO*///	{ "KEYCODE_I",		   		IKT_STD,		KEYCODE_I },
/*TODO*///	{ "KEYCODE_J",		   		IKT_STD,		KEYCODE_J },
/*TODO*///	{ "KEYCODE_K",		   		IKT_STD,		KEYCODE_K },
/*TODO*///	{ "KEYCODE_L",		   		IKT_STD,		KEYCODE_L },
/*TODO*///	{ "KEYCODE_M",		   		IKT_STD,		KEYCODE_M },
/*TODO*///	{ "KEYCODE_N",		   		IKT_STD,		KEYCODE_N },
/*TODO*///	{ "KEYCODE_O",		   		IKT_STD,		KEYCODE_O },
/*TODO*///	{ "KEYCODE_P",		   		IKT_STD,		KEYCODE_P },
/*TODO*///	{ "KEYCODE_Q",		   		IKT_STD,		KEYCODE_Q },
/*TODO*///	{ "KEYCODE_R",		   		IKT_STD,		KEYCODE_R },
/*TODO*///	{ "KEYCODE_S",		   		IKT_STD,		KEYCODE_S },
/*TODO*///	{ "KEYCODE_T",		   		IKT_STD,		KEYCODE_T },
/*TODO*///	{ "KEYCODE_U",		   		IKT_STD,		KEYCODE_U },
/*TODO*///	{ "KEYCODE_V",		   		IKT_STD,		KEYCODE_V },
/*TODO*///	{ "KEYCODE_W",		   		IKT_STD,		KEYCODE_W },
/*TODO*///	{ "KEYCODE_X",		   		IKT_STD,		KEYCODE_X },
/*TODO*///	{ "KEYCODE_Y",		   		IKT_STD,		KEYCODE_Y },
/*TODO*///	{ "KEYCODE_Z",		   		IKT_STD,		KEYCODE_Z },
/*TODO*///	{ "KEYCODE_0",		   		IKT_STD,		KEYCODE_0 },
/*TODO*///	{ "KEYCODE_1",		   		IKT_STD,		KEYCODE_1 },
/*TODO*///	{ "KEYCODE_2",		   		IKT_STD,		KEYCODE_2 },
/*TODO*///	{ "KEYCODE_3",		   		IKT_STD,		KEYCODE_3 },
/*TODO*///	{ "KEYCODE_4",		   		IKT_STD,		KEYCODE_4 },
/*TODO*///	{ "KEYCODE_5",		   		IKT_STD,		KEYCODE_5 },
/*TODO*///	{ "KEYCODE_6",		   		IKT_STD,		KEYCODE_6 },
/*TODO*///	{ "KEYCODE_7",		   		IKT_STD,		KEYCODE_7 },
/*TODO*///	{ "KEYCODE_8",		   		IKT_STD,		KEYCODE_8 },
/*TODO*///	{ "KEYCODE_9",		   		IKT_STD,		KEYCODE_9 },
/*TODO*///	{ "KEYCODE_0_PAD",	   		IKT_STD,		KEYCODE_0_PAD },
/*TODO*///	{ "KEYCODE_1_PAD",	   		IKT_STD,		KEYCODE_1_PAD },
/*TODO*///	{ "KEYCODE_2_PAD",	   		IKT_STD,		KEYCODE_2_PAD },
/*TODO*///	{ "KEYCODE_3_PAD",	   		IKT_STD,		KEYCODE_3_PAD },
/*TODO*///	{ "KEYCODE_4_PAD",	   		IKT_STD,		KEYCODE_4_PAD },
/*TODO*///	{ "KEYCODE_5_PAD",	   		IKT_STD,		KEYCODE_5_PAD },
/*TODO*///	{ "KEYCODE_6_PAD",	   		IKT_STD,		KEYCODE_6_PAD },
/*TODO*///	{ "KEYCODE_7_PAD",	   		IKT_STD,		KEYCODE_7_PAD },
/*TODO*///	{ "KEYCODE_8_PAD",	      	IKT_STD,		KEYCODE_8_PAD },
/*TODO*///	{ "KEYCODE_9_PAD",	      	IKT_STD,		KEYCODE_9_PAD },
/*TODO*///	{ "KEYCODE_F1",		   		IKT_STD,		KEYCODE_F1 },
/*TODO*///	{ "KEYCODE_F2",			  	IKT_STD,		KEYCODE_F2 },
/*TODO*///	{ "KEYCODE_F3",			  	IKT_STD,		KEYCODE_F3 },
/*TODO*///	{ "KEYCODE_F4",			  	IKT_STD,		KEYCODE_F4 },
/*TODO*///	{ "KEYCODE_F5",			  	IKT_STD,		KEYCODE_F5 },
/*TODO*///	{ "KEYCODE_F6",			  	IKT_STD,		KEYCODE_F6 },
/*TODO*///	{ "KEYCODE_F7",			  	IKT_STD,		KEYCODE_F7 },
/*TODO*///	{ "KEYCODE_F8",			  	IKT_STD,		KEYCODE_F8 },
/*TODO*///	{ "KEYCODE_F9",			  	IKT_STD,		KEYCODE_F9 },
/*TODO*///	{ "KEYCODE_F10",		  	IKT_STD,		KEYCODE_F10 },
/*TODO*///	{ "KEYCODE_F11",		  	IKT_STD,		KEYCODE_F11 },
/*TODO*///	{ "KEYCODE_F12",		  	IKT_STD,		KEYCODE_F12 },
/*TODO*///	{ "KEYCODE_ESC",		  	IKT_STD,		KEYCODE_ESC },
/*TODO*///	{ "KEYCODE_TILDE",		  	IKT_STD,		KEYCODE_TILDE },
/*TODO*///	{ "KEYCODE_MINUS",		  	IKT_STD,		KEYCODE_MINUS },
/*TODO*///	{ "KEYCODE_EQUALS",		  	IKT_STD,		KEYCODE_EQUALS },
/*TODO*///	{ "KEYCODE_BACKSPACE",	  	IKT_STD,		KEYCODE_BACKSPACE },
/*TODO*///	{ "KEYCODE_TAB",		  	IKT_STD,		KEYCODE_TAB },
/*TODO*///	{ "KEYCODE_OPENBRACE",	  	IKT_STD,		KEYCODE_OPENBRACE },
/*TODO*///	{ "KEYCODE_CLOSEBRACE",	  	IKT_STD,		KEYCODE_CLOSEBRACE },
/*TODO*///	{ "KEYCODE_ENTER",		  	IKT_STD,		KEYCODE_ENTER },
/*TODO*///	{ "KEYCODE_COLON",		  	IKT_STD,		KEYCODE_COLON },
/*TODO*///	{ "KEYCODE_QUOTE",		  	IKT_STD,		KEYCODE_QUOTE },
/*TODO*///	{ "KEYCODE_BACKSLASH",	  	IKT_STD,		KEYCODE_BACKSLASH },
/*TODO*///	{ "KEYCODE_BACKSLASH2",	  	IKT_STD,		KEYCODE_BACKSLASH2 },
/*TODO*///	{ "KEYCODE_COMMA",		  	IKT_STD,		KEYCODE_COMMA },
/*TODO*///	{ "KEYCODE_STOP",		  	IKT_STD,		KEYCODE_STOP },
/*TODO*///	{ "KEYCODE_SLASH",		  	IKT_STD,		KEYCODE_SLASH },
/*TODO*///	{ "KEYCODE_SPACE",		  	IKT_STD,		KEYCODE_SPACE },
/*TODO*///	{ "KEYCODE_INSERT",		  	IKT_STD,		KEYCODE_INSERT },
/*TODO*///	{ "KEYCODE_DEL",		  	IKT_STD,		KEYCODE_DEL },
/*TODO*///	{ "KEYCODE_HOME",		  	IKT_STD,		KEYCODE_HOME },
/*TODO*///	{ "KEYCODE_END",		  	IKT_STD,		KEYCODE_END },
/*TODO*///	{ "KEYCODE_PGUP",		  	IKT_STD,		KEYCODE_PGUP },
/*TODO*///	{ "KEYCODE_PGDN",		  	IKT_STD,		KEYCODE_PGDN },
/*TODO*///	{ "KEYCODE_LEFT",		  	IKT_STD,		KEYCODE_LEFT },
/*TODO*///	{ "KEYCODE_RIGHT",		  	IKT_STD,		KEYCODE_RIGHT },
/*TODO*///	{ "KEYCODE_UP",			  	IKT_STD,		KEYCODE_UP },
/*TODO*///	{ "KEYCODE_DOWN",		  	IKT_STD,		KEYCODE_DOWN },
/*TODO*///	{ "KEYCODE_SLASH_PAD",	  	IKT_STD,		KEYCODE_SLASH_PAD },
/*TODO*///	{ "KEYCODE_ASTERISK",	  	IKT_STD,		KEYCODE_ASTERISK },
/*TODO*///	{ "KEYCODE_MINUS_PAD",	  	IKT_STD,		KEYCODE_MINUS_PAD },
/*TODO*///	{ "KEYCODE_PLUS_PAD",	  	IKT_STD,		KEYCODE_PLUS_PAD },
/*TODO*///	{ "KEYCODE_DEL_PAD",	  	IKT_STD,		KEYCODE_DEL_PAD },
/*TODO*///	{ "KEYCODE_ENTER_PAD",	  	IKT_STD,		KEYCODE_ENTER_PAD },
/*TODO*///	{ "KEYCODE_PRTSCR",		  	IKT_STD,		KEYCODE_PRTSCR },
/*TODO*///	{ "KEYCODE_PAUSE",		  	IKT_STD,		KEYCODE_PAUSE },
/*TODO*///	{ "KEYCODE_LSHIFT",		  	IKT_STD,		KEYCODE_LSHIFT },
/*TODO*///	{ "KEYCODE_RSHIFT",		  	IKT_STD,		KEYCODE_RSHIFT },
/*TODO*///	{ "KEYCODE_LCONTROL",	  	IKT_STD,		KEYCODE_LCONTROL },
/*TODO*///	{ "KEYCODE_RCONTROL",	  	IKT_STD,		KEYCODE_RCONTROL },
/*TODO*///	{ "KEYCODE_LALT",		  	IKT_STD,		KEYCODE_LALT },
/*TODO*///	{ "KEYCODE_RALT",		  	IKT_STD,		KEYCODE_RALT },
/*TODO*///	{ "KEYCODE_SCRLOCK",	  	IKT_STD,		KEYCODE_SCRLOCK },
/*TODO*///	{ "KEYCODE_NUMLOCK",	  	IKT_STD,		KEYCODE_NUMLOCK },
/*TODO*///	{ "KEYCODE_CAPSLOCK",	  	IKT_STD,		KEYCODE_CAPSLOCK },
/*TODO*///	{ "KEYCODE_LWIN",		  	IKT_STD,		KEYCODE_LWIN },
/*TODO*///	{ "KEYCODE_RWIN",		  	IKT_STD,		KEYCODE_RWIN },
/*TODO*///	{ "KEYCODE_MENU",		  	IKT_STD,		KEYCODE_MENU },
/*TODO*///
/*TODO*///	{ "JOYCODE_1_LEFT",		  	IKT_STD,		JOYCODE_1_LEFT },
/*TODO*///	{ "JOYCODE_1_RIGHT",	  	IKT_STD,		JOYCODE_1_RIGHT },
/*TODO*///	{ "JOYCODE_1_UP",		  	IKT_STD,		JOYCODE_1_UP },
/*TODO*///	{ "JOYCODE_1_DOWN",		  	IKT_STD,		JOYCODE_1_DOWN },
/*TODO*///	{ "JOYCODE_1_BUTTON1",	  	IKT_STD,		JOYCODE_1_BUTTON1 },
/*TODO*///	{ "JOYCODE_1_BUTTON2",	  	IKT_STD,		JOYCODE_1_BUTTON2 },
/*TODO*///	{ "JOYCODE_1_BUTTON3",	  	IKT_STD,		JOYCODE_1_BUTTON3 },
/*TODO*///	{ "JOYCODE_1_BUTTON4",	  	IKT_STD,		JOYCODE_1_BUTTON4 },
/*TODO*///	{ "JOYCODE_1_BUTTON5",	  	IKT_STD,		JOYCODE_1_BUTTON5 },
/*TODO*///	{ "JOYCODE_1_BUTTON6",	  	IKT_STD,		JOYCODE_1_BUTTON6 },
/*TODO*///	{ "JOYCODE_1_BUTTON7",	  	IKT_STD,		JOYCODE_1_BUTTON7 },
/*TODO*///	{ "JOYCODE_1_BUTTON8",	  	IKT_STD,		JOYCODE_1_BUTTON8 },
/*TODO*///	{ "JOYCODE_1_BUTTON9",	  	IKT_STD,		JOYCODE_1_BUTTON9 },
/*TODO*///	{ "JOYCODE_1_BUTTON10",	  	IKT_STD,		JOYCODE_1_BUTTON10 },
/*TODO*///	{ "JOYCODE_1_START",	  	IKT_STD,		JOYCODE_1_START },
/*TODO*///	{ "JOYCODE_1_SELECT",	  	IKT_STD,		JOYCODE_1_SELECT },
/*TODO*///	{ "JOYCODE_2_LEFT",		  	IKT_STD,		JOYCODE_2_LEFT },
/*TODO*///	{ "JOYCODE_2_RIGHT",	  	IKT_STD,		JOYCODE_2_RIGHT },
/*TODO*///	{ "JOYCODE_2_UP",		  	IKT_STD,		JOYCODE_2_UP },
/*TODO*///	{ "JOYCODE_2_DOWN",		  	IKT_STD,		JOYCODE_2_DOWN },
/*TODO*///	{ "JOYCODE_2_BUTTON1",	  	IKT_STD,		JOYCODE_2_BUTTON1 },
/*TODO*///	{ "JOYCODE_2_BUTTON2",	  	IKT_STD,		JOYCODE_2_BUTTON2 },
/*TODO*///	{ "JOYCODE_2_BUTTON3",	  	IKT_STD,		JOYCODE_2_BUTTON3 },
/*TODO*///	{ "JOYCODE_2_BUTTON4",	  	IKT_STD,		JOYCODE_2_BUTTON4 },
/*TODO*///	{ "JOYCODE_2_BUTTON5",	  	IKT_STD,		JOYCODE_2_BUTTON5 },
/*TODO*///	{ "JOYCODE_2_BUTTON6",	  	IKT_STD,		JOYCODE_2_BUTTON6 },
/*TODO*///	{ "JOYCODE_2_BUTTON7",	  	IKT_STD,		JOYCODE_2_BUTTON7 },
/*TODO*///	{ "JOYCODE_2_BUTTON8",	  	IKT_STD,		JOYCODE_2_BUTTON8 },
/*TODO*///	{ "JOYCODE_2_BUTTON9",	  	IKT_STD,		JOYCODE_2_BUTTON9 },
/*TODO*///	{ "JOYCODE_2_BUTTON10",	  	IKT_STD,		JOYCODE_2_BUTTON10 },
/*TODO*///	{ "JOYCODE_2_START",	  	IKT_STD,		JOYCODE_2_START },
/*TODO*///	{ "JOYCODE_2_SELECT",	  	IKT_STD,		JOYCODE_2_SELECT },
/*TODO*///	{ "JOYCODE_3_LEFT",		  	IKT_STD,		JOYCODE_3_LEFT },
/*TODO*///	{ "JOYCODE_3_RIGHT",	  	IKT_STD,		JOYCODE_3_RIGHT },
/*TODO*///	{ "JOYCODE_3_UP",		  	IKT_STD,		JOYCODE_3_UP },
/*TODO*///	{ "JOYCODE_3_DOWN",		  	IKT_STD,		JOYCODE_3_DOWN },
/*TODO*///	{ "JOYCODE_3_BUTTON1",	  	IKT_STD,		JOYCODE_3_BUTTON1 },
/*TODO*///	{ "JOYCODE_3_BUTTON2",	  	IKT_STD,		JOYCODE_3_BUTTON2 },
/*TODO*///	{ "JOYCODE_3_BUTTON3",	  	IKT_STD,		JOYCODE_3_BUTTON3 },
/*TODO*///	{ "JOYCODE_3_BUTTON4",	  	IKT_STD,		JOYCODE_3_BUTTON4 },
/*TODO*///	{ "JOYCODE_3_BUTTON5",	  	IKT_STD,		JOYCODE_3_BUTTON5 },
/*TODO*///	{ "JOYCODE_3_BUTTON6",	  	IKT_STD,		JOYCODE_3_BUTTON6 },
/*TODO*///	{ "JOYCODE_3_BUTTON7",	  	IKT_STD,		JOYCODE_3_BUTTON7 },
/*TODO*///	{ "JOYCODE_3_BUTTON8",	  	IKT_STD,		JOYCODE_3_BUTTON8 },
/*TODO*///	{ "JOYCODE_3_BUTTON9",	  	IKT_STD,		JOYCODE_3_BUTTON9 },
/*TODO*///	{ "JOYCODE_3_BUTTON10",	  	IKT_STD,		JOYCODE_3_BUTTON10 },
/*TODO*///	{ "JOYCODE_3_START",	  	IKT_STD,		JOYCODE_3_START },
/*TODO*///	{ "JOYCODE_3_SELECT",	  	IKT_STD,		JOYCODE_3_SELECT },
/*TODO*///	{ "JOYCODE_4_LEFT",		  	IKT_STD,		JOYCODE_4_LEFT },
/*TODO*///	{ "JOYCODE_4_RIGHT",	  	IKT_STD,		JOYCODE_4_RIGHT },
/*TODO*///	{ "JOYCODE_4_UP",		  	IKT_STD,		JOYCODE_4_UP },
/*TODO*///	{ "JOYCODE_4_DOWN",		  	IKT_STD,		JOYCODE_4_DOWN },
/*TODO*///	{ "JOYCODE_4_BUTTON1",	  	IKT_STD,		JOYCODE_4_BUTTON1 },
/*TODO*///	{ "JOYCODE_4_BUTTON2",	  	IKT_STD,		JOYCODE_4_BUTTON2 },
/*TODO*///	{ "JOYCODE_4_BUTTON3",	  	IKT_STD,		JOYCODE_4_BUTTON3 },
/*TODO*///	{ "JOYCODE_4_BUTTON4",	  	IKT_STD,		JOYCODE_4_BUTTON4 },
/*TODO*///	{ "JOYCODE_4_BUTTON5",	  	IKT_STD,		JOYCODE_4_BUTTON5 },
/*TODO*///	{ "JOYCODE_4_BUTTON6",	  	IKT_STD,		JOYCODE_4_BUTTON6 },
/*TODO*///	{ "JOYCODE_4_BUTTON7",	  	IKT_STD,		JOYCODE_4_BUTTON7 },
/*TODO*///	{ "JOYCODE_4_BUTTON8",	  	IKT_STD,		JOYCODE_4_BUTTON8 },
/*TODO*///	{ "JOYCODE_4_BUTTON9",	  	IKT_STD,		JOYCODE_4_BUTTON9 },
/*TODO*///	{ "JOYCODE_4_BUTTON10",	  	IKT_STD,		JOYCODE_4_BUTTON10 },
/*TODO*///	{ "JOYCODE_4_START",	  	IKT_STD,		JOYCODE_4_START },
/*TODO*///	{ "JOYCODE_4_SELECT",	  	IKT_STD,		JOYCODE_4_SELECT },
/*TODO*///	{ "JOYCODE_5_LEFT",		  	IKT_STD,		JOYCODE_5_LEFT },
/*TODO*///	{ "JOYCODE_5_RIGHT",	  	IKT_STD,		JOYCODE_5_RIGHT },
/*TODO*///	{ "JOYCODE_5_UP",		  	IKT_STD,		JOYCODE_5_UP },
/*TODO*///	{ "JOYCODE_5_DOWN",		  	IKT_STD,		JOYCODE_5_DOWN },
/*TODO*///	{ "JOYCODE_5_BUTTON1",	  	IKT_STD,		JOYCODE_5_BUTTON1 },
/*TODO*///	{ "JOYCODE_5_BUTTON2",	  	IKT_STD,		JOYCODE_5_BUTTON2 },
/*TODO*///	{ "JOYCODE_5_BUTTON3",	  	IKT_STD,		JOYCODE_5_BUTTON3 },
/*TODO*///	{ "JOYCODE_5_BUTTON4",	  	IKT_STD,		JOYCODE_5_BUTTON4 },
/*TODO*///	{ "JOYCODE_5_BUTTON5",	  	IKT_STD,		JOYCODE_5_BUTTON5 },
/*TODO*///	{ "JOYCODE_5_BUTTON6",	  	IKT_STD,		JOYCODE_5_BUTTON6 },
/*TODO*///	{ "JOYCODE_5_BUTTON7",	  	IKT_STD,		JOYCODE_5_BUTTON7 },
/*TODO*///	{ "JOYCODE_5_BUTTON8",	  	IKT_STD,		JOYCODE_5_BUTTON8 },
/*TODO*///	{ "JOYCODE_5_BUTTON9",	  	IKT_STD,		JOYCODE_5_BUTTON9 },
/*TODO*///	{ "JOYCODE_5_BUTTON10",	  	IKT_STD,		JOYCODE_5_BUTTON10 },
/*TODO*///	{ "JOYCODE_5_START",	  	IKT_STD,		JOYCODE_5_START },
/*TODO*///	{ "JOYCODE_5_SELECT",	  	IKT_STD,		JOYCODE_5_SELECT },
/*TODO*///	{ "JOYCODE_6_LEFT",		  	IKT_STD,		JOYCODE_6_LEFT },
/*TODO*///	{ "JOYCODE_6_RIGHT",	  	IKT_STD,		JOYCODE_6_RIGHT },
/*TODO*///	{ "JOYCODE_6_UP",		  	IKT_STD,		JOYCODE_6_UP },
/*TODO*///	{ "JOYCODE_6_DOWN",		  	IKT_STD,		JOYCODE_6_DOWN },
/*TODO*///	{ "JOYCODE_6_BUTTON1",	  	IKT_STD,		JOYCODE_6_BUTTON1 },
/*TODO*///	{ "JOYCODE_6_BUTTON2",	  	IKT_STD,		JOYCODE_6_BUTTON2 },
/*TODO*///	{ "JOYCODE_6_BUTTON3",	  	IKT_STD,		JOYCODE_6_BUTTON3 },
/*TODO*///	{ "JOYCODE_6_BUTTON4",	  	IKT_STD,		JOYCODE_6_BUTTON4 },
/*TODO*///	{ "JOYCODE_6_BUTTON5",	  	IKT_STD,		JOYCODE_6_BUTTON5 },
/*TODO*///	{ "JOYCODE_6_BUTTON6",	  	IKT_STD,		JOYCODE_6_BUTTON6 },
/*TODO*///	{ "JOYCODE_6_BUTTON7",	  	IKT_STD,		JOYCODE_6_BUTTON7 },
/*TODO*///	{ "JOYCODE_6_BUTTON8",	  	IKT_STD,		JOYCODE_6_BUTTON8 },
/*TODO*///	{ "JOYCODE_6_BUTTON9",	  	IKT_STD,		JOYCODE_6_BUTTON9 },
/*TODO*///	{ "JOYCODE_6_BUTTON10",	  	IKT_STD,		JOYCODE_6_BUTTON10 },
/*TODO*///	{ "JOYCODE_6_START",	  	IKT_STD,		JOYCODE_6_START },
/*TODO*///	{ "JOYCODE_6_SELECT",	  	IKT_STD,		JOYCODE_6_SELECT },
/*TODO*///	{ "JOYCODE_7_LEFT",		  	IKT_STD,		JOYCODE_7_LEFT },
/*TODO*///	{ "JOYCODE_7_RIGHT",	  	IKT_STD,		JOYCODE_7_RIGHT },
/*TODO*///	{ "JOYCODE_7_UP",		  	IKT_STD,		JOYCODE_7_UP },
/*TODO*///	{ "JOYCODE_7_DOWN",		  	IKT_STD,		JOYCODE_7_DOWN },
/*TODO*///	{ "JOYCODE_7_BUTTON1",	  	IKT_STD,		JOYCODE_7_BUTTON1 },
/*TODO*///	{ "JOYCODE_7_BUTTON2",	  	IKT_STD,		JOYCODE_7_BUTTON2 },
/*TODO*///	{ "JOYCODE_7_BUTTON3",	  	IKT_STD,		JOYCODE_7_BUTTON3 },
/*TODO*///	{ "JOYCODE_7_BUTTON4",	  	IKT_STD,		JOYCODE_7_BUTTON4 },
/*TODO*///	{ "JOYCODE_7_BUTTON5",	  	IKT_STD,		JOYCODE_7_BUTTON5 },
/*TODO*///	{ "JOYCODE_7_BUTTON6",	  	IKT_STD,		JOYCODE_7_BUTTON6 },
/*TODO*///	{ "JOYCODE_7_BUTTON7",	  	IKT_STD,		JOYCODE_7_BUTTON7 },
/*TODO*///	{ "JOYCODE_7_BUTTON8",	  	IKT_STD,		JOYCODE_7_BUTTON8 },
/*TODO*///	{ "JOYCODE_7_BUTTON9",	  	IKT_STD,		JOYCODE_7_BUTTON9 },
/*TODO*///	{ "JOYCODE_7_BUTTON10",	  	IKT_STD,		JOYCODE_7_BUTTON10 },
/*TODO*///	{ "JOYCODE_7_START",	  	IKT_STD,		JOYCODE_7_START },
/*TODO*///	{ "JOYCODE_7_SELECT",	  	IKT_STD,		JOYCODE_7_SELECT },
/*TODO*///	{ "JOYCODE_8_LEFT",		  	IKT_STD,		JOYCODE_8_LEFT },
/*TODO*///	{ "JOYCODE_8_RIGHT",	  	IKT_STD,		JOYCODE_8_RIGHT },
/*TODO*///	{ "JOYCODE_8_UP",		  	IKT_STD,		JOYCODE_8_UP },
/*TODO*///	{ "JOYCODE_8_DOWN",		  	IKT_STD,		JOYCODE_8_DOWN },
/*TODO*///	{ "JOYCODE_8_BUTTON1",	  	IKT_STD,		JOYCODE_8_BUTTON1 },
/*TODO*///	{ "JOYCODE_8_BUTTON2",	  	IKT_STD,		JOYCODE_8_BUTTON2 },
/*TODO*///	{ "JOYCODE_8_BUTTON3",	  	IKT_STD,		JOYCODE_8_BUTTON3 },
/*TODO*///	{ "JOYCODE_8_BUTTON4",	  	IKT_STD,		JOYCODE_8_BUTTON4 },
/*TODO*///	{ "JOYCODE_8_BUTTON5",	  	IKT_STD,		JOYCODE_8_BUTTON5 },
/*TODO*///	{ "JOYCODE_8_BUTTON6",	  	IKT_STD,		JOYCODE_8_BUTTON6 },
/*TODO*///	{ "JOYCODE_8_BUTTON7",	  	IKT_STD,		JOYCODE_8_BUTTON7 },
/*TODO*///	{ "JOYCODE_8_BUTTON8",	  	IKT_STD,		JOYCODE_8_BUTTON8 },
/*TODO*///	{ "JOYCODE_8_BUTTON9",	  	IKT_STD,		JOYCODE_8_BUTTON9 },
/*TODO*///	{ "JOYCODE_8_BUTTON10",	  	IKT_STD,		JOYCODE_8_BUTTON10 },
/*TODO*///	{ "JOYCODE_8_START",	  	IKT_STD,		JOYCODE_8_START },
/*TODO*///	{ "JOYCODE_8_SELECT",	  	IKT_STD,		JOYCODE_8_SELECT },
/*TODO*///
/*TODO*///	{ "MOUSECODE_1_BUTTON1", 	IKT_STD,		JOYCODE_MOUSE_1_BUTTON1 },
/*TODO*///	{ "MOUSECODE_1_BUTTON2", 	IKT_STD,		JOYCODE_MOUSE_1_BUTTON2 },
/*TODO*///	{ "MOUSECODE_1_BUTTON3", 	IKT_STD,		JOYCODE_MOUSE_1_BUTTON3 },
/*TODO*///
/*TODO*///	{ "KEYCODE_NONE",			IKT_STD,		CODE_NONE },
/*TODO*///	{ "CODE_NONE",			  	IKT_STD,		CODE_NONE },
/*TODO*///	{ "CODE_OTHER",				IKT_STD,		CODE_OTHER },
/*TODO*///	{ "CODE_DEFAULT",			IKT_STD,		CODE_DEFAULT },
/*TODO*///	{ "CODE_PREVIOUS",			IKT_STD,		CODE_PREVIOUS },
/*TODO*///	{ "CODE_NOT",				IKT_STD,		CODE_NOT },
/*TODO*///	{ "CODE_OR",			   	IKT_STD,		CODE_OR },
/*TODO*///	{ "!",						IKT_STD,		CODE_NOT },
/*TODO*///	{ "|",					   	IKT_STD,		CODE_OR },
/*TODO*///
/*TODO*///	{ "UI_CONFIGURE", 			IKT_IPT,	 	IPT_UI_CONFIGURE },
/*TODO*///	{ "UI_ON_SCREEN_DISPLAY",	IKT_IPT,		IPT_UI_ON_SCREEN_DISPLAY },
/*TODO*///	{ "UI_PAUSE",				IKT_IPT,		IPT_UI_PAUSE },
/*TODO*///	{ "UI_RESET_MACHINE",		IKT_IPT,		IPT_UI_RESET_MACHINE },
/*TODO*///	{ "UI_SHOW_GFX",			IKT_IPT,		IPT_UI_SHOW_GFX },
/*TODO*///	{ "UI_FRAMESKIP_DEC",		IKT_IPT,		IPT_UI_FRAMESKIP_DEC },
/*TODO*///	{ "UI_FRAMESKIP_INC",		IKT_IPT,		IPT_UI_FRAMESKIP_INC },
/*TODO*///	{ "UI_THROTTLE",			IKT_IPT,		IPT_UI_THROTTLE },
/*TODO*///	{ "UI_SHOW_FPS",			IKT_IPT,		IPT_UI_SHOW_FPS },
/*TODO*///	{ "UI_SHOW_PROFILER",		IKT_IPT,		IPT_UI_SHOW_PROFILER },
/*TODO*///#ifdef MESS
/*TODO*///	{ "UI_TOGGLE_UI",			IKT_IPT,		IPT_UI_TOGGLE_UI },
/*TODO*///#endif
/*TODO*///	{ "UI_SNAPSHOT",			IKT_IPT,		IPT_UI_SNAPSHOT },
/*TODO*///	{ "UI_TOGGLE_CHEAT",		IKT_IPT,		IPT_UI_TOGGLE_CHEAT },
/*TODO*///	{ "UI_UP",					IKT_IPT,		IPT_UI_UP },
/*TODO*///	{ "UI_DOWN",				IKT_IPT,		IPT_UI_DOWN },
/*TODO*///	{ "UI_LEFT",				IKT_IPT,		IPT_UI_LEFT },
/*TODO*///	{ "UI_RIGHT",				IKT_IPT,		IPT_UI_RIGHT },
/*TODO*///	{ "UI_SELECT",				IKT_IPT,		IPT_UI_SELECT },
/*TODO*///	{ "UI_CANCEL",				IKT_IPT,		IPT_UI_CANCEL },
/*TODO*///	{ "UI_PAN_UP",				IKT_IPT,		IPT_UI_PAN_UP },
/*TODO*///	{ "UI_PAN_DOWN",			IKT_IPT,		IPT_UI_PAN_DOWN },
/*TODO*///	{ "UI_PAN_LEFT",			IKT_IPT,		IPT_UI_PAN_LEFT },
/*TODO*///	{ "UI_PAN_RIGHT",			IKT_IPT,		IPT_UI_PAN_RIGHT },
/*TODO*///	{ "UI_TOGGLE_DEBUG",		IKT_IPT,		IPT_UI_TOGGLE_DEBUG },
/*TODO*///	{ "UI_SAVE_STATE",			IKT_IPT,		IPT_UI_SAVE_STATE },
/*TODO*///	{ "UI_LOAD_STATE",			IKT_IPT,		IPT_UI_LOAD_STATE },
/*TODO*///	{ "UI_ADD_CHEAT",			IKT_IPT,		IPT_UI_ADD_CHEAT },
/*TODO*///	{ "UI_DELETE_CHEAT",		IKT_IPT,		IPT_UI_DELETE_CHEAT },
/*TODO*///	{ "UI_SAVE_CHEAT",			IKT_IPT,		IPT_UI_SAVE_CHEAT },
/*TODO*///	{ "UI_WATCH_VALUE",			IKT_IPT,		IPT_UI_WATCH_VALUE },
/*TODO*///	{ "UI_EDIT_CHEAT",			IKT_IPT,		IPT_UI_EDIT_CHEAT },
/*TODO*///	{ "UI_TOGGLE_CROSSHAIR",	IKT_IPT,		IPT_UI_TOGGLE_CROSSHAIR },
/*TODO*///	{ "START1",					IKT_IPT,		IPT_START1 },
/*TODO*///	{ "START2",					IKT_IPT,		IPT_START2 },
/*TODO*///	{ "START3",					IKT_IPT,		IPT_START3 },
/*TODO*///	{ "START4",					IKT_IPT,		IPT_START4 },
/*TODO*///	{ "START5",					IKT_IPT,		IPT_START5 },
/*TODO*///	{ "START6",					IKT_IPT,		IPT_START6 },
/*TODO*///	{ "START7",					IKT_IPT,		IPT_START7 },
/*TODO*///	{ "START8",					IKT_IPT,		IPT_START8 },
/*TODO*///	{ "COIN1",					IKT_IPT,		IPT_COIN1 },
/*TODO*///	{ "COIN2",					IKT_IPT,		IPT_COIN2 },
/*TODO*///	{ "COIN3",					IKT_IPT,		IPT_COIN3 },
/*TODO*///	{ "COIN4",					IKT_IPT,		IPT_COIN4 },
/*TODO*///	{ "COIN5",					IKT_IPT,		IPT_COIN5 },
/*TODO*///	{ "COIN6",					IKT_IPT,		IPT_COIN6 },
/*TODO*///	{ "COIN7",					IKT_IPT,		IPT_COIN7 },
/*TODO*///	{ "COIN8",					IKT_IPT,		IPT_COIN8 },
/*TODO*///	{ "SERVICE1",				IKT_IPT,		IPT_SERVICE1 },
/*TODO*///	{ "SERVICE2",				IKT_IPT,		IPT_SERVICE2 },
/*TODO*///	{ "SERVICE3",				IKT_IPT,		IPT_SERVICE3 },
/*TODO*///	{ "SERVICE4",				IKT_IPT,		IPT_SERVICE4 },
/*TODO*///	{ "TILT",					IKT_IPT,		IPT_TILT },
/*TODO*///
/*TODO*///	{ "P1_JOYSTICK_UP",			IKT_IPT,		IPF_PLAYER1 | IPT_JOYSTICK_UP },
/*TODO*///	{ "P1_JOYSTICK_DOWN",		IKT_IPT,		IPF_PLAYER1 | IPT_JOYSTICK_DOWN },
/*TODO*///	{ "P1_JOYSTICK_LEFT",		IKT_IPT,		IPF_PLAYER1 | IPT_JOYSTICK_LEFT },
/*TODO*///	{ "P1_JOYSTICK_RIGHT",		IKT_IPT,		IPF_PLAYER1 | IPT_JOYSTICK_RIGHT },
/*TODO*///	{ "P1_BUTTON1",				IKT_IPT,		IPF_PLAYER1 | IPT_BUTTON1 },
/*TODO*///	{ "P1_BUTTON2",				IKT_IPT,		IPF_PLAYER1 | IPT_BUTTON2 },
/*TODO*///	{ "P1_BUTTON3",				IKT_IPT,		IPF_PLAYER1 | IPT_BUTTON3 },
/*TODO*///	{ "P1_BUTTON4",				IKT_IPT,		IPF_PLAYER1 | IPT_BUTTON4 },
/*TODO*///	{ "P1_BUTTON5",				IKT_IPT,		IPF_PLAYER1 | IPT_BUTTON5 },
/*TODO*///	{ "P1_BUTTON6",				IKT_IPT,		IPF_PLAYER1 | IPT_BUTTON6 },
/*TODO*///	{ "P1_BUTTON7",				IKT_IPT,		IPF_PLAYER1 | IPT_BUTTON7 },
/*TODO*///	{ "P1_BUTTON8",				IKT_IPT,		IPF_PLAYER1 | IPT_BUTTON8 },
/*TODO*///	{ "P1_BUTTON9",				IKT_IPT,		IPF_PLAYER1 | IPT_BUTTON9 },
/*TODO*///	{ "P1_BUTTON10",			IKT_IPT,		IPF_PLAYER1 | IPT_BUTTON10 },
/*TODO*///	{ "P1_JOYSTICKRIGHT_UP",	IKT_IPT,		IPF_PLAYER1 | IPT_JOYSTICKRIGHT_UP },
/*TODO*///	{ "P1_JOYSTICKRIGHT_DOWN",	IKT_IPT,		IPF_PLAYER1 | IPT_JOYSTICKRIGHT_DOWN },
/*TODO*///	{ "P1_JOYSTICKRIGHT_LEFT",	IKT_IPT,		IPF_PLAYER1 | IPT_JOYSTICKRIGHT_LEFT },
/*TODO*///	{ "P1_JOYSTICKRIGHT_RIGHT",	IKT_IPT,		IPF_PLAYER1 | IPT_JOYSTICKRIGHT_RIGHT },
/*TODO*///	{ "P1_JOYSTICKLEFT_UP",		IKT_IPT,		IPF_PLAYER1 | IPT_JOYSTICKLEFT_UP },
/*TODO*///	{ "P1_JOYSTICKLEFT_DOWN",	IKT_IPT,		IPF_PLAYER1 | IPT_JOYSTICKLEFT_DOWN },
/*TODO*///	{ "P1_JOYSTICKLEFT_LEFT",	IKT_IPT,		IPF_PLAYER1 | IPT_JOYSTICKLEFT_LEFT },
/*TODO*///	{ "P1_JOYSTICKLEFT_RIGHT",	IKT_IPT,		IPF_PLAYER1 | IPT_JOYSTICKLEFT_RIGHT },
/*TODO*///
/*TODO*///	{ "P2_JOYSTICK_UP",			IKT_IPT,		IPF_PLAYER2 | IPT_JOYSTICK_UP },
/*TODO*///	{ "P2_JOYSTICK_DOWN",		IKT_IPT,		IPF_PLAYER2 | IPT_JOYSTICK_DOWN },
/*TODO*///	{ "P2_JOYSTICK_LEFT",		IKT_IPT,		IPF_PLAYER2 | IPT_JOYSTICK_LEFT },
/*TODO*///	{ "P2_JOYSTICK_RIGHT",		IKT_IPT,		IPF_PLAYER2 | IPT_JOYSTICK_RIGHT },
/*TODO*///	{ "P2_BUTTON1",				IKT_IPT,		IPF_PLAYER2 | IPT_BUTTON1 },
/*TODO*///	{ "P2_BUTTON2",				IKT_IPT,		IPF_PLAYER2 | IPT_BUTTON2 },
/*TODO*///	{ "P2_BUTTON3",				IKT_IPT,		IPF_PLAYER2 | IPT_BUTTON3 },
/*TODO*///	{ "P2_BUTTON4",				IKT_IPT,		IPF_PLAYER2 | IPT_BUTTON4 },
/*TODO*///	{ "P2_BUTTON5",				IKT_IPT,		IPF_PLAYER2 | IPT_BUTTON5 },
/*TODO*///	{ "P2_BUTTON6",				IKT_IPT,		IPF_PLAYER2 | IPT_BUTTON6 },
/*TODO*///	{ "P2_BUTTON7",				IKT_IPT,		IPF_PLAYER2 | IPT_BUTTON7 },
/*TODO*///	{ "P2_BUTTON8",				IKT_IPT,		IPF_PLAYER2 | IPT_BUTTON8 },
/*TODO*///	{ "P2_BUTTON9",				IKT_IPT,		IPF_PLAYER2 | IPT_BUTTON9 },
/*TODO*///	{ "P2_BUTTON10",			IKT_IPT,		IPF_PLAYER2 | IPT_BUTTON10 },
/*TODO*///	{ "P2_JOYSTICKRIGHT_UP",	IKT_IPT,		IPF_PLAYER2 | IPT_JOYSTICKRIGHT_UP },
/*TODO*///	{ "P2_JOYSTICKRIGHT_DOWN",	IKT_IPT,		IPF_PLAYER2 | IPT_JOYSTICKRIGHT_DOWN },
/*TODO*///	{ "P2_JOYSTICKRIGHT_LEFT",	IKT_IPT,		IPF_PLAYER2 | IPT_JOYSTICKRIGHT_LEFT },
/*TODO*///	{ "P2_JOYSTICKRIGHT_RIGHT",	IKT_IPT,		IPF_PLAYER2 | IPT_JOYSTICKRIGHT_RIGHT },
/*TODO*///	{ "P2_JOYSTICKLEFT_UP",		IKT_IPT,		IPF_PLAYER2 | IPT_JOYSTICKLEFT_UP },
/*TODO*///	{ "P2_JOYSTICKLEFT_DOWN",	IKT_IPT,		IPF_PLAYER2 | IPT_JOYSTICKLEFT_DOWN },
/*TODO*///	{ "P2_JOYSTICKLEFT_LEFT",	IKT_IPT,		IPF_PLAYER2 | IPT_JOYSTICKLEFT_LEFT },
/*TODO*///	{ "P2_JOYSTICKLEFT_RIGHT",	IKT_IPT,		IPF_PLAYER2 | IPT_JOYSTICKLEFT_RIGHT },
/*TODO*///
/*TODO*///	{ "P3_JOYSTICK_UP",			IKT_IPT,		IPF_PLAYER3 | IPT_JOYSTICK_UP },
/*TODO*///	{ "P3_JOYSTICK_DOWN",		IKT_IPT,		IPF_PLAYER3 | IPT_JOYSTICK_DOWN },
/*TODO*///	{ "P3_JOYSTICK_LEFT",		IKT_IPT,		IPF_PLAYER3 | IPT_JOYSTICK_LEFT },
/*TODO*///	{ "P3_JOYSTICK_RIGHT",		IKT_IPT,		IPF_PLAYER3 | IPT_JOYSTICK_RIGHT },
/*TODO*///	{ "P3_BUTTON1",				IKT_IPT,		IPF_PLAYER3 | IPT_BUTTON1 },
/*TODO*///	{ "P3_BUTTON2",				IKT_IPT,		IPF_PLAYER3 | IPT_BUTTON2 },
/*TODO*///	{ "P3_BUTTON3",				IKT_IPT,		IPF_PLAYER3 | IPT_BUTTON3 },
/*TODO*///	{ "P3_BUTTON4",				IKT_IPT,		IPF_PLAYER3 | IPT_BUTTON4 },
/*TODO*///	{ "P3_BUTTON5",				IKT_IPT,		IPF_PLAYER3 | IPT_BUTTON5 },
/*TODO*///	{ "P3_BUTTON6",				IKT_IPT,		IPF_PLAYER3 | IPT_BUTTON6 },
/*TODO*///	{ "P3_BUTTON7",				IKT_IPT,		IPF_PLAYER3 | IPT_BUTTON7 },
/*TODO*///	{ "P3_BUTTON8",				IKT_IPT,		IPF_PLAYER3 | IPT_BUTTON8 },
/*TODO*///	{ "P3_BUTTON9",				IKT_IPT,		IPF_PLAYER3 | IPT_BUTTON9 },
/*TODO*///	{ "P3_BUTTON10",			IKT_IPT,		IPF_PLAYER3 | IPT_BUTTON10 },
/*TODO*///	{ "P3_JOYSTICKRIGHT_UP",	IKT_IPT,		IPF_PLAYER3 | IPT_JOYSTICKRIGHT_UP },
/*TODO*///	{ "P3_JOYSTICKRIGHT_DOWN",	IKT_IPT,		IPF_PLAYER3 | IPT_JOYSTICKRIGHT_DOWN },
/*TODO*///	{ "P3_JOYSTICKRIGHT_LEFT",	IKT_IPT,		IPF_PLAYER3 | IPT_JOYSTICKRIGHT_LEFT },
/*TODO*///	{ "P3_JOYSTICKRIGHT_RIGHT",	IKT_IPT,		IPF_PLAYER3 | IPT_JOYSTICKRIGHT_RIGHT },
/*TODO*///	{ "P3_JOYSTICKLEFT_UP",		IKT_IPT,		IPF_PLAYER3 | IPT_JOYSTICKLEFT_UP },
/*TODO*///	{ "P3_JOYSTICKLEFT_DOWN",	IKT_IPT,		IPF_PLAYER3 | IPT_JOYSTICKLEFT_DOWN },
/*TODO*///	{ "P3_JOYSTICKLEFT_LEFT",	IKT_IPT,		IPF_PLAYER3 | IPT_JOYSTICKLEFT_LEFT },
/*TODO*///	{ "P3_JOYSTICKLEFT_RIGHT",	IKT_IPT,		IPF_PLAYER3 | IPT_JOYSTICKLEFT_RIGHT },
/*TODO*///
/*TODO*///	{ "P4_JOYSTICK_UP",			IKT_IPT,		IPF_PLAYER4 | IPT_JOYSTICK_UP },
/*TODO*///	{ "P4_JOYSTICK_DOWN",		IKT_IPT,		IPF_PLAYER4 | IPT_JOYSTICK_DOWN },
/*TODO*///	{ "P4_JOYSTICK_LEFT",		IKT_IPT,		IPF_PLAYER4 | IPT_JOYSTICK_LEFT },
/*TODO*///	{ "P4_JOYSTICK_RIGHT",		IKT_IPT,		IPF_PLAYER4 | IPT_JOYSTICK_RIGHT },
/*TODO*///	{ "P4_BUTTON1",				IKT_IPT,		IPF_PLAYER4 | IPT_BUTTON1 },
/*TODO*///	{ "P4_BUTTON2",				IKT_IPT,		IPF_PLAYER4 | IPT_BUTTON2 },
/*TODO*///	{ "P4_BUTTON3",				IKT_IPT,		IPF_PLAYER4 | IPT_BUTTON3 },
/*TODO*///	{ "P4_BUTTON4",				IKT_IPT,		IPF_PLAYER4 | IPT_BUTTON4 },
/*TODO*///	{ "P4_BUTTON5",				IKT_IPT,		IPF_PLAYER4 | IPT_BUTTON5 },
/*TODO*///	{ "P4_BUTTON6",				IKT_IPT,		IPF_PLAYER4 | IPT_BUTTON6 },
/*TODO*///	{ "P4_BUTTON7",				IKT_IPT,		IPF_PLAYER4 | IPT_BUTTON7 },
/*TODO*///	{ "P4_BUTTON8",				IKT_IPT,		IPF_PLAYER4 | IPT_BUTTON8 },
/*TODO*///	{ "P4_BUTTON9",				IKT_IPT,		IPF_PLAYER4 | IPT_BUTTON9 },
/*TODO*///	{ "P4_BUTTON10",			IKT_IPT,		IPF_PLAYER4 | IPT_BUTTON10 },
/*TODO*///	{ "P4_JOYSTICKRIGHT_UP",	IKT_IPT,		IPF_PLAYER4 | IPT_JOYSTICKRIGHT_UP },
/*TODO*///	{ "P4_JOYSTICKRIGHT_DOWN",	IKT_IPT,		IPF_PLAYER4 | IPT_JOYSTICKRIGHT_DOWN },
/*TODO*///	{ "P4_JOYSTICKRIGHT_LEFT",	IKT_IPT,		IPF_PLAYER4 | IPT_JOYSTICKRIGHT_LEFT },
/*TODO*///	{ "P4_JOYSTICKRIGHT_RIGHT",	IKT_IPT,		IPF_PLAYER4 | IPT_JOYSTICKRIGHT_RIGHT },
/*TODO*///	{ "P4_JOYSTICKLEFT_UP",		IKT_IPT,		IPF_PLAYER4 | IPT_JOYSTICKLEFT_UP },
/*TODO*///	{ "P4_JOYSTICKLEFT_DOWN",	IKT_IPT,		IPF_PLAYER4 | IPT_JOYSTICKLEFT_DOWN },
/*TODO*///	{ "P4_JOYSTICKLEFT_LEFT",	IKT_IPT,		IPF_PLAYER4 | IPT_JOYSTICKLEFT_LEFT },
/*TODO*///	{ "P4_JOYSTICKLEFT_RIGHT",	IKT_IPT,		IPF_PLAYER4 | IPT_JOYSTICKLEFT_RIGHT },
/*TODO*///
/*TODO*///	{ "P5_JOYSTICK_UP",			IKT_IPT,		IPF_PLAYER5 | IPT_JOYSTICK_UP },
/*TODO*///	{ "P5_JOYSTICK_DOWN",		IKT_IPT,		IPF_PLAYER5 | IPT_JOYSTICK_DOWN },
/*TODO*///	{ "P5_JOYSTICK_LEFT",		IKT_IPT,		IPF_PLAYER5 | IPT_JOYSTICK_LEFT },
/*TODO*///	{ "P5_JOYSTICK_RIGHT",		IKT_IPT,		IPF_PLAYER5 | IPT_JOYSTICK_RIGHT },
/*TODO*///	{ "P5_BUTTON1",				IKT_IPT,		IPF_PLAYER5 | IPT_BUTTON1 },
/*TODO*///	{ "P5_BUTTON2",				IKT_IPT,		IPF_PLAYER5 | IPT_BUTTON2 },
/*TODO*///	{ "P5_BUTTON3",				IKT_IPT,		IPF_PLAYER5 | IPT_BUTTON3 },
/*TODO*///	{ "P5_BUTTON4",				IKT_IPT,		IPF_PLAYER5 | IPT_BUTTON4 },
/*TODO*///	{ "P5_BUTTON5",				IKT_IPT,		IPF_PLAYER5 | IPT_BUTTON5 },
/*TODO*///	{ "P5_BUTTON6",				IKT_IPT,		IPF_PLAYER5 | IPT_BUTTON6 },
/*TODO*///	{ "P5_BUTTON7",				IKT_IPT,		IPF_PLAYER5 | IPT_BUTTON7 },
/*TODO*///	{ "P5_BUTTON8",				IKT_IPT,		IPF_PLAYER5 | IPT_BUTTON8 },
/*TODO*///	{ "P5_BUTTON9",				IKT_IPT,		IPF_PLAYER5 | IPT_BUTTON9 },
/*TODO*///	{ "P5_BUTTON10",			IKT_IPT,		IPF_PLAYER5 | IPT_BUTTON10 },
/*TODO*///	{ "P5_JOYSTICKRIGHT_UP",	IKT_IPT,		IPF_PLAYER5 | IPT_JOYSTICKRIGHT_UP },
/*TODO*///	{ "P5_JOYSTICKRIGHT_DOWN",	IKT_IPT,		IPF_PLAYER5 | IPT_JOYSTICKRIGHT_DOWN },
/*TODO*///	{ "P5_JOYSTICKRIGHT_LEFT",	IKT_IPT,		IPF_PLAYER5 | IPT_JOYSTICKRIGHT_LEFT },
/*TODO*///	{ "P5_JOYSTICKRIGHT_RIGHT",	IKT_IPT,		IPF_PLAYER5 | IPT_JOYSTICKRIGHT_RIGHT },
/*TODO*///	{ "P5_JOYSTICKLEFT_UP",		IKT_IPT,		IPF_PLAYER5 | IPT_JOYSTICKLEFT_UP },
/*TODO*///	{ "P5_JOYSTICKLEFT_DOWN",	IKT_IPT,		IPF_PLAYER5 | IPT_JOYSTICKLEFT_DOWN },
/*TODO*///	{ "P5_JOYSTICKLEFT_LEFT",	IKT_IPT,		IPF_PLAYER5 | IPT_JOYSTICKLEFT_LEFT },
/*TODO*///	{ "P5_JOYSTICKLEFT_RIGHT",	IKT_IPT,		IPF_PLAYER5 | IPT_JOYSTICKLEFT_RIGHT },
/*TODO*///
/*TODO*///	{ "P6_JOYSTICK_UP",			IKT_IPT,		IPF_PLAYER6 | IPT_JOYSTICK_UP },
/*TODO*///	{ "P6_JOYSTICK_DOWN",		IKT_IPT,		IPF_PLAYER6 | IPT_JOYSTICK_DOWN },
/*TODO*///	{ "P6_JOYSTICK_LEFT",		IKT_IPT,		IPF_PLAYER6 | IPT_JOYSTICK_LEFT },
/*TODO*///	{ "P6_JOYSTICK_RIGHT",		IKT_IPT,		IPF_PLAYER6 | IPT_JOYSTICK_RIGHT },
/*TODO*///	{ "P6_BUTTON1",				IKT_IPT,		IPF_PLAYER6 | IPT_BUTTON1 },
/*TODO*///	{ "P6_BUTTON2",				IKT_IPT,		IPF_PLAYER6 | IPT_BUTTON2 },
/*TODO*///	{ "P6_BUTTON3",				IKT_IPT,		IPF_PLAYER6 | IPT_BUTTON3 },
/*TODO*///	{ "P6_BUTTON4",				IKT_IPT,		IPF_PLAYER6 | IPT_BUTTON4 },
/*TODO*///	{ "P6_BUTTON5",				IKT_IPT,		IPF_PLAYER6 | IPT_BUTTON5 },
/*TODO*///	{ "P6_BUTTON6",				IKT_IPT,		IPF_PLAYER6 | IPT_BUTTON6 },
/*TODO*///	{ "P6_BUTTON7",				IKT_IPT,		IPF_PLAYER6 | IPT_BUTTON7 },
/*TODO*///	{ "P6_BUTTON8",				IKT_IPT,		IPF_PLAYER6 | IPT_BUTTON8 },
/*TODO*///	{ "P6_BUTTON9",				IKT_IPT,		IPF_PLAYER6 | IPT_BUTTON9 },
/*TODO*///	{ "P6_BUTTON10",			IKT_IPT,		IPF_PLAYER6 | IPT_BUTTON10 },
/*TODO*///	{ "P6_JOYSTICKRIGHT_UP",	IKT_IPT,		IPF_PLAYER6 | IPT_JOYSTICKRIGHT_UP },
/*TODO*///	{ "P6_JOYSTICKRIGHT_DOWN",	IKT_IPT,		IPF_PLAYER6 | IPT_JOYSTICKRIGHT_DOWN },
/*TODO*///	{ "P6_JOYSTICKRIGHT_LEFT",	IKT_IPT,		IPF_PLAYER6 | IPT_JOYSTICKRIGHT_LEFT },
/*TODO*///	{ "P6_JOYSTICKRIGHT_RIGHT",	IKT_IPT,		IPF_PLAYER6 | IPT_JOYSTICKRIGHT_RIGHT },
/*TODO*///	{ "P6_JOYSTICKLEFT_UP",		IKT_IPT,		IPF_PLAYER6 | IPT_JOYSTICKLEFT_UP },
/*TODO*///	{ "P6_JOYSTICKLEFT_DOWN",	IKT_IPT,		IPF_PLAYER6 | IPT_JOYSTICKLEFT_DOWN },
/*TODO*///	{ "P6_JOYSTICKLEFT_LEFT",	IKT_IPT,		IPF_PLAYER6 | IPT_JOYSTICKLEFT_LEFT },
/*TODO*///	{ "P6_JOYSTICKLEFT_RIGHT",	IKT_IPT,		IPF_PLAYER6 | IPT_JOYSTICKLEFT_RIGHT },
/*TODO*///
/*TODO*///	{ "P7_JOYSTICK_UP",			IKT_IPT,		IPF_PLAYER7 | IPT_JOYSTICK_UP },
/*TODO*///	{ "P7_JOYSTICK_DOWN",		IKT_IPT,		IPF_PLAYER7 | IPT_JOYSTICK_DOWN },
/*TODO*///	{ "P7_JOYSTICK_LEFT",		IKT_IPT,		IPF_PLAYER7 | IPT_JOYSTICK_LEFT },
/*TODO*///	{ "P7_JOYSTICK_RIGHT",		IKT_IPT,		IPF_PLAYER7 | IPT_JOYSTICK_RIGHT },
/*TODO*///	{ "P7_BUTTON1",				IKT_IPT,		IPF_PLAYER7 | IPT_BUTTON1 },
/*TODO*///	{ "P7_BUTTON2",				IKT_IPT,		IPF_PLAYER7 | IPT_BUTTON2 },
/*TODO*///	{ "P7_BUTTON3",				IKT_IPT,		IPF_PLAYER7 | IPT_BUTTON3 },
/*TODO*///	{ "P7_BUTTON4",				IKT_IPT,		IPF_PLAYER7 | IPT_BUTTON4 },
/*TODO*///	{ "P7_BUTTON5",				IKT_IPT,		IPF_PLAYER7 | IPT_BUTTON5 },
/*TODO*///	{ "P7_BUTTON6",				IKT_IPT,		IPF_PLAYER7 | IPT_BUTTON6 },
/*TODO*///	{ "P7_BUTTON7",				IKT_IPT,		IPF_PLAYER7 | IPT_BUTTON7 },
/*TODO*///	{ "P7_BUTTON8",				IKT_IPT,		IPF_PLAYER7 | IPT_BUTTON8 },
/*TODO*///	{ "P7_BUTTON9",				IKT_IPT,		IPF_PLAYER7 | IPT_BUTTON9 },
/*TODO*///	{ "P7_BUTTON10",			IKT_IPT,		IPF_PLAYER7 | IPT_BUTTON10 },
/*TODO*///	{ "P7_JOYSTICKRIGHT_UP",	IKT_IPT,		IPF_PLAYER7 | IPT_JOYSTICKRIGHT_UP },
/*TODO*///	{ "P7_JOYSTICKRIGHT_DOWN",	IKT_IPT,		IPF_PLAYER7 | IPT_JOYSTICKRIGHT_DOWN },
/*TODO*///	{ "P7_JOYSTICKRIGHT_LEFT",	IKT_IPT,		IPF_PLAYER7 | IPT_JOYSTICKRIGHT_LEFT },
/*TODO*///	{ "P7_JOYSTICKRIGHT_RIGHT",	IKT_IPT,		IPF_PLAYER7 | IPT_JOYSTICKRIGHT_RIGHT },
/*TODO*///	{ "P7_JOYSTICKLEFT_UP",		IKT_IPT,		IPF_PLAYER7 | IPT_JOYSTICKLEFT_UP },
/*TODO*///	{ "P7_JOYSTICKLEFT_DOWN",	IKT_IPT,		IPF_PLAYER7 | IPT_JOYSTICKLEFT_DOWN },
/*TODO*///	{ "P7_JOYSTICKLEFT_LEFT",	IKT_IPT,		IPF_PLAYER7 | IPT_JOYSTICKLEFT_LEFT },
/*TODO*///	{ "P7_JOYSTICKLEFT_RIGHT",	IKT_IPT,		IPF_PLAYER7 | IPT_JOYSTICKLEFT_RIGHT },
/*TODO*///
/*TODO*///	{ "P8_JOYSTICK_UP",			IKT_IPT,		IPF_PLAYER8 | IPT_JOYSTICK_UP },
/*TODO*///	{ "P8_JOYSTICK_DOWN",		IKT_IPT,		IPF_PLAYER8 | IPT_JOYSTICK_DOWN },
/*TODO*///	{ "P8_JOYSTICK_LEFT",		IKT_IPT,		IPF_PLAYER8 | IPT_JOYSTICK_LEFT },
/*TODO*///	{ "P8_JOYSTICK_RIGHT",		IKT_IPT,		IPF_PLAYER8 | IPT_JOYSTICK_RIGHT },
/*TODO*///	{ "P8_BUTTON1",				IKT_IPT,		IPF_PLAYER8 | IPT_BUTTON1 },
/*TODO*///	{ "P8_BUTTON2",				IKT_IPT,		IPF_PLAYER8 | IPT_BUTTON2 },
/*TODO*///	{ "P8_BUTTON3",				IKT_IPT,		IPF_PLAYER8 | IPT_BUTTON3 },
/*TODO*///	{ "P8_BUTTON4",				IKT_IPT,		IPF_PLAYER8 | IPT_BUTTON4 },
/*TODO*///	{ "P8_BUTTON5",				IKT_IPT,		IPF_PLAYER8 | IPT_BUTTON5 },
/*TODO*///	{ "P8_BUTTON6",				IKT_IPT,		IPF_PLAYER8 | IPT_BUTTON6 },
/*TODO*///	{ "P8_BUTTON7",				IKT_IPT,		IPF_PLAYER8 | IPT_BUTTON7 },
/*TODO*///	{ "P8_BUTTON8",				IKT_IPT,		IPF_PLAYER8 | IPT_BUTTON8 },
/*TODO*///	{ "P8_BUTTON9",				IKT_IPT,		IPF_PLAYER8 | IPT_BUTTON9 },
/*TODO*///	{ "P8_BUTTON10",			IKT_IPT,		IPF_PLAYER8 | IPT_BUTTON10 },
/*TODO*///	{ "P8_JOYSTICKRIGHT_UP",	IKT_IPT,		IPF_PLAYER8 | IPT_JOYSTICKRIGHT_UP },
/*TODO*///	{ "P8_JOYSTICKRIGHT_DOWN",	IKT_IPT,		IPF_PLAYER8 | IPT_JOYSTICKRIGHT_DOWN },
/*TODO*///	{ "P8_JOYSTICKRIGHT_LEFT",	IKT_IPT,		IPF_PLAYER8 | IPT_JOYSTICKRIGHT_LEFT },
/*TODO*///	{ "P8_JOYSTICKRIGHT_RIGHT",	IKT_IPT,		IPF_PLAYER8 | IPT_JOYSTICKRIGHT_RIGHT },
/*TODO*///	{ "P8_JOYSTICKLEFT_UP",		IKT_IPT,		IPF_PLAYER8 | IPT_JOYSTICKLEFT_UP },
/*TODO*///	{ "P8_JOYSTICKLEFT_DOWN",	IKT_IPT,		IPF_PLAYER8 | IPT_JOYSTICKLEFT_DOWN },
/*TODO*///	{ "P8_JOYSTICKLEFT_LEFT",	IKT_IPT,		IPF_PLAYER8 | IPT_JOYSTICKLEFT_LEFT },
/*TODO*///	{ "P8_JOYSTICKLEFT_RIGHT",	IKT_IPT,		IPF_PLAYER8 | IPT_JOYSTICKLEFT_RIGHT },
/*TODO*///
/*TODO*///	{ "P1_PEDAL",				IKT_IPT,		IPF_PLAYER1 | IPT_PEDAL },
/*TODO*///	{ "P1_PEDAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER1 | IPT_PEDAL },
/*TODO*///	{ "P2_PEDAL",				IKT_IPT,		IPF_PLAYER2 | IPT_PEDAL },
/*TODO*///	{ "P2_PEDAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER2 | IPT_PEDAL },
/*TODO*///	{ "P3_PEDAL",				IKT_IPT,		IPF_PLAYER3 | IPT_PEDAL },
/*TODO*///	{ "P3_PEDAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER3 | IPT_PEDAL },
/*TODO*///	{ "P4_PEDAL",				IKT_IPT,		IPF_PLAYER4 | IPT_PEDAL },
/*TODO*///	{ "P4_PEDAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER4 | IPT_PEDAL },
/*TODO*///	{ "P5_PEDAL",				IKT_IPT,		IPF_PLAYER5 | IPT_PEDAL },
/*TODO*///	{ "P5_PEDAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER5 | IPT_PEDAL },
/*TODO*///	{ "P6_PEDAL",				IKT_IPT,		IPF_PLAYER6 | IPT_PEDAL },
/*TODO*///	{ "P6_PEDAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER6 | IPT_PEDAL },
/*TODO*///	{ "P7_PEDAL",				IKT_IPT,		IPF_PLAYER7 | IPT_PEDAL },
/*TODO*///	{ "P7_PEDAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER7 | IPT_PEDAL },
/*TODO*///	{ "P8_PEDAL",				IKT_IPT,		IPF_PLAYER8 | IPT_PEDAL },
/*TODO*///	{ "P8_PEDAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER8 | IPT_PEDAL },
/*TODO*///
/*TODO*///	{ "P1_PEDAL2",				IKT_IPT,		IPF_PLAYER1 | IPT_PEDAL2 },
/*TODO*///	{ "P1_PEDAL2_EXT",			IKT_IPT_EXT,	IPF_PLAYER1 | IPT_PEDAL2 },
/*TODO*///	{ "P2_PEDAL2",				IKT_IPT,		IPF_PLAYER2 | IPT_PEDAL2 },
/*TODO*///	{ "P2_PEDAL2_EXT",			IKT_IPT_EXT,	IPF_PLAYER2 | IPT_PEDAL2 },
/*TODO*///	{ "P3_PEDAL2",				IKT_IPT,		IPF_PLAYER3 | IPT_PEDAL2 },
/*TODO*///	{ "P3_PEDAL2_EXT",			IKT_IPT_EXT,	IPF_PLAYER3 | IPT_PEDAL2 },
/*TODO*///	{ "P4_PEDAL2",				IKT_IPT,		IPF_PLAYER4 | IPT_PEDAL2 },
/*TODO*///	{ "P4_PEDAL2_EXT",			IKT_IPT_EXT,	IPF_PLAYER4 | IPT_PEDAL2 },
/*TODO*///	{ "P5_PEDAL2",				IKT_IPT,		IPF_PLAYER5 | IPT_PEDAL2 },
/*TODO*///	{ "P5_PEDAL2_EXT",			IKT_IPT_EXT,	IPF_PLAYER5 | IPT_PEDAL2 },
/*TODO*///	{ "P6_PEDAL2",				IKT_IPT,		IPF_PLAYER6 | IPT_PEDAL2 },
/*TODO*///	{ "P6_PEDAL2_EXT",			IKT_IPT_EXT,	IPF_PLAYER6 | IPT_PEDAL2 },
/*TODO*///	{ "P7_PEDAL2",				IKT_IPT,		IPF_PLAYER7 | IPT_PEDAL2 },
/*TODO*///	{ "P7_PEDAL2_EXT",			IKT_IPT_EXT,	IPF_PLAYER7 | IPT_PEDAL2 },
/*TODO*///	{ "P8_PEDAL2",				IKT_IPT,		IPF_PLAYER8 | IPT_PEDAL2 },
/*TODO*///	{ "P8_PEDAL2_EXT",			IKT_IPT_EXT,	IPF_PLAYER8 | IPT_PEDAL2 },
/*TODO*///
/*TODO*///	{ "P1_PADDLE",				IKT_IPT,		IPF_PLAYER1 | IPT_PADDLE },
/*TODO*///	{ "P1_PADDLE_EXT",			IKT_IPT_EXT,	IPF_PLAYER1 | IPT_PADDLE },
/*TODO*///	{ "P2_PADDLE",				IKT_IPT,		IPF_PLAYER2 | IPT_PADDLE },
/*TODO*///	{ "P2_PADDLE_EXT",			IKT_IPT_EXT,	IPF_PLAYER2 | IPT_PADDLE },
/*TODO*///	{ "P3_PADDLE",				IKT_IPT,		IPF_PLAYER3 | IPT_PADDLE },
/*TODO*///	{ "P3_PADDLE_EXT",			IKT_IPT_EXT,	IPF_PLAYER3 | IPT_PADDLE },
/*TODO*///	{ "P4_PADDLE",				IKT_IPT,		IPF_PLAYER4 | IPT_PADDLE },
/*TODO*///	{ "P4_PADDLE_EXT",			IKT_IPT_EXT,	IPF_PLAYER4 | IPT_PADDLE },
/*TODO*///	{ "P5_PADDLE",				IKT_IPT,		IPF_PLAYER5 | IPT_PADDLE },
/*TODO*///	{ "P5_PADDLE_EXT",			IKT_IPT_EXT,	IPF_PLAYER5 | IPT_PADDLE },
/*TODO*///	{ "P6_PADDLE",				IKT_IPT,		IPF_PLAYER6 | IPT_PADDLE },
/*TODO*///	{ "P6_PADDLE_EXT",			IKT_IPT_EXT,	IPF_PLAYER6 | IPT_PADDLE },
/*TODO*///	{ "P7_PADDLE",				IKT_IPT,		IPF_PLAYER7 | IPT_PADDLE },
/*TODO*///	{ "P7_PADDLE_EXT",			IKT_IPT_EXT,	IPF_PLAYER7 | IPT_PADDLE },
/*TODO*///	{ "P8_PADDLE",				IKT_IPT,		IPF_PLAYER8 | IPT_PADDLE },
/*TODO*///	{ "P8_PADDLE_EXT",			IKT_IPT_EXT,	IPF_PLAYER8 | IPT_PADDLE },
/*TODO*///
/*TODO*///	{ "P1_PADDLE_V",			IKT_IPT,		IPF_PLAYER1 | IPT_PADDLE_V },
/*TODO*///	{ "P1_PADDLE_V_EXT",		IKT_IPT_EXT,	IPF_PLAYER1 | IPT_PADDLE_V },
/*TODO*///	{ "P2_PADDLE_V",			IKT_IPT,		IPF_PLAYER2 | IPT_PADDLE_V },
/*TODO*///	{ "P2_PADDLE_V_EXT",		IKT_IPT_EXT,	IPF_PLAYER2 | IPT_PADDLE_V },
/*TODO*///	{ "P3_PADDLE_V",			IKT_IPT,		IPF_PLAYER3 | IPT_PADDLE_V },
/*TODO*///	{ "P3_PADDLE_V_EXT",		IKT_IPT_EXT,	IPF_PLAYER3 | IPT_PADDLE_V },
/*TODO*///	{ "P4_PADDLE_V",			IKT_IPT,		IPF_PLAYER4 | IPT_PADDLE_V },
/*TODO*///	{ "P4_PADDLE_V_EXT",		IKT_IPT_EXT,	IPF_PLAYER4 | IPT_PADDLE_V },
/*TODO*///	{ "P5_PADDLE_V",			IKT_IPT,		IPF_PLAYER5 | IPT_PADDLE_V },
/*TODO*///	{ "P5_PADDLE_V_EXT",		IKT_IPT_EXT,	IPF_PLAYER5 | IPT_PADDLE_V },
/*TODO*///	{ "P6_PADDLE_V",			IKT_IPT,		IPF_PLAYER6 | IPT_PADDLE_V },
/*TODO*///	{ "P6_PADDLE_V_EXT",		IKT_IPT_EXT,	IPF_PLAYER6 | IPT_PADDLE_V },
/*TODO*///	{ "P7_PADDLE_V",			IKT_IPT,		IPF_PLAYER7 | IPT_PADDLE_V },
/*TODO*///	{ "P7_PADDLE_V_EXT",		IKT_IPT_EXT,	IPF_PLAYER7 | IPT_PADDLE_V },
/*TODO*///	{ "P8_PADDLE_V",			IKT_IPT,		IPF_PLAYER8 | IPT_PADDLE_V },
/*TODO*///	{ "P8_PADDLE_V_EXT",		IKT_IPT_EXT,	IPF_PLAYER8 | IPT_PADDLE_V },
/*TODO*///
/*TODO*///	{ "P1_DIAL",				IKT_IPT,		IPF_PLAYER1 | IPT_DIAL },
/*TODO*///	{ "P1_DIAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER1 | IPT_DIAL },
/*TODO*///	{ "P2_DIAL",				IKT_IPT,		IPF_PLAYER2 | IPT_DIAL },
/*TODO*///	{ "P2_DIAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER2 | IPT_DIAL },
/*TODO*///	{ "P3_DIAL",				IKT_IPT,		IPF_PLAYER3 | IPT_DIAL },
/*TODO*///	{ "P3_DIAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER3 | IPT_DIAL },
/*TODO*///	{ "P4_DIAL",				IKT_IPT,		IPF_PLAYER4 | IPT_DIAL },
/*TODO*///	{ "P4_DIAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER4 | IPT_DIAL },
/*TODO*///	{ "P5_DIAL",				IKT_IPT,		IPF_PLAYER5 | IPT_DIAL },
/*TODO*///	{ "P5_DIAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER5 | IPT_DIAL },
/*TODO*///	{ "P6_DIAL",				IKT_IPT,		IPF_PLAYER6 | IPT_DIAL },
/*TODO*///	{ "P6_DIAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER6 | IPT_DIAL },
/*TODO*///	{ "P7_DIAL",				IKT_IPT,		IPF_PLAYER7 | IPT_DIAL },
/*TODO*///	{ "P7_DIAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER7 | IPT_DIAL },
/*TODO*///	{ "P8_DIAL",				IKT_IPT,		IPF_PLAYER8 | IPT_DIAL },
/*TODO*///	{ "P8_DIAL_EXT",			IKT_IPT_EXT,	IPF_PLAYER8 | IPT_DIAL },
/*TODO*///
/*TODO*///	{ "P1_DIAL_V",				IKT_IPT,		IPF_PLAYER1 | IPT_DIAL_V },
/*TODO*///	{ "P1_DIAL_V_EXT",			IKT_IPT_EXT,	IPF_PLAYER1 | IPT_DIAL_V },
/*TODO*///	{ "P2_DIAL_V",				IKT_IPT,		IPF_PLAYER2 | IPT_DIAL_V },
/*TODO*///	{ "P2_DIAL_V_EXT",			IKT_IPT_EXT,	IPF_PLAYER2 | IPT_DIAL_V },
/*TODO*///	{ "P3_DIAL_V",				IKT_IPT,		IPF_PLAYER3 | IPT_DIAL_V },
/*TODO*///	{ "P3_DIAL_V_EXT",			IKT_IPT_EXT,	IPF_PLAYER3 | IPT_DIAL_V },
/*TODO*///	{ "P4_DIAL_V",				IKT_IPT,		IPF_PLAYER4 | IPT_DIAL_V },
/*TODO*///	{ "P4_DIAL_V_EXT",			IKT_IPT_EXT,	IPF_PLAYER4 | IPT_DIAL_V },
/*TODO*///	{ "P5_DIAL_V",				IKT_IPT,		IPF_PLAYER5 | IPT_DIAL_V },
/*TODO*///	{ "P5_DIAL_V_EXT",			IKT_IPT_EXT,	IPF_PLAYER5 | IPT_DIAL_V },
/*TODO*///	{ "P6_DIAL_V",				IKT_IPT,		IPF_PLAYER6 | IPT_DIAL_V },
/*TODO*///	{ "P6_DIAL_V_EXT",			IKT_IPT_EXT,	IPF_PLAYER6 | IPT_DIAL_V },
/*TODO*///	{ "P7_DIAL_V",				IKT_IPT,		IPF_PLAYER7 | IPT_DIAL_V },
/*TODO*///	{ "P7_DIAL_V_EXT",			IKT_IPT_EXT,	IPF_PLAYER7 | IPT_DIAL_V },
/*TODO*///	{ "P8_DIAL_V",				IKT_IPT,		IPF_PLAYER8 | IPT_DIAL_V },
/*TODO*///	{ "P8_DIAL_V_EXT",			IKT_IPT_EXT,	IPF_PLAYER8 | IPT_DIAL_V },
/*TODO*///
/*TODO*///	{ "P1_TRACKBALL_X",			IKT_IPT,		IPF_PLAYER1 | IPT_TRACKBALL_X },
/*TODO*///	{ "P1_TRACKBALL_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER1 | IPT_TRACKBALL_X },
/*TODO*///	{ "P2_TRACKBALL_X",			IKT_IPT,		IPF_PLAYER2 | IPT_TRACKBALL_X },
/*TODO*///	{ "P2_TRACKBALL_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER2 | IPT_TRACKBALL_X },
/*TODO*///	{ "P3_TRACKBALL_X",			IKT_IPT,		IPF_PLAYER3 | IPT_TRACKBALL_X },
/*TODO*///	{ "P3_TRACKBALL_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER3 | IPT_TRACKBALL_X },
/*TODO*///	{ "P4_TRACKBALL_X",			IKT_IPT,		IPF_PLAYER4 | IPT_TRACKBALL_X },
/*TODO*///	{ "P4_TRACKBALL_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER4 | IPT_TRACKBALL_X },
/*TODO*///	{ "P5_TRACKBALL_X",			IKT_IPT,		IPF_PLAYER5 | IPT_TRACKBALL_X },
/*TODO*///	{ "P5_TRACKBALL_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER5 | IPT_TRACKBALL_X },
/*TODO*///	{ "P6_TRACKBALL_X",			IKT_IPT,		IPF_PLAYER6 | IPT_TRACKBALL_X },
/*TODO*///	{ "P6_TRACKBALL_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER6 | IPT_TRACKBALL_X },
/*TODO*///	{ "P7_TRACKBALL_X",			IKT_IPT,		IPF_PLAYER7 | IPT_TRACKBALL_X },
/*TODO*///	{ "P7_TRACKBALL_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER7 | IPT_TRACKBALL_X },
/*TODO*///	{ "P8_TRACKBALL_X",			IKT_IPT,		IPF_PLAYER8 | IPT_TRACKBALL_X },
/*TODO*///	{ "P8_TRACKBALL_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER8 | IPT_TRACKBALL_X },
/*TODO*///
/*TODO*///	{ "P1_TRACKBALL_Y",			IKT_IPT,		IPF_PLAYER1 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P1_TRACKBALL_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER1 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P2_TRACKBALL_Y",			IKT_IPT,		IPF_PLAYER2 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P2_TRACKBALL_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER2 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P3_TRACKBALL_Y",			IKT_IPT,		IPF_PLAYER3 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P3_TRACKBALL_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER3 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P4_TRACKBALL_Y",			IKT_IPT,		IPF_PLAYER4 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P4_TRACKBALL_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER4 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P5_TRACKBALL_Y",			IKT_IPT,		IPF_PLAYER5 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P5_TRACKBALL_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER5 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P6_TRACKBALL_Y",			IKT_IPT,		IPF_PLAYER6 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P6_TRACKBALL_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER6 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P7_TRACKBALL_Y",			IKT_IPT,		IPF_PLAYER7 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P7_TRACKBALL_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER7 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P8_TRACKBALL_Y",			IKT_IPT,		IPF_PLAYER8 | IPT_TRACKBALL_Y },
/*TODO*///	{ "P8_TRACKBALL_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER8 | IPT_TRACKBALL_Y },
/*TODO*///
/*TODO*///	{ "P1_AD_STICK_X",			IKT_IPT,		IPF_PLAYER1 | IPT_AD_STICK_X },
/*TODO*///	{ "P1_AD_STICK_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER1 | IPT_AD_STICK_X },
/*TODO*///	{ "P2_AD_STICK_X",			IKT_IPT,		IPF_PLAYER2 | IPT_AD_STICK_X },
/*TODO*///	{ "P2_AD_STICK_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER2 | IPT_AD_STICK_X },
/*TODO*///	{ "P3_AD_STICK_X",			IKT_IPT,		IPF_PLAYER3 | IPT_AD_STICK_X },
/*TODO*///	{ "P3_AD_STICK_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER3 | IPT_AD_STICK_X },
/*TODO*///	{ "P4_AD_STICK_X",			IKT_IPT,		IPF_PLAYER4 | IPT_AD_STICK_X },
/*TODO*///	{ "P4_AD_STICK_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER4 | IPT_AD_STICK_X },
/*TODO*///	{ "P5_AD_STICK_X",			IKT_IPT,		IPF_PLAYER5 | IPT_AD_STICK_X },
/*TODO*///	{ "P5_AD_STICK_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER5 | IPT_AD_STICK_X },
/*TODO*///	{ "P6_AD_STICK_X",			IKT_IPT,		IPF_PLAYER6 | IPT_AD_STICK_X },
/*TODO*///	{ "P6_AD_STICK_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER6 | IPT_AD_STICK_X },
/*TODO*///	{ "P7_AD_STICK_X",			IKT_IPT,		IPF_PLAYER7 | IPT_AD_STICK_X },
/*TODO*///	{ "P7_AD_STICK_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER7 | IPT_AD_STICK_X },
/*TODO*///	{ "P8_AD_STICK_X",			IKT_IPT,		IPF_PLAYER8 | IPT_AD_STICK_X },
/*TODO*///	{ "P8_AD_STICK_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER8 | IPT_AD_STICK_X },
/*TODO*///
/*TODO*///	{ "P1_AD_STICK_Y",			IKT_IPT,		IPF_PLAYER1 | IPT_AD_STICK_Y },
/*TODO*///	{ "P1_AD_STICK_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER1 | IPT_AD_STICK_Y },
/*TODO*///	{ "P2_AD_STICK_Y",			IKT_IPT,		IPF_PLAYER2 | IPT_AD_STICK_Y },
/*TODO*///	{ "P2_AD_STICK_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER2 | IPT_AD_STICK_Y },
/*TODO*///	{ "P3_AD_STICK_Y",			IKT_IPT,		IPF_PLAYER3 | IPT_AD_STICK_Y },
/*TODO*///	{ "P3_AD_STICK_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER3 | IPT_AD_STICK_Y },
/*TODO*///	{ "P4_AD_STICK_Y",			IKT_IPT,		IPF_PLAYER4 | IPT_AD_STICK_Y },
/*TODO*///	{ "P4_AD_STICK_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER4 | IPT_AD_STICK_Y },
/*TODO*///	{ "P5_AD_STICK_Y",			IKT_IPT,		IPF_PLAYER5 | IPT_AD_STICK_Y },
/*TODO*///	{ "P5_AD_STICK_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER5 | IPT_AD_STICK_Y },
/*TODO*///	{ "P6_AD_STICK_Y",			IKT_IPT,		IPF_PLAYER6 | IPT_AD_STICK_Y },
/*TODO*///	{ "P6_AD_STICK_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER6 | IPT_AD_STICK_Y },
/*TODO*///	{ "P7_AD_STICK_Y",			IKT_IPT,		IPF_PLAYER7 | IPT_AD_STICK_Y },
/*TODO*///	{ "P7_AD_STICK_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER7 | IPT_AD_STICK_Y },
/*TODO*///	{ "P8_AD_STICK_Y",			IKT_IPT,		IPF_PLAYER8 | IPT_AD_STICK_Y },
/*TODO*///	{ "P8_AD_STICK_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER8 | IPT_AD_STICK_Y },
/*TODO*///
/*TODO*///	{ "P1_LIGHTGUN_X",			IKT_IPT,		IPF_PLAYER1 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P1_LIGHTGUN_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER1 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P2_LIGHTGUN_X",			IKT_IPT,		IPF_PLAYER2 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P2_LIGHTGUN_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER2 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P3_LIGHTGUN_X",			IKT_IPT,		IPF_PLAYER3 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P3_LIGHTGUN_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER3 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P4_LIGHTGUN_X",			IKT_IPT,		IPF_PLAYER4 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P4_LIGHTGUN_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER4 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P5_LIGHTGUN_X",			IKT_IPT,		IPF_PLAYER5 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P5_LIGHTGUN_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER5 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P6_LIGHTGUN_X",			IKT_IPT,		IPF_PLAYER6 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P6_LIGHTGUN_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER6 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P7_LIGHTGUN_X",			IKT_IPT,		IPF_PLAYER7 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P7_LIGHTGUN_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER7 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P8_LIGHTGUN_X",			IKT_IPT,		IPF_PLAYER8 | IPT_LIGHTGUN_X },
/*TODO*///	{ "P8_LIGHTGUN_X_EXT",		IKT_IPT_EXT,	IPF_PLAYER8 | IPT_LIGHTGUN_X },
/*TODO*///
/*TODO*///	{ "P1_LIGHTGUN_Y",			IKT_IPT,		IPF_PLAYER1 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P1_LIGHTGUN_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER1 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P2_LIGHTGUN_Y",			IKT_IPT,		IPF_PLAYER2 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P2_LIGHTGUN_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER2 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P3_LIGHTGUN_Y",			IKT_IPT,		IPF_PLAYER3 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P3_LIGHTGUN_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER3 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P4_LIGHTGUN_Y",			IKT_IPT,		IPF_PLAYER4 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P4_LIGHTGUN_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER4 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P5_LIGHTGUN_Y",			IKT_IPT,		IPF_PLAYER5 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P5_LIGHTGUN_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER5 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P6_LIGHTGUN_Y",			IKT_IPT,		IPF_PLAYER6 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P6_LIGHTGUN_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER6 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P7_LIGHTGUN_Y",			IKT_IPT,		IPF_PLAYER7 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P7_LIGHTGUN_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER7 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P8_LIGHTGUN_Y",			IKT_IPT,		IPF_PLAYER8 | IPT_LIGHTGUN_Y },
/*TODO*///	{ "P8_LIGHTGUN_Y_EXT",		IKT_IPT_EXT,	IPF_PLAYER8 | IPT_LIGHTGUN_Y },
/*TODO*///
/*TODO*///	{ "P1_AD_STICK_Z",			IKT_IPT,		IPF_PLAYER1 | IPT_AD_STICK_Z },
/*TODO*///	{ "P1_AD_STICK_Z_EXT",		IKT_IPT_EXT,	IPF_PLAYER1 | IPT_AD_STICK_Z },
/*TODO*///	{ "P2_AD_STICK_Z",			IKT_IPT,		IPF_PLAYER2 | IPT_AD_STICK_Z },
/*TODO*///	{ "P2_AD_STICK_Z_EXT",		IKT_IPT_EXT,	IPF_PLAYER2 | IPT_AD_STICK_Z },
/*TODO*///	{ "P3_AD_STICK_Z",			IKT_IPT,		IPF_PLAYER3 | IPT_AD_STICK_Z },
/*TODO*///	{ "P3_AD_STICK_Z_EXT",		IKT_IPT_EXT,	IPF_PLAYER3 | IPT_AD_STICK_Z },
/*TODO*///	{ "P4_AD_STICK_Z",			IKT_IPT,		IPF_PLAYER4 | IPT_AD_STICK_Z },
/*TODO*///	{ "P4_AD_STICK_Z_EXT",		IKT_IPT_EXT,	IPF_PLAYER4 | IPT_AD_STICK_Z },
/*TODO*///	{ "P5_AD_STICK_Z",			IKT_IPT,		IPF_PLAYER5 | IPT_AD_STICK_Z },
/*TODO*///	{ "P5_AD_STICK_Z_EXT",		IKT_IPT_EXT,	IPF_PLAYER5 | IPT_AD_STICK_Z },
/*TODO*///	{ "P6_AD_STICK_Z",			IKT_IPT,		IPF_PLAYER6 | IPT_AD_STICK_Z },
/*TODO*///	{ "P6_AD_STICK_Z_EXT",		IKT_IPT_EXT,	IPF_PLAYER6 | IPT_AD_STICK_Z },
/*TODO*///	{ "P7_AD_STICK_Z",			IKT_IPT,		IPF_PLAYER7 | IPT_AD_STICK_Z },
/*TODO*///	{ "P7_AD_STICK_Z_EXT",		IKT_IPT_EXT,	IPF_PLAYER7 | IPT_AD_STICK_Z },
/*TODO*///	{ "P8_AD_STICK_Z",			IKT_IPT,		IPF_PLAYER8 | IPT_AD_STICK_Z },
/*TODO*///	{ "P8_AD_STICK_Z_EXT",		IKT_IPT_EXT,	IPF_PLAYER8 | IPT_AD_STICK_Z },
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///	{ "P1_MOUSE_X",				IKT_IPT,		IPF_PLAYER1 | IPT_MOUSE_X },
/*TODO*///	{ "P1_MOUSE_X_EXT",			IKT_IPT_EXT,	IPF_PLAYER1 | IPT_MOUSE_X },
/*TODO*///	{ "P2_MOUSE_X",				IKT_IPT,		IPF_PLAYER2 | IPT_MOUSE_X },
/*TODO*///	{ "P2_MOUSE_X_EXT",			IKT_IPT_EXT,	IPF_PLAYER2 | IPT_MOUSE_X },
/*TODO*///	{ "P3_MOUSE_X",				IKT_IPT,		IPF_PLAYER3 | IPT_MOUSE_X },
/*TODO*///	{ "P3_MOUSE_X_EXT",			IKT_IPT_EXT,	IPF_PLAYER3 | IPT_MOUSE_X },
/*TODO*///	{ "P4_MOUSE_X",				IKT_IPT,		IPF_PLAYER4 | IPT_MOUSE_X },
/*TODO*///	{ "P4_MOUSE_X_EXT",			IKT_IPT_EXT,	IPF_PLAYER4 | IPT_MOUSE_X },
/*TODO*///	{ "P5_MOUSE_X",				IKT_IPT,		IPF_PLAYER5 | IPT_MOUSE_X },
/*TODO*///	{ "P5_MOUSE_X_EXT",			IKT_IPT_EXT,	IPF_PLAYER5 | IPT_MOUSE_X },
/*TODO*///	{ "P6_MOUSE_X",				IKT_IPT,		IPF_PLAYER6 | IPT_MOUSE_X },
/*TODO*///	{ "P6_MOUSE_X_EXT",			IKT_IPT_EXT,	IPF_PLAYER6 | IPT_MOUSE_X },
/*TODO*///	{ "P7_MOUSE_X",				IKT_IPT,		IPF_PLAYER7 | IPT_MOUSE_X },
/*TODO*///	{ "P7_MOUSE_X_EXT",			IKT_IPT_EXT,	IPF_PLAYER7 | IPT_MOUSE_X },
/*TODO*///	{ "P8_MOUSE_X",				IKT_IPT,		IPF_PLAYER8 | IPT_MOUSE_X },
/*TODO*///	{ "P8_MOUSE_X_EXT",			IKT_IPT_EXT,	IPF_PLAYER8 | IPT_MOUSE_X },
/*TODO*///
/*TODO*///	{ "P1_MOUSE_Y",				IKT_IPT,		IPF_PLAYER1 | IPT_MOUSE_Y },
/*TODO*///	{ "P1_MOUSE_Y_EXT",			IKT_IPT_EXT,	IPF_PLAYER1 | IPT_MOUSE_Y },
/*TODO*///	{ "P2_MOUSE_Y",				IKT_IPT,		IPF_PLAYER2 | IPT_MOUSE_Y },
/*TODO*///	{ "P2_MOUSE_Y_EXT",			IKT_IPT_EXT,	IPF_PLAYER2 | IPT_MOUSE_Y },
/*TODO*///	{ "P3_MOUSE_Y",				IKT_IPT,		IPF_PLAYER3 | IPT_MOUSE_Y },
/*TODO*///	{ "P3_MOUSE_Y_EXT",			IKT_IPT_EXT,	IPF_PLAYER3 | IPT_MOUSE_Y },
/*TODO*///	{ "P4_MOUSE_Y",				IKT_IPT,		IPF_PLAYER4 | IPT_MOUSE_Y },
/*TODO*///	{ "P4_MOUSE_Y_EXT",			IKT_IPT_EXT,	IPF_PLAYER4 | IPT_MOUSE_Y },
/*TODO*///	{ "P5_MOUSE_Y",				IKT_IPT,		IPF_PLAYER5 | IPT_MOUSE_Y },
/*TODO*///	{ "P5_MOUSE_Y_EXT",			IKT_IPT_EXT,	IPF_PLAYER5 | IPT_MOUSE_Y },
/*TODO*///	{ "P6_MOUSE_Y",				IKT_IPT,		IPF_PLAYER6 | IPT_MOUSE_Y },
/*TODO*///	{ "P6_MOUSE_Y_EXT",			IKT_IPT_EXT,	IPF_PLAYER6 | IPT_MOUSE_Y },
/*TODO*///	{ "P7_MOUSE_Y",				IKT_IPT,		IPF_PLAYER7 | IPT_MOUSE_Y },
/*TODO*///	{ "P7_MOUSE_Y_EXT",			IKT_IPT_EXT,	IPF_PLAYER7 | IPT_MOUSE_Y },
/*TODO*///	{ "P8_MOUSE_Y",				IKT_IPT,		IPF_PLAYER8 | IPT_MOUSE_Y },
/*TODO*///	{ "P8_MOUSE_Y_EXT",			IKT_IPT_EXT,	IPF_PLAYER8 | IPT_MOUSE_Y },
/*TODO*///
/*TODO*///	{ "P1_START",				IKT_IPT,		IPF_PLAYER1 | IPT_START },
/*TODO*///	{ "P2_START",				IKT_IPT,		IPF_PLAYER2 | IPT_START },
/*TODO*///	{ "P3_START",				IKT_IPT,		IPF_PLAYER3 | IPT_START },
/*TODO*///	{ "P4_START",				IKT_IPT,		IPF_PLAYER4 | IPT_START },
/*TODO*///	{ "P5_START",				IKT_IPT,		IPF_PLAYER5 | IPT_START },
/*TODO*///	{ "P6_START",				IKT_IPT,		IPF_PLAYER6 | IPT_START },
/*TODO*///	{ "P7_START",				IKT_IPT,		IPF_PLAYER7 | IPT_START },
/*TODO*///	{ "P8_START",				IKT_IPT,		IPF_PLAYER8 | IPT_START },
/*TODO*///	{ "P1_SELECT",				IKT_IPT,		IPF_PLAYER1 | IPT_SELECT },
/*TODO*///	{ "P2_SELECT",				IKT_IPT,		IPF_PLAYER2 | IPT_SELECT },
/*TODO*///	{ "P3_SELECT",				IKT_IPT,		IPF_PLAYER3 | IPT_SELECT },
/*TODO*///	{ "P4_SELECT",				IKT_IPT,		IPF_PLAYER4 | IPT_SELECT },
/*TODO*///	{ "P5_SELECT",				IKT_IPT,		IPF_PLAYER5 | IPT_SELECT },
/*TODO*///	{ "P6_SELECT",				IKT_IPT,		IPF_PLAYER6 | IPT_SELECT },
/*TODO*///	{ "P7_SELECT",				IKT_IPT,		IPF_PLAYER7 | IPT_SELECT },
/*TODO*///	{ "P8_SELECT",				IKT_IPT,		IPF_PLAYER8 | IPT_SELECT },
/*TODO*///#endif /* MESS */
/*TODO*///
/*TODO*///	{ "OSD_1",					IKT_IPT,		IPT_OSD_1 },
/*TODO*///	{ "OSD_2",					IKT_IPT,		IPT_OSD_2 },
/*TODO*///	{ "OSD_3",					IKT_IPT,		IPT_OSD_3 },
/*TODO*///	{ "OSD_4",					IKT_IPT,		IPT_OSD_4 },
/*TODO*///
/*TODO*///	{ "UNKNOWN",				IKT_IPT,		IPT_UNKNOWN },
/*TODO*///	{ "END",					IKT_IPT,		IPT_END },
/*TODO*///
/*TODO*///	{ "",						0,	0 }
/*TODO*///};
/*TODO*///
/*TODO*///int num_ik = sizeof(input_keywords)/sizeof(struct ik);
/*TODO*///
/*TODO*////***************************************************************************/
/*TODO*////* Generic IO */
/*TODO*///
/*TODO*///static int readint(mame_file *f,UINT32 *num)
/*TODO*///{
/*TODO*///	unsigned i;
/*TODO*///
/*TODO*///	*num = 0;
/*TODO*///	for (i = 0;i < sizeof(UINT32);i++)
/*TODO*///	{
/*TODO*///		unsigned char c;
/*TODO*///
/*TODO*///
/*TODO*///		*num <<= 8;
/*TODO*///		if (mame_fread(f,&c,1) != 1)
/*TODO*///			return -1;
/*TODO*///		*num |= c;
/*TODO*///	}
/*TODO*///
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///static void writeint(mame_file *f,UINT32 num)
/*TODO*///{
/*TODO*///	unsigned i;
/*TODO*///
/*TODO*///	for (i = 0;i < sizeof(UINT32);i++)
/*TODO*///	{
/*TODO*///		unsigned char c;
/*TODO*///
/*TODO*///
/*TODO*///		c = (num >> 8 * (sizeof(UINT32)-1)) & 0xff;
/*TODO*///		mame_fwrite(f,&c,1);
/*TODO*///		num <<= 8;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///static int readword(mame_file *f,UINT16 *num)
/*TODO*///{
/*TODO*///	unsigned i;
/*TODO*///	int res;
/*TODO*///
/*TODO*///	res = 0;
/*TODO*///	for (i = 0;i < sizeof(UINT16);i++)
/*TODO*///	{
/*TODO*///		unsigned char c;
/*TODO*///
/*TODO*///
/*TODO*///		res <<= 8;
/*TODO*///		if (mame_fread(f,&c,1) != 1)
/*TODO*///			return -1;
/*TODO*///		res |= c;
/*TODO*///	}
/*TODO*///
/*TODO*///	*num = res;
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///static void writeword(mame_file *f,UINT16 num)
/*TODO*///{
/*TODO*///	unsigned i;
/*TODO*///
/*TODO*///	for (i = 0;i < sizeof(UINT16);i++)
/*TODO*///	{
/*TODO*///		unsigned char c;
/*TODO*///
/*TODO*///
/*TODO*///		c = (num >> 8 * (sizeof(UINT16)-1)) & 0xff;
/*TODO*///		mame_fwrite(f,&c,1);
/*TODO*///		num <<= 8;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************/
/*TODO*////* Load */
/*TODO*///
/*TODO*///static void load_default_keys(void)
/*TODO*///{
/*TODO*///	config_file *cfg;
/*TODO*///
/*TODO*///	osd_customize_inputport_defaults(inputport_defaults);
/*TODO*///	memcpy(inputport_defaults_backup,inputport_defaults,sizeof(inputport_defaults));
/*TODO*///
/*TODO*///	cfg = config_open(NULL);
/*TODO*///	if (cfg)
/*TODO*///	{
/*TODO*///		config_read_default_ports(cfg, inputport_defaults);
/*TODO*///		config_close(cfg);
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///static void save_default_keys(void)
/*TODO*///{
/*TODO*///	config_file *cfg;
/*TODO*///
/*TODO*///	cfg = config_create(NULL);
/*TODO*///	if (cfg)
/*TODO*///	{
/*TODO*///		config_write_default_ports(cfg, inputport_defaults_backup, inputport_defaults);
/*TODO*///		config_close(cfg);
/*TODO*///			}
/*TODO*///
/*TODO*///	memcpy(inputport_defaults,inputport_defaults_backup,sizeof(inputport_defaults_backup));
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///int load_input_port_settings(void)
/*TODO*///{
/*TODO*///	config_file *cfg;
/*TODO*///	int err;
/*TODO*///	struct mixer_config mixercfg;
/*TODO*///#ifdef MAME_NET
/*TODO*///    struct InputPort *in;
/*TODO*///    int port, player;
/*TODO*///#endif /* MAME_NET */
/*TODO*///
/*TODO*///
/*TODO*///	load_default_keys();
/*TODO*///
/*TODO*///	cfg = config_open(Machine->gamedrv->name);
/*TODO*///	if (cfg)
/*TODO*///		{
/*TODO*///		err = config_read_ports(cfg, Machine->input_ports_default, Machine->input_ports);
/*TODO*///		if (err)
/*TODO*///				goto getout;
/*TODO*///
/*TODO*///		err = config_read_coin_and_ticket_counters(cfg, coins, lastcoin, coinlockedout, &dispensed_tickets);
/*TODO*///		if (err)
/*TODO*///				goto getout;
/*TODO*///
/*TODO*///		err = config_read_mixer_config(cfg, &mixercfg);
/*TODO*///		if (err)
/*TODO*///			goto getout;
/*TODO*///
/*TODO*///		mixer_load_config(&mixercfg);
/*TODO*///
/*TODO*///getout:
/*TODO*///		config_close(cfg);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* All analog ports need initialization */
/*TODO*///	{
/*TODO*///		int i;
/*TODO*///		for (i = 0; i < MAX_INPUT_PORTS; i++)
/*TODO*///			input_analog_init[i] = 1;
/*TODO*///	}
/*TODO*///#ifdef MAME_NET
/*TODO*///	/* Find out what port is used by what player and swap regular inputs */
/*TODO*///	in = Machine->input_ports;
/*TODO*///
/*TODO*/////	if (in->type == IPT_END) return; 	/* nothing to do */
/*TODO*///
/*TODO*///	/* make sure the InputPort definition is correct */
/*TODO*/////	if (in->type != IPT_PORT)
/*TODO*/////	{
/*TODO*/////		logerror("Error in InputPort definition: expecting PORT_START\n");
/*TODO*/////		return;
/*TODO*/////	}
/*TODO*/////	else in++;
/*TODO*///	in++;
/*TODO*///
/*TODO*///	/* scan all the input ports */
/*TODO*///	port = 0;
/*TODO*///	while (in->type != IPT_END && port < MAX_INPUT_PORTS)
/*TODO*///	{
/*TODO*///		/* now check the input bits. */
/*TODO*///		while (in->type != IPT_END && in->type != IPT_PORT)
/*TODO*///		{
/*TODO*///			if ((in->type & ~IPF_MASK) != IPT_DIPSWITCH_SETTING &&	/* skip dipswitch definitions */
/*TODO*///#ifdef MESS
/*TODO*///				(in->type & ~IPF_MASK) != IPT_CONFIG_SETTING &&		/* skip config definitions */
/*TODO*///#endif
/*TODO*///				(in->type & ~IPF_MASK) != IPT_EXTENSION &&			/* skip analog extension fields */
/*TODO*///				(in->type & IPF_UNUSED) == 0 &&						/* skip unused bits */
/*TODO*///				!(!options.cheat && (in->type & IPF_CHEAT)) &&				/* skip cheats if cheats disabled */
/*TODO*///				(in->type & ~IPF_MASK) != IPT_VBLANK &&				/* skip vblank stuff */
/*TODO*///				((in->type & ~IPF_MASK) >= IPT_COIN1 &&				/* skip if coin input and it's locked out */
/*TODO*///				(in->type & ~IPF_MASK) <= IPT_COIN4 &&
/*TODO*///                 coinlockedout[(in->type & ~IPF_MASK) - IPT_COIN1]))
/*TODO*///			{
/*TODO*///				player = IP_GET_PLAYER(in);
/*TODO*///
/*TODO*///				if (((in->type & ~IPF_MASK) > IPT_ANALOG_START)
/*TODO*///					&& ((in->type & ~IPF_MASK) < IPT_ANALOG_END))
/*TODO*///				{
/*TODO*///					analog_player_port[port] = player;
/*TODO*///				}
/*TODO*///				if (((in->type & ~IPF_MASK) == IPT_BUTTON1) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_BUTTON2) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_BUTTON3) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_BUTTON4) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICK_UP) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICK_DOWN) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICK_LEFT) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICK_RIGHT) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKRIGHT_UP) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKRIGHT_DOWN) ||
/*TODO*/// 					((in->type & ~IPF_MASK) == IPT_JOYSTICKRIGHT_LEFT) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKRIGHT_RIGHT) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKLEFT_UP) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKLEFT_DOWN) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKLEFT_LEFT) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKLEFT_RIGHT) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_PADDLE) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_DIAL) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_TRACKBALL_X) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_TRACKBALL_Y) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_LIGHTGUN_X) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_LIGHTGUN_Y) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_AD_STICK_X) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_AD_STICK_Y) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_AD_STICK_Z))
/*TODO*///				{
/*TODO*///					switch (default_player)
/*TODO*///					{
/*TODO*///						case 0:
/*TODO*///							/* do nothing */
/*TODO*///							break;
/*TODO*///						case 1:
/*TODO*///							if (player == 0)
/*TODO*///							{
/*TODO*///								in->type &= ~IPF_PLAYER1;
/*TODO*///								in->type |= IPF_PLAYER2;
/*TODO*///							}
/*TODO*///							else if (player == 1)
/*TODO*///							{
/*TODO*///								in->type &= ~IPF_PLAYER2;
/*TODO*///								in->type |= IPF_PLAYER1;
/*TODO*///							}
/*TODO*///							break;
/*TODO*///						case 2:
/*TODO*///							if (player == 0)
/*TODO*///							{
/*TODO*///								in->type &= ~IPF_PLAYER1;
/*TODO*///								in->type |= IPF_PLAYER3;
/*TODO*///							}
/*TODO*///							else if (player == 2)
/*TODO*///							{
/*TODO*///								in->type &= ~IPF_PLAYER3;
/*TODO*///								in->type |= IPF_PLAYER1;
/*TODO*///							}
/*TODO*///							break;
/*TODO*///						case 3:
/*TODO*///							if (player == 0)
/*TODO*///							{
/*TODO*///								in->type &= ~IPF_PLAYER1;
/*TODO*///								in->type |= IPF_PLAYER4;
/*TODO*///							}
/*TODO*///							else if (player == 3)
/*TODO*///							{
/*TODO*///								in->type &= ~IPF_PLAYER4;
/*TODO*///								in->type |= IPF_PLAYER1;
/*TODO*///							}
/*TODO*///							break;
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///			in++;
/*TODO*///		}
/*TODO*///		port++;
/*TODO*///		if (in->type == IPT_PORT) in++;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* TODO: at this point the games should initialize peers to same as server */
/*TODO*///
/*TODO*///#endif /* MAME_NET */
/*TODO*///
/*TODO*///	init_analog_seq();
/*TODO*///
/*TODO*///	update_input_ports();
/*TODO*///
/*TODO*///	/* if we didn't find a saved config, return 0 so the main core knows that it */
/*TODO*///	/* is the first time the game is run and it should diplay the disclaimer. */
/*TODO*///	return cfg ? 1 : 0;
/*TODO*///}
/*TODO*///
/*TODO*////***************************************************************************/
/*TODO*////* Save */
/*TODO*///
/*TODO*///void save_input_port_settings(void)
/*TODO*///{
/*TODO*///	config_file *cfg;
/*TODO*///	struct mixer_config mixercfg;
/*TODO*///#ifdef MAME_NET
/*TODO*///	struct InputPort *in;
/*TODO*///	int port, player;
/*TODO*///
/*TODO*///	/* Swap input port definitions back to defaults */
/*TODO*///	in = Machine->input_ports;
/*TODO*///
/*TODO*///	if (in->type == IPT_END) return; 	/* nothing to do */
/*TODO*///
/*TODO*///	/* make sure the InputPort definition is correct */
/*TODO*///	if (in->type != IPT_PORT)
/*TODO*///	{
/*TODO*///		logerror("Error in InputPort definition: expecting PORT_START\n");
/*TODO*///		return;
/*TODO*///	}
/*TODO*///	else in++;
/*TODO*///
/*TODO*///	/* scan all the input ports */
/*TODO*///	port = 0;
/*TODO*///	while (in->type != IPT_END && port < MAX_INPUT_PORTS)
/*TODO*///	{
/*TODO*///		/* now check the input bits. */
/*TODO*///		while (in->type != IPT_END && in->type != IPT_PORT)
/*TODO*///		{
/*TODO*///			if ((in->type & ~IPF_MASK) != IPT_DIPSWITCH_SETTING &&	/* skip dipswitch definitions */
/*TODO*///#ifdef MESS
/*TODO*///				(in->type & ~IPF_MASK) != IPT_CONFIG_SETTING &&		/* skip config definitions */
/*TODO*///#endif
/*TODO*///				(in->type & ~IPF_MASK) != IPT_EXTENSION &&			/* skip analog extension fields */
/*TODO*///				(in->type & IPF_UNUSED) == 0 &&						/* skip unused bits */
/*TODO*///				!(!options.cheat && (in->type & IPF_CHEAT)) &&				/* skip cheats if cheats disabled */
/*TODO*///				(in->type & ~IPF_MASK) != IPT_VBLANK &&				/* skip vblank stuff */
/*TODO*///				((in->type & ~IPF_MASK) >= IPT_COIN1 &&				/* skip if coin input and it's locked out */
/*TODO*///				(in->type & ~IPF_MASK) <= IPT_COIN4 &&
/*TODO*///                 coinlockedout[(in->type & ~IPF_MASK) - IPT_COIN1]))
/*TODO*///			{
/*TODO*///				player = IP_GET_PLAYER(in);
/*TODO*///
/*TODO*///				if (((in->type & ~IPF_MASK) == IPT_BUTTON1) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_BUTTON2) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_BUTTON3) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_BUTTON4) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICK_UP) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICK_DOWN) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICK_LEFT) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICK_RIGHT) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKRIGHT_UP) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKRIGHT_DOWN) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKRIGHT_LEFT) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKRIGHT_RIGHT) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKLEFT_UP) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKLEFT_DOWN) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKLEFT_LEFT) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_JOYSTICKLEFT_RIGHT) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_PADDLE) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_DIAL) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_TRACKBALL_X) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_TRACKBALL_Y) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_LIGHTGUN_X) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_LIGHTGUN_Y) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_AD_STICK_X) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_AD_STICK_Y) ||
/*TODO*///					((in->type & ~IPF_MASK) == IPT_AD_STICK_Z))
/*TODO*///				{
/*TODO*///					switch (default_player)
/*TODO*///					{
/*TODO*///						case 0:
/*TODO*///							/* do nothing */
/*TODO*///							analog_player_port[port] = player;
/*TODO*///							break;
/*TODO*///						case 1:
/*TODO*///							if (player == 0)
/*TODO*///							{
/*TODO*///								in->type &= ~IPF_PLAYER1;
/*TODO*///								in->type |= IPF_PLAYER2;
/*TODO*///								analog_player_port[port] = 1;
/*TODO*///							}
/*TODO*///							else if (player == 1)
/*TODO*///							{
/*TODO*///								in->type &= ~IPF_PLAYER2;
/*TODO*///								in->type |= IPF_PLAYER1;
/*TODO*///								analog_player_port[port] = 0;
/*TODO*///							}
/*TODO*///							break;
/*TODO*///						case 2:
/*TODO*///							if (player == 0)
/*TODO*///							{
/*TODO*///								in->type &= ~IPF_PLAYER1;
/*TODO*///								in->type |= IPF_PLAYER3;
/*TODO*///								analog_player_port[port] = 2;
/*TODO*///							}
/*TODO*///							else if (player == 2)
/*TODO*///							{
/*TODO*///								in->type &= ~IPF_PLAYER3;
/*TODO*///								in->type |= IPF_PLAYER1;
/*TODO*///								analog_player_port[port] = 0;
/*TODO*///							}
/*TODO*///							break;
/*TODO*///						case 3:
/*TODO*///							if (player == 0)
/*TODO*///							{
/*TODO*///								in->type &= ~IPF_PLAYER1;
/*TODO*///								in->type |= IPF_PLAYER4;
/*TODO*///								analog_player_port[port] = 3;
/*TODO*///							}
/*TODO*///							else if (player == 3)
/*TODO*///							{
/*TODO*///								in->type &= ~IPF_PLAYER4;
/*TODO*///								in->type |= IPF_PLAYER1;
/*TODO*///								analog_player_port[port] = 0;
/*TODO*///							}
/*TODO*///							break;
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///			in++;
/*TODO*///		}
/*TODO*///		port++;
/*TODO*///		if (in->type == IPT_PORT) in++;
/*TODO*///	}
/*TODO*///#endif /* MAME_NET */
/*TODO*///
/*TODO*///	save_default_keys();
/*TODO*///
/*TODO*///	cfg = config_create(Machine->gamedrv->name);
/*TODO*///	if (cfg)
/*TODO*///		{
/*TODO*///		mixer_save_config(&mixercfg);
/*TODO*///
/*TODO*///		config_write_ports(cfg, Machine->input_ports_default, Machine->input_ports);
/*TODO*///		config_write_coin_and_ticket_counters(cfg, coins, lastcoin, coinlockedout, dispensed_tickets);
/*TODO*///		config_write_mixer_config(cfg, &mixercfg);
/*TODO*///		config_close(cfg);
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////* Note that the following 3 routines have slightly different meanings with analog ports */
/*TODO*///const char *input_port_name(const struct InputPort *in)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	unsigned type;
/*TODO*///
/*TODO*///	if (in->name != IP_NAME_DEFAULT) return in->name;
/*TODO*///
/*TODO*///	i = 0;
/*TODO*///
/*TODO*///	if ((in->type & ~IPF_MASK) == IPT_EXTENSION)
/*TODO*///		type = (in-1)->type & (~IPF_MASK | IPF_PLAYERMASK);
/*TODO*///	else
/*TODO*///		type = in->type & (~IPF_MASK | IPF_PLAYERMASK);
/*TODO*///
/*TODO*///	while (inputport_defaults[i].type != IPT_END &&
/*TODO*///			inputport_defaults[i].type != type)
/*TODO*///		i++;
/*TODO*///
/*TODO*///	if ((in->type & ~IPF_MASK) == IPT_EXTENSION)
/*TODO*///		return inputport_defaults[i+1].name;
/*TODO*///	else
/*TODO*///		return inputport_defaults[i].name;
/*TODO*///}
/*TODO*///
/*TODO*///InputSeq* input_port_type_seq(int type)
/*TODO*///{
/*TODO*///	unsigned i;
/*TODO*///
/*TODO*///	i = 0;
/*TODO*///
/*TODO*///	while (inputport_defaults[i].type != IPT_END &&
/*TODO*///			inputport_defaults[i].type != type)
/*TODO*///		i++;
/*TODO*///
/*TODO*///	return &inputport_defaults[i].seq;
/*TODO*///}
/*TODO*///
/*TODO*///InputSeq* input_port_seq(const struct InputPort *in)
/*TODO*///{
/*TODO*///	int i,type;
/*TODO*///
/*TODO*///	static InputSeq ip_none = SEQ_DEF_1(CODE_NONE);
/*TODO*///
/*TODO*///	while (seq_get_1((InputSeq*)&in->seq) == CODE_PREVIOUS) in--;
/*TODO*///
/*TODO*///	if ((in->type & ~IPF_MASK) == IPT_EXTENSION)
/*TODO*///	{
/*TODO*///		type = (in-1)->type & (~IPF_MASK | IPF_PLAYERMASK);
/*TODO*///		/* if port is disabled, or cheat with cheats disabled, return no key */
/*TODO*///		if (((in-1)->type & IPF_UNUSED) || (!options.cheat && ((in-1)->type & IPF_CHEAT)))
/*TODO*///			return &ip_none;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		type = in->type & (~IPF_MASK | IPF_PLAYERMASK);
/*TODO*///		/* if port is disabled, or cheat with cheats disabled, return no key */
/*TODO*///		if ((in->type & IPF_UNUSED) || (!options.cheat && (in->type & IPF_CHEAT)))
/*TODO*///			return &ip_none;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (seq_get_1((InputSeq*)&in->seq) != CODE_DEFAULT)
/*TODO*///		return (InputSeq*)&in->seq;
/*TODO*///
/*TODO*///	i = 0;
/*TODO*///
/*TODO*///	while (inputport_defaults[i].type != IPT_END &&
/*TODO*///			inputport_defaults[i].type != type)
/*TODO*///		i++;
/*TODO*///
/*TODO*///	if ((in->type & ~IPF_MASK) == IPT_EXTENSION)
/*TODO*///		return &inputport_defaults[i+1].seq;
/*TODO*///	else
/*TODO*///		return &inputport_defaults[i].seq;
/*TODO*///}
/*TODO*///
/*TODO*///void update_analog_port(int port)
/*TODO*///{
/*TODO*///	struct InputPort *in;
/*TODO*///	int current, delta, type, sensitivity, min, max, default_value;
/*TODO*///	int axis, is_stick, is_gun, check_bounds;
/*TODO*///	InputSeq* incseq;
/*TODO*///	InputSeq* decseq;
/*TODO*///	int keydelta;
/*TODO*///	int player;
/*TODO*///
/*TODO*///	/* get input definition */
/*TODO*///	in = input_analog[port];
/*TODO*///
/*TODO*///	/* if we're not cheating and this is a cheat-only port, bail */
/*TODO*///	if (!options.cheat && (in->type & IPF_CHEAT)) return;
/*TODO*///	type=(in->type & ~IPF_MASK);
/*TODO*///
/*TODO*///	decseq = input_port_seq(in);
/*TODO*///	incseq = input_port_seq(in+1);
/*TODO*///
/*TODO*///	keydelta = IP_GET_DELTA(in);
/*TODO*///
/*TODO*///	switch (type)
/*TODO*///	{
/*TODO*///		case IPT_PADDLE:
/*TODO*///			axis = X_AXIS; is_stick = 1; is_gun=0; check_bounds = 1; break;
/*TODO*///		case IPT_PADDLE_V:
/*TODO*///			axis = Y_AXIS; is_stick = 1; is_gun=0; check_bounds = 1; break;
/*TODO*///		case IPT_DIAL:
/*TODO*///			axis = X_AXIS; is_stick = 0; is_gun=0; check_bounds = 0; break;
/*TODO*///		case IPT_DIAL_V:
/*TODO*///			axis = Y_AXIS; is_stick = 0; is_gun=0; check_bounds = 0; break;
/*TODO*///		case IPT_TRACKBALL_X:
/*TODO*///			axis = X_AXIS; is_stick = 0; is_gun=0; check_bounds = 0; break;
/*TODO*///		case IPT_TRACKBALL_Y:
/*TODO*///			axis = Y_AXIS; is_stick = 0; is_gun=0; check_bounds = 0; break;
/*TODO*///		case IPT_AD_STICK_X:
/*TODO*///			axis = X_AXIS; is_stick = 1; is_gun=0; check_bounds = 1; break;
/*TODO*///		case IPT_AD_STICK_Y:
/*TODO*///			axis = Y_AXIS; is_stick = 1; is_gun=0; check_bounds = 1; break;
/*TODO*///		case IPT_AD_STICK_Z:
/*TODO*///			axis = Z_AXIS; is_stick = 1; is_gun=0; check_bounds = 1; break;
/*TODO*///		case IPT_LIGHTGUN_X:
/*TODO*///			axis = X_AXIS; is_stick = 1; is_gun=1; check_bounds = 1; break;
/*TODO*///		case IPT_LIGHTGUN_Y:
/*TODO*///			axis = Y_AXIS; is_stick = 1; is_gun=1; check_bounds = 1; break;
/*TODO*///		case IPT_PEDAL:
/*TODO*///			axis = PEDAL_AXIS; is_stick = 1; is_gun=0; check_bounds = 1; break;
/*TODO*///		case IPT_PEDAL2:
/*TODO*///			axis = Z_AXIS; is_stick = 1; is_gun=0; check_bounds = 1; break;
/*TODO*///		default:
/*TODO*///			/* Use some defaults to prevent crash */
/*TODO*///			axis = X_AXIS; is_stick = 0; is_gun=0; check_bounds = 0;
/*TODO*///			logerror("Oops, polling non analog device in update_analog_port()????\n");
/*TODO*///	}
/*TODO*///
/*TODO*///
/*TODO*///	sensitivity = IP_GET_SENSITIVITY(in);
/*TODO*///	min = IP_GET_MIN(in);
/*TODO*///	max = IP_GET_MAX(in);
/*TODO*///	default_value = in->default_value * 100 / sensitivity;
/*TODO*///	/* extremes can be either signed or unsigned */
/*TODO*///	if (min > max)
/*TODO*///	{
/*TODO*///		if (in->mask > 0xff) min = min - 0x10000;
/*TODO*///		else min = min - 0x100;
/*TODO*///	}
/*TODO*///
/*TODO*///	input_analog_previous_value[port] = input_analog_current_value[port];
/*TODO*///
/*TODO*///	/* if IPF_CENTER go back to the default position */
/*TODO*///	/* sticks are handled later... */
/*TODO*///	if ((in->type & IPF_CENTER) && (!is_stick))
/*TODO*///		input_analog_current_value[port] = in->default_value * 100 / sensitivity;
/*TODO*///
/*TODO*///	current = input_analog_current_value[port];
/*TODO*///
/*TODO*///	delta = 0;
/*TODO*///
/*TODO*///	player = IP_GET_PLAYER(in);
/*TODO*///
/*TODO*///	delta = mouse_delta_axis[player][axis];
/*TODO*///
/*TODO*///	if (seq_pressed(decseq)) delta -= keydelta;
/*TODO*///
/*TODO*///	if (type != IPT_PEDAL && type != IPT_PEDAL2)
/*TODO*///	{
/*TODO*///		if (seq_pressed(incseq)) delta += keydelta;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		/* is this cheesy or what? */
/*TODO*///		if (!delta && seq_get_1(incseq) == KEYCODE_Y) delta += keydelta;
/*TODO*///		delta = -delta;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (in->type & IPF_REVERSE) delta = -delta;
/*TODO*///
/*TODO*///	if (is_gun)
/*TODO*///	{
/*TODO*///		/* The OSD lightgun call should return the delta from the middle of the screen
/*TODO*///		when the gun is fired (not the absolute pixel value), and 0 when the gun is
/*TODO*///		inactive.  We take advantage of this to provide support for other controllers
/*TODO*///		in place of a physical lightgun.  When the OSD lightgun returns 0, then control
/*TODO*///		passes through to the analog joystick, and mouse, in that order.  When the OSD
/*TODO*///		lightgun returns a value it overrides both mouse & analog joystick.
/*TODO*///
/*TODO*///		The value returned by the OSD layer should be -128 to 128, same as analog
/*TODO*///		joysticks.
/*TODO*///
/*TODO*///		There is an ugly hack to stop scaling of lightgun returned values.  It really
/*TODO*///		needs rewritten...
/*TODO*///		*/
/*TODO*///		if (axis == X_AXIS) {
/*TODO*///			if (lightgun_delta_axis[player][X_AXIS] || lightgun_delta_axis[player][Y_AXIS]) {
/*TODO*///				analog_previous_axis[player][X_AXIS]=0;
/*TODO*///				analog_current_axis[player][X_AXIS]=lightgun_delta_axis[player][X_AXIS];
/*TODO*///				input_analog_scale[port]=0;
/*TODO*///				sensitivity=100;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			if (lightgun_delta_axis[player][X_AXIS] || lightgun_delta_axis[player][Y_AXIS]) {
/*TODO*///				analog_previous_axis[player][Y_AXIS]=0;
/*TODO*///				analog_current_axis[player][Y_AXIS]=lightgun_delta_axis[player][Y_AXIS];
/*TODO*///				input_analog_scale[port]=0;
/*TODO*///				sensitivity=100;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	if (is_stick)
/*TODO*///	{
/*TODO*///		int new, prev;
/*TODO*///
/*TODO*///		/* center stick */
/*TODO*///		if ((delta == 0) && (in->type & IPF_CENTER))
/*TODO*///		{
/*TODO*///			if (current > default_value)
/*TODO*///			delta = -100 / sensitivity;
/*TODO*///			if (current < default_value)
/*TODO*///			delta = 100 / sensitivity;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* An analog joystick which is not at zero position (or has just */
/*TODO*///		/* moved there) takes precedence over all other computations */
/*TODO*///		/* analog_x/y holds values from -128 to 128 (yes, 128, not 127) */
/*TODO*///
/*TODO*///		new  = analog_current_axis[player][axis];
/*TODO*///		prev = analog_previous_axis[player][axis];
/*TODO*///
/*TODO*///		if ((new != 0) || (new-prev != 0))
/*TODO*///		{
/*TODO*///			delta=0;
/*TODO*///
/*TODO*///			/* for pedals, need to change to possitive number */
/*TODO*///			/* and, if needed, reverse pedal input */
/*TODO*///			if (type == IPT_PEDAL || type == IPT_PEDAL2)
/*TODO*///			{
/*TODO*///				new  = -new;
/*TODO*///				prev = -prev;
/*TODO*///				if (in->type & IPF_REVERSE)		// a reversed pedal is diff than normal reverse
/*TODO*///				{								// 128 = no gas, 0 = all gas
/*TODO*///					new  = 128-new;				// the default "new=-new" doesn't handle this
/*TODO*///					prev = 128-prev;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			else if (in->type & IPF_REVERSE)
/*TODO*///			{
/*TODO*///				new  = -new;
/*TODO*///				prev = -prev;
/*TODO*///			}
/*TODO*///
/*TODO*///			/* apply sensitivity using a logarithmic scale */
/*TODO*///			if (in->mask > 0xff)
/*TODO*///			{
/*TODO*///				if (new > 0)
/*TODO*///				{
/*TODO*///					current = (pow(new / 32768.0, 100.0 / sensitivity) * (max-in->default_value)
/*TODO*///							+ in->default_value) * 100 / sensitivity;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					current = (pow(-new / 32768.0, 100.0 / sensitivity) * (min-in->default_value)
/*TODO*///							+ in->default_value) * 100 / sensitivity;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				if (new > 0)
/*TODO*///				{
/*TODO*///					current = (pow(new / 128.0, 100.0 / sensitivity) * (max-in->default_value)
/*TODO*///							+ in->default_value) * 100 / sensitivity;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					current = (pow(-new / 128.0, 100.0 / sensitivity) * (min-in->default_value)
/*TODO*///							+ in->default_value) * 100 / sensitivity;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	current += delta;
/*TODO*///
/*TODO*///	if (check_bounds)
/*TODO*///	{
/*TODO*///		int temp;
/*TODO*///
/*TODO*///		if (current >= 0)
/*TODO*///			temp = (current * sensitivity + 50) / 100;
/*TODO*///		else
/*TODO*///			temp = (-current * sensitivity + 50) / -100;
/*TODO*///
/*TODO*///		if (temp < min)
/*TODO*///		{
/*TODO*///			if (min >= 0)
/*TODO*///				current = (min * 100 + sensitivity/2) / sensitivity;
/*TODO*///			else
/*TODO*///				current = (-min * 100 + sensitivity/2) / -sensitivity;
/*TODO*///		}
/*TODO*///		if (temp > max)
/*TODO*///		{
/*TODO*///			if (max >= 0)
/*TODO*///				current = (max * 100 + sensitivity/2) / sensitivity;
/*TODO*///			else
/*TODO*///				current = (-max * 100 + sensitivity/2) / -sensitivity;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	input_analog_current_value[port] = current;
/*TODO*///}
/*TODO*///
/*TODO*///static void scale_analog_port(int port)
/*TODO*///{
/*TODO*///	struct InputPort *in;
/*TODO*///	int delta,current,sensitivity;
/*TODO*///
/*TODO*///profiler_mark(PROFILER_INPUT);
/*TODO*///	in = input_analog[port];
/*TODO*///	sensitivity = IP_GET_SENSITIVITY(in);
/*TODO*///
/*TODO*///	/* apply scaling fairly in both positive and negative directions */
/*TODO*///	delta = input_analog_current_value[port] - input_analog_previous_value[port];
/*TODO*///	if (delta >= 0)
/*TODO*///		delta = cpu_scalebyfcount(delta);
/*TODO*///	else
/*TODO*///		delta = -cpu_scalebyfcount(-delta);
/*TODO*///
/*TODO*///	current = input_analog_previous_value[port] + delta;
/*TODO*///
/*TODO*///	/* An ugly hack to remove scaling on lightgun ports */
/*TODO*///	if (input_analog_scale[port]) {
/*TODO*///		/* apply scaling fairly in both positive and negative directions */
/*TODO*///		if (current >= 0)
/*TODO*///			current = (current * sensitivity + 50) / 100;
/*TODO*///		else
/*TODO*///			current = (-current * sensitivity + 50) / -100;
/*TODO*///	}
/*TODO*///
/*TODO*///	input_port_value[port] &= ~in->mask;
/*TODO*///	input_port_value[port] |= current & in->mask;
/*TODO*///
/*TODO*///	if (playback)
/*TODO*///		readword(playback,&input_port_value[port]);
/*TODO*///	if (record)
/*TODO*///		writeword(record,input_port_value[port]);
/*TODO*///#ifdef MAME_NET
/*TODO*///	if ( net_active() && (default_player != NET_SPECTATOR) )
/*TODO*///		net_analog_sync((unsigned char *) input_port_value, port, analog_player_port, default_player);
/*TODO*///#endif /* MAME_NET */
/*TODO*///profiler_mark(PROFILER_END);
/*TODO*///}
/*TODO*///
/*TODO*///#define MAX_JOYSTICKS 3
/*TODO*///#define MAX_PLAYERS 8
/*TODO*///static int mJoyCurrent[MAX_JOYSTICKS*MAX_PLAYERS];
/*TODO*///static int mJoyPrevious[MAX_JOYSTICKS*MAX_PLAYERS];
/*TODO*///static int mJoy4Way[MAX_JOYSTICKS*MAX_PLAYERS];
/*TODO*////*
/*TODO*///The above "Joy" states contain packed bits:
/*TODO*///	0001	up
/*TODO*///	0010	down
/*TODO*///	0100	left
/*TODO*///	1000	right
/*TODO*///*/
/*TODO*///
/*TODO*///static void
/*TODO*///ScanJoysticks( struct InputPort *in )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	int port = 0;
/*TODO*///
/*TODO*///	/* Save old Joystick state. */
/*TODO*///	memcpy( mJoyPrevious, mJoyCurrent, sizeof(mJoyPrevious) );
/*TODO*///
/*TODO*///	/* Initialize bits of mJoyCurrent to zero. */
/*TODO*///	memset( mJoyCurrent, 0, sizeof(mJoyCurrent) );
/*TODO*///
/*TODO*///	/* Now iterate over the input port structure to populate mJoyCurrent. */
/*TODO*///	while( in->type != IPT_END && port < MAX_INPUT_PORTS )
/*TODO*///	{
/*TODO*///		while (in->type != IPT_END && in->type != IPT_PORT)
/*TODO*///		{
/*TODO*///			if ((in->type & ~IPF_MASK) >= IPT_JOYSTICK_UP &&
/*TODO*///				(in->type & ~IPF_MASK) <= IPT_JOYSTICKLEFT_RIGHT)
/*TODO*///			{
/*TODO*///				InputSeq* seq;
/*TODO*///				seq = input_port_seq(in);
/*TODO*///				if( seq_pressed(seq) )
/*TODO*///				{
/*TODO*///					int joynum,joydir,player;
/*TODO*///					player = IP_GET_PLAYER(in);
/*TODO*///
/*TODO*///					joynum = player * MAX_JOYSTICKS +
/*TODO*///							 ((in->type & ~IPF_MASK) - IPT_JOYSTICK_UP) / 4;
/*TODO*///					joydir = ((in->type & ~IPF_MASK) - IPT_JOYSTICK_UP) % 4;
/*TODO*///
/*TODO*///					mJoyCurrent[joynum] |= 1<<joydir;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			in++;
/*TODO*///		}
/*TODO*///		port++;
/*TODO*///		if (in->type == IPT_PORT) in++;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* Process the joystick states, to filter out illegal combinations of switches. */
/*TODO*///	for( i=0; i<MAX_JOYSTICKS*MAX_PLAYERS; i++ )
/*TODO*///	{
/*TODO*///		if( (mJoyCurrent[i]&0x3)==0x3 ) /* both up and down are pressed */
/*TODO*///		{
/*TODO*///			mJoyCurrent[i]&=0xc; /* clear up and down */
/*TODO*///		}
/*TODO*///		if( (mJoyCurrent[i]&0xc)==0xc ) /* both left and right are pressed */
/*TODO*///		{
/*TODO*///			mJoyCurrent[i]&=0x3; /* clear left and right */
/*TODO*///		}
/*TODO*///
/*TODO*///		/* Only update mJoy4Way if the joystick has moved. */
/*TODO*///		if( mJoyCurrent[i]!=mJoyPrevious[i] )
/*TODO*///		{
/*TODO*///			mJoy4Way[i] = mJoyCurrent[i];
/*TODO*///
/*TODO*///			if( (mJoy4Way[i] & 0x3) && (mJoy4Way[i] & 0xc) )
/*TODO*///			{
/*TODO*///				/* If joystick is pointing at a diagonal, acknowledge that the player moved
/*TODO*///				 * the joystick by favoring a direction change.  This minimizes frustration
/*TODO*///				 * when using a keyboard for input, and maximizes responsiveness.
/*TODO*///				 *
/*TODO*///				 * For example, if you are holding "left" then switch to "up" (where both left
/*TODO*///				 * and up are briefly pressed at the same time), we'll transition immediately
/*TODO*///				 * to "up."
/*TODO*///				 *
/*TODO*///				 * Under the old "sticky" key implentation, "up" wouldn't be triggered until
/*TODO*///				 * left was released.
/*TODO*///				 *
/*TODO*///				 * Zero any switches that didn't change from the previous to current state.
/*TODO*///				 */
/*TODO*///				mJoy4Way[i] ^= (mJoy4Way[i] & mJoyPrevious[i]);
/*TODO*///			}
/*TODO*///
/*TODO*///			if( (mJoy4Way[i] & 0x3) && (mJoy4Way[i] & 0xc) )
/*TODO*///			{
/*TODO*///				/* If we are still pointing at a diagonal, we are in an indeterminant state.
/*TODO*///				 *
/*TODO*///				 * This could happen if the player moved the joystick from the idle position directly
/*TODO*///				 * to a diagonal, or from one diagonal directly to an extreme diagonal.
/*TODO*///				 *
/*TODO*///				 * The chances of this happening with a keyboard are slim, but we still need to
/*TODO*///				 * constrain this case.
/*TODO*///				 *
/*TODO*///				 * For now, just resolve randomly.
/*TODO*///				 */
/*TODO*///				if( rand()&1 )
/*TODO*///				{
/*TODO*///					mJoy4Way[i] &= 0x3; /* eliminate horizontal component */
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					mJoy4Way[i] &= 0xc; /* eliminate vertical component */
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///} /* ScanJoysticks */
/*TODO*///
/*TODO*///void update_input_ports(void)
/*TODO*///{
/*TODO*///	int port,ib;
/*TODO*///	struct InputPort *in;
/*TODO*///
/*TODO*///#define MAX_INPUT_BITS 1024
/*TODO*///	static int impulsecount[MAX_INPUT_BITS];
/*TODO*///	static int waspressed[MAX_INPUT_BITS];
/*TODO*///	static int pbwaspressed[MAX_INPUT_BITS];
/*TODO*///
/*TODO*///#ifdef MAME_NET
/*TODO*///	int player;
/*TODO*///#endif /* MAME_NET */
/*TODO*///
/*TODO*///
/*TODO*///profiler_mark(PROFILER_INPUT);
/*TODO*///
/*TODO*///	/* clear all the values before proceeding */
/*TODO*///	for (port = 0;port < MAX_INPUT_PORTS;port++)
/*TODO*///	{
/*TODO*///		input_port_value[port] = 0;
/*TODO*///		input_vblank[port] = 0;
/*TODO*///		input_analog[port] = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	in = Machine->input_ports;
/*TODO*///	if (in->type == IPT_END) return; 	/* nothing to do */
/*TODO*///
/*TODO*///	/* make sure the InputPort definition is correct */
/*TODO*///	if (in->type != IPT_PORT)
/*TODO*///	{
/*TODO*///		logerror("Error in InputPort definition: expecting PORT_START\n");
/*TODO*///		return;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		in++;
/*TODO*///	}
/*TODO*///
/*TODO*///	ScanJoysticks( in ); /* populates mJoyCurrent[] */
/*TODO*///
/*TODO*///	/* scan all the input ports */
/*TODO*///	port = 0;
/*TODO*///	ib = 0;
/*TODO*///	while (in->type != IPT_END && port < MAX_INPUT_PORTS)
/*TODO*///	{
/*TODO*///		struct InputPort *start;
/*TODO*///		/* first of all, scan the whole input port definition and build the */
/*TODO*///		/* default value. I must do it before checking for input because otherwise */
/*TODO*///		/* multiple keys associated with the same input bit wouldn't work (the bit */
/*TODO*///		/* would be reset to its default value by the second entry, regardless if */
/*TODO*///		/* the key associated with the first entry was pressed) */
/*TODO*///		start = in;
/*TODO*///		while (in->type != IPT_END && in->type != IPT_PORT)
/*TODO*///		{
/*TODO*///			if ((in->type & ~IPF_MASK) != IPT_DIPSWITCH_SETTING &&	/* skip dipswitch definitions */
/*TODO*///#ifdef MESS
/*TODO*///				(in->type & ~IPF_MASK) != IPT_CONFIG_SETTING &&		/* skip config definitions */
/*TODO*///#endif /* MESS */
/*TODO*///				(in->type & ~IPF_MASK) != IPT_EXTENSION)			/* skip analog extension fields */
/*TODO*///			{
/*TODO*///				input_port_value[port] =
/*TODO*///						(input_port_value[port] & ~in->mask) | (in->default_value & in->mask);
/*TODO*///#ifdef MAME_NET
/*TODO*///				if ( net_active() )
/*TODO*///					input_port_defaults[port] = input_port_value[port];
/*TODO*///#elif defined XMAME_NET
/*TODO*///				if ( osd_net_active() )
/*TODO*///					input_port_defaults[port] = input_port_value[port];
/*TODO*///#endif /* MAME_NET */
/*TODO*///			}
/*TODO*///
/*TODO*///			in++;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* now get back to the beginning of the input port and check the input bits. */
/*TODO*///		for (in = start;
/*TODO*///			 in->type != IPT_END && in->type != IPT_PORT;
/*TODO*///			 in++, ib++)
/*TODO*///		{
/*TODO*///#ifdef MAME_NET
/*TODO*///			player = IP_GET_PLAYER(in);
/*TODO*///#endif /* MAME_NET */
/*TODO*///			if ((in->type & ~IPF_MASK) != IPT_DIPSWITCH_SETTING &&	/* skip dipswitch definitions */
/*TODO*///#ifdef MESS
/*TODO*///				(in->type & ~IPF_MASK) != IPT_CONFIG_SETTING &&		/* skip config definitions */
/*TODO*///#endif
/*TODO*///					(in->type & ~IPF_MASK) != IPT_EXTENSION)		/* skip analog extension fields */
/*TODO*///			{
/*TODO*///				if ((in->type & ~IPF_MASK) == IPT_VBLANK)
/*TODO*///				{
/*TODO*///					input_vblank[port] ^= in->mask;
/*TODO*///					input_port_value[port] ^= in->mask;
/*TODO*///if (Machine->drv->vblank_duration == 0)
/*TODO*///	logerror("Warning: you are using IPT_VBLANK with vblank_duration = 0. You need to increase vblank_duration for IPT_VBLANK to work.\n");
/*TODO*///				}
/*TODO*///				/* If it's an analog control, handle it appropriately */
/*TODO*///				else if (((in->type & ~IPF_MASK) > IPT_ANALOG_START)
/*TODO*///					  && ((in->type & ~IPF_MASK) < IPT_ANALOG_END  )) /* LBO 120897 */
/*TODO*///				{
/*TODO*///					input_analog[port]=in;
/*TODO*///					/* reset the analog port on first access */
/*TODO*///					if (input_analog_init[port])
/*TODO*///					{
/*TODO*///						input_analog_init[port] = 0;
/*TODO*///						input_analog_scale[port] = 1;
/*TODO*///						input_analog_current_value[port] = input_analog_previous_value[port]
/*TODO*///							= in->default_value * 100 / IP_GET_SENSITIVITY(in);
/*TODO*///					}
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					InputSeq* seq;
/*TODO*///					seq = input_port_seq(in);
/*TODO*///					if (seq_pressed(seq))
/*TODO*///					{
/*TODO*///#ifdef MESS
/*TODO*///						if (((in->type & ~IPF_MASK) == IPT_KEYBOARD) && osd_keyboard_disabled())
/*TODO*///							continue;
/*TODO*///#endif
/*TODO*///
/*TODO*///						/* skip if coin input and it's locked out */
/*TODO*///						if ((in->type & ~IPF_MASK) >= IPT_COIN1 &&
/*TODO*///							(in->type & ~IPF_MASK) <= IPT_COIN4 &&
/*TODO*///                            coinlockedout[(in->type & ~IPF_MASK) - IPT_COIN1])
/*TODO*///						{
/*TODO*///							continue;
/*TODO*///						}
/*TODO*///						if ((in->type & ~IPF_MASK) >= IPT_COIN5 &&
/*TODO*///							(in->type & ~IPF_MASK) <= IPT_COIN8 &&
/*TODO*///                            coinlockedout[(in->type & ~IPF_MASK) - IPT_COIN5 + 4])
/*TODO*///						{
/*TODO*///							continue;
/*TODO*///						}
/*TODO*///
/*TODO*///						/* if IPF_RESET set, reset the first CPU */
/*TODO*///						if ((in->type & IPF_RESETCPU) && waspressed[ib] == 0 && !playback)
/*TODO*///						{
/*TODO*///							cpu_set_reset_line(0,PULSE_LINE);
/*TODO*///						}
/*TODO*///
/*TODO*///						if (in->type & IPF_IMPULSE)
/*TODO*///						{
/*TODO*///if (IP_GET_IMPULSE(in) == 0)
/*TODO*///	logerror("error in input port definition: IPF_IMPULSE with length = 0\n");
/*TODO*///							if (waspressed[ib] == 0)
/*TODO*///								impulsecount[ib] = IP_GET_IMPULSE(in);
/*TODO*///								/* the input bit will be toggled later */
/*TODO*///						}
/*TODO*///						else if (in->type & IPF_TOGGLE)
/*TODO*///						{
/*TODO*///							if (waspressed[ib] == 0)
/*TODO*///							{
/*TODO*///								in->default_value ^= in->mask;
/*TODO*///								input_port_value[port] ^= in->mask;
/*TODO*///							}
/*TODO*///						}
/*TODO*///						else if ((in->type & ~IPF_MASK) >= IPT_JOYSTICK_UP &&
/*TODO*///								(in->type & ~IPF_MASK) <= IPT_JOYSTICKLEFT_RIGHT)
/*TODO*///						{
/*TODO*///#ifndef MAME_NET
/*TODO*///							int joynum,joydir,mask,player;
/*TODO*///
/*TODO*///							player = IP_GET_PLAYER(in);
/*TODO*///#else
/*TODO*///							int joynum,joydir,mask;
/*TODO*///#endif /* !MAME_NET */
/*TODO*///							joynum = player * MAX_JOYSTICKS +
/*TODO*///									((in->type & ~IPF_MASK) - IPT_JOYSTICK_UP) / 4;
/*TODO*///
/*TODO*///							joydir = ((in->type & ~IPF_MASK) - IPT_JOYSTICK_UP) % 4;
/*TODO*///
/*TODO*///							mask = in->mask;
/*TODO*///
/*TODO*///							if( in->type & IPF_4WAY )
/*TODO*///							{
/*TODO*///								/* apply 4-way joystick constraint */
/*TODO*///								if( ((mJoy4Way[joynum]>>joydir)&1) == 0 )
/*TODO*///								{
/*TODO*///									mask = 0;
/*TODO*///								}
/*TODO*///							}
/*TODO*///							else
/*TODO*///							{
/*TODO*///								/* filter up+down and left+right */
/*TODO*///								if( ((mJoyCurrent[joynum]>>joydir)&1) == 0 )
/*TODO*///								{
/*TODO*///									mask = 0;
/*TODO*///								}
/*TODO*///							}
/*TODO*///
/*TODO*///							input_port_value[port] ^= mask;
/*TODO*///						} /* joystick */
/*TODO*///						else
/*TODO*///						{
/*TODO*///							input_port_value[port] ^= in->mask;
/*TODO*///						}
/*TODO*///						waspressed[ib] = 1;
/*TODO*///					}
/*TODO*///					else
/*TODO*///						waspressed[ib] = 0;
/*TODO*///
/*TODO*///					if ((in->type & IPF_IMPULSE) && impulsecount[ib] > 0)
/*TODO*///					{
/*TODO*///						impulsecount[ib]--;
/*TODO*///						waspressed[ib] = 1;
/*TODO*///						input_port_value[port] ^= in->mask;
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		port++;
/*TODO*///		if (in->type == IPT_PORT) in++;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (playback)
/*TODO*///	{
/*TODO*///		int i;
/*TODO*///
/*TODO*///		ib=0;
/*TODO*///		in = Machine->input_ports;
/*TODO*///		in++;
/*TODO*///		for (i = 0; i < MAX_INPUT_PORTS; i ++)
/*TODO*///		{
/*TODO*///			readword(playback,&input_port_value[i]);
/*TODO*///
/*TODO*///			/* check if the input port includes an IPF_RESETCPU bit
/*TODO*///			   and reset the CPU on first "press", no need to check
/*TODO*///			   the impulse count as this was done during recording */
/*TODO*///			for (; in->type != IPT_END && in->type != IPT_PORT; in++, ib++)
/*TODO*///			{
/*TODO*///				if (in->type & IPF_RESETCPU)
/*TODO*///				{
/*TODO*///					if((input_port_value[i] ^ in->default_value) & in->mask)
/*TODO*///					{
/*TODO*///						if (pbwaspressed[ib] == 0)
/*TODO*///							cpu_set_reset_line(0,PULSE_LINE);
/*TODO*///						pbwaspressed[ib] = 1;
/*TODO*///					}
/*TODO*///					else
/*TODO*///						pbwaspressed[ib] = 0;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			if (in->type == IPT_PORT) in++;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///	inputx_update(input_port_value);
/*TODO*///#endif
/*TODO*///
/*TODO*///	if (record)
/*TODO*///	{
/*TODO*///		int i;
/*TODO*///
/*TODO*///		for (i = 0; i < MAX_INPUT_PORTS; i ++)
/*TODO*///			writeword(record,input_port_value[i]);
/*TODO*///	}
/*TODO*///#ifdef MAME_NET
/*TODO*///	if ( net_active() && (default_player != NET_SPECTATOR) )
/*TODO*///		net_input_sync((unsigned char *) input_port_value, (unsigned char *) input_port_defaults, MAX_INPUT_PORTS);
/*TODO*///#elif defined XMAME_NET
/*TODO*///	if ( osd_net_active() )
/*TODO*///		osd_net_sync(input_port_value, input_port_defaults);
/*TODO*///#endif /* MAME_NET */
/*TODO*///
/*TODO*///profiler_mark(PROFILER_END);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////* used the the CPU interface to notify that VBlank has ended, so we can update */
/*TODO*////* IPT_VBLANK input ports. */
/*TODO*///void inputport_vblank_end(void)
/*TODO*///{
/*TODO*///	int port;
/*TODO*///	int i;
/*TODO*///
/*TODO*///
/*TODO*///profiler_mark(PROFILER_INPUT);
/*TODO*///	for (port = 0;port < MAX_INPUT_PORTS;port++)
/*TODO*///	{
/*TODO*///		if (input_vblank[port])
/*TODO*///		{
/*TODO*///			input_port_value[port] ^= input_vblank[port];
/*TODO*///			input_vblank[port] = 0;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* update the analog devices */
/*TODO*///	for (i = 0;i < OSD_MAX_JOY_ANALOG;i++)
/*TODO*///	{
/*TODO*///		/* update the analog joystick position */
/*TODO*///		int a;
/*TODO*///		for (a=0; a<MAX_ANALOG_AXES ; a++)
/*TODO*///		{
/*TODO*///			analog_previous_axis[i][a] = analog_current_axis[i][a];
/*TODO*///		}
/*TODO*///		osd_analogjoy_read (i, analog_current_axis[i], analogjoy_input[i]);
/*TODO*///
/*TODO*///		/* update mouse/trackball position */
/*TODO*///		osd_trak_read (i, &(mouse_delta_axis[i])[X_AXIS], &(mouse_delta_axis[i])[Y_AXIS]);
/*TODO*///
/*TODO*///		/* update lightgun position, if any */
/*TODO*/// 		osd_lightgun_read (i, &(lightgun_delta_axis[i])[X_AXIS], &(lightgun_delta_axis[i])[Y_AXIS]);
/*TODO*///	}
/*TODO*///
/*TODO*///	for (i = 0;i < MAX_INPUT_PORTS;i++)
/*TODO*///	{
/*TODO*///		struct InputPort *in;
/*TODO*///
/*TODO*///		in=input_analog[i];
/*TODO*///		if (in)
/*TODO*///		{
/*TODO*///			update_analog_port(i);
/*TODO*///		}
/*TODO*///	}
/*TODO*///profiler_mark(PROFILER_END);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///int readinputport(int port)
/*TODO*///{
/*TODO*///	struct InputPort *in;
/*TODO*///
/*TODO*///	/* Update analog ports on demand */
/*TODO*///	in=input_analog[port];
/*TODO*///	if (in)
/*TODO*///	{
/*TODO*///		scale_analog_port(port);
/*TODO*///	}
/*TODO*///
/*TODO*///	return input_port_value[port];
/*TODO*///}
/*TODO*///
/*TODO*///READ_HANDLER( input_port_0_r ) { return readinputport(0); }
/*TODO*///READ_HANDLER( input_port_1_r ) { return readinputport(1); }
/*TODO*///READ_HANDLER( input_port_2_r ) { return readinputport(2); }
/*TODO*///READ_HANDLER( input_port_3_r ) { return readinputport(3); }
/*TODO*///READ_HANDLER( input_port_4_r ) { return readinputport(4); }
/*TODO*///READ_HANDLER( input_port_5_r ) { return readinputport(5); }
/*TODO*///READ_HANDLER( input_port_6_r ) { return readinputport(6); }
/*TODO*///READ_HANDLER( input_port_7_r ) { return readinputport(7); }
/*TODO*///READ_HANDLER( input_port_8_r ) { return readinputport(8); }
/*TODO*///READ_HANDLER( input_port_9_r ) { return readinputport(9); }
/*TODO*///READ_HANDLER( input_port_10_r ) { return readinputport(10); }
/*TODO*///READ_HANDLER( input_port_11_r ) { return readinputport(11); }
/*TODO*///READ_HANDLER( input_port_12_r ) { return readinputport(12); }
/*TODO*///READ_HANDLER( input_port_13_r ) { return readinputport(13); }
/*TODO*///READ_HANDLER( input_port_14_r ) { return readinputport(14); }
/*TODO*///READ_HANDLER( input_port_15_r ) { return readinputport(15); }
/*TODO*///READ_HANDLER( input_port_16_r ) { return readinputport(16); }
/*TODO*///READ_HANDLER( input_port_17_r ) { return readinputport(17); }
/*TODO*///READ_HANDLER( input_port_18_r ) { return readinputport(18); }
/*TODO*///READ_HANDLER( input_port_19_r ) { return readinputport(19); }
/*TODO*///READ_HANDLER( input_port_20_r ) { return readinputport(20); }
/*TODO*///READ_HANDLER( input_port_21_r ) { return readinputport(21); }
/*TODO*///READ_HANDLER( input_port_22_r ) { return readinputport(22); }
/*TODO*///READ_HANDLER( input_port_23_r ) { return readinputport(23); }
/*TODO*///READ_HANDLER( input_port_24_r ) { return readinputport(24); }
/*TODO*///READ_HANDLER( input_port_25_r ) { return readinputport(25); }
/*TODO*///READ_HANDLER( input_port_26_r ) { return readinputport(26); }
/*TODO*///READ_HANDLER( input_port_27_r ) { return readinputport(27); }
/*TODO*///READ_HANDLER( input_port_28_r ) { return readinputport(28); }
/*TODO*///READ_HANDLER( input_port_29_r ) { return readinputport(29); }
/*TODO*///
/*TODO*///READ16_HANDLER( input_port_0_word_r ) { return readinputport(0); }
/*TODO*///READ16_HANDLER( input_port_1_word_r ) { return readinputport(1); }
/*TODO*///READ16_HANDLER( input_port_2_word_r ) { return readinputport(2); }
/*TODO*///READ16_HANDLER( input_port_3_word_r ) { return readinputport(3); }
/*TODO*///READ16_HANDLER( input_port_4_word_r ) { return readinputport(4); }
/*TODO*///READ16_HANDLER( input_port_5_word_r ) { return readinputport(5); }
/*TODO*///READ16_HANDLER( input_port_6_word_r ) { return readinputport(6); }
/*TODO*///READ16_HANDLER( input_port_7_word_r ) { return readinputport(7); }
/*TODO*///READ16_HANDLER( input_port_8_word_r ) { return readinputport(8); }
/*TODO*///READ16_HANDLER( input_port_9_word_r ) { return readinputport(9); }
/*TODO*///READ16_HANDLER( input_port_10_word_r ) { return readinputport(10); }
/*TODO*///READ16_HANDLER( input_port_11_word_r ) { return readinputport(11); }
/*TODO*///READ16_HANDLER( input_port_12_word_r ) { return readinputport(12); }
/*TODO*///READ16_HANDLER( input_port_13_word_r ) { return readinputport(13); }
/*TODO*///READ16_HANDLER( input_port_14_word_r ) { return readinputport(14); }
/*TODO*///READ16_HANDLER( input_port_15_word_r ) { return readinputport(15); }
/*TODO*///READ16_HANDLER( input_port_16_word_r ) { return readinputport(16); }
/*TODO*///READ16_HANDLER( input_port_17_word_r ) { return readinputport(17); }
/*TODO*///READ16_HANDLER( input_port_18_word_r ) { return readinputport(18); }
/*TODO*///READ16_HANDLER( input_port_19_word_r ) { return readinputport(19); }
/*TODO*///READ16_HANDLER( input_port_20_word_r ) { return readinputport(20); }
/*TODO*///READ16_HANDLER( input_port_21_word_r ) { return readinputport(21); }
/*TODO*///READ16_HANDLER( input_port_22_word_r ) { return readinputport(22); }
/*TODO*///READ16_HANDLER( input_port_23_word_r ) { return readinputport(23); }
/*TODO*///READ16_HANDLER( input_port_24_word_r ) { return readinputport(24); }
/*TODO*///READ16_HANDLER( input_port_25_word_r ) { return readinputport(25); }
/*TODO*///READ16_HANDLER( input_port_26_word_r ) { return readinputport(26); }
/*TODO*///READ16_HANDLER( input_port_27_word_r ) { return readinputport(27); }
/*TODO*///READ16_HANDLER( input_port_28_word_r ) { return readinputport(28); }
/*TODO*///READ16_HANDLER( input_port_29_word_r ) { return readinputport(29); }
/*TODO*///
/*TODO*///#ifdef MAME_NET
/*TODO*///void set_default_player_controls(int player)
/*TODO*///{
/*TODO*///	if (player == NET_SPECTATOR)
/*TODO*///		default_player = NET_SPECTATOR;
/*TODO*///	else
/*TODO*///		default_player = player - 1;
/*TODO*///}
/*TODO*///#endif /* MAME_NET */
/*TODO*///
/*TODO*////***************************************************************************/
/*TODO*////* InputPort conversion */
/*TODO*///
/*TODO*///static unsigned input_port_count(const struct InputPortTiny *src)
/*TODO*///{
/*TODO*///	unsigned total;
/*TODO*///
/*TODO*///	total = 0;
/*TODO*///	while (src->type != IPT_END)
/*TODO*///	{
/*TODO*///		int type = src->type & ~IPF_MASK;
/*TODO*///		if (type > IPT_ANALOG_START && type < IPT_ANALOG_END)
/*TODO*///			total += 2;
/*TODO*///		else if (type != IPT_EXTENSION)
/*TODO*///			++total;
/*TODO*///		++src;
/*TODO*///	}
/*TODO*///
/*TODO*///	++total; /* for IPT_END */
/*TODO*///
/*TODO*///	return total;
/*TODO*///}
/*TODO*///
/*TODO*///struct InputPort* input_port_allocate(const struct InputPortTiny *src)
/*TODO*///{
/*TODO*///	struct InputPort* dst;
/*TODO*///	struct InputPort* base;
/*TODO*///	unsigned total;
/*TODO*///
/*TODO*///	total = input_port_count(src);
/*TODO*///
/*TODO*///	base = (struct InputPort*)malloc(total * sizeof(struct InputPort));
/*TODO*///	dst = base;
/*TODO*///
/*TODO*///	while (src->type != IPT_END)
/*TODO*///	{
/*TODO*///		int type = src->type & ~IPF_MASK;
/*TODO*///		const struct InputPortTiny *ext;
/*TODO*///		const struct InputPortTiny *src_end;
/*TODO*///		InputCode seq_default;
/*TODO*///
/*TODO*///		if (type > IPT_ANALOG_START && type < IPT_ANALOG_END)
/*TODO*///			src_end = src + 2;
/*TODO*///		else
/*TODO*///			src_end = src + 1;
/*TODO*///
/*TODO*///		switch (type)
/*TODO*///		{
/*TODO*///			case IPT_END :
/*TODO*///			case IPT_PORT :
/*TODO*///			case IPT_DIPSWITCH_NAME :
/*TODO*///			case IPT_DIPSWITCH_SETTING :
/*TODO*///#ifdef MESS
/*TODO*///			case IPT_CONFIG_NAME :
/*TODO*///			case IPT_CONFIG_SETTING :
/*TODO*///#endif
/*TODO*///				seq_default = CODE_NONE;
/*TODO*///			break;
/*TODO*///			default:
/*TODO*///				seq_default = CODE_DEFAULT;
/*TODO*///				break;
/*TODO*///		}
/*TODO*///
/*TODO*///		ext = src_end;
/*TODO*///		while (src != src_end)
/*TODO*///		{
/*TODO*///			dst->type = src->type;
/*TODO*///			dst->mask = src->mask;
/*TODO*///			dst->default_value = src->default_value;
/*TODO*///			dst->name = src->name;
/*TODO*///
/*TODO*///  			if (ext->type == IPT_EXTENSION)
/*TODO*///  			{
/*TODO*///				InputCode or1 =	IP_GET_CODE_OR1(ext);
/*TODO*///				InputCode or2 =	IP_GET_CODE_OR2(ext);
/*TODO*///				InputCode or3;
/*TODO*///
/*TODO*///				switch(or2)
/*TODO*///				{
/*TODO*///					case JOYCODE_1_BUTTON1:		or3 = JOYCODE_MOUSE_1_BUTTON1;	break;
/*TODO*///					case JOYCODE_1_BUTTON2:		or3 = JOYCODE_MOUSE_1_BUTTON2;	break;
/*TODO*///					case JOYCODE_1_BUTTON3:		or3 = JOYCODE_MOUSE_1_BUTTON3;	break;
/*TODO*///					case JOYCODE_2_BUTTON1:		or3 = JOYCODE_MOUSE_2_BUTTON1;	break;
/*TODO*///					case JOYCODE_2_BUTTON2:		or3 = JOYCODE_MOUSE_2_BUTTON2;	break;
/*TODO*///					case JOYCODE_2_BUTTON3:		or3 = JOYCODE_MOUSE_2_BUTTON3;	break;
/*TODO*///					case JOYCODE_3_BUTTON1:		or3 = JOYCODE_MOUSE_3_BUTTON1;	break;
/*TODO*///					case JOYCODE_3_BUTTON2:		or3 = JOYCODE_MOUSE_3_BUTTON2;	break;
/*TODO*///					case JOYCODE_3_BUTTON3:		or3 = JOYCODE_MOUSE_3_BUTTON3;	break;
/*TODO*///					case JOYCODE_4_BUTTON1:		or3 = JOYCODE_MOUSE_4_BUTTON1;	break;
/*TODO*///					case JOYCODE_4_BUTTON2:		or3 = JOYCODE_MOUSE_4_BUTTON2;	break;
/*TODO*///					case JOYCODE_4_BUTTON3:		or3 = JOYCODE_MOUSE_4_BUTTON3;	break;
/*TODO*///					default:					or3 = CODE_NONE;				break;
/*TODO*///				}
/*TODO*///
/*TODO*///				if (or1 < __code_max)
/*TODO*///				{
/*TODO*///					if (or3 < __code_max)
/*TODO*///						seq_set_5(&dst->seq, or1, CODE_OR, or2, CODE_OR, or3);
/*TODO*///					else if (or2 < __code_max)
/*TODO*///						seq_set_3(&dst->seq, or1, CODE_OR, or2);
/*TODO*///					else
/*TODO*///						seq_set_1(&dst->seq, or1);
/*TODO*///				} else {
/*TODO*///					if (or1 == CODE_NONE)
/*TODO*///						seq_set_1(&dst->seq, or2);
/*TODO*///					else
/*TODO*///						seq_set_1(&dst->seq, or1);
/*TODO*///				}
/*TODO*///
/*TODO*///  				++ext;
/*TODO*///  			} else {
/*TODO*///				seq_set_1(&dst->seq,seq_default);
/*TODO*///  			}
/*TODO*///
/*TODO*///			++src;
/*TODO*///			++dst;
/*TODO*///		}
/*TODO*///
/*TODO*///		src = ext;
/*TODO*///	}
/*TODO*///
/*TODO*///	dst->type = IPT_END;
/*TODO*///
/*TODO*///	return base;
/*TODO*///}
/*TODO*///
/*TODO*///void input_port_free(struct InputPort* dst)
/*TODO*///{
/*TODO*///	free(dst);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///void seq_set_string(InputSeq* a, const char *buf)
/*TODO*///{
/*TODO*///	char *lbuf;
/*TODO*///	char *arg = NULL;
/*TODO*///	int j;
/*TODO*///	struct ik *pik;
/*TODO*///	int found;
/*TODO*///
/*TODO*///	// create a locale buffer to be parsed by strtok
/*TODO*///	lbuf = malloc (strlen(buf)+1);
/*TODO*///
/*TODO*///	// copy the input string
/*TODO*///	strcpy (lbuf, buf);
/*TODO*///
/*TODO*///	for(j=0;j<SEQ_MAX;++j)
/*TODO*///		(*a)[j] = CODE_NONE;
/*TODO*///
/*TODO*///	arg = strtok(lbuf, " \t\r\n");
/*TODO*///	j = 0;
/*TODO*///	while( arg != NULL )
/*TODO*///	{
/*TODO*///		found = 0;
/*TODO*///
/*TODO*///		pik = input_keywords;
/*TODO*///
/*TODO*///		while (!found && pik->name && pik->name[0] != 0)
/*TODO*///		{
/*TODO*///			if (strcmp(pik->name,arg) == 0)
/*TODO*///			{
/*TODO*///				// this entry is only valid if it is a KEYCODE
/*TODO*///				if (pik->type == IKT_STD)
/*TODO*///				{
/*TODO*///					(*a)[j] = pik->val;
/*TODO*///					j++;
/*TODO*///					found = 1;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			pik++;
/*TODO*///		}
/*TODO*///
/*TODO*///		pik = osd_input_keywords;
/*TODO*///
/*TODO*///		if (pik)
/*TODO*///		{
/*TODO*///			while (!found && pik->name && pik->name[0] != 0)
/*TODO*///			{
/*TODO*///				if (strcmp(pik->name,arg) == 0)
/*TODO*///				{
/*TODO*///					switch (pik->type)
/*TODO*///					{
/*TODO*///						case IKT_STD:
/*TODO*///							(*a)[j] = pik->val;
/*TODO*///							j++;
/*TODO*///							found = 1;
/*TODO*///						break;
/*TODO*///
/*TODO*///						case IKT_OSD_KEY:
/*TODO*///							(*a)[j] = keyoscode_to_code(pik->val);
/*TODO*///							j++;
/*TODO*///							found = 1;
/*TODO*///						break;
/*TODO*///
/*TODO*///						case IKT_OSD_JOY:
/*TODO*///							(*a)[j] = joyoscode_to_code(pik->val);
/*TODO*///							j++;
/*TODO*///							found = 1;
/*TODO*///						break;
/*TODO*///					}
/*TODO*///				}
/*TODO*///				pik++;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		arg = strtok(NULL, " \t\r\n");
/*TODO*///	}
/*TODO*///	free (lbuf);
/*TODO*///}
/*TODO*///
/*TODO*///void init_analog_seq()
/*TODO*///{
/*TODO*///	struct InputPort *in;
/*TODO*///	int player, axis;
/*TODO*///
/*TODO*////* init analogjoy_input array */
/*TODO*///	for (player=0; player<OSD_MAX_JOY_ANALOG; player++)
/*TODO*///	{
/*TODO*///		for (axis=0; axis<MAX_ANALOG_AXES; axis++)
/*TODO*///		{
/*TODO*///			analogjoy_input[player][axis] = CODE_NONE;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	in = Machine->input_ports;
/*TODO*///	if (in->type == IPT_END) return; 	/* nothing to do */
/*TODO*///
/*TODO*///	/* make sure the InputPort definition is correct */
/*TODO*///	if (in->type != IPT_PORT)
/*TODO*///	{
/*TODO*///		logerror("Error in InputPort definition: expecting PORT_START\n");
/*TODO*///		return;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		in++;
/*TODO*///	}
/*TODO*///
/*TODO*///	while (in->type != IPT_END)
/*TODO*///	{
/*TODO*///		if (in->type != IPT_PORT && ((in->type & ~IPF_MASK) > IPT_ANALOG_START)
/*TODO*///			&& ((in->type & ~IPF_MASK) < IPT_ANALOG_END))
/*TODO*///		{
/*TODO*///			int j, invert;
/*TODO*///			InputSeq *seq;
/*TODO*///			InputCode analog_seq;
/*TODO*///
/*TODO*///			seq = input_port_seq(in);
/*TODO*///			invert = 0;
/*TODO*///			analog_seq = CODE_NONE;
/*TODO*///
/*TODO*///			for(j=0; j<SEQ_MAX && analog_seq == CODE_NONE; ++j)
/*TODO*///			{
/*TODO*///				switch ((*seq)[j])
/*TODO*///				{
/*TODO*///					case CODE_NONE :
/*TODO*///						continue;
/*TODO*///					case CODE_NOT :
/*TODO*///						invert = !invert;
/*TODO*///						break;
/*TODO*///					case CODE_OR :
/*TODO*///						invert = 0;
/*TODO*///						break;
/*TODO*///					default:
/*TODO*///						if (!invert && is_joystick_axis_code((*seq)[j]) )
/*TODO*///						{
/*TODO*///							analog_seq = return_os_joycode((*seq)[j]);
/*TODO*///						}
/*TODO*///						invert = 0;
/*TODO*///						break;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			if (analog_seq != CODE_NONE)
/*TODO*///			{
/*TODO*///				player = IP_GET_PLAYER(in);
/*TODO*///
/*TODO*///				switch (in->type & ~IPF_MASK)
/*TODO*///				{
/*TODO*///					case IPT_DIAL:
/*TODO*///					case IPT_PADDLE:
/*TODO*///					case IPT_TRACKBALL_X:
/*TODO*///					case IPT_LIGHTGUN_X:
/*TODO*///					case IPT_AD_STICK_X:
/*TODO*///						axis = X_AXIS;
/*TODO*///						break;
/*TODO*///					case IPT_DIAL_V:
/*TODO*///					case IPT_PADDLE_V:
/*TODO*///					case IPT_TRACKBALL_Y:
/*TODO*///					case IPT_LIGHTGUN_Y:
/*TODO*///					case IPT_AD_STICK_Y:
/*TODO*///						axis = Y_AXIS;
/*TODO*///						break;
/*TODO*///					case IPT_AD_STICK_Z:
/*TODO*///					case IPT_PEDAL2:
/*TODO*///						axis = Z_AXIS;
/*TODO*///						break;
/*TODO*///					case IPT_PEDAL:
/*TODO*///						axis = PEDAL_AXIS;
/*TODO*///						break;
/*TODO*///					default:
/*TODO*///						axis = 0;
/*TODO*///						break;
/*TODO*///				}
/*TODO*///
/*TODO*///				analogjoy_input[player][axis] = analog_seq;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		in++;
/*TODO*///	}
/*TODO*///
/*TODO*///	return;
/*TODO*///}
/*TODO*///
}
