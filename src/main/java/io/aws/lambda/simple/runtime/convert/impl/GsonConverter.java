package io.aws.lambda.simple.runtime.convert.impl;

import com.google.gson.Gson;
import io.aws.lambda.simple.runtime.convert.Converter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

/**
 * {@link Gson} converter implementation
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.3.2021
 */
@Singleton
public class GsonConverter implements Converter {

    private final Gson gson;

    @Inject
    public GsonConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public @NotNull <T> T fromJson(@NotNull String json, @NotNull Class<T> type) {
        return gson.fromJson(json, type);
    }

    @Override
    public String toJson(Object o) {
        return gson.toJson(o);
    }
}
