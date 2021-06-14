package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.Introspected;
import org.jetbrains.annotations.NotNull;

@Introspected
public class Function {

    private final Class<?> input;
    private final Class<?> output;

    public Function(Class<?> input, Class<?> output) {
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
        return "[input=" + input + ", output=" + output + ']';
    }
}
