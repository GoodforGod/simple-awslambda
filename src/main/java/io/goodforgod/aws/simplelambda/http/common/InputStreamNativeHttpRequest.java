package io.goodforgod.aws.simplelambda.http.common;

import io.goodforgod.aws.simplelambda.http.SimpleHttpRequest;
import io.goodforgod.http.common.HttpHeaders;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow.Publisher;
import org.jetbrains.annotations.NotNull;

/**
 * Handles {@link InputStream} as http request
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public final class InputStreamNativeHttpRequest implements SimpleHttpRequest {

    private final InputStream inputStream;
    private final HttpHeaders headers;

    private InputStreamNativeHttpRequest(@NotNull InputStream inputStream, @NotNull HttpHeaders headers) {
        this.inputStream = inputStream;
        this.headers = headers;
    }

    public static InputStreamNativeHttpRequest ofStream(@NotNull InputStream stream) {
        return ofStream(stream, HttpHeaders.empty());
    }

    public static InputStreamNativeHttpRequest ofStream(@NotNull InputStream stream, @NotNull HttpHeaders headers) {
        return new InputStreamNativeHttpRequest(stream, headers);
    }

    @Override
    public Publisher<ByteBuffer> body() {
        return HttpRequest.BodyPublishers.ofInputStream(() -> inputStream);
    }

    @Override
    public @NotNull HttpHeaders headers() {
        return headers;
    }

    @Override
    public String toString() {
        return "[headers=" + headers + ']';
    }
}
