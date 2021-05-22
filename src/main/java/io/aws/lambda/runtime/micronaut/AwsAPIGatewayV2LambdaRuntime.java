package io.aws.lambda.runtime.micronaut;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import io.aws.lambda.runtime.AwsRuntimeInvoker;
import io.aws.lambda.runtime.config.SimpleLoggerRefresher;
import io.aws.lambda.runtime.handler.impl.APIGatewayV2EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS Lambda Runtime main entry point for Lambda API Gateway
 * {@link APIGatewayV2HTTPEvent}.
 *
 * @see APIGatewayV2HTTPEvent
 * @see APIGatewayV2EventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class AwsAPIGatewayV2LambdaRuntime {

    public static void main(String[] args) {
        SimpleLoggerRefresher.refresh();

        try {
            new AwsRuntimeInvoker().invoke(() -> new MicronautContext(args), APIGatewayV2EventHandler.class);
        } catch (Exception e) {
            final Logger logger = LoggerFactory.getLogger(AwsAPIGatewayV2LambdaRuntime.class);
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}
