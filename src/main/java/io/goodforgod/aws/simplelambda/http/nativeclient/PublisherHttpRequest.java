package io.goodforgod.aws.simplelambda.http.nativeclient;

import io.goodforgod.aws.simplelambda.http.SimpleHttpRequest;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Flow.Publisher;
import org.jetbrains.annotations.NotNull;

/**
 * Handles {@link Publisher} as http request
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public final class PublisherHttpRequest implements SimpleHttpRequest {

    private final Publisher<ByteBuffer> publisher;
    private final Map<String, String> headers;

    private PublisherHttpRequest(Publisher<ByteBuffer> publisher, @NotNull Map<String, String> headers) {
        this.publisher = publisher;
        this.headers = headers;
    }

    public static PublisherHttpRequest ofPublisher(@NotNull Publisher<ByteBuffer> publisher) {
        return ofPublisher(publisher, Collections.emptyMap());
    }

    public static PublisherHttpRequest ofPublisher(@NotNull Publisher<ByteBuffer> publisher,
                                                   @NotNull Map<String, String> headers) {
        return new PublisherHttpRequest(publisher, headers);
    }

    @Override
    public Publisher<ByteBuffer> body() {
        return publisher;
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
