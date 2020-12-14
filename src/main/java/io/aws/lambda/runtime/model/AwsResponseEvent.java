package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.Introspected;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author GoodforGod
 * @since 29.10.2020
 */
@ToString
@Accessors(chain = true)
@Getter
@Setter
@Introspected
public class AwsResponseEvent {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String MEDIA_TYPE_JSON = "application/json";

    private int statusCode = 200;
    private Map<String, String> headers = Map.of(CONTENT_TYPE, MEDIA_TYPE_JSON);
    private String body;
    private boolean isBase64Encoded = false;

    public boolean isBase64Encoded() {
        return isBase64Encoded;
    }
}
