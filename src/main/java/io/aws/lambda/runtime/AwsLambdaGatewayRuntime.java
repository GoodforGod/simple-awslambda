package io.aws.lambda.runtime;

import io.aws.lambda.runtime.handler.impl.AwsGatewayEventHandler;
import io.aws.lambda.runtime.invoker.AwsRuntimeInvoker;
import io.aws.lambda.runtime.model.AwsGatewayRequest;
import io.aws.lambda.runtime.model.AwsGatewayResponse;
import io.micronaut.core.annotation.Introspected;

/**
 * Runtime for Gateway Events Lambda processing
 *
 * @see AwsGatewayResponse
 * @see AwsGatewayRequest
 * @see AwsGatewayEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Introspected
public class AwsLambdaGatewayRuntime {

    public static void main(String[] args) {
        try {
            new AwsRuntimeInvoker().invoke(AwsGatewayEventHandler.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
