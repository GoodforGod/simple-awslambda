package io.aws.lambda.simple.runtime.error;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 28.10.2020
 */
public class StatusException extends RuntimeException {

    private final int httpCode;

    public StatusException(String message, int httpCode) {
        super(message);
        this.httpCode = httpCode;
    }

    public StatusException(String message, Throwable cause, int httpCode) {
        super(message, cause);
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return (httpCode <= 100 || httpCode >= 600) ? 500 : httpCode;
    }
}
