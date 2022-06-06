# Simple AWSLambda Micronaut

Simple AWSLambda extension that allow to use Micronaut IoC and all Micronaut modules.

## Dependency :rocket:

**Gradle**
```groovy
implementation "io.goodforgod:simple-awslambda-micronaut:0.29.0-SNAPSHOT"
```

**Maven**
```xml
<dependency>
    <groupId>io.goodforgod</groupId>
    <artifactId>simple-awslambda-micronaut</artifactId>
    <version>0.29.0-SNAPSHOT</version>
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
