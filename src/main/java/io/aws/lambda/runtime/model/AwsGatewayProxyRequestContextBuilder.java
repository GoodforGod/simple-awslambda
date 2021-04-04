package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 4.4.2021
 */
@TypeHint(value = { AwsGatewayProxyRequestContextBuilder.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Introspected
public class AwsGatewayProxyRequestContextBuilder {

    private String accountId;
    private String apiId;
    private String domainName;
    private String domainPrefix;
    private String requestId;
    private String routeKey;
    private String stage;
    private String time;
    private long timeEpoch;

    private AwsGatewayRequestHttpContextBuilder http;
    private AwsGatewayRequestIdentityBuilder authorizer;

    public AwsGatewayProxyRequestContextBuilder setAccountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public AwsGatewayProxyRequestContextBuilder setApiId(String apiId) {
        this.apiId = apiId;
        return this;
    }

    public AwsGatewayProxyRequestContextBuilder setDomainName(String domainName) {
        this.domainName = domainName;
        return this;
    }

    public AwsGatewayProxyRequestContextBuilder setDomainPrefix(String domainPrefix) {
        this.domainPrefix = domainPrefix;
        return this;
    }

    public AwsGatewayProxyRequestContextBuilder setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public AwsGatewayProxyRequestContextBuilder setRouteKey(String routeKey) {
        this.routeKey = routeKey;
        return this;
    }

    public AwsGatewayProxyRequestContextBuilder setStage(String stage) {
        this.stage = stage;
        return this;
    }

    public AwsGatewayProxyRequestContextBuilder setTime(String time) {
        this.time = time;
        return this;
    }

    public AwsGatewayProxyRequestContextBuilder setTimeEpoch(long timeEpoch) {
        this.timeEpoch = timeEpoch;
        return this;
    }

    public AwsGatewayProxyRequestContextBuilder setAuthorizer(AwsGatewayRequestIdentityBuilder authorizer) {
        this.authorizer = authorizer;
        return this;
    }

    public AwsGatewayProxyRequestContextBuilder setHttp(AwsGatewayRequestHttpContextBuilder http) {
        this.http = http;
        return this;
    }

    public AwsGatewayProxyRequestContext build() {
        return new AwsGatewayProxyRequestContext(accountId, apiId, domainName, domainPrefix, requestId, routeKey, stage,
                time, timeEpoch, (http == null) ? null : http.build(), (authorizer == null) ? null : authorizer.build());
    }
}
