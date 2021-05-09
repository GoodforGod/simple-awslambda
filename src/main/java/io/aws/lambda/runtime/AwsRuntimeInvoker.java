package io.aws.lambda.runtime;

import io.aws.lambda.runtime.error.ContextException;
import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.http.AwsHttpClient;
import io.aws.lambda.runtime.http.AwsHttpResponse;
import io.aws.lambda.runtime.http.impl.NativeAwsHttpClient;
import io.aws.lambda.runtime.model.AwsRequestContext;
import io.aws.lambda.runtime.utils.StringUtils;
import io.aws.lambda.runtime.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.function.Supplier;

/**
 * Implementation of AWS Lambda invocation pipeline
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
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
     * AWS Lambda provides an HTTP API for custom runtimes to receive invocation
     * events from Lambda and send response data back within the Lambda execution
     * environment.
     */
    private static final String AWS_LAMBDA_RUNTIME_API = "AWS_LAMBDA_RUNTIME_API";

    private static final String INIT_ERROR = "/2018-06-01/runtime/init/error";
    private static final String INVOCATION_URI = "/2018-06-01/runtime/invocation/";
    private static final String INVOCATION_NEXT_URI = INVOCATION_URI + "next";

    /**
     * @param contextType class type to instantiate
     * @param handlerType class type to instantiate from context
     */
    public void invoke(@NotNull Class<? extends RuntimeContext> contextType,
                       @NotNull Class<? extends EventHandler> handlerType) {
        try (RuntimeContext context = getInstance(contextType)) {
            invoke(() -> context, handlerType);
        } catch (Exception e) {
            e.printStackTrace();
            final AwsHttpClient httpClient = new NativeAwsHttpClient();
            final URI apiEndpoint = getRuntimeApiEndpoint();
            httpClient.postAndForget(apiEndpoint.resolve(INIT_ERROR), getErrorResponse(e));
        }
    }

    /**
     * @param contextSupplier runtime instance supplier
     * @param handlerType     class type to instantiate from contextSupplier
     */
    public void invoke(@NotNull Supplier<RuntimeContext> contextSupplier,
                       @NotNull Class<? extends EventHandler> handlerType) {
        final URI apiEndpoint = getRuntimeApiEndpoint();
        final Logger logger = LoggerFactory.getLogger(getClass());
        final long contextStart = TimeUtils.getTime();
        try (final RuntimeContext context = contextSupplier.get()) {
            final EventHandler eventHandler = context.getBean(handlerType);
            final AwsHttpClient httpClient = context.getBean(AwsHttpClient.class);
            if (logger.isInfoEnabled()) {
                logger.info("Context startup took: {}", TimeUtils.timeSpent(contextStart));
                logger.debug("AWS Runtime URI: {}", apiEndpoint);
            }

            final URI invocationUri = getInvocationNextUri(apiEndpoint);
            logger.debug("AWS Runtime Event provider at: {}", invocationUri);

            while (!Thread.currentThread().isInterrupted()) {
                final AwsHttpResponse httpRequest = httpClient.get(invocationUri);
                if (StringUtils.isEmpty(httpRequest.body()))
                    throw new IllegalArgumentException("Request body is not present!");

                final String requestId = httpRequest.headerAnyOrThrow(LAMBDA_RUNTIME_AWS_REQUEST_ID);
                final String traceId = httpRequest.headerAny(LAMBDA_RUNTIME_TRACE_ID);
                final AwsRequestContext requestContext = new AwsRequestContext(requestId, traceId);

                if (logger.isDebugEnabled()) {
                    logger.debug("AWS Request Event received with {}", requestContext);
                    httpRequest.headers().forEach((k, v) -> logger.debug("Request header: {} - {}", k, v));
                }

                try {
                    final String responseEvent = eventHandler.handle(httpRequest.body(), requestContext);
                    final URI responseUri = getInvocationResponseUri(apiEndpoint, requestContext.getRequestId());
                    logger.debug("Responding to AWS invocation started: {}", responseUri);
                    final long respondingStart = TimeUtils.getTime();
                    final AwsHttpResponse awsResponse = httpClient.post(responseUri, responseEvent);
                    if (logger.isInfoEnabled())
                        logger.info("Responding to AWS invocation took: {}", TimeUtils.timeSpent(respondingStart));

                    logger.debug("AWS invocation response: Http Code '{}' and Body: {}",
                            awsResponse.code(), awsResponse.body());
                } catch (Exception e) {
                    logger.error("Invocation error occurred", e);
                    final URI uri = getInvocationErrorUri(apiEndpoint, requestContext.getRequestId());
                    httpClient.postAndForget(uri, getErrorResponse(e));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            final AwsHttpClient httpClient = new NativeAwsHttpClient();
            httpClient.postAndForget(apiEndpoint.resolve(INIT_ERROR), getErrorResponse(e));
        }
    }

    private static <T> T getInstance(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ContextException("Context can not be instantiated through constructor due to: " + e.getMessage());
        }
    }

    /**
     * Retrieves an invocation event.
     *
     * @param apiEndpoint of api URI
     * @return invocation response uri
     * @see <a href=
     *      "https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
     */
    private static URI getInvocationNextUri(URI apiEndpoint) {
        return apiEndpoint.resolve(INVOCATION_NEXT_URI);
    }

    /**
     * Sends an invocation response to Lambda.
     *
     * @param apiEndpoint of api URI
     * @param requestId   of request
     * @return invocation response uri
     * @see <a href=
     *      "https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
     */
    private static URI getInvocationResponseUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve(INVOCATION_URI + requestId + "/response");
    }

    /**
     * If the function returns an error, the runtime formats the error into a JSON
     * document, and posts it to the invocation error path.
     *
     * @param apiEndpoint of api URI
     * @param requestId   of request
     * @return invocation response uri
     * @see <a href=
     *      "https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
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
