package io.aws.lambda.simple.runtime.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.aws.lambda.simple.runtime.convert.Converter;
import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.handler.RequestFunction;
import io.aws.lambda.simple.runtime.utils.InputStreamUtils;
import io.aws.lambda.simple.runtime.utils.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public abstract class AbstractEventHandler implements EventHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Converter converter;

    protected AbstractEventHandler(Converter converter) {
        this.converter = converter;
    }

    protected @NotNull Object getFunctionInput(@NotNull Class<?> funcInputType,
                                               @NotNull InputStream funcInputValue,
                                               @NotNull Context context) {
        if (InputStream.class.equals(funcInputType))
            return funcInputType;

        final String inputAsString = getInputAsString(funcInputValue);
        return getFunctionInput(funcInputType, inputAsString, context);
    }

    protected @NotNull Object getFunctionInput(@NotNull Class<?> funcInputType,
                                               @NotNull String funcInputValue,
                                               @NotNull Context context) {
        logger.debug("Converting input to '{}' for {}", funcInputType, context);

        if (String.class.equals(funcInputType) || Object.class.equals(funcInputType))
            return funcInputValue;

        return converter.fromJson(funcInputValue, funcInputType);
    }

    protected Object getFunctionOutput(Object funcOutValue,
                                       @NotNull Class<?> funcInputType,
                                       @NotNull Class<?> funcOutputType,
                                       @NotNull Context context) {
        logger.debug("Converting output to '{}' for {}", funcOutputType, context);

        if (funcOutValue == null)
            return null;
        if (funcOutValue instanceof String)
            return funcOutValue;

        return converter.toJson(funcOutValue);
    }

    protected String getInputAsString(InputStream inputStream) {
        return InputStreamUtils.getInputAsStringUTF8(inputStream);
    }

    protected <T extends RequestHandler> RequestFunction getFunctionArguments(T t) {
        final Class[] args = ReflectionUtils.resolveInterfaceTypeArguments(t.getClass(), RequestHandler.class);
        if (args.length < 2)
            throw new IllegalStateException(
                    "Lambda interface is not correctly implemented, interface generic types must be set for input and output!");

        return new RequestFunction(args[0], args[1]);
    }
}
