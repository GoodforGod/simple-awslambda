package io.aws.lambda.simple.runtime.example.entrypoint;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.aws.lambda.simple.runtime.AbstractLambdaEntrypoint;
import io.aws.lambda.simple.runtime.example.HelloWorldLambda;
import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.handler.impl.InputEventHandler;
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
    protected @NotNull RequestHandler getRequestHandler(String[] args) {
        return new HelloWorldLambda();
    }
}
