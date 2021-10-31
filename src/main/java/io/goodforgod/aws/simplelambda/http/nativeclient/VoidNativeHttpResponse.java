package io.goodforgod.aws.simplelambda.http.nativeclient;

import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.http.common.AbstractHttpResponse;
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
public final class VoidNativeHttpResponse extends AbstractHttpResponse {

    private VoidNativeHttpResponse(@NotNull HttpResponse<Void> httpResponse) {
        super(httpResponse.statusCode(), httpResponse.headers().map());
    }

    public static VoidNativeHttpResponse of(@NotNull HttpResponse<Void> httpResponse) {
        return new VoidNativeHttpResponse(httpResponse);
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
