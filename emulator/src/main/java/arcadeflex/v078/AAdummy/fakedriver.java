/**
 * WARNING!!! This is only for testing!!!
 */
package arcadeflex.v078.AAdummy;

import arcadeflex.v078.generic.funcPtr.InputPortHandlerPtr;
import arcadeflex.v078.generic.funcPtr.RomLoadHandlerPtr;
import static arcadeflex.v078.mame.driverH.*;

/**
 *
 * @author giorg
 */
public class fakedriver {

    public static InputPortHandlerPtr input_ports_dummy = new InputPortHandlerPtr() {
        public void handler() {

        }
    };
    static RomLoadHandlerPtr rom_dummy = new RomLoadHandlerPtr() {
        public void handler() {

        }
    };

    public static GameDriver driver_1942 = new GameDriver("1984", "1942", "_1942.java", rom_dummy, null, null, input_ports_dummy, null, ROT270, "Capcom", "1942 (set 1)");
    public static GameDriver driver_1942a = new GameDriver("1984", "1942a", "_1942.java", rom_dummy, driver_1942, null, input_ports_dummy, null, ROT270, "Capcom", "1942 (set 2)");
    public static GameDriver driver_1942b = new GameDriver("1984", "1942b", "_1942.java", rom_dummy, driver_1942, null, input_ports_dummy, null, ROT270, "Capcom", "1942 (set 3)");
    public static GameDriver driver_minivadr = new GameDriver("1990", "minivadr", "minivadr.java", rom_dummy, null, null, input_ports_dummy, null, ROT0, "Taito Corporation", "Minivader");
}
