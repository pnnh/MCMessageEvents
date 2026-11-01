package pnnh.mcmessageevents.kubejs;

import pnnh.mcmessageevents.observe.LogObservation;

import dev.latvian.mods.kubejs.event.KubeEvent;

/**
 * KubeJS payload for logger observations.
 */
public final class LogMessageKubeEvent implements KubeEvent {
    private final LogObservation observation;

    public LogMessageKubeEvent(LogObservation observation) {
        this.observation = observation;
    }

    public String getPlainText() {
        return observation.plainText();
    }

    public String getSide() {
        return observation.side().name();
    }

    public String getLoggerName() {
        return observation.loggerName();
    }

    public String getSeverity() {
        return observation.severity().name();
    }

    public long getGameTimeMillis() {
        return observation.gameTimeMillis();
    }
}
