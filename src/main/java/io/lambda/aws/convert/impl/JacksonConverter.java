package io.lambda.aws.convert.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lambda.aws.convert.Converter;
import io.lambda.aws.error.ConvertException;
import io.micronaut.core.annotation.Introspected;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Introspected
@Singleton
public class JacksonConverter implements Converter {

    private final ObjectMapper mapper;

    @Inject
    public JacksonConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public <T> T convertToType(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new ConvertException(e.getMessage());
        }
    }

    @Override
    public String convertToJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new ConvertException(e.getMessage());
        }
    }
}
