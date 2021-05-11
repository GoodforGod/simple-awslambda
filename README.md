# AWS Lambda Runtime

AWS Lambda Runtime with Micronaut DI context support and GraalVM native compatibility.

Allow building smallest and fastest native lambdas with DI support.

## Dependency :rocket:

**Gradle**
```groovy
dependencies {
    compile 'com.github.goodforgod:aws-lambda-runtime:2.0.0-SNAPSHOT'
}
```

**Maven**
```xml
<dependency>
    <groupId>com.github.goodforgod</groupId>
    <artifactId>aws-lambda-runtime</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

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
- **io.aws.lambda.runtime.micronaut.AwsLambdaRuntime** (Process requests as is)
- **io.aws.lambda.runtime.micronaut.AwsGatewayLambdaRuntime** (Processes requests as [requests from AWS API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-develop-integrations-lambda.html) and respond in [AWS API Gateway format](https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-develop-integrations-lambda.html))

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

#### Type Hint

In most cases you have classes as input\output type, so you need provide a hint for GraalVM to use reflection for serialization\deserialization.

This can easily be done with annotation: *io.micronaut.core.annotation.TypeHint*

For input there will be User class:
```java
@TypeHint(value = {User.class}, accessType = {TypeHint.AccessType.ALL_PUBLIC})
public class User {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

For output Lambda will return Book class:
```java
@TypeHint(value = {Book.class}, accessType = {TypeHint.AccessType.ALL_PUBLIC})
public class Book {
    private String guid;
    private int pages;
    
    public Book(String guid, int pages) {
        this.guid = guid;
        this.pages = pages;
    }
    
    public String getGuid() {
        return guid;
    }
    
    public int getPages() {
        return pages;
    }
}
```

For correct serialization\deserialization we need reflection access, that is unavailable in GraalVM, in this case
we need to provide a [hint for GraalVM](https://www.graalvm.org/reference-manual/native-image/Reflection/) for which classes we need such access.

```java
@Singleton
public class GatewayLambda implements Lambda<Book, User> {

    @Override
    public Request handle(@NotNull User user) {
        return new Book(UUID.randomUUID().toString(), 10);
    }
}
```

#### Runtime

Just place *native-image.properties* in resource folder as [GraalVM specify](https://docs.oracle.com/en/graalvm/enterprise/19/guide/reference/native-image/configuration.html) with runtime as main class:
```text
Args = -H:Name=lambda \
       -H:Class=io.aws.lambda.runtime.micronaut.AwsLambdaRuntime
```
