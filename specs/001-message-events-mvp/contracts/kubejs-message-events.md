# Contract: KubeJS `MessageEvents`

**Audience**: Pack script authors (KubeJS 2101.7.x on NeoForge 1.21.1).

**Prerequisite**: Both `mcmessageevents` and `kubejs` installed on the same
side. If KubeJS is missing, this group does not exist; NeoForge events still
fire for other listeners.

**Group name**: `MessageEvents`

## Handlers

### `MessageEvents.chat`

| Property | Value |
|----------|-------|
| Script types | `common` handler — use in `client_scripts` and/or `server_scripts` on the install side |
| Payload | Chat observation fields from [data-model.md](../data-model.md) |

**Fields exposed on the event object** (camelCase for scripts):

- `plainText` (string)
- `side` (string enum: `CLIENT` \| `SERVER`)
- `phase` (string enum: `PLAYER_SEND` \| `DELIVERED` \| `SYSTEM`)
- `speakerName` (string \| null/undefined)
- `speakerUuid` (string \| null/undefined)
- `gameTimeMillis` (number)

**Example** (`server_scripts` or `client_scripts` as appropriate):

```js
MessageEvents.chat(event => {
  console.info(`[chat ${event.side}/${event.phase}] ${event.plainText}`)
})
```

### `MessageEvents.log`

| Property | Value |
|----------|-------|
| Script types | `common` handler — use in `client_scripts` and/or `server_scripts` |
| Payload | Log observation fields |

**Fields**:

- `plainText` (string)
- `side` (string)
- `loggerName` (string)
- `severity` (string)
- `gameTimeMillis` (number)

**Example**:

```js
MessageEvents.log(event => {
  console.info(`[log ${event.severity}] ${event.loggerName}: ${event.plainText}`)
})
```

## Semantics

- Events are **observe-only**. There is no `event.cancel()` effect on vanilla
  chat or logging in MVP.
- Enabling logger observations requires COMMON config
  `loggerObservationEnabled=true` (see quickstart).
- Duplicate sightings across phases/sides are expected; scripts filter as needed.

## Discovery

Plugin class registered in `kubejs.plugins.txt`. ProbeJS dump (when available)
SHOULD list `MessageEvents` after a successful client/server start with both
mods present.
