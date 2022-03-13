package io.goodforgod.aws.simplelambda.http.common;

import io.goodforgod.aws.simplelambda.http.SimpleHttpRequest;
import io.goodforgod.http.common.HttpHeaders;
import io.goodforgod.http.common.MediaType;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow.Publisher;
import org.jetbrains.annotations.NotNull;

/**
 * Handles {@link String} as http request
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public final class StringHttpRequest implements SimpleHttpRequest {

    private static final HttpHeaders JSON_HEADERS = HttpHeaders.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

    private static final StringHttpRequest EMPTY = new StringHttpRequest(null, HttpHeaders.empty());

    private final String body;
    private final HttpHeaders headers;

    private StringHttpRequest(String body, @NotNull HttpHeaders headers) {
        this.body = body;
        this.headers = headers;
    }

    public static StringHttpRequest empty() {
        return EMPTY;
    }

    public static StringHttpRequest ofJson(String value) {
        return ofString(value, JSON_HEADERS);
    }

    public static StringHttpRequest ofString(String value) {
        return ofString(value, HttpHeaders.empty());
    }

    public static StringHttpRequest ofString(String value, @NotNull HttpHeaders headers) {
        return new StringHttpRequest(value, headers);
    }

    public static StringHttpRequest ofHeaders(@NotNull HttpHeaders headers) {
        return new StringHttpRequest(null, headers);
    }

    @Override
    public Publisher<ByteBuffer> body() {
        return (body == null)
                ? null
                : HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8);
    }

    @Override
    public @NotNull HttpHeaders headers() {
        return headers;
    }

    @Override
    public String toString() {
        return "[body=" + body + ", headers=" + headers + ']';
    }
}
