package io.goodforgod.aws.simplelambda.http.common;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 10.10.2021
 */
public final class EmptyHttpResponse extends AbstractHttpResponse {

    private static final EmptyHttpResponse EMPTY = new EmptyHttpResponse(200, Collections.emptyMap());

    private EmptyHttpResponse(int statusCode, Map<String, List<String>> headers) {
        super(statusCode, headers);
    }

    public static EmptyHttpResponse of() {
        return EMPTY;
    }

    public static EmptyHttpResponse of(int statusCode) {
        return new EmptyHttpResponse(statusCode, Collections.emptyMap());
    }

    public static EmptyHttpResponse of(int statusCode, Map<String, List<String>> headers) {
        return new EmptyHttpResponse(statusCode, headers);
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
