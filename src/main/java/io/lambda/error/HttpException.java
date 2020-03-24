package io.lambda.error;

/**
 * @author GoodforGod
 * @since 28.10.2020
 */
public class HttpException extends LambdaException {

    private final int code;

    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
