package io.aws.lambda.runtime.convert.impl;

import com.google.gson.Gson;
import io.aws.lambda.runtime.config.GsonConfiguration;
import io.aws.lambda.runtime.convert.Converter;
import io.micronaut.core.annotation.Introspected;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 22.3.2021
 */
@Introspected
@Singleton
public class GsonConverter implements Converter {

    private final Gson mapper;

    @Inject
    public GsonConverter(GsonConfiguration configuration) {
        this.mapper = configuration.build();
    }

    @Override
    public <T> @NotNull T convertToType(@NotNull String json, @NotNull Class<T> type) {
        return mapper.fromJson(json, type);
    }

    @Override
    public @NotNull String convertToJson(@NotNull Object o) {
        if (o instanceof String)
            return ((String) o);

        return mapper.toJson(o);
    }
}
