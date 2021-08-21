package io.aws.lambda.simple.runtime.http.uri;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.*;

/**
 * Helper for building URI (Micronaut copycat of UriBuilder)
 *
 * @author Anton Kurako (GoodforGod)
 * @since 21.08.2021
 */
class QueryStringDecoder {

    private static final String EMPTY_STRING = "";
    private static final char SPACE = 0x20;

    private static final int DEFAULT_MAX_PARAMS = 1024;

    private final Charset charset;
    private final String uri;
    private final int maxParams;
    private int pathEndIdx;
    private String path;
    private Map<String, List<String>> params;

    QueryStringDecoder(String uri) {
        this(uri, StandardCharsets.UTF_8);
    }

    QueryStringDecoder(String uri, Charset charset) {
        this(uri, charset, true);
    }

    QueryStringDecoder(String uri, Charset charset, boolean hasPath) {
        this(uri, charset, hasPath, DEFAULT_MAX_PARAMS);
    }

    /**
     * Creates a new decoder that decodes the specified URI encoded in the specified
     * charset.
     *
     * @param uri       The URI
     * @param charset   The charset to use
     * @param hasPath   whether a path is present
     * @param maxParams The maximum number of params
     */
    QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams) {
        this.uri = Objects.requireNonNull(uri, "uri");
        this.charset = Objects.requireNonNull(charset, "charset");
        this.maxParams = Objects.requireNonNull(maxParams, "maxParams");

        // `-1` means that path end index will be initialized lazily
        pathEndIdx = hasPath ? -1 : 0;
    }

    QueryStringDecoder(URI uri) {
        this(uri, StandardCharsets.UTF_8);
    }

    QueryStringDecoder(URI uri, Charset charset) {
        this(uri, charset, DEFAULT_MAX_PARAMS);
    }

    /**
     * Creates a new decoder that decodes the specified URI encoded in the specified
     * charset.
     *
     * @param uri       The URI
     * @param charset   The charset to use
     * @param maxParams The maximum number of params
     */
    QueryStringDecoder(URI uri, Charset charset, int maxParams) {
        String rawPath = uri.getRawPath();
        if (rawPath == null) {
            rawPath = EMPTY_STRING;
        }
        String rawQuery = uri.getRawQuery();
        // Also take care of cut of things like "http://localhost"
        this.uri = rawQuery == null ? rawPath : rawPath + '?' + rawQuery;
        this.charset = Objects.requireNonNull(charset, "charset");
        if (maxParams < 0) {
            throw new IllegalArgumentException("Argument [maxParams] cannot be negative");
        }
        this.maxParams = maxParams;
        pathEndIdx = rawPath.length();
    }

    @Override
    public String toString() {
        return uri();
    }

    /**
     * @return Returns the uri used to initialize this {@link QueryStringDecoder}.
     */
    public String uri() {
        return uri;
    }

    /**
     * @return Returns the decoded path string of the URI.
     */
    public String path() {
        if (path == null) {
            path = decodeComponent(uri, 0, pathEndIdx(), charset, true);
        }
        return path;
    }

    /**
     * @return Returns the decoded key-value parameter pairs of the URI.
     */
    public Map<String, List<String>> parameters() {
        if (params == null) {
            params = decodeParams(uri, pathEndIdx(), charset, maxParams);
        }
        return params;
    }

    /**
     * @return Returns the raw path string of the URI.
     */
    public String rawPath() {
        return uri.substring(0, pathEndIdx());
    }

    /**
     * @return Returns raw query string of the URI.
     */
    public String rawQuery() {
        int start = pathEndIdx() + 1;
        return start < uri.length() ? uri.substring(start) : EMPTY_STRING;
    }

    private int pathEndIdx() {
        if (pathEndIdx == -1) {
            pathEndIdx = findPathEndIndex(uri);
        }
        return pathEndIdx;
    }

    private Map<String, List<String>> decodeParams(String s, int from, Charset charset, int paramsLimit) {
        int len = s.length();
        if (from >= len) {
            return Collections.emptyMap();
        }
        if (s.charAt(from) == '?') {
            from++;
        }
        Map<String, List<String>> params = new LinkedHashMap<>();
        int nameStart = from;
        int valueStart = -1;
        int i;
        loop: for (i = from; i < len; i++) {
            switch (s.charAt(i)) {
                case '=':
                    if (nameStart == i) {
                        nameStart = i + 1;
                    } else if (valueStart < nameStart) {
                        valueStart = i + 1;
                    }
                    break;
                case '&':
                case ';':
                    if (addParam(s, nameStart, valueStart, i, params, charset)) {
                        paramsLimit--;
                        if (paramsLimit == 0) {
                            return params;
                        }
                    }
                    nameStart = i + 1;
                    break;
                case '#':
                    break loop;
                default:
                    // continue
            }
        }
        addParam(s, nameStart, valueStart, i, params, charset);
        return params;
    }

    /**
     * Decodes a bit of an URL encoded by a browser.
     * <p>
     * This is equivalent to calling {@link #decodeComponent(String, Charset)} with
     * the UTF-8 charset (recommended to comply with RFC 3986, Section 2).
     * 
     * @param s The string to decode (can be empty).
     * @return The decoded string, or {@code s} if there's nothing to decode. If the
     *         string to decode is {@code null}, returns an empty string.
     * @throws IllegalArgumentException if the string contains a malformed escape
     *                                  sequence.
     */
    public String decodeComponent(final String s) {
        return decodeComponent(s, StandardCharsets.UTF_8);
    }

    /**
     * Decodes a bit of an URL encoded by a browser.
     * <p>
     * The string is expected to be encoded as per RFC 3986, Section 2. This is the
     * encoding used by JavaScript functions {@code encodeURI} and
     * {@code encodeURIComponent}, but not {@code escape}. For example in this
     * encoding, &eacute; (in Unicode {@code U+00E9} or in UTF-8 {@code 0xC3 0xA9})
     * is encoded as {@code %C3%A9} or {@code %c3%a9}.
     * <p>
     * This is essentially equivalent to calling
     * {@link java.net.URLDecoder#decode(String, String)} except that it's over 2x
     * faster and generates less garbage for the GC. Actually this function doesn't
     * allocate any memory if there's nothing to decode, the argument itself is
     * returned.
     * 
     * @param s       The string to decode (can be empty).
     * @param charset The charset to use to decode the string (should really be
     *                {@link StandardCharsets#UTF_8}.
     * @return The decoded string, or {@code s} if there's nothing to decode. If the
     *         string to decode is {@code null}, returns an empty string.
     * @throws IllegalArgumentException if the string contains a malformed escape
     *                                  sequence.
     */
    private String decodeComponent(final String s, final Charset charset) {
        if (s == null) {
            return EMPTY_STRING;
        }
        return decodeComponent(s, 0, s.length(), charset, false);
    }

    private boolean addParam(String s, int nameStart, int valueStart, int valueEnd,
                             Map<String, List<String>> params, Charset charset) {
        if (nameStart >= valueEnd) {
            return false;
        }
        if (valueStart <= nameStart) {
            valueStart = valueEnd + 1;
        }
        String name = decodeComponent(s, nameStart, valueStart - 1, charset, false);
        String value = decodeComponent(s, valueStart, valueEnd, charset, false);
        List<String> values = params.get(name);
        if (values == null) {
            values = new ArrayList<>(1); // Often there's only 1 value.
            params.put(name, values);
        }
        values.add(value);
        return true;
    }

    private String decodeComponent(String s, int from, int toExcluded, Charset charset, boolean isPath) {
        int len = toExcluded - from;
        if (len <= 0) {
            return EMPTY_STRING;
        }
        int firstEscaped = -1;
        for (int i = from; i < toExcluded; i++) {
            char c = s.charAt(i);
            if (c == '%' || c == '+' && !isPath) {
                firstEscaped = i;
                break;
            }
        }
        if (firstEscaped == -1) {
            return s.substring(from, toExcluded);
        }

        CharsetDecoder decoder = charset.newDecoder();

        // Each encoded byte takes 3 characters (e.g. "%20")
        int decodedCapacity = (toExcluded - firstEscaped) / 3;
        ByteBuffer byteBuf = ByteBuffer.allocate(decodedCapacity);
        CharBuffer charBuf = CharBuffer.allocate(decodedCapacity);

        StringBuilder strBuf = new StringBuilder(len);
        strBuf.append(s, from, firstEscaped);

        for (int i = firstEscaped; i < toExcluded; i++) {
            char c = s.charAt(i);
            if (c != '%') {
                strBuf.append(c != '+' || isPath ? c : SPACE);
                continue;
            }

            byteBuf.clear();
            do {
                if (i + 3 > toExcluded) {
                    throw new IllegalArgumentException("unterminated escape sequence at index " + i + " of: " + s);
                }
                byteBuf.put(decodeHexByte(s, i + 1));
                i += 3;
            } while (i < toExcluded && s.charAt(i) == '%');
            i--;

            byteBuf.flip();
            charBuf.clear();
            CoderResult result = decoder.reset().decode(byteBuf, charBuf, true);
            try {
                if (!result.isUnderflow()) {
                    result.throwException();
                }
                result = decoder.flush(charBuf);
                if (!result.isUnderflow()) {
                    result.throwException();
                }
            } catch (CharacterCodingException ex) {
                throw new IllegalStateException(ex);
            }
            strBuf.append(charBuf.flip());
        }
        return strBuf.toString();
    }

    private int findPathEndIndex(String uri) {
        int len = uri.length();
        for (int i = 0; i < len; i++) {
            char c = uri.charAt(i);
            if (c == '?' || c == '#') {
                return i;
            }
        }
        return len;
    }

    private byte decodeHexByte(CharSequence s, int pos) {
        int hi = decodeHexNibble(s.charAt(pos));
        int lo = decodeHexNibble(s.charAt(pos + 1));
        if (hi == -1 || lo == -1) {
            throw new IllegalArgumentException(String.format(
                    "invalid hex byte '%s' at index %d of '%s'", s.subSequence(pos, pos + 2), pos, s));
        }
        return (byte) ((hi << 4) + lo);
    }

    private int decodeHexNibble(final char c) {
        // Character.digit() is not used here, as it addresses a larger
        // set of characters (both ASCII and full-width latin letters).
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return c - ('A' - 0xA);
        }
        if (c >= 'a' && c <= 'f') {
            return c - ('a' - 0xA);
        }
        return -1;
    }
}
