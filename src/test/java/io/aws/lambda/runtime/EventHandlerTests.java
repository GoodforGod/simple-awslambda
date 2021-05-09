package io.aws.lambda.runtime;

import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.handler.impl.DirectEventHandler;
import io.aws.lambda.runtime.model.AwsRequestContext;
import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
class EventHandlerTests extends Assertions {

    @Test
    void handled() {
        try (final ApplicationContext context = ApplicationContext.run()) {
            final EventHandler handler = context.getBean(DirectEventHandler.class);

            final String json = "{\"name\":\"bob\"}";
            final String response = handler.handle(json, new AwsRequestContext("1", "1"));
            assertNotNull(response);
            assertEquals("response for bob", response);
        }
    }
}
