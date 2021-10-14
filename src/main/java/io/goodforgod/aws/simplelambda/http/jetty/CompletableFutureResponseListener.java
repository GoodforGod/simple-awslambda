package io.goodforgod.aws.simplelambda.http.jetty;

import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.http.common.InputStreamHttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.http.HttpField;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 11.10.2021
 */
final class CompletableFutureResponseListener extends BufferingResponseListener {

    private final CompletableFuture<SimpleHttpResponse> future = new CompletableFuture<>();

    @Override
    public void onComplete(Result result) {
        final Map<String, List<String>> headers = result.getResponse().getHeaders().stream()
                .collect(Collectors.toMap(HttpField::getName, v -> List.of(v.getValue())));

        final InputStreamHttpResponse response = InputStreamHttpResponse.of(result.getResponse().getStatus(), getContentAsInputStream(),
                headers);
        future.complete(response);
    }

    @Override
    public void onFailure(Response response, Throwable failure) {
        future.completeExceptionally(failure);
    }

    public CompletableFuture<SimpleHttpResponse> getResult() {
        return future;
    }
}
