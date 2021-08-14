package io.aws.lambda.simple.runtime.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 4.4.2021
 */
public class Base64Utils {

    private static final Base64.Decoder decoder = Base64.getMimeDecoder();

    public static String decode(String stringAsBase64) {
        return new String(decoder.decode(stringAsBase64), StandardCharsets.UTF_8);
    }
}
