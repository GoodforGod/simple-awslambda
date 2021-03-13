package io.aws.lambda.runtime.error;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public class ConvertException extends LambdaException {

    public ConvertException(String message) {
        super(message);
    }
}
