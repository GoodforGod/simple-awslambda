package io.aws.lambda.runtime.handler.impl;

import io.aws.lambda.runtime.Lambda;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.model.AwsRequestContext;
import io.aws.lambda.runtime.model.Pair;
import io.aws.lambda.runtime.model.gateway.AwsGatewayRequest;
import io.aws.lambda.runtime.model.gateway.AwsGatewayRequestBuilder;
import io.aws.lambda.runtime.model.gateway.AwsGatewayResponse;
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
public class GatewayEventHandler extends DirectEventHandler {

    @Inject
    public GatewayEventHandler(Lambda function, Converter converter) {
        super(function, converter);
    }

    @Override
    public String handle(@NotNull String event, @NotNull AwsRequestContext context) {
        logger.debug("Gateway Request Event conversion started...");
        final long requestStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;;

        final Pair<Class, Class> funcArgs = getInterfaceGenericType(function);
        final String requestBody = (AwsGatewayRequest.class.isAssignableFrom(funcArgs.getRight())
                || AwsGatewayRequestBuilder.class.isAssignableFrom(funcArgs.getRight()))
                        ? event
                        : converter.convertToType(event, AwsGatewayRequestBuilder.class).build().getBodyDecoded();

        if (logger.isDebugEnabled()) {
            logger.debug("Gateway Request Event conversion took: {} millis", TimeUtils.timeTook(requestStart));
            logger.debug("Gateway Request Event body: {}", requestBody);
        }

        final Object functionOutput = super.handle(requestBody, context);

        logger.debug("Gateway Response Event conversion started...");
        final long outputStart = (logger.isDebugEnabled()) ? TimeUtils.getTime() : 0;
        final AwsGatewayResponse responseEvent = getFunctionResponseEvent(functionOutput);
        if (logger.isDebugEnabled()) {
            logger.debug("Gateway Response Event conversion took: {} millis", TimeUtils.timeTook(outputStart));
            logger.debug("Gateway Response Event body: {}", responseEvent.getBody());
        }

        return converter.convertToJson(responseEvent);
    }

    private @NotNull AwsGatewayResponse getFunctionResponseEvent(Object functionOutput) {
        if (functionOutput == null)
            return new AwsGatewayResponse();

        if (functionOutput instanceof String)
            return new AwsGatewayResponse().setBody((String) functionOutput);

        if (functionOutput instanceof AwsGatewayResponse)
            return (AwsGatewayResponse) functionOutput;

        final String json = converter.convertToJson(functionOutput);
        return new AwsGatewayResponse().setBody(json);
    }
}
