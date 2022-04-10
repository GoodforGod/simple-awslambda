package io.goodforgod.aws.lambda.simple.http;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;

/**
 * Handles {@link InputStream} as http request
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.08.2021
 */
record InputStreamHttpBody(InputStream inputStream) implements SimpleHttpBody {

    @Override
    public Flow.Publisher<ByteBuffer> value() {
        return HttpRequest.BodyPublishers.ofInputStream(() -> inputStream);
    }
}
