package io.aws.lambda.simple.runtime.utils;

import io.aws.lambda.simple.runtime.error.LambdaException;

import java.io.BufferedInputStream;
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

    public static String getStringUTF8FromInputStream(InputStream inputStream) {
        try {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new LambdaException(e);
        }
    }

    public static InputStream getInputStreamFromStringUTF8(String value) {
        return StringUtils.isEmpty(value)
                ? InputStream.nullInputStream()
                : new BufferedInputStream(new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)));
    }
}
