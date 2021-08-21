package io.aws.lambda.simple.runtime;

import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.handler.impl.InputEventHandler;
import io.aws.lambda.simple.runtime.utils.InputStreamUtils;
import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.UUID;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
class InputEventHandlerTests extends Assertions {

    @Test
    void inputEventHandled() {
        try (final ApplicationContext context = ApplicationContext.run()) {
            final EventHandler handler = context.getBean(InputEventHandler.class);

            final String json = "{\"name\":\"Steeven King\"}";
            final InputStream inputStream = InputStreamUtils.getStringUTF8AsInputStream(json);
            final String response = handler.handle(inputStream, LambdaContext.ofRequestId(UUID.randomUUID().toString()));
            assertNotNull(response);
            assertTrue(response.contains("Hello - Steeven King"));
        }
    }
}
