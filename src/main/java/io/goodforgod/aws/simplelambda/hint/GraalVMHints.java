package io.goodforgod.aws.simplelambda.hint;

import io.goodforgod.aws.lambda.events.*;
import io.goodforgod.aws.lambda.events.cognito.CognitoEvent;
import io.goodforgod.aws.lambda.events.cognito.CognitoUserPoolEvent;
import io.goodforgod.aws.lambda.events.dynamodb.DynamoDBEvent;
import io.goodforgod.aws.lambda.events.gateway.*;
import io.goodforgod.aws.lambda.events.kinesis.KinesisEvent;
import io.goodforgod.aws.lambda.events.s3.S3BatchEvent;
import io.goodforgod.aws.lambda.events.s3.S3EventNotification;
import io.goodforgod.aws.lambda.events.s3.S3ObjectLambdaEvent;
import io.goodforgod.graalvm.hint.annotation.InitializationHint;
import io.goodforgod.graalvm.hint.annotation.ResourceHint;
import io.goodforgod.graalvm.hint.annotation.TypeHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 27.09.2021
 */
@TypeHint(
        value = { TypeHint.AccessType.ALL_DECLARED },
        types = {
                APIGatewayProxyEvent.class,
                APIGatewayProxyEvent.RequestIdentity.class,
                APIGatewayProxyEvent.ProxyRequestContext.class,
                APIGatewayProxyResponse.class,
                APIGatewayV2HTTPEvent.class,
                APIGatewayV2HTTPEvent.RequestContext.class,
                APIGatewayV2HTTPEvent.RequestContext.Http.class,
                APIGatewayV2HTTPEvent.RequestContext.IAM.class,
                APIGatewayV2HTTPEvent.RequestContext.CognitoIdentity.class,
                APIGatewayV2HTTPEvent.RequestContext.Authorizer.class,
                APIGatewayV2HTTPEvent.RequestContext.Authorizer.JWT.class,
                APIGatewayV2HTTPResponse.class,
                APIGatewayV2WebSocketEvent.class,
                APIGatewayV2WebSocketEvent.RequestContext.class,
                APIGatewayV2WebSocketEvent.RequestIdentity.class,
                APIGatewayV2WebSocketResponse.class,

                BodyEvent.class,
                Base64BodyEvent.class,

                ActiveMQEvent.class,
                ActiveMQEvent.ActiveMQMessage.class,
                ActiveMQEvent.Destination.class,
                ConnectEvent.class,
                ConnectEvent.ContactData.class,
                ConnectEvent.CustomerEndpoint.class,
                ConnectEvent.Details.class,
                ConnectEvent.SystemEndpoint.class,
                KafkaEvent.class,
                KafkaEvent.KafkaEventRecord.class,
                KafkaEvent.TopicPartition.class,
                ScheduledEvent.class,
                SNSEvent.class,
                SNSEvent.SNS.class,
                SNSEvent.SNSRecord.class,
                SNSEvent.MessageAttribute.class,
                SQSEvent.class,
                SQSEvent.SQSMessage.class,
                SQSEvent.MessageAttribute.class,

                S3BatchEvent.class,
                S3BatchEvent.Job.class,
                S3BatchEvent.Task.class,
                S3EventNotification.class,
                S3EventNotification.S3Entity.class,
                S3EventNotification.S3BucketEntity.class,
                S3EventNotification.S3ObjectEntity.class,
                S3EventNotification.S3EventNotificationRecord.class,
                S3EventNotification.RequestParametersEntity.class,
                S3EventNotification.ResponseElementsEntity.class,
                S3EventNotification.UserIdentityEntity.class,
                S3ObjectLambdaEvent.class,
                S3ObjectLambdaEvent.UserRequest.class,
                S3ObjectLambdaEvent.UserIdentity.class,
                S3ObjectLambdaEvent.Configuration.class,
                S3ObjectLambdaEvent.GetObjectContext.class,

                KinesisEvent.class,
                KinesisEvent.Record.class,
                KinesisEvent.KinesisEventRecord.class,

                DynamoDBEvent.class,
                DynamoDBEvent.DynamodbStreamRecord.class,

                CognitoEvent.class,
                CognitoEvent.DatasetRecord.class,
                CognitoUserPoolEvent.class,
                CognitoUserPoolEvent.Request.class,
                CognitoUserPoolEvent.CallerContext.class,

        })
@InitializationHint(typeNames = {
        "io.goodforgod.aws.simplelambda",
        "io.goodforgod.gson.configuration",
        "io.goodforgod.slf4j.simplelogger",
        "io.goodforgod.net.uri",
        "com.google.gson",
        "org.slf4j.impl",
        "org.slf4j.LoggerFactory"
}, value = InitializationHint.InitPhase.BUILD)
@ResourceHint(patterns = {
        "gson.properties",
        "simplelogger.properties"
})
final class GraalVMHints {
}
