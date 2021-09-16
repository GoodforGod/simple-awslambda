package io.aws.lambda.simple.runtime.http;

import io.aws.lambda.simple.runtime.http.nativeclient.StringSimpleHttpRequest;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.Flow.Publisher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public interface SimpleHttpRequest {

    /**
     * @return empty simple http request
     */
    static SimpleHttpRequest empty() {
        return StringSimpleHttpRequest.empty();
    }

    /**
     * @param headers to include in HttpRequest
     * @return simple http request with headers only
     */
    static SimpleHttpRequest ofHeaders(@NotNull Map<String, String> headers) {
        return StringSimpleHttpRequest.ofHeaders(headers);
    }

    /**
     * @return body as {@link InputStream}
     */
    @NotNull
    Publisher<ByteBuffer> body();

    /**
     * @return http header multi map
     */
    @NotNull
    Map<String, String> headers();
}
