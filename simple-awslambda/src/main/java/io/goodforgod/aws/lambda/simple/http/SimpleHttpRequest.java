package io.goodforgod.aws.lambda.simple.http;

import io.goodforgod.aws.lambda.simple.http.common.StringHttpRequest;
import io.goodforgod.http.common.HttpHeaders;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow.Publisher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public interface SimpleHttpRequest {

    /**
     * @return empty simple http request
     */
    @NotNull
    static SimpleHttpRequest empty() {
        return StringHttpRequest.empty();
    }

    /**
     * @param headers to include in HttpRequest
     * @return simple http request with headers only
     */
    @NotNull
    static SimpleHttpRequest ofHeaders(@NotNull HttpHeaders headers) {
        return StringHttpRequest.ofHeaders(headers);
    }

    @NotNull
    HttpHeaders headers();

    @Nullable
    Publisher<ByteBuffer> body();
}
