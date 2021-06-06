package io.aws.lambda.runtime.config;

import com.google.gson.Gson;
import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Introspected;

/**
 * @see io.aws.lambda.runtime.convert.impl.GsonConverter
 * @author Anton Kurako (GoodforGod)
 * @since 25.04.2021
 */
@Introspected
@ConfigurationProperties("gson")
public class GsonConfiguration {

    @ConfigurationBuilder
    private final io.gson.adapters.config.GsonConfiguration configuration = new io.gson.adapters.config.GsonConfiguration();

    public io.gson.adapters.config.GsonConfiguration getConfiguration() {
        return configuration;
    }

    public Gson build() {
        return configuration.builder().create();
    }
}
