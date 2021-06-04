package io.aws.lambda.runtime;

import io.aws.lambda.runtime.context.micronaut.MicronautContext;
import io.aws.lambda.runtime.invoker.AwsEventInvoker;
import io.aws.lambda.runtime.config.SimpleLoggerRefresher;
import io.aws.lambda.runtime.handler.impl.RawEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS Lambda Runtime main entry point for direct lambda request\response.
 *
 * @see RawEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class AwsLambdaRuntime {

    public static void main(String[] args) {
        SimpleLoggerRefresher.refresh();

        try {
            new AwsEventInvoker().invoke(() -> new MicronautContext(args), RawEventHandler.class);
        } catch (Exception e) {
            final Logger logger = LoggerFactory.getLogger(AwsLambdaRuntime.class);
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}
