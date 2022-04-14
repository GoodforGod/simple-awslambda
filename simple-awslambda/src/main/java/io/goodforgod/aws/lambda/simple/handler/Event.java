package io.goodforgod.aws.lambda.simple.handler;

import com.amazonaws.services.lambda.runtime.Context;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

/**
 * Describes AWS Lambda event
 *
 * @author Anton Kurako (GoodforGod)
 * @since 12.04.2022
 */
public interface Event {

    @NotNull
    InputStream input();

    @NotNull
    Context context();
}
