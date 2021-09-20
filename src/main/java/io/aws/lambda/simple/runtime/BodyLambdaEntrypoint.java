package io.aws.lambda.simple.runtime;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.aws.lambda.events.BodyEvent;
import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.handler.impl.BodyEventHandler;
import io.aws.lambda.simple.runtime.micronaut.MicronautRuntimeContext;
import io.aws.lambda.simple.runtime.runtime.RuntimeContext;
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
    public @NotNull RuntimeContext getRuntimeContext(String[] args) {
        return new MicronautRuntimeContext(args);
    }

    @Override
    protected @NotNull RequestHandler getRequestHandler(String[] args) {
        throw new UnsupportedOperationException("Entrypoint doesn't support manual RequestHandler configuration!");
    }
}
