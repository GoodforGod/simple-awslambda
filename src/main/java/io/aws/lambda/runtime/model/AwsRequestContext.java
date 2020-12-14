package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 29.11.2020
 */
@ToString
@Accessors(chain = true)
@Getter
@Introspected
@AllArgsConstructor
public class AwsRequestContext {

    private final String requestId;
    private final String traceId;
}
