package io.goodforgod.aws.simplelambda;

import io.goodforgod.aws.simplelambda.handler.impl.InputEventHandler;
import io.goodforgod.aws.simplelambda.runtime.RuntimeContext;
import io.goodforgod.aws.simplelambda.runtime.SimpleRuntimeContext;
import java.util.function.Consumer;

/**
 * Abstract Simple Lambda Entrypoint for {@link InputEventHandler}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 09.03.2022
 */
public abstract class AbstractInputLambdaEntrypoint extends AbstractLambdaEntrypoint {

    /**
     * @return consumer to setup context in runtime
     */
    protected Consumer<SimpleRuntimeContext> setupInRuntime() {
        return context -> {};
    }

    /**
     * @return consumer to setup context in compile time
     */
    protected Consumer<SimpleRuntimeContext> setupInCompileTime() {
        return context -> {};
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return new SimpleRuntimeContext(setupInRuntime(), setupInCompileTime());
    }

    @Override
    protected String getEventHandlerQualifier() {
        return InputEventHandler.QUALIFIER;
    }
}
