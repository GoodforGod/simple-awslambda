package io.aws.lambda.runtime.micronaut;

import io.aws.lambda.runtime.AwsRuntimeInvoker;
import io.aws.lambda.runtime.config.SimpleLoggerRefresher;
import io.aws.lambda.runtime.handler.impl.GatewayEventHandler;
import io.aws.lambda.runtime.model.gateway.AwsGatewayRequest;
import io.aws.lambda.runtime.model.gateway.AwsGatewayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS Lambda Runtime main entry point for Lambda API Gateway
 * {@link AwsGatewayRequest}.
 *
 * @see AwsGatewayResponse
 * @see AwsGatewayRequest
 * @see GatewayEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class AwsGatewayLambdaRuntime {

    public static void main(String[] args) {
        SimpleLoggerRefresher.refresh();

        try {
            new AwsRuntimeInvoker().invoke(() -> new MicronautContext(args), GatewayEventHandler.class);
        } catch (Exception e) {
            final Logger logger = LoggerFactory.getLogger(AwsGatewayLambdaRuntime.class);
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}
