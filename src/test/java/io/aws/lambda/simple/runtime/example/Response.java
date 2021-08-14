package io.aws.lambda.simple.runtime.example;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 21.3.2021
 */
@TypeHint(value = { Response.class }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Introspected
public class Response {

    private final String id;
    private final String message;

    public Response(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
