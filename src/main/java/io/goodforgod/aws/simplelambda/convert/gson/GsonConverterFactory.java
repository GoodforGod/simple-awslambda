package io.goodforgod.aws.simplelambda.convert.gson;

import com.google.gson.Gson;
import io.goodforgod.aws.simplelambda.convert.Converter;
import io.goodforgod.gson.configuration.GsonFactory;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

/**
 * Simple {@link Gson} factory implementation that builds instance of Gson from property file:
 * gson.properties
 *
 * @author Anton Kurako (GoodforGod)
 * @since 02.10.2021
 */
@Singleton
public class GsonConverterFactory {

    private final GsonFactory factory = new GsonFactory();

    @NotNull
    public Converter build() {
        return new GsonConverter(factory.build());
    }
}
