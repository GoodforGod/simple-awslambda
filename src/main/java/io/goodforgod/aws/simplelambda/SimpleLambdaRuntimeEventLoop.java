package io.goodforgod.aws.simplelambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.simplelambda.config.AwsRuntimeVariables;
import io.goodforgod.aws.simplelambda.handler.EventHandler;
import io.goodforgod.aws.simplelambda.http.SimpleHttpClient;
import io.goodforgod.aws.simplelambda.http.SimpleHttpRequest;
import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.http.common.StringHttpRequest;
import io.goodforgod.aws.simplelambda.http.nativeclient.NativeHttpClient;
import io.goodforgod.aws.simplelambda.http.nativeclient.PublisherNativeHttpRequest;
import io.goodforgod.aws.simplelambda.runtime.RuntimeContext;
import io.goodforgod.aws.simplelambda.utils.StringUtils;
import io.goodforgod.aws.simplelambda.utils.TimeUtils;
import io.goodforgod.http.common.HttpStatus;
import io.goodforgod.http.common.exception.HttpStatusException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.Flow.Publisher;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime event loop for AWS Lambda
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
final class SimpleLambdaRuntimeEventLoop {

    private static final Logger logger = LoggerFactory.getLogger(SimpleLambdaRuntimeEventLoop.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(11);

    /**
     * @param runtimeContext        RuntimeContext instance supplier
     * @param eventHandlerQualifier to use for implementation injection
     */
    void execute(@NotNull RuntimeContext runtimeContext, @NotNull String eventHandlerQualifier) {
        SimpleLoggerLogLevelRefresher.refresh();

        final URI apiEndpoint = getRuntimeApiEndpoint();
        logger.debug("AWS Runtime API Endpoint URI: {}", apiEndpoint);

        final long contextStart = TimeUtils.getTime();
        try (final RuntimeContext context = runtimeContext) {
            context.setupInRuntime();
            final SimpleHttpClient httpClient = context.getBean(SimpleHttpClient.class);

            if (logger.isInfoEnabled()) {
                logger.info("RuntimeContext runtime initialization took: {} millis", TimeUtils.timeTook(contextStart));
            }

            final URI invocationUri = getInvocationNextUri(apiEndpoint);
            logger.debug("AWS Event Invocation URI: {}", invocationUri);

            while (!Thread.currentThread().isInterrupted()) {
                processLoop(context, eventHandlerQualifier, httpClient, invocationUri, apiEndpoint);
            }
        } catch (Exception e) {
            logger.error("Function unexpected initialization error occurred", e);
            final SimpleHttpClient httpClient = new NativeHttpClient();
            final URI errorUri = apiEndpoint.resolve(AwsRuntimeVariables.INIT_ERROR);
            logger.debug("Responding to AWS Runtime Init Error URI: {}", errorUri);
            httpClient.postAndForget(errorUri, getErrorResponse(e), DEFAULT_TIMEOUT);
        }
    }

    private static void processLoop(RuntimeContext context,
                                    String eventHandlerQualifier,
                                    SimpleHttpClient httpClient,
                                    URI invocationUri,
                                    URI apiEndpoint) {
        logger.trace("Invoking next event...");

        final SimpleHttpResponse event = httpClient.get(invocationUri, DEFAULT_TIMEOUT);
        SimpleLoggerLogLevelRefresher.refresh();
        logger.trace("Event received with httpCode '{}' with headers: {}", event.status(), event.headers());

        if (!HttpStatus.OK.equals(event.status())) {
            throw new HttpStatusException(event.status(), event.bodyAsString());
        }

        final EventHandler eventHandler = context.getBean(EventHandler.class, eventHandlerQualifier);
        if (eventHandler == null) {
            throw new IllegalStateException("EventHandler for qualifier '" + eventHandlerQualifier + "' not found!");
        }

        final EventContext requestContext = EventContext.ofHeaders(event.headers());
        if (StringUtils.isEmpty(requestContext.getAwsRequestId())) {
            throw new IllegalStateException("AWS Request ID is not present!");
        }

        final String handlerName = requestContext.getHandlerName();
        RequestHandler requestHandler = context.getBean(RequestHandler.class, handlerName);
        if (requestHandler == null) {
            logger.debug("RequestHandler for qualifier '{}' not found, looking without qualifier...", handlerName);
            requestHandler = context.getBean(RequestHandler.class);
        }

        if (requestHandler == null) {
            throw new IllegalStateException("RequestHandler for qualifier '" + handlerName + "' not found!");
        }

        logger.debug("Event received with RequestContext: {}", requestContext);
        processEvent(event::body, requestContext, eventHandler, requestHandler, httpClient, apiEndpoint);
    }

    private static void processEvent(Supplier<InputStream> eventSupplier,
                                     Context eventContext,
                                     EventHandler eventHandler,
                                     RequestHandler requestHandler,
                                     SimpleHttpClient httpClient,
                                     URI apiResponseEndpoint) {
        try (final InputStream eventStream = eventSupplier.get()) {
            final Publisher<ByteBuffer> responsePublisher = eventHandler.handle(requestHandler, eventStream, eventContext);

            final URI responseUri = getInvocationResponseUri(apiResponseEndpoint, eventContext.getAwsRequestId());
            logger.debug("Responding to AWS Invocation URI: {}", responseUri);
            final long respondingStart = (logger.isDebugEnabled())
                    ? TimeUtils.getTime()
                    : 0;

            final SimpleHttpRequest responseHttpEvent = PublisherNativeHttpRequest.ofPublisher(responsePublisher);
            final SimpleHttpResponse awsResponse = httpClient.post(responseUri, responseHttpEvent, DEFAULT_TIMEOUT);
            if (logger.isDebugEnabled()) {
                logger.debug("Responding to AWS Invocation took: {} millis", TimeUtils.timeTook(respondingStart));
            }

            if (logger.isTraceEnabled()) {
                final String responseBody = awsResponse.bodyAsString();
                logger.trace("AWS Invocation responded with httpCode '{}' and body: {}",
                        awsResponse.status(), responseBody);
            }
        } catch (Exception e) {
            logger.error("Function Invocation error occurred", e);
            final URI uri = getInvocationErrorUri(apiResponseEndpoint, eventContext.getAwsRequestId());
            logger.debug("Responding to AWS Invocation Error URI: {}", uri);
            httpClient.postAndForget(uri, getErrorResponse(e), DEFAULT_TIMEOUT);
        }
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

    private static URI getRuntimeApiEndpoint() {
        final String runtimeApiEndpoint = System.getenv(AwsRuntimeVariables.AWS_LAMBDA_RUNTIME_API);
        if (StringUtils.isEmpty(runtimeApiEndpoint))
            throw new IllegalStateException("Missing '" + AwsRuntimeVariables.AWS_LAMBDA_RUNTIME_API
                    + "' environment variable. Custom runtime can only be run within AWS Lambda environment.");

        return URI.create("http://" + runtimeApiEndpoint);
    }

    private static SimpleHttpRequest getErrorResponse(Throwable e) {
        final String body = "{\"errorMessage\":\"" + e.getMessage() + "\", \"errorType\":\"" + e.getClass().getName() + "\"}";
        return StringHttpRequest.ofJson(body);
    }
}
