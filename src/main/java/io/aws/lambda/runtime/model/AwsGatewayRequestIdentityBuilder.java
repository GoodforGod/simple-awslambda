package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;

import java.util.Map;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 4.4.2021
 */
@TypeHint(value = { AwsGatewayRequestIdentityBuilder.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Introspected
public class AwsGatewayRequestIdentityBuilder {

    private IAMBuilder iam;
    private Map<String, Object> jwt;

    public AwsGatewayRequestIdentityBuilder setIam(IAMBuilder iam) {
        this.iam = iam;
        return this;
    }

    public AwsGatewayRequestIdentityBuilder setJwt(Map<String, Object> jwt) {
        this.jwt = jwt;
        return this;
    }

    public AwsGatewayRequestIdentity build() {
        return new AwsGatewayRequestIdentity((iam == null) ? null : iam.build(), jwt);
    }
}
