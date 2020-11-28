package io.lambda.aws;

import io.lambda.aws.convert.Converter;
import io.lambda.aws.logger.LambdaLogger;
import io.lambda.aws.model.AwsRequestEvent;
import io.lambda.aws.model.AwsResponseEvent;
import io.lambda.aws.model.Pair;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.reflect.GenericTypeUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.lambda.aws.utils.TimeUtils.getTime;
import static io.lambda.aws.utils.TimeUtils.timeSpent;

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

    public AwsResponseEvent handle(AwsRequestEvent requestEvent) {
        logger.debug("Function request body: %s", requestEvent.getBody());
        final Pair<Class, Class> functionArgs = getInterfaceGenericType(function);
        logger.debug("Function %s with request type '%s' and response type '%s' found",
                function.getClass(), functionArgs.getRight(), functionArgs.getLeft());

        final long inputStart = getTime();
        final Object functionInput = getFunctionInput(functionArgs.getRight(), requestEvent);
        logger.debug("Function input conversion took: %s", timeSpent(inputStart));

        final long responseStart = getTime();
        logger.debug("Starting function processing...");
        final Object functionOutput = function.handle(functionInput);
        logger.info("Function processing took: %s", timeSpent(responseStart));

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
