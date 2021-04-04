package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.Introspected;

import java.util.Objects;

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

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    public boolean isEmpty() {
        return left == null && right == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "{\"left\":\"" + left + "\", \"right\":\"" + right + "\"}";
    }
}
