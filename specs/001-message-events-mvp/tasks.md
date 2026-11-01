# Tasks: Message Events MVP

**Input**: Design documents from `/specs/001-message-events-mvp/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: Not requested for TDD; optional mapping unit test in polish only.

**Organization**: Tasks grouped by user story for independent delivery.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no incomplete dependencies)
- **[Story]**: US1 / US2 / US3 from spec.md
- File paths are exact

## Path Conventions

Single NeoForge project under `src/main/java/pnnh/mcmessageevents/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Ensure package layout and mod metadata match the plan

- [x] T001 Create package directories `config/`, `observe/`, `capture/`, `log/`, `mixin/`, `kubejs/` under `src/main/java/pnnh/mcmessageevents/`
- [x] T002 [P] Add mixin entry to `src/main/templates/META-INF/neoforge.mods.toml` for `mcmessageevents.mixins.json`
- [x] T003 [P] Create empty mixin config skeleton in `src/main/resources/mcmessageevents.mixins.json`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Observation model + dispatcher + mod bootstrap shared by all stories

**⚠️ CRITICAL**: No user story work until this phase completes

- [x] T004 [P] Add `ObservationSide` enum in `src/main/java/pnnh/mcmessageevents/observe/ObservationSide.java`
- [x] T005 [P] Add `MessagePhase` enum in `src/main/java/pnnh/mcmessageevents/observe/MessagePhase.java`
- [x] T006 [P] Add `ChatObservation` record in `src/main/java/pnnh/mcmessageevents/observe/ChatObservation.java`
- [x] T007 [P] Add `LogObservation` record in `src/main/java/pnnh/mcmessageevents/observe/LogObservation.java`
- [x] T008 [P] Add non-cancellable `ChatObservedEvent` in `src/main/java/pnnh/mcmessageevents/observe/ChatObservedEvent.java`
- [x] T009 [P] Add non-cancellable `LogObservedEvent` in `src/main/java/pnnh/mcmessageevents/observe/LogObservedEvent.java`
- [x] T010 Implement `ObservationDispatcher` in `src/main/java/pnnh/mcmessageevents/observe/ObservationDispatcher.java`
- [x] T011 Wire COMMON config registration stub in `src/main/java/pnnh/mcmessageevents/config/CommonConfig.java` and register from `src/main/java/pnnh/mcmessageevents/MCMessageEvents.java`

**Checkpoint**: Foundation ready — stories can proceed

---

## Phase 3: User Story 1 - Observe chat HUD on the installed side (Priority: P1) 🎯 MVP

**Goal**: Emit chat observations on client and dedicated server without needing the other side

**Independent Test**: Client-only: player chat + HUD system line → events; server-only: player chat + `/say` → events (quickstart A/B)

### Implementation for User Story 1

- [x] T012 [P] [US1] Implement client capture in `src/main/java/pnnh/mcmessageevents/capture/ClientChatCapture.java` (`ClientChatEvent`, `ClientChatReceivedEvent`, skip overlay)
- [x] T013 [P] [US1] Implement server player-chat capture in `src/main/java/pnnh/mcmessageevents/capture/ServerChatCapture.java` (`ServerChatEvent`)
- [x] T014 [US1] Implement `PlayerListSystemMessageMixin` in `src/main/java/pnnh/mcmessageevents/mixin/PlayerListSystemMessageMixin.java` and register in `src/main/resources/mcmessageevents.mixins.json`
- [x] T015 [US1] Ensure captures only observe (no cancel/rewrite) and route all emits through `ObservationDispatcher`

**Checkpoint**: US1 independently verifiable via NeoForge bus / debug log

---

## Phase 4: User Story 2 - Script authors listen via KubeJS when present (Priority: P1)

**Goal**: Soft KubeJS `MessageEvents.chat` (and forwarder) without hard dependency

**Independent Test**: With KubeJS: script logs chat; without KubeJS: mod still loads (quickstart C/D)

### Implementation for User Story 2

