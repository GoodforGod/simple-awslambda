package io.aws.lambda.runtime.http;

import java.net.URI;
import java.time.Duration;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
public interface AwsHttpClient {

    AwsHttpResponse get(URI uri);

    AwsHttpResponse get(URI uri, Duration timeout);

    AwsHttpResponse post(URI uri, String body);

    void postAndForget(URI uri, String body);
}
