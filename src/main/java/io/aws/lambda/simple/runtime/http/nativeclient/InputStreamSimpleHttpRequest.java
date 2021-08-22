package io.aws.lambda.simple.runtime.http.nativeclient;

import io.aws.lambda.simple.runtime.http.SimpleHttpRequest;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Flow.Publisher;

/**
 * Handles {@link InputStream} as http request
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public class InputStreamSimpleHttpRequest implements SimpleHttpRequest {

    private final InputStream inputStream;
    private final Map<String, String> headers;

    private InputStreamSimpleHttpRequest(@NotNull InputStream inputStream, @NotNull Map<String, String> headers) {
        this.inputStream = inputStream;
        this.headers = headers;
    }

    public static InputStreamSimpleHttpRequest ofStream(@NotNull InputStream stream) {
        return ofStream(stream, Collections.emptyMap());
    }

    public static InputStreamSimpleHttpRequest ofStream(@NotNull InputStream stream, @NotNull Map<String, String> headers) {
        return new InputStreamSimpleHttpRequest(stream, headers);
    }

    @Override
    public @NotNull Publisher<ByteBuffer> body() {
        return HttpRequest.BodyPublishers.ofInputStream(() -> inputStream);
    }

    @Override
    public @NotNull Map<String, String> headers() {
        return headers;
    }

    @Override
    public String toString() {
        return "[inputStream=" + inputStream + ", headers=" + headers + ']';
    }
}
