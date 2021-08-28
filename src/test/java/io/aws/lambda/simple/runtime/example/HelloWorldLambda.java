package io.aws.lambda.simple.runtime.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 31.07.2021
 */
@Introspected
@Singleton
public class HelloWorldLambda implements RequestHandler<Request, Response> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Response handleRequest(Request request, Context context) {
        logger.info("Processing User with name: {}", request.getName());
        return new Response(UUID.randomUUID().toString(), "Hello - " + request.getName());
    }
}
