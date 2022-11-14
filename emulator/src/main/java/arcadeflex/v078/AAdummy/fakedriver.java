/**
 * WARNING!!! This is only for testing!!!
 */
package arcadeflex.v078.AAdummy;

import static arcadeflex.v078.mame.driverH.*;

/**
 *
 * @author giorg
 */
public class fakedriver {

    public static GameDriver driver_1942 = new GameDriver("1984", "1942", "_1942.java", null, null, null, null, null, ROT270, "Capcom", "1942 (set 1)");
    public static GameDriver driver_1942a = new GameDriver("1984", "1942a", "_1942.java", null, driver_1942, null, null, null, ROT270, "Capcom", "1942 (set 2)");
    public static GameDriver driver_1942b = new GameDriver("1984", "1942b", "_1942.java", null, driver_1942, null, null, null, ROT270, "Capcom", "1942 (set 3)");
    public static GameDriver driver_minivadr = new GameDriver("1990", "minivadr", "minivadr.java", null, null, null, null, null, ROT0, "Taito Corporation", "Minivader");
}
