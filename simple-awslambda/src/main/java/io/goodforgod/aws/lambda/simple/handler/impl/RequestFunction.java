package io.goodforgod.aws.lambda.simple.handler.impl;

import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Represent {@link RequestHandler} type errasure
 *
 * @author Anton Kurako (GoodforGod)
 * @since 14.06.2021
 */
record RequestFunction(Class<?> input, Class<?> output) {

    @Override
    public String toString() {
        return "[input=" + input.getName() + ", output=" + output.getName() + ']';
    }
}
