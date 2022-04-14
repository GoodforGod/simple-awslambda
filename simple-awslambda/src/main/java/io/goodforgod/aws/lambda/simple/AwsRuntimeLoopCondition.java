package io.goodforgod.aws.lambda.simple;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 13.04.2022
 */
public interface AwsRuntimeLoopCondition {

    /**
     * @return true if continue runtime loop
     */
    boolean continueLoop();
}
