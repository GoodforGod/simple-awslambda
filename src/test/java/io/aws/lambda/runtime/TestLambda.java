package io.aws.lambda.runtime;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 8.11.2020
 */
@Singleton
public class TestLambda implements Lambda<String, String> {

    @Override
    public @NotNull String handle(@NotNull String request) {
        return "response for " + request;
    }
}
