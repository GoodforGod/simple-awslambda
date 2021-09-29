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
public class SimpleLambdaDefaultLogLevelRefresher {

    enum LoggingLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    private static final String LOGGING_ENV = "SIMPLE_LAMBDA_DEFAULT_LOG_LEVEL";
    private static final String DEFAULT_LOGGING_PROPERTY = "org.slf4j.simpleLogger.defaultLogLevel";

    private static String prevEnvLevel = null;

    private SimpleLambdaDefaultLogLevelRefresher() {}

    public static void refresh() {
        final String envLevel = System.getenv(LOGGING_ENV);
        if (StringUtils.isEmpty(envLevel))
            return;

        if (envLevel.equals(prevEnvLevel))
            return;

        try {
            final LoggingLevel level = LoggingLevel.valueOf(envLevel);
            System.setProperty(DEFAULT_LOGGING_PROPERTY, level.name().toLowerCase());
            prevEnvLevel = envLevel;
        } catch (IllegalArgumentException e) {
            final Logger logger = LoggerFactory.getLogger(SimpleLambdaDefaultLogLevelRefresher.class);
            logger.warn("Can't set logging level due to invalid variable: {}", envLevel);
        }
    }
}
