package io.aws.lambda.runtime;

import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 8.11.2020
 */
@Singleton
public class TestLambda implements Lambda<String, String> {

    @Override
    public String handle(String request) {
        return "response for " + request;
    }
}
