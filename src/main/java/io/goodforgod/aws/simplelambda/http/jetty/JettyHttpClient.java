package io.goodforgod.aws.simplelambda.http.jetty;

import io.goodforgod.aws.simplelambda.error.LambdaException;
import io.goodforgod.aws.simplelambda.error.StatusException;
import io.goodforgod.aws.simplelambda.http.SimpleHttpClient;
import io.goodforgod.aws.simplelambda.http.SimpleHttpRequest;
import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.http.common.EmptyHttpResponse;
import io.goodforgod.aws.simplelambda.http.common.InputStreamHttpResponse;
import io.goodforgod.aws.simplelambda.reactive.ByteBufferSubscriber;
import io.goodforgod.aws.simplelambda.utils.SubscriberUtils;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.AsyncRequestContent;
import org.eclipse.jetty.client.util.BytesRequestContent;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2;
import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 11.10.2021
 */
@Named("jetty")
@Singleton
public class JettyHttpClient implements SimpleHttpClient {

    private static final long CONNECTION_TIMEOUT = Duration.ofMinutes(11).toMillis();

    private final HttpClient httpClient;

    public JettyHttpClient() {
        this(getDefaultHttpClient());
    }

    public JettyHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setup() {
        try {
            httpClient.start();
        } catch (Exception e) {
            throw new LambdaException(e);
        }
    }

    private static HttpClient getDefaultHttpClient() {
        final HTTP2Client http2Client = new HTTP2Client();
        final HttpClientTransportOverHTTP2 clientTransport = new HttpClientTransportOverHTTP2(http2Client);
        clientTransport.setUseALPN(true);
        final HttpClient httpClient = new HttpClient(clientTransport);
        // final HttpClient httpClient = new HttpClient( new
        // HttpClientTransportDynamic());
        httpClient.setConnectTimeout(CONNECTION_TIMEOUT);
        httpClient.setIdleTimeout(CONNECTION_TIMEOUT);
        return httpClient;
    }

    // TODO
    protected int maxResponseLimit() {
        return 8 * 1024 * 1024;
    }

    @Override
    public @NotNull SimpleHttpResponse execute(@NotNull CharSequence httpMethod,
                                               @NotNull URI uri,
                                               @NotNull SimpleHttpRequest request,
                                               @NotNull Duration timeout) {
        try {
            final Request requestJetty = httpClient.newRequest(uri)
                    .version(HttpVersion.HTTP_2)
                    .method(httpMethod.toString())
                    .timeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                    .headers(headers -> request.headers().forEach(headers::put));

            final Publisher<ByteBuffer> body = request.body();
            if (body != null) {
                final byte[] bytes = SubscriberUtils.getPublisherBytes(body);
                requestJetty.body(new BytesRequestContent(bytes));
            }

            final ContentResponse response = requestJetty.send();

            final Map<String, List<String>> headers = response.getHeaders().stream()
                    .collect(Collectors.toMap(HttpField::getName, v -> List.of(v.getValue())));
            return InputStreamHttpResponse.of(response.getStatus(), new ByteArrayInputStream(response.getContent()), headers);
        } catch (InterruptedException | ExecutionException e) {
            throw new StatusException(500, e.getCause());
        } catch (TimeoutException e) {
            throw new StatusException(408, e);
        }
    }

    @Override
    public @NotNull SimpleHttpResponse executeAndForget(@NotNull CharSequence httpMethod,
                                                        @NotNull URI uri,
                                                        @NotNull SimpleHttpRequest request,
                                                        @NotNull Duration timeout) {
        return executeAndForgetAsync(httpMethod, uri, request, timeout).join();
    }

    @Override
    public @NotNull CompletableFuture<SimpleHttpResponse> executeAsync(@NotNull CharSequence httpMethod,
                                                                       @NotNull URI uri,
                                                                       @NotNull SimpleHttpRequest request,
                                                                       @NotNull Duration timeout) {
        final Request requestJetty = buildRequest(httpMethod, uri, request, timeout);

        final CompletableFutureResponseListener listener = new CompletableFutureResponseListener();
        requestJetty.send(listener);
        return listener.getResult();
    }

    @Override
    public @NotNull CompletableFuture<SimpleHttpResponse> executeAndForgetAsync(@NotNull CharSequence httpMethod,
                                                                                @NotNull URI uri,
                                                                                @NotNull SimpleHttpRequest request,
                                                                                @NotNull Duration timeout) {
        final Request requestJetty = buildRequest(httpMethod, uri, request, timeout);

        final CompletableFuture<SimpleHttpResponse> future = new CompletableFuture<>();

        requestJetty.send(result -> {
            if (result.getFailure() != null) {
                future.completeExceptionally(result.getFailure());
            } else if (result.getRequestFailure() != null) {
                future.completeExceptionally(result.getRequestFailure());
            } else if (result.getResponseFailure() != null) {
                future.completeExceptionally(result.getResponseFailure());
            } else {
                final Map<String, List<String>> headers = result.getResponse().getHeaders().stream()
                        .collect(Collectors.toMap(HttpField::getName, v -> List.of(v.getValue())));

                final EmptyHttpResponse response = EmptyHttpResponse.of(result.getResponse().getStatus(), headers);
                future.complete(response);
            }
        });

        return future;
    }

    private Request buildRequest(@NotNull CharSequence httpMethod,
                                 @NotNull URI uri,
                                 @NotNull SimpleHttpRequest request,
                                 @NotNull Duration timeout) {
        final Request requestJetty = httpClient.newRequest(uri)
                .version(HttpVersion.HTTP_2)
                .method(httpMethod.toString())
                .timeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .headers(headers -> request.headers().forEach(headers::put));

        final Publisher<ByteBuffer> body = request.body();
        if (body != null) {
            final AsyncRequestContent content = new AsyncRequestContent();
            final ByteBufferSubscriber subscriber = new ByteBufferSubscriber(b -> {
                final ByteBuffer buffer = ByteBuffer.wrap(b);
                content.offer(buffer);
                return buffer;
            });

            body.subscribe(subscriber);
            requestJetty.body(content);
        }

        return requestJetty;
    }

    @Override
    public void close() throws Exception {
        httpClient.stop();
    }
}
