package io.aws.lambda.runtime.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.annotation.Introspected;

import javax.inject.Singleton;

/**
 * @author GoodforGod
 * @since 28.10.2020
 */
@Introspected
@Factory
public class ObjectMapperConfig {

    @Singleton
    @Bean
    public ObjectMapper getMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
