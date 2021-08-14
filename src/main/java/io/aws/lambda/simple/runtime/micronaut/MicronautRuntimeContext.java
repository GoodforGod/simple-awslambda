package io.aws.lambda.simple.runtime.micronaut;

import io.aws.lambda.simple.runtime.context.RuntimeContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import org.jetbrains.annotations.NotNull;

/**
 * RuntimeContext implementation based on Micronaut DI
 * {@link ApplicationContext}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.2.2021
 */
public class MicronautRuntimeContext implements RuntimeContext {

    private final ApplicationContext context;

    public MicronautRuntimeContext(String[] args) {
        this.context = ApplicationContext.builder()
                .banner(false)
                .defaultEnvironments(Environment.FUNCTION, Environment.AMAZON_EC2, Environment.CLOUD)
                .args(args)
                .packages("io.aws.lambda.simple.runtime")
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
