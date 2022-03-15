package io.goodforgod.aws.simplelambda.mock;

import io.goodforgod.aws.simplelambda.AbstractBodyLambdaEntrypoint;
import io.goodforgod.aws.simplelambda.handler.impl.BodyEventHandler;
import io.goodforgod.aws.simplelambda.runtime.SimpleRuntimeContext;
import java.util.function.Consumer;

/**
 * AWS Lambda Entrypoint for Lambda
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
    protected Consumer<SimpleRuntimeContext> setupInRuntime() {
        return context -> context.registerBean(new HelloWorldLambda());
    }
}
