/**
 * ported to 0.78
 */
package arcadeflex.v078.mame;

public class inputH {

    /*TODO*///#ifndef INPUT_H
/*TODO*///#define INPUT_H
/*TODO*///
/*TODO*///typedef unsigned InputCode;
/*TODO*///
/*TODO*///struct KeyboardInfo
/*TODO*///{
/*TODO*///	char *name; /* OS dependant name; 0 terminates the list */
/*TODO*///	unsigned code; /* OS dependant code */
/*TODO*///	InputCode standardcode;	/* CODE_xxx equivalent from list below, or CODE_OTHER if n/a */
/*TODO*///};
/*TODO*///
/*TODO*///struct JoystickInfo
/*TODO*///{
/*TODO*///	char *name; /* OS dependant name; 0 terminates the list */
/*TODO*///	unsigned code; /* OS dependant code */
/*TODO*///	InputCode standardcode;	/* CODE_xxx equivalent from list below, or CODE_OTHER if n/a */
/*TODO*///};
/*TODO*///
    /* key */
    public static final int KEYCODE_A = 0;
    public static final int KEYCODE_B = 1;
    public static final int KEYCODE_C = 2;
    public static final int KEYCODE_D = 3;
    public static final int KEYCODE_E = 4;
    public static final int KEYCODE_F = 5;
    public static final int KEYCODE_G = 6;
    public static final int KEYCODE_H = 7;
    public static final int KEYCODE_I = 8;
    public static final int KEYCODE_J = 9;
    public static final int KEYCODE_K = 10;
    public static final int KEYCODE_L = 11;
    public static final int KEYCODE_M = 12;
    public static final int KEYCODE_N = 13;
    public static final int KEYCODE_O = 14;
    public static final int KEYCODE_P = 15;
    public static final int KEYCODE_Q = 16;
    public static final int KEYCODE_R = 17;
    public static final int KEYCODE_S = 18;
    public static final int KEYCODE_T = 19;
    public static final int KEYCODE_U = 20;
    public static final int KEYCODE_V = 21;
    public static final int KEYCODE_W = 22;
    public static final int KEYCODE_X = 23;
    public static final int KEYCODE_Y = 24;
    public static final int KEYCODE_Z = 25;
    public static final int KEYCODE_0 = 26;
    public static final int KEYCODE_1 = 27;
    public static final int KEYCODE_2 = 28;
    public static final int KEYCODE_3 = 29;
    public static final int KEYCODE_4 = 30;
    public static final int KEYCODE_5 = 31;
    public static final int KEYCODE_6 = 32;
    public static final int KEYCODE_7 = 33;
    public static final int KEYCODE_8 = 34;
    public static final int KEYCODE_9 = 35;
    public static final int KEYCODE_0_PAD = 36;
    public static final int KEYCODE_1_PAD = 37;
    public static final int KEYCODE_2_PAD = 38;
    public static final int KEYCODE_3_PAD = 39;
    public static final int KEYCODE_4_PAD = 40;
    public static final int KEYCODE_5_PAD = 41;
    public static final int KEYCODE_6_PAD = 42;
    public static final int KEYCODE_7_PAD = 43;
    public static final int KEYCODE_8_PAD = 44;
    public static final int KEYCODE_9_PAD = 45;
    public static final int KEYCODE_F1 = 46;
    public static final int KEYCODE_F2 = 47;
    public static final int KEYCODE_F3 = 48;
    public static final int KEYCODE_F4 = 49;
    public static final int KEYCODE_F5 = 50;
    public static final int KEYCODE_F6 = 51;
    public static final int KEYCODE_F7 = 52;
    public static final int KEYCODE_F8 = 53;
    public static final int KEYCODE_F9 = 54;
    public static final int KEYCODE_F10 = 55;
    public static final int KEYCODE_F11 = 56;
    public static final int KEYCODE_F12 = 57;
    public static final int KEYCODE_ESC = 58;
    public static final int KEYCODE_TILDE = 59;
    public static final int KEYCODE_MINUS = 60;
    public static final int KEYCODE_EQUALS = 61;
    public static final int KEYCODE_BACKSPACE = 62;
    public static final int KEYCODE_TAB = 63;
    public static final int KEYCODE_OPENBRACE = 64;
    public static final int KEYCODE_CLOSEBRACE = 65;
    public static final int KEYCODE_ENTER = 66;
    public static final int KEYCODE_COLON = 67;
    public static final int KEYCODE_QUOTE = 68;
    public static final int KEYCODE_BACKSLASH = 69;
    public static final int KEYCODE_BACKSLASH2 = 70;
    public static final int KEYCODE_COMMA = 71;
    public static final int KEYCODE_STOP = 72;
    public static final int KEYCODE_SLASH = 73;
    public static final int KEYCODE_SPACE = 74;
    public static final int KEYCODE_INSERT = 75;
    public static final int KEYCODE_DEL = 76;
    public static final int KEYCODE_HOME = 77;
    public static final int KEYCODE_END = 78;
    public static final int KEYCODE_PGUP = 79;
    public static final int KEYCODE_PGDN = 80;
    public static final int KEYCODE_LEFT = 81;
    public static final int KEYCODE_RIGHT = 82;
    public static final int KEYCODE_UP = 83;
    public static final int KEYCODE_DOWN = 84;
    public static final int KEYCODE_SLASH_PAD = 85;
    public static final int KEYCODE_ASTERISK = 86;
    public static final int KEYCODE_MINUS_PAD = 87;
    public static final int KEYCODE_PLUS_PAD = 88;
    public static final int KEYCODE_DEL_PAD = 89;
    public static final int KEYCODE_ENTER_PAD = 90;
    public static final int KEYCODE_PRTSCR = 91;
    public static final int KEYCODE_PAUSE = 92;
    public static final int KEYCODE_LSHIFT = 93;
    public static final int KEYCODE_RSHIFT = 94;
    public static final int KEYCODE_LCONTROL = 95;
    public static final int KEYCODE_RCONTROL = 96;
    public static final int KEYCODE_LALT = 97;
    public static final int KEYCODE_RALT = 98;
    public static final int KEYCODE_SCRLOCK = 99;
    public static final int KEYCODE_NUMLOCK = 100;
    public static final int KEYCODE_CAPSLOCK = 101;
    public static final int KEYCODE_LWIN = 102;
    public static final int KEYCODE_RWIN = 103;
    public static final int KEYCODE_MENU = 104;

