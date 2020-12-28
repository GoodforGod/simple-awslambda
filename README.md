## AWS Lambda Runtime

AWS lambda runtime with Micronaut DI support with GraalVM native lambda compatability.

Provides Java native library *AwsLambdaRuntime* with support for Dependency Injection from [Micronaut framework](https://docs.micronaut.io/latest/guide/index.html#ioc).

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

### How To

You just need to implement *Lambda* interface and implement it.

```java
@Singleton
public class MyLambda implements Lambda<String, String> {

    public String handle(String s) {
        return "response for " + s;
    }
}
```

All will be setup for using it as AWS lambda, you will need just to correctly provide GraalVM properties for image to be build.

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

    public String handle(String s) {
        return "response for " + s;
    }
}
```