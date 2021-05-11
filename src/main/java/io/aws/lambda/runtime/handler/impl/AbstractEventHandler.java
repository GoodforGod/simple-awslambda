package io.aws.lambda.runtime.handler.impl;

import io.aws.lambda.runtime.Lambda;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.model.AwsRequestContext;
import io.aws.lambda.runtime.model.Pair;
import io.aws.lambda.runtime.model.gateway.AwsGatewayRequest;
import io.aws.lambda.runtime.model.gateway.AwsGatewayRequestBuilder;
import io.micronaut.core.reflect.GenericTypeUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public abstract class AbstractEventHandler implements EventHandler {

    protected final Converter converter;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public AbstractEventHandler(Converter converter) {
        this.converter = converter;
    }

    protected @NotNull Object getFunctionInput(@NotNull Class<?> inputType,
                                               @NotNull String event,
                                               @NotNull AwsRequestContext context) {
        if (String.class.equals(inputType))
            return event;

        if (AwsGatewayRequest.class.equals(inputType)) {
            return converter.convertToType(event, AwsGatewayRequestBuilder.class)
                    .setContext(context)
                    .build();
        }

        return converter.convertToType(event, inputType);
    }

    protected <T extends Lambda> Pair<Class, Class> getInterfaceGenericType(T t) {
        final Class[] args = GenericTypeUtils.resolveInterfaceTypeArguments(t.getClass(), Lambda.class);
        if (args.length < 2)
            throw new IllegalArgumentException("Lambda interface is not correctly implemented, interface generic types must be set!");

        return new Pair<>(args[0], args[1]);
    }
}
