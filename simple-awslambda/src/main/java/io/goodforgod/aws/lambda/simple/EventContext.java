package io.goodforgod.aws.lambda.simple;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import io.goodforgod.aws.lambda.simple.config.AwsContextVariables;
import io.goodforgod.aws.lambda.simple.config.AwsRuntimeVariables;
import io.goodforgod.aws.lambda.simple.config.SimpleLambdaContextVariables;
import io.goodforgod.aws.lambda.simple.utils.StringUtils;
import io.goodforgod.http.common.HttpHeaders;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of {@link Context} runtime context
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.5.2021
 */
record EventContext(HttpHeaders headers) implements Context {

    @NotNull
    public static EventContext ofHeaders(@NotNull HttpHeaders headers) {
        return new EventContext(headers);
    }

    @Override
    public String getAwsRequestId() {
        return headers.findFirst(AwsRuntimeVariables.LAMBDA_RUNTIME_AWS_REQUEST_ID).orElse(null);
    }

    @Override
    public String getLogGroupName() {
        return getEnv(AwsContextVariables.AWS_LAMBDA_LOG_GROUP_NAME);
    }

    @Override
    public String getLogStreamName() {
        return getEnv(AwsContextVariables.AWS_LAMBDA_LOG_STREAM_NAME);
    }

    public String getHandlerName() {
        return getEnv(AwsContextVariables.HANDLER);
    }

    @Override
    public String getFunctionName() {
        return getEnv(AwsContextVariables.AWS_LAMBDA_FUNCTION_NAME);
    }

    @Override
    public String getFunctionVersion() {
        return getEnv(AwsContextVariables.AWS_LAMBDA_FUNCTION_VERSION);
    }

    @Override
    public String getInvokedFunctionArn() {
        return headers.findFirst(AwsRuntimeVariables.LAMBDA_RUNTIME_INVOKED_FUNCTION_ARN).orElse(null);
    }

    @Override
    public CognitoIdentity getIdentity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientContext getClientContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRemainingTimeInMillis() {
        final String millis = headers.findFirst(AwsRuntimeVariables.LAMBDA_RUNTIME_DEADLINE_MS).orElse(null);
        if (StringUtils.isEmpty(millis))
            return 0;

        try {
            final long deadlineEpoch = Long.parseLong(millis);
            final long currentEpoch = currentTime();
            final long remainingTime = deadlineEpoch - currentEpoch;
            return (int) remainingTime;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int getMemoryLimitInMB() {
        final String memory = getEnv(AwsContextVariables.AWS_LAMBDA_FUNCTION_MEMORY_SIZE);
        if (StringUtils.isEmpty(memory))
            return 0;

        try {
            return Integer.parseInt(memory);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public LambdaLogger getLogger() {
        throw new UnsupportedOperationException("Please use SL4J for logging!");
    }

    /**
     * @param name the name of the environment variable
     * @return the string value of the variable, or {@code null} if the variable is not defined
     */
    private String getEnv(String name) {
        return System.getenv(name);
    }

    /**
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT until the current date
     */
    private long currentTime() {
        return System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "[AwsRequestID:" + getAwsRequestId() + ","
                + formatOrEmpty("requestHandler: ", getEnv(SimpleLambdaContextVariables.REQUEST_HANDLER), ",")
                + formatOrEmpty("eventHandler: ", getEnv(SimpleLambdaContextVariables.EVENT_HANDLER), ",")
                + formatOrEmpty("getLogStreamName: ", getLogStreamName(), ",")
                + formatOrEmpty("getLogGroupName: ", getLogGroupName(), ",")
                + formatOrEmpty("getLogStreamName: ", getLogStreamName(), ",")
                + formatOrEmpty("getFunctionName: ", getFunctionName(), ",")
                + formatOrEmpty("getFunctionVersion: ", getFunctionVersion(), ",")
                + formatOrEmpty("getInvokedFunctionArn: ", getInvokedFunctionArn(), ",")
                + formatOrEmpty("getRemainingTimeInMillis: ", getRemainingTimeInMillis(), ",")
                + formatOrEmpty("getMemoryLimitInMB: ", getMemoryLimitInMB(), ",")
                + "currentTime: " + currentTime() + "]";
    }

    private String formatOrEmpty(String prefix, Object value, String suffix) {
        return (value == null)
                ? ""
                : prefix + value + suffix;
    }
}
