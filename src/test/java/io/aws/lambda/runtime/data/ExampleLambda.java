package io.aws.lambda.runtime.data;

import com.amazonaws.services.lambda.runtime.Context;
import io.aws.lambda.runtime.Lambda;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 8.11.2020
 */
@Singleton
public class ExampleLambda implements Lambda<String, User> {

    @Override
    public @NotNull String handle(@NotNull User user, @NotNull Context context) {
        return "response for " + user.getName();
    }
}
