package io.goodforgod.aws.simplelambda.runtime;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.simplelambda.convert.Converter;
import io.goodforgod.aws.simplelambda.convert.gson.GsonConverterPropertyFactory;
import io.goodforgod.aws.simplelambda.handler.EventHandler;
import io.goodforgod.aws.simplelambda.handler.impl.BodyEventHandler;
import io.goodforgod.aws.simplelambda.handler.impl.InputEventHandler;
import io.goodforgod.aws.simplelambda.http.SimpleHttpClient;
import io.goodforgod.aws.simplelambda.http.nativeclient.NativeHttpClient;
import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple Runtime without any DI that can be extended for more performant
 * workload.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.08.2021
 */
public class SimpleRuntimeContext implements RuntimeContext {

    private RequestHandler requestHandler;
    private SimpleHttpClient simpleHttpClient;
    private InputEventHandler inputEventHandler;
    private BodyEventHandler bodyEventHandler;

    private final Converter converter;
    private final Function<RuntimeContext, RequestHandler> requestHandlerFunction;

    public SimpleRuntimeContext(@NotNull Function<RuntimeContext, RequestHandler> requestHandlerFunction) {
        Objects.requireNonNull(requestHandlerFunction, "RequestHandlerFunction can't be nullable!");
        this.converter = new GsonConverterPropertyFactory().build();
        this.requestHandlerFunction = requestHandlerFunction;
    }

    @Override
    public <T> T getBean(@NotNull Class<T> beanType) {
        return getBean(beanType, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(@NotNull Class<T> beanType,
                         @Nullable String qualifier) {
        if (Converter.class.isAssignableFrom(beanType)) {
            return (T) converter;
        }

        if (EventHandler.class.isAssignableFrom(beanType)) {
            if (InputEventHandler.class.isAssignableFrom(beanType)
                    || InputEventHandler.QUALIFIER.equals(qualifier)
                    || (EventHandler.class.equals(beanType) && qualifier == null)) {
                if (inputEventHandler == null) {
                    this.inputEventHandler = new InputEventHandler(getBean(RequestHandler.class), getBean(Converter.class));
                }
                return (T) this.inputEventHandler;
            } else if (BodyEventHandler.class.isAssignableFrom(beanType) || BodyEventHandler.QUALIFIER.equals(qualifier)) {
                if (bodyEventHandler == null) {
                    this.bodyEventHandler = new BodyEventHandler(getBean(RequestHandler.class), getBean(Converter.class));
                }
                return (T) this.bodyEventHandler;
            } else {
                throw new UnsupportedOperationException(
                        "Unknown EventHandler type is requested for qualifier: " + qualifier + ", and type " + beanType);
            }
        }

        if (SimpleHttpClient.class.isAssignableFrom(beanType)) {
            if (NativeHttpClient.class.isAssignableFrom(beanType)
                    || NativeHttpClient.QUALIFIER.equals(qualifier)
                    || (SimpleHttpClient.class.equals(beanType) && qualifier == null)) {
                if (simpleHttpClient == null) {
                    this.simpleHttpClient = new NativeHttpClient();
                }

                return (T) this.simpleHttpClient;
            } else {
                throw new UnsupportedOperationException(
                        "Unknown SimpleHttpClient type is requested for qualifier: " + qualifier + ", and type " + beanType);
            }
        }

        if (RequestHandler.class.isAssignableFrom(beanType)) {
            if (requestHandler == null) {
                this.requestHandler = requestHandlerFunction.apply(this);
                Objects.requireNonNull(this.requestHandler, "RequestHandler can't be nullable!");
            }
            return (T) requestHandler;
        }

        return null;
    }

    @Override
    public void close() {
        // do nothing
    }
}
