package io.goodforgod.aws.lambda.simple.runtime;

import io.goodforgod.aws.lambda.simple.config.RuntimeVariables;
import io.goodforgod.aws.lambda.simple.config.SimpleLambdaDefaultLogLevelRefresher;
import io.goodforgod.aws.lambda.simple.handler.EventHandler;
import io.goodforgod.aws.lambda.simple.handler.LambdaContext;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpClient;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpRequest;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpResponse;
import io.goodforgod.aws.lambda.simple.http.nativeclient.NativeSimpleHttpClient;
import io.goodforgod.aws.lambda.simple.http.nativeclient.PublisherSimpleHttpRequest;
import io.goodforgod.aws.lambda.simple.http.nativeclient.StringSimpleHttpRequest;
import io.goodforgod.aws.lambda.simple.utils.StringUtils;
import io.goodforgod.aws.lambda.simple.utils.TimeUtils;
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
 * Runtime event loop for AWS Lambda event handling
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public final class SimpleLambdaRuntimeEventLoop {

    private static final Logger logger = LoggerFactory.getLogger(SimpleLambdaRuntimeEventLoop.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(11);

    /**
     * @param contextSupplier  RuntimeContext instance supplier
     * @param eventHandlerType type of EventHandler processor
     */
    public void execute(@NotNull Supplier<RuntimeContext> contextSupplier,
                        @NotNull Class<? extends EventHandler> eventHandlerType) {
        SimpleLambdaDefaultLogLevelRefresher.refresh();

        final URI apiEndpoint = getRuntimeApiEndpoint();
        logger.debug("AWS Runtime API Endpoint URI: {}", apiEndpoint);

        final long contextStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;
        try (final RuntimeContext context = contextSupplier.get()) {
            Objects.requireNonNull(context, "RuntimeContext can't be nullable!");

            final EventHandler eventHandler = context.getBean(eventHandlerType);
            final SimpleHttpClient httpClient = context.getBean(SimpleHttpClient.class);
            Objects.requireNonNull(eventHandler, "EventHandler '" + eventHandlerType.getName() + "' implementation can't be nullable!");
            Objects.requireNonNull(httpClient, "SimpleHttpClient implementation can't be nullable!");

            if (logger.isInfoEnabled()) {
                logger.info("RuntimeContext startup took: {} millis", TimeUtils.timeTook(contextStart));
            }

            final URI invocationUri = getInvocationNextUri(apiEndpoint);
            logger.debug("AWS Event Invocation URI: {}", invocationUri);

            while (!Thread.currentThread().isInterrupted()) {
                logger.trace("Invoking next event...");
                final SimpleHttpResponse requestEvent = httpClient.get(invocationUri, DEFAULT_TIMEOUT);
                SimpleLambdaDefaultLogLevelRefresher.refresh();
                logger.debug("Event received with httpCode '{}'", requestEvent.statusCode());

                final LambdaContext requestContext = LambdaContext.ofHeadersMulti(requestEvent.headersMultiValues());
                if (StringUtils.isEmpty(requestContext.getAwsRequestId()))
                    throw new IllegalStateException("AWS Request ID is not present!");

                logger.debug("Event received for {}", requestContext);
                logger.trace("Event headers: {}", requestEvent.headers());

                processRequestEvent(requestEvent, requestContext, eventHandler, httpClient, apiEndpoint);
            }
        } catch (Exception e) {
            logger.error("Function Initialization error occurred", e);
            final SimpleHttpClient httpClient = new NativeSimpleHttpClient();
            final URI errorUri = apiEndpoint.resolve(RuntimeVariables.INIT_ERROR);
            logger.debug("Responding to AWS Runtime Init Error URI: {}", errorUri);
            httpClient.postAndForget(errorUri, getErrorResponse(e), DEFAULT_TIMEOUT);
        }
    }

    private void processRequestEvent(SimpleHttpResponse httpRequest,
                                     LambdaContext requestContext,
                                     EventHandler eventHandler,
                                     SimpleHttpClient httpClient,
                                     URI apiEndpoint) {
        try (final InputStream eventStream = httpRequest.body()) {
            final Publisher<ByteBuffer> responsePublisher = eventHandler.handle(eventStream, requestContext);

            final URI responseUri = getInvocationResponseUri(apiEndpoint, requestContext.getAwsRequestId());
            logger.debug("Responding to AWS Invocation URI: {}", responseUri);
            final long respondingStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;

            final SimpleHttpRequest responseHttpEvent = PublisherSimpleHttpRequest.ofPublisher(responsePublisher);
            final SimpleHttpResponse awsResponse = httpClient.post(responseUri, responseHttpEvent, DEFAULT_TIMEOUT);
            if (logger.isDebugEnabled()) {
                logger.debug("Responding to AWS Invocation took: {} millis", TimeUtils.timeTook(respondingStart));
            }

            if (logger.isDebugEnabled()) {
                final String responseBody = awsResponse.bodyAsString();
                logger.debug("AWS Invocation responded with httpCode '{}' and body: {}",
                        awsResponse.statusCode(), responseBody);
            }
        } catch (Exception e) {
            logger.error("Function Invocation error occurred", e);
            final URI uri = getInvocationErrorUri(apiEndpoint, requestContext.getAwsRequestId());
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
        return apiEndpoint.resolve(RuntimeVariables.INVOCATION_NEXT_URI);
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
        return apiEndpoint.resolve(RuntimeVariables.INVOCATION_URI + requestId + "/response");
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
        return apiEndpoint.resolve(RuntimeVariables.INVOCATION_URI + requestId + "/error");
    }

    private static URI getRuntimeApiEndpoint() {
        final String runtimeApiEndpoint = System.getenv(RuntimeVariables.AWS_LAMBDA_RUNTIME_API);
        if (StringUtils.isEmpty(runtimeApiEndpoint))
            throw new IllegalStateException("Missing '" + RuntimeVariables.AWS_LAMBDA_RUNTIME_API
                    + "' environment variable. Custom runtime can only be run within AWS Lambda environment.");

        return URI.create("http://" + runtimeApiEndpoint);
    }

    private static SimpleHttpRequest getErrorResponse(Throwable e) {
        final String body = "{\"errorMessage\":\"" + e.getMessage() + "\", \"errorType\":\"" + e.getClass().getSimpleName() + "\"}";
        return StringSimpleHttpRequest.ofJson(body);
    }
}
