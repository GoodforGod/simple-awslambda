package io.goodforgod.aws.lambda.simple.micronaut.bean;

import com.google.gson.Gson;
import io.goodforgod.aws.lambda.simple.convert.gson.GsonConverter;
import io.goodforgod.gson.configuration.GsonConfiguration;
import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Introspected;

/**
 * Configuration for {@link Gson} in Micronaut DI.
 *
 * @see GsonConverter
 * @author Anton Kurako (GoodforGod)
 * @since 25.04.2021
 */
@Introspected
@ConfigurationProperties("gson")
class MicronautGsonConfiguration {

    private boolean enabled = true;

    @ConfigurationBuilder
    private final GsonConfiguration configuration = new GsonConfiguration();

    GsonConfiguration getConfiguration() {
        return configuration;
    }

    public boolean isEnabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
