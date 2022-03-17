package io.goodforgod.aws.lambda.simple.http.nativeclient;

import io.goodforgod.aws.lambda.simple.http.SimpleHttpResponse;
import io.goodforgod.aws.lambda.simple.http.common.AbstractHttpResponse;
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
public final class InputStreamNativeHttpResponse extends AbstractHttpResponse {

    private final HttpResponse<InputStream> httpResponse;

    private InputStreamNativeHttpResponse(@NotNull HttpResponse<InputStream> httpResponse) {
        super(HttpStatus.valueOf(httpResponse.statusCode()), HttpHeaders.ofMultiMap(httpResponse.headers().map()));
        this.httpResponse = httpResponse;
    }

    public static InputStreamNativeHttpResponse of(@NotNull HttpResponse<InputStream> httpResponse) {
        return new InputStreamNativeHttpResponse(httpResponse);
    }

    @Override
    public @NotNull InputStream body() {
        return httpResponse.body();
    }

    /**
     * @return body as {@link java.nio.charset.StandardCharsets#UTF_8} String
     */
    @Override
    public @NotNull String bodyAsString(Charset charset) {
        return InputStreamUtils.getStringFromInputStream(body(), charset);
    }
}
