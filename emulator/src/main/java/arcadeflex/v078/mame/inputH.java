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

    /* joy */
    public static final int JOYCODE_1_LEFT = 105;
    public static final int JOYCODE_1_RIGHT = 106;
    public static final int JOYCODE_1_UP = 107;
    public static final int JOYCODE_1_DOWN = 108;
    public static final int JOYCODE_1_BUTTON1 = 109;
    public static final int JOYCODE_1_BUTTON2 = 110;
    public static final int JOYCODE_1_BUTTON3 = 111;
    public static final int JOYCODE_1_BUTTON4 = 112;
    public static final int JOYCODE_1_BUTTON5 = 113;
    public static final int JOYCODE_1_BUTTON6 = 114;
    public static final int JOYCODE_1_BUTTON7 = 115;
    public static final int JOYCODE_1_BUTTON8 = 116;
    public static final int JOYCODE_1_BUTTON9 = 117;
    public static final int JOYCODE_1_BUTTON10 = 118;
    public static final int JOYCODE_1_START = 119;
    public static final int JOYCODE_1_SELECT = 120;
    public static final int JOYCODE_2_LEFT = 121;
    public static final int JOYCODE_2_RIGHT = 122;
    public static final int JOYCODE_2_UP = 123;
    public static final int JOYCODE_2_DOWN = 124;
    public static final int JOYCODE_2_BUTTON1 = 125;
    public static final int JOYCODE_2_BUTTON2 = 126;
    public static final int JOYCODE_2_BUTTON3 = 127;
    public static final int JOYCODE_2_BUTTON4 = 128;
    public static final int JOYCODE_2_BUTTON5 = 129;
    public static final int JOYCODE_2_BUTTON6 = 130;
    public static final int JOYCODE_2_BUTTON7 = 131;
    public static final int JOYCODE_2_BUTTON8 = 132;
    public static final int JOYCODE_2_BUTTON9 = 133;
    public static final int JOYCODE_2_BUTTON10 = 134;
    public static final int JOYCODE_2_START = 135;
    public static final int JOYCODE_2_SELECT = 136;
    public static final int JOYCODE_3_LEFT = 137;
    public static final int JOYCODE_3_RIGHT = 138;
    public static final int JOYCODE_3_UP = 139;
    public static final int JOYCODE_3_DOWN = 140;
    public static final int JOYCODE_3_BUTTON1 = 141;
    public static final int JOYCODE_3_BUTTON2 = 142;
    public static final int JOYCODE_3_BUTTON3 = 143;
    public static final int JOYCODE_3_BUTTON4 = 144;
    public static final int JOYCODE_3_BUTTON5 = 145;
    public static final int JOYCODE_3_BUTTON6 = 146;
    public static final int JOYCODE_3_BUTTON7 = 147;
    public static final int JOYCODE_3_BUTTON8 = 148;
    public static final int JOYCODE_3_BUTTON9 = 149;
    public static final int JOYCODE_3_BUTTON10 = 150;
    public static final int JOYCODE_3_START = 151;
    public static final int JOYCODE_3_SELECT = 152;
    public static final int JOYCODE_4_LEFT = 153;
    public static final int JOYCODE_4_RIGHT = 154;
    public static final int JOYCODE_4_UP = 155;
    public static final int JOYCODE_4_DOWN = 156;
    public static final int JOYCODE_4_BUTTON1 = 157;
    public static final int JOYCODE_4_BUTTON2 = 158;
    public static final int JOYCODE_4_BUTTON3 = 159;
    public static final int JOYCODE_4_BUTTON4 = 160;
    public static final int JOYCODE_4_BUTTON5 = 161;
    public static final int JOYCODE_4_BUTTON6 = 162;
    public static final int JOYCODE_4_BUTTON7 = 163;
    public static final int JOYCODE_4_BUTTON8 = 164;
    public static final int JOYCODE_4_BUTTON9 = 165;
    public static final int JOYCODE_4_BUTTON10 = 166;
    public static final int JOYCODE_4_START = 167;
    public static final int JOYCODE_4_SELECT = 168;
    public static final int JOYCODE_MOUSE_1_BUTTON1 = 169;
    public static final int JOYCODE_MOUSE_1_BUTTON2 = 170;
    public static final int JOYCODE_MOUSE_1_BUTTON3 = 171;
    public static final int JOYCODE_MOUSE_2_BUTTON1 = 172;
    public static final int JOYCODE_MOUSE_2_BUTTON2 = 173;
    public static final int JOYCODE_MOUSE_2_BUTTON3 = 174;
    public static final int JOYCODE_MOUSE_3_BUTTON1 = 175;
    public static final int JOYCODE_MOUSE_3_BUTTON2 = 176;
    public static final int JOYCODE_MOUSE_3_BUTTON3 = 177;
    public static final int JOYCODE_MOUSE_4_BUTTON1 = 178;
    public static final int JOYCODE_MOUSE_4_BUTTON2 = 179;
    public static final int JOYCODE_MOUSE_4_BUTTON3 = 180;
    public static final int JOYCODE_5_LEFT = 181;
    public static final int JOYCODE_5_RIGHT = 182;
    public static final int JOYCODE_5_UP = 183;
    public static final int JOYCODE_5_DOWN = 184;/* JOYCODEs 5-8 placed here, */
    public static final int JOYCODE_5_BUTTON1 = 185;
    public static final int JOYCODE_5_BUTTON2 = 186;
    public static final int JOYCODE_5_BUTTON3 = 187;/* after original joycode mouse button */
    public static final int JOYCODE_5_BUTTON4 = 188;
    public static final int JOYCODE_5_BUTTON5 = 189;
    public static final int JOYCODE_5_BUTTON6 = 190;/* so old cfg files won't be broken */
    public static final int JOYCODE_5_BUTTON7 = 191;
    public static final int JOYCODE_5_BUTTON8 = 192;
    public static final int JOYCODE_5_BUTTON9 = 193;/* as much by their addition */
    public static final int JOYCODE_5_BUTTON10 = 194;
    public static final int JOYCODE_5_START = 195;
    public static final int JOYCODE_5_SELECT = 196;
    public static final int JOYCODE_6_LEFT = 197;
    public static final int JOYCODE_6_RIGHT = 198;
    public static final int JOYCODE_6_UP = 199;
    public static final int JOYCODE_6_DOWN = 200;
    public static final int JOYCODE_6_BUTTON1 = 201;
    public static final int JOYCODE_6_BUTTON2 = 202;
    public static final int JOYCODE_6_BUTTON3 = 203;
    public static final int JOYCODE_6_BUTTON4 = 204;
    public static final int JOYCODE_6_BUTTON5 = 205;
    public static final int JOYCODE_6_BUTTON6 = 206;
    public static final int JOYCODE_6_BUTTON7 = 207;
    public static final int JOYCODE_6_BUTTON8 = 208;
    public static final int JOYCODE_6_BUTTON9 = 209;
    public static final int JOYCODE_6_BUTTON10 = 210;
    public static final int JOYCODE_6_START = 211;
    public static final int JOYCODE_6_SELECT = 212;
    public static final int JOYCODE_7_LEFT = 213;
    public static final int JOYCODE_7_RIGHT = 214;
    public static final int JOYCODE_7_UP = 215;
    public static final int JOYCODE_7_DOWN = 216;
    public static final int JOYCODE_7_BUTTON1 = 217;
    public static final int JOYCODE_7_BUTTON2 = 218;
    public static final int JOYCODE_7_BUTTON3 = 219;
    public static final int JOYCODE_7_BUTTON4 = 220;
    public static final int JOYCODE_7_BUTTON5 = 221;
    public static final int JOYCODE_7_BUTTON6 = 222;
    public static final int JOYCODE_7_BUTTON7 = 223;
    public static final int JOYCODE_7_BUTTON8 = 224;
    public static final int JOYCODE_7_BUTTON9 = 225;
    public static final int JOYCODE_7_BUTTON10 = 226;
    public static final int JOYCODE_7_START = 227;
    public static final int JOYCODE_7_SELECT = 228;
    public static final int JOYCODE_8_LEFT = 229;
    public static final int JOYCODE_8_RIGHT = 230;
    public static final int JOYCODE_8_UP = 231;
    public static final int JOYCODE_8_DOWN = 232;
    public static final int JOYCODE_8_BUTTON1 = 233;
    public static final int JOYCODE_8_BUTTON2 = 234;
    public static final int JOYCODE_8_BUTTON3 = 235;
    public static final int JOYCODE_8_BUTTON4 = 236;
    public static final int JOYCODE_8_BUTTON5 = 237;
    public static final int JOYCODE_8_BUTTON6 = 238;
    public static final int JOYCODE_8_BUTTON7 = 239;
    public static final int JOYCODE_8_BUTTON8 = 240;
    public static final int JOYCODE_8_BUTTON9 = 241;
    public static final int JOYCODE_8_BUTTON10 = 242;
    public static final int JOYCODE_8_START = 243;
    public static final int JOYCODE_8_SELECT = 244;
    public static final int JOYCODE_MOUSE_5_BUTTON1 = 245;
    public static final int JOYCODE_MOUSE_5_BUTTON2 = 246;
    public static final int JOYCODE_MOUSE_5_BUTTON3 = 247;
    public static final int JOYCODE_MOUSE_6_BUTTON1 = 248;
    public static final int JOYCODE_MOUSE_6_BUTTON2 = 249;
    public static final int JOYCODE_MOUSE_6_BUTTON3 = 250;
    public static final int JOYCODE_MOUSE_7_BUTTON1 = 251;
    public static final int JOYCODE_MOUSE_7_BUTTON2 = 252;
    public static final int JOYCODE_MOUSE_7_BUTTON3 = 253;
    public static final int JOYCODE_MOUSE_8_BUTTON1 = 254;
    public static final int JOYCODE_MOUSE_8_BUTTON2 = 255;
    public static final int JOYCODE_MOUSE_8_BUTTON3 = 256;
    public static final int JOYCODE_MOUSE_1_BUTTON4 = 257;
    public static final int JOYCODE_MOUSE_1_BUTTON5 = 258;
    public static final int JOYCODE_MOUSE_1_BUTTON6 = 259;/* Placed here so old cfg files won't break */
    public static final int JOYCODE_MOUSE_2_BUTTON4 = 260;
    public static final int JOYCODE_MOUSE_2_BUTTON5 = 261;
    public static final int JOYCODE_MOUSE_2_BUTTON6 = 262;
    public static final int JOYCODE_MOUSE_3_BUTTON4 = 263;
    public static final int JOYCODE_MOUSE_3_BUTTON5 = 264;
    public static final int JOYCODE_MOUSE_3_BUTTON6 = 265;
    public static final int JOYCODE_MOUSE_4_BUTTON4 = 266;
    public static final int JOYCODE_MOUSE_4_BUTTON5 = 267;
    public static final int JOYCODE_MOUSE_4_BUTTON6 = 268;
    public static final int JOYCODE_MOUSE_5_BUTTON4 = 269;
    public static final int JOYCODE_MOUSE_5_BUTTON5 = 270;
    public static final int JOYCODE_MOUSE_5_BUTTON6 = 271;
    public static final int JOYCODE_MOUSE_6_BUTTON4 = 272;
    public static final int JOYCODE_MOUSE_6_BUTTON5 = 273;
    public static final int JOYCODE_MOUSE_6_BUTTON6 = 274;
    public static final int JOYCODE_MOUSE_7_BUTTON4 = 275;
    public static final int JOYCODE_MOUSE_7_BUTTON5 = 276;
    public static final int JOYCODE_MOUSE_7_BUTTON6 = 277;
    public static final int JOYCODE_MOUSE_8_BUTTON4 = 278;
    public static final int JOYCODE_MOUSE_8_BUTTON5 = 279;
    public static final int JOYCODE_MOUSE_8_BUTTON6 = 280;

    public static final int __code_joy_first = JOYCODE_1_LEFT;
    public static final int __code_joy_last = JOYCODE_MOUSE_8_BUTTON6;

    public static final int __code_max = 281;/* Temination of standard code */

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
/*TODO*///
/*TODO*////* Wrappers for compatibility */
/*TODO*///#define keyboard_name                   code_name
/*TODO*///#define keyboard_pressed                code_pressed
/*TODO*///#define keyboard_pressed_memory         code_pressed_memory
/*TODO*///#define keyboard_pressed_memory_repeat  code_pressed_memory_repeat
/*TODO*///#define keyboard_read_async             code_read_async

    /**
     * ************************************************************************
     */
    /* Sequence code funtions */

 /* NOTE: If you modify this value you need also to modify the SEQ_DEF declarations */
    public static final int SEQ_MAX = 16;

    /*TODO*///typedef InputCode InputSeq[SEQ_MAX];
/*TODO*///
    public static int seq_get_1(int[] a) {
        return a[0];
    }

    /* NOTE: It's very important that this sequence is EXACLY long SEQ_MAX */
    public static int[] SEQ_DEF_6(int a, int b, int c, int d, int e, int f) {
        return new int[]{a, b, c, d, e, f, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE, CODE_NONE};
    }

    public static int[] SEQ_DEF_5(int a, int b, int c, int d, int e) {
        return SEQ_DEF_6(a, b, c, d, e, CODE_NONE);
    }

    public static int[] SEQ_DEF_4(int a, int b, int c, int d) {
        return SEQ_DEF_5(a, b, c, d, CODE_NONE);
    }

    public static int[] SEQ_DEF_3(int a, int b, int c) {
        return SEQ_DEF_4(a, b, c, CODE_NONE);
    }

    public static int[] SEQ_DEF_2(int a, int b) {
        return SEQ_DEF_3(a, b, CODE_NONE);
    }

    public static int[] SEQ_DEF_1(int a) {
        return SEQ_DEF_2(a, CODE_NONE);
    }

    public static int[] SEQ_DEF_0() {
        return SEQ_DEF_1(CODE_NONE);
    }
}
