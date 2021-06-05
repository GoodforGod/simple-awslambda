package io.aws.lambda.runtime.invoker;

import io.aws.lambda.events.BodyEncodedEvent;
import io.aws.lambda.events.BodyEvent;
import io.aws.lambda.events.KafkaEvent;
import io.aws.lambda.events.SNSEvent;
import io.aws.lambda.events.SQSEvent;
import io.aws.lambda.events.dynamodb.AttributeValue;
import io.aws.lambda.events.dynamodb.DynamodbEvent;
import io.aws.lambda.events.dynamodb.DynamodbTimeWindowEvent;
import io.aws.lambda.events.dynamodb.Identity;
import io.aws.lambda.events.dynamodb.StreamRecord;
import io.aws.lambda.events.gateway.APIGatewayCustomAuthorizerEvent;
import io.aws.lambda.events.gateway.APIGatewayProxyEvent;
import io.aws.lambda.events.gateway.APIGatewayProxyResponse;
import io.aws.lambda.events.gateway.APIGatewayV2CustomAuthorizerEvent;
import io.aws.lambda.events.gateway.APIGatewayV2HTTPEvent;
import io.aws.lambda.events.gateway.APIGatewayV2HTTPResponse;
import io.aws.lambda.events.gateway.APIGatewayV2WebSocketEvent;
import io.aws.lambda.events.gateway.APIGatewayV2WebSocketResponse;
import io.aws.lambda.events.s3.S3BatchEvent;
import io.aws.lambda.events.s3.S3BatchResponse;
import io.aws.lambda.events.s3.S3Event;
import io.aws.lambda.events.s3.S3EventNotification;
import io.aws.lambda.events.s3.S3ObjectLambdaEvent;
import io.aws.lambda.runtime.LambdaContext;
import io.aws.lambda.runtime.context.RuntimeContext;
import io.aws.lambda.runtime.config.RuntimeVariables;
import io.aws.lambda.runtime.error.ContextException;
import io.aws.lambda.runtime.handler.EventHandler;
import io.aws.lambda.runtime.http.AwsHttpClient;
import io.aws.lambda.runtime.http.AwsHttpResponse;
import io.aws.lambda.runtime.http.impl.NativeAwsHttpClient;
import io.aws.lambda.runtime.utils.StringUtils;
import io.aws.lambda.runtime.utils.TimeUtils;
import io.micronaut.core.annotation.TypeHint;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.function.Supplier;

