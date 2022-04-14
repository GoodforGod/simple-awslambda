package io.goodforgod.aws.lambda.simple.testing;

import com.amazonaws.services.lambda.runtime.Context;
import io.goodforgod.aws.lambda.simple.AwsRuntimeClient;
import io.goodforgod.aws.lambda.simple.handler.Event;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpBody;
import io.goodforgod.aws.lambda.simple.reactive.PublisherUtils;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;
import org.jetbrains.annotations.NotNull;

/**
 * Testing AwsRuntimeClient
 *
 * @author Anton Kurako (GoodforGod)
 * @since 13.04.2022
 */
final class TestingAwsRuntimeClient implements AwsRuntimeClient {

    record SimpleEvent(InputStream input, Context context) implements Event {}

    private InputStream event;
    private Throwable throwable;
    private byte[] result;

    void setEvent(InputStream event) {
        this.event = event;
    }

    void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public @NotNull URI getAwsRuntimeApi() {
        return URI.create("http://localhost:8080/");
    }

    @Override
    public @NotNull Event getNextEvent(@NotNull URI runtimeEndpoint) {
        return new SimpleEvent(event, EventContextBuilder.empty());
    }

    @Override
    public void reportInvocationSuccess(@NotNull URI runtimeEndpoint,
                                        @NotNull SimpleHttpBody lambdaResult,
                                        @NotNull Context context) {
        final Flow.Publisher<ByteBuffer> value = lambdaResult.value();
        if (value == null) {
            this.result = new byte[0];
        } else {
            this.result = PublisherUtils.asBytes(value);
        }
    }

    @Override
    public void reportInitializationError(@NotNull URI runtimeEndpoint,
                                          @NotNull Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public void reportInvocationError(@NotNull URI runtimeEndpoint,
                                      @NotNull Throwable throwable,
                                      @NotNull Context context) {
        reportInitializationError(runtimeEndpoint, throwable);
    }

    Throwable getThrowable() {
        return throwable;
    }

    byte[] getResult() {
        return result;
    }
}
