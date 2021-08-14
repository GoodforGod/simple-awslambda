package io.aws.lambda.simple.runtime.handler;

import org.jetbrains.annotations.NotNull;

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
