package io.goodforgod.aws.lambda.simple.micronaut;

import io.goodforgod.aws.lambda.simple.AbstractLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.handler.impl.InputEventHandler;
import io.goodforgod.aws.lambda.simple.runtime.RuntimeContext;

/**
 * AWS Lambda Entrypoint for Lambda direct input event.
 *
 * @see InputEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class MicronautInputLambdaEntrypoint extends AbstractLambdaEntrypoint {

    private static final MicronautInputLambdaEntrypoint INSTANCE = new MicronautInputLambdaEntrypoint();

    public static void main(String[] args) {
        INSTANCE.run(args);
    }

    @Override
    public String getEventHandlerQualifier() {
        return InputEventHandler.QUALIFIER;
    }

    @Override
    protected RuntimeContext initializeRuntimeContext() {
        return new MicronautRuntimeContext();
    }
}
