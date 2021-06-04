package io.aws.lambda.runtime;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import io.aws.lambda.runtime.context.micronaut.MicronautContext;
import io.aws.lambda.runtime.invoker.AwsEventInvoker;
import io.aws.lambda.runtime.config.SimpleLoggerRefresher;
import io.aws.lambda.runtime.handler.impl.BodyEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS Lambda Runtime main entry point for Lambda {@link APIGatewayV2HTTPEvent}.
 *
 * @see BodyEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class AwsBodyLambdaRuntime {

    public static void main(String[] args) {
        SimpleLoggerRefresher.refresh();

        try {
            new AwsEventInvoker().invoke(() -> new MicronautContext(args), BodyEventHandler.class);
        } catch (Exception e) {
            final Logger logger = LoggerFactory.getLogger(AwsBodyLambdaRuntime.class);
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}
