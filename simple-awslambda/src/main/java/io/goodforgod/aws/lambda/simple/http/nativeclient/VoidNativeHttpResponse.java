package io.goodforgod.aws.lambda.simple.http.nativeclient;

import io.goodforgod.aws.lambda.simple.http.SimpleHttpResponse;
import io.goodforgod.http.common.HttpHeaders;
import io.goodforgod.http.common.HttpStatus;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import org.jetbrains.annotations.NotNull;

/**
 * Native {@link HttpResponse} wrapper without body for {@link SimpleHttpResponse}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 20.08.2020
 */
final record VoidNativeHttpResponse(HttpStatus status, HttpHeaders headers) implements SimpleHttpResponse {

    VoidNativeHttpResponse(@NotNull HttpResponse<Void> httpResponse) {
        this(HttpStatus.valueOf(httpResponse.statusCode()), HttpHeaders.ofMultiMap(httpResponse.headers().map()));
    }

    @Override
    public @NotNull InputStream body() {
        return InputStream.nullInputStream();
    }

    @Override
    public String bodyAsString(@NotNull Charset charset) {
        return null;
    }
}
