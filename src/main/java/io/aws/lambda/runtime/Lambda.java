package io.aws.lambda.runtime;

import com.amazonaws.services.lambda.runtime.Context;
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
     * @param context of request execution
     * @return output in specified type or null if no output is desired
     */
    O handle(@NotNull I request, @NotNull Context context);
}
