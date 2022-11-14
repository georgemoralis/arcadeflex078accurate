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

    /**
     * Compares string1 and string2 without sensitivity to case
     *
     * @param string1
     * @param string2
     * @return a negative integer, zero, or a positive integer as the specified
     * String is greater than, equal to, or less than this String, ignoring case
     * considerations.
     */
    public static int stricmp(String str1, String str2) {
        return str1.compareToIgnoreCase(str2);
    }

}
