package io.aws.lambda.simple.runtime;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import io.aws.lambda.simple.runtime.config.ContextVariables;
import io.aws.lambda.simple.runtime.config.RuntimeVariables;
import io.aws.lambda.simple.runtime.utils.StringUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of {@link Context} runtime context
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.5.2021
 */
public class LambdaContext implements Context {

    private final Map<String, String> headers;

    private LambdaContext(@NotNull Map<String, String> headers) {
        this.headers = Map.copyOf(headers);
    }

    public static LambdaContext ofRequestId(@NotNull String requestId) {
        return new LambdaContext(Map.of(RuntimeVariables.LAMBDA_RUNTIME_AWS_REQUEST_ID, requestId));
    }

    public static LambdaContext ofHeaders(@NotNull Map<String, String> headers) {
        return new LambdaContext(headers);
    }

    public static LambdaContext ofHeadersMulti(@NotNull Map<String, List<String>> headers) {
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
        final String millis = headers.get(RuntimeVariables.LAMBDA_RUNTIME_DEADLINE_MS);
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
        final String memory = getEnv(ContextVariables.AWS_LAMBDA_FUNCTION_MEMORY_SIZE);
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
        return System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "{\"AwsRequestID\":\"" + getAwsRequestId() + "\"}";
    }
}
