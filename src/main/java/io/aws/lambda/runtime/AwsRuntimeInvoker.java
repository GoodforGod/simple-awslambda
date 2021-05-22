package io.aws.lambda.runtime;

import io.aws.lambda.runtime.config.RuntimeVariables;
import io.aws.lambda.runtime.error.ContextException;
import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.http.AwsHttpClient;
import io.aws.lambda.runtime.http.AwsHttpResponse;
import io.aws.lambda.runtime.http.impl.NativeAwsHttpClient;
import io.aws.lambda.runtime.utils.StringUtils;
import io.aws.lambda.runtime.utils.TimeUtils;
import io.micronaut.core.annotation.TypeHint;
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
@TypeHint(
        accessType = { TypeHint.AccessType.ALL_DECLARED_CONSTRUCTORS, TypeHint.AccessType.ALL_PUBLIC },
        value = {
                com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent.class,
                com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent.ProxyRequestContext.class,
                com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent.RequestIdentity.class,
                com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent.class,
                com.amazonaws.services.lambda.runtime.events.ScheduledEvent.class,

                com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent.class,
                com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent.RequestContext.class,
                com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent.RequestContext.Authorizer.class,
                com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent.RequestContext.Http.class,
                com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent.RequestContext.IAM.class,
                com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent.RequestContext.CognitoIdentity.class,
                com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse.class,

                com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent.class,
                com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent.RequestContext.class,
                com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent.RequestIdentity.class,
                com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse.class,

                com.amazonaws.services.lambda.runtime.events.CloudFrontEvent.class,
                com.amazonaws.services.lambda.runtime.events.CloudWatchLogsEvent.class,
                com.amazonaws.services.lambda.runtime.events.CodeCommitEvent.class,
                com.amazonaws.services.lambda.runtime.events.CognitoEvent.class,
                com.amazonaws.services.lambda.runtime.events.ConfigEvent.class,
                com.amazonaws.services.lambda.runtime.events.IoTButtonEvent.class,
                com.amazonaws.services.lambda.runtime.events.LexEvent.class,
                com.amazonaws.services.lambda.runtime.events.SNSEvent.class,
                com.amazonaws.services.lambda.runtime.events.SQSEvent.class
        })
public class AwsRuntimeInvoker {

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
        final long contextStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;

        try (final RuntimeContext context = contextSupplier.get()) {
            final EventHandler eventHandler = context.getBean(handlerType);
            final AwsHttpClient httpClient = context.getBean(AwsHttpClient.class);
            if (logger.isInfoEnabled()) {
                logger.info("Context startup took: {} millis", TimeUtils.timeTook(contextStart));
                logger.debug("AWS Runtime URI: {}", apiEndpoint);
            }

            final URI invocationUri = getInvocationNextUri(apiEndpoint);
            logger.debug("AWS Runtime Event provider at: {}", invocationUri);

            while (!Thread.currentThread().isInterrupted()) {
                final AwsHttpResponse httpRequest = httpClient.get(invocationUri);
                if (StringUtils.isEmpty(httpRequest.body()))
                    throw new IllegalArgumentException("Request body is not present!");

                final LambdaContext requestContext = LambdaContext.ofHeadersMulti(httpRequest.headers());
                if (StringUtils.isEmpty(requestContext.getAwsRequestId()))
                    throw new IllegalArgumentException("Request ID is not present!");

                if (logger.isDebugEnabled()) {
                    logger.debug("AWS Request Event received with {}", requestContext);
                    httpRequest.headers().forEach((k, v) -> logger.debug("Request header: {} - {}", k, v));
                }

                try {
                    final String responseEvent = eventHandler.handle(httpRequest.body(), requestContext);
                    final URI responseUri = getInvocationResponseUri(apiEndpoint, requestContext.getAwsRequestId());
                    logger.debug("Responding to AWS invocation started: {}", responseUri);

                    final long respondingStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;
                    final AwsHttpResponse awsResponse = httpClient.post(responseUri, responseEvent);
                    if (logger.isInfoEnabled()) {
                        logger.info("Responding to AWS invocation took: {} millis", TimeUtils.timeTook(respondingStart));
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("AWS invocation response: Http Code '{}' and Body: {}",
                                awsResponse.code(), awsResponse.body());
                    }
                } catch (Exception e) {
                    logger.error("Invocation error occurred", e);
                    final URI uri = getInvocationErrorUri(apiEndpoint, requestContext.getAwsRequestId());
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

    private static URI getRuntimeApiEndpoint() {
        final String runtimeApiEndpoint = System.getenv(RuntimeVariables.AWS_LAMBDA_RUNTIME_API);
        if (StringUtils.isEmpty(runtimeApiEndpoint))
            throw new IllegalStateException("Missing '" + RuntimeVariables.AWS_LAMBDA_RUNTIME_API
                    + "' environment variable. Custom runtime can only be run within AWS Lambda environment.");

        return URI.create("http://" + runtimeApiEndpoint);
    }

    private static String getErrorResponse(Throwable e) {
        return "{\"errorMessage\":\"" + e.getMessage() + "\", \"errorType\":\"" + e.getClass().getSimpleName() + "\"}";
    }
}
