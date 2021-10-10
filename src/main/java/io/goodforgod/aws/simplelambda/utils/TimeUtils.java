package io.goodforgod.aws.simplelambda.utils;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public final class TimeUtils {

    private TimeUtils() {}

    public static long getTime() {
        return System.currentTimeMillis();
    }

    public static long timeTook(long startedInMillis) {
        return getTime() - startedInMillis;
    }
}
