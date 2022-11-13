package common.libc;

/**
 *
 * @author shadow
 */
public class cstring {

    /**
     * strlen
     */
    public static int strlen(String str) {
        return str.length();
    }

    /**
     * strncmp
     */
    public static boolean strncmp(char[] s1, String s2, int n) {
        if (n > s2.length()) {
            n = s2.length();
        }
        String s1s = new String(s1).substring(0, n);//not proper but should work that way
        int compare = s1s.compareTo(s2.substring(0, n));
        if (compare == 0) {
            return false;//should be true , but for matching c format return false
        }
        return true;
    }

    /**
     * strcmp
     */
    public static int strcmp(String str1, String str2) {
        return str1.compareTo(str2);
    }

}
