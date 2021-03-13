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
