package io.lambda.aws.http;

import java.util.List;
import java.util.Map;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public interface AwsHttpResponse {

    String body();

    Map<String, List<String>> headers();

    String headerAnyOrThrow(String name);
}
