package io.goodforgod.aws.lambda.simple.http;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/**
 * Simple Http Client contract
 *
 * @author Anton Kurako (GoodforGod)
 * @since 27.10.2020
 */
public interface SimpleHttpClient extends AutoCloseable {

    /**
     * Executes request
     *
     * @param request to execute
     * @return http response
     */
    @NotNull
    SimpleHttpResponse execute(@NotNull SimpleHttpRequest request);

    /**
     * Executes request and doesn't retrieve response body
     *
     * @param request to execute
     * @return http response without response body
     */
    @NotNull
    SimpleHttpResponse executeAndForget(@NotNull SimpleHttpRequest request);

    /**
     * Executes request asynchronously
     *
     * @param request to execute
     * @return http response
     */
    @NotNull
    CompletableFuture<SimpleHttpResponse> executeAsync(@NotNull SimpleHttpRequest request);

    /**
     * Executes request asynchronously and doesn't retrieve response body
     *
     * @param request to execute
     * @return http response without response body
     */
    @NotNull
    CompletableFuture<SimpleHttpResponse> executeAndForgetAsync(@NotNull SimpleHttpRequest request);
}
