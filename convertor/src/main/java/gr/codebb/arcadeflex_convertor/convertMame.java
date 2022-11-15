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
    static final int NVRAM_HANDLER = 11;
    static final int READ_HANDLER8 = 12;
    static final int WRITE_HANDLER8 = 13;
    static final int MEMORY_READ8 = 14;
    static final int MEMORY_WRITE8 = 15;
    static final int PORT_READ8 = 16;
    static final int PORT_WRITE8 = 17;
    static final int GFXLAYOUT = 18;
    static final int GFXDECODE = 19;
    static final int AY8910INTF = 20;
    static final int SAMPLESINTF = 21;
    static final int DACINTF = 22;
    static final int TILEINFO = 23;

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
                case 'A': {
                    i = Convertor.inpos;
                    if (type == READ_HANDLER8 || type == WRITE_HANDLER8) {
                        if (sUtil.getToken("AY8910_read_port_0_r")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_read_port_0_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_read_port_1_r")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_read_port_1_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_read_port_2_r")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_read_port_2_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_read_port_3_r")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_read_port_3_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_read_port_4_r")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_read_port_4_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_write_port_0_w")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_write_port_0_w.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_write_port_1_w")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_write_port_1_w.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_write_port_2_w")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_write_port_2_w.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_write_port_3_w")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_write_port_3_w.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_write_port_4_w")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_write_port_4_w.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_control_port_0_w")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_control_port_0_w.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_control_port_1_w")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_control_port_1_w.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_control_port_2_w")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_control_port_2_w.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_control_port_3_w")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_control_port_3_w.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("AY8910_control_port_4_w")) {
                            sUtil.putString((new StringBuilder()).append("AY8910_control_port_4_w.handler").toString());
                            continue;
                        }
                    }
                }
                Convertor.inpos = i;
                break;
                case 'c': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("colorram")) {
                        if (sUtil.parseChar() != '[') {
                            Convertor.inpos = i;
                            break;
                        }
                        Convertor.token[0] = sUtil.parseToken(']');
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ']') {
                            Convertor.inpos = i;
                            break;
                        } else {
                            sUtil.skipSpace();
                            if (sUtil.parseChar() == '=') {
                                int g = Convertor.inpos;
                                if (sUtil.parseChar() == '=') {
                                    Convertor.inpos = i;
                                    break;
                                }
                                Convertor.inpos = g;
                                sUtil.skipSpace();
                                Convertor.token[1] = sUtil.parseToken(';');
                                sUtil.putString((new StringBuilder()).append("colorram.write(").append(Convertor.token[0]).append(",").append(Convertor.token[1]).append(");").toString());
                                Convertor.inpos += 1;
                                break;
                            }
                            sUtil.putString((new StringBuilder()).append("colorram.read(").append(Convertor.token[0]).append(")").toString());
                            Convertor.inpos -= 1;
                            continue;
                        }
                    }
                    if (sUtil.getToken("color_prom")) {
                        if (sUtil.parseChar() != '[') {
                            Convertor.inpos = i;
                            break;
                        }
                        Convertor.token[0] = sUtil.parseToken(']');
                        Convertor.token[0] = Convertor.token[0].replace("->", ".");
                        sUtil.skipSpace();
                        if (sUtil.parseChar() != ']') {
                            Convertor.inpos = i;
                            break;
                        } else {
                            sUtil.skipSpace();
                            if (sUtil.parseChar() == '=') {
                                int g = Convertor.inpos;
                                if (sUtil.parseChar() == '=') {
                                    Convertor.inpos = i;
                                    break;
                                }
                                Convertor.inpos = g;
                                sUtil.skipSpace();
                                Convertor.token[1] = sUtil.parseToken(';');
                                sUtil.putString((new StringBuilder()).append("color_prom.write(").append(Convertor.token[0]).append(",").append(Convertor.token[1]).append(");").toString());
                                Convertor.inpos += 1;
                                break;
                            }
                            sUtil.putString((new StringBuilder()).append("color_prom.read(").append(Convertor.token[0]).append(")").toString());
                            Convertor.inpos -= 1;
                            continue;
                        }
                    }
                }
                Convertor.inpos = i;
                break;
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
                case 'e': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("extern")) {
                        sUtil.skipLine();
                        continue;
                    }
                }
                Convertor.inpos = i;
                break;
                case 'f':
                    i = Convertor.inpos;
                    if (sUtil.getToken("flip_screen")) {
                        sUtil.putString((new StringBuilder()).append("flip_screen()").toString());
                        continue;
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
                case 'i':{
                    i = Convertor.inpos;
                    if (type == READ_HANDLER8) {
                        if (sUtil.getToken("input_port_0_r")) {
                            sUtil.putString((new StringBuilder()).append("input_port_0_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("input_port_1_r")) {
                            sUtil.putString((new StringBuilder()).append("input_port_1_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("input_port_2_r")) {
                            sUtil.putString((new StringBuilder()).append("input_port_2_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("input_port_3_r")) {
                            sUtil.putString((new StringBuilder()).append("input_port_3_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("input_port_4_r")) {
                            sUtil.putString((new StringBuilder()).append("input_port_4_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("input_port_5_r")) {
                            sUtil.putString((new StringBuilder()).append("input_port_5_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("input_port_6_r")) {
                            sUtil.putString((new StringBuilder()).append("input_port_6_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("input_port_7_r")) {
                            sUtil.putString((new StringBuilder()).append("input_port_7_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("input_port_8_r")) {
                            sUtil.putString((new StringBuilder()).append("input_port_8_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("input_port_9_r")) {
                            sUtil.putString((new StringBuilder()).append("input_port_9_r.handler").toString());
                            continue;
                        }
                        if (sUtil.getToken("input_port_10_r")) {
                            sUtil.putString((new StringBuilder()).append("input_port_10_r.handler").toString());
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
                    if (sUtil.getToken("MEMORY_READ_START(") || sUtil.getToken("MEMORY_READ_START (")) {
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.getToken(")")) {
                            sUtil.putString("public static Memory_ReadAddress " + Convertor.token[0] + "[]={\n\t\tnew Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),");
                            type = MEMORY_READ8;
                            i3 = 1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (sUtil.getToken("MEMORY_WRITE_START(") || sUtil.getToken("MEMORY_WRITE_START (")) {
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.getToken(")")) {
                            sUtil.putString("public static Memory_WriteAddress " + Convertor.token[0] + "[]={\n\t\tnew Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),");
                            type = MEMORY_WRITE8;
                            i3 = 1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (!sUtil.getToken("MEMORY_END")) {
                        Convertor.inpos = i;
                        break;
                    }
                    if (type == MEMORY_READ8) {
                        sUtil.putString("\tnew Memory_ReadAddress(MEMPORT_MARKER, 0)\n\t};");
                        type = -1;
                        Convertor.inpos += 1;
                        continue;
                    } else if (type == MEMORY_WRITE8) {
                        sUtil.putString("\tnew Memory_WriteAddress(MEMPORT_MARKER, 0)\n\t};");
                        type = -1;
                        Convertor.inpos += 1;
                        continue;
                    }
                }
                Convertor.inpos = i;
                break;
                case 'N': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("NVRAM_HANDLER")) {
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
                            sUtil.putString("public static NVRAMHandlerPtr nvram_handler_" + Convertor.token[0] + "  = new NVRAMHandlerPtr() { public void handler(mame_file file, int read_or_write)");
                            type = NVRAM_HANDLER;
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
                    if (sUtil.getToken("PORT_READ_START(") || sUtil.getToken("PORT_READ_START (")) {
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.getToken(")")) {
                            sUtil.putString("public static IO_ReadPort " + Convertor.token[0] + "[]={\n\t\tnew IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),");
                            type = PORT_READ8;
                            i3 = 1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (sUtil.getToken("PORT_WRITE_START(") || sUtil.getToken("PORT_WRITE_START (")) {
                        sUtil.skipSpace();
                        Convertor.token[0] = sUtil.parseToken();
                        sUtil.skipSpace();
                        if (sUtil.getToken(")")) {
                            sUtil.putString("public static IO_WritePort " + Convertor.token[0] + "[]={\n\t\tnew IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),");
                            type = PORT_WRITE8;
                            i3 = 1;
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (sUtil.getToken("PORT_END")) {
                        if (type == PORT_READ8) {
                            sUtil.putString("\tnew IO_ReadPort(MEMPORT_MARKER, 0)\n\t};");
                            type = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
                        if (type == PORT_WRITE8) {
                            sUtil.putString("\tnew IO_WritePort(MEMPORT_MARKER, 0)\n\t};");
                            type = -1;
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
                    if (sUtil.getToken("READ_HANDLER")) {
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
                            sUtil.putString("public static ReadHandlerPtr " + Convertor.token[0] + "  = new ReadHandlerPtr() { public int handler(int offset)");
                            type = READ_HANDLER8;
                            i3 = -1;
                            Convertor.inpos += 1;
                            continue;
                        }
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
                        if (sUtil.getToken("NVRAM_HANDLER")) {
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
                                sUtil.putString("public static NVRAMHandlerPtr nvram_handler_" + Convertor.token[0] + "  = new NVRAMHandlerPtr() { public void handler(mame_file file, int read_or_write)");
                                type = NVRAM_HANDLER;
                                i3 = -1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("READ_HANDLER")) {
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
                                sUtil.putString("public static ReadHandlerPtr " + Convertor.token[0] + "  = new ReadHandlerPtr() { public int handler(int offset)");
                                type = READ_HANDLER8;
                                i3 = -1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("WRITE_HANDLER")) {
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
                                sUtil.putString("public static WriteHandlerPtr " + Convertor.token[0] + " = new WriteHandlerPtr() {public void handler(int offset, int data)");
                                type = WRITE_HANDLER8;
                                i3 = -1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("MEMORY_READ_START(") || sUtil.getToken("MEMORY_READ_START (")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.getToken(")")) {
                                sUtil.putString("public static Memory_ReadAddress " + Convertor.token[0] + "[]={\n\t\tnew Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),");
                                type = MEMORY_READ8;
                                i3 = 1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("MEMORY_WRITE_START(") || sUtil.getToken("MEMORY_WRITE_START (")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.getToken(")")) {
                                sUtil.putString("public static Memory_WriteAddress " + Convertor.token[0] + "[]={\n\t\tnew Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),");
                                type = MEMORY_WRITE8;
                                i3 = 1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("PORT_READ_START(") || sUtil.getToken("PORT_READ_START (")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.getToken(")")) {
                                sUtil.putString("public static IO_ReadPort " + Convertor.token[0] + "[]={\n\t\tnew IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),");
                                type = PORT_READ8;
                                i3 = 1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("PORT_WRITE_START(") || sUtil.getToken("PORT_WRITE_START (")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.getToken(")")) {
                                sUtil.putString("public static IO_WritePort " + Convertor.token[0] + "[]={\n\t\tnew IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),");
                                type = PORT_WRITE8;
                                i3 = 1;
                                Convertor.inpos += 1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("void")) {

                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '(') {
                                Convertor.inpos = i;
                                break;
                            }
                            sUtil.skipSpace();
                            if (sUtil.getToken("int tile_index")) {
                                sUtil.skipSpace();
                                if (sUtil.parseChar() != ')') {
                                    Convertor.inpos = i;
                                    break;
                                }
                                if (sUtil.getChar() == ';') {
                                    sUtil.skipLine();
                                    continue;
                                }
                                if (Convertor.token[0].contains("tile")) {
                                    sUtil.putString((new StringBuilder()).append("public static GetTileInfoHandlerPtr ").append(Convertor.token[0]).append(" = new GetTileInfoHandlerPtr() { public void handler(int tile_index) ").toString());
                                    type = TILEINFO;
                                    i3 = -1;
                                    continue;
                                }
                            }
                        }
                    } // end of static but not static struct
                    else {
                        sUtil.skipSpace();
                        if (sUtil.getToken("GfxLayout")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '=') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                sUtil.putString("static GfxLayout " + Convertor.token[0] + " = new GfxLayout");
                                type = GFXLAYOUT;
                                i3 = -1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("GfxDecodeInfo")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '[') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                if (sUtil.parseChar() != ']') {
                                    Convertor.inpos = i;
                                } else {
                                    sUtil.skipSpace();
                                    if (sUtil.parseChar() != '=') {
                                        Convertor.inpos = i;
                                    } else {
                                        sUtil.skipSpace();
                                        sUtil.putString("static GfxDecodeInfo " + Convertor.token[0] + "[] =");
                                        type = GFXDECODE;
                                        i3 = -1;
                                        continue;
                                    }
                                }
                            }
                        }
                        if (sUtil.getToken("AY8910interface")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '=') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                sUtil.putString("static AY8910interface " + Convertor.token[0] + " = new AY8910interface");
                                type = AY8910INTF;
                                i3 = -1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("Samplesinterface")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '=') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                sUtil.putString("static Samplesinterface " + Convertor.token[0] + " = new Samplesinterface");
                                type = SAMPLESINTF;
                                i3 = -1;
                                continue;
                            }
                        }
                        if (sUtil.getToken("DACinterface")) {
                            sUtil.skipSpace();
                            Convertor.token[0] = sUtil.parseToken();
                            sUtil.skipSpace();
                            if (sUtil.parseChar() != '=') {
                                Convertor.inpos = i;
                            } else {
                                sUtil.skipSpace();
                                sUtil.putString("static DACinterface " + Convertor.token[0] + " = new DACinterface");
                                type = DACINTF;
                                i3 = -1;
                                continue;
                            }
                        }
                    }
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
                case 'W': {
                    i = Convertor.inpos;
                    if (sUtil.getToken("WRITE_HANDLER")) {
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
                            sUtil.putString("public static WriteHandlerPtr " + Convertor.token[0] + " = new WriteHandlerPtr() {public void handler(int offset, int data)");
                            type = WRITE_HANDLER8;
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
                            || type == DRIVER_INIT || type == NVRAM_HANDLER || type == READ_HANDLER8 || type == WRITE_HANDLER8 || type == TILEINFO) {
                        i3++;
                    }
                    if (type == MEMORY_READ8) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 2) {
                            sUtil.putString("new Memory_ReadAddress(");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == MEMORY_WRITE8) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 2) {
                            sUtil.putString("new Memory_WriteAddress(");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == PORT_READ8) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 2) {
                            sUtil.putString("new IO_ReadPort(");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == PORT_WRITE8) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 2) {
                            sUtil.putString("new IO_WritePort(");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == GFXLAYOUT) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = '(';
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 4) || (insideagk[0] == 5) || (insideagk[0] == 6) || (insideagk[0] == 7))) {
                            sUtil.putString("new int[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == GFXDECODE) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 1) {
                            sUtil.putString("new GfxDecodeInfo(");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == AY8910INTF) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = '(';
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 2))) {
                            sUtil.putString("new int[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 3))) {
                            sUtil.putString("new ReadHandlerPtr[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 4))) {
                            sUtil.putString("new ReadHandlerPtr[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 5))) {
                            sUtil.putString("new WriteHandlerPtr[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1) || (insideagk[0] == 6))) {
                            sUtil.putString("new WriteHandlerPtr[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == SAMPLESINTF) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = '(';
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == DACINTF) {
                        i3++;
                        insideagk[i3] = 0;
                        if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = '(';
                            Convertor.inpos += 1;
                            continue;
                        }
                        if ((i3 == 1) && ((insideagk[0] == 1))) {
                            sUtil.putString("new int[] {");
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                }
                Convertor.inpos = i;
                break;
                case '}': {
                    i = Convertor.inpos;
                    if (type == INTERRUPT || type == VIDEO_START || type == VIDEO_STOP || type == VIDEO_UPDATE
                            || type == VIDEO_EOF || type == PALETTE_INIT || type == MACHINE_INIT || type == MACHINE_STOP
                            || type == DRIVER_INIT || type == NVRAM_HANDLER || type == READ_HANDLER8 || type == WRITE_HANDLER8 || type == TILEINFO) {
                        i3--;
                        if (i3 == -1) {
                            sUtil.putString("} };");
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                    if ((type == MEMORY_READ8) || type == MEMORY_WRITE8 || type == PORT_READ8 || type == PORT_WRITE8) {
                        i3--;
                        if (i3 == 0) {
                            type = -1;
                        } else if (i3 == 1) {
                            Convertor.outbuf[(Convertor.outpos++)] = ')';
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == GFXLAYOUT) {
                        i3--;
                        if (i3 == -1) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                    if (type == GFXDECODE) {
                        i3--;
                        if (i3 == -1) {
                            type = -1;
                        } else if (i3 == 0) {
                            Convertor.outbuf[(Convertor.outpos++)] = ')';
                            Convertor.inpos += 1;
                            continue;
                        }
                    }
                    if (type == AY8910INTF) {
                        i3--;
                        if (i3 == -1) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                    if (type == SAMPLESINTF) {
                        i3--;
                        if (i3 == -1) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                    if (type == DACINTF) {
                        i3--;
                        if (i3 == -1) {
                            Convertor.outbuf[(Convertor.outpos++)] = 41;
                            Convertor.inpos += 1;
                            type = -1;
                            continue;
                        }
                    }
                }
                Convertor.inpos = i;
                break;
                case ',': {
                    if (type == GFXLAYOUT || type == GFXDECODE || type == AY8910INTF || type == DACINTF) {
                        if ((type != -1)) {
                            if (i3 != -1) {
                                insideagk[i3] += 1;
                            }
                        }
                    }
                }
                break;
            }

            Convertor.outbuf[Convertor.outpos++] = Convertor.inbuf[Convertor.inpos++];//grapse to inputbuffer sto output
        } while (true);
        if (only_once_flag) {
            sUtil.putString("}\r\n");
        }
    }

}
