package io.goodforgod.aws.simplelambda.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.simplelambda.convert.Converter;
import io.goodforgod.aws.simplelambda.handler.EventHandler;
import io.goodforgod.aws.simplelambda.handler.RequestFunction;
import io.goodforgod.aws.simplelambda.http.SimpleHttpRequest;
import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.utils.InputStreamUtils;
import io.goodforgod.aws.simplelambda.utils.ReflectionUtils;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow.Publisher;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    protected @NotNull Object getFunctionInput(@NotNull InputStream funcInputValue,
                                               @NotNull Class<?> funcInputType,
                                               @NotNull Class<?> funcOutputType,
                                               @NotNull Context context) {
        if (InputStream.class.equals(funcInputType)) {
            return funcInputType;
        }

        final String inputAsString = getInputAsString(funcInputValue);
        return getFunctionInput(inputAsString, funcInputType, funcOutputType, context);
    }

    protected @NotNull Object getFunctionInput(@NotNull String funcInputValue,
                                               @NotNull Class<?> funcInputType,
                                               @NotNull Class<?> funcOutputType,
                                               @NotNull Context context) {
        logger.debug("Converting input to '{}' for {}", funcInputType.getName(), context);

        if (String.class.equals(funcInputType)) {
            return funcInputValue;
        }

        return converter.fromString(funcInputValue, funcInputType);
    }

    protected Object getFunctionOutput(Object funcOutValue,
                                       @NotNull Class<?> funcInputType,
                                       @NotNull Class<?> funcOutputType,
                                       @NotNull Context context) {
        logger.debug("Converting output to '{}' for {}", funcOutputType.getName(), context);

        if (funcOutValue == null) {
            return null;
        } else if (funcOutValue instanceof InputStream) {
            return funcOutValue;
        } else if (funcOutValue instanceof String) {
            return funcOutValue;
        } else if (funcOutValue instanceof SimpleHttpResponse) {
            return ((SimpleHttpResponse) funcOutValue).body();
        } else if (funcOutValue instanceof SimpleHttpRequest) {
            return ((SimpleHttpRequest) funcOutValue).body();
        }

        return converter.toString(funcOutValue);
    }

    protected String getInputAsString(InputStream inputStream) {
        return InputStreamUtils.getStringUTF8FromInputStream(inputStream);
    }

    protected <T extends RequestHandler> RequestFunction getFunctionArguments(T t) {
        final Class[] args = ReflectionUtils.resolveInterfaceTypeArguments(t.getClass(), RequestHandler.class);
        if (args.length < 2) {
            throw new IllegalStateException(
                    "Lambda interface is not correctly implemented, interface generic types must be set for input and output!");
        }

        return new RequestFunction(args[0], args[1]);
    }

    /**
     * @param response to wrap into {@link Publisher}
     * @return event wrapped in {@link Publisher}
     */
    protected @NotNull Publisher<ByteBuffer> getResponsePublisher(Object response) {
        if (response == null) {
            return HttpRequest.BodyPublishers.noBody();
        } else if (response instanceof Publisher) {
            return (Publisher<ByteBuffer>) response;
        } else if (response instanceof InputStream) {
            return HttpRequest.BodyPublishers.ofInputStream(() -> (InputStream) response);
        } else if (response instanceof byte[]) {
            return HttpRequest.BodyPublishers.ofByteArray((byte[]) response);
        }

        return HttpRequest.BodyPublishers.ofString(response.toString(), StandardCharsets.UTF_8);
    }
}
