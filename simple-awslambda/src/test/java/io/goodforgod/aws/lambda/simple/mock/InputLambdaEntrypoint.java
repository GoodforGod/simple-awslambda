package io.goodforgod.aws.lambda.simple.mock;

import io.goodforgod.aws.lambda.simple.AbstractInputLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.handler.impl.InputEventHandler;
import io.goodforgod.aws.lambda.simple.runtime.SimpleRuntimeContext;
import java.util.function.Consumer;

/**
 * AWS Lambda Entrypoint for Lambda direct input event.
 *
 * @see InputEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class InputLambdaEntrypoint extends AbstractInputLambdaEntrypoint {

    public static void main(String[] args) {
        new InputLambdaEntrypoint().run(args);
    }

    @Override
    protected Consumer<SimpleRuntimeContext> setupInRuntime() {
        return context -> context.registerBean(new HelloWorldLambda());
    }
}
