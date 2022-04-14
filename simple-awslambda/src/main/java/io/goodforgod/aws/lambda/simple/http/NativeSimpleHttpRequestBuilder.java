package io.goodforgod.aws.lambda.simple.http;

import io.goodforgod.http.common.HttpHeaders;
import io.goodforgod.http.common.HttpMethod;
import java.net.URI;
import java.time.Duration;
import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 11.04.2022
 */
final class NativeSimpleHttpRequestBuilder implements SimpleHttpRequestBuilder {

    private final URI uri;

    private String method;
    private HttpHeaders headers;
    private Duration timeout;
    private SimpleHttpBody body;

    NativeSimpleHttpRequestBuilder(@NotNull URI uri) {
        this.uri = uri;
    }

    @Override
    public @NotNull SimpleHttpRequestBuilder method(@NotNull String method) {
        this.method = method;
        return this;
    }

    @Override
    public @NotNull SimpleHttpRequestBuilder headers(@NotNull HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public @NotNull SimpleHttpRequestBuilder timeout(@NotNull Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public @NotNull SimpleHttpRequestBuilder body(@NotNull SimpleHttpBody body) {
        this.body = body;
        return this;
    }

    @Override
    public @NotNull SimpleHttpRequest build() {
        if (method == null || method.isBlank()) {
            throw new IllegalStateException("Http Request Method can't be nullable or empty!");
        }

        if (HttpMethod.GET.name().equalsIgnoreCase(method) && body != null) {
            throw new IllegalStateException("Http Request Method GET can't contain body!");
        }

        var finalHeaders = (headers == null)
                ? HttpHeaders.empty()
                : headers;

        return new NativeSimpleHttpRequest(uri, method, finalHeaders, timeout, body);
    }
}
