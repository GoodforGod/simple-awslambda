package io.aws.lambda.runtime.model.gateway;

import io.aws.lambda.runtime.model.AwsRequestContext;
import io.aws.lambda.runtime.utils.Base64Utils;
import io.aws.lambda.runtime.utils.StringUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 28.10.2020
 */
@TypeHint(value = { AwsGatewayRequest.class, }, accessType = { TypeHint.AccessType.ALL_PUBLIC })
@Introspected
public class AwsGatewayRequest {

    private final AwsRequestContext context;

    private final boolean isBase64Encoded;
    private final String version;
    private final String rawPath;
    private final String body;
    private String bodyDecoded;
    private final String rawQueryString;

    private final Map<String, String> headers;
    private final Map<String, String> queryStringParameters;
    private final Map<String, String> pathParameters;
    private final Map<String, String> stageVariables;
    private final List<String> cookies;

    private final AwsGatewayProxyRequestContext requestContext;

    protected AwsGatewayRequest(AwsRequestContext context, boolean isBase64Encoded, String version, String rawPath, String body,
                                String rawQueryString, Map<String, String> headers, Map<String, String> queryStringParameters,
                                Map<String, String> pathParameters, Map<String, String> stageVariables, List<String> cookies,
                                AwsGatewayProxyRequestContext requestContext) {
        this.context = context;
        this.isBase64Encoded = isBase64Encoded;
        this.version = version;
        this.rawPath = rawPath;
        this.body = body;
        this.rawQueryString = rawQueryString;
        this.headers = headers;
        this.queryStringParameters = queryStringParameters;
        this.pathParameters = pathParameters;
        this.stageVariables = stageVariables;
        this.cookies = cookies;
        this.requestContext = requestContext;
    }

    public static AwsGatewayRequestBuilder builder() {
        return new AwsGatewayRequestBuilder();
    }

    public AwsRequestContext getContext() {
        return context;
    }

    public boolean isBase64Encoded() {
        return isBase64Encoded;
    }

    public String getVersion() {
        return version;
    }

    public String getRawPath() {
        return rawPath;
    }

    public String getBody() {
        return body;
    }

    /**
     * @return body decoded from base64 if that was the case
     *         {@link #isBase64Encoded}
     */
    public String getBodyDecoded() {
        if (bodyDecoded == null)
            bodyDecoded = (isBase64Encoded) ? Base64Utils.decode(body) : body;

        return bodyDecoded;
    }

    public String getRawQueryString() {
        return rawQueryString;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryStringParameters() {
        return queryStringParameters;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters;
    }

    public Map<String, String> getStageVariables() {
        return stageVariables;
    }

    public List<String> getCookies() {
        return cookies;
    }

    public AwsGatewayProxyRequestContext getRequestContext() {
        return requestContext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AwsGatewayRequest that = (AwsGatewayRequest) o;
        return Objects.equals(context, that.context) && Objects.equals(isBase64Encoded, that.isBase64Encoded)
                && Objects.equals(version, that.version) && Objects.equals(rawPath, that.rawPath) && Objects.equals(body, that.body)
                && Objects.equals(rawQueryString, that.rawQueryString) && Objects.equals(headers, that.headers)
                && Objects.equals(queryStringParameters, that.queryStringParameters) && Objects.equals(pathParameters, that.pathParameters)
                && Objects.equals(stageVariables, that.stageVariables) && Objects.equals(cookies, that.cookies)
                && Objects.equals(requestContext, that.requestContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, isBase64Encoded, version, rawPath, body, rawQueryString, headers, queryStringParameters,
                pathParameters, stageVariables, cookies, requestContext);
    }

    @Override
    public String toString() {
        return "{\"context\":" + context +
                ", \"requestContext\":" + requestContext +
                ", \"body\":" + getBodyDecoded() +
                ", \"isBase64Encoded\":" + isBase64Encoded +
                ", \"version\":\"" + version +
                StringUtils.concatOrEmpty("\", \"rawPath\":\"", rawPath) +
                StringUtils.concatOrEmpty("\", \"rawQueryString\":\"", rawQueryString) +
                StringUtils.concatOrEmpty("\", \"headers\":\"", headers) +
                StringUtils.concatOrEmpty("\", \"queryStringParameters\":\"", queryStringParameters) +
                StringUtils.concatOrEmpty("\", \"pathParameters\":\"", pathParameters) +
                StringUtils.concatOrEmpty("\", \"stageVariables\":\"", stageVariables) +
                StringUtils.concatOrEmpty("\", \"cookies\":\"", cookies) + "\"}";
    }
}
