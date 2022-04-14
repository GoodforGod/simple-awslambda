package io.goodforgod.aws.lambda.simple.micronaut.bean;

import io.goodforgod.aws.lambda.simple.http.SimpleHttpClient;
import io.goodforgod.aws.lambda.simple.http.nativeclient.SimpleAwsRuntimeClient;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.04.2022
 */
@Introspected
@Named(SimpleAwsRuntimeClient.QUALIFIER)
@Secondary
@Singleton
class MicronautAwsRuntimeClient extends SimpleAwsRuntimeClient {

    @Inject
    MicronautAwsRuntimeClient(SimpleHttpClient httpClient) {
        super(httpClient);
    }
}
