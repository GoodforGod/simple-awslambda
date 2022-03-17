package io.goodforgod.aws.lambda.simple.utils;

import io.goodforgod.aws.lambda.simple.error.LambdaException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public final class InputStreamUtils {

    private InputStreamUtils() {}

    public static String getStringFromInputStreamUTF8(InputStream inputStream) {
        return getStringFromInputStream(inputStream, StandardCharsets.UTF_8);
    }

    public static String getStringFromInputStream(InputStream inputStream, Charset charset) {
        try {
            return new String(inputStream.readAllBytes(), charset);
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
