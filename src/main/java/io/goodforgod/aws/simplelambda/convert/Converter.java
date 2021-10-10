package io.goodforgod.aws.simplelambda.convert;

import org.jetbrains.annotations.NotNull;

/**
 * Simple converter contract
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public interface Converter {

    @NotNull
    <T> T fromString(@NotNull String value, @NotNull Class<T> type);

    String toString(Object o);
}
