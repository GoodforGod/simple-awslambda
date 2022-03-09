package io.goodforgod.aws.simplelambda;

import io.goodforgod.aws.simplelambda.handler.impl.InputEventHandler;
import io.goodforgod.aws.simplelambda.runtime.RuntimeContext;
import io.goodforgod.aws.simplelambda.runtime.SimpleRuntimeContext;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    protected abstract Consumer<SimpleRuntimeContext> setupInRuntime();

    /**
     * @return consumer to setup context in compile time
     */
    @NotNull
    protected Consumer<SimpleRuntimeContext> setupInCompileTime() {
        return context -> {};
    }

    @Override
    public @NotNull RuntimeContext getRuntimeContext() {
        return new SimpleRuntimeContext(setupInRuntime(), setupInCompileTime());
    }

    @Override
    protected @NotNull String getEventHandlerQualifier() {
        return InputEventHandler.QUALIFIER;
    }
}
