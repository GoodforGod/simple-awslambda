package io.goodforgod.aws.simplelambda.config;

/**
 * Lambda runtimes set several environment variables during initialization. Most of the environment
 * variables provide information about the function or runtime.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.5.2021
 */
public interface AwsContextVariables {

    /**
     * The handler location configured on the function.
     */
    String HANDLER = "_HANDLER";

    /**
     * The AWS Region where the Lambda function is executed.
     */
    String AWS_REGION = "AWS_REGION";

    /**
     * The runtime identifier, prefixed by AWS_Lambda_—for example, AWS_Lambda_java8.
     */
    String AWS_EXECUTION_ENV = "AWS_EXECUTION_ENV";

    /**
     * The name of the function.
     */
    String AWS_LAMBDA_FUNCTION_NAME = "AWS_LAMBDA_FUNCTION_NAME";

    /**
     * The amount of memory available to the function in MB.
     */
    String AWS_LAMBDA_FUNCTION_MEMORY_SIZE = "AWS_LAMBDA_FUNCTION_MEMORY_SIZE";

    /**
     * The version of the function being executed.
     */
    String AWS_LAMBDA_FUNCTION_VERSION = "AWS_LAMBDA_FUNCTION_VERSION";

    /**
     * The name of the Amazon CloudWatch Logs group for the function.
     */
    String AWS_LAMBDA_LOG_GROUP_NAME = "AWS_LAMBDA_LOG_GROUP_NAME";

    /**
     * The name of the Amazon CloudWatch stream for the function.
     */
    String AWS_LAMBDA_LOG_STREAM_NAME = "AWS_LAMBDA_LOG_STREAM_NAME";

    /**
     * Access key id obtained from the function's execution role.
     */
    String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";

    /**
     * secret access key obtained from the function's execution role.
     */
    String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";

    /**
     *
     * The access keys obtained from the function's execution role.
     */
    String AWS_SESSION_TOKEN = "AWS_SESSION_TOKEN";

    /**
     * (Custom runtime) The host and port of the runtime API.
     */
    String AWS_LAMBDA_RUNTIME_API = "AWS_LAMBDA_RUNTIME_API";

    /**
     * The path to your Lambda function code.
     */
    String LAMBDA_TASK_ROOT = "LAMBDA_TASK_ROOT";

    /**
     * The path to runtime libraries.
     */
    String LAMBDA_RUNTIME_DIR = "LAMBDA_RUNTIME_DIR";

    /**
     * The environment's time zone (UTC). The execution environment uses NTP to synchronize the system
     * clock.
     */
    String TZ = "TZ";
}
