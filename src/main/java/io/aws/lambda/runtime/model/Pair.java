package io.aws.lambda.runtime.model;

/**
 * @author GoodforGod
 * @since 07.11.2020
 */
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
}
