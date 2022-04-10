package io.goodforgod.aws.lambda.simple.http;

import io.goodforgod.http.common.HttpHeaders;
import io.goodforgod.http.common.HttpStatus;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public interface SimpleHttpResponse {

    /**
     * @return http response status code
     */
    @NotNull
    HttpStatus status();

    /**
     * @return http header multi map
     */
    @NotNull
    HttpHeaders headers();

    /**
     * @return body as {@link InputStream}
     */
    @NotNull
    InputStream body();

    /**
     * @return body as String
     */
    default String bodyAsString() {
        return bodyAsString(StandardCharsets.UTF_8);
    }

    /**
     * @param charset to convert body with
     * @return body as String
     */
    String bodyAsString(@NotNull Charset charset);
}
