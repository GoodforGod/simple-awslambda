package io.aws.lambda.simple.runtime.http.uri;

import io.aws.lambda.simple.runtime.error.LambdaException;
import io.aws.lambda.simple.runtime.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.Matcher;

import static io.aws.lambda.simple.runtime.http.uri.URITemplate.PATTERN_FULL_PATH;
import static io.aws.lambda.simple.runtime.http.uri.URITemplate.PATTERN_FULL_URI;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 21.08.2021
 */
class DefaultURIBuilder implements URIBuilder {

    private String authority;
    private final Map<String, List<String>> queryParams;
    private String scheme;
    private String userInfo;
    private String host;
    private int port = -1;
    private StringBuilder path = new StringBuilder();
    private String fragment;

    /**
     * Constructor to create from a URI.
     * 
     * @param uri The URI
     */
    DefaultURIBuilder(URI uri) {
        this.scheme = uri.getScheme();
        this.userInfo = uri.getRawUserInfo();
        this.authority = uri.getRawAuthority();
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.path = new StringBuilder();
        final String rawPath = uri.getRawPath();
        if (rawPath != null) {
            this.path.append(rawPath);
        }
        this.fragment = uri.getRawFragment();
        final String query = uri.getQuery();
        if (query != null) {
            final Map<String, List<String>> parameters = new QueryStringDecoder(uri).parameters();
            this.queryParams = new LinkedHashMap<>(parameters);
        } else {
            this.queryParams = new LinkedHashMap<>();
        }
    }

    /**
     * Constructor for charsequence.
     *
     * @param uri The URI
     */
    DefaultURIBuilder(CharSequence uri) {
        if (URITemplate.PATTERN_SCHEME.matcher(uri).matches()) {
            Matcher matcher = PATTERN_FULL_URI.matcher(uri);

            if (matcher.find()) {
                String scheme = matcher.group(2);
                if (scheme != null) {
                    this.scheme = scheme;
                }
                String userInfo = matcher.group(5);
                String host = matcher.group(6);
                String port = matcher.group(8);
                String path = matcher.group(9);
                String query = matcher.group(11);
                String fragment = matcher.group(13);
                if (userInfo != null) {
                    this.userInfo = userInfo;
                }
                if (host != null) {
                    this.host = host;
                }
                if (port != null) {
                    this.port = Integer.parseInt(port);
                }
                if (path != null) {

                    if (fragment != null) {
                        this.fragment = fragment;
                    }
                    this.path = new StringBuilder(path);
                }
                if (query != null) {
                    final Map<String, List<String>> parameters = new QueryStringDecoder(query).parameters();
                    this.queryParams = new LinkedHashMap<>(parameters);
                } else {
                    this.queryParams = new LinkedHashMap<>();
                }
            } else {
                this.path = new StringBuilder(uri.toString());
                this.queryParams = new LinkedHashMap<>();
            }
        } else {
            Matcher matcher = PATTERN_FULL_PATH.matcher(uri);
            if (matcher.find()) {
                final String path = matcher.group(1);
                final String query = matcher.group(3);
                this.fragment = matcher.group(5);

                this.path = new StringBuilder(path);
                if (query != null) {
                    final Map<String, List<String>> parameters = new QueryStringDecoder(uri.toString()).parameters();
                    this.queryParams = new LinkedHashMap<>(parameters);
                } else {
                    this.queryParams = new LinkedHashMap<>();
                }

            } else {
                this.path = new StringBuilder(uri.toString());
                this.queryParams = new LinkedHashMap<>();
            }
        }
    }

    @NotNull
    @Override
    public DefaultURIBuilder fragment(@Nullable String fragment) {
        if (fragment != null)
            this.fragment = fragment;
        return this;
    }

    @NotNull
    @Override
    public DefaultURIBuilder scheme(@Nullable String scheme) {
        if (scheme != null)
            this.scheme = scheme;
        return this;
    }

    @NotNull
    @Override
    public DefaultURIBuilder userInfo(@Nullable String userInfo) {
        if (userInfo != null)
            this.userInfo = userInfo;
        return this;
    }

    @NotNull
    @Override
    public DefaultURIBuilder host(@Nullable String host) {
        if (host != null)
            this.host = host;
        return this;
    }

    @NotNull
    @Override
    public DefaultURIBuilder port(int port) {
        if (port < -1)
            throw new IllegalArgumentException("Invalid port value");
        this.port = port;
        return this;
    }

    @NotNull
    @Override
    public DefaultURIBuilder path(@Nullable String path) {
        if (StringUtils.isEmpty(path)) {
            final int len = this.path.length();
            final boolean endsWithSlash = len > 0 && this.path.charAt(len - 1) == '/';
            if (endsWithSlash) {
                if (path.charAt(0) == '/') {
                    this.path.append(path.substring(1));
                } else {
                    this.path.append(path);
                }
            } else {
                if (path.charAt(0) == '/') {
                    this.path.append(path);
                } else {
                    this.path.append('/').append(path);
                }
            }
        }
        return this;
    }

