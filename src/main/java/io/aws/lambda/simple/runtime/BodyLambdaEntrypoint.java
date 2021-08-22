package io.aws.lambda.simple.runtime;

import io.aws.lambda.events.BodyEvent;
import io.aws.lambda.simple.runtime.handler.impl.BodyEventHandler;

/**
 * AWS Lambda Entrypoint for Lambda {@link BodyEvent}.
 *
 * @see BodyEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class BodyLambdaEntrypoint extends AbstractLambdaEntrypoint {

    public static void main(String[] args) {
        setupWithDefaultRuntimeContext(args, BodyEventHandler.class);
    }
}
