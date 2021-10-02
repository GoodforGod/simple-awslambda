package io.aws.lambda.simple.runtime.runtime;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.aws.lambda.simple.runtime.convert.Converter;
import io.aws.lambda.simple.runtime.convert.gson.GsonConverter;
import io.aws.lambda.simple.runtime.convert.gson.SimpleGsonFactory;
import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.handler.impl.BodyEventHandler;
import io.aws.lambda.simple.runtime.handler.impl.InputEventHandler;
import io.aws.lambda.simple.runtime.http.SimpleHttpClient;
import io.aws.lambda.simple.runtime.http.nativeclient.NativeSimpleHttpClient;
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

    private final SimpleHttpClient httpClient;
    private final Converter converter;
    private final RequestHandler requestHandler;
    private final EventHandler eventHandler;

    public SimpleRuntimeContext(@NotNull RequestHandler requestHandler,
                                @NotNull Class<? extends EventHandler> eventHandlerType) {
        Objects.requireNonNull(requestHandler, "RequestHandler can't be nullable!");
        this.httpClient = new NativeSimpleHttpClient();
        this.converter = new GsonConverter(new SimpleGsonFactory().getGson());
        this.requestHandler = requestHandler;
        this.eventHandler = getEventHandler(eventHandlerType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(@NotNull Class<T> beanType) {
        if (Converter.class.isAssignableFrom(beanType)) {
            return (T) converter;
        }

        if (EventHandler.class.isAssignableFrom(beanType)) {
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

    protected EventHandler getEventHandler(@NotNull Class<? extends EventHandler> eventHandlerType) {
        if (InputEventHandler.class.isAssignableFrom(eventHandlerType)) {
            return new InputEventHandler(requestHandler, converter);
        } else if (BodyEventHandler.class.isAssignableFrom(eventHandlerType)) {
            return new BodyEventHandler(requestHandler, converter);
        } else {
            throw new IllegalStateException("Unknown EventHandler type implementation: " + eventHandlerType);
        }
    }

    @Override
    public void close() {
        // do nothing
    }
}
