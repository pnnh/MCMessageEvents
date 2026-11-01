# Research: Message Events MVP

## 1. Client chat capture points

**Decision**: Subscribe to NeoForge `ClientChatEvent` (outbound player send) and
`ClientChatReceivedEvent` (inbound player + system HUD lines). Skip
`ClientChatReceivedEvent.System` when `isOverlay()` is true for MVP.

**Rationale**: These events fire on the logical client bus and cover “about to
send” versus “about to show in chat HUD”. Overlay/action-bar text is not the
chat HUD and would confuse pack scripts.

**Alternatives considered**:
- Mixin into `ChatComponent.addMessage` only — more complete but heavier and
  harder to classify phase/speaker; keep as fallback if NeoForge events miss a
  path in implementation testing.
- Rely solely on received events — loses an explicit player-send phase before
  server round-trip.

## 2. Server player chat capture

**Decision**: Subscribe to NeoForge `ServerChatEvent` on the logical server.

**Rationale**: Fires when a `ServerboundChatPacket` is accepted; exposes raw
text, username, player, and message component. Sufficient for FR-002 player
chat without requiring client install.

**Alternatives considered**:
- Mixin into `PlayerList.broadcastChatMessage` — redundant with
  `ServerChatEvent` for ordinary player chat; reserve if signed-chat edge cases
  appear during implement.

## 3. Server system / broadcast capture

**Decision**: Mixin into both public `PlayerList.broadcastSystemMessage`
overloads; emit `MessagePhase.SYSTEM` observations. Do not cancel or alter
arguments.

**Rationale**: NeoForge 1.21.1 has no dedicated server system-message event.
`broadcastSystemMessage` is the common path for `/say`, many join/leave and
command feedback broadcasts.

**Alternatives considered**:
- `CommandEvent` — fires before execution, not when feedback text is produced;
  incomplete.
- Client-only system capture — fails dedicated-server-only install (FR-002 /
  constitution I).

## 4. Observation event surface (NeoForge)

**Decision**: Define non-cancellable `ChatObservedEvent` and `LogObservedEvent`
on `NeoForge.EVENT_BUS`, emitted only through `ObservationDispatcher`. Captures
MUST NOT call `setCanceled` / `setMessage` on vanilla NeoForge chat events.

**Rationale**: Constitution II requires observe-only. Separate events give native
mods a stable API and let KubeJS forward without depending on vanilla chat
event types.

**Alternatives considered**:
- Expose only vanilla events to KubeJS `NativeEvents` — works but duplicates
  side/phase modeling and skips server system mixin traffic unless scripts also
  learn the mixin-backed event.

## 5. Soft KubeJS integration

**Decision**:
- Keep `kubejs` as `optional` in `neoforge.mods.toml`.
- `compileOnly` + `localRuntime` on `dev.latvian.mods:kubejs-neoforge:2101.7.2-build.379`.
- Implement `KubeJSPlugin` under `pnnh.mcmessageevents.kubejs`, listed in
  `kubejs.plugins.txt`.
- Register `EventGroup.of("MessageEvents")` with `client`/`server` handlers for
  `chat` and `log` as appropriate.
- Plugin `init` registers a NeoForge listener that maps our observation events
  into KubeJS `KubeEvent` posts. Main `@Mod` never references KubeJS types.

**Rationale**: Matches KubeJS 7.x addon guidance and constitution III. Plugin
class loads only when KubeJS discovers `kubejs.plugins.txt`.

**Alternatives considered**:
- Hard `required` dependency — rejected by constitution.
- Reflection-only posts without EventGroup — poorer script UX; still useful as
  NativeEvents fallback documented in contracts.

## 6. Plain-text extraction

**Decision**: Use `Component.getString()` (or equivalent resolved string) at emit
time for chat payloads. For `ClientChatEvent` / `ServerChatEvent` raw string
fields, prefer the API’s raw/original message string when already plain text.

**Rationale**: Spec allows imperfect style fidelity; scripts need stable plain
text. Richer component trees can wait for a later feature.

**Alternatives considered**: Serialize full component JSON — heavier and not
required for MVP success criteria.

## 7. Logger bridge

**Decision**: Implement a Log4j2 `Appender` installed on the root logger when
COMMON config `loggerObservationEnabled=true`. Filter by configured minimum
level (default `INFO` when enabled). Default **enabled=false**. Use a
`ThreadLocal` recursion guard and ignore loggers under
`pnnh.mcmessageevents` (and optionally `dev.latvian.mods.kubejs` while posting)
to prevent feedback loops. Marshal appender callbacks onto the game thread
before dispatching observation events if the appender thread is not the game
thread.

**Rationale**: Constitution IV and SC-005. Idle sessions must emit zero log
observations when disabled.

**Alternatives considered**:
- Always-on INFO bridge — too noisy for pack scripts.
- Mixin into Mojang `Logger` wrappers — fragile across mappings.

## 8. Dual-side class loading

**Decision**: Annotate client capture with `@EventBusSubscriber(Dist.CLIENT)` (or
equivalent registration only on client). Keep mixins that touch shared
`PlayerList` server-safe. No `Dist.CLIENT`-only classes referenced from common
static initializers.

**Rationale**: Constitution I — dedicated server must not load client chat UI
types.

## 9. Duplicate observations

**Decision**: Every chat payload includes `ObservationSide` (`CLIENT` |
`SERVER`) and `MessagePhase` (`PLAYER_SEND` | `DELIVERED` | `SYSTEM`). Scripts
de-dupe; the mod does not suppress second sightings.

**Rationale**: FR-009 / constitution additional constraints. Suppressing in-mod
risks hiding legitimate dual-path visibility when both sides are installed.

## 10. Action bar / overlay

**Decision**: Exclude overlay system messages from MVP chat observations.

**Rationale**: Spec focuses on chat HUD; overlay is a different UI surface.
Can be added later behind config if needed.
