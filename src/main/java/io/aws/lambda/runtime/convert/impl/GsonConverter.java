package io.aws.lambda.runtime.convert.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.aws.lambda.runtime.convert.Converter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 22.3.2021
 */
@Singleton
public class GsonConverter implements Converter {

    private static final Gson MAPPER = new GsonBuilder().create();

    @Override
    public <T> @NotNull T convertToType(@NotNull String json, @NotNull Class<T> type) {
        return MAPPER.fromJson(json, type);
    }

    @Override
    public @NotNull String convertToJson(@NotNull Object o) {
        return MAPPER.toJson(o);
    }
}
