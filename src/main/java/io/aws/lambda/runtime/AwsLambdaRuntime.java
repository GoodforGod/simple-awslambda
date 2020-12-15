package io.aws.lambda.runtime;

import io.aws.lambda.runtime.handler.impl.AwsEventHandler;
import io.aws.lambda.runtime.invoker.AwsRuntimeInvoker;
import io.micronaut.core.annotation.Introspected;

/**
 * Runtime for direct Lambda processing
 *
 * @see AwsEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Introspected
public class AwsLambdaRuntime {

    public static void main(String[] args) {
        try {
            new AwsRuntimeInvoker().invoke(AwsEventHandler.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
