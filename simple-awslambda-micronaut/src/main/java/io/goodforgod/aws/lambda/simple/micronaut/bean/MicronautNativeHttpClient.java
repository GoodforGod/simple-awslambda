package io.goodforgod.aws.lambda.simple.micronaut.bean;

import io.goodforgod.aws.lambda.simple.http.nativeclient.NativeHttpClient;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.10.2021
 */
@Introspected
@Named(NativeHttpClient.QUALIFIER)
@Secondary
@Singleton
class MicronautNativeHttpClient extends NativeHttpClient {

    MicronautNativeHttpClient() {
        super();
    }
}
