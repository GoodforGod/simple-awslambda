package io.lambda.aws.model;

import io.micronaut.core.annotation.Introspected;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author GoodforGod
 * @since 28.10.2020
 */
@ToString
@Accessors(chain = true)
@Getter
@Setter
@Introspected
public class AwsRequestEvent {

    private String requestId;
    private String resource;
    private String path;
    private String httpMethod;
    private Map<String, String> headers;
    private Map<String, List<String>> multiValueHeaders;
    private Map<String, String> queryStringParameters;
    private Map<String, List<String>> multiValueQueryStringParameters;
    private Map<String, String> pathParameters;
    private Map<String, String> stageVariables;
    private String body;
    private Boolean isBase64Encoded;

    public AwsRequestEvent setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
}
