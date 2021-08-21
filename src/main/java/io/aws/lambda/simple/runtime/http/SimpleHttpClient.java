package io.aws.lambda.simple.runtime.http;

import io.aws.lambda.simple.runtime.http.client.StringSimpleHttpRequest;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

import static io.aws.lambda.simple.runtime.http.client.NativeSimpleHttpClient.*;

/**
 * Simple Http Client
 *
 * @author Anton Kurako (GoodforGod)
 * @since 27.10.2020
 */
public interface SimpleHttpClient {

    /**
     * GET
     * 
     * @param uri to execute http request against
     * @return http response
     */
    @NotNull
    default SimpleHttpResponse get(@NotNull URI uri) {
        return execute(METHOD_GET, uri, StringSimpleHttpRequest.empty());
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
        return execute(METHOD_GET, uri, StringSimpleHttpRequest.ofHeaders(headers));
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
        return execute(METHOD_POST, uri, request);
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
        return executeAndForget(METHOD_POST, uri, request);
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
        return execute(METHOD_PUT, uri, request);
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
        return executeAndForget(METHOD_PUT, uri, request);
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
        return execute(METHOD_PATCH, uri, request);
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
        return executeAndForget(METHOD_PATCH, uri, request);
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
        return execute(METHOD_DELETE, uri, request);

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
        return executeAndForget(METHOD_DELETE, uri, request);
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
    default SimpleHttpResponse execute(@NotNull String httpMethod,
                                       @NotNull URI uri,
                                       @NotNull SimpleHttpRequest request) {
        return execute(httpMethod, uri, DEFAULT_TIMEOUT, request);
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
    default SimpleHttpResponse executeAndForget(@NotNull String httpMethod,
                                                @NotNull URI uri,
                                                @NotNull SimpleHttpRequest request) {
        return executeAndForget(httpMethod, uri, DEFAULT_TIMEOUT, request);
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
    SimpleHttpResponse execute(@NotNull String httpMethod,
                               @NotNull URI uri,
                               @NotNull Duration timeout,
                               @NotNull SimpleHttpRequest request);

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
    SimpleHttpResponse executeAndForget(@NotNull String httpMethod,
                                        @NotNull URI uri,
                                        @NotNull Duration timeout,
                                        @NotNull SimpleHttpRequest request);
}
