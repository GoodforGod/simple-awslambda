package io.goodforgod.aws.lambda.simple;

import static io.goodforgod.aws.lambda.simple.SimpleLoggerRefresher.SIMPLE_LOGGER_FACTORY;

import io.goodforgod.graalvm.hint.annotation.ReflectionHint;
import java.lang.reflect.Method;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * Configures logging properties of {@link org.slf4j.Logger}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 09.05.2021
 */
@ReflectionHint(typeNames = SIMPLE_LOGGER_FACTORY, value = ReflectionHint.AccessType.ALL_DECLARED_METHODS)
final class SimpleLoggerRefresher {

    static final String SIMPLE_LOGGER_FACTORY = "io.goodforgod.slf4j.simplelogger.SimpleLoggerFactory";

    private SimpleLoggerRefresher() {}

    static void refresh() {
        final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        try {
            if (loggerFactory.getClass().getName().equals(SIMPLE_LOGGER_FACTORY)) {
                final Method setLogLevel = loggerFactory.getClass().getMethod("refresh", Level.class);
                setLogLevel.invoke(loggerFactory);
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
