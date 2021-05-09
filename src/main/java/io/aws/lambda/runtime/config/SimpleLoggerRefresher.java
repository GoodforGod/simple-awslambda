package io.aws.lambda.runtime.config;

import io.aws.lambda.runtime.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures logging properties of {@link org.slf4j.Logger}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 09.05.2021
 */
public class SimpleLoggerRefresher {

    public enum LoggingLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    public static void refresh() {
        final String envLevel = System.getenv("LAMBDA_LOGGING_LEVEL");
        if (StringUtils.isEmpty(envLevel))
            return;

        try {
            final LoggingLevel level = LoggingLevel.valueOf(envLevel);
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", level.name().toLowerCase());
        } catch (IllegalArgumentException e) {
            final Logger logger = LoggerFactory.getLogger(SimpleLoggerRefresher.class);
            logger.warn("Can't set logging level due to invalid variable: {}", envLevel);
        }
    }
}
