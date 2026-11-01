package pnnh.mcmessageevents.observe;

import net.neoforged.bus.api.Event;

/**
 * Read-only NeoForge bus event for a chat observation.
 */
public final class ChatObservedEvent extends Event {
    private final ChatObservation observation;

    public ChatObservedEvent(ChatObservation observation) {
        this.observation = observation;
    }

    public ChatObservation getObservation() {
        return observation;
    }
}
