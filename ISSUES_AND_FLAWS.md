# SpySpectator — System Audit, Flaws & Resolution Log

This document tracks all audited bugs, security flaws, performance pitfalls, and feature mismatches in [SpySpectator](file:///e:/Vaibhav/Projects/SpySpectator), along with their verified resolution in **v3.0.1**.

---

## 🚨 Critical Severity Issues

### 1. Severe Item Loss on Server Restart / Reload
- **Location**: [`SpectatorManager.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/utils/SpectatorManager.java)
- **Status**: ✅ **FIXED in v3.0.1**
- **Resolution**:
  Created [`InventorySerializer.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/utils/InventorySerializer.java) using `BukkitObjectOutputStream`/`BukkitObjectInputStream` to losslessly serialize `ItemStack[]` arrays to Base64 in `spectators.yml`. On startup, `loadSpectators()` deserializes survival inventories and armor into memory. When a spectator exits after a reboot, their exact survival items and equipped armor are 100% restored.

---

### 2. Phantom Dependency in `plugin.yml`
- **Location**: [`plugin.yml`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/resources/plugin.yml)
- **Status**: ✅ **FIXED in v3.0.1**
- **Resolution**:
  Removed non-existent `SpyInventories` from the `depend` list in `plugin.yml`. The plugin now cleanly enables on standard Paper, Purpur, and Folia servers.

---

### 3. Missing Registration of `/spectate` & Permission Lockout
- **Location**: [`SpySpectator.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/SpySpectator.java), [`plugin.yml`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/resources/plugin.yml)
- **Status**: ✅ **FIXED in v3.0.1**
- **Resolution**:
  - Registered `/spectate` with executor and tab completer in `SpySpectator.java`.
  - Changed `/spectator` permission in `plugin.yml` to `spyspectator.use` (defaulting to true) so normal players can toggle spectator mode.
  - Subcommands like `reload` and `lobby` are guarded by `spyspectator.admin`.

---

## ⚠️ High Severity Issues

### 4. Zero Support for Target Operators (`@s`, `@p`, `@a`, `@r`, `@e`)
- **Location**: [`SpectatorCommand.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/commands/SpectatorCommand.java)
- **Status**: ✅ **FIXED in v3.0.1**
- **Resolution**:
  Implemented entity selector parsing in `SpectatorCommand.resolveTargets()` utilizing Paper's `Bukkit.selectEntities(sender, selector)`. Selectors like `@p`, `@a`, `@s`, and `@r` are now supported across `/spectate` and `/spectator switch`.

---

### 5. Chunk Loading Anti-Pattern in `PlayerMoveEvent`
- **Location**: [`SpectatorListener.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/listeners/SpectatorListener.java)
- **Status**: ✅ **FIXED in v3.0.1**
- **Resolution**:
  Replaced synchronous `event.getTo().getChunk()` with non-blocking `event.getTo().getWorld().isChunkLoaded(x >> 4, z >> 4)`. Unloaded chunk boundaries are safely blocked without forcing chunk generation or TPS drops.

---

### 6. Folia Multi-Threading Crashes (`teleportAsync`)
- **Location**: [`SpectatorManager.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/utils/SpectatorManager.java), [`SpectatorCommand.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/commands/SpectatorCommand.java)
- **Status**: ✅ **FIXED in v3.0.1**
- **Resolution**:
  All teleport operations upgraded to `player.teleportAsync(...)`, ensuring asynchronous thread safety on Folia and modern Paper.

---

## ⚡ Medium Severity Issues

### 7. Disconnection of `config.yml`
- **Location**: [`config.yml`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/resources/config.yml), [`SpectatorManager.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/utils/SpectatorManager.java)
- **Status**: ✅ **FIXED in v3.0.1**
- **Resolution**:
  - `SpySpectator.onEnable()` calls `saveDefaultConfig()`.
  - All messages, custom item names, lores, hotbar slots, and flight/walk speeds are dynamically loaded from `config.yml`.
  - Implemented `/spectator reload` for runtime configuration reloads.

---

### 8. Unimplemented First-Person Spectating
- **Location**: [`SpectatorListener.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/listeners/SpectatorListener.java), [`SpectatorManager.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/utils/SpectatorManager.java)
- **Status**: ✅ **FIXED in v3.0.1**
- **Resolution**:
  Implemented native camera locking (`setSpectatorTarget(target)`). Right-clicking a player attaches to their first-person camera; sneaking (Shift) detaches and returns to free-flight.

---

### 9. Console Sender Lockout
- **Location**: [`SpectatorCommand.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/commands/SpectatorCommand.java)
- **Status**: ✅ **FIXED in v3.0.1**
- **Resolution**:
  Console can now run `/spectator reload`, `/spectator switch <player|@selector>`, and `/spectator leave <player|@selector>`.

---

### 10. Off-Hand Item Swap Bypass
- **Location**: [`SpectatorListener.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/listeners/SpectatorListener.java)
- **Status**: ✅ **FIXED in v3.0.1**
- **Resolution**:
  Added `@EventHandler` for `PlayerSwapHandItemsEvent`, preventing spectators from swapping hotbar items to their offhand.

---

### 11. Fragile Name Extraction in GUI Click Handlers
- **Location**: [`SpectatorGUI.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/gui/SpectatorGUI.java), [`SpectatorListener.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/listeners/SpectatorListener.java)
- **Status**: ✅ **FIXED in v3.0.1**
- **Resolution**:
  Player heads store target `UUID` directly inside the item's `PersistentDataContainer`. Click handlers read the UUID directly, eliminating string parsing errors.

---

### 12. Lack of GUI Pagination
- **Location**: [`SpectatorGUI.java`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/gui/SpectatorGUI.java)
- **Status**: ✅ **FIXED in v3.0.1**
- **Resolution**:
  Added multi-page pagination support with previous and next page navigation arrows.
