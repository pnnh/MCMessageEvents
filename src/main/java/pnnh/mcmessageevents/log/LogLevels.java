package pnnh.mcmessageevents.log;

import pnnh.mcmessageevents.observe.LogSeverity;

import org.apache.logging.log4j.Level;

/**
 * Bridges {@link LogSeverity} and Log4j {@link Level}.
 */
public final class LogLevels {
    private LogLevels() {
    }

    public static LogSeverity fromLog4j(Level level) {
        if (level == null) {
            return LogSeverity.INFO;
        }
        if (level.isMoreSpecificThan(Level.FATAL)) {
            return LogSeverity.FATAL;
        }
        if (level.isMoreSpecificThan(Level.ERROR)) {
            return LogSeverity.ERROR;
        }
        if (level.isMoreSpecificThan(Level.WARN)) {
            return LogSeverity.WARN;
        }
        if (level.isMoreSpecificThan(Level.INFO)) {
            return LogSeverity.INFO;
        }
        if (level.isMoreSpecificThan(Level.DEBUG)) {
            return LogSeverity.DEBUG;
        }
        return LogSeverity.TRACE;
    }

    public static Level toLog4j(LogSeverity severity) {
        if (severity == null) {
            return Level.INFO;
        }
        return switch (severity) {
            case TRACE -> Level.TRACE;
            case DEBUG -> Level.DEBUG;
            case INFO -> Level.INFO;
            case WARN -> Level.WARN;
            case ERROR -> Level.ERROR;
            case FATAL -> Level.FATAL;
        };
    }
}
