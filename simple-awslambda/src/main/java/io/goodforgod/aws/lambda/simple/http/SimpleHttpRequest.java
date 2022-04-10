package io.goodforgod.aws.lambda.simple.http;

import io.goodforgod.http.common.HttpHeaders;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.Flow.Publisher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
public interface SimpleHttpRequest {

    @NotNull
    URI uri();

    @NotNull
    String method();

    @NotNull
    HttpHeaders headers();

    @Nullable
    Duration timeout();

    @Nullable
    Publisher<ByteBuffer> body();

    @NotNull
    static SimpleHttpRequestBuilder builder(@NotNull URI uri) {
        return new NativeSimpleHttpRequestBuilder(uri);
    }
}
