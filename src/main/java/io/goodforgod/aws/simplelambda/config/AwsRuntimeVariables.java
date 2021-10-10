package io.goodforgod.aws.simplelambda.config;

/**
 * AWS Lambda Runtime Environment variables
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.5.2021
 */
public interface AwsRuntimeVariables {

    /**
     * AWS Lambda provides an HTTP API for custom runtimes to receive invocation
     * events from Lambda and send response data back within the Lambda execution
     * environment.
     */
    String AWS_LAMBDA_RUNTIME_API = "AWS_LAMBDA_RUNTIME_API";

    String INIT_ERROR = "/2018-06-01/runtime/init/error";
    String INVOCATION_URI = "/2018-06-01/runtime/invocation/";
    String INVOCATION_NEXT_URI = INVOCATION_URI + "next";

    /**
     * The request ID, which identifies the request that triggered the function
     * invocation. For example, 8476a536-e9f4-11e8-9739-2dfe598c3fcd.
     */
    String LAMBDA_RUNTIME_AWS_REQUEST_ID = "lambda-runtime-aws-request-id";

    /**
     * The date that the function times out in Unix time milliseconds. For example,
     * 1542409706888.
     */
    String LAMBDA_RUNTIME_DEADLINE_MS = "lambda-runtime-deadline-ms";

    /**
     * The ARN of the Lambda function, version, or alias that's specified in the
     * invocation. For example,
     * arn:aws:lambda:us-east-2:123456789012:function:custom-runtime.
     */
    String LAMBDA_RUNTIME_INVOKED_FUNCTION_ARN = "lambda-runtime-invoked-function-arn";

    /**
     * The AWS X-Ray tracing header. For example,
     * Root=1-5bef4de7-ad49b0e87f6ef6c87fc2e700;Parent=9a9197af755a6419;Sampled=1.
     */
    String LAMBDA_RUNTIME_TRACE_ID = "lambda-runtime-trace-id";
}
