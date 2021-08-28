package io.aws.lambda.simple.runtime.http.nativeclient;

import io.aws.lambda.simple.runtime.error.StatusException;
import io.aws.lambda.simple.runtime.http.SimpleHttpClient;
import io.aws.lambda.simple.runtime.http.SimpleHttpRequest;
import io.aws.lambda.simple.runtime.http.SimpleHttpResponse;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Publisher;

/**
 * Native Java implementation of {@link SimpleHttpClient} for {@link HttpClient}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 27.10.2020
 */
@Singleton
public class NativeSimpleHttpClient implements SimpleHttpClient {

    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(5);

    private static final HttpClient.Version DEFAULT_VERSION = HttpClient.Version.HTTP_2;
    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofMinutes(11);

    private final HttpClient client;

    public NativeSimpleHttpClient() {
        this.client = getClient();
    }

    protected HttpClient getClient() {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                .version(DEFAULT_VERSION)
                .build();
    }

    @Override
    public @NotNull SimpleHttpResponse execute(@NotNull CharSequence httpMethod,
                                               @NotNull URI uri,
                                               @NotNull SimpleHttpRequest request,
                                               @NotNull Duration timeout) {
        final HttpRequest httpRequest = createHttpRequest(uri, httpMethod, timeout, request);
        return sendAndGetResponse(httpRequest);
    }

    @Override
    public @NotNull SimpleHttpResponse executeAndForget(@NotNull CharSequence httpMethod,
                                                        @NotNull URI uri,
                                                        @NotNull SimpleHttpRequest request,
                                                        @NotNull Duration timeout) {
        final HttpRequest httpRequest = createHttpRequest(uri, httpMethod, timeout, request);
        return sendAndDiscardResponse(httpRequest);
    }

    @Override
    public @NotNull CompletableFuture<SimpleHttpResponse> executeAsync(@NotNull CharSequence httpMethod,
                                                                       @NotNull URI uri,
                                                                       @NotNull SimpleHttpRequest request,
                                                                       @NotNull Duration timeout) {
        final HttpRequest httpRequest = createHttpRequest(uri, httpMethod, timeout, request);
        return sendAndDiscardResponseAsync(httpRequest);
    }

    @Override
    public @NotNull CompletableFuture<SimpleHttpResponse> executeAndForgetAsync(@NotNull CharSequence httpMethod,
                                                                                @NotNull URI uri,
                                                                                @NotNull SimpleHttpRequest request,
                                                                                @NotNull Duration timeout) {
        final HttpRequest httpRequest = createHttpRequest(uri, httpMethod, timeout, request);
        return sendAndDiscardResponseAsync(httpRequest);
    }

    private HttpRequest createHttpRequest(@NotNull URI uri,
                                          @NotNull CharSequence httpMethod,
                                          @NotNull Duration timeout,
                                          @NotNull SimpleHttpRequest request) {
        final Publisher<ByteBuffer> bufferPublisher = request.body();
        final HttpRequest.BodyPublisher publisher = (bufferPublisher instanceof HttpRequest.BodyPublisher)
                ? (HttpRequest.BodyPublisher) bufferPublisher
                : HttpRequest.BodyPublishers.fromPublisher(bufferPublisher);

        final HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .method(httpMethod.toString(), publisher)
                .timeout(timeout)
                .version(DEFAULT_VERSION);

        if (!request.headers().isEmpty())
            request.headers().forEach(builder::header);

        return builder.build();
    }

    private SimpleHttpResponse sendAndGetResponse(HttpRequest request) {
        try {
            final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return new NativeInputStreamSimpleHttpResponse(response);
        } catch (Exception e) {
            throw new StatusException(500, e);
        }
    }

    private SimpleHttpResponse sendAndDiscardResponse(HttpRequest request) {
        try {
            final HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return new NativeVoidSimpleHttpResponse(response);
        } catch (Exception e) {
            throw new StatusException(500, e);
        }
    }

    private CompletableFuture<SimpleHttpResponse> sendAndGetResponseAsync(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(NativeInputStreamSimpleHttpResponse::new);
    }

    private CompletableFuture<SimpleHttpResponse> sendAndDiscardResponseAsync(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(NativeVoidSimpleHttpResponse::new);
    }
}
