package io.goodforgod.aws.simplelambda.convert.gson;

import com.google.gson.Gson;
import io.goodforgod.aws.simplelambda.convert.Converter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

/**
 * JSON converter implementation based on {@link Gson}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.3.2021
 */
@Named("json")
@Singleton
public class GsonConverter implements Converter {

    private final Gson gson;

    @Inject
    public GsonConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public @NotNull <T> T fromString(@NotNull String value, @NotNull Class<T> type) {
        return gson.fromJson(value, type);
    }

    @Override
    public String toString(Object o) {
        return gson.toJson(o);
    }
}
