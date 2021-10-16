package io.goodforgod.aws.simplelambda.http.nativeclient;

import io.goodforgod.aws.simplelambda.http.SimpleHttpRequest;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
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
    private final Map<String, String> headers;

    private InputStreamNativeHttpRequest(@NotNull InputStream inputStream, @NotNull Map<String, String> headers) {
        this.inputStream = inputStream;
        this.headers = headers;
    }

    public static InputStreamNativeHttpRequest ofStream(@NotNull InputStream stream) {
        return ofStream(stream, Collections.emptyMap());
    }

    public static InputStreamNativeHttpRequest ofStream(@NotNull InputStream stream, @NotNull Map<String, String> headers) {
        return new InputStreamNativeHttpRequest(stream, headers);
    }

    @Override
    public Publisher<ByteBuffer> body() {
        return HttpRequest.BodyPublishers.ofInputStream(() -> inputStream);
    }

    @Override
    public @NotNull Map<String, String> headers() {
        return headers;
    }

    @Override
    public String toString() {
        return "[headers=" + headers + ']';
    }
}