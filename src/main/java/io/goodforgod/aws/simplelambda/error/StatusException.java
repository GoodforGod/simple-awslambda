package io.goodforgod.aws.simplelambda.error;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 28.10.2020
 */
public class StatusException extends LambdaException {

    private final int statusCode;

    public StatusException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public StatusException(int statusCode, Throwable throwable) {
        super(throwable);
        this.statusCode = statusCode;
    }

    public StatusException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int statusCode() {
        return (statusCode <= 100 || statusCode >= 600) ? 500 : statusCode;
    }
}
