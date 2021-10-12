package io.goodforgod.aws.simplelambda.http;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/**
 * Simple Http Client
 *
 * @author Anton Kurako (GoodforGod)
 * @since 27.10.2020
 */
public interface SimpleHttpClient extends AutoCloseable {

    Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    /**
     * GET
     *
     * @param uri to execute http request against
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse get(@NotNull URI uri) {
        return execute(HttpMethod.GET, uri, SimpleHttpRequest.empty(), DEFAULT_TIMEOUT);
    }

    /**
     * GET
     *
     * @param uri     to execute http request against
     * @param timeout for http response
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse get(@NotNull URI uri,
                                   @NotNull Duration timeout) {
        return execute(HttpMethod.GET, uri, SimpleHttpRequest.empty(), timeout);
    }

    /**
     * GET
     *
     * @param uri     to execute http request against
     * @param headers to execute request with
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse get(@NotNull URI uri,
                                   @NotNull Map<String, String> headers) {
        return execute(HttpMethod.GET, uri, SimpleHttpRequest.ofHeaders(headers), DEFAULT_TIMEOUT);
    }

    /**
     * GET
     *
     * @param uri     to execute http request against
     * @param headers to execute request with
     * @param timeout for http response
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse get(@NotNull URI uri,
                                   @NotNull Map<String, String> headers,
                                   @NotNull Duration timeout) {
        return execute(HttpMethod.GET, uri, SimpleHttpRequest.ofHeaders(headers), timeout);
    }

    /**
     * POST
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse post(@NotNull URI uri,
                                    @NotNull SimpleHttpRequest request) {
        return execute(HttpMethod.POST, uri, request, DEFAULT_TIMEOUT);
    }

    /**
     * POST
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @param timeout for http response
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse post(@NotNull URI uri,
                                    @NotNull SimpleHttpRequest request,
                                    @NotNull Duration timeout) {
        return execute(HttpMethod.POST, uri, request, timeout);
    }

    /**
     * POST
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @return http response without response body
     */
    @NotNull
    default SimpleHttpResponse postAndForget(@NotNull URI uri,
                                             @NotNull SimpleHttpRequest request) {
        return executeAndForget(HttpMethod.POST, uri, request, DEFAULT_TIMEOUT);
    }

    /**
     * POST
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @param timeout for http response
     * @return http response without response body
     */
    @NotNull
    default SimpleHttpResponse postAndForget(@NotNull URI uri,
                                             @NotNull SimpleHttpRequest request,
                                             @NotNull Duration timeout) {
        return executeAndForget(HttpMethod.POST, uri, request, timeout);
    }

    /**
     * PUT
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse put(@NotNull URI uri,
                                   @NotNull SimpleHttpRequest request) {
        return execute(HttpMethod.PUT, uri, request, DEFAULT_TIMEOUT);
    }

    /**
     * PUT
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @param timeout for http response
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse put(@NotNull URI uri,
                                   @NotNull SimpleHttpRequest request,
                                   @NotNull Duration timeout) {
        return execute(HttpMethod.PUT, uri, request, timeout);
    }

    /**
     * PUT
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @return http response without response body
     */
    @NotNull
    default SimpleHttpResponse putAndForget(@NotNull URI uri,
                                            @NotNull SimpleHttpRequest request) {
        return executeAndForget(HttpMethod.PUT, uri, request, DEFAULT_TIMEOUT);
    }

    /**
     * PUT
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @param timeout for http response
     * @return http response without response body
     */
    @NotNull
    default SimpleHttpResponse putAndForget(@NotNull URI uri,
                                            @NotNull SimpleHttpRequest request,
                                            @NotNull Duration timeout) {
        return executeAndForget(HttpMethod.PUT, uri, request, timeout);
    }

    /**
     * PATCH
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse patch(@NotNull URI uri,
                                     @NotNull SimpleHttpRequest request) {
        return execute(HttpMethod.PATCH, uri, request, DEFAULT_TIMEOUT);
    }

    /**
     * PATCH
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @param timeout for http response
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse patch(@NotNull URI uri,
                                     @NotNull SimpleHttpRequest request,
                                     @NotNull Duration timeout) {
        return execute(HttpMethod.PATCH, uri, request, timeout);
    }

    /**
     * PATCH
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @return http response without response body
     */
    @NotNull
    default SimpleHttpResponse patchAndForget(@NotNull URI uri,
                                              @NotNull SimpleHttpRequest request) {
        return executeAndForget(HttpMethod.PATCH, uri, request, DEFAULT_TIMEOUT);
    }

    /**
     * PATCH
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @param timeout for http response
     * @return http response without response body
     */
    @NotNull
    default SimpleHttpResponse patchAndForget(@NotNull URI uri,
                                              @NotNull SimpleHttpRequest request,
                                              @NotNull Duration timeout) {
        return executeAndForget(HttpMethod.PATCH, uri, request, timeout);
    }

