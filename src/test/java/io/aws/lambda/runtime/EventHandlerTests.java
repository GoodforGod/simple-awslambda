package io.aws.lambda.runtime;

import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.handler.impl.RawEventHandler;
import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
class EventHandlerTests extends Assertions {

    @Test
    void handled() {
        try (final ApplicationContext context = ApplicationContext.run()) {
            final EventHandler handler = context.getBean(RawEventHandler.class);

            final String json = "{\"name\":\"bob\"}";
            final String response = handler.handle(json, LambdaContext.ofHeaders(Collections.emptyMap()));
            assertNotNull(response);
            assertEquals("response for bob", response);
        }
    }
}
