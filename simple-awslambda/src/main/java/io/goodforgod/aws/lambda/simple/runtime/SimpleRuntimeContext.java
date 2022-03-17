package io.goodforgod.aws.lambda.simple.runtime;

import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.aws.lambda.simple.convert.gson.GsonConverterFactory;
import io.goodforgod.aws.lambda.simple.handler.EventHandler;
import io.goodforgod.aws.lambda.simple.handler.impl.BodyEventHandler;
import io.goodforgod.aws.lambda.simple.handler.impl.InputEventHandler;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpClient;
import io.goodforgod.aws.lambda.simple.http.nativeclient.NativeHttpClient;
import java.util.*;
import java.util.function.Consumer;
import javax.inject.Named;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple Runtime without any DI that can be extended for more performant workload.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.08.2021
 */
public class SimpleRuntimeContext implements RuntimeContext {

    private record Qualifier(@NotNull String beanName, @Nullable String qualifierName) {

        private Qualifier(@NotNull Class<?> beanType) {
            this(beanType.getName(), null);
        }
    }

    private boolean isSetup = false;
    private final Map<Qualifier, List<Object>> beanMap = new HashMap<>(8);
    private final Consumer<SimpleRuntimeContext> runtimeContextConsumer;

    public SimpleRuntimeContext(@NotNull Consumer<SimpleRuntimeContext> setupInRuntimeConsumer) {
        final Converter converter = new GsonConverterFactory().build();
        final EventHandler inputEventHandler = new InputEventHandler(converter);
        final EventHandler bodyEventHandler = new BodyEventHandler(converter);
        registerBean(converter);
        registerBean(inputEventHandler);
        registerBean(bodyEventHandler);

        this.runtimeContextConsumer = setupInRuntimeConsumer;
    }

    public SimpleRuntimeContext(@NotNull Consumer<SimpleRuntimeContext> setupInRuntimeConsumer,
                                @NotNull Consumer<SimpleRuntimeContext> setupInCompileTimeConsumer) {
        this(setupInRuntimeConsumer);
        setupInCompileTimeConsumer.accept(this);
    }

    public void registerBean(@NotNull Object bean) {
        final Class<?> beanType = bean.getClass();
        final String qualifier = beanType.isAnnotationPresent(Named.class)
                ? beanType.getAnnotation(Named.class).value()
                : null;

        for (final Class<?> beanInterface : beanType.getInterfaces()) {
            registerBean(bean, beanInterface, qualifier);
        }

        registerBean(bean, beanType, qualifier);
    }

    private void registerBean(@NotNull Object bean, @NotNull Class<?> beanType, @Nullable String beanQualifier) {
        final Qualifier qualifier = new Qualifier(beanType.getName(), beanQualifier);
        final List<Object> beans = beanMap.computeIfAbsent(qualifier, k -> new ArrayList<>(3));
        beans.add(bean);

        if (beanQualifier != null) {
            final Qualifier qualifierUnnamed = new Qualifier(beanType);
            final List<Object> beansUnnamed = beanMap.computeIfAbsent(qualifierUnnamed, k -> new ArrayList<>(3));
            beansUnnamed.add(bean);
        }
    }

    @Override
    public void setupInRuntime() {
        if (!isSetup) {
            final SimpleHttpClient httpClient = new NativeHttpClient();
            registerBean(httpClient);
            runtimeContextConsumer.accept(this);
            isSetup = true;
        }
    }

    @Override
    public <T> T getBean(@NotNull Class<T> beanType) {
        return getBean(beanType, null);
    }

    /**
     * @param beanType  class or interface of bean to look for
     * @param qualifier of the bean type {@link Named}
     * @param <T>       type to cast bean to
     * @return bean or null if not found
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(@NotNull Class<T> beanType, @Nullable String qualifier) {
        final Qualifier beanQualifier = new Qualifier(beanType.getName(), qualifier);
        final List<Object> beans = beanMap.get(beanQualifier);
        if (beans != null) {
            return (T) beans.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void close() {
        // do nothing
    }
}
