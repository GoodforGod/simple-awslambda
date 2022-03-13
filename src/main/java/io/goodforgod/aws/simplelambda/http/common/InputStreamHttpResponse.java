package io.goodforgod.aws.simplelambda.http.common;

import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.utils.InputStreamUtils;
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
public final class InputStreamHttpResponse extends AbstractHttpResponse {

    private final InputStream body;

    private InputStreamHttpResponse(@NotNull HttpStatus status,
                                    InputStream body,
                                    @NotNull HttpHeaders headers) {
        super(status, headers);
        this.body = body;
    }

    public static InputStreamHttpResponse of(@NotNull HttpStatus status,
                                             InputStream body,
                                             @NotNull HttpHeaders headers) {
        return new InputStreamHttpResponse(status, body, headers);
    }

    @Override
    public @NotNull InputStream body() {
        return body;
    }

    /**
     * @return body as {@link java.nio.charset.StandardCharsets#UTF_8} String
     */
    @Override
    public @NotNull String bodyAsString(@NotNull Charset charset) {
        return InputStreamUtils.getStringFromInputStream(body(), charset);
    }
}
