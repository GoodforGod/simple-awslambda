package io.lambda.error;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class LambdaException extends RuntimeException {

    public LambdaException(String message) {
        super(message);
    }

    public LambdaException(String message, Throwable cause) {
        super(message, cause);
    }

    public LambdaException(Throwable cause) {
        super(cause);
    }
}
