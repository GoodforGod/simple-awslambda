package io.goodforgod.aws.lambda.simple.http;

import io.goodforgod.http.common.HttpHeaders;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.Flow.Publisher;
import org.jetbrains.annotations.Nullable;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
record NativeSimpleHttpRequest(URI uri,
                               String method,
                               HttpHeaders headers,
                               Duration timeout,
                               SimpleHttpBody httpBody)
        implements SimpleHttpRequest {

    @Override
    public @Nullable Publisher<ByteBuffer> body() {
        return (httpBody == null)
                ? null
                : httpBody.value();
    }
}
