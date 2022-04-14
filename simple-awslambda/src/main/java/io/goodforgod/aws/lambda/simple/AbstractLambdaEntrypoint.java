package io.goodforgod.aws.lambda.simple;

import io.goodforgod.aws.lambda.simple.handler.EventHandler;
import io.goodforgod.aws.lambda.simple.handler.impl.InputEventHandler;
import io.goodforgod.aws.lambda.simple.runtime.RuntimeContext;
import io.goodforgod.aws.lambda.simple.utils.TimeUtils;
import java.util.Objects;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SimpleLambdaRuntimeEventLoop eventLoop;
    private final RuntimeContext runtimeContext;

    protected AbstractLambdaEntrypoint() {
        final long contextStart = TimeUtils.getTime();
        this.runtimeContext = initializeRuntimeContext();
        this.eventLoop = getLambdaRuntimeEventLoop();
        if (logger.isInfoEnabled()) {
            logger.info("RuntimeContext compile time initialization took: {} millis", TimeUtils.timeTook(contextStart));
        }
    }

    public final void run(String[] args) {
        try {
            Objects.requireNonNull(eventLoop, "EventLoop can't be nullable!");
            Objects.requireNonNull(runtimeContext, "RuntimeContext can't be nullable!");

            final String eventHandlerQualifier = getEventHandlerQualifier();
            eventLoop.execute(runtimeContext, eventHandlerQualifier);
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }

    /**
     * @return {@link RuntimeContext} implementation
     */
    public final RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    /**
     * @return initial {@link RuntimeContext} implementation
     */
    protected abstract RuntimeContext initializeRuntimeContext();

    /**
     * @return Type of {@link EventHandler} implementation that will be responsible for handing event
     *             processing
     */
    public String getEventHandlerQualifier() {
        return InputEventHandler.QUALIFIER;
    }

    @NotNull
    private static SimpleLambdaRuntimeEventLoop getLambdaRuntimeEventLoop() {
        return new SimpleLambdaRuntimeEventLoop();
    }

    protected void handleInitializationError(Throwable e) {
        logger.error(e.getMessage(), e);
        System.exit(1);
    }
}
