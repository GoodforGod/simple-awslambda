package io.goodforgod.aws.lambda.simple.runtime;

import io.goodforgod.aws.lambda.simple.AwsRuntimeLoopCondition;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 13.04.2022
 */
final class DefaultAwsRuntimeLoopCondition implements AwsRuntimeLoopCondition {

    @Override
    public boolean continueLoop() {
        return !Thread.currentThread().isInterrupted();
    }
}
