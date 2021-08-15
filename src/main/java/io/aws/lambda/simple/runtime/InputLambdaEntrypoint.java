package io.aws.lambda.simple.runtime;

import io.aws.lambda.simple.runtime.handler.impl.InputEventHandler;
import io.aws.lambda.simple.runtime.runtime.DefaultLambdaEventRuntime;

/**
 * AWS Lambda Entrypoint for Lambda direct input event.
 *
 * @see InputEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class InputLambdaEntrypoint extends AbstractLambdaEntrypoint {

    public static void main(String[] args) {
        try {
            final DefaultLambdaEventRuntime runtime = getDefaultRuntime();
            runtime.execute(() -> getDefaultRuntimeContext(args), InputEventHandler.class);
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }
}
