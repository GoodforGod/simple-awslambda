package io.goodforgod.aws.lambda.simple.testing;

import io.goodforgod.aws.lambda.simple.AbstractLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.AwsRuntimeClient;

/**
 * Testing AbstractLambdaEntrypoint wraps real entrypoint
 *
 * @author Anton Kurako (GoodforGod)
 * @since 13.04.2022
 */
final class TestingEntrypoint extends AbstractLambdaEntrypoint {

    final AbstractLambdaEntrypoint entrypoint;

    TestingEntrypoint(AbstractLambdaEntrypoint entrypoint) {
        super();
        this.entrypoint = entrypoint;
    }

    void test(String[] args) {
        run(args);
    }

    @Override
    public String getEventHandlerQualifier() {
        return entrypoint.getEventHandlerQualifier();
    }

    @Override
    public TestingRuntimeContext initializeRuntimeContext() {
        return new TestingRuntimeContext(this);
    }

    @Override
    protected void handleInitializationError(Throwable e) {
        final AwsRuntimeClient awsRuntimeClient = entrypoint.getRuntimeContext().getBean(AwsRuntimeClient.class);
        if (awsRuntimeClient instanceof TestingAwsRuntimeClient) {
            ((TestingAwsRuntimeClient) awsRuntimeClient).setThrowable(e);
        } else {
            throw new TestingAwsLambdaException(e);
        }
    }
}
