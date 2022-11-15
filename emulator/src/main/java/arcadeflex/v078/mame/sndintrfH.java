/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

//generic imports
import static arcadeflex.v078.generic.funcPtr.*;

public class sndintrfH {

    public static class MachineSound {

        public MachineSound(int sound_type, Object sound_interface, String tag) {
            this.sound_type = sound_type;
            this.sound_interface = sound_interface;
        }

        public MachineSound() {
            this(0, null, null);
        }

        public static MachineSound[] create(int n) {
            MachineSound[] a = new MachineSound[n];
            for (int k = 0; k < n; k++) {
                a[k] = new MachineSound();
            }
            return a;
        }

        public int sound_type;
        public Object sound_interface;
        public String tag;
    }

    public static final int SOUND_DUMMY = 0;
    public static final int SOUND_CUSTOM = 1;
    public static final int SOUND_SAMPLES = 2;
    public static final int SOUND_DAC = 3;
    public static final int SOUND_DISCRETE = 4;
    public static final int SOUND_AY8910 = 5;
    public static final int SOUND_YM2203 = 6;
    public static final int SOUND_YM2151 = 7;
    public static final int SOUND_YM2608 = 8;
    public static final int SOUND_YM2610 = 9;
    public static final int SOUND_YM2610B = 10;
    public static final int SOUND_YM2612 = 11;
    public static final int SOUND_YM3438 = 12;
    public static final int SOUND_YM2413 = 13;
    public static final int SOUND_YM3812 = 14;
    public static final int SOUND_YM3526 = 15;
    public static final int SOUND_YMZ280B = 16;
    public static final int SOUND_Y8950 = 17;
    public static final int SOUND_SN76477 = 18;
    public static final int SOUND_SN76496 = 19;
    public static final int SOUND_POKEY = 20;
    public static final int SOUND_NES = 21;
    public static final int SOUND_ASTROCADE = 22;
    public static final int SOUND_NAMCO = 23;
    public static final int SOUND_NAMCONA = 24;
    public static final int SOUND_TMS36XX = 25;
    public static final int SOUND_TMS5110 = 26;
    public static final int SOUND_TMS5220 = 27;
    public static final int SOUND_VLM5030 = 28;
    public static final int SOUND_ADPCM = 29;
    public static final int SOUND_OKIM6295 = 30;
    public static final int SOUND_MSM5205 = 31;
    public static final int SOUND_MSM5232 = 32;
    public static final int SOUND_UPD7759 = 33;
    public static final int SOUND_HC55516 = 34;
    public static final int SOUND_K005289 = 35;
    public static final int SOUND_K007232 = 36;
    public static final int SOUND_K051649 = 37;
    public static final int SOUND_K053260 = 38;
    public static final int SOUND_K054539 = 39;
    public static final int SOUND_SEGAPCM = 40;
    public static final int SOUND_RF5C68 = 41;
    public static final int SOUND_CEM3394 = 42;
    public static final int SOUND_C140 = 43;
    public static final int SOUND_QSOUND = 44;
    public static final int SOUND_SAA1099 = 45;
    public static final int SOUND_IREMGA20 = 46;
    public static final int SOUND_ES5505 = 47;
    public static final int SOUND_ES5506 = 48;
    public static final int SOUND_BSMT2000 = 49;
    public static final int SOUND_YMF262 = 50;
    public static final int SOUND_YMF278B = 51;
    public static final int SOUND_GAELCO_CG1V = 52;
    public static final int SOUND_GAELCO_GAE1 = 53;
    public static final int SOUND_X1_010 = 54;
    public static final int SOUND_MULTIPCM = 55;
    public static final int SOUND_C6280 = 56;
    public static final int SOUND_TIA = 57;
    public static final int SOUND_SP0250 = 58;
    public static final int SOUND_SCSP = 59;
    public static final int SOUND_PSXSPU = 60;
    public static final int SOUND_YMF271 = 61;

    public static final int SOUND_COUNT = 62;

    /* structure for SOUND_CUSTOM sound drivers */
    public static class CustomSound_interface {

        public CustomSound_interface(ShStartHandlerPtr sh_start, ShStopHandlerPtr sh_stop, ShUpdateHandlerPtr sh_update) {
            this.sh_start = sh_start;
            this.sh_stop = sh_stop;
            this.sh_update = sh_update;
        }

        public ShStartHandlerPtr sh_start;
        public ShStopHandlerPtr sh_stop;
        public ShUpdateHandlerPtr sh_update;

    }
}
