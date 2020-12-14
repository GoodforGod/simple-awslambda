package io.aws.lambda.runtime.invoker;

import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.http.AwsHttpClient;
import io.aws.lambda.runtime.http.AwsHttpResponse;
import io.aws.lambda.runtime.http.impl.NativeAwsHttpClient;
import io.aws.lambda.runtime.logger.LambdaLogger;
import io.aws.lambda.runtime.model.AwsRequestContext;
import io.aws.lambda.runtime.utils.TimeUtils;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
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

    private static final String AWS_LAMBDA_RUNTIME_API = "AWS_LAMBDA_RUNTIME_API";
    private static final String NEXT_INVOCATION_URI = "/2018-06-01/runtime/invocation/next";
    private static final String INIT_ERROR = "/2018-06-01/runtime/init/error";

    public void invoke(@NotNull Class<? extends EventHandler> handlerType) {
        final URI apiEndpoint = getRuntimeApiEndpoint();
        final long contextStart = TimeUtils.getTime();
        final ApplicationContextBuilder builder = ApplicationContext.builder();
        try (final ApplicationContext context = builder.build().start()) {
            final EventHandler eventHandler = context.getBean(handlerType);
            final LambdaLogger logger = context.getBean(LambdaLogger.class);
            final AwsHttpClient httpClient = context.getBean(AwsHttpClient.class);
            logger.debug("Context startup took: %s", TimeUtils.timeSpent(contextStart));
            logger.debug("AWS runtime uri: %s", apiEndpoint);

            final URI invocationUri = apiEndpoint.resolve(NEXT_INVOCATION_URI);
            logger.debug("Request event awaiting at: %s", invocationUri);

            while (!Thread.currentThread().isInterrupted()) {
                final AwsHttpResponse httpRequest = httpClient.get(invocationUri);
                if (StringUtils.isEmpty(httpRequest.body()))
                    throw new IllegalArgumentException("Request body is not present!");

                final String requestId = httpRequest.headerAnyOrThrow(LAMBDA_RUNTIME_AWS_REQUEST_ID);
                final String traceId = httpRequest.headerAny(LAMBDA_RUNTIME_TRACE_ID);
                final AwsRequestContext requestContext = new AwsRequestContext(requestId, traceId);

                logger.refresh();
                logger.debug("Request event received with RequestID: %s", requestContext.getRequestId());
                if (logger.isDebugEnabled()) {
                    httpRequest.headers().forEach((k, v) -> logger.debug("Request header: %s - %s", k, v));
                }

                try {
                    final String responseEvent = eventHandler.handle(httpRequest.body(), requestContext);
                    final URI responseUri = getResponseUri(apiEndpoint, requestContext.getRequestId());

                    logger.debug("Responding to AWS started: %s", responseUri);
                    final long respondingStart = TimeUtils.getTime();
                    final AwsHttpResponse awsResponse = httpClient.post(responseUri, responseEvent);
                    logger.info("Responding to AWS took: %s", TimeUtils.timeSpent(respondingStart));
                    logger.debug("AWS responded with http code '%s' and body: %s",
                            awsResponse.code(), awsResponse.body().strip());
                } catch (Exception e) {
                    logger.error("Invocation error occurred", e);
                    final URI uri = getResponseErrorUri(apiEndpoint, requestContext.getRequestId());
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

    private static URI getResponseErrorUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve("/2018-06-01/runtime/invocation/" + requestId + "/error");
    }

    private static URI getRuntimeApiEndpoint() {
        final String runtimeApiEndpoint = System.getenv(AWS_LAMBDA_RUNTIME_API);
        if (StringUtils.isEmpty(runtimeApiEndpoint))
            throw new IllegalStateException("Missing " + AWS_LAMBDA_RUNTIME_API
                    + " environment variable. Custom runtime can only be run within AWS Lambda environment.");

        return URI.create("http://" + runtimeApiEndpoint);
    }

    private static String getErrorResponse(Throwable e) {
        return "{\"errorMessage\":\"" + e.getMessage() + "\", \"errorType\":\"" + e.getClass().getSimpleName() + "\"}";
    }
}
