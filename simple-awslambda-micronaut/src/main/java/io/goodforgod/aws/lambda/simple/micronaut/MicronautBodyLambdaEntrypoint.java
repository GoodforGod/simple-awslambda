package io.goodforgod.aws.lambda.simple.micronaut;

import io.goodforgod.aws.lambda.events.BodyEvent;
import io.goodforgod.aws.lambda.simple.AbstractLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.handler.impl.BodyEventHandler;
import io.goodforgod.aws.lambda.simple.runtime.RuntimeContext;

/**
 * AWS Lambda Entrypoint for Lambda {@link BodyEvent}.
 *
 * @see BodyEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class MicronautBodyLambdaEntrypoint extends AbstractLambdaEntrypoint {

    private static final MicronautBodyLambdaEntrypoint INSTANCE = new MicronautBodyLambdaEntrypoint();

    public static void main(String[] args) {
        INSTANCE.run(args);
    }

    @Override
    public String getEventHandlerQualifier() {
        return BodyEventHandler.QUALIFIER;
    }

    @Override
    protected RuntimeContext initializeRuntimeContext() {
        return new MicronautRuntimeContext();
    }
}
