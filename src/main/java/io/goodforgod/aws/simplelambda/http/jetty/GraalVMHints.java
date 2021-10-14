package io.goodforgod.aws.simplelambda.http.jetty;

import io.goodforgod.graalvm.hint.annotation.InitializationHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 15.10.2021
 */
@InitializationHint(typeNames = {
        "org.eclipse.jetty.client.HttpClient",
        "org.eclipse.jetty.client.AbstractHttpClientTransport",
        "org.eclipse.jetty.client.http.HttpClientConnectionFactory",
        "org.eclipse.jetty.http.HttpCompliance",
        "org.eclipse.jetty.http2.client.HTTP2Client",
        "org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2",
        "org.eclipse.jetty.io.ClientConnector",
        "org.eclipse.jetty.util.component",
        "org.eclipse.jetty.util.Jetty",
        "org.eclipse.jetty.util.StringUtil",
        "org.eclipse.jetty.util.TypeUtil",
        "org.eclipse.jetty.util.component.AbstractLifeCycle",
        "org.eclipse.jetty.util.ProcessorUtils"
}, value = InitializationHint.InitPhase.BUILD)
final class GraalVMHints {
}
