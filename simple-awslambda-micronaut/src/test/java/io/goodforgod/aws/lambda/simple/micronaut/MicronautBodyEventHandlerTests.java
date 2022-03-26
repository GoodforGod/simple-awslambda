package io.goodforgod.aws.lambda.simple.micronaut;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.lambda.events.gateway.APIGatewayV2HTTPEvent;
import io.goodforgod.aws.lambda.simple.EventContextBuilder;
import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.aws.lambda.simple.handler.EventHandler;
import io.goodforgod.aws.lambda.simple.handler.impl.BodyEventHandler;
import io.goodforgod.aws.lambda.simple.reactive.SubscriberUtils;
import io.goodforgod.aws.lambda.simple.runtime.RuntimeContext;
import io.goodforgod.aws.lambda.simple.utils.InputStreamUtils;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.Flow.Publisher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
class MicronautBodyEventHandlerTests extends Assertions {

    private static final RuntimeContext CONTEXT = new MicronautBodyLambdaEntrypoint().getRuntimeContext();

    @BeforeAll
    public static void setup() {
        CONTEXT.setupInRuntime();
    }

    @AfterAll
    public static void cleanup() throws Exception {
        CONTEXT.close();
    }

    @Test
    void bodyEventHandled() {
        final RequestHandler requestHandler = CONTEXT.getBean(RequestHandler.class);
        final EventHandler eventHandler = CONTEXT.getBean(BodyEventHandler.class);
        final Converter converter = CONTEXT.getBean(Converter.class);

        final String eventBody = "{\"name\":\"Steeven King\"}";
        final APIGatewayV2HTTPEvent event = new APIGatewayV2HTTPEvent().setBody(eventBody);
        final String eventAsString = converter.toString(event);
        final InputStream inputStream = InputStreamUtils.getInputStreamFromStringUTF8(eventAsString);

        final Context eventContext = EventContextBuilder.builder().setAwsRequestId(UUID.randomUUID().toString()).build();
        final Publisher<ByteBuffer> publisher = eventHandler.handle(requestHandler, inputStream, eventContext);
        assertNotNull(publisher);

        final String responseAsString = SubscriberUtils.getPublisherString(publisher);
        assertNotNull(responseAsString);
        assertTrue(responseAsString.contains("Hello - Steeven King"));
    }
}