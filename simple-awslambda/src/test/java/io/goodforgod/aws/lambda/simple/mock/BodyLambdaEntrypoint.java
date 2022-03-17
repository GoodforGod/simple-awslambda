package io.goodforgod.aws.lambda.simple.mock;

import io.goodforgod.aws.lambda.simple.AbstractBodyLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.handler.impl.BodyEventHandler;
import io.goodforgod.aws.lambda.simple.runtime.SimpleRuntimeContext;
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
