package io.goodforgod.aws.lambda.simple.http.nativeclient;

import io.goodforgod.aws.lambda.simple.http.SimpleHttpResponse;
import io.goodforgod.aws.lambda.simple.utils.InputStreamUtils;
import io.goodforgod.http.common.HttpHeaders;
import io.goodforgod.http.common.HttpStatus;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import org.jetbrains.annotations.NotNull;

/**
 * Native {@link HttpResponse} wrapper with body as {@link InputStream} for
 * {@link SimpleHttpResponse}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
final record InputStreamNativeHttpResponse(HttpStatus status,
                                           HttpHeaders headers,
                                           InputStream inputStream)
        implements SimpleHttpResponse {

    InputStreamNativeHttpResponse(@NotNull HttpResponse<InputStream> httpResponse) {
        this(HttpStatus.valueOf(httpResponse.statusCode()),
                HttpHeaders.ofMultiMap(httpResponse.headers().map()),
                httpResponse.body());
    }

    @Override
    public @NotNull InputStream body() {
        return inputStream;
    }

    /**
     * @return body as {@link java.nio.charset.StandardCharsets#UTF_8} String
     */
    @Override
    public @NotNull String bodyAsString(@NotNull Charset charset) {
        return InputStreamUtils.getStringFromInputStream(body(), charset);
    }
}
