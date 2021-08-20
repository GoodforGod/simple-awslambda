package io.aws.lambda.simple.runtime.convert;

import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public interface Converter {

    @NotNull
    <T> T fromJson(@NotNull String json, @NotNull Class<T> type);

    String toJson(Object o);
}
