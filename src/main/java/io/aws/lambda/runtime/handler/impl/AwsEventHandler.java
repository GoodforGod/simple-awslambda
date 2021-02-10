package io.aws.lambda.runtime.handler.impl;

import io.aws.lambda.runtime.Lambda;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.logger.LambdaLogger;
import io.aws.lambda.runtime.model.AwsRequestContext;
import io.aws.lambda.runtime.model.Pair;
import io.aws.lambda.runtime.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * AWS Lambda Handler for handling direct requests for AWS Lambda.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Singleton
public class AwsEventHandler extends AbstractEventHandler implements EventHandler {

    private final Lambda function;

    @Inject
    public AwsEventHandler(Lambda function, Converter converter, LambdaLogger logger) {
        super(converter, logger);
        this.function = function;
    }

    @SuppressWarnings("unchecked")
    public String handle(@NotNull String event, @NotNull AwsRequestContext context) {
        logger.debug("Function request event body: %s", event);
        final Pair<Class, Class> funcArgs = getInterfaceGenericType(function);
        logger.debug("Function to handle '%s' with Request type '%s' and Response type '%s'",
                function.getClass().getName(), funcArgs.getRight().getName(), funcArgs.getLeft().getName());

        logger.debug("Function input conversion started...");
        final long inputStart = TimeUtils.getTime();
        final Object functionInput = getFunctionInput(funcArgs.getRight(), event);
        logger.debug("Function input conversion took: %s", TimeUtils.timeSpent(inputStart));

        logger.debug("Function processing started...");
        final long responseStart = TimeUtils.getTime();
        final Object functionOutput = function.handle(functionInput);
        logger.info("Function processing took: %s", TimeUtils.timeSpent(responseStart));

        logger.debug("Function output conversion started...");
        final long outputStart = TimeUtils.getTime();
        final String response = getFunctionResponse(functionOutput);
        logger.debug("Function output conversion took: %s", TimeUtils.timeSpent(outputStart));
        logger.debug("Function response body: %s", response);
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
