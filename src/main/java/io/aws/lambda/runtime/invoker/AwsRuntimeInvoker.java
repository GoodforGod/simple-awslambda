package io.aws.lambda.runtime.invoker;

import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.http.AwsHttpClient;
import io.aws.lambda.runtime.http.AwsHttpResponse;
import io.aws.lambda.runtime.http.impl.NativeAwsHttpClient;
import io.aws.lambda.runtime.logger.LambdaLogger;
import io.aws.lambda.runtime.model.AwsRequestContext;
import io.aws.lambda.runtime.utils.TimeUtils;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Introspected
public class AwsRuntimeInvoker {

    /**
     * The request ID, which identifies the request that triggered the function
     * invocation. For example, 8476a536-e9f4-11e8-9739-2dfe598c3fcd.
     */
    private static final String LAMBDA_RUNTIME_AWS_REQUEST_ID = "Lambda-Runtime-Aws-Request-Id";

    /**
     * The AWS X-Ray tracing header. For example,
     * Root=1-5bef4de7-ad49b0e87f6ef6c87fc2e700;Parent=9a9197af755a6419;Sampled=1.
     */
    private static final String LAMBDA_RUNTIME_TRACE_ID = "Lambda-Runtime-Trace-Id";

    /**
     * AWS Lambda provides an HTTP API for custom runtimes to receive invocation events
     * from Lambda and send response data back within the Lambda execution environment.
     */
    private static final String AWS_LAMBDA_RUNTIME_API = "AWS_LAMBDA_RUNTIME_API";

    private static final String INIT_ERROR = "/2018-06-01/runtime/init/error";
    private static final String INVOCATION_URI = "/2018-06-01/runtime/invocation/";
    private static final String INVOCATION_NEXT_URI = INVOCATION_URI + "next";

    public void invoke(@NotNull Class<? extends EventHandler> handlerType) {
        final URI apiEndpoint = getRuntimeApiEndpoint();
        final long contextStart = TimeUtils.getTime();
        try (final ApplicationContext context = ApplicationContext.build().start()) {
            final EventHandler eventHandler = context.getBean(handlerType);
            final LambdaLogger logger = context.getBean(LambdaLogger.class);
            final AwsHttpClient httpClient = context.getBean(AwsHttpClient.class);
            logger.debug("Context startup took: %s", TimeUtils.timeSpent(contextStart));
            logger.debug("AWS runtime uri: %s", apiEndpoint);

            final URI invocationUri = getInvocationNextUri(apiEndpoint);
            logger.debug("AWS Runtime Event provider at: %s", invocationUri);

            while (!Thread.currentThread().isInterrupted()) {
                final AwsHttpResponse httpRequest = httpClient.get(invocationUri);
                if (StringUtils.isEmpty(httpRequest.body()))
                    throw new IllegalArgumentException("Request body is not present!");

                final String requestId = httpRequest.headerAnyOrThrow(LAMBDA_RUNTIME_AWS_REQUEST_ID);
                final String traceId = httpRequest.headerAny(LAMBDA_RUNTIME_TRACE_ID);
                final AwsRequestContext requestContext = new AwsRequestContext(requestId, traceId);

                logger.refresh();
                logger.debug("AWS Request Event received with %s", requestContext);
                if (logger.isDebugEnabled()) {
                    httpRequest.headers().forEach((k, v) -> logger.debug("Request header: %s - %s", k, v));
                }

                try {
                    final String responseEvent = eventHandler.handle(httpRequest.body(), requestContext);
                    final URI responseUri = getInvocationResponseUri(apiEndpoint, requestContext.getRequestId());

                    logger.debug("Responding to AWS started: %s", responseUri);
                    final long respondingStart = TimeUtils.getTime();
                    final AwsHttpResponse awsResponse = httpClient.post(responseUri, responseEvent);
                    logger.info("Responding to AWS took: %s", TimeUtils.timeSpent(respondingStart));
                    logger.debug("AWS responded with http code '%s' and body: %s",
                            awsResponse.code(), awsResponse.body());
                } catch (Exception e) {
                    logger.error("Invocation error occurred", e);
                    final URI uri = getInvocationErrorUri(apiEndpoint, requestContext.getRequestId());
                    httpClient.postAndForget(uri, getErrorResponse(e));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            final NativeAwsHttpClient httpClient = new NativeAwsHttpClient();
            httpClient.postAndForget(apiEndpoint.resolve(INIT_ERROR), getErrorResponse(e));
        }
    }

    /**
     * Retrieves an invocation event.
     * @see <a href="https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
     * @param apiEndpoint of api URI
     * @return invocation response uri
     */
    private static URI getInvocationNextUri(URI apiEndpoint) {
        return apiEndpoint.resolve(INVOCATION_NEXT_URI);
    }

    /**
     * Sends an invocation response to Lambda.
     * @see <a href="https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
     * @param apiEndpoint of api URI
     * @param requestId of request
     * @return invocation response uri
     */
    private static URI getInvocationResponseUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve(INVOCATION_URI + requestId + "/response");
    }

    /**
     * If the function returns an error, the runtime formats the error into a JSON document, and posts it to the invocation error path.
     * @see <a href="https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
     * @param apiEndpoint of api URI
     * @param requestId of request
     * @return invocation response uri
     */
    private static URI getInvocationErrorUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve(INVOCATION_URI + requestId + "/error");
    }

    /**
     * @return {@link #AWS_LAMBDA_RUNTIME_API}
     */
    private static URI getRuntimeApiEndpoint() {
        final String runtimeApiEndpoint = System.getenv(AWS_LAMBDA_RUNTIME_API);
        if (StringUtils.isEmpty(runtimeApiEndpoint))
            throw new IllegalStateException("Missing '" + AWS_LAMBDA_RUNTIME_API
                    + "' environment variable. Custom runtime can only be run within AWS Lambda environment.");

        return URI.create("http://" + runtimeApiEndpoint);
    }

    private static String getErrorResponse(Throwable e) {
        return "{\"errorMessage\":\"" + e.getMessage() + "\", \"errorType\":\"" + e.getClass().getSimpleName() + "\"}";
    }
}
