package io.aws.lambda.simple.runtime.http;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Map;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public interface SimpleHttpRequest {

    /**
     * @return body as {@link InputStream}
     */
    String body();

    /**
     * @return http header multi map
     */
    @NotNull
    Map<String, String> headers();
}
