package io.lambda.aws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GoodforGod
 * @since 29.10.2020
 */
@Introspected
public class AwsResponseEvent {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String MEDIA_TYPE_JSON = "application/json";

    private int statusCode = 200;
    private Map<String, String> headers = Map.of(CONTENT_TYPE, MEDIA_TYPE_JSON);
    private String body;
    private boolean isBase64Encoded = false;

    public AwsResponseEvent addHeader(String name, String value) {
        if (headers == null)
            this.headers = new HashMap<>(4);
        this.headers.put(name, value);
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public AwsResponseEvent setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public AwsResponseEvent setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public String getBody() {
        return body;
    }

    public AwsResponseEvent setBody(String body) {
        this.body = body;
        return this;
    }

    @JsonProperty("isBase64Encoded")
    public boolean isBase64Encoded() {
        return isBase64Encoded;
    }

    public AwsResponseEvent setBase64Encoded(boolean base64Encoded) {
        isBase64Encoded = base64Encoded;
        return this;
    }
}
