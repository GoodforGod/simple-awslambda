package io.aws.lambda.simple.runtime;

import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.handler.impl.InputEventHandler;
import io.aws.lambda.simple.runtime.micronaut.MicronautRuntimeContext;
import io.aws.lambda.simple.runtime.runtime.RuntimeContext;
import org.jetbrains.annotations.NotNull;

/**
 * AWS Lambda Entrypoint for Lambda direct input event.
 *
 * @see InputEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class InputLambdaEntrypoint extends AbstractLambdaEntrypoint {

    public static void main(String[] args) {
        new InputLambdaEntrypoint().run(args);
    }

    @Override
    public @NotNull Class<? extends EventHandler> getEventHandlerType(String[] args) {
        return InputEventHandler.class;
    }

    @Override
    public @NotNull RuntimeContext getRuntimeContext(String[] args) {
        return new MicronautRuntimeContext(args);
    }
}
