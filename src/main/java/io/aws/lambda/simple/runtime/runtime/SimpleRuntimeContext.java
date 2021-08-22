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
import org.jetbrains.annotations.NotNull;

/**
 * Simple Runtime without any DI that can be extended for more performant
 * workload.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.08.2021
 */
public abstract class SimpleRuntimeContext implements RuntimeContext {

    private SimpleHttpClient httpClient;
    private Converter converter;
    private EventHandler eventHandler;
    private RequestHandler requestHandler;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(@NotNull Class<T> beanType) {
        if (SimpleHttpClient.class.equals(beanType) || NativeSimpleHttpClient.class.equals(beanType)) {
            if (httpClient == null)
                httpClient = new NativeSimpleHttpClient();
            return (T) httpClient;
        }

        if (Converter.class.equals(beanType) || GsonConverter.class.equals(beanType)) {
            if (converter == null)
                converter = new GsonConverter(new GsonConfiguration().builder().create());
            return (T) converter;
        }

        if (EventHandler.class.equals(beanType) && eventHandler != null) {
            return (T) this.eventHandler;
        }

        if (InputEventHandler.class.equals(beanType)) {
            if (eventHandler == null) {
                final RequestHandler handler = getBean(RequestHandler.class);
                this.eventHandler = new InputEventHandler(handler, converter);
            }
            return (T) eventHandler;
        }

        if (BodyEventHandler.class.equals(beanType)) {
            if (eventHandler == null) {
                final RequestHandler handler = getBean(RequestHandler.class);
                this.eventHandler = new BodyEventHandler(handler, converter);
            }
            return (T) eventHandler;
        }

        if (RequestHandler.class.equals(beanType)) {
            if (requestHandler == null)
                this.requestHandler = createRequestHandler();
            return (T) requestHandler;
        }

        return null;
    }

    /**
     * @return instance of {@link RequestHandler} implementation
     */
    protected abstract @NotNull RequestHandler createRequestHandler();

    @Override
    public void close() throws Exception {
        // do nothing
    }
}
