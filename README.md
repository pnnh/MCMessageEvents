# MCMessageEvents

NeoForge mod for Minecraft **1.21.1** that exposes **chat HUD traffic** and optional **logger lines** as observation events for pack scripts (especially **KubeJS**) and other mods.

Client-only and dedicated-server-only installs are both supported. Neither side requires the other.

---

## English

### Features

- **Chat observation** (always on when the mod is installed on that side)
  - Client: outgoing player chat and messages delivered into the chat HUD (overlay/action bar excluded)
  - Dedicated server: player chat and system broadcasts (e.g. `/say`)
- **KubeJS soft integration** (optional)
  - `MessageEvents.chat` / `MessageEvents.log` when KubeJS is present
  - Mod still loads and fires NeoForge bus events if KubeJS is absent
- **Logger observation** (optional, off by default)
  - Configurable Log4j bridge with minimum severity filter
- **Observe-only**: events do not cancel or rewrite vanilla chat or logs

### Requirements

| Item | Version |
|------|---------|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.251+ (build pin in `gradle.properties`) |
| Java (dev) | 21 |
| KubeJS | Optional, 2101.7.x NeoForge |

### Installation

1. Install NeoForge for Minecraft 1.21.1 on the client and/or dedicated server.
2. Drop the mod jar into `mods/`.
3. Optionally install KubeJS on the **same** side if you want script listeners.

`displayTest` is set so mismatched client/server installs do not block joining.

### KubeJS usage

Place scripts in `kubejs/client_scripts` and/or `kubejs/server_scripts` on the side where this mod is installed.

```js
MessageEvents.chat(event => {
  console.info(`[chat ${event.side}/${event.phase}] ${event.plainText}`)
})

MessageEvents.log(event => {
  console.info(`[log ${event.severity}] ${event.loggerName}: ${event.plainText}`)
})
```

**Chat event fields:** `plainText`, `side` (`CLIENT` \| `SERVER`), `phase` (`PLAYER_SEND` \| `DELIVERED` \| `SYSTEM`), `speakerName`, `speakerUuid`, `gameTimeMillis`

**Log event fields:** `plainText`, `side`, `loggerName`, `severity`, `gameTimeMillis`

The same logical message may be observed more than once (e.g. send then deliver). Scripts should filter using `side` / `phase` if needed.

Without KubeJS, listen on the NeoForge game bus to:

- `pnnh.mcmessageevents.observe.ChatObservedEvent`
- `pnnh.mcmessageevents.observe.LogObservedEvent`

### Configuration

COMMON config (`mcmessageevents-common.toml`):

| Key | Default | Meaning |
|-----|---------|---------|
| `logger_observation.enabled` | `false` | Enable Log4j observation |
| `logger_observation.minimumLevel` | `INFO` | Minimum severity when enabled (`TRACE`…`FATAL`) |

### Building

```bat
gradlew.bat build
```

Dev runs:

```bat
gradlew.bat runClient
gradlew.bat runServer
```

If HTTPS fails with a PKIX certificate error on Windows:

```powershell
$env:JAVA_TOOL_OPTIONS="-Djavax.net.ssl.trustStoreType=Windows-ROOT"
.\gradlew.bat build
```

### Spec Kit / docs

Feature specs and contracts live under `specs/001-message-events-mvp/`.  
Background notes: `docs/可行性.md`.

### License

All Rights Reserved (see `mod_license` in `gradle.properties`).

---

## 中文

### 功能

- **聊天观察**（本端安装后默认开启）
  - 客户端：玩家发出的聊天，以及进入聊天 HUD 的消息（不含动作栏 overlay）
  - 专用服务端：玩家聊天与系统广播（如 `/say`）
- **KubeJS 软集成**（可选）
  - 安装 KubeJS 时可使用 `MessageEvents.chat` / `MessageEvents.log`
  - 未安装 KubeJS 时模组仍可加载，并照常发出 NeoForge 总线事件
- **日志观察**（可选，默认关闭）
  - 可配置的 Log4j 桥，支持最低级别过滤
- **只读观察**：不会取消或改写原版聊天/日志

### 环境要求

| 项目 | 版本 |
|------|------|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.251+（以 `gradle.properties` 钉死版本为准） |
| 开发 JDK | 21 |
| KubeJS | 可选，NeoForge 2101.7.x |

### 安装

1. 在客户端和/或专用服务端安装 Minecraft 1.21.1 的 NeoForge。
2. 将模组 jar 放入 `mods/`。
3. 若要用脚本监听，在**同一端**再安装 KubeJS。

`displayTest` 已配置为允许客户端与服务端不同时装模，互不强制。

### KubeJS 用法

在安装了本模组的一端，于 `kubejs/client_scripts` 和/或 `kubejs/server_scripts` 中编写：

```js
MessageEvents.chat(event => {
  console.info(`[chat ${event.side}/${event.phase}] ${event.plainText}`)
})

MessageEvents.log(event => {
  console.info(`[log ${event.severity}] ${event.loggerName}: ${event.plainText}`)
})
```

**聊天事件字段：** `plainText`、`side`（`CLIENT` \| `SERVER`）、`phase`（`PLAYER_SEND` \| `DELIVERED` \| `SYSTEM`）、`speakerName`、`speakerUuid`、`gameTimeMillis`

**日志事件字段：** `plainText`、`side`、`loggerName`、`severity`、`gameTimeMillis`

同一条消息可能被观察多次（例如先发送再送达）。脚本可按 `side` / `phase` 自行去重。

无 KubeJS 时可监听 NeoForge 游戏总线：

- `pnnh.mcmessageevents.observe.ChatObservedEvent`
- `pnnh.mcmessageevents.observe.LogObservedEvent`

### 配置

COMMON 配置（`mcmessageevents-common.toml`）：

| 键 | 默认 | 说明 |
|----|------|------|
| `logger_observation.enabled` | `false` | 是否启用 Log4j 观察 |
| `logger_observation.minimumLevel` | `INFO` | 启用时的最低级别（`TRACE`…`FATAL`） |

### 构建

```bat
gradlew.bat build
```

开发运行：

```bat
gradlew.bat runClient
gradlew.bat runServer
```

若 Windows 上 HTTPS 出现 PKIX 证书错误：

```powershell
$env:JAVA_TOOL_OPTIONS="-Djavax.net.ssl.trustStoreType=Windows-ROOT"
.\gradlew.bat build
```

### Spec Kit / 文档

特性规格与契约见 `specs/001-message-events-mvp/`。  
背景说明见 `docs/可行性.md`。

### 许可

保留所有权利（见 `gradle.properties` 中的 `mod_license`）。
