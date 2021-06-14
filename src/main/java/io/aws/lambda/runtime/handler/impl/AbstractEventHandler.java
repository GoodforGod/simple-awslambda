package io.aws.lambda.runtime.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.model.Function;
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

    protected @NotNull Object getFunctionInput(@NotNull Class<?> funcInputType,
                                               @NotNull String funcInputValue,
                                               @NotNull Context context) {
        if (String.class.equals(funcInputType))
            return funcInputValue;

        return converter.convertToType(funcInputValue, funcInputType);
    }

    protected Object getFunctionOutput(Object funcOutValue,
                                       @NotNull Class<?> funcInputType,
                                       @NotNull Class<?> funcOutputType,
                                       @NotNull Context context) {
        if (funcOutValue == null)
            return null;

        if (funcOutValue instanceof String)
            return funcOutValue;

        return converter.convertToJson(funcOutValue);
    }

    protected <T extends RequestHandler> Function getFunctionArguments(T t) {
        final Class[] args = GenericTypeUtils.resolveInterfaceTypeArguments(t.getClass(), RequestHandler.class);
        if (args.length < 2)
            throw new IllegalArgumentException(
                    "Lambda interface is not correctly implemented, interface generic types must be set for input and output!");

        return new Function(args[0], args[1]);
    }
}
