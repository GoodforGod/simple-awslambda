package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.TypeHint;

import java.util.List;
import java.util.Map;

@TypeHint(value = { AwsGatewayRequestBuilder.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
public class AwsGatewayRequestBuilder {

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

    public AwsGatewayRequestBuilder setContext(AwsRequestContext context) {
        this.context = context;
        return this;
    }

    public AwsGatewayRequestBuilder setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public AwsGatewayRequestBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public AwsGatewayRequestBuilder setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public AwsGatewayRequestBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public AwsGatewayRequestBuilder setMultiValueHeaders(Map<String, List<String>> multiValueHeaders) {
        this.multiValueHeaders = multiValueHeaders;
        return this;
    }

    public AwsGatewayRequestBuilder setQueryStringParameters(Map<String, String> queryStringParameters) {
        this.queryStringParameters = queryStringParameters;
        return this;
    }

    public AwsGatewayRequestBuilder setMultiValueQueryStringParameters(Map<String, List<String>> multiValueQueryStringParameters) {
        this.multiValueQueryStringParameters = multiValueQueryStringParameters;
        return this;
    }

    public AwsGatewayRequestBuilder setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
        return this;
    }

    public AwsGatewayRequestBuilder setStageVariables(Map<String, String> stageVariables) {
        this.stageVariables = stageVariables;
        return this;
    }

    public AwsGatewayRequestBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    public AwsGatewayRequestBuilder setIsBase64Encoded(Boolean isBase64Encoded) {
        this.isBase64Encoded = isBase64Encoded;
        return this;
    }

    public AwsGatewayRequest build() {
        return new AwsGatewayRequest(context, resource, path, httpMethod, headers,
                multiValueHeaders, queryStringParameters, multiValueQueryStringParameters,
                pathParameters, stageVariables, body, isBase64Encoded);
    }
}
