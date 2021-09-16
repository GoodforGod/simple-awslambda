package io.aws.lambda.simple.runtime;

import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.micronaut.MicronautRuntimeContext;
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

    protected AbstractLambdaEntrypoint() {}

    /**
     * @param args             to setup default {@link RuntimeContext}
     * @param eventHandlerType to handle lambda events in runtime
     */
    protected static void setupWithDefaultRuntimeContext(String[] args,
                                                         @NotNull Class<? extends EventHandler> eventHandlerType) {
        setup(eventHandlerType, () -> getDefaultRuntimeContext(args));
    }

    /**
     * @param eventHandlerType       to handle lambda events in runtime
     * @param runtimeContextSupplier provides Lambda {@link RuntimeContext}
     */
    protected static void setup(@NotNull Class<? extends EventHandler> eventHandlerType,
                                @NotNull Supplier<RuntimeContext> runtimeContextSupplier) {
        try {
            final SimpleLambdaRuntimeEventLoop eventLoop = getDefaultRuntimeEventLoop();
            eventLoop.execute(runtimeContextSupplier, eventHandlerType);
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }

    protected static SimpleLambdaRuntimeEventLoop getDefaultRuntimeEventLoop() {
        return new SimpleLambdaRuntimeEventLoop();
    }

    protected static RuntimeContext getDefaultRuntimeContext(String[] args) {
        return new MicronautRuntimeContext(args);
    }

    protected static void handleInitializationError(Throwable e) {
        final Logger logger = LoggerFactory.getLogger(InputLambdaEntrypoint.class);
        logger.error(e.getMessage(), e);
        System.exit(1);
    }
}
