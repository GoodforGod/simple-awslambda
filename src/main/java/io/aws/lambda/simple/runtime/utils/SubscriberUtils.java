package io.aws.lambda.simple.runtime.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Please Add Description Here.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.08.2021
 */
public class SubscriberUtils {

    private SubscriberUtils() {}

    private static class SimpleByteBufferSubscriber implements Flow.Subscriber<ByteBuffer> {

        private final Function<byte[], ByteBuffer> finisher;
        private final CompletableFuture<ByteBuffer> result = new CompletableFuture<>();
        private final List<ByteBuffer> received = new ArrayList<>();

        private volatile Flow.Subscription subscription;

        public SimpleByteBufferSubscriber() {
            this(ByteBuffer::wrap);
        }

        public SimpleByteBufferSubscriber(Function<byte[], ByteBuffer> finisher) {
            this.finisher = finisher;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            if (this.subscription != null) {
                subscription.cancel();
                return;
            }
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ByteBuffer items) {
            received.add(items);
        }

        @Override
        public void onError(Throwable throwable) {
            received.clear();
            result.completeExceptionally(throwable);
        }

        int remaining(List<ByteBuffer> bufs, int max) {
            long remain = 0;
            synchronized (bufs) {
                for (ByteBuffer buf : bufs) {
                    remain += buf.remaining();
                    if (remain > max) {
                        throw new IllegalArgumentException("too many bytes");
                    }
                }
            }
            return (int) remain;
        }

        byte[] join(List<ByteBuffer> bytes) {
            int size = remaining(bytes, Integer.MAX_VALUE);
            byte[] res = new byte[size];
            int from = 0;
            for (ByteBuffer b : bytes) {
                int l = b.remaining();
                b.get(res, from, l);
                from += l;
            }
            return res;
        }

        @Override
        public void onComplete() {
            try {
                final byte[] array = join(received);
                final ByteBuffer buffer = finisher.apply(array);
                result.complete(buffer);
                received.clear();
            } catch (IllegalArgumentException e) {
                result.completeExceptionally(e);
            }
        }

        public CompletableFuture<ByteBuffer> getBody() {
            return result;
        }
    }

    private static class SimpleStringSubscriber extends SimpleByteBufferSubscriber {

        public CompletableFuture<String> getBodyAsString() {
            return getBody().thenApply(r -> new String(r.array(), StandardCharsets.UTF_8));
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
        final SimpleByteBufferSubscriber subscriber = new SimpleByteBufferSubscriber();
        publisher.subscribe(subscriber);
        return subscriber.getBody().orTimeout(10, TimeUnit.SECONDS).join().array();
    }
}
