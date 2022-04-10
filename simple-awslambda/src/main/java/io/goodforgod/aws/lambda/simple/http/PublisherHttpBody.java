package io.goodforgod.aws.lambda.simple.http;

import java.nio.ByteBuffer;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Publisher;

/**
 * Handles {@link Publisher} as http request
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
record PublisherHttpBody(Publisher<ByteBuffer> publisher) implements SimpleHttpBody {

    @Override
    public Flow.Publisher<ByteBuffer> value() {
        return publisher;
    }
}
