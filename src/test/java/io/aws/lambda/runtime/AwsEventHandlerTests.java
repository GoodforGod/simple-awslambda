package io.aws.lambda.runtime;

import io.aws.lambda.runtime.model.AwsRequestEvent;
import io.aws.lambda.runtime.model.AwsResponseEvent;
import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
class AwsEventHandlerTests extends Assertions {

    @Test
    void handled() {
        try (final ApplicationContext context = ApplicationContext.run()) {
            final AwsEventHandler eventHandler = context.getBean(AwsEventHandler.class);
            final AwsRequestEvent requestEvent = new AwsRequestEvent().setBody("{\"name\":\"bob\"}");
            final AwsResponseEvent responseEvent = eventHandler.handle(requestEvent);
            assertNotNull(responseEvent);
            assertEquals("response for bob", responseEvent.getBody());
        }
    }
}
