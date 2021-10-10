package io.goodforgod.aws.simplelambda.runtime;

import static io.goodforgod.aws.simplelambda.runtime.SimpleLoggerLogLevelRefresher.SIMPLE_LOGGER_FACTORY;

import io.goodforgod.aws.simplelambda.config.SimpleLambdaContextVariables;
import io.goodforgod.aws.simplelambda.utils.StringUtils;
import io.goodforgod.graalvm.hint.annotation.TypeHint;
import java.lang.reflect.Method;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * Configures logging properties of {@link org.slf4j.Logger}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 09.05.2021
 */
@TypeHint(typeNames = SIMPLE_LOGGER_FACTORY, value = TypeHint.AccessType.ALL_DECLARED_METHODS)
class SimpleLoggerLogLevelRefresher {

    static final String SIMPLE_LOGGER_FACTORY = "io.goodforgod.slf4j.simplelogger.SimpleLoggerFactory";

    private static String prevEnvLevel = null;

    private SimpleLoggerLogLevelRefresher() {}

    static void refresh() {
        final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        try {
            if (loggerFactory.getClass().getName().equals(SIMPLE_LOGGER_FACTORY)) {
                final String envLevel = System.getenv(SimpleLambdaContextVariables.LOGGING_LEVEL);
                if (StringUtils.isEmpty(envLevel)) {
                    return;
                }

                if (envLevel.equals(prevEnvLevel)) {
                    return;
                }

                final Level level = Level.valueOf(envLevel);
                final Method setLogLevel = loggerFactory.getClass().getMethod("setLogLevel", Level.class);
                setLogLevel.invoke(loggerFactory, level);
                prevEnvLevel = envLevel;
            }
        } catch (IllegalArgumentException e) {
            final Logger logger = LoggerFactory.getLogger(SimpleLoggerLogLevelRefresher.class);
            final String envLevel = System.getenv(SimpleLambdaContextVariables.LOGGING_LEVEL);
            logger.warn("Can't set logging level due to invalid variable: {}", envLevel);
        } catch (Exception e) {
            // ignore
        }
    }
}
