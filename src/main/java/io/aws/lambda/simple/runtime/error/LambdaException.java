package io.aws.lambda.simple.runtime.error;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class LambdaException extends StatusException {

    public LambdaException(String message) {
        super(message, 500);
    }

    public LambdaException(String message, Throwable cause) {
        super(message, cause, 500);
    }
}
