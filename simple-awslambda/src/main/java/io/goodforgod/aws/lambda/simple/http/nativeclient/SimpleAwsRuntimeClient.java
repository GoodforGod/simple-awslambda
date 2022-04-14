package io.goodforgod.aws.lambda.simple.http.nativeclient;

import static io.goodforgod.aws.lambda.simple.http.nativeclient.SimpleAwsRuntimeClient.*;

import com.amazonaws.services.lambda.runtime.Context;
import io.goodforgod.aws.lambda.simple.AwsRuntimeClient;
import io.goodforgod.aws.lambda.simple.config.AwsRuntimeVariables;
import io.goodforgod.aws.lambda.simple.handler.Event;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpBody;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpClient;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpRequest;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpResponse;
import io.goodforgod.aws.lambda.simple.utils.StringUtils;
import io.goodforgod.aws.lambda.simple.utils.TimeUtils;
import io.goodforgod.http.common.HttpHeaders;
import io.goodforgod.http.common.HttpMethod;
import io.goodforgod.http.common.HttpStatus;
import io.goodforgod.http.common.MediaType;
import io.goodforgod.http.common.exception.HttpStatusException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import javax.inject.Named;
import javax.inject.Singleton;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 11.04.2022
 */
@Internal
@Named(QUALIFIER)
@Singleton
public class SimpleAwsRuntimeClient implements AwsRuntimeClient {

    public static final String QUALIFIER = "native";

    record SimpleEvent(InputStream input, Context context) implements Event {}

    private static final Logger logger = LoggerFactory.getLogger(SimpleAwsRuntimeClient.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(11);
    private static final HttpHeaders JSON_HEADERS = HttpHeaders.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

    private final SimpleHttpClient httpClient;

    public SimpleAwsRuntimeClient(SimpleHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public @NotNull URI getAwsRuntimeApi() {
        final String runtimeApiEndpoint = System.getenv(AwsRuntimeVariables.AWS_LAMBDA_RUNTIME_API);
        if (StringUtils.isEmpty(runtimeApiEndpoint))
            throw new IllegalStateException("Missing '" + AwsRuntimeVariables.AWS_LAMBDA_RUNTIME_API
                    + "' environment variable. Custom runtime can only be run within AWS Lambda environment.");

        return URI.create("http://" + runtimeApiEndpoint);
    }

    @Override
    public @NotNull Event getNextEvent(@NotNull URI runtimeEndpoint) {
        final URI invocationUri = getInvocationNextUri(runtimeEndpoint);
        logger.debug("AWS Event Invocation URI: {}", invocationUri);

        final SimpleHttpRequest request = SimpleHttpRequest.builder(invocationUri)
                .method(HttpMethod.GET)
                .timeout(DEFAULT_TIMEOUT)
                .build();

        final SimpleHttpResponse response = httpClient.execute(request);
        logger.trace("Event received with httpCode '{}' with headers: {}", response.status(), response.headers());

        if (!HttpStatus.OK.equals(response.status())) {
            throw new HttpStatusException(response.status(), response.bodyAsString());
        }

        final EventContext eventContext = new EventContext(response.headers());
        if (StringUtils.isEmpty(eventContext.getAwsRequestId())) {
            throw new IllegalStateException("AWS Request ID is not present!");
        }

        return new SimpleEvent(response.body(), eventContext);
    }

    @Override
    public void reportInvocationSuccess(@NotNull URI runtimeEndpoint,
                                        @NotNull SimpleHttpBody lambdaResult,
                                        @NotNull Context context) {
        final URI responseUri = getInvocationResponseUri(runtimeEndpoint, context.getAwsRequestId());
        logger.debug("Responding to AWS Invocation URI: {}", responseUri);
        final long respondingStart = (logger.isDebugEnabled())
                ? TimeUtils.getTime()
                : 0;

        final SimpleHttpRequest request = SimpleHttpRequest.builder(responseUri)
                .method(HttpMethod.POST)
                .timeout(DEFAULT_TIMEOUT)
                .body(lambdaResult)
                .build();

        final SimpleHttpResponse awsResponse = httpClient.execute(request);
        if (logger.isDebugEnabled()) {
            logger.debug("Responding to AWS Invocation took: {} millis", TimeUtils.timeTook(respondingStart));
        }

        if (logger.isTraceEnabled()) {
            final String responseBody = awsResponse.bodyAsString();
            logger.trace("AWS Invocation responded with httpCode '{}' and body: {}",
                    awsResponse.status(), responseBody);
        }
    }

    @Override
    public void reportInitializationError(@NotNull URI runtimeEndpoint,
                                          @NotNull Throwable throwable) {
        logger.error("Function initialization error occurred", throwable);
        final URI errorUri = runtimeEndpoint.resolve(AwsRuntimeVariables.INIT_ERROR);
        logger.debug("Responding to AWS Runtime Init Error URI: {}", errorUri);
        respondWithError(errorUri, throwable);
    }

    @Override
    public void reportInvocationError(@NotNull URI runtimeEndpoint,
                                      @NotNull Throwable throwable,
                                      @NotNull Context context) {
        logger.error("Function Invocation error occurred", throwable);
        final URI errorUri = getInvocationErrorUri(runtimeEndpoint, context.getAwsRequestId());
        logger.debug("Responding to AWS Invocation Error URI: {}", errorUri);
        respondWithError(errorUri, throwable);
    }

    private void respondWithError(@NotNull URI uri, @NotNull Throwable throwable) {
        final SimpleHttpRequest request = SimpleHttpRequest.builder(uri)
                .method(HttpMethod.POST)
                .body(getErrorResponseBody(throwable))
                .headers(JSON_HEADERS)
                .timeout(DEFAULT_TIMEOUT)
                .build();

        httpClient.executeAndForget(request);
    }

    /**
     * Retrieves an invocation event.
     *
     * @param apiEndpoint of api URI
     * @return invocation response uri
     * @see <a href=
     *          "https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
     */
    private static URI getInvocationNextUri(URI apiEndpoint) {
        return apiEndpoint.resolve(AwsRuntimeVariables.INVOCATION_NEXT_URI);
    }

    /**
     * Sends an invocation response to Lambda.
     *
     * @param apiEndpoint of api URI
     * @param requestId   of request
     * @return invocation response uri
     * @see <a href=
     *          "https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
     */
    private static URI getInvocationResponseUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve(AwsRuntimeVariables.INVOCATION_URI + requestId + "/response");
    }

    /**
     * If the function returns an error, the runtime formats the error into a JSON document, and posts
     * it to the invocation error path.
     *
     * @param apiEndpoint of api URI
     * @param requestId   of request
     * @return invocation response uri
     * @see <a href=
     *          "https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
     */
    private static URI getInvocationErrorUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve(AwsRuntimeVariables.INVOCATION_URI + requestId + "/error");
    }

    private static SimpleHttpBody getErrorResponseBody(Throwable e) {
        final String body = "{\"errorMessage\":\"" + e.getMessage() + "\", \"errorType\":\"" + e.getClass().getName() + "\"}";
        return SimpleHttpBody.ofString(body);
    }
}
