/**
 * ported to v0.78
 */
package arcadeflex.v078.generic;

//mame imports
import static arcadeflex.v078.mame.drawgfxH.*;
import static arcadeflex.v078.mame.commonH.*;
import static arcadeflex.v078.mame.driverH.*;
import static arcadeflex.v078.mame.sndintrfH.*;
//common imports
import static common.ptrLib.*;

public class funcPtr {

    /**
     * common functions
     */
    public abstract static interface ReadHandlerPtr {

        public abstract int handler(int offset);
    }

    public abstract static interface WriteHandlerPtr {

        public abstract void handler(int offset, int data);
    }

    public static abstract interface InterruptHandlerPtr {

        public abstract void handler();
    }

    public static abstract interface MachineHandlerPtr {

        public abstract void handler(InternalMachineDriver machine);
    }

    public static abstract interface MachineInitHandlerPtr {

        public abstract void handler();
    }

    public static abstract interface MachineStopHandlerPtr {

        public abstract void handler();
    }

    public static abstract interface DriverInitHandlerPtr {

        public abstract void handler();
    }

    /**
     * Rom related
     */
    public static abstract interface RomLoadHandlerPtr {

        public abstract void handler();
    }

    /**
     * Input port related
     */
    public static abstract interface InputPortHandlerPtr {

        public abstract void handler();
    }

    /**
     * Timer callback
     */
    public static abstract interface TimerCallbackHandlerPtr {

        public abstract void handler(int i);
    }

    /**
     * Video related
     */
    public static abstract interface PaletteInitHandlerPtr {

        public abstract void handler(char[] colortable, UBytePtr color_prom);
    }

    public static abstract interface VideoStartHandlerPtr {

        public abstract int handler();
    }

    public static abstract interface VideoUpdateHandlerPtr {

        public abstract void handler(mame_bitmap bitmap, rectangle cliprect);
    }

    /**
     * Sound related
     */
    public static abstract interface ShStartHandlerPtr {

        public abstract int handler(MachineSound msound);
    }

    public static abstract interface ShStopHandlerPtr {

        public abstract void handler();
    }

    public static abstract interface ShUpdateHandlerPtr {

        public abstract void handler();
    }
}
