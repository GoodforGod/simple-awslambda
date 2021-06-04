package io.aws.lambda.runtime.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import io.aws.lambda.runtime.Lambda;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.model.Pair;
import io.aws.lambda.runtime.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * AWS Lambda Handler for handling raw event as it was passed to lambda.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Singleton
public class RawEventHandler extends AbstractEventHandler implements EventHandler {

    protected final Lambda function;

    @Inject
    public RawEventHandler(Lambda function, Converter converter) {
        super(converter);
        this.function = function;
    }

    @SuppressWarnings("unchecked")
    public String handle(@NotNull String event, @NotNull Context context) {
        logger.debug("Function request event body: {}", event);
        final Pair<Class, Class> funcArgs = getInterfaceGenericType(function);
        logger.debug("Function to handle '{}' with Request type '{}' and Response type '{}'",
                function.getClass().getName(), funcArgs.getRight().getName(), funcArgs.getLeft().getName());

        logger.debug("Function input conversion started...");
        final long inputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;
        final Object functionInput = getFunctionInput(funcArgs.getRight(), event, context);
        if (logger.isDebugEnabled()) {
            logger.debug("Function input conversion took: {} millis", TimeUtils.timeTook(inputStart));
            logger.debug("Function input: {}", functionInput);
        }

        logger.debug("Function processing started...");
        final long responseStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;
        final Object functionOutput = function.handle(functionInput, context);
        if (logger.isInfoEnabled())
            logger.info("Function processing took: {} millis", TimeUtils.timeTook(responseStart));

        logger.debug("Function output conversion started...");
        final long outputStart = TimeUtils.getTime();
        final String response = getFunctionResponse(functionOutput);
        if (logger.isDebugEnabled()) {
            logger.debug("Function output conversion took: {} millis", TimeUtils.timeTook(outputStart));
            logger.debug("Function response body: {}", response);
        }

        return response;
    }

    private String getFunctionResponse(Object functionOutput) {
        if (functionOutput == null)
            return null;

        if (functionOutput instanceof String)
            return ((String) functionOutput);

        return converter.convertToJson(functionOutput);
    }
}