- [x] T016 [P] [US2] Add `ChatMessageKubeEvent` in `src/main/java/pnnh/mcmessageevents/kubejs/ChatMessageKubeEvent.java`
- [x] T017 [P] [US2] Add `LogMessageKubeEvent` stub-ready in `src/main/java/pnnh/mcmessageevents/kubejs/LogMessageKubeEvent.java` (used by US3; register handlers now)
- [x] T018 [US2] Define `MessageEventsGroup` in `src/main/java/pnnh/mcmessageevents/kubejs/MessageEventsGroup.java`
- [x] T019 [US2] Implement `MessageEventsKubeJSPlugin` in `src/main/java/pnnh/mcmessageevents/kubejs/MessageEventsKubeJSPlugin.java` (register group; forward `ChatObservedEvent` → KubeJS)
- [x] T020 [US2] Add `src/main/resources/kubejs.plugins.txt` pointing at the plugin class
- [x] T021 [US2] Verify `MCMessageEvents.java` has zero KubeJS type references

**Checkpoint**: US2 independently verifiable with/without KubeJS on classpath

---

## Phase 5: User Story 3 - Optional logger observation (Priority: P2)

**Goal**: Configurable Log4j bridge emitting `LogObservedEvent` / `MessageEvents.log`

**Independent Test**: Disabled → zero log events; enabled INFO → deliberate INFO observed (quickstart E)

### Implementation for User Story 3

- [x] T022 [US3] Complete logger fields in `src/main/java/pnnh/mcmessageevents/config/CommonConfig.java` (enabled default false, min level INFO)
- [x] T023 [US3] Implement `MessageLogAppender` in `src/main/java/pnnh/mcmessageevents/log/MessageLogAppender.java` with recursion guard
- [x] T024 [US3] Implement `LogBridge` install/remove in `src/main/java/pnnh/mcmessageevents/log/LogBridge.java` and hook config load/reload from `MCMessageEvents.java`
- [x] T025 [US3] Forward `LogObservedEvent` to KubeJS in `MessageEventsKubeJSPlugin.java` via `LogMessageKubeEvent`
- [x] T026 [US3] Marshal off-thread appender callbacks onto the game thread before `ObservationDispatcher` emit

**Checkpoint**: US3 independently verifiable via config toggle

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Hardening and lightweight verification aids

- [x] T027 [P] Add unit test for observation field mapping in `src/test/java/pnnh/mcmessageevents/observe/ChatObservationTest.java`
- [x] T028 [P] Add brief usage note linking contracts in `docs/可行性.md` or leave contracts as source of truth (prefer update `docs/可行性.md` with “已规划实现” pointer)
- [x] T029 Confirm `.gitignore` covers `.gradle/`, `build/`, `run/`, `third_part/`
- [x] T030 Compile with `gradlew.bat compileJava` (or document SSL blocker if host certs fail)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: immediate
- **Foundational (Phase 2)**: after Setup — BLOCKS stories
- **US1 (Phase 3)**: after Foundational — MVP chat
- **US2 (Phase 4)**: after Foundational; ideally after US1 emits exist (forwards chat)
- **US3 (Phase 5)**: after Foundational; KubeJS log forward needs T017/T019
- **Polish (Phase 6)**: after desired stories

### User Story Dependencies

- **US1**: no dependency on US2/US3
- **US2**: needs dispatcher + chat events from foundation/US1 to demonstrate chat scripts
- **US3**: needs config + dispatcher; KubeJS log forward reuses US2 plugin

### Parallel Opportunities

- T002–T003 parallel in Setup
- T004–T009 parallel in Foundational
- T012–T013 parallel in US1
- T016–T017 parallel in US2

---

## Parallel Example: User Story 1

```text
Task: "Implement ClientChatCapture.java"
Task: "Implement ServerChatCapture.java"
# then sequential mixin T014
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Setup + Foundational
2. US1 captures
3. Validate quickstart A/B before KubeJS/logger

### Incremental Delivery

1. US1 → NeoForge chat observations
2. US2 → KubeJS `MessageEvents.chat`
3. US3 → logger bridge + `MessageEvents.log`

---

## Notes

- Observe-only: never cancel vanilla chat events
- Keep KubeJS types out of `@Mod` class
- Overlay system messages excluded per research.md
