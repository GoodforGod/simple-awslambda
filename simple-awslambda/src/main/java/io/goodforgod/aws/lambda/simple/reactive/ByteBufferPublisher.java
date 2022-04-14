package io.goodforgod.aws.lambda.simple.reactive;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

/**
 * Copy of JDK internal jdk.internal.net.http.common.SequentialScheduler.ByteArraySubscriber
 *
 * @author Anton Kurako (GoodforGod)
 * @since 10.10.2021
 */
class ByteBufferPublisher implements Flow.Publisher<ByteBuffer> {

    private static final int DEFAULT_BUFSIZE = 16 * 1024;

    private final int length;
    private final byte[] content;
    private final int offset;
    private final int bufSize;

    public ByteBufferPublisher(ByteBuffer byteBuffer) {
        this(byteBuffer.array());
    }

    public ByteBufferPublisher(byte[] content) {
        this(content, 0, content.length);
    }

    public ByteBufferPublisher(byte[] content, int offset, int length) {
        this.content = content;
        this.offset = offset;
        this.length = length;
        this.bufSize = DEFAULT_BUFSIZE;
    }

    private List<ByteBuffer> copy(byte[] content, int offset, int length) {
        final List<ByteBuffer> bufs = new ArrayList<>();
        if (length < bufSize) {
            return List.of(ByteBuffer.wrap(content));
        }

        while (length > 0) {
            ByteBuffer b = ByteBuffer.allocate(Math.min(bufSize, length));
            int max = b.capacity();
            int tocopy = Math.min(max, length);
            b.put(content, offset, tocopy);
            offset += tocopy;
            length -= tocopy;
            b.flip();
            bufs.add(b);
        }
        return bufs;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
        final List<ByteBuffer> copy = copy(content, offset, length);
        var delegate = new PullPublisher<>(copy);
        delegate.subscribe(subscriber);
    }
}
