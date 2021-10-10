package io.goodforgod.aws.simplelambda.example.entrypoint;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.lambda.events.BodyEvent;
import io.goodforgod.aws.simplelambda.AbstractLambdaEntrypoint;
import io.goodforgod.aws.simplelambda.example.HelloWorldLambda;
import io.goodforgod.aws.simplelambda.handler.impl.BodyEventHandler;
import io.goodforgod.aws.simplelambda.runtime.RuntimeContext;
import java.util.function.Function;
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
    protected @NotNull Function<RuntimeContext, RequestHandler> getRequestHandler() {
        return context -> new HelloWorldLambda();
    }

    @Override
    public @NotNull String getEventHandlerQualifier() {
        return BodyEventHandler.QUALIFIER;
    }
}
