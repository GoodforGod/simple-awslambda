package io.goodforgod.aws.simplelambda.http.common;

import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.http.common.HttpHeaders;
import io.goodforgod.http.common.HttpStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 10.10.2021
 */
public abstract class AbstractHttpResponse implements SimpleHttpResponse {

    private final HttpStatus status;
    private final HttpHeaders headers;

    protected AbstractHttpResponse(@NotNull HttpStatus status,
                                   @NotNull HttpHeaders headers) {
        this.status = status;
        this.headers = headers;
    }

    @Override
    public @NotNull HttpStatus status() {
        return status;
    }

    public @NotNull HttpHeaders headers() {
        return headers;
    }

    @Override
    public String toString() {
        return headers.toString();
    }
}
