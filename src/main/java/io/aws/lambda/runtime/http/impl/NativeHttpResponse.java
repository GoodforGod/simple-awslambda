package io.aws.lambda.runtime.http.impl;

import io.aws.lambda.runtime.http.AwsHttpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class NativeHttpResponse implements AwsHttpResponse {

    private final java.net.http.HttpResponse<String> httpResponse;

    public NativeHttpResponse(java.net.http.HttpResponse<String> httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public int code() {
        return httpResponse.statusCode();
    }

    @Override
    public String body() {
        return httpResponse.body();
    }

    @Override
    public @NotNull Map<String, List<String>> headers() {
        return httpResponse.headers().map();
    }

    @Override
    public @NotNull String headerAnyOrThrow(@NotNull String name) {
        return httpResponse.headers().firstValue(name)
                .orElseThrow(() -> new IllegalArgumentException("Header not found with name: " + name));
    }

    @Override
    public String headerAny(@NotNull String name) {
        return httpResponse.headers().firstValue(name).orElse(null);
    }
}
