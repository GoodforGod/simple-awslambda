package io.aws.lambda.simple.runtime.convert.impl;

import com.google.gson.Gson;
import io.aws.lambda.simple.runtime.convert.Converter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

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
    public @NotNull <T> T convertToType(@NotNull String json, @NotNull Class<T> type) {
        return gson.fromJson(json, type);
    }

    @Override
    public String convertToJson(Object o) {
        return (o instanceof String)
                ? ((String) o)
                : gson.toJson(o);
    }
}
