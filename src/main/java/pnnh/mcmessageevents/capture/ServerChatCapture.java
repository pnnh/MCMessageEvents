package pnnh.mcmessageevents.capture;

import pnnh.mcmessageevents.MCMessageEvents;
import pnnh.mcmessageevents.observe.ChatObservation;
import pnnh.mcmessageevents.observe.MessagePhase;
import pnnh.mcmessageevents.observe.ObservationDispatcher;
import pnnh.mcmessageevents.observe.ObservationSide;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;

/**
 * Logical-server player chat observation. Observe-only: never cancels or rewrites.
 */
@EventBusSubscriber(modid = MCMessageEvents.MOD_ID)
public final class ServerChatCapture {
    private ServerChatCapture() {
    }

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        Component message = event.getMessage();
        String plain = event.getRawText();
        if (plain == null || plain.isEmpty()) {
            plain = message == null ? "" : message.getString();
        }
        ObservationDispatcher.emitChat(new ChatObservation(
                plain,
                ObservationSide.SERVER,
                MessagePhase.PLAYER_SEND,
                event.getUsername(),
                event.getPlayer() != null ? event.getPlayer().getUUID() : null,
                false,
                System.currentTimeMillis()));
    }
}
