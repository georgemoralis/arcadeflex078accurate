/**
 * ported to 0.78
 */
package arcadeflex.v078.platform;

public class rcH {

    public static final int rc_ignore = -1;
    public static final int rc_end = 0;
    public static final int rc_bool = 1;
    public static final int rc_string = 2;
    public static final int rc_int = 3;
    public static final int rc_float = 4;
    public static final int rc_set_int = 5;
    public static final int rc_seperator = 6;
    public static final int rc_file = 7;
    public static final int rc_use_function = 8;
    public static final int rc_use_function_no_arg = 9;
    public static final int rc_link = 10;

    public static abstract interface ArgCallbackHandlerPtr {

        public abstract int handler(String arg);
    }

    public static abstract interface RcFuncHandlerPtr {

        public abstract int handler(rc_option option, String arg, int priority);
    }

    public static abstract interface RcAssignFuncHandlerPtr {

        public abstract void handler(int value);
    }

    public static class rc_option {

        String name;/* name of the option */
        String shortname;/* shortcut name of the option, or clear for bool */
        int type;/* type of the option */
        Object dest;/* ptr to where the value of the option should be stored */
        String deflt;/* default value of the option in a c-string */
        float min;/* used to verify rc_int or rc_float, this check is not */
        float max;/* done if min == max. min is also used as value for
                         set_int, and as write flag for rc_file. */
        RcFuncHandlerPtr func;/* function which is called for additional verification
                         of the value, or which is called to parse the value if
                         type == use_function, or NULL. Should return 0 on
                         success, -1 on failure */
        String help;/* help text for this option */
        int priority;/* priority of the current value, the current value
                         is only changed when the priority of the source
                         is higher as this, and then the priority is set to
                         the priority of the source */

        public rc_option() {

        }

        public rc_option(String name, String shortname, int type, Object dest, String deflt, float min, float max, RcFuncHandlerPtr func, String help) {
            this.name = name;
            this.shortname = shortname;
            this.type = type;
            this.dest = dest;
            this.deflt = deflt;
            this.min = min;
            this.max = max;
            this.func = func;
            this.help = help;
            this.priority = 0;
        }

    }
}
