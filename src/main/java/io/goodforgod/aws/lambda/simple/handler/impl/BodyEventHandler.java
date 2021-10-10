package io.goodforgod.aws.lambda.simple.handler.impl;

import static io.goodforgod.aws.lambda.simple.handler.impl.BodyEventHandler.QUALIFIER;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.lambda.events.BodyBase64Event;
import io.goodforgod.aws.lambda.events.BodyEvent;
import io.goodforgod.aws.lambda.events.gateway.*;
import io.goodforgod.aws.lambda.events.system.LoadBalancerRequest;
import io.goodforgod.aws.lambda.events.system.LoadBalancerResponse;
import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.aws.lambda.simple.handler.EventHandler;
import io.goodforgod.aws.lambda.simple.handler.RequestFunction;
import io.goodforgod.aws.lambda.simple.http.nativeclient.StringSimpleHttpRequest;
import io.goodforgod.aws.lambda.simple.utils.TimeUtils;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow.Publisher;
import org.jetbrains.annotations.NotNull;

/**
 * AWS Lambda Gateway Handler for handling requests coming from events that
 * contains body.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Named(QUALIFIER)
@Singleton
public class BodyEventHandler extends AbstractEventHandler implements EventHandler {

    public static final String QUALIFIER = "body-event";

    private final RequestHandler requestHandler;

    @Inject
    public BodyEventHandler(RequestHandler requestHandler, Converter converter) {
        super(converter);
        this.requestHandler = requestHandler;
    }

    @Override
    public @NotNull Publisher<ByteBuffer> handle(@NotNull InputStream eventStream, @NotNull Context context) {
        logger.trace("Function input conversion started...");
        final long inputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;

        final RequestFunction function = getFunctionArguments(requestHandler);
        logger.debug("Function '{}' execution started with input '{}' and output '{}'",
                requestHandler.getClass().getName(), function.getInput().getName(), function.getOutput().getName());

        final Object functionInput = getFunctionInput(eventStream, function.getInput(), function.getOutput(), context);
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
    protected @NotNull Object getFunctionInput(@NotNull InputStream funcInputValue,
                                               @NotNull Class<?> funcInputType,
                                               @NotNull Class<?> funcOutputType,
                                               @NotNull Context context) {
        final String event = getInputAsString(funcInputValue);

        final String eventBody;
        if (BodyEvent.class.isAssignableFrom(funcInputType)) {
            eventBody = event;
        } else {
            final BodyBase64Event<?> bodyEvent = converter.fromString(event, BodyBase64Event.class);
            eventBody = (bodyEvent.isBase64Encoded()) ? bodyEvent.getBodyDecoded() : bodyEvent.getBody();
        }

        return super.getFunctionInput(eventBody, funcInputType, funcOutputType, context);
    }

    /**
     * @param funcOutValue   received from {@link RequestHandler}
     * @param funcInputType  that is input argument class type of
     *                       {@link RequestHandler}
     * @param funcOutputType that is output argument class type of
     *                       {@link RequestHandler}
     * @param context        of request
     * @return converted event output
     */
    @Override
    protected Object getFunctionOutput(Object funcOutValue,
                                       @NotNull Class<?> funcInputType,
                                       @NotNull Class<?> funcOutputType,
                                       @NotNull Context context) {
        if (funcOutValue instanceof InputStream
                || funcOutValue instanceof Publisher
                || funcOutValue instanceof byte[]) {
            return funcOutValue;
        }

        final Object wrappedEvent = tryWrapEvent(funcOutValue, funcInputType);
        if (wrappedEvent == null) {
            return null;
        }

        return converter.toString(wrappedEvent);
    }

    /**
     * @param funcOutValue  received from {@link RequestHandler}
     * @param funcInputType that is input argument class type of
     *                      {@link RequestHandler}
     * @return if argument type is one of AWS DTOs {@link BodyEvent} than result is
     *         wrapped in corresponding type or outValue as is
     */
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
                    .setHeaders(StringSimpleHttpRequest.JSON_HEADERS);
        } else if (APIGatewayProxyEvent.class.isAssignableFrom(funcInputType)) {
            return new APIGatewayProxyResponse()
                    .setBody(funcOutValue)
                    .setHeaders(StringSimpleHttpRequest.JSON_HEADERS);
        } else if (APIGatewayV2HTTPEvent.class.isAssignableFrom(funcInputType)) {
            return new APIGatewayV2HTTPResponse()
                    .setBody(funcOutValue)
                    .setHeaders(StringSimpleHttpRequest.JSON_HEADERS);
        } else if (APIGatewayV2WebSocketEvent.class.isAssignableFrom(funcInputType)) {
            return new APIGatewayV2WebSocketResponse()
                    .setBody(funcOutValue)
                    .setHeaders(StringSimpleHttpRequest.JSON_HEADERS);
        }

        return funcOutValue;
    }
}
