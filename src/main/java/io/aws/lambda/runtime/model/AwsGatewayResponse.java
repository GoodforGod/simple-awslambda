package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.TypeHint;

import java.util.Map;

/**
 * @author GoodforGod
 * @since 29.10.2020
 */
@TypeHint(value = { AwsGatewayResponse.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
public class AwsGatewayResponse {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String MEDIA_TYPE_JSON = "application/json";

    private int statusCode = 200;
    private Map<String, String> headers = Map.of(CONTENT_TYPE, MEDIA_TYPE_JSON);
    private String body;
    private boolean isBase64Encoded = false;

    public int getStatusCode() {
        return statusCode;
    }

    public AwsGatewayResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public AwsGatewayResponse setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public String getBody() {
        return body;
    }

    public AwsGatewayResponse setBody(String body) {
        this.body = body;
        return this;
    }

    public AwsGatewayResponse setBase64Encoded(boolean base64Encoded) {
        isBase64Encoded = base64Encoded;
        return this;
    }

    public boolean isBase64Encoded() {
        return isBase64Encoded;
    }

    @Override
    public String toString() {
        return "AwsGatewayResponse{" +
                "statusCode=" + statusCode +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", isBase64Encoded=" + isBase64Encoded +
                '}';
    }
}
