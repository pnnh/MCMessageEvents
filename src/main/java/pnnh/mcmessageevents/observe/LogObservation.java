package pnnh.mcmessageevents.observe;

/**
 * Immutable logger-line observation snapshot.
 */
public record LogObservation(
        String plainText,
        ObservationSide side,
        String loggerName,
        LogSeverity severity,
        long gameTimeMillis
) {
}
