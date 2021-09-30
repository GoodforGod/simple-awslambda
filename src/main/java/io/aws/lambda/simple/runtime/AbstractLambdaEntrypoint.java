package io.aws.lambda.simple.runtime;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.handler.impl.InputEventHandler;
import io.aws.lambda.simple.runtime.runtime.RuntimeContext;
import io.aws.lambda.simple.runtime.runtime.SimpleLambdaRuntimeEventLoop;
import io.aws.lambda.simple.runtime.runtime.SimpleRuntimeContext;
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

    /**
     * @param args passed to entrypoint by AWS
     * @return {@link RequestHandler} implementation
     */
    @NotNull
    protected abstract RequestHandler getRequestHandler(String[] args);

    /**
     * @param args passed to entrypoint by AWS
     * @return Type of {@link EventHandler} implementation that will be responsible
     *         for handing event processing
     */
    @NotNull
    protected Class<? extends EventHandler> getEventHandlerType(String[] args) {
        return InputEventHandler.class;
    }

    /**
     * @param args passed to entrypoint by AWS
     * @return {@link RuntimeContext} implementation for Lambda
     */
    @NotNull
    protected RuntimeContext getRuntimeContext(String[] args) {
        return new SimpleRuntimeContext(getRequestHandler(args), getEventHandlerType(args));
    }

    @NotNull
    protected SimpleLambdaRuntimeEventLoop getDefaultRuntimeEventLoop() {
        return new SimpleLambdaRuntimeEventLoop();
    }

    protected void handleInitializationError(Throwable e) {
        final Logger logger = LoggerFactory.getLogger(AbstractLambdaEntrypoint.class);
        logger.error(e.getMessage(), e);
        System.exit(1);
    }
}
