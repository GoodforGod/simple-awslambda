package io.aws.lambda.runtime.model;

import io.aws.lambda.runtime.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 29.11.2020
 */
@Accessors(chain = true)
@Getter
@AllArgsConstructor
public class AwsRequestContext {

    private final String requestId;
    private final String traceId;

    @Override
    public String toString() {
        return (StringUtils.isEmpty(traceId))
                ? "[AwsRequestID=" + requestId + ']'
                : "[AwsRequestID=" + requestId + ", AwsTraceID=" + traceId + ']';
    }
}
