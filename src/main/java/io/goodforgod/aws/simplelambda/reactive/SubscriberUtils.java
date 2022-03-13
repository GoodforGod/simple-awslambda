package io.goodforgod.aws.simplelambda.reactive;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.TimeUnit;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 22.08.2021
 */
public final class SubscriberUtils {

    private SubscriberUtils() {}

    private static class SimpleStringSubscriber extends ByteBufferSubscriber {

        public CompletableFuture<String> getBodyAsString() {
            return result().thenApply(r -> new String(r.array(), StandardCharsets.UTF_8));
        }
    }

    /**
     * @param publisher to extract byte data from
     * @return result converter string from bytes
     */
    public static String getPublisherString(Publisher<ByteBuffer> publisher) {
        final SimpleStringSubscriber subscriber = new SimpleStringSubscriber();
        publisher.subscribe(subscriber);
        return subscriber.getBodyAsString().orTimeout(10, TimeUnit.SECONDS).join();
    }

    /**
     * @param publisher to extract byte data from
     * @return result published from publisher
     */
    public static byte[] getPublisherBytes(Publisher<ByteBuffer> publisher) {
        final ByteBufferSubscriber subscriber = new ByteBufferSubscriber();
        publisher.subscribe(subscriber);
        return subscriber.result().orTimeout(10, TimeUnit.SECONDS).join().array();
    }
}
