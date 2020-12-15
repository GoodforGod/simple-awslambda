package io.aws.lambda.runtime;

import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.handler.impl.AwsGatewayEventHandler;
import io.aws.lambda.runtime.model.AwsGatewayRequest;
import io.aws.lambda.runtime.model.AwsGatewayResponse;
import io.aws.lambda.runtime.model.AwsRequestContext;
import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
class AwsGatewayEventHandlerTests extends Assertions {

    @Test
    void handled() {
        try (final ApplicationContext context = ApplicationContext.run()) {
            final EventHandler handler = context.getBean(AwsGatewayEventHandler.class);
            final Converter converter = context.getBean(Converter.class);

            final AwsGatewayRequest requestEvent = new AwsGatewayRequest().setBody("{\"name\":\"bob\"}");
            final String json = converter.convertToJson(requestEvent);

            final String response = handler.handle(json, new AwsRequestContext("1", "1"));
            assertNotNull(response);

            final AwsGatewayResponse responseEvent = converter.convertToType(response, AwsGatewayResponse.class);
            assertEquals("response for bob", responseEvent.getBody());
        }
    }
}
