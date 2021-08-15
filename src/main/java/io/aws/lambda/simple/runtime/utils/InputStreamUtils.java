package io.aws.lambda.simple.runtime.utils;

import io.aws.lambda.simple.runtime.error.StatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public class InputStreamUtils {

    private InputStreamUtils() {}

    public static String getInputAsStringUTF8(InputStream inputStream) {
        try {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StatusException(e.getMessage(), e, 500);
        }
    }

    public static InputStream getStringUTF8AsInputStream(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }
}
