package io.goodforgod.aws.lambda.simple.testing;

import io.goodforgod.aws.lambda.simple.AwsRuntimeLoopCondition;

/**
 * Execute only first iteration runtime loop
 *
 * @author Anton Kurako (GoodforGod)
 * @since 13.04.2022
 */
final class TestingAwsRuntimeLoopCondition implements AwsRuntimeLoopCondition {

    private boolean firstLoopExecuted = false;

    @Override
    public boolean continueLoop() {
        if (firstLoopExecuted) {
            return false;
        }

        return firstLoopExecuted = true;
    }
}
