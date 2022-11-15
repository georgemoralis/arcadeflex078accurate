package gr.codebb.arcadeflex_convertor;

import static gr.codebb.arcadeflex_convertor.Convertor.inpos;
import static gr.codebb.arcadeflex_convertor.Convertor.token;
import static gr.codebb.arcadeflex_convertor.sUtil.getToken;
import static gr.codebb.arcadeflex_convertor.sUtil.parseChar;
import static gr.codebb.arcadeflex_convertor.sUtil.parseToken;
import static gr.codebb.arcadeflex_convertor.sUtil.skipLine;
import static gr.codebb.arcadeflex_convertor.sUtil.skipSpace;

public class convertMame {

    public static void ConvertMame() {
        Analyse();
        Convert();
    }

    public static void Analyse() {

    }

    static final int GAMEDRIVER = 0;
    static final int INPUTPORTS = 1;
    static final int INTERRUPT = 2;
    static final int VIDEO_START = 3;
    static final int VIDEO_STOP = 4;
    static final int VIDEO_UPDATE = 5;
    static final int VIDEO_EOF = 6;
    static final int PALETTE_INIT = 7;
    static final int MACHINE_INIT = 8;
    static final int MACHINE_STOP = 9;
    static final int DRIVER_INIT = 10;

    public static void Convert() {
        Convertor.inpos = 0;//position of pointer inside the buffers
        Convertor.outpos = 0;
        boolean only_once_flag = false;//gia na baleis to header mono mia fora
        boolean line_change_flag = false;

        int kapa = 0;
        int i = 0;
        int type = 0;
        int i3 = -1;
        int i8 = -1;
        int type2 = 0;
        int[] insideagk = new int[10];//get the { that are inside functions

        do {
            if (Convertor.inpos >= Convertor.inbuf.length)//an to megethos einai megalitero spase to loop
            {
                break;
            }
            char c = sUtil.getChar(); //pare ton character
            if (line_change_flag) {
                for (int i1 = 0; i1 < kapa; i1++) {
                    sUtil.putString("\t");
                }

                line_change_flag = false;
            }
            switch (c) {
                case 35: // '#'
                {
                    if (!sUtil.getToken("#include"))//an den einai #include min to trexeis
                    {
                        break;
                    }
                    sUtil.skipLine();
                    if (!only_once_flag)//trekse auto to komati mono otan bris to proto include
                    {
                        only_once_flag = true;
                        sUtil.putString("/*\r\n");
                        sUtil.putString(" * ported to v" + Convertor.mameversion + "\r\n");
                        sUtil.putString(" * using automatic conversion tool v" + Convertor.convertorversion + "\r\n");
                        /*sUtil.putString(" * converted at : " + Convertor.timenow() + "\r\n");*/
                        sUtil.putString(" */ \r\n");
                        sUtil.putString("package " + Convertor.packageName + ";\r\n");
                        sUtil.putString("\r\n");
                        sUtil.putString((new StringBuilder()).append("public class ").append(Convertor.className).append("\r\n").toString());
                        sUtil.putString("{\r\n");
                        kapa = 1;
                        line_change_flag = true;
                    }
                    continue;
                }
                case 10: // '\n'
                {
                    Convertor.outbuf[Convertor.outpos++] = Convertor.inbuf[Convertor.inpos++];
                    line_change_flag = true;
                    continue;
                }
                case 'D': {
                    i = Convertor.inpos;
                    if (type2 == INPUTPORTS) {
                        if (sUtil.getToken("DEF_STR(")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.putString((new StringBuilder()).append("DEF_STR( \"").append(Convertor.token[0]).append("\")").toString());
                            i3 = -1;

                            continue;
                        }
                    }
                    if (sUtil.getToken("DRIVER_INIT")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        if (sUtil.parseChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        } else {
                            sUtil.putString("public static DriverInitHandlerPtr init_" + Convertor.token[0] + "  = new DriverInitHandlerPtr() { public void handler()");
                            type = DRIVER_INIT;
                            i3 = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                }
                Convertor.inpos = i;
                break;
                case 'G': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("GAME") || sUtil.getToken("GAMEX")) {
                        sUtil.skipSpace();
                        if (sUtil.getChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        } else {
                            Convertor.inpos += 1;
                        }
                        if (sUtil.getChar() == ')')//fix an issue in driverH
                        {
                            Convertor.inpos = i;
                            break;
                        }
                        type2 = GAMEDRIVER;
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseTokenGameDriv();//year
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[1] = sUtil.parseToken();//rom
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[2] = sUtil.parseToken();//parent
                        if (Convertor.token[2].matches("0")) {
                            Convertor.token[2] = "null";
                        } else {
                            Convertor.token[2] = "driver_" + Convertor.token[2];
                        }
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[3] = sUtil.parseToken();//machine
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[4] = sUtil.parseToken();//input
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[5] = sUtil.parseToken();//init
                        if (Convertor.token[5].matches("0")) {
                            Convertor.token[5] = "null";
                        } else {
                            Convertor.token[5] = "init_" + Convertor.token[5];
                        }
                        Convertor.inpos++;
                        sUtil.skipSpace();
                        Convertor.token[6] = sUtil.parseToken();//ROT

                        sUtil.putString((new StringBuilder()).append("public static GameDriver driver_").append(Convertor.token[1]).append("\t   = new GameDriver(\"").append(Convertor.token[0]).append("\"\t,\"").append(Convertor.token[1]).append("\"\t,\"").append(Convertor.className).append(".java\"\t,rom_")
                                .append(Convertor.token[1]).append(",").append(Convertor.token[2])
                                .append("\t,machine_driver_").append(Convertor.token[3])
                                .append("\t,input_ports_").append(Convertor.token[4])
                                .append("\t,").append(Convertor.token[5])
                                .append("\t,").append(Convertor.token[6])
                                .toString());
                        continue;
                    }
                }
                Convertor.inpos = i;
                break;
                case 'I': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("INPUT_PORTS_START")) {
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.putString((new StringBuilder()).append("static InputPortHandlerPtr input_ports_").append(Convertor.token[0]).append(" = new InputPortHandlerPtr(){ public void handler() { ").toString());
                    }
                    if (sUtil.getToken("INPUT_PORTS_END")) {
                        sUtil.putString((new StringBuilder()).append("INPUT_PORTS_END(); }}; ").toString());
                        continue;
                    }
                    if (sUtil.getToken("INTERRUPT_GEN")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        if (sUtil.parseChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        } else {
                            sUtil.putString("public static InterruptHandlerPtr " + Convertor.token[0] + " = new InterruptHandlerPtr() {public void handler()");
                            type = INTERRUPT;
                            i3 = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                }
                Convertor.inpos = i;
                break;
                case 'M': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("MACHINE_INIT")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        if (sUtil.parseChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        } else {
                            sUtil.putString("public static MachineInitHandlerPtr machine_init_" + Convertor.token[0] + "  = new MachineInitHandlerPtr() { public void handler()");
                            type = MACHINE_INIT;
                            i3 = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (sUtil.getToken("MACHINE_STOP")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        if (sUtil.parseChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        } else {
                            sUtil.putString("public static MachineStopHandlerPtr machine_stop_" + Convertor.token[0] + "  = new MachineStopHandlerPtr() { public void handler()");
                            type = MACHINE_STOP;
                            i3 = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                }
                Convertor.inpos = i;
                break;
                case 'P': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("PORT_START")) {
                        sUtil.putString((new StringBuilder()).append("PORT_START(); ").toString());
                        continue;
                    }
                    if (sUtil.getToken("PORT_DIPNAME") || sUtil.getToken("PORT_BIT") || sUtil.getToken("PORT_DIPSETTING") || sUtil.getToken("PORT_BITX") || sUtil.getToken("PORT_SERVICE") || sUtil.getToken("PORT_BIT_IMPULSE") || sUtil.getToken("PORT_ANALOG") || sUtil.getToken("PORT_ANALOGX")) {
                        i8++;
                        type2 = INPUTPORTS;
                        sUtil.skipSpace();
                        if (sUtil.parseChar() == '(') {
                            Convertor.inpos = i;
                        }
                    }
                    if (sUtil.getToken("PALETTE_INIT")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        if (sUtil.parseChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        } else {
                            sUtil.putString("public static PaletteInitHandlerPtr palette_init_" + Convertor.token[0] + "  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom)");
                            type = PALETTE_INIT;
                            i3 = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                }
                Convertor.inpos = i;
                break;
                case 'R': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("ROM_START")) {
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.putString((new StringBuilder()).append("static RomLoadHandlerPtr rom_").append(Convertor.token[0]).append(" = new RomLoadHandlerPtr(){ public void handler(){ ").toString());
                        continue;
                    }
                    if (sUtil.getToken("ROM_END")) {
                        sUtil.putString((new StringBuilder()).append("ROM_END(); }}; ").toString());
                        continue;
                    }
                }
                Convertor.inpos = i;
                break;
                case 's': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("static")) {
                        sUtil.skipSpace();
                    }
                    if (!sUtil.getToken("struct")) //static but not static struct
                    {
                        if (sUtil.getToken("INTERRUPT_GEN")) {
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '(') {
                                Convertor.inpos = i;
                                break;
                            }
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != ')') {
                                Convertor.inpos = i;
                                break;
                            }
                            if (sUtil.parseChar() == ';') {
                                sUtil.skipLine();
                                continue;
                            } else {
                                sUtil.putString("public static InterruptHandlerPtr " + Convertor.token[0] + " = new InterruptHandlerPtr() {public void handler()");
                                type = INTERRUPT;
                                i3 = -1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("PALETTE_INIT")) {
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '(') {
                                Convertor.inpos = i;
                                break;
                            }
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != ')') {
                                Convertor.inpos = i;
                                break;
                            }
                            if (sUtil.parseChar() == ';') {
                                sUtil.skipLine();
                                continue;
                            } else {
                                sUtil.putString("public static PaletteInitHandlerPtr palette_init_" + Convertor.token[0] + "  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom)");
                                type = PALETTE_INIT;
                                i3 = -1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("MACHINE_INIT")) {
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '(') {
                                Convertor.inpos = i;
                                break;
                            }
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != ')') {
                                Convertor.inpos = i;
                                break;
                            }
                            if (sUtil.parseChar() == ';') {
                                sUtil.skipLine();
                                continue;
                            } else {
                                sUtil.putString("public static MachineInitHandlerPtr machine_init_" + Convertor.token[0] + "  = new MachineInitHandlerPtr() { public void handler()");
                                type = MACHINE_INIT;
                                i3 = -1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("DRIVER_INIT")) {
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '(') {
                                Convertor.inpos = i;
                                break;
                            }
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != ')') {
                                Convertor.inpos = i;
                                break;
                            }
                            if (sUtil.parseChar() == ';') {
                                sUtil.skipLine();
                                continue;
                            } else {
                                sUtil.putString("public static DriverInitHandlerPtr init_" + Convertor.token[0] + "  = new DriverInitHandlerPtr() { public void handler()");
                                type = DRIVER_INIT;
                                i3 = -1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        }
                    } // end of static but not static struct
                }
                Convertor.inpos = i;
                break;   
                case 'V': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("VIDEO_START")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        if (sUtil.parseChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        } else {
                            sUtil.putString("public static VideoStartHandlerPtr video_start_" + Convertor.token[0] + "  = new VideoStartHandlerPtr() { public int handler()");
                            type = VIDEO_START;
                            i3 = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (sUtil.getToken("VIDEO_STOP")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        if (sUtil.parseChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        } else {
                            sUtil.putString("public static VideoStopHandlerPtr video_stop_" + Convertor.token[0] + "  = new VideoStopHandlerPtr() { public void handler()");
                            type = VIDEO_STOP;
                            i3 = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (sUtil.getToken("VIDEO_UPDATE")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        if (sUtil.parseChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        } else {
                            sUtil.putString("public static VideoUpdateHandlerPtr video_update_" + Convertor.token[0] + "  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect)");
                            type = VIDEO_UPDATE;
                            i3 = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (sUtil.getToken("VIDEO_EOF")) {
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != '(') {
                            Convertor.inpos = i;
                            break;
                        }
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ')') {
                            Convertor.inpos = i;
                            break;
                        }
                        if (sUtil.parseChar() == ';') {
                            sUtil.skipLine();
                            continue;
                        } else {
                            sUtil.putString("public static VideoEofHandlerPtr video_eof_" + Convertor.token[0] + "  = new VideoEofHandlerPtr() { public void handler()");
                            type = VIDEO_EOF;
                            i3 = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                }
                Convertor.inpos = i;
                break;
                case ')': {
                    i = Convertor.inpos;
                    if (type2 == INPUTPORTS) {
                        i8--;
                        i = Convertor.inpos;
                        Convertor.inpos += 1;
                        if (sUtil.parseChar() == '\"') {
                            Convertor.outbuf[(Convertor.outpos++)] = '\"';
                            Convertor.outbuf[(Convertor.outpos++)] = ')';
                            Convertor.outbuf[(Convertor.outpos++)] = ')';
                            Convertor.outbuf[(Convertor.outpos++)] = ';';
                            Convertor.inpos += 3;
                        } else {
                            Convertor.inpos = i;
                            Convertor.outbuf[(Convertor.outpos++)] = ')';
                            Convertor.outbuf[(Convertor.outpos++)] = ';';
                            Convertor.inpos += 2;
                        }
                        if (sUtil.getChar() == ')') {
                            Convertor.inpos += 1;
                        }
                        type2 = -1;
                        continue;
                    }
                }
                Convertor.inpos = i;
                break;
                case '{': {
                    i = Convertor.inpos;
                    if (type == INTERRUPT || type == VIDEO_START || type == VIDEO_STOP || type == VIDEO_UPDATE
                            || type == VIDEO_EOF || type == PALETTE_INIT || type == MACHINE_INIT || type == MACHINE_STOP
                            || type == DRIVER_INIT) {
                        i3++;
                    }
                }
                Convertor.inpos = i;
                break;
                case '}': {
                    i = Convertor.inpos;
                    if (type == INTERRUPT || type == VIDEO_START || type == VIDEO_STOP || type == VIDEO_UPDATE
                            || type == VIDEO_EOF || type == PALETTE_INIT || type == MACHINE_INIT || type == MACHINE_STOP
                            || type == DRIVER_INIT) {
                        i3--;
                        if (i3 == -1) {
                            sUtil.putString("} };");
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                }
                Convertor.inpos = i;
                break;
            }

            Convertor.outbuf[Convertor.outpos++] = Convertor.inbuf[Convertor.inpos++];//grapse to inputbuffer sto output
        } while (true);
        if (only_once_flag) {
            sUtil.putString("}\r\n");
        }
    }

}
