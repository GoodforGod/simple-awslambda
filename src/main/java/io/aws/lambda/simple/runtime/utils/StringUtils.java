package io.aws.lambda.simple.runtime.utils;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.2.2021
 */
public class StringUtils {

    private StringUtils() {}

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return isEmpty(value);
    }

    public static String concatOrEmpty(String prefix, Object value) {
        if (value == null)
            return "";

        final String s = value.toString();
        return s.isEmpty()
                ? ""
                : prefix + s;
    }
}
