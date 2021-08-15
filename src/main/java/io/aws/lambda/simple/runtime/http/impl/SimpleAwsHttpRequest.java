package io.aws.lambda.simple.runtime.http.impl;

import io.aws.lambda.simple.runtime.http.AwsHttpRequest;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public class SimpleAwsHttpRequest implements AwsHttpRequest {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String MEDIA_TYPE_JSON = "application/json";

    public static final Map<String, String> JSON_HEADERS = Map.of(CONTENT_TYPE, MEDIA_TYPE_JSON);

    private final String body;
    private final Map<String, List<String>> headers;

    public SimpleAwsHttpRequest(String body, @NotNull Map<String, List<String>> headers) {
        this.body = body;
        this.headers = headers;
    }

    public static SimpleAwsHttpRequest ofString(String value) {
        return ofString(value, Collections.emptyMap());
    }

    public static SimpleAwsHttpRequest ofJson(String value) {
        return ofString(value, JSON_HEADERS);
    }

    public static SimpleAwsHttpRequest ofString(String value, @NotNull Map<String, String> headers) {
        final Map<String, List<String>> headersMulti = headers.isEmpty()
                ? Collections.emptyMap()
                : headers.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> List.of(e.getValue())));

        return ofStringHeaders(value, headersMulti);
    }

    public static SimpleAwsHttpRequest ofStringHeaders(String value, @NotNull Map<String, List<String>> headers) {
        return new SimpleAwsHttpRequest(value, headers);
    }

    @Override
    public String body() {
        return body;
    }

    @Override
    public @NotNull Map<String, List<String>> headers() {
        return headers;
    }

    @Override
    public String toString() {
        return "[body=" + body + ", headers=" + headers + ']';
    }
}
