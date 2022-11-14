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

    /**
     * strstr
     */
    public static int strstr(String X, String Y) {
        // if X is null or if X's length is less than that of Y's
        if (X == null || Y.length() > X.length()) {
            return -1;
        }

        // if Y is null or is empty
        if (Y == null || Y.length() == 0) {
            return 0;
        }

        for (int i = 0; i <= X.length() - Y.length(); i++) {
            int j;
            for (j = 0; j < Y.length(); j++) {
                if (Y.charAt(j) != X.charAt(i + j)) {
                    break;
                }
            }

            if (j == Y.length()) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Locate first occurrence of character in string Returns a pointer to the
     * first occurrence of character in the C string str.
     *
     * @param str
     * @param ch
     * @return
     */
    public static String strchr(String str, char ch) {
        int found = str.indexOf(ch);
        if (found == -1)//not found
        {
            return null;
        } else {
            return str.substring(found,str.length());
        }
    }
}
