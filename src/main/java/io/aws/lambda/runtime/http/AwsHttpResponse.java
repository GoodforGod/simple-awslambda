package io.aws.lambda.runtime.http;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public interface AwsHttpResponse {

    String body();

    @NotNull
    Map<String, List<String>> headers();

    @NotNull
    String headerAnyOrThrow(@NotNull String name);

    String headerAny(@NotNull String name);
}
