package io.aws.lambda.simple.runtime.http.impl;

import io.aws.lambda.simple.runtime.http.SimpleHttpResponse;
import io.aws.lambda.simple.runtime.utils.InputStreamUtils;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Native {@link HttpResponse} wrapper with body as {@link InputStream} for
 * {@link SimpleHttpResponse}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class NativeInputStreamSimpleHttpResponse implements SimpleHttpResponse {

    private final HttpResponse<InputStream> httpResponse;

    public NativeInputStreamSimpleHttpResponse(@NotNull HttpResponse<InputStream> httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public int statusCode() {
        return httpResponse.statusCode();
    }

    @Override
    public @NotNull InputStream body() {
        return httpResponse.body();
    }

    @Override
    public @NotNull String bodyAsString() {
        return InputStreamUtils.getInputAsStringUTF8(body());
    }

    @Override
    public @NotNull Map<String, List<String>> headersMultiValues() {
        return httpResponse.headers().map();
    }

    @Override
    public @NotNull Map<String, String> headers() {
        return headersMultiValues().entrySet().stream()
                .filter(e -> !e.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().iterator().next()));
    }

    @Override
    public Optional<String> headerFirst(@NotNull String name) {
        return httpResponse.headers().firstValue(name);
    }

    @Override
    public String toString() {
        return httpResponse.toString();
    }
}
