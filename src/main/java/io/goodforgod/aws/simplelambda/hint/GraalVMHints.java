package io.goodforgod.aws.simplelambda.hint;

import io.goodforgod.aws.lambda.events.BodyBase64Event;
import io.goodforgod.aws.lambda.events.BodyEvent;
import io.goodforgod.aws.lambda.events.gateway.*;
import io.goodforgod.graalvm.hint.annotation.InitializationHint;
import io.goodforgod.graalvm.hint.annotation.ResourceHint;
import io.goodforgod.graalvm.hint.annotation.TypeHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 27.09.2021
 */
@TypeHint(
        value = { TypeHint.AccessType.ALL_DECLARED },
        types = {
                APIGatewayProxyEvent.class,
                APIGatewayProxyEvent.RequestIdentity.class,
                APIGatewayProxyEvent.ProxyRequestContext.class,
                APIGatewayProxyResponse.class,
                APIGatewayV2HTTPEvent.class,
                APIGatewayV2HTTPEvent.RequestContext.class,
                APIGatewayV2HTTPEvent.RequestContext.Http.class,
                APIGatewayV2HTTPEvent.RequestContext.IAM.class,
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
@InitializationHint(typeNames = {
        "io.goodforgod.aws.lambda.simple",
        "io.goodforgod.gson.configuration",
        "com.google.gson.Gson",
        "io.goodforgod.slf4j.simplelogger",
        "org.slf4j.impl",
        "org.slf4j.LoggerFactory"
})
@ResourceHint(patterns = {
        "gson.properties",
        "simplelogger.properties"
})
interface GraalVMHints {
}
