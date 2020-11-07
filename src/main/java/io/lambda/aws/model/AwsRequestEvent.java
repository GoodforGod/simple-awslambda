package io.lambda.aws.model;

import io.micronaut.core.annotation.Introspected;

import java.util.Map;

/**
 * @author GoodforGod
 * @since 28.10.2020
 */
@Introspected
public class AwsRequestEvent {

    private String path;
    private String httpMethod;
    private Map<String, String> headers;
    private Map<String, String> pathParameters;
    private String body;
    private Boolean isBase64Encoded;
    private String requestId;

    public String getPath() {
        return path;
    }

    public AwsRequestEvent setPath(String path) {
        this.path = path;
        return this;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public AwsRequestEvent setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public AwsRequestEvent setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters;
    }

    public AwsRequestEvent setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
        return this;
    }

    public String getBody() {
        return body;
    }

    public AwsRequestEvent setBody(String body) {
        this.body = body;
        return this;
    }

    public Boolean getBase64Encoded() {
        return isBase64Encoded;
    }

    public AwsRequestEvent setBase64Encoded(Boolean base64Encoded) {
        isBase64Encoded = base64Encoded;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public AwsRequestEvent setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
}
