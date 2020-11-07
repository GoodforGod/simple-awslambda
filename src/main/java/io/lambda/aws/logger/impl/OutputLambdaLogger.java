package io.lambda.aws.logger.impl;

import io.lambda.aws.logger.LambdaLogger;
import io.lambda.aws.logger.Level;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.StringUtils;

import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Introspected
@Singleton
public class OutputLambdaLogger implements LambdaLogger {

    private final Level level;

    public OutputLambdaLogger() {
        final String envLevel = System.getenv("LAMBDA_LOGGING_LEVEL");
        if (StringUtils.isEmpty(envLevel)) {
            this.level = Level.valueOf(envLevel);
        } else {
            this.level = Level.INFO;
        }
    }

    @Override
    public void debug(String message) {
        if (level.equals(Level.DEBUG))
            print(message);
    }

    @Override
    public void debug(String message, Object... args) {
        if (level.equals(Level.DEBUG))
            print(message, args);
    }

    @Override
    public void info(String message) {
        if (level.ordinal() <= Level.INFO.ordinal())
            print(message);
    }

    @Override
    public void info(String message, Object... args) {
        if (level.ordinal() <= Level.INFO.ordinal())
            print(message, args);
    }

    @Override
    public void warn(String message) {
        if (level.ordinal() <= Level.WARN.ordinal())
            print(message);
    }

    @Override
    public void warn(String message, Object... args) {
        if (level.ordinal() <= Level.WARN.ordinal())
            print(message, args);
    }

    @Override
    public void error(String message) {
        if (level.ordinal() <= Level.ERROR.ordinal())
            print(message);
    }

    @Override
    public void error(String message, Object... args) {
        if (level.ordinal() <= Level.ERROR.ordinal())
            print(message, args);
    }

    private void print(String message, Object... args) {
        System.out.printf((message) + "%n", args);
    }

    private void print(String message) {
        System.out.println(message);
    }
}
