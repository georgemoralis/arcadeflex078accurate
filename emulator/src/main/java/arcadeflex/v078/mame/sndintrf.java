/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

//generic imports
import static arcadeflex.v078.generic.funcPtr.*;
import static arcadeflex.v078.mame.driverH.*;
import static arcadeflex.v078.mame.mame.*;
import static arcadeflex.v078.mame.sndintrfH.*;
//mame improts
import static arcadeflex.v078.mame.timer.*;
import static arcadeflex.v078.mame.timerH.*;
//platform imports
import static arcadeflex.v078.platform.config.*;
//sound imports
import static arcadeflex.v078.sound.mixer.*;
import static arcadeflex.v078.sound.streams.*;
import arcadeflex.v078.sound.customSound;
import arcadeflex.v078.sound.dummySound;

public class sndintrf {

    static int cleared_value = 0x00;

    static int latch, read_debug;

    public static TimerCallbackHandlerPtr soundlatch_callback = new TimerCallbackHandlerPtr() {
        public void handler(int param) {
            if (read_debug == 0 && latch != param) {
                logerror("Warning: sound latch written before being read. Previous: %02x, new: %02x\n", latch, param);
            }
            latch = param;
            read_debug = 0;
        }
    };
    public static WriteHandlerPtr soundlatch_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* make all the CPUs synchronize, and only AFTER that write the new command to the latch */
            timer_set(TIME_NOW, data, soundlatch_callback);
        }
    };
    /*TODO*///
/*TODO*///WRITE16_HANDLER( soundlatch_word_w )
/*TODO*///{
/*TODO*///	static data16_t word;
/*TODO*///	COMBINE_DATA(&word);
/*TODO*///
/*TODO*///	/* make all the CPUs synchronize, and only AFTER that write the new command to the latch */
/*TODO*///	timer_set(TIME_NOW,word,soundlatch_callback);
/*TODO*///}
/*TODO*///
    public static ReadHandlerPtr soundlatch_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            read_debug = 1;
            return latch;
        }
    };
    /*TODO*///
/*TODO*///READ16_HANDLER( soundlatch_word_r )
/*TODO*///{
/*TODO*///	read_debug = 1;
/*TODO*///	return latch;
/*TODO*///}
/*TODO*///
    public static WriteHandlerPtr soundlatch_clear_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            latch = cleared_value;
        }
    };

    static int latch2, read_debug2;

    public static TimerCallbackHandlerPtr soundlatch2_callback = new TimerCallbackHandlerPtr() {
        public void handler(int param) {
            if (read_debug2 == 0 && latch2 != param) {
                logerror("Warning: sound latch 2 written before being read. Previous: %02x, new: %02x\n", latch2, param);
            }
            latch2 = param;
            read_debug2 = 0;
        }
    };

    public static WriteHandlerPtr soundlatch2_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* make all the CPUs synchronize, and only AFTER that write the new command to the latch */
            timer_set(TIME_NOW, data, soundlatch2_callback);
        }
    };
    /*TODO*///
/*TODO*///WRITE16_HANDLER( soundlatch2_word_w )
/*TODO*///{
/*TODO*///	static data16_t word;
/*TODO*///	COMBINE_DATA(&word);
/*TODO*///
/*TODO*///	/* make all the CPUs synchronize, and only AFTER that write the new command to the latch */
/*TODO*///	timer_set(TIME_NOW,word,soundlatch2_callback);
/*TODO*///}
/*TODO*///
    public static ReadHandlerPtr soundlatch2_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            read_debug2 = 1;
            return latch2;
        }
    };
    /*TODO*///
/*TODO*///READ16_HANDLER( soundlatch2_word_r )
/*TODO*///{
/*TODO*///	read_debug2 = 1;
/*TODO*///	return latch2;
/*TODO*///}
/*TODO*///
    public static WriteHandlerPtr soundlatch2_clear_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            latch2 = cleared_value;
        }
    };

    static int latch3, read_debug3;

    public static TimerCallbackHandlerPtr soundlatch3_callback = new TimerCallbackHandlerPtr() {
        public void handler(int param) {
            if (read_debug3 == 0 && latch3 != param) {
                logerror("Warning: sound latch 3 written before being read. Previous: %02x, new: %02x\n", latch3, param);
            }
            latch3 = param;
            read_debug3 = 0;
        }
    };

    public static WriteHandlerPtr soundlatch3_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* make all the CPUs synchronize, and only AFTER that write the new command to the latch */
            timer_set(TIME_NOW, data, soundlatch3_callback);
        }
    };
    /*TODO*///
/*TODO*///WRITE16_HANDLER( soundlatch3_word_w )
/*TODO*///{
/*TODO*///	static data16_t word;
/*TODO*///	COMBINE_DATA(&word);
/*TODO*///
/*TODO*///	/* make all the CPUs synchronize, and only AFTER that write the new command to the latch */
/*TODO*///	timer_set(TIME_NOW,word,soundlatch3_callback);
/*TODO*///}

    public static ReadHandlerPtr soundlatch3_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            read_debug3 = 1;
            return latch3;
        }
    };
    /*TODO*///
