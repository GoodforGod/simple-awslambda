package io.lambda.aws;

import io.lambda.aws.model.AwsRequestEvent;
import io.lambda.aws.model.AwsResponseEvent;
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
            final AwsRequestEvent requestEvent = new AwsRequestEvent().setBody("bob");
            final AwsResponseEvent responseEvent = eventHandler.handle(requestEvent);
            assertNotNull(responseEvent);
            assertEquals("response for bob", responseEvent.getBody());
        }
    }
}
