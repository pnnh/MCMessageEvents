package pnnh.mcmessageevents.capture;

import java.util.UUID;

import pnnh.mcmessageevents.MCMessageEvents;
import pnnh.mcmessageevents.observe.ChatObservation;
import pnnh.mcmessageevents.observe.MessagePhase;
import pnnh.mcmessageevents.observe.ObservationDispatcher;
import pnnh.mcmessageevents.observe.ObservationSide;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientChatEvent;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;

/**
 * Logical-client chat observation. Observe-only: never cancels or rewrites.
 */
@EventBusSubscriber(modid = MCMessageEvents.MOD_ID, value = Dist.CLIENT)
public final class ClientChatCapture {
    private ClientChatCapture() {
    }

    @SubscribeEvent
    public static void onClientChatSend(ClientChatEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        String name = player != null ? player.getGameProfile().getName() : null;
        UUID uuid = player != null ? player.getUUID() : null;
        ObservationDispatcher.emitChat(new ChatObservation(
                event.getMessage(),
                ObservationSide.CLIENT,
                MessagePhase.PLAYER_SEND,
                name,
                uuid,
                false,
                System.currentTimeMillis()));
    }

    @SubscribeEvent
    public static void onClientChatReceived(ClientChatReceivedEvent event) {
        if (event instanceof ClientChatReceivedEvent.System system && system.isOverlay()) {
            return;
        }
        Component message = event.getMessage();
        String plain = message == null ? "" : message.getString();
        String speakerName = null;
        UUID speakerUuid = event.getSender();
        if (speakerUuid != null && speakerUuid.getLeastSignificantBits() == 0L
                && speakerUuid.getMostSignificantBits() == 0L) {
            speakerUuid = null;
        }
        if (event.getBoundChatType() != null && event.getBoundChatType().name() != null) {
            speakerName = event.getBoundChatType().name().getString();
        }
        ObservationDispatcher.emitChat(new ChatObservation(
                plain,
                ObservationSide.CLIENT,
                MessagePhase.DELIVERED,
                speakerName,
                speakerUuid,
                false,
                System.currentTimeMillis()));
    }
}
