# Implementation Plan: Message Events MVP

**Branch**: `001-message-events-mvp` | **Date**: 2026-11-01 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-message-events-mvp/spec.md`

## Summary

Expose read-only chat and optional logger observations as NeoForge bus events on
whichever side installs the mod, and forward them into a soft-loaded KubeJS
`MessageEvents` group when KubeJS is present. Client capture uses NeoForge chat
events; dedicated-server player chat uses `ServerChatEvent`; server system
broadcasts use a narrow `PlayerList` mixin. Logger capture is a configurable
Log4j appender (default off).

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Minecraft 1.21.1, NeoForge 21.1.251, ModDevGradle
2.0.148; optional compile/runtime KubeJS `2101.7.2-build.379` (soft load)

**Storage**: N/A (ephemeral observation events; NeoForge COMMON config for logger
toggle / min level)

**Testing**: JUnit 5 for plain-text helpers and payload mapping; manual
`runClient` / `runServer` per constitution; optional KubeJS script smoke when
localRuntime includes KubeJS

**Target Platform**: Minecraft client and/or dedicated server (NeoForge), Windows
dev host

**Project Type**: dual-side NeoForge mod (single Gradle project)

**Performance Goals**: Survive normal chat rates without crashing; logger bridge
MUST stay disabled by default so idle sessions emit zero log observations

**Constraints**: No hard KubeJS load dependency; no private cross-side protocol;
observe-only (do not cancel/rewrite chat or logs); client-only types must not
load on dedicated server; event payloads include side + phase for de-dupe

**Scale/Scope**: One mod jar; chat HUD + optional logger; KubeJS event group with
`chat` and `log` handlers; no persistence or mutation APIs in MVP

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Gate | Status | Notes |
|------|--------|-------|
| I. Independent client/server installs | PASS | `side=BOTH`, `displayTest=IGNORE_ALL_VERSION`; no cross-side sync |
| II. Observe-only | PASS | Custom events are non-cancellable observation records; captures never call `setCanceled` / `setMessage` |
| III. Soft KubeJS | PASS | `kubejs` optional in mods.toml; KubeJS types only under `kubejs/` loaded via `kubejs.plugins.txt` |
| IV. Chat first; logger configurable | PASS | Chat always on; logger COMMON config default `enabled=false` |
| V. Verify on changed dist | PASS | quickstart requires separate client-only and server-only checks |
| Toolchain pin 1.21.1 / 21.1.251 | PASS | Matches reference MCBilingual pin |
| Payload plain text + side/phase | PASS | See data-model.md |

**Post-design re-check**: PASS — design artifacts stay within the gates above.
Mixin use for server system messages does not violate observe-only or dual-side
independence (see Complexity Tracking).

## Project Structure

### Documentation (this feature)

```text
specs/001-message-events-mvp/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── neoforge-events.md
│   └── kubejs-message-events.md
└── tasks.md                 # /speckit-tasks — not created here
```

### Source Code (repository root)

```text
src/main/java/pnnh/mcmessageevents/
├── MCMessageEvents.java                 # @Mod BOTH; no KubeJS types
├── config/
│   └── CommonConfig.java                # logger enable + min level
├── observe/
│   ├── ObservationSide.java
│   ├── MessagePhase.java
│   ├── ChatObservation.java
│   ├── LogObservation.java
│   ├── ChatObservedEvent.java           # NeoForge bus, non-cancellable
│   ├── LogObservedEvent.java
│   └── ObservationDispatcher.java       # single emit path
├── capture/
│   ├── ClientChatCapture.java           # Dist.CLIENT subscribers
│   └── ServerChatCapture.java           # logical server subscribers
├── log/
│   ├── LogBridge.java                   # install/remove appender from config
│   └── MessageLogAppender.java          # Log4j2 Appender + recursion guard
├── mixin/
│   └── PlayerListSystemMessageMixin.java
└── kubejs/                              # loaded only by KubeJS plugin discovery
    ├── MessageEventsKubeJSPlugin.java
    ├── MessageEventsGroup.java
    ├── ChatMessageKubeEvent.java
    └── LogMessageKubeEvent.java

src/main/resources/
├── mcmessageevents.mixins.json
└── kubejs.plugins.txt

src/main/templates/META-INF/neoforge.mods.toml

src/test/java/pnnh/mcmessageevents/
└── observe/                             # plain-text / mapping unit tests
```

**Structure Decision**: Single NeoForge mod project. Capture → observation model →
NeoForge event dispatch; KubeJS package is a soft forwarder discovered only when
KubeJS is installed.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Mixin on `PlayerList.broadcastSystemMessage` | NeoForge has no first-party server system-broadcast observation event; FR-002 requires dedicated-server system/broadcast coverage without clients | Listening only to `ServerChatEvent` misses `/say`, join messages, and command system broadcasts |
