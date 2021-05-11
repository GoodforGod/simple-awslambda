package io.aws.lambda.runtime.error;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 28.10.2020
 */
public class HttpException extends LambdaException {

    private final int code;

    public HttpException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
