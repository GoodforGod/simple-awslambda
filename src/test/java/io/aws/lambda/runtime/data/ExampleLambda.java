package io.aws.lambda.runtime.data;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 8.11.2020
 */
@Singleton
public class ExampleLambda implements RequestHandler<User, String> {

    @Override
    public @NotNull String handleRequest(@NotNull User user, @NotNull Context context) {
        return "response for " + user.getName();
    }
}
