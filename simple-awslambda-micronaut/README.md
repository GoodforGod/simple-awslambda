# Simple AWSLambda Micronaut

Fastest, Lightweight and GraalVM optimized AWS Lambda Runtime.

Most simple and efficient way to build Native Serverless Java executables for AWS Lambda.

## Dependency :rocket:

**Gradle**
```groovy
implementation "io.goodforgod:simple-awslambda-micronaut:0.22.0-SNAPSHOT"
```

**Maven**
```xml
<dependency>
    <groupId>io.goodforgod</groupId>
    <artifactId>simple-awslambda-micronaut</artifactId>
    <version>0.22.0-SNAPSHOT</version>
</dependency>
```

## Micronaut

Do not forget to add such dependencies in you build for DI and GraalVM support:

```groovy
dependencies {
    annotationProcessor 'io.micronaut:micronaut-inject-java'
    annotationProcessor 'io.micronaut:micronaut-graal'

    compileOnly 'org.graalvm.nativeimage:svm'
}
```
