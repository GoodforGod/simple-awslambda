package io.aws.lambda.simple.runtime.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.aws.lambda.events.BodyBase64Event;
import io.aws.lambda.events.BodyEvent;
import io.aws.lambda.events.gateway.*;
import io.aws.lambda.events.system.LoadBalancerRequest;
import io.aws.lambda.events.system.LoadBalancerResponse;
import io.aws.lambda.simple.runtime.convert.Converter;
import io.aws.lambda.simple.runtime.handler.EventHandler;
import io.aws.lambda.simple.runtime.handler.RequestFunction;
import io.aws.lambda.simple.runtime.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow.Publisher;

import static io.aws.lambda.simple.runtime.http.nativeclient.StringSimpleHttpRequest.JSON_HEADERS;

/**
 * AWS Lambda Gateway Handler for handling requests coming from events that
 * contains body.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Singleton
public class BodyEventHandler extends AbstractEventHandler implements EventHandler {

    private final RequestHandler requestHandler;

    @Inject
    public BodyEventHandler(RequestHandler requestHandler, Converter converter) {
        super(converter);
        this.requestHandler = requestHandler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Publisher<ByteBuffer> handle(@NotNull InputStream eventStream, @NotNull Context context) {
        logger.trace("Function input event conversion started...");
        final long requestStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;

        final String event = getInputAsString(eventStream);
        final RequestFunction function = getFunctionArguments(requestHandler);
        logger.debug("Function '{}' with input '{}' and output '{}'",
                requestHandler.getClass().getName(), function.getInput().getName(), function.getOutput().getName());

        final String eventBody;
        if (BodyEvent.class.isAssignableFrom(function.getInput())) {
            eventBody = event;
        } else {
            final BodyBase64Event<?> bodyEvent = converter.fromJson(event, BodyBase64Event.class);
            eventBody = (bodyEvent.isBase64Encoded()) ? bodyEvent.getBodyDecoded() : bodyEvent.getBody();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Function input event conversion took: {} millis", TimeUtils.timeTook(requestStart));
            logger.debug("Function input event: {}", eventBody);
        }

        logger.trace("Function input conversion started...");
        final long inputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;
        final Object functionInput = getFunctionInput(function.getInput(), eventBody, context);
        if (logger.isDebugEnabled()) {
            logger.debug("Function input conversion took: {} millis", TimeUtils.timeTook(inputStart));
            logger.debug("Function input: {}", functionInput);
        }

        logger.trace("Function processing started...");
        final long responseStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;
        final Object functionOutput = requestHandler.handleRequest(functionInput, context);
        if (logger.isInfoEnabled()) {
            logger.info("Function processing took: {} millis", TimeUtils.timeTook(responseStart));
        }

        logger.trace("Function output event conversion started...");
        final long outputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;
        final Object response = getFunctionOutput(functionOutput, function.getInput(), function.getOutput(), context);
        if (logger.isDebugEnabled()) {
            logger.debug("Function output event took: {} millis", TimeUtils.timeTook(outputStart));
            logger.debug("Function output event: {}", response);
        }

        return getResponsePublisher(response);
    }

    @Override
    protected Object getFunctionOutput(Object funcOutValue,
                                       @NotNull Class<?> funcInputType,
                                       @NotNull Class<?> funcOutputType,
                                       @NotNull Context context) {
        if (funcOutValue instanceof InputStream) {
            return funcOutValue;
        }

        final Object wrappedEvent = tryWrapEvent(funcOutValue, funcInputType);
        return converter.toJson(wrappedEvent);
    }

    private Object tryWrapEvent(Object funcOutValue,
                                @NotNull Class<?> funcInputType) {
        if (funcOutValue instanceof LoadBalancerResponse
                || funcOutValue instanceof APIGatewayProxyResponse
                || funcOutValue instanceof APIGatewayV2HTTPResponse
                || funcOutValue instanceof APIGatewayV2WebSocketResponse) {
            return funcOutValue;
        }

        if (LoadBalancerRequest.class.isAssignableFrom(funcInputType)) {
            return new LoadBalancerResponse()
                    .setBody(funcOutValue)
                    .setHeaders(JSON_HEADERS);
        } else if (APIGatewayProxyEvent.class.isAssignableFrom(funcInputType)) {
            return new APIGatewayProxyResponse()
                    .setBody(funcOutValue)
                    .setHeaders(JSON_HEADERS);
        } else if (APIGatewayV2HTTPEvent.class.isAssignableFrom(funcInputType)) {
            return new APIGatewayV2HTTPResponse()
                    .setBody(funcOutValue)
                    .setHeaders(JSON_HEADERS);
        } else if (APIGatewayV2WebSocketEvent.class.isAssignableFrom(funcInputType)) {
            return new APIGatewayV2WebSocketResponse()
                    .setBody(funcOutValue)
                    .setHeaders(JSON_HEADERS);
        }

        return funcOutValue;
    }
}
