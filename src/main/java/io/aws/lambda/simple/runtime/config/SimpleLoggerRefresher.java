package io.aws.lambda.simple.runtime.config;

import io.aws.lambda.simple.runtime.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures logging properties of {@link org.slf4j.Logger}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 09.05.2021
 */
public class SimpleLoggerRefresher {

    private static final String LOGGING_ENV = "SIMPLE_LAMBDA_DEFAULT_LOG_LEVEL";
    private static final String DEFAULT_LOGGING_PROPERTY = "org.slf4j.simpleLogger.defaultLogLevel";

    public enum LoggingLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    public static void refresh() {
        final String envLevel = System.getenv(LOGGING_ENV);
        if (StringUtils.isEmpty(envLevel))
            return;

        try {
            final LoggingLevel level = LoggingLevel.valueOf(envLevel);
            System.setProperty(DEFAULT_LOGGING_PROPERTY, level.name().toLowerCase());
        } catch (IllegalArgumentException e) {
            final Logger logger = LoggerFactory.getLogger(SimpleLoggerRefresher.class);
            logger.warn("Can't set logging level due to invalid variable: {}", envLevel);
        }
    }
}
