package io.goodforgod.aws.simplelambda.convert.gson;

import com.google.gson.Gson;
import io.goodforgod.aws.simplelambda.convert.Converter;
import io.goodforgod.gson.configuration.GsonFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Simple {@link Gson} factory implementation that builds instance of Gson from property file:
 * gson.properties
 *
 * @author Anton Kurako (GoodforGod)
 * @since 02.10.2021
 */
public final class GsonConverterFactory {

    private static final GsonFactory GSON_FACTORY = new GsonFactory();
    private static final RecordTypeAdapterFactory ADAPTER_FACTORY = new RecordTypeAdapterFactory();

    @NotNull
    public Converter build() {
        final Gson gson = GSON_FACTORY.builder()
                .registerTypeAdapterFactory(ADAPTER_FACTORY)
                .create();

        return new GsonConverter(gson);
    }
}