/*TODO*///READ16_HANDLER( soundlatch3_word_r )
/*TODO*///{
/*TODO*///	read_debug3 = 1;
/*TODO*///	return latch3;
/*TODO*///}
/*TODO*///
    public static WriteHandlerPtr soundlatch3_clear_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            latch3 = cleared_value;
        }
    };

    static int latch4, read_debug4;

    public static TimerCallbackHandlerPtr soundlatch4_callback = new TimerCallbackHandlerPtr() {
        public void handler(int param) {
            if (read_debug4 == 0 && latch4 != param) {
                logerror("Warning: sound latch 4 written before being read. Previous: %02x, new: %02x\n", latch2, param);
            }
            latch4 = param;
            read_debug4 = 0;
        }
    };

    public static WriteHandlerPtr soundlatch4_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* make all the CPUs synchronize, and only AFTER that write the new command to the latch */
            timer_set(TIME_NOW, data, soundlatch4_callback);
        }
    };
    /*TODO*///
/*TODO*///WRITE16_HANDLER( soundlatch4_word_w )
/*TODO*///{
/*TODO*///	static data16_t word;
/*TODO*///	COMBINE_DATA(&word);
/*TODO*///
/*TODO*///	/* make all the CPUs synchronize, and only AFTER that write the new command to the latch */
/*TODO*///	timer_set(TIME_NOW,word,soundlatch4_callback);
/*TODO*///}
/*TODO*///
    public static ReadHandlerPtr soundlatch4_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            read_debug4 = 1;
            return latch4;
        }
    };
    /*TODO*///
/*TODO*///READ16_HANDLER( soundlatch4_word_r )
/*TODO*///{
/*TODO*///	read_debug4 = 1;
/*TODO*///	return latch4;
/*TODO*///}
/*TODO*///
    public static WriteHandlerPtr soundlatch4_clear_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            latch4 = cleared_value;
        }
    };

    public static void soundlatch_setclearedvalue(int value) {
        cleared_value = value;
    }

    /**
     * *************************************************************************
     *
     *
     *
     **************************************************************************
     */
    static mame_timer sound_update_timer;
    static double refresh_period;
    static double refresh_period_inv;

    public static abstract class snd_interface {

        public int sound_num;
        public String name;

        public abstract int chips_num(MachineSound msound);/* returns number of chips if applicable */
        public abstract int chips_clock(MachineSound msound);/* returns chips clock if applicable */
        public abstract int start(MachineSound msound);/* starts sound emulation */
        public abstract void stop();/* stops sound emulation */
        public abstract void update();/* updates emulation once per frame if necessary */
        public abstract void reset();/* resets sound emulation */
    }

    /*TODO*///#if (HAS_DAC)
