package io.aws.lambda.simple.runtime.http;

import org.jetbrains.annotations.NotNull;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 22.08.2021
 */
public enum HttpMethod implements CharSequence {

    /**
     * See https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.3.
     */
    GET,

    /**
     * See https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.5.
     */
    POST,

    /**
     * See https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.6.
     */
    PUT,

    /**
     * See https://tools.ietf.org/html/rfc5789.
     */
    PATCH,

    /**
     * See https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.7.
     */
    DELETE,

    /**
     * See https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.4.
     */
    HEAD,

    /**
     * See https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.2.
     */
    OPTIONS,

    /**
     * See https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.8.
     */
    TRACE,

    /**
     * See https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.9.
     */
    CONNECT;

    @Override
    public int length() {
        return name().length();
    }

    @Override
    public char charAt(int index) {
        return name().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return name().subSequence(start, end);
    }

    @Override
    public @NotNull String toString() {
        return name();
    }
}
