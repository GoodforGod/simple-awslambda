package io.goodforgod.aws.lambda.simple.reactive;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 22.08.2021
 */
public final class PublisherUtils {

    private PublisherUtils() {}

    private static class SimpleStringSubscriber extends ByteBufferSubscriber {

        public CompletableFuture<String> getBodyAsString() {
            return result().thenApply(r -> new String(r.array(), StandardCharsets.UTF_8));
        }
    }

    /**
     * @param publisher to extract byte data from
     * @return result converter string from bytes
     */
    @NotNull
    public static String asString(@NotNull Publisher<ByteBuffer> publisher) {
        final SimpleStringSubscriber subscriber = new SimpleStringSubscriber();
        publisher.subscribe(subscriber);
        return subscriber.getBodyAsString().orTimeout(10, TimeUnit.SECONDS).join();
    }

    /**
     * @param publisher to extract byte data from
     * @return result published from publisher
     */
    public static byte[] asBytes(@NotNull Publisher<ByteBuffer> publisher) {
        final ByteBufferSubscriber subscriber = new ByteBufferSubscriber();
        publisher.subscribe(subscriber);
        return subscriber.result().orTimeout(10, TimeUnit.SECONDS).join().array();
    }
}
