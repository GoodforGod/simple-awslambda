package io.aws.lambda.runtime;

import io.aws.lambda.runtime.model.AwsResponseEvent;
import io.aws.lambda.runtime.model.Pair;
import io.aws.lambda.runtime.utils.TimeUtils;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.logger.LambdaLogger;
import io.aws.lambda.runtime.model.AwsRequestEvent;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.reflect.GenericTypeUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Introspected
@Singleton
public class AwsEventHandler {

    private final Lambda function;
    private final Converter converter;
    private final LambdaLogger logger;

    @Inject
    public AwsEventHandler(Lambda function, Converter converter, LambdaLogger logger) {
        this.function = function;
        this.converter = converter;
        this.logger = logger;
    }

    @SuppressWarnings("unchecked")
    public @NotNull AwsResponseEvent handle(@NotNull AwsRequestEvent requestEvent) {
        logger.debug("Function request body: %s", requestEvent.getBody());
        final Pair<Class, Class> functionArgs = getInterfaceGenericType(function);
        logger.debug("Function %s with request type '%s' and response type '%s' found",
                function.getClass(), functionArgs.getRight(), functionArgs.getLeft());

        final long inputStart = TimeUtils.getTime();
        final Object functionInput = getFunctionInput(functionArgs.getRight(), requestEvent);
        logger.debug("Function input conversion took: %s", TimeUtils.timeSpent(inputStart));

        final long responseStart = TimeUtils.getTime();
        logger.debug("Starting function processing...");
        final Object functionOutput = function.handle(functionInput);
        logger.info("Function processing took: %s", TimeUtils.timeSpent(responseStart));

        final AwsResponseEvent responseEvent = getFunctionResponseEvent(functionOutput);
        logger.debug("Function response body: %s", responseEvent.getBody());
        return responseEvent;
    }

    private Object getFunctionInput(Class<?> inputType, AwsRequestEvent requestEvent) {
        if (String.class.equals(inputType))
            return requestEvent.getBody();

        if (AwsRequestEvent.class.equals(inputType))
            return requestEvent;

        return converter.convertToType(requestEvent.getBody(), inputType);
    }

    private AwsResponseEvent getFunctionResponseEvent(Object functionOutput) {
        if (functionOutput instanceof String)
            return new AwsResponseEvent().setBody((String) functionOutput);

        if (functionOutput instanceof AwsResponseEvent)
            return (AwsResponseEvent) functionOutput;

        final String json = converter.convertToJson(functionOutput);
        return new AwsResponseEvent().setBody(json);
    }

    private <T extends Lambda> Pair<Class, Class> getInterfaceGenericType(T t) {
        final Class[] args = GenericTypeUtils.resolveInterfaceTypeArguments(t.getClass(), Lambda.class);
        if (args.length < 2)
            throw new IllegalArgumentException("Lambda interface is not correctly implemented, interface generic types must be set!");

        return new Pair<>(args[0], args[1]);
    }
}
