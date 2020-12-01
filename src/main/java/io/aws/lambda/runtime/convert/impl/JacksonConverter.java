package io.aws.lambda.runtime.convert.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.error.ConvertException;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Singleton
public class JacksonConverter implements Converter {

    private final ObjectMapper mapper;

    @Inject
    public JacksonConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public <T> @NotNull T convertToType(@NotNull String json, @NotNull Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new ConvertException(e.getMessage());
        }
    }

    @Override
    public @NotNull String convertToJson(@NotNull Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new ConvertException(e.getMessage());
        }
    }
}
