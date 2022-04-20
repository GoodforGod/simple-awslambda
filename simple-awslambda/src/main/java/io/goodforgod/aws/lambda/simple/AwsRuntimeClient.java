package io.goodforgod.aws.lambda.simple;

import com.amazonaws.services.lambda.runtime.Context;
import io.goodforgod.aws.lambda.simple.handler.Event;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpBody;
import java.net.URI;
import org.jetbrains.annotations.NotNull;

/**
 * AWS Lambda Runtime client
 *
 * @author Anton Kurako (GoodforGod)
 * @since 11.04.2022
 */
public interface AwsRuntimeClient {

    /**
     * AWS Lambda provides an HTTP API for custom runtimes to receive invocation events from Lambda and
     * send response data back within the Lambda execution environment.
     *
     * @see io.goodforgod.aws.lambda.simple.config.AwsRuntimeVariables#AWS_LAMBDA_RUNTIME_API
     * @return URI where AWS Lambda runtime is located
     */
    @NotNull
    URI getAwsRuntimeApi();

    /**
     * @param runtimeEndpoint {@link io.goodforgod.aws.lambda.simple.config.AwsRuntimeVariables#AWS_LAMBDA_RUNTIME_API}
     * @return next AWSLambda event to be processed
     */
    @NotNull
    Event getNextEvent(@NotNull URI runtimeEndpoint);

    /**
     * @param runtimeEndpoint {@link io.goodforgod.aws.lambda.simple.config.AwsRuntimeVariables#AWS_LAMBDA_RUNTIME_API}
     * @param lambdaResult    to post for AWSLambda event
     * @param context         of the event
     */
    void reportInvocationSuccess(@NotNull URI runtimeEndpoint,
                                 @NotNull SimpleHttpBody lambdaResult,
                                 @NotNull Context context);

    /**
     * @param runtimeEndpoint {@link io.goodforgod.aws.lambda.simple.config.AwsRuntimeVariables#AWS_LAMBDA_RUNTIME_API}
     * @param throwable       to report for AWS
     * @param context         of the event
     */
    void reportInvocationError(@NotNull URI runtimeEndpoint,
                               @NotNull Throwable throwable,
                               @NotNull Context context);

    /**
     * @param runtimeEndpoint {@link io.goodforgod.aws.lambda.simple.config.AwsRuntimeVariables#AWS_LAMBDA_RUNTIME_API}
     * @param throwable       to report for AWS
     */
    void reportInitializationError(@NotNull URI runtimeEndpoint,
                                   @NotNull Throwable throwable);
}
