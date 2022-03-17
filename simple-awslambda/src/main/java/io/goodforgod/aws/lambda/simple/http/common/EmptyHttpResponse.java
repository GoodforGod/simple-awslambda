package io.goodforgod.aws.lambda.simple.http.common;

import io.goodforgod.http.common.HttpHeaders;
import io.goodforgod.http.common.HttpStatus;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 10.10.2021
 */
public final class EmptyHttpResponse extends AbstractHttpResponse {

    private static final EmptyHttpResponse EMPTY = new EmptyHttpResponse(HttpStatus.OK, HttpHeaders.empty());

    private EmptyHttpResponse(@NotNull HttpStatus status, @NotNull HttpHeaders headers) {
        super(status, headers);
    }

    public static EmptyHttpResponse of() {
        return EMPTY;
    }

    public static EmptyHttpResponse of(@NotNull HttpStatus status) {
        return new EmptyHttpResponse(status, HttpHeaders.empty());
    }

    public static EmptyHttpResponse of(@NotNull HttpStatus status,
                                       @NotNull HttpHeaders headers) {
        return new EmptyHttpResponse(status, headers);
    }

    @Override
    public @NotNull InputStream body() {
        return InputStream.nullInputStream();
    }

    @Override
    public @NotNull String bodyAsString(Charset charset) {
        return "";
    }
}
