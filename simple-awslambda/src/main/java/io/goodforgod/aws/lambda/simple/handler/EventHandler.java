package io.goodforgod.aws.lambda.simple.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow.Publisher;
import org.jetbrains.annotations.NotNull;

/**
 * Process event as input stream and add logic for conversation, transformation, normalization of
 * events before processing them in {@link RequestHandler}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public interface EventHandler {

    /**
     * @param requestHandler for the event stream for handler
     * @param eventStream    to handler
     * @param context        of the request
     * @return response representation as JSON
     */
    @NotNull
    Publisher<ByteBuffer> handle(@NotNull RequestHandler requestHandler,
                                 @NotNull InputStream eventStream,
                                 @NotNull Context context);
}