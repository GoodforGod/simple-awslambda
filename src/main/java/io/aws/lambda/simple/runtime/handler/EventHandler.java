package io.aws.lambda.simple.runtime.handler;

import com.amazonaws.services.lambda.runtime.Context;
import org.jetbrains.annotations.NotNull;

/**
 * Handle event for AWS Lambda
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public interface EventHandler {

    /**
     * @param event   to handle
     * @param context of the request
     * @return response representation as string
     */
    String handle(@NotNull String event, @NotNull Context context);
}
