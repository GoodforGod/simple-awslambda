package io.aws.lambda.simple.runtime;

import io.aws.lambda.events.gateway.APIGatewayV2HTTPEvent;
import io.aws.lambda.simple.runtime.convert.Converter;
import io.aws.lambda.simple.runtime.example.HelloWorldLambda;
import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.handler.LambdaContext;
import io.aws.lambda.simple.runtime.handler.impl.BodyEventHandler;
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
class BodyEventHandlerTests extends Assertions {

    @Test
    void bodyEventHandled() {
        try (final RuntimeContext context = new SimpleRuntimeContext(new HelloWorldLambda(), BodyEventHandler.class)) {
            final EventHandler handler = context.getBean(EventHandler.class);
            final Converter converter = context.getBean(Converter.class);

            final String eventBody = "{\"name\":\"Steeven King\"}";
            final APIGatewayV2HTTPEvent event = new APIGatewayV2HTTPEvent().setBody(eventBody);
            final String eventAsString = converter.toString(event);
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
