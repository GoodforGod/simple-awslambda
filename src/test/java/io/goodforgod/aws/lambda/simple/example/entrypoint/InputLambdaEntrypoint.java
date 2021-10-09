package io.goodforgod.aws.lambda.simple.example.entrypoint;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.lambda.simple.AbstractLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.example.HelloWorldLambda;
import io.goodforgod.aws.lambda.simple.handler.impl.InputEventHandler;
import io.goodforgod.aws.lambda.simple.runtime.RuntimeContext;
import java.util.function.Function;
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
    protected @NotNull Function<RuntimeContext, RequestHandler> getRequestHandler() {
        return context -> new HelloWorldLambda();
    }

    @Override
    public @NotNull String getEventHandlerQualifier() {
        return InputEventHandler.QUALIFIER;
    }
}
