package io.goodforgod.aws.lambda.simple.micronaut.bean;

import io.goodforgod.aws.lambda.simple.AwsRuntimeLoopCondition;
import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.04.2022
 */
@Introspected
@Singleton
class MicronautAwsRuntimeLoopCondition implements AwsRuntimeLoopCondition {

    @Override
    public boolean continueLoop() {
        return true;
    }
}
