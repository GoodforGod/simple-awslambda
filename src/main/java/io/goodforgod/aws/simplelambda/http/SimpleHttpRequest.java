package io.goodforgod.aws.simplelambda.http;

import io.goodforgod.aws.simplelambda.http.common.StringHttpRequest;
import java.nio.ByteBuffer;
import java.util.Map;
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
    static SimpleHttpRequest empty() {
        return StringHttpRequest.empty();
    }

    /**
     * @param headers to include in HttpRequest
     * @return simple http request with headers only
     */
    static SimpleHttpRequest ofHeaders(@NotNull Map<String, String> headers) {
        return StringHttpRequest.ofHeaders(headers);
    }

    @Nullable
    Publisher<ByteBuffer> body();

    /**
     * @return http header multi map
     */
    @NotNull
    Map<String, String> headers();
}
