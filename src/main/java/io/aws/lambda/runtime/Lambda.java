package io.aws.lambda.runtime;

import org.jetbrains.annotations.NotNull;

/**
 * Lambda function contract to implement
 *
 * @param <I> type of input
 * @param <O> type of output
 */
public interface Lambda<O, I> {

    /**
     * @param request to process
     * @return output in specified type or null if no output is desired
     */
    O handle(@NotNull I request);
}
