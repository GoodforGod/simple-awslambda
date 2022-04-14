package io.goodforgod.aws.lambda.simple;

import io.goodforgod.aws.lambda.events.gateway.APIGatewayV2HTTPEvent;
import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.aws.lambda.simple.mock.BodyLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.mock.Request;
import io.goodforgod.aws.lambda.simple.mock.Response;
import io.goodforgod.aws.lambda.simple.testing.AwsLambdaAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
class BodyEventHandlerTests extends Assertions {

    @Test
    void eventHandled() {
        final Request request = new Request("Steeven King");
        final Response response = AwsLambdaAssertions.ofEntrypoint(new BodyLambdaEntrypoint())
                .input(context -> {
                    final Converter converter = context.getBean(Converter.class);
                    final String req = converter.toString(request);
                    final String event = converter.toString(new APIGatewayV2HTTPEvent().setBody(req));
                    return event.getBytes();
                })
                .expectJson(Response.class);

        assertEquals("Hello - Steeven King", response.message());
    }
}
