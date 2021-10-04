package io.goodforgod.aws.lambda.simple.handler;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Represent {@link RequestHandler} type errasure
 *
 * @author Anton Kurako (GoodforGod)
 * @since 14.06.2021
 */
public final class RequestFunction {

    private final Class<?> input;
    private final Class<?> output;

    public RequestFunction(Class<?> input, Class<?> output) {
        this.input = input;
        this.output = output;
    }

    public @NotNull Class<?> getInput() {
        return input;
    }

    public @NotNull Class<?> getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return "[input=" + input.getName() + ", output=" + output.getName() + ']';
    }
}
