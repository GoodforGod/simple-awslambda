package io.goodforgod.aws.simplelambda.http.nativeclient;

import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.http.common.AbstractHttpResponse;
import io.goodforgod.aws.simplelambda.utils.InputStreamUtils;
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
        super(httpResponse.statusCode(), httpResponse.headers().map());
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
