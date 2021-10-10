package io.goodforgod.aws.simplelambda.http.common;

import io.goodforgod.aws.simplelambda.http.SimpleHttpRequest;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Flow.Publisher;
import org.jetbrains.annotations.NotNull;

/**
 * Handles {@link String} as http request
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public final class StringHttpRequest implements SimpleHttpRequest {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String MEDIA_TYPE_JSON = "application/json";

    public static final Map<String, String> JSON_HEADERS = Map.of(CONTENT_TYPE, MEDIA_TYPE_JSON);

    private static final StringHttpRequest EMPTY = new StringHttpRequest(null, Collections.emptyMap());

    private final String body;
    private final Map<String, String> headers;

    private StringHttpRequest(String body, @NotNull Map<String, String> headers) {
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
        return ofString(value, Collections.emptyMap());
    }

    public static StringHttpRequest ofString(String value, @NotNull Map<String, String> headers) {
        return new StringHttpRequest(value, headers);
    }

    public static StringHttpRequest ofHeaders(@NotNull Map<String, String> headers) {
        return new StringHttpRequest(null, headers);
    }

    @Override
    public Publisher<ByteBuffer> body() {
        return (body == null)
                ? null
                : HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8);
    }

    @Override
    public @NotNull Map<String, String> headers() {
        return headers;
    }

    @Override
    public String toString() {
        return "[body=" + body + ", headers=" + headers + ']';
    }
}
