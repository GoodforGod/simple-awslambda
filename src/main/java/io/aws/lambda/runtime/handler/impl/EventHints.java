package io.aws.lambda.runtime.handler.impl;

import io.aws.lambda.events.BodyBase64Event;
import io.aws.lambda.events.BodyEvent;
import io.aws.lambda.events.gateway.*;
import io.micronaut.core.annotation.TypeHint;

@TypeHint(
        accessType = { TypeHint.AccessType.ALL_DECLARED_CONSTRUCTORS, TypeHint.AccessType.ALL_PUBLIC },
        value = {
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
interface EventHints {

}
