package io.aws.lambda.simple.runtime.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 27.10.2020
 */
public interface AwsHttpClient {

    @NotNull
    AwsHttpResponse get(@NotNull URI uri);

    @NotNull
    AwsHttpResponse post(@NotNull URI uri,
                         @Nullable AwsHttpRequest request);

    void postAndForget(@NotNull URI uri,
                       @Nullable AwsHttpRequest request);
}
