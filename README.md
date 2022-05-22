# Simple AWSLambda

![GraalVM Enabled](https://img.shields.io/badge/GraalVM-Ready-orange?style=plastic)
[![GitHub Action](https://github.com/goodforgod/simple-awslambda-project/workflows/Java%20CI/badge.svg)](https://github.com/GoodforGod/simple-awslambda-project/actions?query=workflow%3A%22Java+CI%22)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_simple-awslambda-project&metric=coverage)](https://sonarcloud.io/dashboard?id=GoodforGod_simple-awslambda-project)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_simple-awslambda-project&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=GoodforGod_simple-awslambda-project)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_simple-awslambda-project&metric=ncloc)](https://sonarcloud.io/dashboard?id=GoodforGod_simple-awslambda-project)

Fast, Lightweight and GraalVM optimized AWS Lambda Runtime.

Simple and efficient way to build Native Java Serverless executables for AWS Lambda.

## Dependency :rocket:

[**Gradle**](https://mvnrepository.com/artifact/io.goodforgod/graalvm-hint-processor)
```groovy
implementation "io.goodforgod:simple-awslambda:0.29.0-SNAPSHOT"
```

[**Maven**](https://mvnrepository.com/artifact/io.goodforgod/graalvm-hint-processor)
```xml
<dependency>
    <groupId>io.goodforgod</groupId>
    <artifactId>simple-awslambda</artifactId>
    <version>0.29.0-SNAPSHOT</version>
</dependency>
```

## Getting Started

### Examples

Here is repository with many examples of [simple-awslambda](https://github.com/GoodforGod/simple-awslambda-examples) for hello-world, DynamoDB, AuroraDB, etc.

### Guide how to deploy

## Ecosystem

*simple-awslambda* runtime provides different *modules* that form an ecosystem and solve crucial serverless problems like:
- Logging 
- Http Components
- GraalVM Hints
- Docker Image
- Testing

### Logging

Aws Lambda Runtime uses [slf4j](https://github.com/qos-ch/slf4j) for logging and not *LambdaLogger* that AWS Context API provides.

It is recommended to use [slf4j-simple-logger](https://github.com/GoodforGod/slf4j-simple-logger) for logging,
it is part of *simple-awslambda* ecosystem and was designed to be used in serverless environment, is GraalVM friendly and easy to use.

You can use any slf4j compatible implementation if needed.

Logging example:
```java
Logger logger = LoggerFactory.getLogger(getClass());
logger.debug("Some logging...");
```

#### Configuration

[slf4j-simple-logger](https://github.com/GoodforGod/slf4j-simple-logger) configuration can use environment variables,
*simple-awslambda* refresh configuration and all env configured properties will be updated.

Given configuration:
```properties
org.slf4j.simpleLogger.defaultLogLevel=${AWS_LAMBDA_LOGGING_LEVEL}
```

Each invocation configuration will be refreshed with actual value for environment variable *AWS_LAMBDA_LOGGING_LEVEL*.

### Native Hints

Native image require special configurations to build and run native executables. 
Most used cases is reflection config for DTO serialization/deserialization.

You can use any approach you would like to generate such configs, but there is library
that provide [annotation based]() way to generate such configs,
this is easy, simple and solid solution for such configs.

You can also use [GraalVM Native Image agent](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html#agent-support), but its up to you how to include and generate such configs.

### Serialization

Default *Converter* implementation that is used for JSON serialization/deserialization is [GSON](https://github.com/google/gson) due to being the most lightweight, simple and solid solution.

Record are also supported by providing custom TypeAdapter.

#### Configuration

You can use property file to configure GSON, [check this documentation](https://github.com/GoodforGod/gson-configuration#properties-file) for more info.

### Http Components

Contracts provide Developer Friendly HTTP based components via [this library](https://github.com/GoodforGod/http-common) that is well integrated into all components.

Runtime provides *SimpleHttpClient* and other contracts to interact with HTTP.

#### Reactive

Runtime that is responsible for handling Event is Reactive by design and returns Publisher from Java API.

### Runtime

There two runtime entrypoints available to extend:
- *AbstractInputLambdaEntrypoint* - entrypoint for direct event that should be propagated for processing.
- *AbstractBodyLambdaEntrypoint* - entrypoint for body events (like APIGatewayV2HTTPEvent, APIGatewayV2WebSocketEvent), this entrypoint extracts Body from response and pass it to RequestHandler directly.

You can also choose what *RequestHandler* will be used for event processing via AWS environment variable *_HANDLER*.

#### Event Support

Runtime encourage using of [this aws event library](https://github.com/GoodforGod/aws-lambda-java-events)
cause this library is GSON/Jackson/etc compatible, easy to use, up-to-date, without any external dependencies.

You can check library documentation for more info.

If you really want, you can use official [AWS Event SDK library](https://github.com/aws/aws-lambda-java-libs/tree/master/aws-lambda-java-events), but you should keep in mind all downsides.

### Build

Runtime encourage users to build their lambdas in three steps:
- Build JAR via Gradle
- Build Native Executable via Docker
- Extract Native Executable from Docker

Each project this is build via this runtime should include Dockerfile and bootstrap file.


Here is simple boostrap example:
```
#!/bin/sh
set -euo pipefail
./application -Djava.library.path=$(pwd)
```

Here is simple Dockerfile example:
```dockerfile
FROM goodforgod/amazonlinux-graalvm:22.1.0-java17-amd64

ADD build/libs/*all.jar build/libs/application.jar

RUN native-image --no-fallback -classpath build/libs/application.jar

ADD bootstrap bootstrap
RUN chmod +x bootstrap application

RUN zip -j function.zip bootstrap application

EXPOSE 8080
ENTRYPOINT ["/home/application/application"]
```

1) Build your lambda JAR:
```shell
./gradlew shadowJar
```

2) Build Native Executable:
```shell
docker build -t your-lambda-name .
```

3) Extract Native Executable:
```shell
docker run --rm --entrypoint cat your-lambda-name /home/application/function.zip > build/function.zip
```

All this can be package into a simple *build.sh* shell script:
```shell
#!/bin/bash

gradlew shadowJar
docker build -t your-lambda-name .
docker run --rm --entrypoint cat your-lambda-name /home/application/function.zip > build/function.zip
```

#### Docker Image

Runtime encourage using [this docker image](https://github.com/GoodforGod/docker-amazonlinux-graalvm), but as always you are free to use any image or approaches to build native executable.

### Testing

Runtime provides mechanisms to easily test Lambdas, this can be done via *AwsLambdaAssertions*.

Given Lambda Entrypoint:
```java
public class InputLambdaEntrypoint extends AbstractInputLambdaEntrypoint {

    public static void main(String[] args) {
        new InputLambdaEntrypoint().run(args);
    }

    @Override
    protected Consumer<SimpleRuntimeContext> setupInRuntime() {
        return context -> context.registerBean(new HelloWorldLambda());
    }
}
```

Given Request Handler:
```java
public class HelloWorldLambda implements RequestHandler<Request, Response> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Response handleRequest(Request request, Context context) {
        logger.info("Processing User with name: {}", request.name());
        return new Response(UUID.randomUUID().toString(), "Hello - " + request.name());
    }
}
```

Given DTOs:
```java
@ReflectionHint
public record Request(String name) {}

@ReflectionHint
public record Response(String id, String message) {}
```

Testing for such Lambda will look like:
```java
class InputEventHandlerTests extends Assertions {

    @Test
    void eventHandled() {
        final Request request = new Request("Steeven King");
        final Response response = AwsLambdaAssertions.ofEntrypoint(new InputLambdaEntrypoint())
                .inputJson(request)
                .expectJson(Response.class);

        assertEquals("Hello - Steeven King", response.message());
    }
}
```

## Extensibility

Most of the core components like Converter, SimpleHttpClient, RuntimeContext, etc can be replaced with your implementations
and this extensibility is a valuable feature.

You can easily extend, replace and improve all components, all ecosystem is open sourced as well and all components are independent.

### Micronaut

There is Micronaut module extension that allow to use [Micronaut](/simple-awslambda-micronaut) as DI and all its components as well.

## License

This project licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details