package io.aws.lambda.runtime.micronaut;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import io.aws.lambda.runtime.AwsRuntimeInvoker;
import io.aws.lambda.runtime.config.SimpleLoggerRefresher;
import io.aws.lambda.runtime.handler.impl.APIGatewayProxyEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS Lambda Runtime main entry point for Lambda API Gateway
 * {@link APIGatewayProxyRequestEvent}.
 *
 * @see APIGatewayProxyRequestEvent
 * @see APIGatewayProxyEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class AwsAPIGatewayProxyLambdaRuntime {

    public static void main(String[] args) {
        SimpleLoggerRefresher.refresh();

        try {
            new AwsRuntimeInvoker().invoke(() -> new MicronautContext(args), APIGatewayProxyEventHandler.class);
        } catch (Exception e) {
            final Logger logger = LoggerFactory.getLogger(AwsAPIGatewayProxyLambdaRuntime.class);
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}
