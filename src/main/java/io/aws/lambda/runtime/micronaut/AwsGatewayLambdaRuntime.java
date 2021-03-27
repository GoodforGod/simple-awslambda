package io.aws.lambda.runtime.micronaut;

import io.aws.lambda.runtime.handler.impl.AwsGatewayEventHandler;
import io.aws.lambda.runtime.invoker.AwsRuntimeInvoker;
import io.aws.lambda.runtime.model.AwsGatewayRequest;
import io.aws.lambda.runtime.model.AwsGatewayResponse;

/**
 * AWS Lambda Runtime main entry point for Lambda API gateway request\response.
 *
 * @see AwsGatewayResponse
 * @see AwsGatewayRequest
 * @see AwsGatewayEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class AwsGatewayLambdaRuntime {

    public static void main(String[] args) {
        try {
            new AwsRuntimeInvoker().invoke(() -> new MicronautContext(args), AwsGatewayEventHandler.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
