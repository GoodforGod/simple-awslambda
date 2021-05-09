package io.aws.lambda.runtime.micronaut;

import io.aws.lambda.runtime.AwsRuntimeInvoker;
import io.aws.lambda.runtime.config.SimpleLoggerRefresher;
import io.aws.lambda.runtime.handler.impl.DirectEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS Lambda Runtime main entry point for direct lambda request\response.
 *
 * @see DirectEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class AwsLambdaRuntime {

    public static void main(String[] args) {
        SimpleLoggerRefresher.refresh();

        try {
            new AwsRuntimeInvoker().invoke(() -> new MicronautContext(args), DirectEventHandler.class);
        } catch (Exception e) {
            final Logger logger = LoggerFactory.getLogger(AwsLambdaRuntime.class);
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}
