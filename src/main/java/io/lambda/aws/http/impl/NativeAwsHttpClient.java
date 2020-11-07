package io.lambda.aws.http.impl;

import io.lambda.aws.error.HttpException;
import io.lambda.aws.http.AwsHttpClient;
import io.lambda.aws.http.AwsHttpResponse;
import io.micronaut.core.annotation.Introspected;

import javax.inject.Singleton;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
@Introspected
@Singleton
public class NativeAwsHttpClient implements AwsHttpClient {

    private final HttpClient client;

    public NativeAwsHttpClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(1))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public AwsHttpResponse get(URI uri) {
        final HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(5))
                .build();

        return sendAndResponse(request);
    }

    @Override
    public AwsHttpResponse post(URI uri, String body) {
        final HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5))
                .build();

        return sendAndResponse(request);
    }

    @Override
    public void postAndForget(URI uri, String body) {
        final HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5))
                .build();

        sendAndForget(request);
    }

    private AwsHttpResponse sendAndResponse(HttpRequest request) {
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() == 200) {
                return new NativeHttpResponse(response);
            } else {
                throw new HttpException(response.body()).code(response.statusCode());
            }
        } catch (Exception e) {
            throw new HttpException(e.getMessage()).code(500);
        }
    }

    private void sendAndForget(HttpRequest request) {
        try {
            final HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200)
                throw new HttpException("Error while executing").code(response.statusCode());
        } catch (Exception e) {
            throw new HttpException(e.getMessage()).code(500);
        }
    }
}
