package io.aws.lambda.simple.runtime.runtime;

import io.aws.lambda.simple.runtime.LambdaContext;
import io.aws.lambda.simple.runtime.config.RuntimeVariables;
import io.aws.lambda.simple.runtime.config.SimpleLoggerRefresher;
import io.aws.lambda.simple.runtime.context.RuntimeContext;
import io.aws.lambda.simple.runtime.error.LambdaException;
import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.http.AwsHttpClient;
import io.aws.lambda.simple.runtime.http.AwsHttpRequest;
import io.aws.lambda.simple.runtime.http.AwsHttpResponse;
import io.aws.lambda.simple.runtime.http.impl.NativeAwsHttpClient;
import io.aws.lambda.simple.runtime.http.impl.SimpleAwsHttpRequest;
import io.aws.lambda.simple.runtime.utils.StringUtils;
import io.aws.lambda.simple.runtime.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.util.function.Supplier;

/**
 * Main runtime for AWS Lambda event hanlding
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class DefaultLambdaEventRuntime {

    private static final String INIT_ERROR = "/2018-06-01/runtime/init/error";
    private static final String INVOCATION_URI = "/2018-06-01/runtime/invocation/";
    private static final String INVOCATION_NEXT_URI = INVOCATION_URI + "next";

    private static final Logger logger = LoggerFactory.getLogger(DefaultLambdaEventRuntime.class);

    /**
     * @param contextType      type of RuntimeContext instance
     * @param eventHandlerType type of EventHandler processor
     */
    public void execute(@NotNull Class<? extends RuntimeContext> contextType,
                        @NotNull Class<? extends EventHandler> eventHandlerType) {
        try (RuntimeContext context = getInstance(contextType)) {
            execute(() -> context, eventHandlerType);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            final URI apiEndpoint = getRuntimeApiEndpoint();
            final AwsHttpClient httpClient = new NativeAwsHttpClient();
            httpClient.postAndForget(apiEndpoint.resolve(INIT_ERROR), getErrorResponse(e));
        }
    }

    /**
     * @param contextSupplier  RuntimeContext instance supplier
     * @param eventHandlerType type of EventHandler processor
     */
    public void execute(@NotNull Supplier<RuntimeContext> contextSupplier,
                        @NotNull Class<? extends EventHandler> eventHandlerType) {
        SimpleLoggerRefresher.refresh();

        final URI apiEndpoint = getRuntimeApiEndpoint();
        logger.debug("AWS Runtime API Endpoint URI: {}", apiEndpoint);

        final long contextStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;
        try (final RuntimeContext context = contextSupplier.get()) {
            final EventHandler eventHandler = context.getBean(eventHandlerType);
            final AwsHttpClient httpClient = context.getBean(AwsHttpClient.class);

            if (logger.isInfoEnabled()) {
                logger.info("RuntimeContext startup took: {} millis", TimeUtils.timeTook(contextStart));
            }

            final URI invocationUri = getInvocationNextUri(apiEndpoint);
            logger.debug("Event invocation URI: {}", invocationUri);

            while (!Thread.currentThread().isInterrupted()) {
                logger.trace("Invoking next event...");
                final AwsHttpResponse requestEvent = httpClient.get(invocationUri);
                logger.debug("Event received with httpCode '{}'", requestEvent.code());

                final LambdaContext requestContext = LambdaContext.ofHeadersMulti(requestEvent.headers());
                if (StringUtils.isEmpty(requestContext.getAwsRequestId()))
                    throw new IllegalStateException("AWS Request ID is not present!");

                logger.debug("Event received for {}", requestContext);
                if (logger.isTraceEnabled()) {
                    logger.trace("Event headers: {}", requestEvent.headerFirstValues());
                }

                processRequestEvent(requestEvent, requestContext, eventHandler, httpClient, apiEndpoint);
            }
        } catch (Exception e) {
            logger.error("Function Initialization error occurred", e);
            final AwsHttpClient httpClient = new NativeAwsHttpClient();
            final URI errorUri = apiEndpoint.resolve(INIT_ERROR);
            logger.debug("Responding to AWS Runtime Init Error URI: {}", errorUri);
            httpClient.postAndForget(errorUri, getErrorResponse(e));
        }
    }

    private void processRequestEvent(AwsHttpResponse httpRequest,
                                     LambdaContext requestContext,
                                     EventHandler eventHandler,
                                     AwsHttpClient httpClient,
                                     URI apiEndpoint) {
        try (final InputStream eventStream = httpRequest.body()) {
            final String responseEvent = eventHandler.handle(eventStream, requestContext);

            final URI responseUri = getInvocationResponseUri(apiEndpoint, requestContext.getAwsRequestId());
            logger.debug("Responding to AWS Invocation URI: {}", responseUri);
            final long respondingStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;

            final SimpleAwsHttpRequest responseHttpEvent = SimpleAwsHttpRequest.ofJson(responseEvent);
            final AwsHttpResponse awsResponse = httpClient.post(responseUri, responseHttpEvent);
            if (logger.isInfoEnabled()) {
                logger.info("Responding to AWS Invocation took: {} millis", TimeUtils.timeTook(respondingStart));
            }

            if (logger.isDebugEnabled()) {
                final String responseBody = awsResponse.bodyAsString();
                logger.debug("AWS Invocation responded with httpCode '{}' and body: {}",
                        awsResponse.code(), responseBody);
            }
        } catch (Exception e) {
            logger.error("Function Invocation error occurred", e);
            final URI uri = getInvocationErrorUri(apiEndpoint, requestContext.getAwsRequestId());
            logger.debug("Responding to AWS Invocation Error URI: {}", uri);
            httpClient.postAndForget(uri, getErrorResponse(e));
        }
    }

    private static <T> T getInstance(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new LambdaException("Context can not be instantiated through constructor due to: " + e.getMessage());
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

    private static AwsHttpRequest getErrorResponse(Throwable e) {
        final String body = "{\"errorMessage\":\"" + e.getMessage() + "\", \"errorType\":\"" + e.getClass().getSimpleName() + "\"}";
        return SimpleAwsHttpRequest.ofJson(body);
    }
}
