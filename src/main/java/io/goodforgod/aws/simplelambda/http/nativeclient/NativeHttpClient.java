package io.goodforgod.aws.simplelambda.http.nativeclient;

import static io.goodforgod.aws.simplelambda.http.nativeclient.NativeHttpClient.QUALIFIER;

import io.goodforgod.aws.simplelambda.http.SimpleHttpClient;
import io.goodforgod.aws.simplelambda.http.SimpleHttpRequest;
import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.http.common.HttpStatus;
import io.goodforgod.http.common.exception.HttpStatusException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Publisher;
import javax.inject.Named;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

/**
 * Native Java implementation of {@link SimpleHttpClient} for {@link HttpClient}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 27.10.2020
 */
@Named(QUALIFIER)
@Singleton
public class NativeHttpClient implements SimpleHttpClient {

    public static final String QUALIFIER = "native";

    private static final HttpClient.Version DEFAULT_VERSION = HttpClient.Version.HTTP_2;
    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofMinutes(11);

    private final HttpClient client;

    public NativeHttpClient() {
        this(getDefaultHttpClient());
    }

    public NativeHttpClient(@NotNull HttpClient client) {
        this.client = client;
    }

    private static HttpClient getDefaultHttpClient() {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                .version(DEFAULT_VERSION)
                .build();
    }

    public @NotNull HttpClient getClient() {
        return client;
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
        return sendAndGetResponseAsync(httpRequest);
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
        final HttpRequest.BodyPublisher publisher;
        if (bufferPublisher == null) {
            publisher = HttpRequest.BodyPublishers.noBody();
        } else if (bufferPublisher instanceof HttpRequest.BodyPublisher) {
            publisher = (HttpRequest.BodyPublisher) bufferPublisher;
        } else {
            publisher = HttpRequest.BodyPublishers.fromPublisher(bufferPublisher);
        }

        final HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .method(httpMethod.toString(), publisher)
                .timeout(timeout)
                .version(DEFAULT_VERSION);

        request.headers().getMultiMap().forEach((header, values) -> {
            for (String value : values) {
                builder.header(header, value);
            }
        });

        return builder.build();
    }

    private SimpleHttpResponse sendAndGetResponse(HttpRequest request) {
        try {
            final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return InputStreamNativeHttpResponse.of(response);
        } catch (Exception e) {
            throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    private SimpleHttpResponse sendAndDiscardResponse(HttpRequest request) {
        try {
            final HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return VoidNativeHttpResponse.of(response);
        } catch (Exception e) {
            throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    private CompletableFuture<SimpleHttpResponse> sendAndGetResponseAsync(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(InputStreamNativeHttpResponse::of);
    }

    private CompletableFuture<SimpleHttpResponse> sendAndDiscardResponseAsync(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(VoidNativeHttpResponse::of);
    }

    @Override
    public void close() {
        // do nothing
    }
}
