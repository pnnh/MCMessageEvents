<!--
Sync Impact Report
- Version change: (none) → 1.0.0
- Modified principles: initial ratification
- Added sections: Purpose, Core Principles I–V, Additional Constraints,
  Development Workflow, Governance
- Removed sections: none
- Follow-up TODOs: none
-->

# MCMessageEvents Constitution

## Purpose

MCMessageEvents is a **dual-side NeoForge bridge** that exposes Minecraft chat
HUD traffic and optional logger output as observable events. Pack authors and
scripts (especially KubeJS, when installed) MUST be able to react to messages
that players see in chat or that appear in game logs, on client and/or dedicated
server installs.

## Core Principles

### I. Independent Client and Server Installs

The mod MUST load on the client, on a dedicated server, or on both. Installing
on one side MUST NOT require the other side to install the same mod. The mod
MUST NOT invent a private cross-side protocol in order for basic observation to
work. Client-only code MUST stay off the dedicated-server classpath path.

Rationale: pack authors often want server-side scripting without forcing every
player to install an observation bridge, and vice versa for client-only tools.

### II. Observe First; Mutate Only When Specified

MVP behavior MUST be read-only observation: capture text and metadata, then
fire events. Cancelling, rewriting, filtering, or suppressing chat or logs MUST
NOT ship unless a later feature specification explicitly allows it.

Rationale: observation has clear value for scripting and audit-style tooling;
mutation raises compatibility and moderation risks that need a separate spec.

### III. Soft KubeJS Integration

KubeJS MUST be optional at load time. The published jar MUST start a world when
KubeJS is absent. Any KubeJS API types MUST be isolated from the main `@Mod`
entrypoint so missing KubeJS cannot cause a hard crash. When KubeJS is present,
the mod SHOULD expose a documented event group for scripts; NeoForge bus events
remain the integration baseline for non-KubeJS listeners.

Rationale: the bridge is useful beyond KubeJS, and hard-requiring KubeJS would
block clients or servers that only need native listeners later.

### IV. Chat HUD Completeness Over Logger Noise

Primary MVP value is **everything that enters the chat HUD** (player chat,
server broadcasts, system and command feedback as shown in chat). Bridging raw
Log4j/console logger lines is secondary and MUST be configurable (enablement and
minimum level) so scripts are not flooded by default.

Rationale: chat is what players and most pack scripts care about; unfiltered
logger bridges are a common source of lag and duplicate handling.

### V. Verify on the Dist That Changed

A feature that claims client behavior is not done until exercised with
`./gradlew runClient`. A feature that claims dedicated-server behavior is not
done until exercised with `./gradlew runServer` (or equivalent). Claims that
both sides work independently MUST be checked in separate installs, not only in
a combined single-player session. Pure text helpers MAY use unit tests without
starting the game.

## Additional Constraints

- Minecraft 1.21.1, NeoForge 21.1.251, Java 21 (aligned with the MCBilingual
  reference toolchain unless a feature plan intentionally retargets).
- Mod id `mcmessageevents`, package `pnnh.mcmessageevents`, distribution BOTH.
- `third_part/` holds local reference checkouts and MUST stay gitignored.
- Event payloads MUST identify logical side and message phase sufficiently for
  scripts to detect duplicates when the same text is observed outbound and
  inbound.
- Payload MUST expose a plain-text form of the message; richer component data
  MAY be added by later specs.
- Spec Kit governs delivery: specify → plan → tasks → implement, one active
  feature at a time.

## Development Workflow

Work follows Spec Kit:

1. Specify observable behavior and success criteria.
2. Plan design (`research.md`, `data-model.md`, `contracts/`, `quickstart.md`).
3. Generate tasks only when implementation is requested.
4. Implement only the active feature's tasks.

`docs/可行性.md` is background. A feature specification overrides background
notes when they disagree.

## Governance

This constitution overrides conflicting local practice. Feature plans MUST pass
the Constitution Check before implementation. A violation needs an entry in that
plan's Complexity Tracking.

Amendments change this file and bump the version:

- MAJOR: remove or redefine a principle.
- MINOR: add a principle or a constraint.
- PATCH: wording that does not change a rule.

**Version**: 1.0.0 | **Ratified**: 2026-11-01 | **Last Amended**: 2026-11-01
