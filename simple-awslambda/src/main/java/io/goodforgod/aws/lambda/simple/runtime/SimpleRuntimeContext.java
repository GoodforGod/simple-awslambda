package io.goodforgod.aws.lambda.simple.runtime;

import io.goodforgod.aws.lambda.simple.AwsRuntimeClient;
import io.goodforgod.aws.lambda.simple.AwsRuntimeLoopCondition;
import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.aws.lambda.simple.convert.gson.GsonConverterFactory;
import io.goodforgod.aws.lambda.simple.handler.EventHandler;
import io.goodforgod.aws.lambda.simple.handler.impl.BodyEventHandler;
import io.goodforgod.aws.lambda.simple.handler.impl.InputEventHandler;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpClient;
import io.goodforgod.aws.lambda.simple.http.nativeclient.NativeHttpClient;
import io.goodforgod.aws.lambda.simple.http.nativeclient.SimpleAwsRuntimeClient;
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

    private record BeanContainer(Class<?> beanType, Object bean) {

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            BeanContainer that = (BeanContainer) o;
            return Objects.equals(beanType, that.beanType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(beanType);
        }
    }

    private boolean isSetup = false;
    private final Map<Qualifier, Collection<BeanContainer>> beanMap = new HashMap<>();
    private final Consumer<SimpleRuntimeContext> runtimeContextConsumer;

    public SimpleRuntimeContext(@NotNull Consumer<SimpleRuntimeContext> setupInRuntime,
                                @NotNull Consumer<SimpleRuntimeContext> setupInCompileTime) {
        this.runtimeContextConsumer = getRuntimeConsumer().andThen(setupInRuntime);
        getCompileTimeConsumer().andThen(setupInCompileTime).accept(this);
    }

    protected Consumer<SimpleRuntimeContext> getRuntimeConsumer() {
        return context -> {
            final SimpleHttpClient httpClient = new NativeHttpClient();
            registerBean(httpClient);
            final AwsRuntimeClient awsRuntimeClient = new SimpleAwsRuntimeClient(httpClient);
            registerBean(awsRuntimeClient);
        };
    }

    protected Consumer<SimpleRuntimeContext> getCompileTimeConsumer() {
        return context -> {
            final Converter converter = new GsonConverterFactory().build();
            registerBean(converter);
            final EventHandler inputEventHandler = new InputEventHandler(converter);
            registerBean(inputEventHandler);
            final EventHandler bodyEventHandler = new BodyEventHandler(converter);
            registerBean(bodyEventHandler);
            final AwsRuntimeLoopCondition loopCondition = new DefaultAwsRuntimeLoopCondition();
            registerBean(loopCondition);
        };
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

    private void registerBean(@NotNull Object bean,
                              @NotNull Class<?> beanType,
                              @Nullable String beanQualifier) {
        final Qualifier qualifier = new Qualifier(beanType.getName(), beanQualifier);
        final BeanContainer beanContainer = new BeanContainer(beanType, bean);
        registerBean(qualifier, beanContainer);

        if (beanQualifier != null) {
            final Qualifier qualifierUnnamed = new Qualifier(beanType);
            registerBean(qualifierUnnamed, beanContainer);
        }
    }

    private void registerBean(@NotNull Qualifier qualifier, @NotNull BeanContainer beanContainer) {
        final Collection<BeanContainer> beansUnnamed = beanMap.computeIfAbsent(qualifier, k -> new HashSet<>(5));
        beansUnnamed.add(beanContainer);
    }

    @Override
    public void setupInRuntime() {
        if (!isSetup) {
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
        final Collection<BeanContainer> beans = beanMap.get(beanQualifier);
        if (beans != null) {
            return (T) beans.iterator().next().bean();
        } else {
            return null;
        }
    }

    @Override
    public void close() {
        // do nothing
    }
}
