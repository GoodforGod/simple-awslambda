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
import io.aws.lambda.simple.runtime.handler.Function;
import io.aws.lambda.simple.runtime.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

import static io.aws.lambda.simple.runtime.http.impl.NativeAwsHttpClient.CONTENT_TYPE;
import static io.aws.lambda.simple.runtime.http.impl.NativeAwsHttpClient.MEDIA_TYPE_JSON;

/**
 * AWS Lambda Gateway Handler for handling requests coming from events that
 * contains body.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Singleton
public class BodyEventHandler extends AbstractEventHandler implements EventHandler {

    private static final Map<String, String> DEFAULT_HEADERS = Map.of(CONTENT_TYPE, MEDIA_TYPE_JSON);

    private final RequestHandler requestHandler;

    @Inject
    public BodyEventHandler(RequestHandler requestHandler, Converter converter) {
        super(converter);
        this.requestHandler = requestHandler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String handle(@NotNull String event, @NotNull Context context) {
        logger.debug("Function input event conversion started...");
        final long requestStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;

        final Function function = getFunctionArguments(requestHandler);
        logger.debug("Function '{}' with input '{}' and output '{}'",
                requestHandler.getClass().getName(), function.getInput().getName(), function.getOutput().getName());

        final String eventBody;
        if (BodyEvent.class.isAssignableFrom(function.getInput())) {
            eventBody = event;
        } else {
            final BodyBase64Event<?> bodyEvent = converter.convertToType(event, BodyBase64Event.class);
            eventBody = (BodyBase64Event.class.isAssignableFrom(function.getInput()) && bodyEvent.isBase64Encoded())
                    ? bodyEvent.getBodyDecoded()
                    : bodyEvent.getBody();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Function input event conversion took: {} millis", TimeUtils.timeTook(requestStart));
            logger.debug("Function input event: {}", eventBody);
        }

        logger.debug("Function input conversion started...");
        final long inputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;
        final Object functionInput = getFunctionInput(function.getInput(), eventBody, context);
        if (logger.isDebugEnabled()) {
            logger.debug("Function input conversion took: {} millis", TimeUtils.timeTook(inputStart));
            logger.debug("Function input: {}", functionInput);
        }

        logger.debug("Function processing started...");
        final long responseStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;
        final Object functionOutput = requestHandler.handleRequest(functionInput, context);
        if (logger.isInfoEnabled())
            logger.info("Function processing took: {} millis", TimeUtils.timeTook(responseStart));

        logger.debug("Function output event conversion started...");
        final long outputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;
        final Object response = getFunctionOutput(functionOutput, function.getInput(), function.getOutput(), context);
        if (logger.isDebugEnabled()) {
            logger.debug("Function output event took: {} millis", TimeUtils.timeTook(outputStart));
            logger.debug("Function output event: {}", response);
        }

        return (response == null) ? null : converter.convertToJson(response);
    }

    @Override
    protected Object getFunctionOutput(Object funcOutValue,
                                       @NotNull Class<?> funcInputType,
                                       @NotNull Class<?> funcOutputType,
                                       @NotNull Context context) {
        if (funcOutValue instanceof APIGatewayProxyResponse
                || funcOutValue instanceof APIGatewayV2HTTPResponse
                || funcOutValue instanceof APIGatewayV2WebSocketResponse
                || funcOutValue instanceof LoadBalancerResponse)
            return funcOutValue;

        if (funcOutValue instanceof String)
            return funcOutValue;

        if (APIGatewayProxyEvent.class.isAssignableFrom(funcInputType)) {
            return new APIGatewayProxyResponse().setBody(funcOutValue).setHeaders(DEFAULT_HEADERS);
        } else if (APIGatewayV2HTTPEvent.class.isAssignableFrom(funcInputType)) {
            return new APIGatewayV2HTTPResponse().setBody(funcOutValue).setHeaders(DEFAULT_HEADERS);
        } else if (APIGatewayV2WebSocketEvent.class.isAssignableFrom(funcInputType)) {
            return new APIGatewayV2WebSocketResponse().setBody(funcOutValue).setHeaders(DEFAULT_HEADERS);
        } else if (LoadBalancerRequest.class.isAssignableFrom(funcInputType)) {
            return new LoadBalancerResponse().setBody(funcOutValue).setHeaders(DEFAULT_HEADERS);
        }

        return funcOutValue;
    }
}