    /**
     * DELETE
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse delete(@NotNull URI uri,
                                      @NotNull SimpleHttpRequest request) {
        return execute(HttpMethod.DELETE, uri, request, DEFAULT_TIMEOUT);
    }

    /**
     * DELETE
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @param timeout for http response
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse delete(@NotNull URI uri,
                                      @NotNull SimpleHttpRequest request,
                                      @NotNull Duration timeout) {
        return execute(HttpMethod.DELETE, uri, request, timeout);

    }

    /**
     * DELETE
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @return http response without response body
     */
    @NotNull
    default SimpleHttpResponse deleteAndForget(@NotNull URI uri,
                                               @NotNull SimpleHttpRequest request) {
        return executeAndForget(HttpMethod.DELETE, uri, request, DEFAULT_TIMEOUT);
    }

    /**
     * DELETE
     *
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @param timeout for http response
     * @return http response without response body
     */
    @NotNull
    default SimpleHttpResponse deleteAndForget(@NotNull URI uri,
                                               @NotNull SimpleHttpRequest request,
                                               @NotNull Duration timeout) {
        return executeAndForget(HttpMethod.DELETE, uri, request, timeout);
    }

    /**
     * Executes specified HTTP method
     *
     * @param httpMethod http method name
     * @param uri        to execute http request against
     * @param request    parameters to execute request with
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse execute(@NotNull CharSequence httpMethod,
                                       @NotNull URI uri,
                                       @NotNull SimpleHttpRequest request) {
        return execute(httpMethod, uri, request, DEFAULT_TIMEOUT);
    }

    /**
     * Executes specified HTTP method
     *
     * @param httpMethod http method name
     * @param uri        to execute http request against
     * @param request    parameters to execute request with
     * @return http response without response body
     */
    @NotNull
    default SimpleHttpResponse executeAndForget(@NotNull CharSequence httpMethod,
                                                @NotNull URI uri,
                                                @NotNull SimpleHttpRequest request) {
        return executeAndForget(httpMethod, uri, request, DEFAULT_TIMEOUT);
    }

    /**
     * Executes specified HTTP method
     *
     * @param httpMethod http method name
     * @param uri        to execute http request against
     * @param timeout    for http response
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse execute(@NotNull CharSequence httpMethod,
                                       @NotNull URI uri,
                                       @NotNull Duration timeout) {
        return execute(httpMethod, uri, SimpleHttpRequest.empty(), timeout);
    }

    /**
     * Executes specified HTTP method
     *
     * @param httpMethod http method name
     * @param uri        to execute http request against
     * @param timeout    for http response
     * @return http response without response body
     */
    @NotNull
    default SimpleHttpResponse executeAndForget(@NotNull CharSequence httpMethod,
                                                @NotNull URI uri,
                                                @NotNull Duration timeout) {
        return executeAndForget(httpMethod, uri, SimpleHttpRequest.empty(), timeout);
    }

    /**
     * Executes specified HTTP method
     *
     * @param httpMethod http method name
     * @param uri        to execute http request against
     * @param timeout    for http response
     * @param request    parameters to execute request with
     * @return http response
     */
    @NotNull
    SimpleHttpResponse execute(@NotNull CharSequence httpMethod,
                               @NotNull URI uri,
                               @NotNull SimpleHttpRequest request,
                               @NotNull Duration timeout);

    /**
     * Executes specified HTTP method
     *
     * @param httpMethod http method name
     * @param uri        to execute http request against
     * @param timeout    for http response
     * @param request    parameters to execute request with
     * @return http response without response body
     */
    @NotNull
    SimpleHttpResponse executeAndForget(@NotNull CharSequence httpMethod,
                                        @NotNull URI uri,
                                        @NotNull SimpleHttpRequest request,
                                        @NotNull Duration timeout);

    /**
     * Executes specified HTTP method
     *
     * @param httpMethod http method name
     * @param uri        to execute http request against
     * @param timeout    for http response
     * @param request    parameters to execute request with
     * @return http response
     */
    @NotNull
    CompletableFuture<SimpleHttpResponse> executeAsync(@NotNull CharSequence httpMethod,
                                                       @NotNull URI uri,
                                                       @NotNull SimpleHttpRequest request,
                                                       @NotNull Duration timeout);

    /**
     * Executes specified HTTP method
     *
     * @param httpMethod http method name
     * @param uri        to execute http request against
     * @param timeout    for http response
     * @param request    parameters to execute request with
     * @return http response without response body
     */
    @NotNull
    CompletableFuture<SimpleHttpResponse> executeAndForgetAsync(@NotNull CharSequence httpMethod,
                                                                @NotNull URI uri,
                                                                @NotNull SimpleHttpRequest request,
                                                                @NotNull Duration timeout);
}
