package io.aws.lambda.runtime;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import io.aws.lambda.runtime.config.ContextVariables;
import io.aws.lambda.runtime.config.RuntimeVariables;
import io.aws.lambda.runtime.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 22.5.2021
 */
public class LambdaContext implements Context {

    private final Map<String, String> headers;

    private LambdaContext(Map<String, String> headers) {
        this.headers = headers;
    }

    public static LambdaContext ofHeaders(@NotNull Map<String, String> headers) {
        return new LambdaContext(headers);
    }

    public static LambdaContext ofMultiHeaders(@NotNull Map<String, List<String>> headers) {
        final Map<String, String> singleValueHeaders = headers.entrySet().stream()
                .filter(e -> !e.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().iterator().next()));

        return new LambdaContext(singleValueHeaders);
    }

    @Override
    public String getAwsRequestId() {
        return headers.get(RuntimeVariables.LAMBDA_RUNTIME_AWS_REQUEST_ID);
    }

    @Override
    public String getLogGroupName() {
        return getEnv(ContextVariables.AWS_LAMBDA_LOG_GROUP_NAME);
    }

    @Override
    public String getLogStreamName() {
        return getEnv(ContextVariables.AWS_LAMBDA_LOG_STREAM_NAME);
    }

    @Override
    public String getFunctionName() {
        return getEnv(ContextVariables.AWS_LAMBDA_FUNCTION_NAME);
    }

    @Override
    public String getFunctionVersion() {
        return getEnv(ContextVariables.AWS_LAMBDA_FUNCTION_VERSION);
    }

    @Override
    public String getInvokedFunctionArn() {
        return headers.get(RuntimeVariables.LAMBDA_RUNTIME_INVOKED_FUNCTION_ARN);
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
        String millis = headers.get(RuntimeVariables.LAMBDA_RUNTIME_DEADLINE_MS);
        if (StringUtils.isEmpty(millis))
            return 0;

        try {
            long deadlineepoch = Long.parseLong(millis);
            long currentepoch = currentTime();
            long remainingTime = deadlineepoch - currentepoch;
            return (int) remainingTime;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int getMemoryLimitInMB() {
        String memory = getEnv(ContextVariables.AWS_LAMBDA_FUNCTION_MEMORY_SIZE);
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
        throw new UnsupportedOperationException("Please use Log4j for logging!");
    }

    /**
     * @param name the name of the environment variable
     * @return the string value of the variable, or {@code null} if the variable is
     *         not defined
     */
    protected String getEnv(String name) {
        return System.getenv(name);
    }

    /**
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT until
     *         the current date
     */
    protected long currentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public String toString() {
        return "{\"AwsRequestID\":\"" + getAwsRequestId() + "\"}";
    }
}
