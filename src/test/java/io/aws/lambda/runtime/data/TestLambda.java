package io.aws.lambda.runtime.data;

import io.aws.lambda.runtime.Lambda;
import io.aws.lambda.runtime.convert.Converter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 8.11.2020
 */
@Singleton
public class TestLambda implements Lambda<String, Card> {

    private final Converter converter;

    @Inject
    public TestLambda(Converter converter) {
        this.converter = converter;
    }

    @Override
    public @NotNull String handle(@NotNull Card card) {
        return "response for " + card.getName();
    }
}
