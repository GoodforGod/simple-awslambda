package io.goodforgod.aws.lambda.simple;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.lambda.simple.config.AwsContextVariables;
import io.goodforgod.aws.lambda.simple.handler.Event;
import io.goodforgod.aws.lambda.simple.handler.EventHandler;
import io.goodforgod.aws.lambda.simple.http.*;
import io.goodforgod.aws.lambda.simple.http.nativeclient.NativeHttpClient;
import io.goodforgod.aws.lambda.simple.http.nativeclient.SimpleAwsRuntimeClient;
import io.goodforgod.aws.lambda.simple.runtime.RuntimeContext;
import io.goodforgod.aws.lambda.simple.utils.TimeUtils;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow.Publisher;
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

    /**
     * @param runtimeContext        RuntimeContext instance supplier
     * @param eventHandlerQualifier to use for implementation injection
     */
    void execute(@NotNull RuntimeContext runtimeContext, @NotNull String eventHandlerQualifier) {
        SimpleLoggerRefresher.refresh();

        final long contextStart = TimeUtils.getTime();
        try (final RuntimeContext context = runtimeContext) {
            context.setupInRuntime();

            final AwsRuntimeClient awsRuntimeClient = context.getBean(AwsRuntimeClient.class);
            if (awsRuntimeClient == null) {
                throw new IllegalStateException("AwsRuntimeClient bean not found, but expected!");
            }

            final URI awsRuntimeApiEndpoint = awsRuntimeClient.getAwsRuntimeApi();
            logger.debug("AWS Runtime API Endpoint URI: {}", awsRuntimeApiEndpoint);
            if (logger.isInfoEnabled()) {
                logger.info("RuntimeContext runtime initialization took: {} millis", TimeUtils.timeTook(contextStart));
            }

            final AwsRuntimeLoopCondition loopCondition = runtimeContext.getBean(AwsRuntimeLoopCondition.class);
            while (loopCondition.continueLoop()) {
                final EventHandler eventHandler = context.getBean(EventHandler.class, eventHandlerQualifier);
                if (eventHandler == null) {
                    throw new IllegalStateException("EventHandler bean for qualifier '" + eventHandlerQualifier + "' not found!");
                }

                final String handlerName = System.getenv(AwsContextVariables.HANDLER);
                RequestHandler requestHandler = context.getBean(RequestHandler.class, handlerName);
                if (requestHandler == null) {
                    logger.debug("RequestHandler bean for qualifier '{}' not found, looking without qualifier...", handlerName);
                    requestHandler = context.getBean(RequestHandler.class);
                }

                if (requestHandler == null) {
                    throw new IllegalStateException("RequestHandler bean for qualifier '" + handlerName + "' not found!");
                }

                logger.trace("Invoking next event...");
                final Event event = awsRuntimeClient.getNextEvent(awsRuntimeApiEndpoint);

                logger.debug("Event received with Context: {}", event.context());

                try {
                    final Publisher<ByteBuffer> responsePublisher = eventHandler.handle(event, requestHandler);
                    final SimpleHttpBody response = SimpleHttpBody.ofPublisher(responsePublisher);
                    awsRuntimeClient.reportInvocationSuccess(awsRuntimeApiEndpoint, response, event.context());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    awsRuntimeClient.reportInvocationError(awsRuntimeApiEndpoint, e, event.context());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            final AwsRuntimeClient awsRuntimeClient = getDefaultAwsRuntimeClient(runtimeContext);
            final URI awsRuntimeApiEndpoint = awsRuntimeClient.getAwsRuntimeApi();
            awsRuntimeClient.reportInitializationError(awsRuntimeApiEndpoint, e);
        }
    }

    AwsRuntimeClient getDefaultAwsRuntimeClient(RuntimeContext context) {
        final AwsRuntimeClient awsRuntimeClient = context.getBean(AwsRuntimeClient.class);
        if (awsRuntimeClient == null) {
            return new SimpleAwsRuntimeClient(new NativeHttpClient());
        }

        return awsRuntimeClient;
    }
}
