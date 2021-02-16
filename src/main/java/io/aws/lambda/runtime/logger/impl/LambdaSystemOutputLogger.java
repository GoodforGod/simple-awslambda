package io.aws.lambda.runtime.logger.impl;

import io.aws.lambda.runtime.logger.LambdaLogger;
import io.aws.lambda.runtime.logger.Level;
import io.aws.lambda.runtime.utils.StringUtils;

import javax.inject.Singleton;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
@Singleton
public class LambdaSystemOutputLogger implements LambdaLogger {

    private Level level;

    public LambdaSystemOutputLogger() {
        refresh();
    }

    @Override
    public void refresh() {
        final String envLevel = System.getenv("LAMBDA_LOGGING_LEVEL");
        this.level = StringUtils.isEmpty(envLevel) ? Level.INFO : Level.valueOf(envLevel);
        debug("Logging level set to: %s", level);
    }

    @Override
    public boolean isDebugEnabled() {
        return level.ordinal() <= Level.DEBUG.ordinal();
    }

    @Override
    public boolean isInfoEnabled() {
        return level.ordinal() <= Level.INFO.ordinal();
    }

    @Override
    public boolean isWarnEnabled() {
        return level.ordinal() <= Level.WARN.ordinal();
    }

    @Override
    public boolean isErrorEnabled() {
        return level.ordinal() <= Level.ERROR.ordinal();
    }

    @Override
    public void debug(String format) {
        if (isDebugEnabled())
            print(format);
    }

    @Override
    public void debug(String format, Object... args) {
        if (isDebugEnabled())
            print(format, args);
    }

    @Override
    public void info(String format) {
        if (isInfoEnabled())
            print(format);
    }

    @Override
    public void info(String format, Object... args) {
        if (isInfoEnabled())
            print(format, args);
    }

    @Override
    public void warn(String format) {
        if (isWarnEnabled())
            print(format);
    }

    @Override
    public void warn(String format, Object... args) {
        if (isWarnEnabled())
            print(format, args);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        if (isWarnEnabled()) {
            print(message);
            throwable.printStackTrace();
        }
    }

    @Override
    public void error(String format) {
        if (isErrorEnabled())
            print(format);
    }

    @Override
    public void error(String format, Object... args) {
        if (isErrorEnabled())
            print(format, args);
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (isErrorEnabled()) {
            print(message);
            throwable.printStackTrace();
        }
    }

    private void print(String format, Object... args) {
        System.out.printf(format, args).println();
    }

    private void print(String format) {
        System.out.println(format);
    }
}
