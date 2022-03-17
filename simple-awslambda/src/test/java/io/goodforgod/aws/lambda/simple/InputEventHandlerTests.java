package io.goodforgod.aws.lambda.simple;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.lambda.simple.handler.EventHandler;
import io.goodforgod.aws.lambda.simple.handler.impl.InputEventHandler;
import io.goodforgod.aws.lambda.simple.mock.BodyLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.reactive.SubscriberUtils;
import io.goodforgod.aws.lambda.simple.runtime.RuntimeContext;
import io.goodforgod.aws.lambda.simple.utils.InputStreamUtils;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.Flow.Publisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
class InputEventHandlerTests extends Assertions {

    @Test
    void inputEventHandled() {
        try (final RuntimeContext runtimeContext = new BodyLambdaEntrypoint().getRuntimeContext()) {
            runtimeContext.setupInRuntime();

            final EventHandler handler = runtimeContext.getBean(InputEventHandler.class);
            final RequestHandler requestHandler = runtimeContext.getBean(RequestHandler.class);

            final String eventAsString = "{\"name\":\"Steeven King\"}";
            final InputStream inputStream = InputStreamUtils.getInputStreamFromStringUTF8(eventAsString);

            final Context eventContext = EventContextBuilder.builder().setAwsRequestId(UUID.randomUUID().toString()).build();
            final Publisher<ByteBuffer> publisher = handler.handle(requestHandler, inputStream, eventContext);

            assertNotNull(publisher);

            final String responseAsString = SubscriberUtils.getPublisherString(publisher);
            assertNotNull(responseAsString);
            assertTrue(responseAsString.contains("Hello - Steeven King"));
        } catch (Exception e) {
            fail(e);
        }
    }
}
