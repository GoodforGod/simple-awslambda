package io.aws.lambda.simple.runtime.http.impl;

import io.aws.lambda.simple.runtime.error.StatusException;
import io.aws.lambda.simple.runtime.http.SimpleHttpClient;
import io.aws.lambda.simple.runtime.http.SimpleHttpRequest;
import io.aws.lambda.simple.runtime.http.SimpleHttpResponse;
import io.aws.lambda.simple.runtime.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Native Java implementation of {@link SimpleHttpClient} for {@link HttpClient}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 27.10.2020
 */
@Singleton
public class NativeSimpleHttpClient implements SimpleHttpClient {

    private static final HttpClient.Version DEFAULT_VERSION = HttpClient.Version.HTTP_2;
    private static final Duration DEFAULT_DURATION = Duration.ofMinutes(10);

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_PATCH = "PATCH";
    public static final String METHOD_DELETE = "DELETE";

    private final HttpClient client;

    public NativeSimpleHttpClient() {
        this.client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(DEFAULT_DURATION)
                .version(DEFAULT_VERSION)
                .build();
    }

    @Override
    public @NotNull SimpleHttpResponse method(@NotNull String method,
                                              @NotNull URI uri,
                                              @NotNull SimpleHttpRequest request) {
        final HttpRequest httpRequest = createHttpRequest(uri, method, request);
        return sendAndGetResponse(httpRequest);
    }

    @Override
    public @NotNull SimpleHttpResponse methodAndForget(@NotNull String method,
                                                       @NotNull URI uri,
                                                       @NotNull SimpleHttpRequest request) {
        final HttpRequest httpRequest = createHttpRequest(uri, method, request);
        return sendAndDiscardResponse(httpRequest);
    }

    private HttpRequest createHttpRequest(@NotNull URI uri,
                                          @NotNull String httpMethod,
                                          @NotNull SimpleHttpRequest request) {
        final String bodyAsString = request.body();
        final HttpRequest.BodyPublisher publisher = StringUtils.isEmpty(bodyAsString)
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(bodyAsString, StandardCharsets.UTF_8);

        final HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .method(httpMethod, publisher)
                .timeout(DEFAULT_DURATION)
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
            throw new StatusException(e, 500);
        }
    }

    private SimpleHttpResponse sendAndDiscardResponse(HttpRequest request) {
        try {
            final HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return new NativeVoidSimpleHttpResponse(response);
        } catch (Exception e) {
            throw new StatusException(e, 500);
        }
    }
}
