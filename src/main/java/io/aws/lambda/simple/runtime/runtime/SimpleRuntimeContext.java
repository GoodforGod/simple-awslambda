package io.aws.lambda.simple.runtime.runtime;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.aws.lambda.simple.runtime.convert.Converter;
import io.aws.lambda.simple.runtime.convert.impl.GsonConverter;
import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.handler.impl.BodyEventHandler;
import io.aws.lambda.simple.runtime.handler.impl.InputEventHandler;
import io.aws.lambda.simple.runtime.http.SimpleHttpClient;
import io.aws.lambda.simple.runtime.http.nativeclient.NativeSimpleHttpClient;
import io.gson.adapters.config.GsonConfiguration;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * Simple Runtime without any DI that can be extended for more performant
 * workload.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.08.2021
 */
public class SimpleRuntimeContext implements RuntimeContext {

    private final SimpleHttpClient httpClient = new NativeSimpleHttpClient();
    private final Converter converter = new GsonConverter(new GsonConfiguration().builder().create());
    private final RequestHandler requestHandler;

    private EventHandler eventHandler;

    public SimpleRuntimeContext(@NotNull RequestHandler requestHandler) {
        Objects.requireNonNull(requestHandler, "RequestHandler can't be nullable!");
        this.requestHandler = requestHandler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(@NotNull Class<T> beanType) {
        if (Converter.class.isAssignableFrom(beanType)) {
            return (T) converter;
        }

        if (EventHandler.class.isAssignableFrom(beanType)) {
            if (eventHandler == null) {
                if (InputEventHandler.class.isAssignableFrom(beanType)) {
                    this.eventHandler = new InputEventHandler(requestHandler, converter);
                } else if (BodyEventHandler.class.isAssignableFrom(beanType)) {
                    this.eventHandler = new BodyEventHandler(requestHandler, converter);
                } else {
                    throw new IllegalStateException("Unknown EventHandler type implementation: " + beanType);
                }
            }

            return (T) this.eventHandler;
        }

        if (SimpleHttpClient.class.isAssignableFrom(beanType)) {
            return (T) httpClient;
        }

        if (RequestHandler.class.isAssignableFrom(beanType)) {
            return (T) requestHandler;
        }

        return null;
    }

    @Override
    public void close() {
        // do nothing
    }
}
