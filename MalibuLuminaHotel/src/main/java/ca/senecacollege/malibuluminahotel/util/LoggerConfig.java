package ca.senecacollege.malibuluminahotel.util;

import java.io.IOException;
import java.util.logging.*;

/**
 * Centralized logger configuration for file-based logging with rotation.
 */
public class LoggerConfig {

    private static final String LOG_FILE_PATTERN = "system_logs.%g.log";
    private static final int MAX_FILE_SIZE = 1024 * 1024; // 1MB per file
    private static final int MAX_FILES = 10; // Keep 10 log files
    private static boolean configured = false;

    /**
     * Configures the global logger with file rotation.
     * Call once at application startup.
     */
    public static void configure() {
        if (configured) {
            return;
        }

        try {
            // Create file handler with rotation
            FileHandler fileHandler = new FileHandler(LOG_FILE_PATTERN, MAX_FILE_SIZE, MAX_FILES, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO);

            // Add to root logger
            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO);

            // Remove default console handler if desired
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                if (handler instanceof ConsoleHandler) {
                    rootLogger.removeHandler(handler);
                }
            }

            configured = true;
            Logger.getGlobal().info("File logging configured: " + LOG_FILE_PATTERN);

        } catch (IOException e) {
            System.err.println("Failed to configure file logging: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets a logger for the specified class.
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }

    /**
     * Logs an exception with stack trace.
     */
    public static void logException(Logger logger, String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }
}
