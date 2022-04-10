package io.goodforgod.aws.lambda.simple.runtime;

import io.goodforgod.graalvm.hint.annotation.InitializationHint.InitPhase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Runtime Context interface so whole code can be used with any DI framework or even with some plain
 * java written realisation RuntimeContext.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 1.2.2021
 */
public interface RuntimeContext extends AutoCloseable {

    /**
     * Setup required for context to be initialized in runtime and not build time {@link InitPhase}
     */
    void setupInRuntime();

    /**
     * @param beanType class or interface of bean to look for
     * @param <T>      type of bean to instantiate
     * @return return bean instance
     */
    @Nullable
    <T> T getBean(@NotNull Class<T> beanType);

    /**
     * @param beanType  class or interface of bean to look for
     * @param qualifier of the bean type
     * @param <T>       type of bean to instantiate
     * @return return bean instance
     */
    @Nullable
    <T> T getBean(@NotNull Class<T> beanType, @Nullable String qualifier);
}
