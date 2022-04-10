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
public interface SimpleHttpRequestBuilder {

    @NotNull
    default SimpleHttpRequestBuilder method(@NotNull HttpMethod method) {
        return method(method.toString());
    }

    @NotNull
    SimpleHttpRequestBuilder method(@NotNull String method);

    @NotNull
    SimpleHttpRequestBuilder headers(@NotNull HttpHeaders headers);

    @NotNull
    SimpleHttpRequestBuilder timeout(@NotNull Duration timeout);

    @NotNull
    SimpleHttpRequestBuilder body(@NotNull SimpleHttpBody body);

    @NotNull
    SimpleHttpRequest build();

    @NotNull
    static SimpleHttpRequestBuilder builder(@NotNull URI uri) {
        return new NativeSimpleHttpRequestBuilder(uri);
    }
}
