package io.goodforgod.aws.lambda.simple.micronaut.bean;

import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.aws.lambda.simple.handler.impl.BodyEventHandler;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.10.2021
 */
@Introspected
@Named(BodyEventHandler.QUALIFIER)
@Secondary
@Singleton
class MicronautBodyEventHandler extends BodyEventHandler {

    @Inject
    MicronautBodyEventHandler(Converter converter) {
        super(converter);
    }
}