    public static final int __code_key_first = KEYCODE_A;
    public static final int __code_key_last = KEYCODE_MENU;
    /*TODO*///
/*TODO*///	/* joy */
/*TODO*///	JOYCODE_1_LEFT,JOYCODE_1_RIGHT,JOYCODE_1_UP,JOYCODE_1_DOWN,
/*TODO*///	JOYCODE_1_BUTTON1,JOYCODE_1_BUTTON2,JOYCODE_1_BUTTON3,
/*TODO*///	JOYCODE_1_BUTTON4,JOYCODE_1_BUTTON5,JOYCODE_1_BUTTON6,
/*TODO*///	JOYCODE_1_BUTTON7,JOYCODE_1_BUTTON8,JOYCODE_1_BUTTON9,
/*TODO*///	JOYCODE_1_BUTTON10, JOYCODE_1_START, JOYCODE_1_SELECT,
/*TODO*///	JOYCODE_2_LEFT,JOYCODE_2_RIGHT,JOYCODE_2_UP,JOYCODE_2_DOWN,
/*TODO*///	JOYCODE_2_BUTTON1,JOYCODE_2_BUTTON2,JOYCODE_2_BUTTON3,
/*TODO*///	JOYCODE_2_BUTTON4,JOYCODE_2_BUTTON5,JOYCODE_2_BUTTON6,
/*TODO*///	JOYCODE_2_BUTTON7,JOYCODE_2_BUTTON8,JOYCODE_2_BUTTON9,
/*TODO*///	JOYCODE_2_BUTTON10, JOYCODE_2_START, JOYCODE_2_SELECT,
/*TODO*///	JOYCODE_3_LEFT,JOYCODE_3_RIGHT,JOYCODE_3_UP,JOYCODE_3_DOWN,
/*TODO*///	JOYCODE_3_BUTTON1,JOYCODE_3_BUTTON2,JOYCODE_3_BUTTON3,
/*TODO*///	JOYCODE_3_BUTTON4,JOYCODE_3_BUTTON5,JOYCODE_3_BUTTON6,
/*TODO*///	JOYCODE_3_BUTTON7,JOYCODE_3_BUTTON8,JOYCODE_3_BUTTON9,
/*TODO*///	JOYCODE_3_BUTTON10, JOYCODE_3_START, JOYCODE_3_SELECT,
/*TODO*///	JOYCODE_4_LEFT,JOYCODE_4_RIGHT,JOYCODE_4_UP,JOYCODE_4_DOWN,
/*TODO*///	JOYCODE_4_BUTTON1,JOYCODE_4_BUTTON2,JOYCODE_4_BUTTON3,
/*TODO*///	JOYCODE_4_BUTTON4,JOYCODE_4_BUTTON5,JOYCODE_4_BUTTON6,
/*TODO*///	JOYCODE_4_BUTTON7,JOYCODE_4_BUTTON8,JOYCODE_4_BUTTON9,
/*TODO*///	JOYCODE_4_BUTTON10, JOYCODE_4_START, JOYCODE_4_SELECT,
/*TODO*///	JOYCODE_MOUSE_1_BUTTON1,JOYCODE_MOUSE_1_BUTTON2,JOYCODE_MOUSE_1_BUTTON3,
/*TODO*///	JOYCODE_MOUSE_2_BUTTON1,JOYCODE_MOUSE_2_BUTTON2,JOYCODE_MOUSE_2_BUTTON3,
/*TODO*///	JOYCODE_MOUSE_3_BUTTON1,JOYCODE_MOUSE_3_BUTTON2,JOYCODE_MOUSE_3_BUTTON3,
/*TODO*///	JOYCODE_MOUSE_4_BUTTON1,JOYCODE_MOUSE_4_BUTTON2,JOYCODE_MOUSE_4_BUTTON3,
/*TODO*///	JOYCODE_5_LEFT,JOYCODE_5_RIGHT,JOYCODE_5_UP,JOYCODE_5_DOWN,			/* JOYCODEs 5-8 placed here, */
/*TODO*///	JOYCODE_5_BUTTON1,JOYCODE_5_BUTTON2,JOYCODE_5_BUTTON3,				/* after original joycode mouse button */
/*TODO*///	JOYCODE_5_BUTTON4,JOYCODE_5_BUTTON5,JOYCODE_5_BUTTON6,				/* so old cfg files won't be broken */
/*TODO*///	JOYCODE_5_BUTTON7,JOYCODE_5_BUTTON8,JOYCODE_5_BUTTON9,				/* as much by their addition */
/*TODO*///	JOYCODE_5_BUTTON10, JOYCODE_5_START, JOYCODE_5_SELECT,
/*TODO*///	JOYCODE_6_LEFT,JOYCODE_6_RIGHT,JOYCODE_6_UP,JOYCODE_6_DOWN,
/*TODO*///	JOYCODE_6_BUTTON1,JOYCODE_6_BUTTON2,JOYCODE_6_BUTTON3,
/*TODO*///	JOYCODE_6_BUTTON4,JOYCODE_6_BUTTON5,JOYCODE_6_BUTTON6,
/*TODO*///	JOYCODE_6_BUTTON7,JOYCODE_6_BUTTON8,JOYCODE_6_BUTTON9,
/*TODO*///	JOYCODE_6_BUTTON10, JOYCODE_6_START, JOYCODE_6_SELECT,
/*TODO*///	JOYCODE_7_LEFT,JOYCODE_7_RIGHT,JOYCODE_7_UP,JOYCODE_7_DOWN,
/*TODO*///	JOYCODE_7_BUTTON1,JOYCODE_7_BUTTON2,JOYCODE_7_BUTTON3,
/*TODO*///	JOYCODE_7_BUTTON4,JOYCODE_7_BUTTON5,JOYCODE_7_BUTTON6,
/*TODO*///	JOYCODE_7_BUTTON7,JOYCODE_7_BUTTON8,JOYCODE_7_BUTTON9,
/*TODO*///	JOYCODE_7_BUTTON10, JOYCODE_7_START, JOYCODE_7_SELECT,
/*TODO*///	JOYCODE_8_LEFT,JOYCODE_8_RIGHT,JOYCODE_8_UP,JOYCODE_8_DOWN,
/*TODO*///	JOYCODE_8_BUTTON1,JOYCODE_8_BUTTON2,JOYCODE_8_BUTTON3,
/*TODO*///	JOYCODE_8_BUTTON4,JOYCODE_8_BUTTON5,JOYCODE_8_BUTTON6,
/*TODO*///	JOYCODE_8_BUTTON7,JOYCODE_8_BUTTON8,JOYCODE_8_BUTTON9,
/*TODO*///	JOYCODE_8_BUTTON10, JOYCODE_8_START, JOYCODE_8_SELECT,
/*TODO*///	JOYCODE_MOUSE_5_BUTTON1,JOYCODE_MOUSE_5_BUTTON2,JOYCODE_MOUSE_5_BUTTON3,
/*TODO*///	JOYCODE_MOUSE_6_BUTTON1,JOYCODE_MOUSE_6_BUTTON2,JOYCODE_MOUSE_6_BUTTON3,
/*TODO*///	JOYCODE_MOUSE_7_BUTTON1,JOYCODE_MOUSE_7_BUTTON2,JOYCODE_MOUSE_7_BUTTON3,
/*TODO*///	JOYCODE_MOUSE_8_BUTTON1,JOYCODE_MOUSE_8_BUTTON2,JOYCODE_MOUSE_8_BUTTON3,
/*TODO*///	JOYCODE_MOUSE_1_BUTTON4,JOYCODE_MOUSE_1_BUTTON5,JOYCODE_MOUSE_1_BUTTON6, /* Placed here so old cfg files won't break */
/*TODO*///	JOYCODE_MOUSE_2_BUTTON4,JOYCODE_MOUSE_2_BUTTON5,JOYCODE_MOUSE_2_BUTTON6,
/*TODO*///	JOYCODE_MOUSE_3_BUTTON4,JOYCODE_MOUSE_3_BUTTON5,JOYCODE_MOUSE_3_BUTTON6,
/*TODO*///	JOYCODE_MOUSE_4_BUTTON4,JOYCODE_MOUSE_4_BUTTON5,JOYCODE_MOUSE_4_BUTTON6, 
/*TODO*///	JOYCODE_MOUSE_5_BUTTON4,JOYCODE_MOUSE_5_BUTTON5,JOYCODE_MOUSE_5_BUTTON6,
/*TODO*///	JOYCODE_MOUSE_6_BUTTON4,JOYCODE_MOUSE_6_BUTTON5,JOYCODE_MOUSE_6_BUTTON6,
/*TODO*///	JOYCODE_MOUSE_7_BUTTON4,JOYCODE_MOUSE_7_BUTTON5,JOYCODE_MOUSE_7_BUTTON6,
/*TODO*///	JOYCODE_MOUSE_8_BUTTON4,JOYCODE_MOUSE_8_BUTTON5,JOYCODE_MOUSE_8_BUTTON6, 
/*TODO*///
/*TODO*///#define __code_joy_first JOYCODE_1_LEFT
/*TODO*///#define __code_joy_last JOYCODE_MOUSE_8_BUTTON6
/*TODO*///
/*TODO*///	__code_max, /* Temination of standard code */
/*TODO*///
    /* special */
    public static final int CODE_NONE = 0x8000;/* no code, also marker of sequence end */
    public static final int CODE_OTHER = 0x8001;/* OS code not mapped to any other code */
    public static final int CODE_DEFAULT = 0x8002;/* special for input port definitions */
    public static final int CODE_PREVIOUS = 0x8003;/* special for input port definitions */
    public static final int CODE_NOT = 0x8004;/* operators for sequences */
    public static final int CODE_OR = 0x8005;/* operators for sequences */

