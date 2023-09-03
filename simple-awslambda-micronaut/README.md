# Simple AWSLambda Micronaut

![GraalVM Enabled](https://img.shields.io/badge/GraalVM-Ready-orange?style=plastic)
[![Minimum required Java version](https://img.shields.io/badge/Java-17%2B-blue?logo=openjdk)](https://openjdk.org/projects/jdk/17/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.goodforgod/simple-awslambda-micronaut/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.goodforgod/simple-awslambda-micronaut)
[![GitHub Action](https://github.com/goodforgod/simple-awslambda/workflows/CI%20Master/badge.svg)](https://github.com/GoodforGod/simple-awslambda/actions?query=workflow%3ACI+Master)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_simple-awslambda&metric=coverage)](https://sonarcloud.io/dashboard?id=GoodforGod_simple-awslambda)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_simple-awslambda&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=GoodforGod_simple-awslambda)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_simple-awslambda&metric=ncloc)](https://sonarcloud.io/dashboard?id=GoodforGod_simple-awslambda)

Fast, Lightweight and GraalVM oriented AWS Lambda Runtime with Micronaut.

Simple AWSLambda extension that allow to use Micronaut IoC and all Micronaut modules.

## Dependency :rocket:

**Gradle**
```groovy
implementation "io.goodforgod:simple-awslambda-micronaut:1.0.0"
```

**Maven**
```xml
<dependency>
    <groupId>io.goodforgod</groupId>
    <artifactId>simple-awslambda-micronaut</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Micronaut Dependencies

You should add such dependencies to project:

```groovy
annotationProcessor "io.micronaut:micronaut-inject-java"
annotationProcessor "io.micronaut:micronaut-graal"

compileOnly "org.graalvm.nativeimage:svm"
```

## Getting Started

When using Micronaut extension you don't need to manually create Entrypoint, cause extension provides
two entrypoints with proper initialization.

You simply need to define *RequestHandler* and thats it. All things will be injected via DI like this is your normal service.

```java
@Singleton
public class HelloWorldLambda implements RequestHandler<Request, Response> {

    @ReflectionHint
    public record Request(String name) {}

    @ReflectionHint
    public record Response(String id, String message) {}

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Response handleRequest(Request request, Context context) {
        logger.info("Processing User with name: {}", request.name());
        return new Response(UUID.randomUUID().toString(), "Hello - " + request.name());
    }
}
```

You can check [examples](https://github.com/GoodforGod/simple-awslambda-examples) for more details and context.

## Configuration

When using *simple-logger* it is still configured via *simplelogger.property* file.

### GSON

GSON configuration is provided via *application.yaml* as other Micronaut configurations, nothing different from Micronaut service configs.

## Entrypoint

There two runtime entrypoints available to extend:
- *MicronautInputLambdaEntrypoint* - entrypoint for direct event that should be propagated for processing.
- *MicronautBodyLambdaEntrypoint* - entrypoint for body events (like APIGatewayV2HTTPEvent, APIGatewayV2WebSocketEvent), this entrypoint extracts Body from response and pass it to RequestHandler directly.

You can also choose what *RequestHandler* will be used for event processing via AWS environment variable *_HANDLER*.
