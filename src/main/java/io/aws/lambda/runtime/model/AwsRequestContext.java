package io.aws.lambda.runtime.model;

import io.aws.lambda.runtime.utils.StringUtils;
import io.micronaut.core.annotation.Introspected;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 29.11.2020
 */
@Deprecated
@Introspected
public class AwsRequestContext {

    private final String requestId;
    private final String traceId;

    public AwsRequestContext(String requestId, String traceId) {
        this.requestId = requestId;
        this.traceId = traceId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getTraceId() {
        return traceId;
    }

    @Override
    public String toString() {
        return (StringUtils.isEmpty(traceId))
                ? "{\"AwsRequestID\":\"" + requestId + "\"}"
                : "{\"AwsRequestID\":\"" + requestId + "\", \"AwsTraceID\":\"" + traceId + "\"}";
    }
}
