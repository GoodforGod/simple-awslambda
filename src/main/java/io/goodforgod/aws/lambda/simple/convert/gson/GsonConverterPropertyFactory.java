package io.goodforgod.aws.lambda.simple.convert.gson;

import com.google.gson.Gson;
import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.gson.configuration.GsonConfiguration;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.util.Properties;
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

    private static final String GSON_PROPERTIES = "gson.properties";

    @NotNull
    public Converter build() {
        return new GsonConverter(getGsonConfiguration().builder().create());
    }

    private GsonConfiguration getGsonConfiguration() {
        try {
            try (InputStream resource = getClass().getClassLoader().getResourceAsStream(GSON_PROPERTIES)) {
                if (resource != null) {
                    final Properties properties = new Properties();
                    properties.load(resource);
                    return GsonConfiguration.ofProperties(properties);
                } else {
                    return new GsonConfiguration();
                }
            }
        } catch (Exception e) {
            return new GsonConfiguration();
        }
    }
}
