package io.aws.lambda.runtime.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import io.aws.lambda.events.BodyBase64Event;
import io.aws.lambda.events.BodyEvent;
import io.aws.lambda.events.gateway.*;
import io.aws.lambda.events.system.LoadBalancerRequest;
import io.aws.lambda.events.system.LoadBalancerResponse;
import io.aws.lambda.runtime.Lambda;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.model.Pair;
import io.aws.lambda.runtime.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * AWS Lambda Gateway Handler for handling requests coming from events that
 * contains body.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Singleton
public class BodyEventHandler extends RawEventHandler {

    @Inject
    public BodyEventHandler(Lambda function, Converter converter) {
        super(function, converter);
    }

    @Override
    public String handle(@NotNull String event, @NotNull Context context) {
        logger.debug("API Event conversion started...");
        final long requestStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;

        final Pair<Class, Class> funcArgs = getInterfaceGenericType(function);
        final Class<?> inputArgument = funcArgs.getRight();
        final String eventBody;
        if (BodyEvent.class.isAssignableFrom(inputArgument)) {
            eventBody = event;
        } else {
            final BodyBase64Event bodyEvent = converter.convertToType(event, BodyBase64Event.class);
            eventBody = (BodyBase64Event.class.isAssignableFrom(inputArgument) && bodyEvent.isBase64Encoded())
                    ? bodyEvent.getBodyDecoded()
                    : bodyEvent.getBody();
        }

        if (logger.isDebugEnabled()) {
            final String eventName = inputArgument.getSimpleName();
            logger.debug("{} API Event conversion took: {} millis", eventName, TimeUtils.timeTook(requestStart));
            logger.debug("{} API Event body: {}", eventName, eventBody);
        }

        final Object functionOutput = super.handle(eventBody, context);

        logger.debug("API Event conversion started...");
        final long outputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;
        final Object response = getFunctionResponseEvent(functionOutput, funcArgs.getLeft());
        if (logger.isDebugEnabled()) {
            final String eventName = inputArgument.getSimpleName();
            logger.debug("{} API Event conversion took: {} millis", eventName, TimeUtils.timeTook(outputStart));
            logger.debug("{} API Event body: {}", eventName, response);
        }

        return converter.convertToJson(response);
    }

    private Object getFunctionResponseEvent(Object funcOutValue, Class<?> funcInputArg) {
        if (funcOutValue instanceof APIGatewayProxyResponse
                || funcOutValue instanceof APIGatewayV2HTTPResponse
                || funcOutValue instanceof APIGatewayV2WebSocketResponse
                || funcOutValue instanceof LoadBalancerResponse)
            return funcOutValue;

        if (APIGatewayProxyEvent.class.isAssignableFrom(funcInputArg)) {
            return new APIGatewayProxyResponse().setBody(funcOutValue);
        } else if (APIGatewayV2HTTPEvent.class.isAssignableFrom(funcInputArg)) {
            return new APIGatewayV2HTTPResponse().setBody(funcOutValue);
        } else if (APIGatewayV2WebSocketEvent.class.isAssignableFrom(funcInputArg)) {
            return new APIGatewayV2WebSocketResponse().setBody(funcOutValue);
        } else if (LoadBalancerRequest.class.isAssignableFrom(funcInputArg)) {
            return new LoadBalancerResponse().setBody(funcOutValue);
        }

        return funcOutValue;
    }
}
