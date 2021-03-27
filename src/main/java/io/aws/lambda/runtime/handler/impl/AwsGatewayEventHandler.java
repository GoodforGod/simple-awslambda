package io.aws.lambda.runtime.handler.impl;

import io.aws.lambda.runtime.Lambda;
import io.aws.lambda.runtime.convert.Converter;
import io.aws.lambda.runtime.logger.LambdaLogger;
import io.aws.lambda.runtime.model.AwsGatewayRequest;
import io.aws.lambda.runtime.model.AwsGatewayResponse;
import io.aws.lambda.runtime.model.AwsRequestContext;
import io.aws.lambda.runtime.model.Pair;
import io.aws.lambda.runtime.utils.TimeUtils;
import io.micronaut.core.annotation.TypeHint;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * AWS Lambda Gateway Handler for handling requests coming from AWS Gateway
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@TypeHint(
        value = { AwsGatewayRequest.class, AwsGatewayResponse.class },
        accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Singleton
public class AwsGatewayEventHandler extends AwsEventHandler {

    @Inject
    public AwsGatewayEventHandler(Lambda function, Converter converter, LambdaLogger logger) {
        super(function, converter, logger);
    }

    @Override
    public String handle(@NotNull String event, @NotNull AwsRequestContext context) {
        logger.debug("Gateway Request Event conversion started...");
        final long requestStart = TimeUtils.getTime();

        final Pair<Class, Class> funcArgs = getInterfaceGenericType(function);
        final String requestBody = (AwsRequestContext.class.isAssignableFrom(funcArgs.getRight()))
                ? event
                : converter.convertToType(event, AwsGatewayRequest.class).getBody();
        logger.debug("Gateway Request Event conversion took: %s", TimeUtils.timeSpent(requestStart));
        logger.debug("Gateway Request Event body: %s", requestBody);

        final Object functionOutput = super.handle(requestBody, context);

        logger.debug("Gateway Response Event conversion started...");
        final long outputStart = TimeUtils.getTime();
        final AwsGatewayResponse responseEvent = getFunctionResponseEvent(functionOutput);
        logger.debug("Gateway Response Event conversion took: %s", TimeUtils.timeSpent(outputStart));
        logger.debug("Gateway Response Event body: %s", responseEvent.getBody());
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
