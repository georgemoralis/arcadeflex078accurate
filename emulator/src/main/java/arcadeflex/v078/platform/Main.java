/**
 * ported to 0.78
 */
package arcadeflex.v078.platform;

//common imports
import static common.util.*;

public class Main {

    public static void main(String[] args) {
        ConvertArguments("arcadeflex", args);
        System.exit(osdepend.main(argc, argv));
    }
}
