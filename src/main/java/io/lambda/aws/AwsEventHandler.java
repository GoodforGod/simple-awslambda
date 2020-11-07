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
import java.util.Map;

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
        final Pair<Class, Class> functionArgs = getInterfaceGenericType(function);
        logger.debug("Function %s with request type '%s' and response type '%s' found",
                function.getClass(), functionArgs.getRight(), functionArgs.getLeft());

        final long responseStart = getTime();
        logger.debug("Function request body: %s", requestEvent.getBody());
        logger.debug("Starting function processing...");
        final Object functionInput = converter.convertToType(requestEvent.getBody(), functionArgs.getRight());
        final Object functionOutput = function.handle(functionInput);
        logger.info("Function processing took: %s", timeSpent(responseStart));

        final String responseBody = converter.convertToJson(functionOutput);
        logger.debug("Function response body: %s", responseBody);
        return new AwsResponseEvent()
                .setBody(responseBody)
                .setHeaders(Map.of("Content-Type", "application/json"));
    }

    private <T extends Lambda> Pair<Class, Class> getInterfaceGenericType(T t) {
        final Class[] args = GenericTypeUtils.resolveInterfaceTypeArguments(t.getClass(), Lambda.class);
        if (args.length < 2)
            throw new IllegalArgumentException("Lambda interface is not correctly implemented, interface generic types must be set!");
        return new Pair<>(args[0], args[1]);
    }
}
