package io.aws.lambda.runtime.http;

import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.time.Duration;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 27.10.2020
 */
public interface AwsHttpClient {

    AwsHttpResponse get(URI uri);

    AwsHttpResponse get(URI uri, Duration timeout);

    AwsHttpResponse post(URI uri, @Nullable String body);

    void postAndForget(URI uri, @Nullable String body);
}
