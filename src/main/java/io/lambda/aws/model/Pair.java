package io.lambda.aws.model;

import io.micronaut.core.annotation.Introspected;

/**
 * @author GoodforGod
 * @since 07.11.2020
 */
@Introspected
public class Pair<L, R> {

    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public boolean isEmpty() {
        return left == null && right == null;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}
