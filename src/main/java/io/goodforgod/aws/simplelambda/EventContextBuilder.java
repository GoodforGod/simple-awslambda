package io.goodforgod.aws.simplelambda;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.jetbrains.annotations.NotNull;

/**
 * Builder for {@link Context} that is used in tests
 *
 * @author Anton Kurako (GoodforGod)
 * @since 16.03.2022
 */
public final class EventContextBuilder {

    static final class DummyContext implements Context {

        private final String awsRequestId;
        private final String logGroupName;
        private final String logStreamName;
        private final String functionName;
        private final String functionVersion;
        private final String invokedFunctionArn;
        private final CognitoIdentity identity;
        private final ClientContext clientContext;
        private final int remainingTimeInMillis;
        private final int memoryLimitInMB;

        DummyContext(String awsRequestId,
                     String logGroupName,
                     String logStreamName,
                     String functionName,
                     String functionVersion,
                     String invokedFunctionArn,
                     CognitoIdentity identity,
                     ClientContext clientContext,
                     int remainingTimeInMillis,
                     int memoryLimitInMB) {
            this.awsRequestId = awsRequestId;
            this.logGroupName = logGroupName;
            this.logStreamName = logStreamName;
            this.functionName = functionName;
            this.functionVersion = functionVersion;
            this.invokedFunctionArn = invokedFunctionArn;
            this.identity = identity;
            this.clientContext = clientContext;
            this.remainingTimeInMillis = remainingTimeInMillis;
            this.memoryLimitInMB = memoryLimitInMB;
        }

        @Override
        public String getAwsRequestId() {
            return awsRequestId;
        }

        @Override
        public String getLogGroupName() {
            return logGroupName;
        }

        @Override
        public String getLogStreamName() {
            return logStreamName;
        }

        @Override
        public String getFunctionName() {
            return functionName;
        }

        @Override
        public String getFunctionVersion() {
            return functionVersion;
        }

        @Override
        public String getInvokedFunctionArn() {
            return invokedFunctionArn;
        }

        @Override
        public CognitoIdentity getIdentity() {
            return identity;
        }

        @Override
        public ClientContext getClientContext() {
            return clientContext;
        }

        @Override
        public int getRemainingTimeInMillis() {
            return remainingTimeInMillis;
        }

        @Override
        public int getMemoryLimitInMB() {
            return memoryLimitInMB;
        }

        @Override
        public LambdaLogger getLogger() {
            throw new UnsupportedOperationException("Please use SL4J for logging!");
        }
    }

    private EventContextBuilder() {}

    private String awsRequestId;
    private String logGroupName;
    private String logStreamName;
    private String functionName;
    private String functionVersion;
    private String invokedFunctionArn;
    private CognitoIdentity identity;
    private ClientContext clientContext;
    private int remainingTimeInMillis = -1;
    private int memoryLimitInMB = -1;

    @NotNull
    public static EventContextBuilder builder() {
        return new EventContextBuilder();
    }

    @NotNull
    public EventContextBuilder setAwsRequestId(String awsRequestId) {
        this.awsRequestId = awsRequestId;
        return this;
    }

    @NotNull
    public EventContextBuilder setLogGroupName(String logGroupName) {
        this.logGroupName = logGroupName;
        return this;
    }

    @NotNull
    public EventContextBuilder setLogStreamName(String logStreamName) {
        this.logStreamName = logStreamName;
        return this;
    }

    @NotNull
    public EventContextBuilder setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    @NotNull
    public EventContextBuilder setFunctionVersion(String functionVersion) {
        this.functionVersion = functionVersion;
        return this;
    }

    @NotNull
    public EventContextBuilder setInvokedFunctionArn(String invokedFunctionArn) {
        this.invokedFunctionArn = invokedFunctionArn;
        return this;
    }

    @NotNull
    public EventContextBuilder setIdentity(CognitoIdentity identity) {
        this.identity = identity;
        return this;
    }

    @NotNull
    public EventContextBuilder setClientContext(ClientContext clientContext) {
        this.clientContext = clientContext;
        return this;
    }

    @NotNull
    public EventContextBuilder setRemainingTimeInMillis(int remainingTimeInMillis) {
        this.remainingTimeInMillis = remainingTimeInMillis;
        return this;
    }

    @NotNull
    public EventContextBuilder setMemoryLimitInMB(int memoryLimitInMB) {
        this.memoryLimitInMB = memoryLimitInMB;
        return this;
    }

    @NotNull
    public Context build() {
        return new DummyContext(awsRequestId, logGroupName, logStreamName, functionName, functionVersion, invokedFunctionArn,
                identity, clientContext, remainingTimeInMillis, memoryLimitInMB);
    }
}
