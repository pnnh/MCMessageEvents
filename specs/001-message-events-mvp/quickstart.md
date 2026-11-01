# Quickstart validation: Message Events MVP

Use this after implementation to prove the feature against
[spec.md](./spec.md) success criteria. Details of payloads:
[data-model.md](./data-model.md), [contracts/](./contracts/).

## Prerequisites

- JDK 21
- NeoForge MDG toolchain as in `gradle.properties` (1.21.1 / 21.1.251)
- Optional: KubeJS on the run classpath (`localRuntime` already declared)

## Build

```bat
gradlew.bat compileJava
```

Fix SSL/certificate issues on the host if the wrapper cannot download
distributions.

## A. Client chat observation (SC-001)

1. `gradlew.bat runClient`
2. Join a world (single-player is fine for this check).
3. Send a chat line as the local player.
4. Trigger a system/broadcast line that appears in the chat HUD (e.g. `/say`
   from the integrated server or another system message).
5. Confirm via temporary debug listener or KubeJS script that:
   - Player send produced `CLIENT` + `PLAYER_SEND` (and/or later `DELIVERED`).
   - HUD delivery produced `CLIENT` + `DELIVERED` with matching plain text.

**Pass**: Sampled HUD lines each have a matching observation with the same
plain text.

## B. Dedicated server without client mod (SC-002)

1. `gradlew.bat runServer` (bridge on server only).
2. Connect with a **vanilla or non-bridge** client.
3. Send player chat; run a command that broadcasts a system message (e.g.
   `/say hello`).
4. Confirm server-side observations: `SERVER` + `PLAYER_SEND` for chat,
   `SERVER` + `SYSTEM` for broadcast.

**Pass**: Both observations appear with no client install of this mod.

## C. KubeJS present (SC-003)

1. Ensure KubeJS is on the run (already via `localRuntime` in dev).
2. Add a minimal script using [kubejs-message-events.md](./contracts/kubejs-message-events.md):

```js
MessageEvents.chat(event => {
  console.info(`[kjs] ${event.side}/${event.phase}: ${event.plainText}`)
})
```

3. Reproduce chat from A or B on the matching script side.
4. Confirm script log lines align with chat appearance in the same session.

**Pass**: Handler runs for observed chat lines.

## D. KubeJS absent (SC-004)

1. Remove KubeJS from the run (comment `localRuntime` KubeJS line or use a
   run configuration without it).
2. Start `runClient` or `runServer` with only this mod.
3. Join/start successfully; chat observations still dispatch on the NeoForge
   bus.

**Pass**: No startup crash; world/session usable.

## E. Logger bridge (SC-005)

1. Leave COMMON config `loggerObservationEnabled=false` (default). Idle ~1–5
   minutes: **zero** `LogObservedEvent` / `MessageEvents.log` emissions.
2. Set `loggerObservationEnabled=true`, `loggerMinimumLevel=INFO`, reload or
   restart.
3. Cause an INFO log (mod init log or deliberate `LOGGER.info`).
4. Confirm at least one log observation with logger name + severity.

**Pass**: Off → silent; on → deliberate INFO observed.

## Independence checklist (constitution V)

- [ ] Client-only path verified (A)
- [ ] Dedicated-server-only path verified (B)
- [ ] Not relying solely on single-player integrated server for “both sides
      independent” claims
