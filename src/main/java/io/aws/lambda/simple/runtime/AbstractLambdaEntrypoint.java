package io.aws.lambda.simple.runtime;

import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.runtime.RuntimeContext;
import io.aws.lambda.simple.runtime.runtime.SimpleLambdaRuntimeEventLoop;
import java.util.function.Supplier;
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

    protected void run(String[] args) {
        try {
            final Supplier<RuntimeContext> runtimeContext = () -> getRuntimeContext(args);
            final Class<? extends EventHandler> handlerType = getEventHandlerType(args);
            final SimpleLambdaRuntimeEventLoop eventLoop = getDefaultRuntimeEventLoop();
            eventLoop.execute(runtimeContext, handlerType);
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }

    @NotNull
    protected abstract Class<? extends EventHandler> getEventHandlerType(String[] args);

    @NotNull
    protected abstract RuntimeContext getRuntimeContext(String[] args);

    @NotNull
    protected SimpleLambdaRuntimeEventLoop getDefaultRuntimeEventLoop() {
        return new SimpleLambdaRuntimeEventLoop();
    }

    protected void handleInitializationError(Throwable e) {
        final Logger logger = LoggerFactory.getLogger(InputLambdaEntrypoint.class);
        logger.error(e.getMessage(), e);
        System.exit(1);
    }
}
