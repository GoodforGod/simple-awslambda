package io.aws.lambda.runtime.error;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 1.2.2021
 */
public class ContextException extends RuntimeException {

    public ContextException(String message) {
        super(message);
    }
}
