package io.goodforgod.aws.simplelambda.reactive;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Copy of JDK internal jdk.internal.net.http.ResponseSubscribers.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 10.10.2021
 */
public class ByteBufferSubscriber implements Flow.Subscriber<ByteBuffer> {

    private final Function<byte[], ByteBuffer> finisher;
    private final CompletableFuture<ByteBuffer> result = new CompletableFuture<>();
    private final List<ByteBuffer> received = new ArrayList<>();

    private volatile Flow.Subscription subscription;

    public ByteBufferSubscriber() {
        this(ByteBuffer::wrap);
    }

    public ByteBufferSubscriber(Function<byte[], ByteBuffer> finisher) {
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

    private byte[] join(List<ByteBuffer> bytes) {
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

    public @NotNull CompletableFuture<ByteBuffer> result() {
        return result;
    }
}
