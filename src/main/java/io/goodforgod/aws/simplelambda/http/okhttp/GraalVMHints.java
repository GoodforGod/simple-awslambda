package io.goodforgod.aws.simplelambda.http.okhttp;

import io.goodforgod.graalvm.hint.annotation.InitializationHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.10.2021
 */
@InitializationHint(typeNames = {
        "okhttp3",
        "okio.Util",
        "okio.ByteString",
}, value = InitializationHint.InitPhase.BUILD)
final class GraalVMHints {
}
