package io.goodforgod.aws.lambda.simple.testing;

import io.goodforgod.aws.lambda.simple.AwsRuntimeClient;
import io.goodforgod.aws.lambda.simple.AwsRuntimeLoopCondition;
import io.goodforgod.aws.lambda.simple.runtime.RuntimeContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Testing RuntimeContext wraps real RuntimeContext
 *
 * @author Anton Kurako (GoodforGod)
 * @since 13.04.2022
 */
final class TestingRuntimeContext implements RuntimeContext {

    private final TestingEntrypoint testingEntrypoint;
    private final TestingAwsRuntimeClient testingAwsRuntimeClient;
    private final AwsRuntimeLoopCondition loopCondition;

    TestingRuntimeContext(TestingEntrypoint testingEntrypoint) {
        this.testingEntrypoint = testingEntrypoint;
        this.loopCondition = new TestingAwsRuntimeLoopCondition();
        this.testingAwsRuntimeClient = new TestingAwsRuntimeClient();
    }

    TestingAwsRuntimeClient getTestingAwsRuntimeClient() {
        return testingAwsRuntimeClient;
    }

    @Override
    public void setupInRuntime() {
        this.testingEntrypoint.entrypoint.getRuntimeContext().setupInRuntime();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(@NotNull Class<T> beanType) {
        if (AwsRuntimeClient.class.isAssignableFrom(beanType)) {
            return (T) testingAwsRuntimeClient;
        } else if (AwsRuntimeLoopCondition.class.isAssignableFrom(beanType)) {
            return (T) loopCondition;
        } else {
            return getRealRuntimeContext().getBean(beanType);
        }
    }

    @Override
    public <T> T getBean(@NotNull Class<T> beanType, @Nullable String qualifier) {
        return getRealRuntimeContext().getBean(beanType, qualifier);
    }

    @Override
    public void close() throws Exception {
        // do nothing
    }

    void closeReal() throws Exception {
        getRealRuntimeContext().close();
    }

    private RuntimeContext getRealRuntimeContext() {
        return this.testingEntrypoint.entrypoint.getRuntimeContext();
    }
}
