package io.goodforgod.aws.lambda.simple.config;

import io.goodforgod.aws.lambda.simple.handler.EventHandler;
import org.slf4j.event.Level;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 09.10.2021
 */
public final class SimpleLambdaContextVariables {

    private SimpleLambdaContextVariables() {}

    /**
     * The handler location configured on the function.
     */
    public static final String REQUEST_HANDLER = "_HANDLER";

    /**
     * The {@link EventHandler} qualifier configured on the function.
     */
    public static final String EVENT_HANDLER = "_HANDLER_EVENT";

    /**
     * Default logging level for {@link org.slf4j.Logger} with values from {@link Level}
     */
    public static final String LOGGING_LEVEL = "SIMPLE_LAMBDA_DEFAULT_LOG_LEVEL";
}
