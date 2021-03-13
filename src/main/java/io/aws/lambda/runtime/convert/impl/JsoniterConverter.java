package io.aws.lambda.runtime.convert.impl;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.DecodingMode;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.error.ConvertException;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Singleton
public class JsoniterConverter implements Converter {

    @Override
    public <T> @NotNull T convertToType(@NotNull String json, @NotNull Class<T> type) {
        try {
            final T t = getInstance(type);
            return JsonIterator.parse(json).read(t);
        } catch (IOException e) {
            throw new ConvertException(e.getMessage());
        }
    }

    @Override
    public @NotNull String convertToJson(@NotNull Object o) {
        return JsonStream.serialize(o);
    }

    public static <T> T getInstance(Class<T> clazz)  {
        try {
            final Constructor<T> constructor = getDeclaredConstructor(clazz);
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ConvertException(e.getMessage());
        }
    }

    public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz) throws NoSuchMethodException {
        return clazz.getDeclaredConstructor();
    }
}
