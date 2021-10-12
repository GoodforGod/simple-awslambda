package io.goodforgod.aws.simplelambda.runtime;

import com.amazonaws.services.lambda.runtime.Context;
import io.goodforgod.aws.simplelambda.config.AwsRuntimeVariables;
import io.goodforgod.aws.simplelambda.error.StatusException;
import io.goodforgod.aws.simplelambda.handler.EventHandler;
import io.goodforgod.aws.simplelambda.http.SimpleHttpClient;
import io.goodforgod.aws.simplelambda.http.SimpleHttpRequest;
import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.http.common.StringHttpRequest;
import io.goodforgod.aws.simplelambda.http.nativeclient.NativeSimpleHttpClient;
import io.goodforgod.aws.simplelambda.http.nativeclient.PublisherHttpRequest;
import io.goodforgod.aws.simplelambda.utils.StringUtils;
import io.goodforgod.aws.simplelambda.utils.TimeUtils;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Objects;
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
public final class SimpleLambdaRuntimeEventLoop {

    private static final Logger logger = LoggerFactory.getLogger(SimpleLambdaRuntimeEventLoop.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(11);

    /**
     * @param runtimeContext        RuntimeContext instance supplier
     * @param eventHandlerQualifier to use for implementation injection
     */
    public void execute(@NotNull RuntimeContext runtimeContext,
                        @NotNull String eventHandlerQualifier) {
        SimpleLoggerLogLevelRefresher.refresh();

        final URI apiEndpoint = getRuntimeApiEndpoint();
        logger.debug("AWS Runtime API Endpoint URI: {}", apiEndpoint);

        final long contextStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;
        try (final RuntimeContext context = runtimeContext) {
            Objects.requireNonNull(context, "RuntimeContext can't be nullable!");

            final EventHandler eventHandler = context.getBean(EventHandler.class, eventHandlerQualifier);
            final SimpleHttpClient httpClient = context.getBean(SimpleHttpClient.class);
            Objects.requireNonNull(eventHandler, "EventHandler implementation for qualifier '" + eventHandlerQualifier + "' not found!");
            Objects.requireNonNull(httpClient, "SimpleHttpClient implementation not found!");

            if (logger.isInfoEnabled()) {
                logger.info("RuntimeContext startup took: {} millis", TimeUtils.timeTook(contextStart));
            }

            final URI invocationUri = getInvocationNextUri(apiEndpoint);
            logger.debug("AWS Event Invocation URI: {}", invocationUri);

            while (!Thread.currentThread().isInterrupted()) {
                logger.trace("Invoking next event...");
                final SimpleHttpResponse event = httpClient.get(invocationUri, DEFAULT_TIMEOUT);
                SimpleLoggerLogLevelRefresher.refresh();
                logger.trace("Event received with httpCode '{}' with headers: {}", event.statusCode(), event.headers());
                if (event.statusCode() != 200) {
                    throw new StatusException(event.statusCode(), event.bodyAsString());
                }

                final Context requestContext = EventContext.ofHeadersMulti(event.headersMultiValues());
                if (StringUtils.isEmpty(requestContext.getAwsRequestId())) {
                    throw new IllegalStateException("AWS Request ID is not present!");
                }

                logger.debug("Event received with RequestContext: {}", requestContext);
                processEvent(event::body, requestContext, eventHandler, httpClient, apiEndpoint);
            }
        } catch (Exception e) {
            logger.error("Function unexpected initialization error occurred", e);
            final SimpleHttpClient httpClient = new NativeSimpleHttpClient();
            final URI errorUri = apiEndpoint.resolve(AwsRuntimeVariables.INIT_ERROR);
            logger.debug("Responding to AWS Runtime Init Error URI: {}", errorUri);
            httpClient.postAndForget(errorUri, getErrorResponse(e), DEFAULT_TIMEOUT);
        }
    }

    private void processEvent(Supplier<InputStream> eventSupplier,
                              Context eventContext,
                              EventHandler eventHandler,
                              SimpleHttpClient httpClient,
                              URI apiResponseEndpoint) {
        try (final InputStream eventStream = eventSupplier.get()) {
            final Publisher<ByteBuffer> responsePublisher = eventHandler.handle(eventStream, eventContext);

            final URI responseUri = getInvocationResponseUri(apiResponseEndpoint, eventContext.getAwsRequestId());
            logger.debug("Responding to AWS Invocation URI: {}", responseUri);
            final long respondingStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;

            final SimpleHttpRequest responseHttpEvent = PublisherHttpRequest.ofPublisher(responsePublisher);
            final SimpleHttpResponse awsResponse = httpClient.post(responseUri, responseHttpEvent, DEFAULT_TIMEOUT);
            if (logger.isDebugEnabled()) {
                logger.debug("Responding to AWS Invocation took: {} millis", TimeUtils.timeTook(respondingStart));
            }

            if (logger.isTraceEnabled()) {
                final String responseBody = awsResponse.bodyAsString();
                logger.trace("AWS Invocation responded with httpCode '{}' and body: {}",
                        awsResponse.statusCode(), responseBody);
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
     *      "https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
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
     *      "https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
     */
    private static URI getInvocationResponseUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve(AwsRuntimeVariables.INVOCATION_URI + requestId + "/response");
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
        final String body = "{\"errorMessage\":\"" + e.getMessage() + "\", \"errorType\":\"" + e.getClass().getSimpleName() + "\"}";
        return StringHttpRequest.ofJson(body);
    }
}
