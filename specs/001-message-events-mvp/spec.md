# Feature Specification: Message Events MVP

**Feature Branch**: `001-message-events-mvp`

**Created**: 2026-11-01

**Status**: Draft

**Input**: User description: "NeoForge 双端可选安装模组；将客户端与服务端聊天区内容（用户发送与服务器下发）以及日志内容暴露为事件，供 KubeJS 监听与后续脚本处理；双方不必同时安装。"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Observe chat HUD on the installed side (Priority: P1)

A pack author installs the bridge on the side they control (client, dedicated
server, or both). Whenever a message appears in that side's chat HUD—or would
be broadcast as chat/system text on a dedicated server—the bridge emits an
observation event with plain text and enough context to tell where it came from
(player send vs server/system delivery) and which logical side saw it.

**Why this priority**: Chat observation is the core product value and is useful
even without logger bridging or a fancy script API.

**Independent Test**: Install only the client (or only the dedicated server),
produce player chat and a system/broadcast line, confirm one observation event
per visible (or server-emitted) chat line with correct plain text.

**Acceptance Scenarios**:

1. **Given** only the client has the bridge installed, **When** the local player
   sends a chat message that appears in the HUD, **Then** an observation event
   is emitted with that message's plain text and an outbound/player-send marker.
2. **Given** only the client has the bridge installed, **When** the server
   delivers a chat or system line into the client HUD, **Then** an observation
   event is emitted with that line's plain text and an inbound/delivery marker.
3. **Given** only the dedicated server has the bridge installed, **When** a
   player chat or server system broadcast is handled on that server, **Then** an
   observation event is emitted without requiring any client to install the
   bridge.
4. **Given** the bridge is absent on one side, **When** the other side runs
   normally, **Then** that side still emits its local observations and the game
   session does not require the missing side's install.

---

### User Story 2 - Script authors listen via KubeJS when present (Priority: P1)

When KubeJS is installed on the same side as the bridge, a script author can
subscribe to a documented message-event API and receive the same observations
as User Story 1 during normal script loaders (client and/or server scripts as
appropriate). When KubeJS is not installed, the bridge still loads and chat
observation still works for any native listeners.

**Why this priority**: The stated consumer is KubeJS; soft integration is a
constitution requirement and must be proven in MVP.

**Independent Test**: With KubeJS present, a minimal script logs each chat
observation. Without KubeJS, the same world still loads with the bridge alone.

**Acceptance Scenarios**:

1. **Given** the bridge and KubeJS are both installed on a side, **When** a chat
   observation occurs, **Then** a subscribed KubeJS handler runs and can read
   the plain-text payload.
2. **Given** the bridge is installed and KubeJS is not, **When** the game
   starts on that side, **Then** the bridge loads without crashing and chat
   observation remains available to non-KubeJS listeners.

---

### User Story 3 - Optional logger observation (Priority: P2)

A pack author can enable observation of logger/console-style log lines (not
only chat HUD text). When enabled, each qualifying log line emits an event with
plain text, logger identity, and severity. When disabled, no logger events are
emitted.

**Why this priority**: The product ask includes logs, but unfiltered logging is
noisy; it ships behind configuration after chat works.

**Independent Test**: Enable logger observation, trigger an info-level log line,
confirm an event; disable it and confirm silence.

**Acceptance Scenarios**:

1. **Given** logger observation is enabled with a minimum severity, **When** a
   log line at or above that severity is written, **Then** an observation event
   is emitted with text, logger identity, and severity.
2. **Given** logger observation is disabled, **When** log lines are written,
   **Then** no logger observation events are emitted.

---

### Edge Cases

- Duplicate perception of the same text on outbound and inbound paths MUST be
  distinguishable via side/phase metadata so scripts can ignore duplicates.
- Empty or whitespace-only chat lines SHOULD still be observable if the game
  would show them; scripts decide whether to ignore them.
- Rapid chat spam MUST NOT crash the bridge; dropping or coalescing is out of
  scope for MVP beyond surviving the load.
- Formatted rich text MUST still yield a usable plain-text form.
- Logger observation MUST NOT recursively observe the bridge's own emission
  chatter in a tight loop.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST emit an observation for each chat HUD line visible on
  a client that has the bridge installed, including player-authored and
  server-delivered lines.
- **FR-002**: System MUST emit an observation for chat and system broadcast
  traffic handled on a dedicated server that has the bridge installed, without
  requiring client installation.
- **FR-003**: Each chat observation MUST include plain text and metadata for
  logical side and message phase (at least player-send vs delivery/system).
- **FR-004**: Installing the bridge on only one side MUST be sufficient for that
  side's observations; the opposite side MUST remain optional.
- **FR-005**: When a supported script host (KubeJS) is present on the same side,
  script authors MUST be able to subscribe to chat observations through a
  documented event surface.
- **FR-006**: When that script host is absent, the bridge MUST still load and
  MUST still perform chat observation for native consumers.
- **FR-007**: System MUST provide a configuration-controlled logger observation
  path that can be turned off and filtered by minimum severity.
- **FR-008**: Observation in MVP MUST be read-only: emitting events MUST NOT by
  itself cancel, rewrite, or suppress the underlying chat or log line.
- **FR-009**: Event metadata MUST be sufficient for a script to detect when the
  same logical message was observed more than once across phases.

### Key Entities

- **Chat Observation**: A single chat-visible or server-broadcast message
  sighting, with plain text, side, phase, and optional speaker identity.
- **Log Observation**: A single logger line sighting, with plain text, logger
  identity, severity, and side.
- **Install Side**: Client or dedicated server instance where the bridge is
  present; observations are local to that side.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In a client-only install, 100% of messages that appear in the chat
  HUD during a manual test session produce a matching observation with the same
  plain text (sample of at least player chat plus one system/broadcast line).
- **SC-002**: In a dedicated-server-only install, player chat and one system
  broadcast each produce a server-side observation without any client install.
- **SC-003**: A KubeJS script subscribed to the documented chat event receives
  observations within the same game tick window as the chat appearing (manual
  verification: handler log lines align with chat).
- **SC-004**: Removing KubeJS while keeping the bridge installed still yields a
  successful world join on that side (no hard crash at startup).
- **SC-005**: With logger observation disabled, a five-minute idle session emits
  zero logger observation events; with it enabled at info level, a deliberate
  info log produces at least one event.

## Assumptions

- Target environment is Minecraft 1.21.1 with NeoForge, matching the reference
  project toolchain unless retargeted later.
- Primary script consumer is KubeJS on NeoForge 1.21; other script hosts are out
  of scope for MVP.
- MVP does not sync observations between client and server through this mod.
- Plain text is derived from the game's normal string form of the message;
  perfect recreation of every style code is not required.
- Default logger observation is off or tightly filtered so chat MVP remains
  usable out of the box.
- Single-player integrated server counts as exercising both logical paths only
  when separately validated behaviors are also checked on dedicated server and
  client-only installs as required by the constitution.
