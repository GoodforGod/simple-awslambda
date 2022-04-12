package io.goodforgod.aws.lambda.simple;

import com.amazonaws.services.lambda.runtime.Context;
import org.jetbrains.annotations.NotNull;

import java.awt.event.InputEvent;
import java.io.InputStream;

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
