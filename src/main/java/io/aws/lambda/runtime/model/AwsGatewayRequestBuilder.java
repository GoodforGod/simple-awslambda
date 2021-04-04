package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;

import java.util.List;
import java.util.Map;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 4.4.2021
 */
@TypeHint(value = { AwsGatewayRequestBuilder.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Introspected
public class AwsGatewayRequestBuilder {

    private AwsRequestContext context;
    private Boolean isBase64Encoded;
    private String version;
    private String rawPath;
    private String body;
    private String rawQueryString;
    private Map<String, String> headers;
    private Map<String, String> queryStringParameters;
    private Map<String, String> pathParameters;
    private Map<String, String> stageVariables;
    private List<String> cookies;
    private AwsGatewayProxyRequestContextBuilder requestContext;

    public AwsGatewayRequestBuilder setContext(AwsRequestContext context) {
        this.context = context;
        return this;
    }

    public AwsGatewayRequestBuilder setIsBase64Encoded(Boolean isBase64Encoded) {
        this.isBase64Encoded = isBase64Encoded;
        return this;
    }

    public AwsGatewayRequestBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public AwsGatewayRequestBuilder setRawPath(String rawPath) {
        this.rawPath = rawPath;
        return this;
    }

    public AwsGatewayRequestBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    public AwsGatewayRequestBuilder setRawQueryString(String rawQueryString) {
        this.rawQueryString = rawQueryString;
        return this;
    }

    public AwsGatewayRequestBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public AwsGatewayRequestBuilder setQueryStringParameters(Map<String, String> queryStringParameters) {
        this.queryStringParameters = queryStringParameters;
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

    public AwsGatewayRequestBuilder setCookies(List<String> cookies) {
        this.cookies = cookies;
        return this;
    }

    public AwsGatewayRequestBuilder setRequestContext(AwsGatewayProxyRequestContextBuilder requestContext) {
        this.requestContext = requestContext;
        return this;
    }

    public AwsGatewayRequest build() {
        return new AwsGatewayRequest(this.context, this.isBase64Encoded != null && this.isBase64Encoded,
                version, rawPath, body, rawQueryString, headers,
                queryStringParameters, pathParameters, stageVariables,
                cookies, (requestContext == null) ? null : requestContext.build());
    }
}
