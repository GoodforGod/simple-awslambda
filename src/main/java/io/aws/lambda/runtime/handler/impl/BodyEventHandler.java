package io.aws.lambda.runtime.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import io.aws.lambda.events.BodyBase64Event;
import io.aws.lambda.events.BodyEvent;
import io.aws.lambda.events.gateway.*;
import io.aws.lambda.events.system.LoadBalancerRequest;
import io.aws.lambda.events.system.LoadBalancerResponse;
import io.aws.lambda.runtime.Lambda;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.model.Pair;
import io.aws.lambda.runtime.utils.TimeUtils;
import io.micronaut.core.annotation.Introspected;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

import static io.aws.lambda.runtime.http.impl.NativeAwsHttpClient.CONTENT_TYPE;
import static io.aws.lambda.runtime.http.impl.NativeAwsHttpClient.MEDIA_TYPE_JSON;

/**
 * AWS Lambda Gateway Handler for handling requests coming from events that
 * contains body.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Introspected
@Singleton
public class BodyEventHandler extends AbstractEventHandler implements EventHandler {

    private static final Map<String, String> DEFAULT_HEADERS = Map.of(CONTENT_TYPE, MEDIA_TYPE_JSON);

    private final Lambda function;

    @Inject
    public BodyEventHandler(Lambda function, Converter converter) {
        super(converter);
        this.function = function;
    }

    @SuppressWarnings("unchecked")
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
            logger.debug("API Event conversion took: {} millis", TimeUtils.timeTook(requestStart));
            logger.debug("API Event body: {}", eventBody);
        }

        logger.debug("Function input conversion started...");
        final long inputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;
        final Object functionInput = getFunctionInput(funcArgs.getRight(), eventBody, context);
        if (logger.isDebugEnabled()) {
            logger.debug("Function input conversion took: {} millis", TimeUtils.timeTook(inputStart));
            logger.debug("Function input: {}", functionInput);
        }

        logger.debug("Function processing started...");
        final long responseStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;
        final Object functionOutput = function.handle(functionInput, context);
        if (logger.isInfoEnabled())
            logger.info("Function processing took: {} millis", TimeUtils.timeTook(responseStart));

        logger.debug("API Event conversion started...");
        final long outputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;
        final Object response = getFunctionResponseEvent(functionOutput, funcArgs.getLeft());
        if (logger.isDebugEnabled()) {
            logger.debug("API Event conversion took: {} millis", TimeUtils.timeTook(outputStart));
            logger.debug("API Event body: {}", response);
        }

        return converter.convertToJson(response);
    }

    private Object getFunctionResponseEvent(Object funcOutValue, Class<?> funcInputArg) {
        if (funcOutValue instanceof APIGatewayProxyResponse
                || funcOutValue instanceof APIGatewayV2HTTPResponse
                || funcOutValue instanceof APIGatewayV2WebSocketResponse
                || funcOutValue instanceof LoadBalancerResponse)
            return funcOutValue;

        if (funcOutValue instanceof String)
            return funcOutValue;

        if (APIGatewayProxyEvent.class.isAssignableFrom(funcInputArg)) {
            return new APIGatewayProxyResponse().setBody(funcOutValue).setHeaders(DEFAULT_HEADERS);
        } else if (APIGatewayV2HTTPEvent.class.isAssignableFrom(funcInputArg)) {
            return new APIGatewayV2HTTPResponse().setBody(funcOutValue).setHeaders(DEFAULT_HEADERS);
        } else if (APIGatewayV2WebSocketEvent.class.isAssignableFrom(funcInputArg)) {
            return new APIGatewayV2WebSocketResponse().setBody(funcOutValue).setHeaders(DEFAULT_HEADERS);
        } else if (LoadBalancerRequest.class.isAssignableFrom(funcInputArg)) {
            return new LoadBalancerResponse().setBody(funcOutValue).setHeaders(DEFAULT_HEADERS);
        }

        return funcOutValue;
    }
}
