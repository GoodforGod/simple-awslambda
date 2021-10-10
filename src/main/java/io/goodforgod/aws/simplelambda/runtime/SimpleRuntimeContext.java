package io.goodforgod.aws.simplelambda.runtime;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.simplelambda.convert.Converter;
import io.goodforgod.aws.simplelambda.convert.gson.GsonConverterPropertyFactory;
import io.goodforgod.aws.simplelambda.handler.EventHandler;
import io.goodforgod.aws.simplelambda.handler.impl.BodyEventHandler;
import io.goodforgod.aws.simplelambda.handler.impl.InputEventHandler;
import io.goodforgod.aws.simplelambda.http.SimpleHttpClient;
import io.goodforgod.aws.simplelambda.http.nativeclient.NativeSimpleHttpClient;
import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Simple Runtime without any DI that can be extended for more performant
 * workload.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.08.2021
 */
public class SimpleRuntimeContext implements RuntimeContext {

    private SimpleHttpClient simpleHttpClient;
    private final Converter converter;
    private final RequestHandler requestHandler;
    private final InputEventHandler inputEventHandler;
    private final BodyEventHandler bodyEventHandler;

    public SimpleRuntimeContext(@NotNull Function<RuntimeContext, RequestHandler> requestHandlerFunction) {
        Objects.requireNonNull(requestHandlerFunction, "RequestHandlerFunction can't be nullable!");
        this.converter = new GsonConverterPropertyFactory().build();
        this.requestHandler = requestHandlerFunction.apply(this);
        Objects.requireNonNull(requestHandler, "RequestHandler can't be nullable!");
        this.inputEventHandler = new InputEventHandler(requestHandler, converter);
        this.bodyEventHandler = new BodyEventHandler(requestHandler, converter);
    }

    @Override
    public <T> T getBean(@NotNull Class<T> beanType) {
        return getBean(beanType, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(@NotNull Class<T> beanType, String qualifier) {
        if (Converter.class.isAssignableFrom(beanType)) {
            return (T) converter;
        }

        if (EventHandler.class.isAssignableFrom(beanType)) {
            if (InputEventHandler.class.isAssignableFrom(beanType) || InputEventHandler.QUALIFIER.equals(qualifier)) {
                return (T) this.inputEventHandler;
            } else if (BodyEventHandler.class.isAssignableFrom(beanType) || BodyEventHandler.QUALIFIER.equals(qualifier)) {
                return (T) this.bodyEventHandler;
            } else if (EventHandler.class.equals(beanType) && qualifier == null) {
                return (T) this.inputEventHandler;
            } else {
                throw new UnsupportedOperationException(
                        "Unknown EventHandler type is requested for qualifier: " + qualifier + ", and type " + beanType);
            }
        }

        if (SimpleHttpClient.class.isAssignableFrom(beanType)) {
            if (NativeSimpleHttpClient.class.isAssignableFrom(beanType) || NativeSimpleHttpClient.QUALIFIER.equals(qualifier)) {
                if (simpleHttpClient == null) {
                    this.simpleHttpClient = new NativeSimpleHttpClient();
                }

                return (T) this.simpleHttpClient;
            } else if (SimpleHttpClient.class.equals(beanType) && qualifier == null) {
                if (simpleHttpClient == null) {
                    this.simpleHttpClient = new NativeSimpleHttpClient();
                }

                return (T) this.simpleHttpClient;
            } else {
                throw new UnsupportedOperationException(
                        "Unknown SimpleHttpClient type is requested for qualifier: " + qualifier + ", and type " + beanType);
            }
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
