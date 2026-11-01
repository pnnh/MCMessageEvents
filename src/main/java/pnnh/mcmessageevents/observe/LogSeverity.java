package pnnh.mcmessageevents.observe;

/**
 * Logger severity for script-facing payloads.
 */
public enum LogSeverity {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL;

    public static LogSeverity parseConfig(String raw) {
        if (raw == null || raw.isBlank()) {
            return INFO;
        }
        try {
            return LogSeverity.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return INFO;
        }
    }
}
