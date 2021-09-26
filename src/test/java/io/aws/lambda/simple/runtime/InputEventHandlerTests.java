package io.aws.lambda.simple.runtime;

import io.aws.lambda.simple.runtime.example.HelloWorldLambda;
import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.handler.LambdaContext;
import io.aws.lambda.simple.runtime.handler.impl.InputEventHandler;
import io.aws.lambda.simple.runtime.runtime.RuntimeContext;
import io.aws.lambda.simple.runtime.runtime.SimpleRuntimeContext;
import io.aws.lambda.simple.runtime.utils.InputStreamUtils;
import io.aws.lambda.simple.runtime.utils.SubscriberUtils;
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
        try (final RuntimeContext context = new SimpleRuntimeContext(new HelloWorldLambda())) {
            final EventHandler handler = context.getBean(InputEventHandler.class);

            final String eventAsString = "{\"name\":\"Steeven King\"}";
            final InputStream inputStream = InputStreamUtils.getInputStreamFromStringUTF8(eventAsString);

            final Publisher<ByteBuffer> publisher = handler.handle(inputStream, LambdaContext.ofRequestId(UUID.randomUUID().toString()));
            assertNotNull(publisher);

            final String responseAsString = SubscriberUtils.getPublisherString(publisher);
            assertNotNull(responseAsString);
            assertTrue(responseAsString.contains("Hello - Steeven King"));
        } catch (Exception e) {
            fail(e);
        }
    }
}
