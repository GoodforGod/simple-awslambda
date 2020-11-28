package io.lambda.aws.logger.impl;

import io.lambda.aws.logger.LambdaLogger;
import io.lambda.aws.logger.Level;
import io.micronaut.core.util.StringUtils;

import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Singleton
public class OutputLambdaLogger implements LambdaLogger {

    private final Level level;

    public OutputLambdaLogger() {
        final String envLevel = System.getenv("LAMBDA_LOGGING_LEVEL");
        this.level = StringUtils.isEmpty(envLevel) ? Level.INFO : Level.valueOf(envLevel);
        debug("Logging level set to: %s", level);
    }

    @Override
    public void debug(String format) {
        if (level.equals(Level.DEBUG))
            print(format);
    }

    @Override
    public void debug(String format, Object... args) {
        if (level.equals(Level.DEBUG))
            print(format, args);
    }

    @Override
    public void info(String format) {
        if (level.ordinal() <= Level.INFO.ordinal())
            print(format);
    }

    @Override
    public void info(String format, Object... args) {
        if (level.ordinal() <= Level.INFO.ordinal())
            print(format, args);
    }

    @Override
    public void warn(String format) {
        if (level.ordinal() <= Level.WARN.ordinal())
            print(format);
    }

    @Override
    public void warn(String format, Object... args) {
        if (level.ordinal() <= Level.WARN.ordinal())
            print(format, args);
    }

    @Override
    public void error(String format) {
        if (level.ordinal() <= Level.ERROR.ordinal())
            print(format);
    }

    @Override
    public void error(String format, Object... args) {
        if (level.ordinal() <= Level.ERROR.ordinal())
            print(format, args);
    }

    private void print(String format, Object... args) {
        System.out.printf((format) + "%n", args);
    }

    private void print(String format) {
        System.out.println(format + "%n");
    }
}