/*TODO*///int DAC_num(const struct MachineSound *msound) { return ((struct DACinterface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_ADPCM)
/*TODO*///int ADPCM_num(const struct MachineSound *msound) { return ((struct ADPCMinterface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_OKIM6295)
/*TODO*///int OKIM6295_num(const struct MachineSound *msound) { return ((struct OKIM6295interface*)msound->sound_interface)->num; }
/*TODO*///int OKIM6295_clock(const struct MachineSound *msound) { return ((struct OKIM6295interface*)msound->sound_interface)->frequency[0]; }
/*TODO*///#endif
/*TODO*///#if (HAS_MSM5205)
/*TODO*///int MSM5205_num(const struct MachineSound *msound) { return ((struct MSM5205interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_MSM5232)
/*TODO*///int MSM5232_num(const struct MachineSound *msound) { return ((struct MSM5232interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_HC55516)
/*TODO*///int HC55516_num(const struct MachineSound *msound) { return ((struct hc55516_interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_K007232)
/*TODO*///int K007232_clock(const struct MachineSound *msound) { return ((struct K007232_interface*)msound->sound_interface)->baseclock; }
/*TODO*///int K007232_num(const struct MachineSound *msound) { return ((struct K007232_interface*)msound->sound_interface)->num_chips; }
/*TODO*///#endif
/*TODO*///#if (HAS_AY8910)
/*TODO*///int AY8910_clock(const struct MachineSound *msound) { return ((struct AY8910interface*)msound->sound_interface)->baseclock; }
/*TODO*///int AY8910_num(const struct MachineSound *msound) { return ((struct AY8910interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_YM2203)
/*TODO*///int YM2203_clock(const struct MachineSound *msound) { return ((struct YM2203interface*)msound->sound_interface)->baseclock; }
/*TODO*///int YM2203_num(const struct MachineSound *msound) { return ((struct YM2203interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_YM2413)
/*TODO*///int YM2413_clock(const struct MachineSound *msound) { return ((struct YM2413interface*)msound->sound_interface)->baseclock; }
/*TODO*///int YM2413_num(const struct MachineSound *msound) { return ((struct YM2413interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_YM2608)
/*TODO*///int YM2608_clock(const struct MachineSound *msound) { return ((struct YM2608interface*)msound->sound_interface)->baseclock; }
/*TODO*///int YM2608_num(const struct MachineSound *msound) { return ((struct YM2608interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_YM2610)
/*TODO*///int YM2610_clock(const struct MachineSound *msound) { return ((struct YM2610interface*)msound->sound_interface)->baseclock; }
/*TODO*///int YM2610_num(const struct MachineSound *msound) { return ((struct YM2610interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_YM2612 || HAS_YM3438)
/*TODO*///int YM2612_clock(const struct MachineSound *msound) { return ((struct YM2612interface*)msound->sound_interface)->baseclock; }
/*TODO*///int YM2612_num(const struct MachineSound *msound) { return ((struct YM2612interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_POKEY)
/*TODO*///int POKEY_clock(const struct MachineSound *msound) { return ((struct POKEYinterface*)msound->sound_interface)->baseclock; }
/*TODO*///int POKEY_num(const struct MachineSound *msound) { return ((struct POKEYinterface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_YM3812)
/*TODO*///int YM3812_clock(const struct MachineSound *msound) { return ((struct YM3812interface*)msound->sound_interface)->baseclock; }
/*TODO*///int YM3812_num(const struct MachineSound *msound) { return ((struct YM3812interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_YM3526)
/*TODO*///int YM3526_clock(const struct MachineSound *msound) { return ((struct YM3526interface*)msound->sound_interface)->baseclock; }
/*TODO*///int YM3526_num(const struct MachineSound *msound) { return ((struct YM3526interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_Y8950)
/*TODO*///int Y8950_clock(const struct MachineSound *msound) { return ((struct Y8950interface*)msound->sound_interface)->baseclock; }
/*TODO*///int Y8950_num(const struct MachineSound *msound) { return ((struct Y8950interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_YMZ280B)
/*TODO*///int YMZ280B_clock(const struct MachineSound *msound) { return ((struct YMZ280Binterface*)msound->sound_interface)->baseclock[0]; }
/*TODO*///int YMZ280B_num(const struct MachineSound *msound) { return ((struct YMZ280Binterface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_VLM5030)
/*TODO*///int VLM5030_clock(const struct MachineSound *msound) { return ((struct VLM5030interface*)msound->sound_interface)->baseclock; }
/*TODO*///#endif
/*TODO*///#if (HAS_TMS36XX)
/*TODO*///int TMS36XX_num(const struct MachineSound *msound) { return ((struct TMS36XXinterface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_TMS5110)
/*TODO*///int TMS5110_clock(const struct MachineSound *msound) { return ((struct TMS5110interface*)msound->sound_interface)->baseclock; }
/*TODO*///#endif
/*TODO*///#if (HAS_TMS5220)
/*TODO*///int TMS5220_clock(const struct MachineSound *msound) { return ((struct TMS5220interface*)msound->sound_interface)->baseclock; }
/*TODO*///#endif
/*TODO*///#if (HAS_YM2151 || HAS_YM2151_ALT)
/*TODO*///int YM2151_clock(const struct MachineSound *msound) { return ((struct YM2151interface*)msound->sound_interface)->baseclock; }
/*TODO*///int YM2151_num(const struct MachineSound *msound) { return ((struct YM2151interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_NES)
/*TODO*///int NES_num(const struct MachineSound *msound) { return ((struct NESinterface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_SN76477)
/*TODO*///int SN76477_num(const struct MachineSound *msound) { return ((struct SN76477interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_SN76496)
/*TODO*///int SN76496_clock(const struct MachineSound *msound) { return ((struct SN76496interface*)msound->sound_interface)->baseclock[0]; }
/*TODO*///int SN76496_num(const struct MachineSound *msound) { return ((struct SN76496interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_MSM5205)
/*TODO*///int MSM5205_clock(const struct MachineSound *msound) { return ((struct MSM5205interface*)msound->sound_interface)->baseclock; }
/*TODO*///#endif
/*TODO*///#if (HAS_MSM5232)
/*TODO*///int MSM5232_clock(const struct MachineSound *msound) { return ((struct MSM5232interface*)msound->sound_interface)->baseclock; }
/*TODO*///#endif
/*TODO*///#if (HAS_ASTROCADE)
/*TODO*///int ASTROCADE_clock(const struct MachineSound *msound) { return ((struct astrocade_interface*)msound->sound_interface)->baseclock; }
/*TODO*///int ASTROCADE_num(const struct MachineSound *msound) { return ((struct astrocade_interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_K051649)
/*TODO*///int K051649_clock(const struct MachineSound *msound) { return ((struct k051649_interface*)msound->sound_interface)->master_clock; }
/*TODO*///#endif
/*TODO*///#if (HAS_K053260)
/*TODO*///int K053260_clock(const struct MachineSound *msound) { return ((struct K053260_interface*)msound->sound_interface)->clock[0]; }
/*TODO*///int K053260_num(const struct MachineSound *msound) { return ((struct K053260_interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_K054539)
/*TODO*///int K054539_clock(const struct MachineSound *msound) { return ((struct K054539interface*)msound->sound_interface)->clock; }
/*TODO*///int K054539_num(const struct MachineSound *msound) { return ((struct K054539interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_CEM3394)
/*TODO*///int cem3394_num(const struct MachineSound *msound) { return ((struct cem3394_interface*)msound->sound_interface)->numchips; }
/*TODO*///#endif
/*TODO*///#if (HAS_QSOUND)
/*TODO*///int qsound_clock(const struct MachineSound *msound) { return ((struct QSound_interface*)msound->sound_interface)->clock; }
/*TODO*///#endif
/*TODO*///#if (HAS_SAA1099)
/*TODO*///int saa1099_num(const struct MachineSound *msound) { return ((struct SAA1099_interface*)msound->sound_interface)->numchips; }
/*TODO*///#endif
/*TODO*///#if (HAS_IREMGA20)
/*TODO*///int iremga20_clock(const struct MachineSound *msound) { return ((struct IremGA20_interface*)msound->sound_interface)->clock; }
/*TODO*///#endif
/*TODO*///#if (HAS_ES5505)
/*TODO*///int ES5505_clock(const struct MachineSound *msound) { return ((struct ES5505interface*)msound->sound_interface)->baseclock[0]; }
/*TODO*///int ES5505_num(const struct MachineSound *msound) { return ((struct ES5505interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_ES5506)
/*TODO*///int ES5506_clock(const struct MachineSound *msound) { return ((struct ES5506interface*)msound->sound_interface)->baseclock[0]; }
/*TODO*///int ES5506_num(const struct MachineSound *msound) { return ((struct ES5506interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_BSMT2000)
/*TODO*///int BSMT2000_clock(const struct MachineSound *msound) { return ((struct BSMT2000interface*)msound->sound_interface)->baseclock[0]; }
/*TODO*///int BSMT2000_num(const struct MachineSound *msound) { return ((struct BSMT2000interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_YMF262)
/*TODO*///int YMF262_clock(const struct MachineSound *msound) { return ((struct YMF262interface*)msound->sound_interface)->baseclock; }
/*TODO*///int YMF262_num(const struct MachineSound *msound) { return ((struct YMF262interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_YMF278B)
/*TODO*///int YMF278B_clock(const struct MachineSound *msound) { return ((struct YMF278B_interface*)msound->sound_interface)->clock[0]; }
/*TODO*///int YMF278B_num(const struct MachineSound *msound) { return ((struct YMF278B_interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_X1_010)
/*TODO*///int seta_clock(const struct MachineSound *msound) { return ((struct x1_010_interface*)msound->sound_interface)->clock; }
/*TODO*///#endif
/*TODO*///#if (HAS_MULTIPCM)
/*TODO*///int MultiPCM_num(const struct MachineSound *msound) { return ((struct MultiPCM_interface*)msound->sound_interface)->chips; }
/*TODO*///#endif
/*TODO*///#if (HAS_C6280)
/*TODO*///int c6280_clock(const struct MachineSound *msound) { return ((struct C6280_interface*)msound->sound_interface)->clock[0]; }
/*TODO*///int c6280_num(const struct MachineSound *msound) { return ((struct C6280_interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_TIA)
/*TODO*///int TIA_clock(const struct MachineSound *msound) { return ((struct TIAinterface*)msound->sound_interface)->baseclock; }
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///#if (HAS_BEEP)
/*TODO*///int beep_num(const struct MachineSound *msound) { return ((struct beep_interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_SPEAKER)
/*TODO*///int speaker_num(const struct MachineSound *msound) { return ((struct Speaker_interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#if (HAS_WAVE)
/*TODO*///int wave_num(const struct MachineSound *msound) { return ((struct Wave_interface*)msound->sound_interface)->num; }
/*TODO*///#endif
/*TODO*///#endif
/*TODO*///
    public static snd_interface sndintf[]
            = {
                new dummySound(),
                new customSound(),
                new dummySound(),
            /*TODO*///#if (HAS_SAMPLES)
/*TODO*///    {
/*TODO*///		SOUND_SAMPLES,
/*TODO*///		"Samples",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		samples_sh_start,
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_DAC)
/*TODO*///    {
/*TODO*///		SOUND_DAC,
/*TODO*///		"DAC",
/*TODO*///		DAC_num,
/*TODO*///		0,
/*TODO*///		DAC_sh_start,
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_DISCRETE)
/*TODO*///    {
/*TODO*///		SOUND_DISCRETE,
/*TODO*///		"Discrete Components",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		discrete_sh_start,
/*TODO*///		discrete_sh_stop,
/*TODO*///		0,
/*TODO*///		discrete_sh_reset
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_AY8910)
/*TODO*///    {
/*TODO*///		SOUND_AY8910,
/*TODO*///		"AY-3-8910",
/*TODO*///		AY8910_num,
/*TODO*///		AY8910_clock,
/*TODO*///		AY8910_sh_start,
/*TODO*///		AY8910_sh_stop,
/*TODO*///		0,
/*TODO*///		AY8910_sh_reset
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YM2203)
/*TODO*///    {
/*TODO*///		SOUND_YM2203,
/*TODO*///		"YM2203",
/*TODO*///		YM2203_num,
/*TODO*///		YM2203_clock,
/*TODO*///		YM2203_sh_start,
/*TODO*///		YM2203_sh_stop,
/*TODO*///		0,
/*TODO*///		YM2203_sh_reset
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YM2151 || HAS_YM2151_ALT)
/*TODO*///    {
/*TODO*///		SOUND_YM2151,
/*TODO*///		"YM2151",
/*TODO*///		YM2151_num,
/*TODO*///		YM2151_clock,
/*TODO*///		YM2151_sh_start,
/*TODO*///		YM2151_sh_stop,
/*TODO*///		0,
/*TODO*///		YM2151_sh_reset
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YM2608)
/*TODO*///    {
/*TODO*///		SOUND_YM2608,
/*TODO*///		"YM2608",
/*TODO*///		YM2608_num,
/*TODO*///		YM2608_clock,
/*TODO*///		YM2608_sh_start,
/*TODO*///		YM2608_sh_stop,
/*TODO*///		0,
/*TODO*///		YM2608_sh_reset
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YM2610)
/*TODO*///    {
/*TODO*///		SOUND_YM2610,
/*TODO*///		"YM2610",
/*TODO*///		YM2610_num,
/*TODO*///		YM2610_clock,
/*TODO*///		YM2610_sh_start,
/*TODO*///		YM2610_sh_stop,
/*TODO*///		0,
/*TODO*///		YM2610_sh_reset
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YM2610B)
/*TODO*///    {
/*TODO*///		SOUND_YM2610B,
/*TODO*///		"YM2610B",
/*TODO*///		YM2610_num,
/*TODO*///		YM2610_clock,
/*TODO*///		YM2610B_sh_start,
/*TODO*///		YM2610_sh_stop,
/*TODO*///		0,
/*TODO*///		YM2610_sh_reset
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YM2612)
/*TODO*///    {
/*TODO*///		SOUND_YM2612,
/*TODO*///		"YM2612",
/*TODO*///		YM2612_num,
/*TODO*///		YM2612_clock,
/*TODO*///		YM2612_sh_start,
/*TODO*///		YM2612_sh_stop,
/*TODO*///		0,
/*TODO*///		YM2612_sh_reset
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YM3438)
/*TODO*///    {
/*TODO*///		SOUND_YM3438,
/*TODO*///		"YM3438",
/*TODO*///		YM2612_num,
/*TODO*///		YM2612_clock,
/*TODO*///		YM2612_sh_start,
/*TODO*///		YM2612_sh_stop,
/*TODO*///		0,
/*TODO*///		YM2612_sh_reset
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YM2413)
/*TODO*///    {
/*TODO*///		SOUND_YM2413,
/*TODO*///		"YM2413",
/*TODO*///		YM2413_num,
/*TODO*///		YM2413_clock,
/*TODO*///		YM2413_sh_start,
/*TODO*///		YM2413_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YM3812)
/*TODO*///    {
/*TODO*///		SOUND_YM3812,
/*TODO*///		"YM3812",
/*TODO*///		YM3812_num,
/*TODO*///		YM3812_clock,
/*TODO*///		YM3812_sh_start,
/*TODO*///		YM3812_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YM3526)
/*TODO*///    {
/*TODO*///		SOUND_YM3526,
/*TODO*///		"YM3526",
/*TODO*///		YM3526_num,
/*TODO*///		YM3526_clock,
/*TODO*///		YM3526_sh_start,
/*TODO*///		YM3526_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YMZ280B)
/*TODO*///    {
/*TODO*///		SOUND_YMZ280B,
/*TODO*///		"YMZ280B",
/*TODO*///		YMZ280B_num,
/*TODO*///		YMZ280B_clock,
/*TODO*///		YMZ280B_sh_start,
/*TODO*///		YMZ280B_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_Y8950)
/*TODO*///	{
/*TODO*///		SOUND_Y8950,
/*TODO*///		"Y8950",	/* (MSX-AUDIO) */
/*TODO*///		Y8950_num,
/*TODO*///		Y8950_clock,
/*TODO*///		Y8950_sh_start,
/*TODO*///		Y8950_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_SN76477)
/*TODO*///    {
/*TODO*///		SOUND_SN76477,
/*TODO*///		"SN76477",
/*TODO*///		SN76477_num,
/*TODO*///		0,
/*TODO*///		SN76477_sh_start,
/*TODO*///		SN76477_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_SN76496)
/*TODO*///    {
/*TODO*///		SOUND_SN76496,
/*TODO*///		"SN76496",
/*TODO*///		SN76496_num,
/*TODO*///		SN76496_clock,
/*TODO*///		SN76496_sh_start,
/*TODO*///        0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_POKEY)
/*TODO*///    {
/*TODO*///		SOUND_POKEY,
/*TODO*///		"Pokey",
/*TODO*///		POKEY_num,
/*TODO*///		POKEY_clock,
/*TODO*///		pokey_sh_start,
/*TODO*///		pokey_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_NES)
/*TODO*///    {
/*TODO*///		SOUND_NES,
/*TODO*///		"Nintendo",
/*TODO*///		NES_num,
/*TODO*///		0,
/*TODO*///		NESPSG_sh_start,
/*TODO*///		NESPSG_sh_stop,
/*TODO*///		NESPSG_sh_update,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_ASTROCADE)
/*TODO*///    {
/*TODO*///		SOUND_ASTROCADE,
/*TODO*///		"Astrocade",
/*TODO*///		ASTROCADE_num,
/*TODO*///		ASTROCADE_clock,
/*TODO*///		astrocade_sh_start,
/*TODO*///		astrocade_sh_stop,
/*TODO*///		astrocade_sh_update,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_NAMCO)
/*TODO*///    {
/*TODO*///		SOUND_NAMCO,
/*TODO*///		"Namco",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		namco_sh_start,
/*TODO*///		namco_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_NAMCONA)
/*TODO*///    {
/*TODO*///		SOUND_NAMCONA,
/*TODO*///		"Namco NA",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		NAMCONA_sh_start,
/*TODO*///		NAMCONA_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_TMS36XX)
/*TODO*///    {
/*TODO*///		SOUND_TMS36XX,
/*TODO*///		"TMS36XX",
/*TODO*///		TMS36XX_num,
/*TODO*///        0,
/*TODO*///		tms36xx_sh_start,
/*TODO*///		tms36xx_sh_stop,
/*TODO*///		tms36xx_sh_update,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_TMS5110)
/*TODO*///    {
/*TODO*///		SOUND_TMS5110,
/*TODO*///		"TMS5110",
/*TODO*///		0,
/*TODO*///		TMS5110_clock,
/*TODO*///		tms5110_sh_start,
/*TODO*///		tms5110_sh_stop,
/*TODO*///		tms5110_sh_update,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_TMS5220)
/*TODO*///    {
/*TODO*///		SOUND_TMS5220,
/*TODO*///		"TMS5220",
/*TODO*///		0,
/*TODO*///		TMS5220_clock,
/*TODO*///		tms5220_sh_start,
/*TODO*///		tms5220_sh_stop,
/*TODO*///		tms5220_sh_update,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_VLM5030)
/*TODO*///    {
/*TODO*///		SOUND_VLM5030,
/*TODO*///		"VLM5030",
/*TODO*///		0,
/*TODO*///		VLM5030_clock,
/*TODO*///		VLM5030_sh_start,
/*TODO*///		VLM5030_sh_stop,
/*TODO*///		VLM5030_sh_update,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_ADPCM)
/*TODO*///    {
/*TODO*///		SOUND_ADPCM,
/*TODO*///		"ADPCM",
/*TODO*///		ADPCM_num,
/*TODO*///		0,
/*TODO*///		ADPCM_sh_start,
/*TODO*///		ADPCM_sh_stop,
/*TODO*///		ADPCM_sh_update,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_OKIM6295)
/*TODO*///    {
/*TODO*///		SOUND_OKIM6295,
/*TODO*///		"MSM6295",
/*TODO*///		OKIM6295_num,
/*TODO*///		OKIM6295_clock,
/*TODO*///		OKIM6295_sh_start,
/*TODO*///		OKIM6295_sh_stop,
/*TODO*///		OKIM6295_sh_update,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_MSM5205)
/*TODO*///    {
/*TODO*///		SOUND_MSM5205,
/*TODO*///		"MSM5205",
/*TODO*///		MSM5205_num,
/*TODO*///		MSM5205_clock,
/*TODO*///		MSM5205_sh_start,
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		MSM5205_sh_reset,
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_MSM5232)
/*TODO*///    {
/*TODO*///		SOUND_MSM5232,
/*TODO*///		"MSM5232",
/*TODO*///		MSM5232_num,
/*TODO*///		MSM5232_clock,
/*TODO*///		MSM5232_sh_start,
/*TODO*///		MSM5232_sh_stop,
/*TODO*///		0,
/*TODO*///		MSM5232_sh_reset,
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_UPD7759)
/*TODO*///    {
/*TODO*///		SOUND_UPD7759,
/*TODO*///		"uPD7759",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		UPD7759_sh_start,
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_HC55516)
/*TODO*///    {
/*TODO*///		SOUND_HC55516,
/*TODO*///		"HC55516",
/*TODO*///		HC55516_num,
/*TODO*///		0,
/*TODO*///		hc55516_sh_start,
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_K005289)
/*TODO*///    {
/*TODO*///		SOUND_K005289,
/*TODO*///		"005289",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		K005289_sh_start,
/*TODO*///		K005289_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_K007232)
/*TODO*///    {
/*TODO*///		SOUND_K007232,
/*TODO*///		"007232",
/*TODO*///		K007232_num,
/*TODO*///		K007232_clock,
/*TODO*///		K007232_sh_start,
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_K051649)
/*TODO*///    {
/*TODO*///		SOUND_K051649,
/*TODO*///		"051649",
/*TODO*///		0,
/*TODO*///		K051649_clock,
/*TODO*///		K051649_sh_start,
/*TODO*///		K051649_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_K053260)
/*TODO*///    {
/*TODO*///		SOUND_K053260,
/*TODO*///		"053260",
/*TODO*///		K053260_num,
/*TODO*///		K053260_clock,
/*TODO*///		K053260_sh_start,
/*TODO*///		K053260_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_K054539)
/*TODO*///    {
/*TODO*///		SOUND_K054539,
/*TODO*///		"054539",
/*TODO*///		K054539_num,
/*TODO*///		K054539_clock,
/*TODO*///		K054539_sh_start,
/*TODO*///		K054539_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_SEGAPCM)
/*TODO*///	{
/*TODO*///		SOUND_SEGAPCM,
/*TODO*///		"Sega PCM",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		SEGAPCM_sh_start,
/*TODO*///		SEGAPCM_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_RF5C68)
/*TODO*///	{
/*TODO*///		SOUND_RF5C68,
/*TODO*///		"RF5C68",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		RF5C68_sh_start,
/*TODO*///		RF5C68_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_CEM3394)
/*TODO*///	{
/*TODO*///		SOUND_CEM3394,
/*TODO*///		"CEM3394",
/*TODO*///		cem3394_num,
/*TODO*///		0,
/*TODO*///		cem3394_sh_start,
/*TODO*///		cem3394_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_C140)
/*TODO*///	{
/*TODO*///		SOUND_C140,
/*TODO*///		"C140",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		C140_sh_start,
/*TODO*///		C140_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_QSOUND)
/*TODO*///	{
/*TODO*///		SOUND_QSOUND,
/*TODO*///		"QSound",
/*TODO*///		0,
/*TODO*///		qsound_clock,
/*TODO*///		qsound_sh_start,
/*TODO*///		qsound_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_SAA1099)
/*TODO*///	{
/*TODO*///		SOUND_SAA1099,
/*TODO*///		"SAA1099",
/*TODO*///		saa1099_num,
/*TODO*///		0,
/*TODO*///		saa1099_sh_start,
/*TODO*///		saa1099_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_IREMGA20)
/*TODO*///	{
/*TODO*///		SOUND_IREMGA20,
/*TODO*///		"GA20",
/*TODO*///		0,
/*TODO*///		iremga20_clock,
/*TODO*///		IremGA20_sh_start,
/*TODO*///		IremGA20_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_ES5505)
/*TODO*///	{
/*TODO*///		SOUND_ES5505,
/*TODO*///		"ES5505",
/*TODO*///		ES5505_num,
/*TODO*///		ES5505_clock,
/*TODO*///		ES5505_sh_start,
/*TODO*///		ES5505_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_ES5506)
/*TODO*///	{
/*TODO*///		SOUND_ES5506,
/*TODO*///		"ES5506",
/*TODO*///		ES5506_num,
/*TODO*///		ES5506_clock,
/*TODO*///		ES5506_sh_start,
/*TODO*///		ES5506_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_BSMT2000)
/*TODO*///	{
/*TODO*///		SOUND_BSMT2000,
/*TODO*///		"BSMT2000",
/*TODO*///		BSMT2000_num,
/*TODO*///		BSMT2000_clock,
/*TODO*///		BSMT2000_sh_start,
/*TODO*///		BSMT2000_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YMF262)
/*TODO*///	{
/*TODO*///		 SOUND_YMF262,
/*TODO*///		 "YMF262",
/*TODO*///		 YMF262_num,
/*TODO*///		 YMF262_clock,
/*TODO*///		 YMF262_sh_start,
/*TODO*///		 YMF262_sh_stop,
/*TODO*///		 0,
/*TODO*///		 0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_YMF278B)
/*TODO*///	{
/*TODO*///		 SOUND_YMF278B,
/*TODO*///		 "YMF278B",
/*TODO*///		 YMF278B_num,
/*TODO*///		 YMF278B_clock,
/*TODO*///		 YMF278B_sh_start,
/*TODO*///		 YMF278B_sh_stop,
/*TODO*///		 0,
/*TODO*///		 0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_GAELCO_CG1V)
/*TODO*///	{
/*TODO*///		SOUND_GAELCO_CG1V,
/*TODO*///		"GAELCO CG-1V",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		gaelco_cg1v_sh_start,
/*TODO*///		gaelcosnd_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_GAELCO_GAE1)
/*TODO*///	{
/*TODO*///		SOUND_GAELCO_GAE1,
/*TODO*///		"GAELCO GAE1",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		gaelco_gae1_sh_start,
/*TODO*///		gaelcosnd_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_X1_010)
/*TODO*///	{
/*TODO*///		SOUND_X1_010,
/*TODO*///		"X1-010",
/*TODO*///		0,
/*TODO*///		seta_clock,
/*TODO*///		seta_sh_start,
/*TODO*///		seta_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_MULTIPCM)
/*TODO*///	{
/*TODO*///		SOUND_MULTIPCM,
/*TODO*///		"Sega 315-5560",
/*TODO*///		MultiPCM_num,
/*TODO*///		0,
/*TODO*///		MultiPCM_sh_start,
/*TODO*///		MultiPCM_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_C6280)
/*TODO*///	{
/*TODO*///		SOUND_C6280,
/*TODO*///		"HuC6280",
/*TODO*///		0,
/*TODO*///		c6280_clock,
/*TODO*///		c6280_sh_start,
/*TODO*///		c6280_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_TIA)
/*TODO*///    {
/*TODO*///		SOUND_TIA,
/*TODO*///		"TIA",
/*TODO*///		0,
/*TODO*///		TIA_clock,
/*TODO*///		tia_sh_start,
/*TODO*///		tia_sh_stop,
/*TODO*///		tia_sh_update,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_SP0250)
/*TODO*///	{
/*TODO*///		SOUND_SP0250,
/*TODO*///		"GI SP0250",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		sp0250_sh_start,
/*TODO*///		sp0250_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_SCSP)
/*TODO*///	{
/*TODO*///		SOUND_SCSP,
/*TODO*///		"YMF292-F SCSP",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		SCSP_sh_start,
/*TODO*///		SCSP_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound(),
/*TODO*///#if (HAS_PSXSPU)
/*TODO*///	{
/*TODO*///		SOUND_PSXSPU,
/*TODO*///		"PSX SPU",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		PSX_sh_start,
/*TODO*///		PSX_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
                new dummySound()
