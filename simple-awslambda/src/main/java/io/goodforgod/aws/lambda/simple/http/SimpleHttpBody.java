package io.goodforgod.aws.lambda.simple.http;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Http Simple Body contract
 *
 * @author Anton Kurako (GoodforGod)
 * @since 11.04.2022
 */
public interface SimpleHttpBody {

    @Nullable
    Flow.Publisher<ByteBuffer> value();

    @NotNull
    static SimpleHttpBody ofString(@NotNull String value) {
        return new StringHttpBody(value);
    }

    @NotNull
    static SimpleHttpBody ofInputStream(@NotNull InputStream stream) {
        return new InputStreamHttpBody(stream);
    }

    @NotNull
    static SimpleHttpBody ofPublisher(@NotNull Flow.Publisher<ByteBuffer> publisher) {
        return new PublisherHttpBody(publisher);
    }
}
