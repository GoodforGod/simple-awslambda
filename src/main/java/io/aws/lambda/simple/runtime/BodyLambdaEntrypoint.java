package io.aws.lambda.simple.runtime;

import io.aws.lambda.events.BodyEvent;
import io.aws.lambda.simple.runtime.handler.impl.BodyEventHandler;
import io.aws.lambda.simple.runtime.invoker.AwsEventInvoker;

/**
 * AWS Lambda Runtime main entry point for Lambda {@link BodyEvent}.
 *
 * @see BodyEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class BodyLambdaEntrypoint extends AbstractLambdaEntrypoint {

    public static void main(String[] args) {
        try {
            final AwsEventInvoker eventInvoker = getDefaultInvoker();
            eventInvoker.invoke(() -> getDefaultContext(args), BodyEventHandler.class);
        } catch (Exception e) {
            handleRuntimeInitializationError(e);
        }
    }
}
