package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 4.4.2021
 */
@TypeHint(value = { IAMBuilder.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Introspected
public class IAMBuilder {

    private String accessKey;
    private String accountId;
    private String callerId;
    private String cognitoIdentity;
    private String principalOrgId;
    private String userId;
    private String userArn;

    public IAMBuilder setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public IAMBuilder setAccountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public IAMBuilder setCallerId(String callerId) {
        this.callerId = callerId;
        return this;
    }

    public IAMBuilder setCognitoIdentity(String cognitoIdentity) {
        this.cognitoIdentity = cognitoIdentity;
        return this;
    }

    public IAMBuilder setPrincipalOrgId(String principalOrgId) {
        this.principalOrgId = principalOrgId;
        return this;
    }

    public IAMBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public IAMBuilder setUserArn(String userArn) {
        this.userArn = userArn;
        return this;
    }

    public IAM build() {
        return new IAM(accessKey, accountId, callerId, cognitoIdentity, principalOrgId, userId, userArn);
    }
}
