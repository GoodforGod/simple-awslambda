package io.aws.lambda.runtime.handler.impl;

import io.aws.lambda.runtime.Lambda;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.logger.LambdaLogger;
import io.aws.lambda.runtime.model.AwsGatewayRequest;
import io.aws.lambda.runtime.model.Pair;
import io.micronaut.core.reflect.GenericTypeUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public abstract class AbstractEventHandler implements EventHandler {

    protected final Converter converter;
    protected final LambdaLogger logger;

    public AbstractEventHandler(Converter converter, LambdaLogger logger) {
        this.converter = converter;
        this.logger = logger;
    }

    protected @NotNull Object getFunctionInput(@NotNull Class<?> inputType, @NotNull String event) {
        if (String.class.equals(inputType))
            return event;

        if (AwsGatewayRequest.class.equals(inputType))
            return event;

        return converter.convertToType(event, inputType);
    }

    protected <T extends Lambda> Pair<Class, Class> getInterfaceGenericType(T t) {
        final Class[] args = GenericTypeUtils.resolveInterfaceTypeArguments(t.getClass(), Lambda.class);
        if (args.length < 2)
            throw new IllegalArgumentException("Lambda interface is not correctly implemented, interface generic types must be set!");

        return new Pair<>(args[0], args[1]);
    }
}
