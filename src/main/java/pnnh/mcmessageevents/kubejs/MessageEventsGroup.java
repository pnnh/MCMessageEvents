package pnnh.mcmessageevents.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

/**
 * KubeJS event group {@code MessageEvents}.
 * Handlers use {@code common} so the same name works for client and server scripts.
 */
public interface MessageEventsGroup {
    EventGroup GROUP = EventGroup.of("MessageEvents");

    EventHandler CHAT = GROUP.common("chat", () -> ChatMessageKubeEvent.class);
    EventHandler LOG = GROUP.common("log", () -> LogMessageKubeEvent.class);
}
