package io.goodforgod.aws.simplelambda.http.connectionurl;

import static io.goodforgod.aws.simplelambda.http.connectionurl.UrlConnectionHttpClient.QUALIFIER;

import io.goodforgod.aws.simplelambda.error.StatusException;
import io.goodforgod.aws.simplelambda.http.SimpleHttpClient;
import io.goodforgod.aws.simplelambda.http.SimpleHttpRequest;
import io.goodforgod.aws.simplelambda.http.SimpleHttpResponse;
import io.goodforgod.aws.simplelambda.http.common.EmptyHttpResponse;
import io.goodforgod.aws.simplelambda.http.common.InputStreamHttpResponse;
import io.goodforgod.aws.simplelambda.reactive.ByteBufferSubscriber;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Flow.Publisher;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 10.10.2021
 */
@Named(QUALIFIER)
@Singleton
public class UrlConnectionHttpClient implements SimpleHttpClient {

    public static final String QUALIFIER = "urlconnection";

    private static final int CONNECTION_TIMEOUT = Math.toIntExact(Duration.ofMinutes(10).toMillis());

    @Override
    public @NotNull SimpleHttpResponse execute(@NotNull CharSequence httpMethod,
                                               @NotNull URI uri,
                                               @NotNull SimpleHttpRequest request,
                                               @NotNull Duration timeout) {
        try {
            return executeAsync(httpMethod, uri, request, timeout).join();
        } catch (CompletionException e) {
            throw new StatusException(500, e.getCause());
        }
    }

    @Override
    public @NotNull SimpleHttpResponse executeAndForget(@NotNull CharSequence httpMethod,
                                                        @NotNull URI uri,
                                                        @NotNull SimpleHttpRequest request,
                                                        @NotNull Duration timeout) {
        try {
            return executeAndForgetAsync(httpMethod, uri, request, timeout).join();
        } catch (CompletionException e) {
            throw new StatusException(500, e.getCause());
        }
    }

    @Override
    public @NotNull CompletableFuture<SimpleHttpResponse> executeAsync(@NotNull CharSequence httpMethod,
                                                                       @NotNull URI uri,
                                                                       @NotNull SimpleHttpRequest request,
                                                                       @NotNull Duration timeout) {
        return exec(httpMethod, uri, request, timeout, false);
    }

    @Override
    public @NotNull CompletableFuture<SimpleHttpResponse> executeAndForgetAsync(@NotNull CharSequence httpMethod,
                                                                                @NotNull URI uri,
                                                                                @NotNull SimpleHttpRequest request,
                                                                                @NotNull Duration timeout) {
        return exec(httpMethod, uri, request, timeout, true);
    }

    private CompletableFuture<SimpleHttpResponse> exec(@NotNull CharSequence httpMethod,
                                                       @NotNull URI uri,
                                                       @NotNull SimpleHttpRequest request,
                                                       @NotNull Duration timeout,
                                                       boolean forgetResponse) {
        try {
            final Publisher<ByteBuffer> body = request.body();
            final URL url = uri.toURL();
            final String method = httpMethod.toString();
            if (body == null) {
                return CompletableFuture.supplyAsync(() -> exec(url, method, request.headers(), null, timeout, forgetResponse));
            } else {
                final ByteBufferSubscriber subscriber = new ByteBufferSubscriber();
                body.subscribe(subscriber);
                return subscriber.result()
                        .thenApply(buffer -> exec(url, method, request.headers(), buffer, timeout, forgetResponse));
            }
        } catch (MalformedURLException e) {
            throw new StatusException(400, e.getMessage());
        }
    }

    private SimpleHttpResponse exec(@NotNull URL url,
                                    @NotNull String httpMethod,
                                    @NotNull Map<String, String> headers,
                                    @Nullable ByteBuffer body,
                                    @NotNull Duration timeout,
                                    boolean forgetResponse) {
        try {
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(httpMethod);
            con.setConnectTimeout(CONNECTION_TIMEOUT);
            con.setReadTimeout(Math.toIntExact(timeout.toMillis()));
            con.setUseCaches(true);
            con.setInstanceFollowRedirects(true);
            con.setDoOutput(!forgetResponse);
            con.setDoInput(true);
            headers.forEach(con::setRequestProperty);

            con.connect();
            final String location = con.getHeaderField("Location");
            if (location != null) {
                return exec(new URL(location), httpMethod, headers, body, timeout, forgetResponse);
            }

            if (body != null) {
                final byte[] array = body.array();
                con.setRequestProperty("Content-Length", String.valueOf(array.length));
                try (final DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream())) {
                    dataOutputStream.write(array);
                }
            }

            if (forgetResponse) {
                return EmptyHttpResponse.of(con.getResponseCode(), con.getHeaderFields());
            } else {
                final InputStream responseInputStream = (con.getResponseCode() >= 400)
                        ? con.getErrorStream()
                        : con.getInputStream();

                final InputStream decodedStream = getDecodedStream(responseInputStream, con.getContentEncoding());
                return InputStreamHttpResponse.of(con.getResponseCode(), decodedStream, con.getHeaderFields());
            }
        } catch (IOException e) {
            throw new StatusException(500, e);
        }
    }

    private InputStream getDecodedStream(InputStream connectionInputStream, String contentEncoding) throws IOException {
        if ("deflate".equals(contentEncoding)) {
            return new BufferedInputStream(new InflaterInputStream(connectionInputStream));
        } else if ("gzip".equals(contentEncoding)) {
            return new BufferedInputStream(new GZIPInputStream(connectionInputStream));
        } else {
            return new BufferedInputStream(connectionInputStream);
        }
    }

    @Override
    public void close() {
        // do nothing
    }
}
