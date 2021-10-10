package io.goodforgod.aws.simplelambda.http.nativeclient;

import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * Native {@link HttpResponse} wrapper without body for
 * {@link SimpleHttpResponse}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 20.08.2020
 */
public final class NativeVoidHttpResponse implements SimpleHttpResponse {

    private final HttpResponse<Void> httpResponse;

    public NativeVoidHttpResponse(@NotNull HttpResponse<Void> httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public int statusCode() {
        return httpResponse.statusCode();
    }

    @Override
    public @NotNull InputStream body() {
        return InputStream.nullInputStream();
    }

    @Override
    public @NotNull String bodyAsString() {
        return "";
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
