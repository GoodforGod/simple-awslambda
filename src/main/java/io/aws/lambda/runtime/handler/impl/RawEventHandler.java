package io.aws.lambda.runtime.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.model.Function;
import io.aws.lambda.runtime.utils.TimeUtils;
import io.micronaut.core.annotation.Introspected;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * AWS Lambda Handler for handling raw event as it was passed to lambda.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Introspected
@Singleton
public class RawEventHandler extends AbstractEventHandler implements EventHandler {

    private final RequestHandler requestHandler;

    @Inject
    public RawEventHandler(RequestHandler requestHandler, Converter converter) {
        super(converter);
        this.requestHandler = requestHandler;
    }

    @SuppressWarnings("unchecked")
    public String handle(@NotNull String event, @NotNull Context context) {
        logger.debug("Function request event body: {}", event);
        final Function function = getFunctionArguments(requestHandler);
        logger.debug("Function to handle '{}' with Request type '{}' and Response type '{}'",
                requestHandler.getClass().getName(), function.getInput().getName(), function.getOutput().getName());

        logger.debug("Function input conversion started...");
        final long inputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;
        final Object functionInput = getFunctionInput(function.getInput(), event, context);
        if (logger.isDebugEnabled()) {
            logger.debug("Function input conversion took: {} millis", TimeUtils.timeTook(inputStart));
            logger.debug("Function input: {}", functionInput);
        }

        logger.debug("Function processing started...");
        final long responseStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;
        final Object functionOutput = requestHandler.handleRequest(functionInput, context);
        if (logger.isInfoEnabled())
            logger.info("Function processing took: {} millis", TimeUtils.timeTook(responseStart));

        logger.debug("Function output conversion started...");
        final long outputStart = TimeUtils.getTime();
        final Object response = getFunctionOutput(functionOutput, function.getInput(), function.getOutput(), context);
        if (logger.isDebugEnabled()) {
            logger.debug("Function output conversion took: {} millis", TimeUtils.timeTook(outputStart));
            logger.debug("Function response body: {}", response);
        }

        return (response == null) ? null : response.toString();
    }
}
