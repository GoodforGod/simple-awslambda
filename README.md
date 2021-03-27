## AWS Lambda Runtime

AWS lambda runtime with custom DI context support and GraalVM native lambda compatibility.

Provides Java native library *AwsLambdaRuntime* with support for custom dependency injection context.

Allow building smallest and fastest native lambdas with DI support.

## Lambda

Just implement *Lambda* interface and implement it.

```java
@Singleton
public class MyLambda implements Lambda<String, String> {

    public String handle(String s) {
        return "response for " + s;
    }
}
```

## Logging

You can use provided *LambdaLogger* for logging.

```java
@Singleton
public class MyLambda implements Lambda<String, String> {

    private final LambdaLogger logger;
    
    @Inject
    public MyLambda(LambdaLogger logger) {
        this.logger = logger;
    }

    public String handle(String input) {
        logger.info("Lambda input %s", input);
        return "response for " + s;
    }
}
```

You can change logging levels via **LAMBDA_LOGGING_LEVEL** environment variable:
- DEBUG
- INFO
- WARN
- ERROR
- OFF


## Dependency

## Dependencies

Do not forget to add such dependencies in you build for DI and GraalVM support:

```groovy
dependencies {
    annotationProcessor 'io.micronaut:micronaut-inject-java'
    annotationProcessor 'io.micronaut:micronaut-graal'

    compileOnly 'io.micronaut:micronaut-inject-java'
    compileOnly 'org.graalvm.nativeimage:svm'
}
```

## Lambda

### Getting Started

Just implement *Lambda* interface and implement it.

```java
@Singleton
public class MyLambda implements Lambda<String, String> {

    public String handle(String s) {
        return "response for " + s;
    }
}
```

### Runtime

There two runtimes available for Lambda execution, choose runtime as main class for correct execution.

Available runtimes:
- **AwsLambdaRuntime** (Process requests as is)
- **AwsGatewayLambdaRuntime** (Processes requests as [requests from AWS API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-develop-integrations-lambda.html) and respond in [AWS API Gateway format](https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-develop-integrations-lambda.html))

In case of migrating lambda from internal usage to *API Gateway* there no need to change rewrite any code just change runtime.

#### Gradle

How to set runtime as main class in *build.gradle* for *jar* execution:
```groovy
mainClassName = "io.aws.lambda.runtime.micronaut.AwsLambdaRuntime"
```

For GraalVM check [corresponding](#graalvm) section.

### GraalVM

#### Dependencies

Do not forget to add such dependencies in you build for DI and GraalVM support:

```groovy
dependencies {
    annotationProcessor 'io.micronaut:micronaut-inject-java'
    annotationProcessor 'io.micronaut:micronaut-graal'

    compileOnly 'org.graalvm.nativeimage:svm'
}
```

#### Runtime

Just place *native-image.properties* in resource folder as [GraalVM specify](https://docs.oracle.com/en/graalvm/enterprise/19/guide/reference/native-image/configuration.html) with runtime as main class:
```text
Args = -H:Name=lambda \
       -H:Class=io.aws.lambda.runtime.micronaut.AwsGatewayLambdaRuntime
```

### Logging

You can use provided *LambdaLogger* for logging.

```java
@Singleton
public class MyLambda implements Lambda<String, String> {

    private final LambdaLogger logger;
    
    @Inject
    public MyLambda(LambdaLogger logger) {
        this.logger = logger;
    }

    public String handle(String input) {
        logger.info("Lambda input %s", input);
        return "response for " + s;
    }
}
```

You can change logging levels via **LAMBDA_LOGGING_LEVEL** environment variable:
- DEBUG
- INFO
- WARN
- ERROR
- OFF
