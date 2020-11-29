package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.Introspected;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 29.11.2020
 */
@ToString
@Accessors(chain = true)
@Getter
@Setter
@Introspected
public class AwsRequestContext {

    private String requestId;
    private String traceId;
}
