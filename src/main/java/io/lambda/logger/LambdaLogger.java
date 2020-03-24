package io.lambda.logger;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public interface LambdaLogger {

    void debug(String message);

    void debug(String message, Object... args);

    void info(String message);

    void info(String message, Object... args);

    void warn(String message);

    void warn(String message, Object... args);

    void error(String message);

    void error(String message, Object... args);
}
