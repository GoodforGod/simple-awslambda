package io.aws.lambda.simple.runtime.error;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 28.10.2020
 */
public class StatusException extends RuntimeException {

    private final int statusCode;

    public StatusException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public StatusException(Throwable throwable, int statusCode) {
        super(throwable);
        this.statusCode = statusCode;
    }

    public StatusException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int statusCode() {
        return (statusCode <= 100 || statusCode >= 600) ? 500 : statusCode;
    }
}
