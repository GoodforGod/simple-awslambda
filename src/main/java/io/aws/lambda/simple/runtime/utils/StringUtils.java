package io.aws.lambda.simple.runtime.utils;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.2.2021
 */
public class StringUtils {

    private StringUtils() {}

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    public static String concatOrEmpty(String prefix, Object value) {
        if (value == null)
            return "";

        final String s = value.toString();
        return s.isEmpty() ? "" : prefix + s;
    }
}
