package io.aws.lambda.runtime.context.micronaut;

import io.aws.lambda.runtime.context.RuntimeContext;
import io.micronaut.context.ApplicationContext;
import org.jetbrains.annotations.NotNull;

/**
 * Micronaut context implementation {@link ApplicationContext}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.2.2021
 */
public class MicronautContext implements RuntimeContext {

    private final ApplicationContext context;

    public MicronautContext(String[] args) {
        this.context = ApplicationContext.builder()
                .args(args)
                .packages("io.aws.lambda.runtime")
                .start();
    }

    @Override
    public <T> T getBean(@NotNull Class<T> beanType) {
        return context.getBean(beanType);
    }

    @Override
    public void close() throws Exception {
        context.close();
    }
}
