package io.aws.lambda.simple.runtime.micronaut;

import com.google.gson.Gson;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Factory for {@link Gson} that is used in Micronaut DI.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 14.08.2021
 */
@Factory
class MicronautGsonFactory {

    private final MicronautGsonConfiguration gsonConfiguration;

    @Inject
    MicronautGsonFactory(MicronautGsonConfiguration gsonConfiguration) {
        this.gsonConfiguration = gsonConfiguration;
    }

    @Singleton
    @Bean
    Gson get() {
        return gsonConfiguration.getConfiguration().builder().create();
    }
}
