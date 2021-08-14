package io.aws.lambda.simple.runtime.example;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 21.3.2021
 */
@TypeHint(value = { Request.class }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Introspected
public class Request {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
