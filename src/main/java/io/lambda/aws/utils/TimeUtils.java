package io.lambda.aws.utils;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class TimeUtils {

    public static long getTime() {
        return System.currentTimeMillis();
    }

    public static String timeSpent(long started) {
        final long diff = getTime() - started;
        return diff + " millis";
    }
}
