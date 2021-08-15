package io.aws.lambda.simple.runtime;

import io.aws.lambda.simple.runtime.runtime.RuntimeContext;
import io.aws.lambda.simple.runtime.micronaut.MicronautRuntimeContext;
import io.aws.lambda.simple.runtime.runtime.DefaultLambdaEventRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Simple Lambda Entrypoint
 *
 * @author Anton Kurako (GoodforGod)
 * @since 14.08.2021
 */
public abstract class AbstractLambdaEntrypoint {

    protected AbstractLambdaEntrypoint() {}

    protected static DefaultLambdaEventRuntime getDefaultRuntime() {
        return new DefaultLambdaEventRuntime();
    }

    protected static RuntimeContext getDefaultRuntimeContext(String[] args) {
        return new MicronautRuntimeContext(args);
    }

    protected static void handleInitializationError(Throwable e) {
        final Logger logger = LoggerFactory.getLogger(InputLambdaEntrypoint.class);
        logger.error(e.getMessage(), e);
        System.exit(1);
    }
}
