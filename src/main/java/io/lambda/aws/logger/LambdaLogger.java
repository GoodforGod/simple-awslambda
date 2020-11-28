package io.lambda.aws.logger;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public interface LambdaLogger {

    void debug(String format);

    void debug(String format, Object... args);

    boolean isDebugEnabled();

    void info(String format);

    void info(String format, Object... args);

    boolean isInfoEnabled();

    void warn(String format);

    void warn(String format, Object... args);

    boolean isWarnEnabled();

    void error(String format);

    void error(String format, Object... args);

    boolean isErrorEnabled();
}
