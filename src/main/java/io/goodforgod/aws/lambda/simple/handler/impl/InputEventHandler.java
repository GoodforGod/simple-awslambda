package io.goodforgod.aws.lambda.simple.handler.impl;

import static io.goodforgod.aws.lambda.simple.handler.impl.InputEventHandler.QUALIFIER;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.aws.lambda.simple.handler.EventHandler;
import io.goodforgod.aws.lambda.simple.handler.RequestFunction;
import io.goodforgod.aws.lambda.simple.utils.TimeUtils;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow.Publisher;
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

    public static final String QUALIFIER = "input-event";

    private final RequestHandler requestHandler;

    @Inject
    public InputEventHandler(RequestHandler requestHandler, Converter converter) {
        super(converter);
        this.requestHandler = requestHandler;
    }

    public @NotNull Publisher<ByteBuffer> handle(@NotNull InputStream eventStream, @NotNull Context context) {
        logger.trace("Function input conversion started...");
        final long inputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;

        final RequestFunction function = getFunctionArguments(requestHandler);
        logger.debug("Function '{}' execution started with input '{}' and output '{}'",
                requestHandler.getClass().getName(), function.getInput().getName(), function.getOutput().getName());

        final Object functionInput = getFunctionInput(eventStream, function.getInput(), function.getOutput(), context);
        if (logger.isDebugEnabled()) {
            logger.debug("Function input conversion took: {} millis", TimeUtils.timeTook(inputStart));
            logger.debug("Function input: {}", functionInput);
        }

        logger.trace("Function processing started...");
        final long responseStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;
        final Object functionOutput = requestHandler.handleRequest(functionInput, context);
        if (logger.isInfoEnabled()) {
            logger.info("Function processing took: {} millis", TimeUtils.timeTook(responseStart));
        }

        logger.trace("Function output conversion started...");
        final long outputStart = TimeUtils.getTime();
        final Object response = getFunctionOutput(functionOutput, function.getInput(), function.getOutput(), context);
        if (logger.isDebugEnabled()) {
            logger.debug("Function output conversion took: {} millis", TimeUtils.timeTook(outputStart));
            logger.debug("Function output: {}", response);
        }

        return getResponsePublisher(response);
    }
}
