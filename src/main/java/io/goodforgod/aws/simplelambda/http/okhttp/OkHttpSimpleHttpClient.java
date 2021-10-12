package io.goodforgod.aws.simplelambda.http.okhttp;

import static io.goodforgod.aws.simplelambda.http.okhttp.OkHttpSimpleHttpClient.QUALIFIER;

import io.goodforgod.aws.simplelambda.error.StatusException;
import io.goodforgod.aws.simplelambda.http.SimpleHttpClient;
import io.goodforgod.aws.simplelambda.http.SimpleHttpRequest;
import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.http.common.EmptyHttpResponse;
import io.goodforgod.aws.simplelambda.reactive.ByteBufferSubscriber;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Flow.Publisher;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Anton Kurako (GoodforGod)
 * @see okhttp3.OkHttpClient
 * @since 10.10.2021
 */
@Named(QUALIFIER)
@Singleton
public class OkHttpSimpleHttpClient implements SimpleHttpClient {

    public static final String QUALIFIER = "okhttp";

    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofMinutes(11);

    private final OkHttpClient okHttpClient;

    public OkHttpSimpleHttpClient() {
        this(new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                .callTimeout(DEFAULT_CONNECTION_TIMEOUT)
                .writeTimeout(DEFAULT_TIMEOUT)
                .readTimeout(DEFAULT_TIMEOUT)
                .followRedirects(true)
                .protocols(List.of(Protocol.H2_PRIOR_KNOWLEDGE))
                .build());
    }

    public OkHttpSimpleHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public @NotNull SimpleHttpResponse execute(@NotNull CharSequence httpMethod,
                                               @NotNull URI uri,
                                               @NotNull SimpleHttpRequest request,
                                               @NotNull Duration timeout) {
        try {
            return executeAsync(httpMethod, uri, request, timeout).join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof RuntimeException)
                throw ((RuntimeException) e.getCause());
            throw e;
        }
    }

    @Override
    public @NotNull SimpleHttpResponse executeAndForget(@NotNull CharSequence httpMethod,
                                                        @NotNull URI uri,
                                                        @NotNull SimpleHttpRequest request,
                                                        @NotNull Duration timeout) {
        try {
            return executeAndForgetAsync(httpMethod, uri, request, timeout).join();
        } catch (Exception e) {
            if (e.getCause() instanceof RuntimeException)
                throw ((RuntimeException) e.getCause());
            throw e;
        }
    }

    @Override
    public @NotNull CompletableFuture<SimpleHttpResponse> executeAsync(@NotNull CharSequence httpMethod,
                                                                       @NotNull URI uri,
                                                                       @NotNull SimpleHttpRequest request,
                                                                       @NotNull Duration timeout) {
        final Publisher<ByteBuffer> body = request.body();
        if (body != null) {
            final ByteBufferSubscriber subscriber = new ByteBufferSubscriber();
            body.subscribe(subscriber);
            return subscriber.result()
                    .thenApply(byteBuffer -> exec(httpMethod, uri, byteBuffer, timeout, false));
        } else {
            return CompletableFuture.supplyAsync(() -> exec(httpMethod, uri, null, timeout, false));
        }
    }

    @Override
    public @NotNull CompletableFuture<SimpleHttpResponse> executeAndForgetAsync(@NotNull CharSequence httpMethod,
                                                                                @NotNull URI uri,
                                                                                @NotNull SimpleHttpRequest request,
                                                                                @NotNull Duration timeout) {
        final Publisher<ByteBuffer> body = request.body();
        if (body != null) {
            final ByteBufferSubscriber subscriber = new ByteBufferSubscriber();
            body.subscribe(subscriber);
            return subscriber.result()
                    .thenApply(byteBuffer -> exec(httpMethod, uri, byteBuffer, timeout, true));
        } else {
            return CompletableFuture.supplyAsync(() -> exec(httpMethod, uri, null, timeout, true));
        }
    }

    private SimpleHttpResponse exec(@NotNull CharSequence httpMethod,
                                    @NotNull URI uri,
                                    @Nullable ByteBuffer body,
                                    @NotNull Duration timeout,
                                    boolean forgetResponse) {
        final RequestBody requestBody = (body == null || body.array().length == 0)
                ? null
                : RequestBody.create(null, body.array());

        final Request okRequest = new Request.Builder()
                .url(uri.toString())
                .method(httpMethod.toString(), requestBody)
                .build();

        try (Response response = okHttpClient.newCall(okRequest).execute()) {
            return (forgetResponse)
                    ? EmptyHttpResponse.of()
                    : OkHttpResponse.of(response);
        } catch (IOException e) {
            throw new StatusException(500, e);
        }
    }

    @Override
    public void close() {
        // do nothing
    }
}
