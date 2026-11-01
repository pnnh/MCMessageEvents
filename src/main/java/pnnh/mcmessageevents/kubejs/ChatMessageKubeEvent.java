package pnnh.mcmessageevents.kubejs;

import java.util.UUID;

import pnnh.mcmessageevents.observe.ChatObservation;

import org.jetbrains.annotations.Nullable;

import dev.latvian.mods.kubejs.event.KubeEvent;

/**
 * KubeJS payload for chat observations.
 */
public final class ChatMessageKubeEvent implements KubeEvent {
    private final ChatObservation observation;

    public ChatMessageKubeEvent(ChatObservation observation) {
        this.observation = observation;
    }

    public String getPlainText() {
        return observation.plainText();
    }

    public String getSide() {
        return observation.side().name();
    }

    public String getPhase() {
        return observation.phase().name();
    }

    @Nullable
    public String getSpeakerName() {
        return observation.speakerName();
    }

    @Nullable
    public String getSpeakerUuid() {
        UUID uuid = observation.speakerUuid();
        return uuid == null ? null : uuid.toString();
    }

    public long getGameTimeMillis() {
        return observation.gameTimeMillis();
    }
}
