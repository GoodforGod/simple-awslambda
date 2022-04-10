package io.goodforgod.aws.lambda.simple.http;

import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow;

/**
 * Handles {@link String} as http request
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
record StringHttpBody(String body) implements SimpleHttpBody {

    @Override
    public Flow.Publisher<ByteBuffer> value() {
        return HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8);
    }
}
