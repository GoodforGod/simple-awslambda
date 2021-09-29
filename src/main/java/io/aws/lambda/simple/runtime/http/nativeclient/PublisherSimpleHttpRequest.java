package io.aws.lambda.simple.runtime.http.nativeclient;

import io.aws.lambda.simple.runtime.http.SimpleHttpRequest;
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
public class PublisherSimpleHttpRequest implements SimpleHttpRequest {

    private final Publisher<ByteBuffer> publisher;
    private final Map<String, String> headers;

    private PublisherSimpleHttpRequest(Publisher<ByteBuffer> publisher, @NotNull Map<String, String> headers) {
        this.publisher = publisher;
        this.headers = headers;
    }

    public static PublisherSimpleHttpRequest ofPublisher(@NotNull Publisher<ByteBuffer> publisher) {
        return ofPublisher(publisher, Collections.emptyMap());
    }

    public static PublisherSimpleHttpRequest ofPublisher(@NotNull Publisher<ByteBuffer> publisher,
                                                         @NotNull Map<String, String> headers) {
        return new PublisherSimpleHttpRequest(publisher, headers);
    }

    @Override
    public @NotNull Publisher<ByteBuffer> body() {
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
