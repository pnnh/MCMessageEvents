package pnnh.mcmessageevents.observe;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

/**
 * Immutable chat observation snapshot.
 */
public record ChatObservation(
        String plainText,
        ObservationSide side,
        MessagePhase phase,
        @Nullable String speakerName,
        @Nullable UUID speakerUuid,
        boolean overlay,
        long gameTimeMillis
) {
}
