package io.goodforgod.aws.lambda.simple.handler.impl;

import static io.goodforgod.aws.lambda.simple.handler.impl.InputEventHandler.QUALIFIER;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.aws.lambda.simple.handler.Event;
import io.goodforgod.aws.lambda.simple.handler.EventHandler;
import io.goodforgod.aws.lambda.simple.utils.TimeUtils;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow.Publisher;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

/**
 * AWS Lambda Handler for handling raw event as it was passed to lambda.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Named(QUALIFIER)
@Singleton
public class InputEventHandler extends AbstractEventHandler implements EventHandler {

    public static final String QUALIFIER = "inputEvent";

    @Inject
    public InputEventHandler(Converter converter) {
        super(converter);
    }

    public @NotNull Publisher<ByteBuffer> handle(@NotNull Event event, @NotNull RequestHandler requestHandler) {
        logger.trace("Function input conversion started...");
        final long inputStart = (logger.isDebugEnabled())
                ? TimeUtils.getTime()
                : 0;

        final RequestFunction function = getFunctionArguments(requestHandler);
        logger.debug("Function '{}' execution started with input '{}' and output '{}'",
                requestHandler.getClass().getName(), function.input().getName(), function.output().getName());

        final Object functionInput = getFunctionInput(event.input(), function.input(), function.output(), event.context());
        if (logger.isDebugEnabled()) {
            logger.debug("Function input conversion took: {} millis", TimeUtils.timeTook(inputStart));
            logger.debug("Function input: {}", functionInput);
        }

        logger.trace("Function processing started...");
        final long responseStart = (logger.isInfoEnabled())
                ? TimeUtils.getTime()
                : 0;
        final Object functionOutput = requestHandler.handleRequest(functionInput, event.context());
        if (logger.isInfoEnabled()) {
            logger.info("Function processing took: {} millis", TimeUtils.timeTook(responseStart));
        }

        logger.trace("Function output conversion started...");
        final long outputStart = TimeUtils.getTime();
        final Object response = getFunctionOutput(functionOutput, function.input(), function.output(), event.context());
        if (logger.isDebugEnabled()) {
            logger.debug("Function output conversion took: {} millis", TimeUtils.timeTook(outputStart));
            logger.debug("Function output: {}", response);
        }

        return getResponsePublisher(response);
    }
}
