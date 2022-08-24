package io.goodforgod.aws.lambda.simple.hint;

import io.goodforgod.aws.lambda.events.*;
import io.goodforgod.aws.lambda.events.dynamodb.DynamoDBEvent;
import io.goodforgod.aws.lambda.events.gateway.*;
import io.goodforgod.aws.lambda.events.kinesis.KinesisEvent;
import io.goodforgod.aws.lambda.events.s3.S3BatchEvent;
import io.goodforgod.graalvm.hint.annotation.InitializationHint;
import io.goodforgod.graalvm.hint.annotation.ReflectionHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 27.09.2021
 */
@InitializationHint( typeNames = { "io.goodforgod.aws.lambda.simple", }, value = InitializationHint.InitPhase.BUILD)
final class GraalVMHints {

    private GraalVMHints() {}
}
