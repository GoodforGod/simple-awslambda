package io.lambda.aws.model;

import io.micronaut.core.annotation.Introspected;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author GoodforGod
 * @since 28.10.2020
 */
@Introspected
@Getter
@Setter
@NoArgsConstructor
public class AwsRequestEvent {

    private String path;
    private String httpMethod;
    private Map<String, String> headers;
    private Map<String, String> pathParameters;
    private String body;
    private Boolean isBase64Encoded;

}
