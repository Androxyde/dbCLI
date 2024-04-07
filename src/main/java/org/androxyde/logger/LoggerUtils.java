package org.androxyde.logger;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {

    public static void setFile(String file) {
        Logger logbackLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        MapAppender appender = (MapAppender)logbackLogger.getAppender("STDOUT");
        appender.setFile(file);
    }

}
