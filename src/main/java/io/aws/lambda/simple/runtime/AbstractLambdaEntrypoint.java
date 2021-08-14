package io.aws.lambda.simple.runtime;

import io.aws.lambda.simple.runtime.context.RuntimeContext;
import io.aws.lambda.simple.runtime.micronaut.MicronautRuntimeContext;
import io.aws.lambda.simple.runtime.invoker.AwsEventInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Simple Lambda Runtime
 *
 * @author Anton Kurako (GoodforGod)
 * @since 14.08.2021
 */
public abstract class AbstractLambdaEntrypoint {

    protected AbstractLambdaEntrypoint() {}

    protected static AwsEventInvoker getDefaultInvoker() {
        return new AwsEventInvoker();
    }

    protected static RuntimeContext getDefaultContext(String[] args) {
        return new MicronautRuntimeContext(args);
    }

    protected static void handleRuntimeInitializationError(Throwable e) {
        final Logger logger = LoggerFactory.getLogger(JsonLambdaEntrypoint.class);
        logger.error(e.getMessage(), e);
        System.exit(1);
    }
}
