package io.goodforgod.aws.simplelambda.mock.entrypoint;

import io.goodforgod.aws.lambda.events.BodyEvent;
import io.goodforgod.aws.simplelambda.AbstractBodyLambdaEntrypoint;
import io.goodforgod.aws.simplelambda.handler.impl.BodyEventHandler;
import io.goodforgod.aws.simplelambda.mock.HelloWorldLambda;
import io.goodforgod.aws.simplelambda.runtime.SimpleRuntimeContext;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * AWS Lambda Entrypoint for Lambda {@link BodyEvent}.
 *
 * @see BodyEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class BodyLambdaEntrypoint extends AbstractBodyLambdaEntrypoint {

    public static void main(String[] args) {
        new BodyLambdaEntrypoint().run(args);
    }

    @Override
    protected @NotNull Consumer<SimpleRuntimeContext> setupInRuntime() {
        return context -> context.registerBean(new HelloWorldLambda());
    }
}
