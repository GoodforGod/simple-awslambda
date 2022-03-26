package io.goodforgod.aws.lambda.simple.micronaut.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.aws.lambda.simple.convert.gson.GsonConverter;
import io.goodforgod.aws.lambda.simple.convert.gson.GsonConverterFactory;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

/**
 * Factory for {@link Gson} that is used in Micronaut DI.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 14.08.2021
 */
@Requires(property = "gson.enabled", value = "true", defaultValue = "true")
@Introspected
@Factory
class MicronautGsonFactory extends GsonConverterFactory {

    private final MicronautGsonConfiguration gsonConfiguration;

    @Inject
    MicronautGsonFactory(MicronautGsonConfiguration gsonConfiguration) {
        this.gsonConfiguration = gsonConfiguration;
    }

    @Named("gson")
    @Singleton
    @Secondary
    @Bean
    @Override
    public @NotNull Converter build() {
        final GsonBuilder builder = registerAdapters(gsonConfiguration.getConfiguration().builder());
        return new GsonConverter(builder.create());
    }
}
