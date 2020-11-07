package io.lambda.aws;

import io.lambda.aws.convert.Converter;
import io.lambda.aws.http.AwsHttpClient;
import io.lambda.aws.http.impl.NativeAwsHttpClient;
import io.lambda.aws.logger.LambdaLogger;
import io.lambda.aws.model.AwsRequestEvent;
import io.lambda.aws.model.AwsResponseEvent;
import io.lambda.aws.http.AwsHttpResponse;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

import static io.lambda.aws.utils.TimeUtils.getTime;
import static io.lambda.aws.utils.TimeUtils.timeSpent;

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

    public static void main(String[] args) {
        try {
            new AwsLambdaRuntime().invoke(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void invoke(String[] args) throws Exception {
        final URI apiEndpoint = getRuntimeApiEndpoint();
        final long contextStart = getTime();
        final ApplicationContextBuilder builder = ApplicationContext.builder().args(args);
        try (final ApplicationContext context = builder.build().start()) {
            final AwsEventHandler requestHandler = context.getBean(AwsEventHandler.class);
            final Converter converter = context.getBean(Converter.class);
            final LambdaLogger logger = context.getBean(LambdaLogger.class);
            final AwsHttpClient httpClient = context.getBean(AwsHttpClient.class);
            logger.debug("Context startup took: %s", timeSpent(contextStart));

            logger.debug("AWS runtime uri: %s", apiEndpoint);
            final URI invocationUri = apiEndpoint.resolve(NEXT_INVOCATION_URI);
            logger.debug("Starting request parsing for: %s", invocationUri);

            while (!Thread.currentThread().isInterrupted()) {
                final AwsHttpResponse httpRequest = httpClient.get(invocationUri);
                if (StringUtils.isEmpty(httpRequest.body()))
                    throw new IllegalArgumentException("Request body is not present!");

                final AwsRequestEvent requestEvent = converter.convertToType(httpRequest.body(), AwsRequestEvent.class)
                        .setRequestId(httpRequest.headerAnyOrThrow(LAMBDA_RUNTIME_AWS_REQUEST_ID));
                try {
                    final AwsResponseEvent responseEvent = requestHandler.handle(requestEvent);
                    final URI responseUri = getResponseUri(apiEndpoint, requestEvent.getRequestId());

                    final long respondingStart = getTime();
                    logger.debug("Starting responding to AWS: %s", responseUri);
                    final AwsHttpResponse sent = httpClient.post(responseUri, responseEvent.getBody());
                    logger.info("Responding to AWS took: %s", timeSpent(respondingStart));
                    logger.info("Response from AWS: %s", sent.body().strip());
                } catch (Exception e) {
                    logger.error("Reporting invocation error: %s", e.getMessage());
                    final URI uri = getErrorResponseUri(apiEndpoint, requestEvent.getRequestId());
                    httpClient.postAndForget(uri, getErrorResponse(e));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            final NativeAwsHttpClient httpClient = new NativeAwsHttpClient();
            httpClient.postAndForget(apiEndpoint.resolve(INIT_ERROR), getErrorResponse(e));
        }
    }

    private static URI getResponseUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve("/2018-06-01/runtime/invocation/" + requestId + "/response");
    }

    private static URI getErrorResponseUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve("/2018-06-01/runtime/invocation/" + requestId + "/error");
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
}
