package pnnh.mcmessageevents.kubejs;

import pnnh.mcmessageevents.observe.ChatObservedEvent;
import pnnh.mcmessageevents.observe.LogObservedEvent;
import pnnh.mcmessageevents.observe.ObservationSide;

import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;

import net.neoforged.neoforge.common.NeoForge;

/**
 * Soft-loaded by KubeJS via {@code kubejs.plugins.txt}. Forwards NeoForge
 * observation events into {@link MessageEventsGroup}.
 */
public final class MessageEventsKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void init() {
        NeoForge.EVENT_BUS.addListener(this::onChatObserved);
        NeoForge.EVENT_BUS.addListener(this::onLogObserved);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(MessageEventsGroup.GROUP);
    }

    private void onChatObserved(ChatObservedEvent event) {
        if (!MessageEventsGroup.CHAT.hasListeners()) {
            return;
        }
        var observation = event.getObservation();
        ScriptType type = observation.side() == ObservationSide.CLIENT
                ? ScriptType.CLIENT
                : ScriptType.SERVER;
        MessageEventsGroup.CHAT.post(type, new ChatMessageKubeEvent(observation));
    }

    private void onLogObserved(LogObservedEvent event) {
        if (!MessageEventsGroup.LOG.hasListeners()) {
            return;
        }
        var observation = event.getObservation();
        ScriptType type = observation.side() == ObservationSide.CLIENT
                ? ScriptType.CLIENT
                : ScriptType.SERVER;
        MessageEventsGroup.LOG.post(type, new LogMessageKubeEvent(observation));
    }
}
