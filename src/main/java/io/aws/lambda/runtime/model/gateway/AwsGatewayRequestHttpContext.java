package io.aws.lambda.runtime.model.gateway;

import io.aws.lambda.runtime.utils.StringUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;

import java.util.Objects;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 4.4.2021
 */
@TypeHint(value = { AwsGatewayRequestHttpContext.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Introspected
public class AwsGatewayRequestHttpContext {

    private final String method;
    private final String path;
    private final String protocol;
    private final String sourceIp;
    private final String userAgent;

    protected AwsGatewayRequestHttpContext(String method, String path, String protocol, String sourceIp, String userAgent) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.sourceIp = sourceIp;
        this.userAgent = userAgent;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AwsGatewayRequestHttpContext that = (AwsGatewayRequestHttpContext) o;
        return Objects.equals(method, that.method) && Objects.equals(path, that.path) && Objects.equals(protocol, that.protocol)
                && Objects.equals(sourceIp, that.sourceIp) && Objects.equals(userAgent, that.userAgent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path, protocol, sourceIp, userAgent);
    }

    @Override
    public String toString() {
        return "{\"method\":\"" + method +
                StringUtils.concatOrEmpty("\", path\":\"", path) +
                StringUtils.concatOrEmpty("\", protocol\":\"", protocol) +
                StringUtils.concatOrEmpty("\", sourceIp\":\"", sourceIp) +
                StringUtils.concatOrEmpty("\", userAgent\":\"", userAgent) + "\"}";
    }
}
