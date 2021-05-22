package io.aws.lambda.runtime.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import io.aws.lambda.runtime.Lambda;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.model.Pair;
import io.aws.lambda.runtime.utils.Base64Utils;
import io.aws.lambda.runtime.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * AWS Lambda Gateway Handler for handling requests coming from AWS Gateway
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Singleton
public class APIGatewayV2EventHandler extends DirectEventHandler {

    @Inject
    public APIGatewayV2EventHandler(Lambda function, Converter converter) {
        super(function, converter);
    }

    @Override
    public String handle(@NotNull String event, @NotNull Context context) {
        logger.debug("Gateway Request event conversion started...");
        final long requestStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;

        final Pair<Class, Class> funcArgs = getInterfaceGenericType(function);
        final String requestBody;
        if (APIGatewayV2HTTPEvent.class.isAssignableFrom(funcArgs.getRight())) {
            requestBody = event;
        } else {
            final APIGatewayV2HTTPEvent httpEvent = converter.convertToType(event, APIGatewayV2HTTPEvent.class);
            final String body = httpEvent.getBody();
            requestBody = (httpEvent.getIsBase64Encoded()) ? Base64Utils.decode(body) : body;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("API Gateway request event conversion took: {} millis", TimeUtils.timeTook(requestStart));
            logger.debug("API Gateway request event body: {}", requestBody);
        }

        final Object functionOutput = super.handle(requestBody, context);

        logger.debug("API Gateway response event conversion started...");
        final long outputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;
        final APIGatewayV2HTTPResponse responseEvent = getFunctionResponseEvent(functionOutput);
        if (logger.isDebugEnabled()) {
            logger.debug("API Gateway response event conversion took: {} millis", TimeUtils.timeTook(outputStart));
            logger.debug("API Gateway response event body: {}", responseEvent.getBody());
        }

        return converter.convertToJson(responseEvent);
    }

    private @NotNull APIGatewayV2HTTPResponse getFunctionResponseEvent(Object functionOutput) {
        if (functionOutput == null)
            return new APIGatewayV2HTTPResponse();

        if (functionOutput instanceof APIGatewayV2HTTPResponse)
            return (APIGatewayV2HTTPResponse) functionOutput;

        final APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
        final String body = (functionOutput instanceof String)
                ? (String) functionOutput
                : converter.convertToJson(functionOutput);

        response.setBody(body);
        return response;
    }
}
