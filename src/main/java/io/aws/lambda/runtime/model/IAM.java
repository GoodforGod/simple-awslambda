package io.aws.lambda.runtime.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;

import java.util.Objects;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 4.4.2021
 */
@TypeHint(value = { IAM.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Introspected
public class IAM {

    private final String accessKey;
    private final String accountId;
    private final String callerId;
    private final String cognitoIdentity;
    private final String principalOrgId;
    private final String userId;
    private final String userArn;

    protected IAM(String accessKey, String accountId, String callerId, String cognitoIdentity, String principalOrgId, String userId,
                  String userArn) {
        this.accessKey = accessKey;
        this.accountId = accountId;
        this.callerId = callerId;
        this.cognitoIdentity = cognitoIdentity;
        this.principalOrgId = principalOrgId;
        this.userId = userId;
        this.userArn = userArn;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCallerId() {
        return callerId;
    }

    public String getCognitoIdentity() {
        return cognitoIdentity;
    }

    public String getPrincipalOrgId() {
        return principalOrgId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserArn() {
        return userArn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IAM iam = (IAM) o;
        return Objects.equals(accessKey, iam.accessKey) && Objects.equals(accountId, iam.accountId)
                && Objects.equals(callerId, iam.callerId) && Objects.equals(cognitoIdentity, iam.cognitoIdentity)
                && Objects.equals(principalOrgId, iam.principalOrgId) && Objects.equals(userId, iam.userId)
                && Objects.equals(userArn, iam.userArn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessKey, accountId, callerId, cognitoIdentity, principalOrgId, userId, userArn);
    }

    @Override
    public String toString() {
        return "[accessKey='" + accessKey +
                "', accountId='" + accountId +
                "', callerId='" + callerId +
                "', cognitoIdentity='" + cognitoIdentity +
                "', principalOrgId='" + principalOrgId +
                "', userId='" + userId +
                "', userArn='" + userArn + "']";
    }
}
