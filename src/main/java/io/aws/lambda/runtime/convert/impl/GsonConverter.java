package io.aws.lambda.runtime.convert.impl;

import com.google.gson.Gson;
import io.aws.lambda.runtime.convert.Converter;
import io.micronaut.core.annotation.Introspected;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Singleton
@Introspected
public class GsonConverter implements Converter {

    private static final Gson MAPPER = new Gson();

    @Override
    public <T> @NotNull T convertToType(@NotNull String json, @NotNull Class<T> type) {
        return MAPPER.fromJson(json, type);
    }

    @Override
    public @NotNull String convertToJson(@NotNull Object o) {
        return MAPPER.toJson(o);
    }
}
