package io.goodforgod.aws.lambda.simple.utils;

import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Internal
public final class TimeUtils {

    private TimeUtils() {}

    public static long getTime() {
        return System.currentTimeMillis();
    }

    public static long timeTook(long startedInMillis) {
        return getTime() - startedInMillis;
    }
}
