package pnnh.mcmessageevents.observe;

import net.neoforged.bus.api.Event;

/**
 * Read-only NeoForge bus event for a logger observation.
 */
public final class LogObservedEvent extends Event {
    private final LogObservation observation;

    public LogObservedEvent(LogObservation observation) {
        this.observation = observation;
    }

    public LogObservation getObservation() {
        return observation;
    }
}
