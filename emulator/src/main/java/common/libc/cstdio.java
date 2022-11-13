package common.libc;

import java.io.PrintStream;

/**
 *
 * @author shadow
 */
public class cstdio {

    /**
     * Streams redirect
     */
    public static PrintStream stderr = System.out;

    /**
     * fprintf
     */
    public static void fprintf(PrintStream file, String str, Object... arguments) {
        str = str.replace("\n", "%n");//fix for windows
        String print = String.format(str, arguments);
        try {
            file.print(print);
        } catch (Exception e) {
        }
    }

    /**
     * printf
     */
    public static void printf(String str, Object... arguments) {
        System.out.printf(str, arguments);
    }
}
