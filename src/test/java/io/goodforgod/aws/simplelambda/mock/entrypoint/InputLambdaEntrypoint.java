package io.goodforgod.aws.simplelambda.mock.entrypoint;

import io.goodforgod.aws.simplelambda.AbstractInputLambdaEntrypoint;
import io.goodforgod.aws.simplelambda.handler.impl.InputEventHandler;
import io.goodforgod.aws.simplelambda.mock.HelloWorldLambda;
import io.goodforgod.aws.simplelambda.runtime.SimpleRuntimeContext;
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
