package io.goodforgod.aws.simplelambda;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.simplelambda.config.AwsRuntimeVariables;
import io.goodforgod.aws.simplelambda.handler.EventHandler;
import io.goodforgod.aws.simplelambda.handler.impl.InputEventHandler;
import io.goodforgod.aws.simplelambda.mock.entrypoint.BodyLambdaEntrypoint;
import io.goodforgod.aws.simplelambda.reactive.SubscriberUtils;
import io.goodforgod.aws.simplelambda.runtime.RuntimeContext;
import io.goodforgod.aws.simplelambda.utils.InputStreamUtils;
import io.goodforgod.http.common.HttpHeaders;
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
        try (final RuntimeContext context = new BodyLambdaEntrypoint().getRuntimeContext()) {
            context.setupInRuntime();

            final EventHandler handler = context.getBean(InputEventHandler.class);
            final RequestHandler requestHandler = context.getBean(RequestHandler.class);

            final String eventAsString = "{\"name\":\"Steeven King\"}";
            final InputStream inputStream = InputStreamUtils.getInputStreamFromStringUTF8(eventAsString);

            final HttpHeaders headers = HttpHeaders.of(AwsRuntimeVariables.LAMBDA_RUNTIME_AWS_REQUEST_ID,
                    UUID.randomUUID().toString());
            final Publisher<ByteBuffer> publisher = handler.handle(requestHandler,
                    inputStream,
                    EventContext.ofHeaders(headers));

            assertNotNull(publisher);

            final String responseAsString = SubscriberUtils.getPublisherString(publisher);
            assertNotNull(responseAsString);
            assertTrue(responseAsString.contains("Hello - Steeven King"));
        } catch (Exception e) {
            fail(e);
        }
    }
}