    @NotNull
    @Override
    public DefaultURIBuilder replacePath(@Nullable String path) {
        if (path != null) {
            this.path.setLength(0);
            this.path.append(path);
        }
        return this;
    }

    @NotNull
    @Override
    public DefaultURIBuilder queryParam(String name, String... values) {
        if (StringUtils.isNotEmpty(name) && values != null && values.length > 0) {
            final List<String> existing = queryParams.getOrDefault(name, Collections.emptyList());
            final List<String> strings = existing != null ? new ArrayList<>(existing) : new ArrayList<>(values.length);
            for (Object value : values) {
                if (value != null) {
                    strings.add(value.toString());
                }
            }
            queryParams.put(name, strings);
        }
        return this;
    }

    @NotNull
    @Override
    public DefaultURIBuilder replaceQueryParam(String name, String... values) {
        if (StringUtils.isNotEmpty(name) && values != null && values.length > 0) {
            List<String> strings = new ArrayList<>(values.length);
            for (Object value : values) {
                if (value != null) {
                    strings.add(value.toString());
                }
            }
            queryParams.put(name, strings);
        }
        return this;
    }

    @NotNull
    @Override
    public URI build() {
        try {
            return new URI(reconstructAsString(null));
        } catch (URISyntaxException e) {
            throw new LambdaException(e);
        }
    }

    @Override
    public String toString() {
        return build().toString();
    }

    private String reconstructAsString(Map<String, String> values) {
        StringBuilder builder = new StringBuilder();
        String scheme = this.scheme;
        String host = this.host;
        if (StringUtils.isNotEmpty(scheme)) {
            if (isTemplate(scheme, values)) {
                scheme = URITemplate.of(scheme).expand(values);
            }

            builder.append(scheme).append(":");
        }

        final boolean hasPort = port != -1;
        final boolean hasHost = host != null;
        final boolean hasUserInfo = StringUtils.isNotEmpty(userInfo);
        if (hasUserInfo || hasHost || hasPort) {
            builder.append("//");
            if (hasUserInfo) {
                String userInfo = this.userInfo;
                if (userInfo.contains(":")) {
                    final String[] sa = userInfo.split(":");
                    userInfo = expandOrEncode(sa[0], values) + ":" + expandOrEncode(sa[1], values);
                } else {
                    userInfo = expandOrEncode(userInfo, values);
                }
                builder.append(userInfo);
                builder.append("@");
            }

            if (hasHost) {
                host = expandOrEncode(host, values);
                builder.append(host);
            }

            if (hasPort) {
                builder.append(":").append(port);
            }
        } else {
            String authority = this.authority;
            if (StringUtils.isNotEmpty(authority)) {
                authority = expandOrEncode(authority, values);
                builder.append("//").append(authority);
            }
        }

        StringBuilder path = this.path;
        if (StringUtils.isNotEmpty(path)) {
            if (builder.length() > 0 && path.charAt(0) != '/') {
                builder.append('/');
            }

            String pathStr = path.toString();
            if (isTemplate(pathStr, values)) {
                pathStr = URITemplate.of(pathStr).expand(values);
            }

            builder.append(pathStr);
        }

        if (!queryParams.isEmpty()) {
            builder.append('?');
            builder.append(buildQueryParams(values));
        }

        String fragment = this.fragment;
        if (StringUtils.isNotEmpty(fragment)) {
            fragment = expandOrEncode(fragment, values);
            if (fragment.charAt(0) != '#') {
                builder.append('#');
            }

            builder.append(fragment);
        }

        return builder.toString();
    }

    private boolean isTemplate(String value, Map<String, String> values) {
        return values != null && value.indexOf('{') > -1;
    }

    private String buildQueryParams(Map<String, String> values) {
        if (!queryParams.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            final Iterator<Map.Entry<String, List<String>>> nameIterator = queryParams.entrySet().iterator();
            while (nameIterator.hasNext()) {
                Map.Entry<String, List<String>> entry = nameIterator.next();
                String rawName = entry.getKey();
                String name = expandOrEncode(rawName, values);

                final Iterator<String> i = entry.getValue().iterator();
                while (i.hasNext()) {
                    String v = expandOrEncode(i.next(), values);
                    builder.append(name).append('=').append(v);
                    if (i.hasNext()) {
                        builder.append('&');
                    }
                }

                if (nameIterator.hasNext()) {
                    builder.append('&');
                }

            }
            return builder.toString();
        }

        return null;
    }

    private String expandOrEncode(String value, Map<String, String> values) {
        return isTemplate(value, values)
                ? URITemplate.of(value).expand(values)
                : encode(value);
    }

    private String encode(String userInfo) {
        try {
            return URLEncoder.encode(userInfo, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("No available charset: " + e.getMessage());
        }
    }
}
