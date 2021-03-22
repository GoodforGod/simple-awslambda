package io.aws.lambda.runtime.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @author GoodforGod
 * @since 28.10.2020
 */
@Getter
@Setter
@ToString
public class AwsGatewayRequest {

    private AwsRequestContext context;

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
}
