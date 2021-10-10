package io.goodforgod.aws.simplelambda.http.okhttp;

import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.utils.InputStreamUtils;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @see okhttp3.Response
 * @since 10.10.2021
 */
public class OkHttpResponse implements SimpleHttpResponse {

    private final Map<String, List<String>> headers;
    private final InputStream body;
    private final int statusCode;
    private final Charset responseCharset;

    private OkHttpResponse(Response response) {
        this.statusCode = response.code();
        this.headers = response.headers().toMultimap();

        final ResponseBody responseBody = response.body();
        if (responseBody == null) {
            this.body = null;
            this.responseCharset = StandardCharsets.UTF_8;
        } else {
            this.body = responseBody.byteStream();
            final MediaType contentType = responseBody.contentType();
            this.responseCharset = contentType != null ? contentType.charset(StandardCharsets.UTF_8) : StandardCharsets.UTF_8;
        }
    }

    public static OkHttpResponse of(@NotNull Response response) {
        return new OkHttpResponse(response);
    }

    @Override
    public int statusCode() {
        return statusCode;
    }

    @Override
    public @NotNull InputStream body() {
        return (body == null) ? InputStream.nullInputStream() : body;
    }

    @Override
    public @NotNull String bodyAsString() {
        return (body == null) ? "" : InputStreamUtils.getStringFromInputStream(body, responseCharset);
    }

    @Override
    public @NotNull Map<String, List<String>> headersMultiValues() {
        return headers;
    }

    @Override
    public @NotNull Map<String, String> headers() {
        return headersMultiValues().entrySet().stream()
                .filter(e -> !e.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().iterator().next()));
    }

    @Override
    public Optional<String> headerFirst(@NotNull String name) {
        return Optional.ofNullable(headers.get(name))
                .filter(v -> !v.isEmpty())
                .map(v -> v.get(0));
    }
}
