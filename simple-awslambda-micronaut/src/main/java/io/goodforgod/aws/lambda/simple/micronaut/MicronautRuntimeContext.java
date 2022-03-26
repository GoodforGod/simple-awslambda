package io.goodforgod.aws.lambda.simple.micronaut;

import io.goodforgod.aws.lambda.simple.runtime.RuntimeContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.inject.qualifiers.Qualifiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * RuntimeContext implementation based on Micronaut DI {@link ApplicationContext}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.2.2021
 */
final class MicronautRuntimeContext implements RuntimeContext {

    private ApplicationContext context;

    @Override
    public void setupInRuntime() {
        if (this.context == null) {
            this.context = ApplicationContext.builder()
                    .banner(false)
                    .defaultEnvironments(Environment.FUNCTION, Environment.AMAZON_EC2, Environment.CLOUD)
                    .build()
                    .start();
        }
    }

    @Override
    public <T> T getBean(@NotNull Class<T> beanType) {
        try {
            return context.getBean(beanType);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public <T> T getBean(@NotNull Class<T> beanType, @Nullable String qualifier) {
        try {
            return (qualifier == null)
                    ? getBean(beanType)
                    : context.getBean(beanType, Qualifiers.byName(qualifier));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void close() {
        context.close();
    }
}
