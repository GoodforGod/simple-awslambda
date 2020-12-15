package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author GoodforGod
 * @since 07.11.2020
 */
@Getter
@AllArgsConstructor
@Introspected
public class Pair<L, R> {

    private final L left;
    private final R right;

    public boolean isEmpty() {
        return left == null && right == null;
    }
}
