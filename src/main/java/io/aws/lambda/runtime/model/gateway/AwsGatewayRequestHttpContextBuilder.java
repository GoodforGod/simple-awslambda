package io.aws.lambda.runtime.model.gateway;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 4.4.2021
 */
@TypeHint(value = { AwsGatewayRequestHttpContextBuilder.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Introspected
public class AwsGatewayRequestHttpContextBuilder {

    private String method;
    private String path;
    private String protocol;
    private String sourceIp;
    private String userAgent;

    public AwsGatewayRequestHttpContextBuilder setMethod(String method) {
        this.method = method;
        return this;
    }

    public AwsGatewayRequestHttpContextBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public AwsGatewayRequestHttpContextBuilder setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public AwsGatewayRequestHttpContextBuilder setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
        return this;
    }

    public AwsGatewayRequestHttpContextBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public AwsGatewayRequestHttpContext build() {
        return new AwsGatewayRequestHttpContext(method, path, protocol, sourceIp, userAgent);
    }
}
