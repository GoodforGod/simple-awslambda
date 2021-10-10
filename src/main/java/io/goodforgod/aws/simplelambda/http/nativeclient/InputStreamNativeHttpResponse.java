package io.goodforgod.aws.simplelambda.http.nativeclient;

import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.utils.InputStreamUtils;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * Native {@link HttpResponse} wrapper with body as {@link InputStream} for
 * {@link SimpleHttpResponse}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public final class InputStreamNativeHttpResponse implements SimpleHttpResponse {

    private final HttpResponse<InputStream> httpResponse;

    public InputStreamNativeHttpResponse(@NotNull HttpResponse<InputStream> httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public int statusCode() {
        return httpResponse.statusCode();
    }

    @Override
    public @NotNull InputStream body() {
        return httpResponse.body();
    }

    /**
     * @return body as {@link java.nio.charset.StandardCharsets#UTF_8} String
     */
    @Override
    public @NotNull String bodyAsString() {
        return InputStreamUtils.getStringFromInputStreamUTF8(body());
    }

    @Override
    public @NotNull Map<String, List<String>> headersMultiValues() {
        return httpResponse.headers().map();
    }

    @Override
    public @NotNull Map<String, String> headers() {
        return headersMultiValues().entrySet().stream()
                .filter(e -> !e.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().iterator().next()));
    }

    @Override
    public Optional<String> headerFirst(@NotNull String name) {
        return httpResponse.headers().firstValue(name);
    }

    @Override
    public String toString() {
        return httpResponse.toString();
    }
}
