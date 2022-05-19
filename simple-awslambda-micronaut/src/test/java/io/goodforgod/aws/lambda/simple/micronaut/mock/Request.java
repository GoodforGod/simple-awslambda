package io.goodforgod.aws.lambda.simple.micronaut.mock;

import io.goodforgod.graalvm.hint.annotation.ReflectionHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 21.3.2021
 */
@ReflectionHint
public record Request(String name) {}
