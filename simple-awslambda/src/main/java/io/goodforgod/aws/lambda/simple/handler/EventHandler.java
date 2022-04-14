package io.goodforgod.aws.lambda.simple.handler;

import com.amazonaws.services.lambda.runtime.RequestHandler;
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
     * @param requestHandler to handle event input
     * @param event          to handle
     * @return response as reactive byte buffered
     */
    @NotNull
    Publisher<ByteBuffer> handle(@NotNull Event event, @NotNull RequestHandler requestHandler);
}
