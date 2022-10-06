package io.goodforgod.aws.lambda.simple.hint;

import io.goodforgod.graalvm.hint.annotation.InitializationHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 27.09.2021
 */
@InitializationHint(typeNames = { "io.goodforgod.aws.lambda.simple", }, value = InitializationHint.InitPhase.BUILD)
final class GraalVMHints {

    private GraalVMHints() {}
}
