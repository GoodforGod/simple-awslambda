package io.aws.lambda.runtime.context.impl;

import io.aws.lambda.runtime.context.RuntimeContext;
import io.micronaut.context.ApplicationContext;

/**
 * Micronaut context implementation
 * {@link io.micronaut.context.ApplicationContext}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 1.2.2021
 */
public class MicronautContext implements RuntimeContext {

    private final ApplicationContext context;

    public MicronautContext() {
        this.context = ApplicationContext.build().start();
    }

    @Override
    public <T> T getBean(Class<T> beanType) {
        return context.getBean(beanType);
    }

    @Override
    public void close() throws Exception {
        context.close();
    }
}