 /*TODO*///
/*TODO*////* Wrapper for compatibility */
/*TODO*///#define KEYCODE_OTHER CODE_OTHER
/*TODO*///#define JOYCODE_OTHER CODE_OTHER
/*TODO*///#define KEYCODE_NONE CODE_NONE
/*TODO*///#define JOYCODE_NONE CODE_NONE
/*TODO*///
/*TODO*////***************************************************************************/
/*TODO*////* Single code functions */
/*TODO*///
/*TODO*///int code_init(void);
/*TODO*///void code_close(void);
/*TODO*///
/*TODO*///InputCode keyoscode_to_code(unsigned oscode);
/*TODO*///InputCode joyoscode_to_code(unsigned oscode);
/*TODO*///InputCode savecode_to_code(unsigned savecode);
/*TODO*///unsigned code_to_savecode(InputCode code);
/*TODO*///
/*TODO*///const char *code_name(InputCode code);
/*TODO*///int code_pressed(InputCode code);
/*TODO*///int code_pressed_memory(InputCode code);
/*TODO*///int code_pressed_memory_repeat(InputCode code, int speed);
/*TODO*///InputCode code_read_async(void);
/*TODO*///INT8 code_read_hex_async(void);
/*TODO*///
/*TODO*////* Wrappers for compatibility */
/*TODO*///#define keyboard_name                   code_name
/*TODO*///#define keyboard_pressed                code_pressed
/*TODO*///#define keyboard_pressed_memory         code_pressed_memory
/*TODO*///#define keyboard_pressed_memory_repeat  code_pressed_memory_repeat
/*TODO*///#define keyboard_read_async             code_read_async
/*TODO*///
/*TODO*////***************************************************************************/
/*TODO*////* Sequence code funtions */
/*TODO*///
/*TODO*////* NOTE: If you modify this value you need also to modify the SEQ_DEF declarations */
    public static final int SEQ_MAX = 16;
    /*TODO*///
/*TODO*///typedef InputCode InputSeq[SEQ_MAX];
/*TODO*///
/*TODO*///INLINE InputCode seq_get_1(InputSeq* a) {
/*TODO*///	return (*a)[0];
/*TODO*///}
/*TODO*///
/*TODO*///void seq_set_0(InputSeq* seq);
/*TODO*///void seq_set_1(InputSeq* seq, InputCode code);
/*TODO*///void seq_set_2(InputSeq* seq, InputCode code1, InputCode code2);
/*TODO*///void seq_set_3(InputSeq* seq, InputCode code1, InputCode code2, InputCode code3);
/*TODO*///void seq_set_4(InputSeq* seq, InputCode code1, InputCode code2, InputCode code3, InputCode code4);
/*TODO*///void seq_set_5(InputSeq* seq, InputCode code1, InputCode code2, InputCode code3, InputCode code4, InputCode code5);
/*TODO*///void seq_copy(InputSeq* seqdst, InputSeq* seqsrc);
/*TODO*///int seq_cmp(InputSeq* seq1, InputSeq* seq2);
/*TODO*///void seq_name(InputSeq* seq, char* buffer, unsigned max);
/*TODO*///int seq_pressed(InputSeq* seq);
/*TODO*///void seq_read_async_start(void);
/*TODO*///int seq_read_async(InputSeq* code, int first);
/*TODO*///
/*TODO*////* NOTE: It's very important that this sequence is EXACLY long SEQ_MAX */
/*TODO*///#define SEQ_DEF_6(a,b,c,d,e,f) { a, b, c, d, e, f, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE }
/*TODO*///#define SEQ_DEF_5(a,b,c,d,e) SEQ_DEF_6(a,b,c,d,e,CODE_NONE)
/*TODO*///#define SEQ_DEF_4(a,b,c,d) SEQ_DEF_5(a,b,c,d,CODE_NONE)
/*TODO*///#define SEQ_DEF_3(a,b,c) SEQ_DEF_4(a,b,c,CODE_NONE)
/*TODO*///#define SEQ_DEF_2(a,b) SEQ_DEF_3(a,b,CODE_NONE)
/*TODO*///#define SEQ_DEF_1(a) SEQ_DEF_2(a,CODE_NONE)
/*TODO*///#define SEQ_DEF_0 SEQ_DEF_1(CODE_NONE)
/*TODO*///
/*TODO*////***************************************************************************/
/*TODO*////* input_ui */
/*TODO*///
/*TODO*///int input_ui_pressed(int code);
/*TODO*///int input_ui_pressed_repeat(int code, int speed);
/*TODO*///
/*TODO*////***************************************************************************/
/*TODO*////* analog joy code functions */
/*TODO*///
/*TODO*///int is_joystick_axis_code(unsigned code);
/*TODO*///int return_os_joycode(InputCode code);
/*TODO*///
/*TODO*///#endif
/*TODO*///    
}
