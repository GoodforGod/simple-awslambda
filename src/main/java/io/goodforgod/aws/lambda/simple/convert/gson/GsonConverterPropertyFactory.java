package io.goodforgod.aws.lambda.simple.convert.gson;

import com.google.gson.Gson;
import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.gson.configuration.GsonFactory;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

/**
 * Simple {@link Gson} factory implementation that builds instance of Gson from
 * property file: gson.properties
 *
 * @author Anton Kurako (GoodforGod)
 * @since 02.10.2021
 */
@Singleton
public class GsonConverterPropertyFactory {

    private final GsonFactory factory = new GsonFactory();

    @NotNull
    public Converter build() {
        return new GsonConverter(factory.build());
    }
}
