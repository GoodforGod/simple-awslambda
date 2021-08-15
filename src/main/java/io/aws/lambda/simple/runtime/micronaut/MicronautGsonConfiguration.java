package io.aws.lambda.simple.runtime.micronaut;

import com.google.gson.Gson;
import io.aws.lambda.simple.runtime.convert.impl.GsonConverter;
import io.gson.adapters.config.GsonConfiguration;
import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;

/**
 * Configuration for {@link Gson} in Micronaut DI.
 *
 * @see GsonConverter
 * @author Anton Kurako (GoodforGod)
 * @since 25.04.2021
 */
@ConfigurationProperties("gson")
public class MicronautGsonConfiguration {

    @ConfigurationBuilder
    private final GsonConfiguration configuration = new GsonConfiguration();

    public GsonConfiguration getConfiguration() {
        return configuration;
    }
}
