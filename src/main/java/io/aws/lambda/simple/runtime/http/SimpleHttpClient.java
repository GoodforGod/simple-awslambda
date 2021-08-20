package io.aws.lambda.simple.runtime.http;

import io.aws.lambda.simple.runtime.http.impl.StringSimpleHttpRequest;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Map;

import static io.aws.lambda.simple.runtime.http.impl.NativeSimpleHttpClient.*;

/**
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
        return method(GET, uri, StringSimpleHttpRequest.empty());
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
        return method(GET, uri, StringSimpleHttpRequest.ofHeaders(headers));
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
        return method(POST, uri, request);
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
        return methodAndForget(POST, uri, request);
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
        return method(PUT, uri, request);
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
        return methodAndForget(PUT, uri, request);
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
        return method(PATCH, uri, request);
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
        return methodAndForget(PATCH, uri, request);
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
        return method(DELETE, uri, request);

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
        return methodAndForget(DELETE, uri, request);
    }

    /**
     * Executes specified HTTP method
     *
     * @param method  http method name
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @return http response
     */
    @NotNull
    SimpleHttpResponse method(@NotNull String method,
                              @NotNull URI uri,
                              @NotNull SimpleHttpRequest request);

    /**
     * Executes specified HTTP method
     *
     * @param method  http method name
     * @param uri     to execute http request against
     * @param request parameters to execute request with
     * @return http response without response body
     */
    @NotNull
    SimpleHttpResponse methodAndForget(@NotNull String method,
                                       @NotNull URI uri,
                                       @NotNull SimpleHttpRequest request);
}
