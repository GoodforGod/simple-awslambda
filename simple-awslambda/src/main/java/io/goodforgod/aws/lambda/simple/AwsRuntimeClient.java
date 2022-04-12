package io.goodforgod.aws.lambda.simple;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;

import com.amazonaws.services.lambda.runtime.Context;
import org.jetbrains.annotations.NotNull;

/**
 * AWS Lambda Runtime client
 *
 * @author Anton Kurako (GoodforGod)
 * @since 11.04.2022
 */
public interface AwsRuntimeClient {

    /**
     * @param runtimeEndpoint {@link io.goodforgod.aws.lambda.simple.config.AwsRuntimeVariables#AWS_LAMBDA_RUNTIME_API}
     * @return next AWSLambda event to be processed
     */
    @NotNull
    Event getNextEvent(@NotNull URI runtimeEndpoint);

    /**
     * @param runtimeEndpoint {@link io.goodforgod.aws.lambda.simple.config.AwsRuntimeVariables#AWS_LAMBDA_RUNTIME_API}
     * @param response to post for AWSLambda event
     * @param context of the event
     */
    void reportInvocationSuccess(@NotNull URI runtimeEndpoint,
                                 @NotNull Flow.Publisher<ByteBuffer> response,
                                 @NotNull Context context);

    /**
     * @param runtimeEndpoint {@link io.goodforgod.aws.lambda.simple.config.AwsRuntimeVariables#AWS_LAMBDA_RUNTIME_API}
     * @param throwable to report for AWS
     */
    void reportInitializationError(@NotNull URI runtimeEndpoint,
                                   @NotNull Throwable throwable);

    /**
     * @param runtimeEndpoint {@link io.goodforgod.aws.lambda.simple.config.AwsRuntimeVariables#AWS_LAMBDA_RUNTIME_API}
     * @param throwable to report for AWS
     * @param context of the event
     */
    void reportInvocationError(@NotNull URI runtimeEndpoint,
                               @NotNull Throwable throwable,
                               @NotNull Context context);
}
