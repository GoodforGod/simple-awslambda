# Simple AWSLambda

Fastest, Lightweight and GraalVM optimized AWS Lambda Runtime.

Simple and efficient way to build Native Java Serverless executables for AWS Lambda.

## Dependency :rocket:

[**Gradle**](https://mvnrepository.com/artifact/io.goodforgod/graalvm-hint-processor)
```groovy
implementation "io.goodforgod:simple-awslambda:0.27.0-SNAPSHOT"
```

[**Maven**](https://mvnrepository.com/artifact/io.goodforgod/graalvm-hint-processor)
```xml
<dependency>
    <groupId>io.goodforgod</groupId>
    <artifactId>simple-awslambda</artifactId>
    <version>0.27.0-SNAPSHOT</version>
</dependency>
```

## Description

## Getting Started

### Examples

Here is repository with many examples of [simple-awslambda]() for hello-world, DynamoDB, AuroraDB, etc.

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

### Serialization

#### Configuration

### Http Components

#### Reactive

### Runtime

#### Event Support

### Build

#### Docker Image

### Testing

## Extensibility













## Lambda

### Getting Started

Just implement *Lambda* interface.

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
- **AwsBodyLambdaRuntime** (Processes requests with body like [requests from AWS API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-develop-integrations-lambda.html) and respond in [AWS API Gateway format](https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-develop-integrations-lambda.html))
