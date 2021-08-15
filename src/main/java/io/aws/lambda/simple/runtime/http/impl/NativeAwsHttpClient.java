package io.aws.lambda.simple.runtime.http.impl;

import io.aws.lambda.simple.runtime.error.StatusException;
import io.aws.lambda.simple.runtime.http.AwsHttpClient;
import io.aws.lambda.simple.runtime.http.AwsHttpRequest;
import io.aws.lambda.simple.runtime.http.AwsHttpResponse;
import io.aws.lambda.simple.runtime.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Singleton;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Stream;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 27.10.2020
 */
@Singleton
public class NativeAwsHttpClient implements AwsHttpClient {

    private static final HttpClient.Version DEFAULT_VERSION = HttpClient.Version.HTTP_2;
    private static final Duration DEFAULT_DURATION = Duration.ofMinutes(10);

    private final HttpClient client;

    public NativeAwsHttpClient() {
        this.client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(DEFAULT_DURATION)
                .version(DEFAULT_VERSION)
                .build();
    }

    @Override
    public @NotNull AwsHttpResponse get(@NotNull URI uri) {
        final HttpRequest request = getGetRequest(uri);
        return sendAndResponse(request);
    }

    @Override
    public @NotNull AwsHttpResponse post(@NotNull URI uri,
                                         @NotNull AwsHttpRequest request) {
        final HttpRequest httpRequest = getPostRequest(uri, request);
        return sendAndResponse(httpRequest);
    }

    @Override
    public int postAndForget(@NotNull URI uri,
                             @NotNull AwsHttpRequest request) {
        final HttpRequest httpRequest = getPostRequest(uri, request);
        return sendAndForget(httpRequest).statusCode();
    }

    private HttpRequest getGetRequest(@NotNull URI uri) {
        return HttpRequest.newBuilder(uri)
                .timeout(DEFAULT_DURATION)
                .version(DEFAULT_VERSION)
                .build();
    }

    private HttpRequest getPostRequest(@NotNull URI uri,
                                       @NotNull AwsHttpRequest request) {
        final HttpRequest.BodyPublisher publisher = StringUtils.isEmpty(request.body())
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(request.body(), StandardCharsets.UTF_8);

        final String[] headers = getRequestHeaders(request);
        final HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .POST(publisher)
                .timeout(DEFAULT_DURATION)
                .version(DEFAULT_VERSION);

        return (headers != null)
                ? builder.headers(headers).build()
                : builder.build();
    }

    protected @Nullable String[] getRequestHeaders(@NotNull AwsHttpRequest request) {
        if (request.headers().isEmpty())
            return null;

        return request.headers().entrySet().stream()
                .flatMap(e -> (e.getValue().size() == 1)
                        ? Stream.of(e.getKey(), e.getValue().get(0))
                        : e.getValue().stream().flatMap(headerValue -> Stream.of(e.getKey(), headerValue)))
                .toArray(String[]::new);
    }

    private AwsHttpResponse sendAndResponse(HttpRequest request) {
        try {
            final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return new NativeAwsHttpResponse(response);
        } catch (Exception e) {
            throw new StatusException(e.getMessage(), e, 500);
        }
    }

    private HttpResponse<Void> sendAndForget(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            throw new StatusException(e.getMessage(), e, 500);
        }
    }
}
