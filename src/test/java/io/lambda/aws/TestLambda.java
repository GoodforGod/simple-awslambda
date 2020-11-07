package io.lambda.aws;

import javax.inject.Singleton;

/**
 * Description
 *
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
