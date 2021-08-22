package io.aws.lambda.simple.runtime;

import io.aws.lambda.simple.runtime.handler.impl.InputEventHandler;

/**
 * AWS Lambda Entrypoint for Lambda direct input event.
 *
 * @see InputEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class InputLambdaEntrypoint extends AbstractLambdaEntrypoint {

    public static void main(String[] args) {
        setupWithDefaultRuntimeContext(args, InputEventHandler.class);
    }
}
