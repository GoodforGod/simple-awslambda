package io.aws.lambda.simple.runtime.http.impl;

import io.aws.lambda.simple.runtime.error.StatusException;
import io.aws.lambda.simple.runtime.http.AwsHttpClient;
import io.aws.lambda.simple.runtime.http.AwsHttpResponse;
import org.jetbrains.annotations.Nullable;

import javax.inject.Singleton;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 27.10.2020
 */
@Singleton
public class NativeAwsHttpClient implements AwsHttpClient {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String MEDIA_TYPE_JSON = "application/json";

    private final HttpClient client;

    public NativeAwsHttpClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public AwsHttpResponse get(URI uri) {
        final HttpRequest request = HttpRequest.newBuilder(uri).build();
        return sendAndResponse(request);
    }

    @Override
    public AwsHttpResponse get(URI uri, Duration timeout) {
        final HttpRequest request = HttpRequest.newBuilder(uri).timeout(timeout).build();
        return sendAndResponse(request);
    }

    @Override
    public AwsHttpResponse post(URI uri, String body) {
        final HttpRequest request = getPostRequest(uri, body);
        return sendAndResponse(request);
    }

    @Override
    public void postAndForget(URI uri, String body) {
        final HttpRequest request = getPostRequest(uri, body);
        sendAndForget(request);
    }

    private HttpRequest getPostRequest(URI uri, @Nullable String body) {
        final HttpRequest.BodyPublisher publisher = (body == null)
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body);

        return HttpRequest.newBuilder(uri)
                .POST(publisher)
                .header(CONTENT_TYPE, MEDIA_TYPE_JSON)
                .timeout(Duration.ofSeconds(10))
                .build();
    }

    private AwsHttpResponse sendAndResponse(HttpRequest request) {
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return new NativeHttpResponse(response);
        } catch (Exception e) {
            throw new StatusException(e.getMessage(), 500);
        }
    }

    private void sendAndForget(HttpRequest request) {
        try {
            client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            throw new StatusException(e.getMessage(), 500);
        }
    }
}
