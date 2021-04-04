package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;

import java.util.Map;
import java.util.Objects;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 4.4.2021
 */
@TypeHint(value = { AwsGatewayRequestIdentity.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Introspected
public class AwsGatewayRequestIdentity {

    private final IAM iam;
    private final Map<String, Object> jwt;

    public AwsGatewayRequestIdentity(IAM iam, Map<String, Object> jwt) {
        this.iam = iam;
        this.jwt = jwt;
    }

    public IAM getIam() {
        return iam;
    }

    public Map<String, Object> getJwt() {
        return jwt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AwsGatewayRequestIdentity that = (AwsGatewayRequestIdentity) o;
        return Objects.equals(iam, that.iam) && Objects.equals(jwt, that.jwt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iam, jwt);
    }

    @Override
    public String toString() {
        return (jwt == null)
                ? "{\"IAM\":" + iam + "}"
                : "{\"IAM\":" + iam + ", \"JWT\":\"" + jwt + "\"}";
    }
}
