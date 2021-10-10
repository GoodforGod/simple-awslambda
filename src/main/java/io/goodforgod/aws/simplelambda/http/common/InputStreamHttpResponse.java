package io.goodforgod.aws.simplelambda.http.common;

import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.utils.InputStreamUtils;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
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

    private InputStreamHttpResponse(int statusCode, InputStream body, Map<String, List<String>> headers) {
        super(statusCode, headers);
        this.body = body;
    }

    public static InputStreamHttpResponse of(int statusCode, InputStream body, Map<String, List<String>> headers) {
        return new InputStreamHttpResponse(statusCode, body, headers);
    }

    @Override
    public @NotNull InputStream body() {
        return body;
    }

    /**
     * @return body as {@link java.nio.charset.StandardCharsets#UTF_8} String
     */
    @Override
    public @NotNull String bodyAsString(Charset charset) {
        return InputStreamUtils.getStringFromInputStream(body(), charset);
    }
}
