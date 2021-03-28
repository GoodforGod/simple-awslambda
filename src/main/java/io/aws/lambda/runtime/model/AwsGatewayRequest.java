package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.TypeHint;

import java.util.List;
import java.util.Map;

/**
 * @author GoodforGod
 * @since 28.10.2020
 */
@TypeHint(value = { AwsGatewayRequest.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
public class AwsGatewayRequest {

    private final AwsRequestContext context;

    private final String resource;
    private final String path;
    private final String httpMethod;
    private final String body;
    private final Boolean isBase64Encoded;

    private final Map<String, String> headers;
    private final Map<String, List<String>> multiValueHeaders;

    private final Map<String, String> queryStringParameters;
    private final Map<String, List<String>> multiValueQueryStringParameters;

    private final Map<String, String> pathParameters;
    private final Map<String, String> stageVariables;

    protected AwsGatewayRequest(AwsRequestContext context, String resource, String path, String httpMethod,
                                Map<String, String> headers,
                                Map<String, List<String>> multiValueHeaders,
                                Map<String, String> queryStringParameters,
                                Map<String, List<String>> multiValueQueryStringParameters,
                                Map<String, String> pathParameters,
                                Map<String, String> stageVariables,
                                String body, Boolean isBase64Encoded) {
        this.context = context;
        this.resource = resource;
        this.path = path;
        this.httpMethod = httpMethod;
        this.headers = headers;
        this.multiValueHeaders = multiValueHeaders;
        this.queryStringParameters = queryStringParameters;
        this.multiValueQueryStringParameters = multiValueQueryStringParameters;
        this.pathParameters = pathParameters;
        this.stageVariables = stageVariables;
        this.body = body;
        this.isBase64Encoded = isBase64Encoded;
    }

    public static AwsGatewayRequestBuilder builder() {
        return new AwsGatewayRequestBuilder();
    }

    public AwsRequestContext getContext() {
        return context;
    }

    public String getResource() {
        return resource;
    }

    public String getPath() {
        return path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, List<String>> getMultiValueHeaders() {
        return multiValueHeaders;
    }

    public Map<String, String> getQueryStringParameters() {
        return queryStringParameters;
    }

    public Map<String, List<String>> getMultiValueQueryStringParameters() {
        return multiValueQueryStringParameters;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters;
    }

    public Map<String, String> getStageVariables() {
        return stageVariables;
    }

    public String getBody() {
        return body;
    }

    public Boolean getBase64Encoded() {
        return isBase64Encoded;
    }

    @Override
    public String toString() {
        return "AwsGatewayRequest{" +
                "context=" + context +
                ", resource='" + resource + '\'' +
                ", path='" + path + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", headers=" + headers +
                ", multiValueHeaders=" + multiValueHeaders +
                ", queryStringParameters=" + queryStringParameters +
                ", multiValueQueryStringParameters=" + multiValueQueryStringParameters +
                ", pathParameters=" + pathParameters +
                ", stageVariables=" + stageVariables +
                ", body='" + body + '\'' +
                ", isBase64Encoded=" + isBase64Encoded +
                '}';
    }
}
