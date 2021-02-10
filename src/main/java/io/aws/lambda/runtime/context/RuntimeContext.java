package io.aws.lambda.runtime.context;

/**
 * Runtime Context interface so whole code can be used with other DI framework
 * or even with some plain java written realisation
 *
 * RuntimeContext instance should have Zero Argument Constructor and should be
 * instantiated via such constructor
 *
 * @author Anton Kurako (GoodforGod)
 * @since 1.2.2021
 */
public interface RuntimeContext extends AutoCloseable {

    /**
     * @param beanType class or interface of bean to look for
     * @return return bean instance
     */
    <T> T getBean(Class<T> beanType);
}
