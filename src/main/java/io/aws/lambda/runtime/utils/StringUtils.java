package io.aws.lambda.runtime.utils;

/**
 * Description
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.2.2021
 */
public class StringUtils {

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    public static String concatOrEmpty(String prefix, Object value) {
        if(value == null)
            return "";

        final String s = value.toString();
        return s.isEmpty() ? "" : prefix + s;
    }
}
