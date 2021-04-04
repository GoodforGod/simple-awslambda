package io.aws.lambda.runtime.model;

import io.aws.lambda.runtime.utils.StringUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;

import java.util.Objects;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 4.4.2021
 */
@TypeHint(value = { AwsGatewayProxyRequestContext.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Introspected
public class AwsGatewayProxyRequestContext {

    private final String accountId;
    private final String apiId;
    private final String domainName;
    private final String domainPrefix;
    private final String requestId;
    private final String routeKey;
    private final String stage;
    private final String time;
    private final long timeEpoch;

    private final AwsGatewayRequestHttpContext http;
    private final AwsGatewayRequestIdentity authorizer;

    public AwsGatewayProxyRequestContext(String accountId, String apiId, String domainName, String domainPrefix, String requestId,
                                         String routeKey, String stage, String time, long timeEpoch, AwsGatewayRequestHttpContext http,
                                         AwsGatewayRequestIdentity authorizer) {
        this.accountId = accountId;
        this.apiId = apiId;
        this.domainName = domainName;
        this.domainPrefix = domainPrefix;
        this.requestId = requestId;
        this.routeKey = routeKey;
        this.stage = stage;
        this.time = time;
        this.timeEpoch = timeEpoch;
        this.http = http;
        this.authorizer = authorizer;
    }

    public static AwsGatewayProxyRequestContextBuilder builder() {
        return new AwsGatewayProxyRequestContextBuilder();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getApiId() {
        return apiId;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getDomainPrefix() {
        return domainPrefix;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public String getStage() {
        return stage;
    }

    public String getTime() {
        return time;
    }

    public long getTimeEpoch() {
        return timeEpoch;
    }

    public AwsGatewayRequestIdentity getAuthorizer() {
        return authorizer;
    }

    public AwsGatewayRequestHttpContext getHttp() {
        return http;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AwsGatewayProxyRequestContext that = (AwsGatewayProxyRequestContext) o;
        return timeEpoch == that.timeEpoch && Objects.equals(accountId, that.accountId) && Objects.equals(apiId, that.apiId)
                && Objects.equals(domainName, that.domainName) && Objects.equals(domainPrefix, that.domainPrefix)
                && Objects.equals(requestId, that.requestId) && Objects.equals(routeKey, that.routeKey) && Objects.equals(stage, that.stage)
                && Objects.equals(time, that.time) && Objects.equals(authorizer, that.authorizer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, apiId, domainName, domainPrefix, requestId, routeKey, stage, time, timeEpoch, authorizer);
    }

    @Override
    public String toString() {
        return '[' + StringUtils.concatOrEmpty( "accountId='", accountId) +
                StringUtils.concatOrEmpty("', apiId='", apiId) +
                        StringUtils.concatOrEmpty("', domainName='", domainName) +
                                StringUtils.concatOrEmpty( "', domainPrefix='", domainPrefix) +
                                        StringUtils.concatOrEmpty( "', requestId='" ,requestId) +
                                                StringUtils.concatOrEmpty( "', routeKey='", routeKey) +
                                                        StringUtils.concatOrEmpty( "', stage='", stage) +
                                                                StringUtils.concatOrEmpty( "', time='" + time +
                                                                        StringUtils.concatOrEmpty( "', timeEpoch=" + timeEpoch +
                                                                                StringUtils.concatOrEmpty( ", identity=" + authorizer + ']';
    }
}
