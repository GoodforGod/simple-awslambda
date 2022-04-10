# Simple AWSLambda

Fastest, Lightweight and GraalVM optimized AWS Lambda Runtime.

Most simple and efficient way to build Native Serverless Java executables for AWS Lambda.

## Dependency :rocket:

**Gradle**
```groovy
implementation "io.goodforgod:simple-awslambda:0.22.0-SNAPSHOT"
```

**Maven**
```xml
<dependency>
    <groupId>io.goodforgod</groupId>
    <artifactId>simple-awslambda</artifactId>
    <version>0.22.0-SNAPSHOT</version>
</dependency>
```

## Getting Started

### Examples

### Guide how to deploy

## Ecosystem


### Logging

#### Configuration

### Native Hints

### Serialization

#### Configuration

### Http Components

#### Reactive

### Runtime

#### Event Support

### Build

#### Docker Image

Body event support and custom support

### Extensibility



## Testing




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
