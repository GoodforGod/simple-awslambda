package io.goodforgod.aws.simplelambda.utils;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.2.2021
 */
public class StringUtils {

    private StringUtils() {}

    public static boolean isEmpty(CharSequence value) {
        return value == null || value.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence value) {
        return !isEmpty(value);
    }
}
