# Contract: NeoForge observation events

**Audience**: Java mods and any bus listener (including KubeJS `NativeEvents`).

**Bus**: `NeoForge.EVENT_BUS`

**Mutability**: Observation-only. Events MUST NOT implement cancellable mutation
APIs for MVP. Listeners MUST treat payloads as immutable snapshots.

## ChatObservedEvent

**Class (planned)**: `pnnh.mcmessageevents.observe.ChatObservedEvent`

**Fired when**: A chat observation is captured on the local install side.

**Payload accessors** (names indicative):

| Accessor | Returns |
|----------|---------|
| `getObservation()` | `ChatObservation` — see [data-model.md](../data-model.md) |
| convenience getters MAY mirror `plainText`, `side`, `phase`, etc. |

**Threading**: Game/main thread for chat captures. Listeners SHOULD NOT block.

**Guarantees**:
- One event per capture site listed in the data-model emission table.
- Does not alter vanilla chat delivery by being posted.
- Overlay/action-bar system messages are not posted in MVP.

**NativeEvents example** (informational):

```js
NativeEvents.onEvent('pnnh.mcmessageevents.observe.ChatObservedEvent', event => {
  const o = event.observation
  console.info(`[${o.side}/${o.phase}] ${o.plainText}`)
})
```

## LogObservedEvent

**Class (planned)**: `pnnh.mcmessageevents.observe.LogObservedEvent`

**Fired when**: Logger bridge is enabled and a qualifying log line is accepted.

**Payload accessors**:

| Accessor | Returns |
|----------|---------|
| `getObservation()` | `LogObservation` |

**Threading**: MAY originate off-thread from Log4j; dispatcher MUST hop to the
game thread before posting this event so script/mod listeners can touch game
state safely.

**Guarantees**:
- Zero events while `loggerObservationEnabled` is false.
- Self-logger lines under `pnnh.mcmessageevents` are never posted.
