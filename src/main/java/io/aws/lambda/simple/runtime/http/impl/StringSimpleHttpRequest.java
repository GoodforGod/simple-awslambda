package io.aws.lambda.simple.runtime.http.impl;

import io.aws.lambda.simple.runtime.http.SimpleHttpRequest;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public class StringSimpleHttpRequest implements SimpleHttpRequest {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String MEDIA_TYPE_JSON = "application/json";

    public static final Map<String, String> JSON_HEADERS = Map.of(CONTENT_TYPE, MEDIA_TYPE_JSON);

    private static final StringSimpleHttpRequest EMPTY = new StringSimpleHttpRequest(null, Collections.emptyMap());

    private final String body;
    private final Map<String, String> headers;

    private StringSimpleHttpRequest(String body, @NotNull Map<String, String> headers) {
        this.body = body;
        this.headers = headers;
    }

    public static StringSimpleHttpRequest empty() {
        return EMPTY;
    }

    public static StringSimpleHttpRequest ofJson(String value) {
        return ofString(value, JSON_HEADERS);
    }

    public static StringSimpleHttpRequest ofString(String value) {
        return ofString(value, Collections.emptyMap());
    }

    public static StringSimpleHttpRequest ofString(String value, @NotNull Map<String, String> headers) {
        return new StringSimpleHttpRequest(value, headers);
    }

    public static StringSimpleHttpRequest ofHeaders(@NotNull Map<String, String> headers) {
        return new StringSimpleHttpRequest(null, headers);
    }

    @Override
    public String body() {
        return body;
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
