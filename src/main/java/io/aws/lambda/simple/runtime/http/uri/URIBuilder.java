package io.aws.lambda.simple.runtime.http.uri;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 21.08.2021
 */
public interface URIBuilder {

    /**
     * Sets the URI fragment.
     *
     * @param fragment The fragment
     * @return This builder
     */
    @NotNull URIBuilder fragment(@Nullable String fragment);

    /**
     * Sets the URI scheme.
     *
     * @param scheme The scheme
     * @return This builder
     */
    @NotNull URIBuilder scheme(@Nullable String scheme);

    /**
     * Sets the URI user info.
     *
     * @param userInfo The use info
     * @return This builder
     */
    @NotNull URIBuilder userInfo(@Nullable String userInfo);

    /**
     * Sets the URI host.
     *
     * @param host The host to use
     * @return This builder
     */
    @NotNull URIBuilder host(@Nullable String host);

    /**
     * Sets the URI port.
     *
     * @param port The port to use
     * @return This builder
     */
    @NotNull URIBuilder port(int port);

    /**
     * Appends the given path to the existing path correctly handling '/'. If path is null it is simply ignored.
     *
     * @param path The path
     * @return This builder
     */
    @NotNull URIBuilder path(@Nullable String path);

    /**
     * Replaces the existing path if any. If path is null it is simply ignored.
     *
     * @param path The path
     * @return This builder
     */
    @NotNull URIBuilder replacePath(@Nullable String path);

    /**
     * Adds a query parameter for the give name and values. The values will be URI encoded.
     * If either name or values are null the value will be ignored.
     *
     * @param name The name
     * @param values The values
     * @return This builder
     */
    @NotNull URIBuilder queryParam(String name, String...values);

    /**
     * Adds a query parameter for the give name and values. The values will be URI encoded.
     * If either name or values are null the value will be ignored.
     *
     * @param name The name
     * @param values The values
     * @return This builder
     */
    @NotNull URIBuilder replaceQueryParam(String name, String...values);

    /**
     * The constructed URI.
     *
     * @return Build the URI
     * @throws io.aws.lambda.simple.runtime.error.LambdaException if the URI to be constructed is invalid
     */
    @NotNull URI build();

    /**
     * Create a {@link URIBuilder} with the given base URI as a starting point.
     *
     * @param uri The URI
     * @return The builder
     */
    static @NotNull URIBuilder of(@NotNull URI uri) {
        Objects.requireNonNull(uri);
        return new DefaultURIBuilder(uri);
    }

    /**
     * Create a {@link URIBuilder} with the given base URI as a starting point.
     *
     * @param uri The URI
     * @return The builder
     */
    static @NotNull URIBuilder of(@NotNull CharSequence uri) {
        Objects.requireNonNull(uri);
        return new DefaultURIBuilder(uri);
    }
}
