package pnnh.mcmessageevents.observe;

import pnnh.mcmessageevents.MCMessageEvents;

import net.neoforged.neoforge.common.NeoForge;

/**
 * Single emit path for observation events. Captures MUST use this class.
 */
public final class ObservationDispatcher {
    private ObservationDispatcher() {
    }

    public static void emitChat(ChatObservation observation) {
        MCMessageEvents.LOGGER.debug(
                "Chat observed {}/{}: {}",
                observation.side(),
                observation.phase(),
                observation.plainText());
        NeoForge.EVENT_BUS.post(new ChatObservedEvent(observation));
    }

    public static void emitLog(LogObservation observation) {
        MCMessageEvents.LOGGER.debug(
                "Log observed {}/{}: {}",
                observation.side(),
                observation.severity(),
                observation.plainText());
        NeoForge.EVENT_BUS.post(new LogObservedEvent(observation));
    }
}
