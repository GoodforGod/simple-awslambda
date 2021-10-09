package io.goodforgod.aws.lambda.simple.config;

import io.goodforgod.aws.lambda.simple.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * Configures logging properties of {@link org.slf4j.Logger}
 *
 * @author Anton Kurako (GoodforGod)
 * @since 09.05.2021
 */
public class SimpleLambdaDefaultLogLevelRefresher {

    private static final String DEFAULT_LOGGING_PROPERTY = "org.slf4j.simpleLogger.defaultLogLevel";

    private static String prevEnvLevel = null;

    private SimpleLambdaDefaultLogLevelRefresher() {}

    public static void refresh() {
        final String envLevel = System.getenv(SimpleLambdaContextVariables.LOGGING_LEVEL);
        if (StringUtils.isEmpty(envLevel)) {
            return;
        }

        if (envLevel.equals(prevEnvLevel)) {
            return;
        }

        try {
            final Level level = Level.valueOf(envLevel);
            System.setProperty(DEFAULT_LOGGING_PROPERTY, level.name().toLowerCase());
            prevEnvLevel = envLevel;
        } catch (IllegalArgumentException e) {
            final Logger logger = LoggerFactory.getLogger(SimpleLambdaDefaultLogLevelRefresher.class);
            logger.warn("Can't set logging level due to invalid variable: {}", envLevel);
        }
    }
}
