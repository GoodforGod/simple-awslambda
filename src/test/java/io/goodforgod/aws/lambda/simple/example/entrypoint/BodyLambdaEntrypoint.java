package io.goodforgod.aws.lambda.simple.example.entrypoint;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.aws.lambda.events.BodyEvent;
import io.goodforgod.aws.lambda.simple.AbstractLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.example.HelloWorldLambda;
import io.goodforgod.aws.lambda.simple.handler.EventHandler;
import io.goodforgod.aws.lambda.simple.handler.impl.BodyEventHandler;
import org.jetbrains.annotations.NotNull;

/**
 * AWS Lambda Entrypoint for Lambda {@link BodyEvent}.
 *
 * @see BodyEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class BodyLambdaEntrypoint extends AbstractLambdaEntrypoint {

    public static void main(String[] args) {
        new BodyLambdaEntrypoint().run(args);
    }

    @Override
    public @NotNull Class<? extends EventHandler> getEventHandlerType(String[] args) {
        return BodyEventHandler.class;
    }

    @Override
    protected @NotNull RequestHandler getRequestHandler(String[] args) {
        return new HelloWorldLambda();
    }
}
