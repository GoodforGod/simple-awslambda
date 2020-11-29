package io.aws.lambda.runtime.error;

/**
 * @author GoodforGod
 * @since 28.10.2020
 */
public class HttpException extends LambdaException {

    private int code = 500;

    public HttpException(String message) {
        super(message);
    }

    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }

    public HttpException code(int code) {
        this.code = code;
        return this;
    }

    public int getCode() {
        return code;
    }
}
