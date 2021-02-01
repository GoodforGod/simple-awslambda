package io.aws.lambda.runtime;

import io.aws.lambda.runtime.context.impl.MicronautContext;
import io.aws.lambda.runtime.handler.impl.AwsGatewayEventHandler;
import io.aws.lambda.runtime.invoker.AwsRuntimeInvoker;
import io.aws.lambda.runtime.model.AwsGatewayRequest;
import io.aws.lambda.runtime.model.AwsGatewayResponse;
import io.micronaut.core.annotation.Introspected;

/**
 * AWS Lambda Runtime main entry point for Lambda API gateway request\response.
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
            new AwsRuntimeInvoker().invoke(MicronautContext.class, AwsGatewayEventHandler.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
