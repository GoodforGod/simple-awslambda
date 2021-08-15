package io.aws.lambda.simple.runtime.micronaut;

import io.aws.lambda.events.BodyBase64Event;
import io.aws.lambda.events.BodyEvent;
import io.aws.lambda.events.gateway.*;
import io.aws.lambda.simple.runtime.convert.impl.GsonConverter;
import io.aws.lambda.simple.runtime.handler.impl.BodyEventHandler;
import io.aws.lambda.simple.runtime.handler.impl.InputEventHandler;
import io.aws.lambda.simple.runtime.http.impl.NativeAwsHttpClient;
import io.micronaut.core.annotation.Introspected;

/**
 * Hints for AOT introspection for DTOs.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 14.08.2021
 */
@Introspected(classes = {
        GsonConverter.class,
        MicronautGsonConfiguration.class,
        MicronautGsonFactory.class,
        NativeAwsHttpClient.class,
        InputEventHandler.class,
        BodyEventHandler.class,

        APIGatewayProxyEvent.class,
        APIGatewayProxyEvent.RequestIdentity.class,
        APIGatewayProxyEvent.ProxyRequestContext.class,
        APIGatewayProxyResponse.class,
        APIGatewayV2HTTPEvent.class,
        APIGatewayV2HTTPEvent.RequestContext.class,
        APIGatewayV2HTTPEvent.RequestContext.Http.class,
        APIGatewayV2HTTPEvent.RequestContext.CognitoIdentity.class,
        APIGatewayV2HTTPEvent.RequestContext.Authorizer.class,
        APIGatewayV2HTTPEvent.RequestContext.Authorizer.JWT.class,
        APIGatewayV2HTTPResponse.class,
        APIGatewayV2WebSocketEvent.class,
        APIGatewayV2WebSocketEvent.RequestContext.class,
        APIGatewayV2WebSocketEvent.RequestIdentity.class,
        APIGatewayV2WebSocketResponse.class,

        BodyEvent.class,
        BodyBase64Event.class
})
public interface IntrospectedHints {
}
