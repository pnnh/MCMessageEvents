package pnnh.mcmessageevents.observe;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ChatObservationTest {
    @Test
    void mapsFieldsForPlayerSend() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        ChatObservation observation = new ChatObservation(
                "hello",
                ObservationSide.CLIENT,
                MessagePhase.PLAYER_SEND,
                "Steve",
                id,
                false,
                123L);

        assertEquals("hello", observation.plainText());
        assertEquals(ObservationSide.CLIENT, observation.side());
        assertEquals(MessagePhase.PLAYER_SEND, observation.phase());
        assertEquals("Steve", observation.speakerName());
        assertEquals(id, observation.speakerUuid());
        assertFalse(observation.overlay());
        assertEquals(123L, observation.gameTimeMillis());
    }

    @Test
    void systemBroadcastHasNoSpeaker() {
        ChatObservation observation = new ChatObservation(
                "server notice",
                ObservationSide.SERVER,
                MessagePhase.SYSTEM,
                null,
                null,
                false,
                1L);

        assertEquals(MessagePhase.SYSTEM, observation.phase());
        assertNull(observation.speakerName());
        assertNull(observation.speakerUuid());
    }

    @Test
    void logSeverityParsesConfig() {
        assertEquals(LogSeverity.INFO, LogSeverity.parseConfig("info"));
        assertEquals(LogSeverity.WARN, LogSeverity.parseConfig("WARN"));
        assertEquals(LogSeverity.INFO, LogSeverity.parseConfig("nope"));
    }
}
