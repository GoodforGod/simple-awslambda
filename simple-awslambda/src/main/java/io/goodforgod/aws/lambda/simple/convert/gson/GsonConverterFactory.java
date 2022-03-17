package io.goodforgod.aws.lambda.simple.convert.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.gson.configuration.GsonFactory;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

/**
 * Simple {@link Gson} factory implementation that builds instance of Gson from property file:
 * gson.properties
 *
 * @author Anton Kurako (GoodforGod)
 * @since 02.10.2021
 */
public class GsonConverterFactory {

    private static final GsonFactory GSON_FACTORY = new GsonFactory();
    private static final RecordTypeAdapterFactory ADAPTER_FACTORY = new RecordTypeAdapterFactory();

    @NotNull
    public Converter build() {
        final GsonBuilder builder = registerAdapters(GSON_FACTORY.builder());
        final Gson gson = builder.create();
        return new GsonConverter(gson);
    }

    @Internal
    protected GsonBuilder registerAdapters(GsonBuilder builder) {
        return builder.registerTypeAdapterFactory(ADAPTER_FACTORY);
    }
}
