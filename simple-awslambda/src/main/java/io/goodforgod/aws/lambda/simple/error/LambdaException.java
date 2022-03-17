package io.goodforgod.aws.lambda.simple.error;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 21.08.2021
 */
public class LambdaException extends RuntimeException {

    public LambdaException(String message) {
        super(message);
    }

    public LambdaException(Throwable cause) {
        super(cause);
    }

    public LambdaException(String message, Throwable cause) {
        super(message, cause);
    }
}
