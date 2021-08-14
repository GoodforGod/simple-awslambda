package io.aws.lambda.simple.runtime;

import io.aws.lambda.simple.runtime.handler.impl.JsonEventHandler;
import io.aws.lambda.simple.runtime.invoker.AwsEventInvoker;

/**
 * AWS Lambda Runtime main entry point for direct lambda request\response.
 *
 * @see JsonEventHandler
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class JsonLambdaEntrypoint extends AbstractLambdaEntrypoint {

    public static void main(String[] args) {
        try {
            final AwsEventInvoker eventInvoker = getDefaultInvoker();
            eventInvoker.invoke(() -> getDefaultContext(args), JsonEventHandler.class);
        } catch (Exception e) {
            handleRuntimeInitializationError(e);
        }
    }
}
