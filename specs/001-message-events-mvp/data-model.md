# Data Model: Message Events MVP

## Enumerations

### ObservationSide

| Value | Meaning |
|-------|---------|
| `CLIENT` | Observation produced on the logical client |
| `SERVER` | Observation produced on the logical server (dedicated or integrated) |

### MessagePhase

| Value | Meaning |
|-------|---------|
| `PLAYER_SEND` | Local/player submission path (client about to send, or server accepted player chat) |
| `DELIVERED` | Message delivered into the client chat HUD (player or non-overlay system receive) |
| `SYSTEM` | Server-side system/broadcast path (e.g. `broadcastSystemMessage`) |

### LogSeverity

Mapped from Log4j levels used in filtering:

| Value | Typical Log4j |
|-------|---------------|
| `TRACE` | TRACE |
| `DEBUG` | DEBUG |
| `INFO` | INFO |
| `WARN` | WARN |
| `ERROR` | ERROR |
| `FATAL` | FATAL |

## Entities

### ChatObservation

Immutable snapshot of one chat-related sighting.

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `plainText` | string | yes | Human-readable text at emit time |
| `side` | ObservationSide | yes | Where the bridge saw it |
| `phase` | MessagePhase | yes | Send vs deliver vs server system |
| `speakerName` | string \| null | no | Username when known (`ServerChatEvent`, player receive) |
| `speakerUuid` | UUID \| null | no | Sender UUID when known; null/NIL for system |
| `overlay` | boolean | yes | Always `false` in MVP emits (overlay excluded) |
| `gameTimeMillis` | long | yes | `System.currentTimeMillis()` at emit (debug / ordering aid) |

**Validation**:
- `plainText` MAY be empty if the game would still show an empty line.
- `phase=SYSTEM` on `side=CLIENT` is represented as `DELIVERED` with
  `speakerUuid` absent (client receive path), not a second SYSTEM enum on
  client — keep SYSTEM for server broadcast capture only.
- Read-only: no mutation fields.

**Relationships**: Emitted as `ChatObservedEvent` payload; mirrored into KubeJS
`ChatMessageKubeEvent`.

### LogObservation

Immutable snapshot of one logger line.

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `plainText` | string | yes | Formatted message text |
| `side` | ObservationSide | yes | Process side where the appender ran |
| `loggerName` | string | yes | Log4j logger name |
| `severity` | LogSeverity | yes | Level after mapping |
| `gameTimeMillis` | long | yes | Emit timestamp |

**Validation**:
- Emitted only when config `loggerObservationEnabled` is true AND severity ≥
  configured minimum.
- Lines from loggers under `pnnh.mcmessageevents` MUST NOT be emitted.

**Relationships**: Emitted as `LogObservedEvent` payload; mirrored into KubeJS
`LogMessageKubeEvent`.

### LoggerBridgeConfig (COMMON)

| Field | Type | Default | Notes |
|-------|------|---------|-------|
| `loggerObservationEnabled` | boolean | `false` | Master switch |
| `loggerMinimumLevel` | enum/string | `INFO` | Minimum severity when enabled |

**State transitions**:
1. Mod load → read config → if enabled, install appender.
2. Config reload → install or remove appender to match new values.

## Emission mapping (capture → entity)

| Capture source | side | phase | speaker |
|----------------|------|-------|---------|
| `ClientChatEvent` | CLIENT | PLAYER_SEND | local player if available |
| `ClientChatReceivedEvent.Player` | CLIENT | DELIVERED | sender UUID / name if available |
| `ClientChatReceivedEvent.System` (non-overlay) | CLIENT | DELIVERED | null |
| `ServerChatEvent` | SERVER | PLAYER_SEND | event player |
| `PlayerList.broadcastSystemMessage*` mixin | SERVER | SYSTEM | null |

## De-duplication guidance (for scripts)

Same logical chat may appear as `CLIENT/PLAYER_SEND` then `CLIENT/DELIVERED`,
and/or `SERVER/PLAYER_SEND` plus later client delivery if both sides install.
Scripts SHOULD key on `(side, phase, plainText, speaker*)` rather than assuming
a single emit.