/*TODO*///#if (HAS_YMF271)
/*TODO*///	{
/*TODO*///		SOUND_YMF271,
/*TODO*///		"YMF271",
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		YMF271_sh_start,
/*TODO*///		YMF271_sh_stop,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///#if (HAS_BEEP)
/*TODO*///	{
/*TODO*///		SOUND_BEEP,
/*TODO*///		"Beep",
/*TODO*///		beep_num,
/*TODO*///		0,
/*TODO*///		beep_sh_start,
/*TODO*///		beep_sh_stop,
/*TODO*///		beep_sh_update,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
/*TODO*///#if (HAS_SPEAKER)
/*TODO*///	{
/*TODO*///		SOUND_SPEAKER,
/*TODO*///		"Speaker",
/*TODO*///		speaker_num,
/*TODO*///		0,
/*TODO*///		speaker_sh_start,
/*TODO*///		speaker_sh_stop,
/*TODO*///		speaker_sh_update,
/*TODO*///		0
/*TODO*///	},
/*TODO*///#endif
/*TODO*///#if (HAS_WAVE)
/*TODO*///	{
/*TODO*///		SOUND_WAVE,
/*TODO*///		"Cassette",
/*TODO*///		wave_num,
/*TODO*///		0,
/*TODO*///		wave_sh_start,
/*TODO*///		0,
/*TODO*///		0,
/*TODO*///		0
/*TODO*///	},
            };

    public static int sound_start() {
        int totalsound = 0;
        int i;
        /*TODO*///
/*TODO*///	/* Verify the order of entries in the sndintf[] array */
/*TODO*///	for (i = 0;i < SOUND_COUNT;i++)
/*TODO*///	{
/*TODO*///		if (sndintf[i].sound_num != i)
/*TODO*///		{
/*TODO*///            int j;
/*TODO*///logerror("Sound #%d wrong ID %d: check enum SOUND_... in src/sndintrf.h!\n",i,sndintf[i].sound_num);
/*TODO*///			for (j = 0; j < i; j++)
/*TODO*///				logerror("ID %2d: %s\n", j, sndintf[j].name);
/*TODO*///            return 1;
/*TODO*///		}
/*TODO*///	}
/*TODO*///

        /* samples will be read later if needed */
        Machine.samples = null;

        refresh_period = TIME_IN_HZ(Machine.drv.frames_per_second);
        refresh_period_inv = 1.0 / refresh_period;
        sound_update_timer = timer_alloc(null);

        if (mixer_sh_start() != 0) {
            return 1;
        }

        if (streams_sh_start() != 0) {
            return 1;
        }

        while (Machine.drv.sound[totalsound].sound_type != 0 && totalsound < MAX_SOUND) {
            if ((sndintf[Machine.drv.sound[totalsound].sound_type].start(Machine.drv.sound[totalsound])) != 0) {
                return 1;//goto getout;
            }
            totalsound++;
        }

        return 0;
    }

    public static void sound_stop() {
        int totalsound = 0;

        while (Machine.drv.sound[totalsound].sound_type != 0 && totalsound < MAX_SOUND) {
            sndintf[Machine.drv.sound[totalsound].sound_type].stop();
            totalsound++;
        }

        streams_sh_stop();
        mixer_sh_stop();

        /* free audio samples */
        Machine.samples = null;
    }

    public static void sound_update() {
        int totalsound = 0;

        while (Machine.drv.sound[totalsound].sound_type != 0 && totalsound < MAX_SOUND) {
            sndintf[Machine.drv.sound[totalsound].sound_type].update();
            totalsound++;
        }

        streams_sh_update();
        mixer_sh_update();

        timer_adjust(sound_update_timer, TIME_NEVER, 0, 0);
    }

    public static void sound_reset() {
        int totalsound = 0;

        while (Machine.drv.sound[totalsound].sound_type != 0 && totalsound < MAX_SOUND) {
            sndintf[Machine.drv.sound[totalsound].sound_type].reset();
            totalsound++;
        }
    }

    public static String soundtype_name(int soundtype) {
        if (soundtype < SOUND_COUNT) {
            return sndintf[soundtype].name;
        } else {
            return "";
        }
    }

    public static String sound_name(MachineSound msound) {
        return soundtype_name(msound.sound_type);
    }

    public static int sound_num(MachineSound msound) {
        if (msound.sound_type < SOUND_COUNT && sndintf[msound.sound_type].chips_num(msound) != 0) {
            return sndintf[msound.sound_type].chips_num(msound);
        } else {
            return 0;
        }
    }

    public static int sound_clock(MachineSound msound) {
        if (msound.sound_type < SOUND_COUNT && sndintf[msound.sound_type].chips_clock(msound) != 0) {
            return sndintf[msound.sound_type].chips_clock(msound);
        } else {
            return 0;
        }
    }

    public static int sound_scalebufferpos(int value) {
        int result = (int) ((double) value * timer_timeelapsed(sound_update_timer) * refresh_period_inv);
        if (value >= 0) {
            return (result < value) ? result : value;
        } else {
            return (result > value) ? result : value;
        }
    }

}
