package io.lambda;

import io.lambda.model.AwsResponseEvent;
import io.lambda.convert.Converter;
import io.lambda.logger.LambdaLogger;
import io.lambda.model.AwsRequestEvent;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.reflect.GenericTypeUtils;
import io.micronaut.core.util.StringUtils;
import org.graalvm.collections.Pair;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Introspected
public class AwsLambdaRuntime {

    /**
     * The request ID, which identifies the request that triggered the function
     * invocation. For example, 8476a536-e9f4-11e8-9739-2dfe598c3fcd.
     */
    private static final String LAMBDA_RUNTIME_AWS_REQUEST_ID = "Lambda-Runtime-Aws-Request-Id";
    private static final String AWS_LAMBDA_RUNTIME_API = "AWS_LAMBDA_RUNTIME_API";
    private static final String NEXT_INVOCATION_URI = "/2018-06-01/runtime/invocation/next";
    private static final String INIT_ERROR = "/2018-06-01/runtime/init/error";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMinutes(1))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public static void main(String[] args) throws Exception {
        new AwsLambdaRuntime().invoke(args);
    }

    public void invoke(String[] args) throws Exception {
        final URI apiEndpoint = getRuntimeApiEndpoint();
        final long contextStart = getTime();
        final ApplicationContextBuilder builder = ApplicationContext.builder().args(args);
        try (final ApplicationContext context = builder.build().start()) {
            final Lambda function = context.getBean(Lambda.class);
            final Converter converter = context.getBean(Converter.class);
            final LambdaLogger logger = context.getBean(LambdaLogger.class);
            logger.debug("Context startup took: ", contextStart);

            final Pair<Class, Class> functionArgs = getClassGenericType(function);
            logger.debug("Function %s with request type '%s' and response type '%s' found",
                    function.getClass(), functionArgs.getLeft(), functionArgs.getRight());

            logger.debug("AWS runtime uri: " + apiEndpoint);
            final URI invocationUri = apiEndpoint.resolve(NEXT_INVOCATION_URI);
            logger.debug("Starting request parsing for: " + invocationUri);
            final HttpRequest awsApiReq = HttpRequest.newBuilder(invocationUri).GET().build();

            while (!Thread.currentThread().isInterrupted()) {
                final HttpResponse<String> httpRequest = httpClient.send(awsApiReq, HttpResponse.BodyHandlers.ofString());
                if (httpRequest.body() == null || httpRequest.body().isEmpty())
                    throw new IllegalArgumentException("Request body is not present!");

                final AwsRequestEvent requestEvent = converter.convertToType(httpRequest.body(), AwsRequestEvent.class);
                final String requestId = httpRequest.headers().firstValue(LAMBDA_RUNTIME_AWS_REQUEST_ID).orElseThrow();
                try {
                    final long responseStart = getTime();
                    logger.debug("Function request body: %s", requestEvent.getBody());
                    logger.debug("Starting function processing...");
                    final Object functionInput = converter.convertToType(requestEvent.getBody(), functionArgs.getLeft());
                    final Object functionOutput = function.handle(functionInput);
                    logger.info("Function processing took: %s", timeSpent(responseStart));

                    final long respondingStart = getTime();
                    final String responseBody = converter.convertToJson(functionOutput);
                    logger.debug("Function response body: %s", responseBody);
                    final AwsResponseEvent responseEvent = new AwsResponseEvent()
                            .setBody(responseBody)
                            .setHeaders(Map.of("Content-Type", "application/json"));

                    final String body = converter.convertToJson(responseEvent);
                    final URI responseUri = getResponseUri(apiEndpoint, requestId);

                    logger.debug("Starting responding to AWS: " + responseUri);
                    final HttpRequest eventRequest = build(responseUri, body);
                    final HttpResponse<String> sent = httpClient.send(eventRequest, HttpResponse.BodyHandlers.ofString());

                    logger.info("Responding to AWS took: ", respondingStart);
                    logger.info("Response from AWS: " + sent.body().strip());
                } catch (Exception e) {
                    logger.error("Reporting invocation error: " + e.getMessage());
                    respondWithErrorToAws(getErrorResponseUri(apiEndpoint, requestId), e);
                }
            }
        } catch (Exception e) {
            respondWithErrorToAws(apiEndpoint.resolve(INIT_ERROR), e);
        }
    }

    private <T extends Lambda> Pair<Class, Class> getClassGenericType(T t) {
        final Class[] args = GenericTypeUtils.resolveSuperTypeGenericArguments(t.getClass(), Lambda.class);
        return Pair.create(args[0], args[1]);
    }

    private static URI getResponseUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve("/2018-06-01/runtime/invocation/" + requestId + "/response");
    }

    private static URI getErrorResponseUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve("/2018-06-01/runtime/invocation/" + requestId + "/error");
    }

    private void respondWithErrorToAws(URI uri, Throwable e) throws Exception {
        final String body = getErrorResponse(e);
        final HttpRequest eventRequest = build(uri, body);
        httpClient.send(eventRequest, HttpResponse.BodyHandlers.discarding());
    }

    private static HttpRequest build(URI uri, String body) {
        return HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5))
                .build();
    }

    private static URI getRuntimeApiEndpoint() throws URISyntaxException {
        final String runtimeApiEndpoint = System.getenv(AWS_LAMBDA_RUNTIME_API);
        if (StringUtils.isEmpty(runtimeApiEndpoint))
            throw new IllegalStateException("Missing " + AWS_LAMBDA_RUNTIME_API
                    + " environment variable. Custom runtime can only be run within AWS Lambda environment.");

        return new URI("http://" + runtimeApiEndpoint);
    }

    private static String getErrorResponse(Throwable e) {
        return String.format("{\"errorMessage\":\"%s\", \"errorType\":\"%s\"}",
                e.getMessage(), e.getClass().getSimpleName());
    }

    private static long getTime() {
        return System.nanoTime();
    }

    private static String timeSpent(long started) {
        return ((System.nanoTime() - started) / 1000) + " millis";
    }
}