/**
 * Implementation of AWS Lambda invocation pipeline
 *
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@TypeHint(
        accessType = { TypeHint.AccessType.ALL_DECLARED_CONSTRUCTORS, TypeHint.AccessType.ALL_PUBLIC },
        value = {
                DynamodbEvent.class,
                DynamodbEvent.DynamodbStreamRecord.class,
                DynamodbTimeWindowEvent.class,
                AttributeValue.class,
                Identity.class,
                StreamRecord.class,

                APIGatewayCustomAuthorizerEvent.class,
                APIGatewayCustomAuthorizerEvent.Identity.class,
                APIGatewayCustomAuthorizerEvent.RequestContext.class,
                APIGatewayV2CustomAuthorizerEvent.class,
                APIGatewayV2CustomAuthorizerEvent.Http.class,
                APIGatewayV2CustomAuthorizerEvent.RequestContext.class,
                APIGatewayProxyEvent.class,
                APIGatewayProxyEvent.RequestIdentity.class,
                APIGatewayProxyEvent.ProxyRequestContext.class,
                APIGatewayProxyResponse.class,
                APIGatewayV2HTTPEvent.class,
                APIGatewayV2HTTPEvent.RequestContext.class,
                APIGatewayV2HTTPEvent.RequestContext.Http.class,
                APIGatewayV2HTTPEvent.RequestContext.CognitoIdentity.class,
                APIGatewayV2HTTPEvent.RequestContext.Authorizer.class,
                APIGatewayV2HTTPEvent.RequestContext.Authorizer.JWT.class,
                APIGatewayV2HTTPResponse.class,
                APIGatewayV2WebSocketEvent.class,
                APIGatewayV2WebSocketEvent.RequestContext.class,
                APIGatewayV2WebSocketEvent.RequestIdentity.class,
                APIGatewayV2WebSocketResponse.class,

                S3Event.class,
                S3BatchEvent.class,
                S3BatchEvent.Job.class,
                S3BatchEvent.Task.class,
                S3BatchResponse.class,
                S3BatchResponse.Result.class,
                S3BatchResponse.ResultCode.class,
                S3EventNotification.class,
                S3EventNotification.RequestParametersEntity.class,
                S3EventNotification.ResponseElementsEntity.class,
                S3EventNotification.S3BucketEntity.class,
                S3EventNotification.S3Entity.class,
                S3EventNotification.S3EventNotificationRecord.class,
                S3EventNotification.S3ObjectEntity.class,
                S3EventNotification.UserIdentityEntity.class,
                S3ObjectLambdaEvent.class,
                S3ObjectLambdaEvent.Configuration.class,
                S3ObjectLambdaEvent.GetObjectContext.class,
                S3ObjectLambdaEvent.UserIdentity.class,
                S3ObjectLambdaEvent.UserRequest.class,

                KafkaEvent.class,
                KafkaEvent.KafkaEventRecord.class,
                KafkaEvent.TopicPartition.class,
                SNSEvent.class,
                SNSEvent.SNS.class,
                SNSEvent.SNSRecord.class,
                SNSEvent.MessageAttribute.class,
                SQSEvent.class,
                SQSEvent.MessageAttribute.class,
                SQSEvent.SQSMessage.class,
                BodyEvent.class,
                BodyEncodedEvent.class
        })
public class AwsEventInvoker {

    private static final String INIT_ERROR = "/2018-06-01/runtime/init/error";
    private static final String INVOCATION_URI = "/2018-06-01/runtime/invocation/";
    private static final String INVOCATION_NEXT_URI = INVOCATION_URI + "next";

    /**
     * @param contextType class type to instantiate
     * @param handlerType class type to instantiate from context
     */
    public void invoke(@NotNull Class<? extends RuntimeContext> contextType,
                       @NotNull Class<? extends EventHandler> handlerType) {
        try (RuntimeContext context = getInstance(contextType)) {
            invoke(() -> context, handlerType);
        } catch (Exception e) {
            e.printStackTrace();
            final AwsHttpClient httpClient = new NativeAwsHttpClient();
            final URI apiEndpoint = getRuntimeApiEndpoint();
            httpClient.postAndForget(apiEndpoint.resolve(INIT_ERROR), getErrorResponse(e));
        }
    }

    /**
     * @param contextSupplier runtime instance supplier
     * @param handlerType     class type to instantiate from contextSupplier
     */
    public void invoke(@NotNull Supplier<RuntimeContext> contextSupplier,
                       @NotNull Class<? extends EventHandler> handlerType) {
        final URI apiEndpoint = getRuntimeApiEndpoint();
        final Logger logger = LoggerFactory.getLogger(getClass());
        final long contextStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;

        try (final RuntimeContext context = contextSupplier.get()) {
            final EventHandler eventHandler = context.getBean(handlerType);
            final AwsHttpClient httpClient = context.getBean(AwsHttpClient.class);
            if (logger.isInfoEnabled()) {
                logger.info("Context startup took: {} millis", TimeUtils.timeTook(contextStart));
                logger.debug("AWS Runtime URI: {}", apiEndpoint);
            }

            final URI invocationUri = getInvocationNextUri(apiEndpoint);
            logger.debug("AWS Runtime Event provider at: {}", invocationUri);

            while (!Thread.currentThread().isInterrupted()) {
                final AwsHttpResponse httpRequest = httpClient.get(invocationUri);
                if (StringUtils.isEmpty(httpRequest.body()))
                    throw new IllegalArgumentException("Request body is not present!");

                final LambdaContext requestContext = LambdaContext.ofHeadersMulti(httpRequest.headers());
                if (StringUtils.isEmpty(requestContext.getAwsRequestId()))
                    throw new IllegalArgumentException("Request ID is not present!");

                if (logger.isDebugEnabled()) {
                    logger.debug("AWS Request Event received with {}", requestContext);
                    httpRequest.headers().forEach((k, v) -> logger.debug("Request header: {} - {}", k, v));
                }

                try {
                    final String responseEvent = eventHandler.handle(httpRequest.body(), requestContext);
                    final URI responseUri = getInvocationResponseUri(apiEndpoint, requestContext.getAwsRequestId());
                    logger.debug("Responding to AWS invocation started: {}", responseUri);

                    final long respondingStart = (logger.isInfoEnabled()) ? TimeUtils.getTime() : 0;
                    final AwsHttpResponse awsResponse = httpClient.post(responseUri, responseEvent);
                    if (logger.isInfoEnabled()) {
                        logger.info("Responding to AWS invocation took: {} millis", TimeUtils.timeTook(respondingStart));
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("AWS invocation response: Http Code '{}' and Body: {}",
                                awsResponse.code(), awsResponse.body());
                    }
                } catch (Exception e) {
                    logger.error("Invocation error occurred", e);
                    final URI uri = getInvocationErrorUri(apiEndpoint, requestContext.getAwsRequestId());
                    httpClient.postAndForget(uri, getErrorResponse(e));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            final AwsHttpClient httpClient = new NativeAwsHttpClient();
            httpClient.postAndForget(apiEndpoint.resolve(INIT_ERROR), getErrorResponse(e));
        }
    }

    private static <T> T getInstance(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ContextException("Context can not be instantiated through constructor due to: " + e.getMessage());
        }
    }

    /**
     * Retrieves an invocation event.
     *
     * @param apiEndpoint of api URI
     * @return invocation response uri
     * @see <a href=
     *      "https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
     */
    private static URI getInvocationNextUri(URI apiEndpoint) {
        return apiEndpoint.resolve(INVOCATION_NEXT_URI);
    }

    /**
     * Sends an invocation response to Lambda.
     *
     * @param apiEndpoint of api URI
     * @param requestId   of request
     * @return invocation response uri
     * @see <a href=
     *      "https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
     */
    private static URI getInvocationResponseUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve(INVOCATION_URI + requestId + "/response");
    }

    /**
     * If the function returns an error, the runtime formats the error into a JSON
     * document, and posts it to the invocation error path.
     *
     * @param apiEndpoint of api URI
     * @param requestId   of request
     * @return invocation response uri
     * @see <a href=
     *      "https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html">https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html</a>
     */
    private static URI getInvocationErrorUri(URI apiEndpoint, String requestId) {
        return apiEndpoint.resolve(INVOCATION_URI + requestId + "/error");
    }

    private static URI getRuntimeApiEndpoint() {
        final String runtimeApiEndpoint = System.getenv(RuntimeVariables.AWS_LAMBDA_RUNTIME_API);
        if (StringUtils.isEmpty(runtimeApiEndpoint))
            throw new IllegalStateException("Missing '" + RuntimeVariables.AWS_LAMBDA_RUNTIME_API
                    + "' environment variable. Custom runtime can only be run within AWS Lambda environment.");

        return URI.create("http://" + runtimeApiEndpoint);
    }

    private static String getErrorResponse(Throwable e) {
        return "{\"errorMessage\":\"" + e.getMessage() + "\", \"errorType\":\"" + e.getClass().getSimpleName() + "\"}";
    }
}
