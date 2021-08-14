package io.aws.lambda.simple.runtime;

import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.handler.impl.JsonEventHandler;
import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
class JsonEventHandlerTests extends Assertions {

    @Test
    void handled() {
        try (final ApplicationContext context = ApplicationContext.run()) {
            final EventHandler handler = context.getBean(JsonEventHandler.class);

            final String json = "{\"name\":\"Steeven King\"}";
            final String response = handler.handle(json, LambdaContext.ofHeaders(Collections.emptyMap()));
            assertNotNull(response);
            assertTrue(response.contains("Hello - Steeven King"));
        }
    }
}
