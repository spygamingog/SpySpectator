# SpySpectator — Project Brain & Agent Guide

> **Agent Ingestion Notice**: Read this file first. Do NOT re-analyze the entire repository from scratch. Keep this document and all sister documentation files (`README.md`, `DOCUMENTATION.md`, `ISSUES_AND_FLAWS.md`) updated with the latest summarized main changes. Do not append repetitive micro-histories.

---

## 1. Project Overview

| Property | Value |
| :--- | :--- |
| **Plugin Name** | [SpySpectator](file:///e:/Vaibhav/Projects/SpySpectator) |
| **Platform Target** | Paper / Purpur / Spigot (1.21.1+), Folia |
| **Java Version** | Java 21 (`pom.xml`) |
| **Version** | `3.0.1` |
| **Build Tool** | Apache Maven (`mvn clean package`) |
| **Main Package** | `com.spygamingog.spyspectator` |
| **Author** | SpyGamingOG |

---

## 2. Core Architecture & Design Patterns

The plugin implements a **custom Adventure-mode spectator engine** with native first-person camera attachment capabilities.

```
Player enters Spectator
       │
       ▼
1. Fire cancelable PlayerSpectateEvent
2. Cache Return Location (RAM + spectators.yml)
3. Serialize & Cache Inventory & Armor (Lossless Base64 to spectators.yml)
4. Clear Inventory & Give Configured Locked Tool Items:
   - Slot 2: Paper (Chat Settings)
   - Slot 4: Compass (Teleporter GUI)
   - Slot 6: Ender Eye (Visibility Settings)
   - Slot 8: Red Bed (Leave Spectator)
5. Set GameMode.ADVENTURE
6. Set Flight = true, Invulnerable = true, Silent = true, Fly/Walk speeds from config
7. Grant Night Vision effect
8. Hide player via Bukkit hidePlayer/showPlayer matrix
       │
       ├─► Right-click Player ──► Attach First-Person Camera (GameMode.SPECTATOR + setSpectatorTarget)
       │                              │
       │                              └─► Sneak (Shift) ──► Detach Camera back to Adventure Flight
       │
       ▼
Player leaves Spectator
       │
1. Fire cancelable PlayerUnspectateEvent
2. Restore Survival GameMode, Speeds, Invulnerability, Collisions
3. Restore exact Survival Inventory & Armor from memory/Base64
4. Async Teleport (player.teleportAsync) to Return Location or Configured Lobby
5. Restore Player Visibility across all clients
```

---

## 3. Directory & Source Code Map

```
SpySpectator/
├── pom.xml                                   # Paper API 1.21.1, Lombok, SpyCore 1.0.5
├── project_brain.md                          # Persistent agent memory & guidelines (this file)
├── README.md                                 # Public presentation for GitHub & Modrinth
├── DOCUMENTATION.md                          # Comprehensive technical & administrative docs
├── ISSUES_AND_FLAWS.md                       # System audit & resolved problem log
├── changelog.md                              # Modrinth version release notes
├── apidoc.md                                 # Developer API documentation & integration guide
├── src/main/resources/
│   ├── plugin.yml                            # Plugin manifest, commands, permissions, dependencies
│   ├── config.yml                            # Full configuration defaults (wired in 3.0.1)
│   └── banner.txt                            # Startup ASCII art
└── src/main/java/com/spygamingog/spyspectator/
    ├── SpySpectator.java                     # Main JavaPlugin entry point (registers /spectate & /spectator)
    ├── api/
    │   ├── SpySpectatorAPI.java              # Public static API facade
    │   └── events/
    │       ├── PlayerSpectateEvent.java      # Cancelable event fired before entering spectator
    │       └── PlayerUnspectateEvent.java    # Cancelable event fired before leaving spectator
    ├── commands/
    │   ├── SpectatorCommand.java             # Executor with selector (@s, @p, @a, @r), console, switch, reload
    │   └── SpectatorTabCompleter.java        # Tab completion logic with selectors and subcommands
    ├── gui/
    │   ├── SpectatorGUI.java                 # Paginated 54-slot Player Teleporter with PDC UUID keys
    │   └── SpectatorSettingsGUI.java         # Chat & Visibility toggle menu with PDC UUID keys
    ├── listeners/
    │   └── SpectatorListener.java            # Event handler (chat, async chunk check, interact, offhand, sneak)
    └── utils/
        ├── FoliaSchedulerHelper.java         # Folia EntityScheduler reflection helper
        ├── InventorySerializer.java          # Lossless Base64 ItemStack[] serializer
        ├── SchedulerUtils.java               # Scheduler abstraction (Folia vs Bukkit)
        └── SpectatorManager.java             # Core state manager, async teleports, config messages & persistence
```

---

## 4. Key Component Summary

| Class / File | Purpose & Responsibilities |
| :--- | :--- |
| [`SpySpectator.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/SpySpectator.java) | Plugin lifecycle. Calls `saveDefaultConfig()`, registers `/spectator` and `/spectate` commands and tab completers, registers listeners. |
| [`SpectatorManager.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/utils/SpectatorManager.java) | State management, Base64 inventory persistence, first-person camera targeting, async teleports (`teleportAsync`), dynamic config messages, and visibility matrix. |
| [`InventorySerializer.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/utils/InventorySerializer.java) | Lossless Base64 serialization and deserialization of `ItemStack[]` using `BukkitObjectInputStream`/`BukkitObjectOutputStream`. |
| [`SpectatorCommand.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/commands/SpectatorCommand.java) | Executes `/spectator` and `/spectate`. Resolves `@s`, `@p`, `@a`, `@r`, `@e` via `Bukkit.selectEntities`. Supports console usage, reload, switch, and direct spectate. |
| [`SpectatorTabCompleter.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/commands/SpectatorTabCompleter.java) | Dynamic completions for subcommands (`leave`, `lobby`, `reload`, `switch`), player names, and selectors (`@s`, `@p`, `@a`, `@r`). |
| [`SpectatorListener.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/listeners/SpectatorListener.java) | Non-blocking chunk checking (`isChunkLoaded`), off-hand swap cancellation, first-person right-click and sneak handling, PDC GUI click handling. |
| [`SpectatorGUI.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/gui/SpectatorGUI.java) | 54-slot paginated GUI with PersistentDataContainer UUID storage, health, gamemode, and world lore. |
| [`SpectatorSettingsGUI.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/gui/SpectatorSettingsGUI.java) | Chat and visibility settings menu using PersistentDataContainer UUID storage. |
| [`SpySpectatorAPI.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/api/SpySpectatorAPI.java) | Public static API facade for external plugins (`isSpectator`, `enableSpectator`, `disableSpectator`, `getSpectators`). |

---

## 5. Target Operators (`@s`, `@p`, `@a`, `@r`, `@e`) Status

- **Status**: **FULLY SUPPORTED** (as of v3.0.1).
- **Implementation**:
  - `SpectatorCommand.resolveTargets(sender, arg)` parses Minecraft selectors starting with `@` via `Bukkit.selectEntities(sender, selectorOrName)`.
  - Filtered to online `Player` entities.
  - Supports `/spectate @p`, `/spectator switch @a`, `/spectator leave @s`, etc.
  - Tab completion suggests `@s`, `@p`, `@a`, `@r`.

---

## 6. Rules for Future AI Agents

1. **Keep Documentation Synchronized**:
   - Any modifications to commands, configurations, permissions, or API methods must be reflected across `project_brain.md`, `README.md`, `DOCUMENTATION.md`, and `ISSUES_AND_FLAWS.md`.
2. **Do Not Bloat Change History**:
   - Never append incremental change-after-change changelogs inside this brain file.
   - Maintain only the **current state snapshot** and update the **Summarized Last Main Change** section below.
3. **Paper/Folia Standards**:
   - Always use `player.teleportAsync` and non-loading chunk lookups (`world.isChunkLoaded(x >> 4, z >> 4)`).

---

## 7. Summarized Last Main Change

- **Version**: `3.0.1` (Release)
- **Date**: 2026-09-04
- **Summary**: Comprehensive upgrade fixing critical inventory restart data loss (lossless Base64 persistence in `spectators.yml`), removing phantom `SpyInventories` dependency, registering `/spectate`, implementing `@s`/`@p`/`@a`/`@r`/`@e` target operator resolution, adding console commands & reload, resolving chunk loading lag via `isChunkLoaded`, implementing native first-person spectating (right-click to spectate, shift to exit), blocking offhand 'F' swap exploit, adding GUI pagination & PersistentDataContainer UUID click handling, and wiring all `config.yml` messages and item settings.
