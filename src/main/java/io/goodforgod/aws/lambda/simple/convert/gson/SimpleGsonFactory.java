package io.goodforgod.aws.lambda.simple.convert.gson;

import com.google.gson.Gson;
import io.goodforgod.gson.configuration.GsonConfiguration;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.util.Properties;

/**
 * Simple {@link Gson} factory implementation
 *
 * @author Anton Kurako (GoodforGod)
 * @since 02.10.2021
 */
@Singleton
public class SimpleGsonFactory {

    private static final String GSON_PROPERTIES = "gson.properties";

    public Gson getGson() {
        return getGsonConfiguration().builder().create();
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
