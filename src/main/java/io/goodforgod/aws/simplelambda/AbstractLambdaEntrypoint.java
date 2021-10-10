package io.goodforgod.aws.simplelambda;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.simplelambda.handler.EventHandler;
import io.goodforgod.aws.simplelambda.handler.impl.InputEventHandler;
import io.goodforgod.aws.simplelambda.runtime.RuntimeContext;
import io.goodforgod.aws.simplelambda.runtime.SimpleLambdaRuntimeEventLoop;
import io.goodforgod.aws.simplelambda.runtime.SimpleRuntimeContext;
import io.goodforgod.aws.simplelambda.utils.TimeUtils;
import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Simple Lambda Entrypoint
 *
 * @author Anton Kurako (GoodforGod)
 * @since 14.08.2021
 */
public abstract class AbstractLambdaEntrypoint {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLambdaEntrypoint.class);

    private final RuntimeContext runtimeContext;
    private final SimpleLambdaRuntimeEventLoop eventLoop;

    protected AbstractLambdaEntrypoint() {
        final long contextStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;

        this.runtimeContext = getRuntimeContext();
        this.eventLoop = getLambdaRuntimeEventLoop();

        if (logger.isInfoEnabled()) {
            logger.info("RuntimeContext startup took: {} millis", TimeUtils.timeTook(contextStart));
        }
    }

    protected void run(String[] args) {
        try {
            final String eventHandler = getEventHandlerQualifier();
            Objects.requireNonNull(eventLoop, "Event loop runtime can't be nullable!");
            eventLoop.execute(runtimeContext, eventHandler);
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }

    /**
     * @return {@link RequestHandler} implementation
     */
    @NotNull
    protected abstract Function<RuntimeContext, RequestHandler> getRequestHandler();

    /**
     * @return Type of {@link EventHandler} implementation that will be responsible
     *         for handing event processing
     */
    @NotNull
    protected String getEventHandlerQualifier() {
        return InputEventHandler.QUALIFIER;
    }

    /**
     * @return {@link RuntimeContext} implementation for Lambda
     */
    @NotNull
    protected RuntimeContext getRuntimeContext() {
        return new SimpleRuntimeContext(getRequestHandler());
    }

    @NotNull
    protected SimpleLambdaRuntimeEventLoop getLambdaRuntimeEventLoop() {
        return new SimpleLambdaRuntimeEventLoop();
    }

    protected void handleInitializationError(Throwable e) {
        final Logger logger = LoggerFactory.getLogger(AbstractLambdaEntrypoint.class);
        logger.error(e.getMessage(), e);
        System.exit(1);
    }
}
