package io.goodforgod.aws.simplelambda.http.common;

import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 10.10.2021
 */
abstract class AbstractHttpResponse implements SimpleHttpResponse {

    private final int statusCode;
    private final Map<String, List<String>> headers;

    AbstractHttpResponse(int statusCode, Map<String, List<String>> headers) {
        this.statusCode = statusCode;
        this.headers = (headers == null)
                ? Collections.emptyMap()
                : Map.copyOf(headers.entrySet().stream()
                        .filter(e -> e.getKey() != null && !e.getKey().isEmpty())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Override
    public int statusCode() {
        return statusCode;
    }

    @Override
    public @NotNull Map<String, List<String>> headersMultiValues() {
        return headers;
    }

    @Override
    public @NotNull Map<String, String> headers() {
        if (headers.isEmpty()) {
            return Collections.emptyMap();
        }

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
