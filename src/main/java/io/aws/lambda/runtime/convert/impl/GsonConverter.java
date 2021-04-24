package io.aws.lambda.runtime.convert.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.aws.lambda.runtime.config.GsonConfiguration;
import io.aws.lambda.runtime.convert.Converter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 22.3.2021
 */
@Singleton
public class GsonConverter implements Converter {

    private final Gson mapper;

    @Inject
    public GsonConverter(GsonConfiguration configuration) {
        final GsonBuilder builder = new GsonBuilder()
                .setDateFormat(configuration.getDateFormat())
        .setLongSerializationPolicy(configuration.getLongSerializationPolicy())
                .setFieldNamingPolicy(configuration.getFieldNamingPolicy());

        if(configuration.isComplexMapKeySerialization())
            builder.enableComplexMapKeySerialization();
        if(!configuration.isEscapeHtmlChars())
            builder.disableHtmlEscaping();
        if(configuration.isGenerateNonExecutableJson())
            builder.generateNonExecutableJson();
        if(configuration.isLenient())
            builder.setLenient();
        if(configuration.isPrettyPrinting())
            builder.setPrettyPrinting();
        if(configuration.isSerializeSpecialFloatingPointValues())
            builder.serializeSpecialFloatingPointValues();
        if(configuration.isSerializeNulls())
            builder.serializeNulls();

        this.mapper = builder.create();
    }

    @Override
    public <T> @NotNull T convertToType(@NotNull String json, @NotNull Class<T> type) {
        return mapper.fromJson(json, type);
    }

    @Override
    public @NotNull String convertToJson(@NotNull Object o) {
        return mapper.toJson(o);
    }
}
