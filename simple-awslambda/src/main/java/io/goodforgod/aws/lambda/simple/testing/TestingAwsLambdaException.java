package io.goodforgod.aws.lambda.simple.testing;

/**
 * Testing exception
 *
 * @author Anton Kurako (GoodforGod)
 * @since 13.04.2022
 */
final class TestingAwsLambdaException extends RuntimeException {

    TestingAwsLambdaException(Throwable cause) {
        super(cause);
    }
}
