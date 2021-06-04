package io.aws.lambda.runtime;

import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.handler.impl.BodyEventHandler;
import io.aws.lambda.runtime.model.gateway.AwsGatewayRequest;
import io.aws.lambda.runtime.model.gateway.AwsGatewayResponse;
import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
class BodyEventHandlerTests extends Assertions {

    @Test
    void handled() {
        try (final ApplicationContext context = ApplicationContext.run()) {
            final EventHandler handler = context.getBean(BodyEventHandler.class);
            final Converter converter = context.getBean(Converter.class);

            final String body = "{\"name\":\"bob\"}";
            final AwsGatewayRequest requestEvent = AwsGatewayRequest.builder()
                    .setBody(body)
                    .build();
            final String json = converter.convertToJson(requestEvent);

            final String response = handler.handle(json, LambdaContext.ofHeaders(Collections.emptyMap()));
            assertNotNull(response);

            final AwsGatewayResponse responseEvent = converter.convertToType(response, AwsGatewayResponse.class);
            assertEquals("response for bob", responseEvent.getBody());
        }
    }
}
