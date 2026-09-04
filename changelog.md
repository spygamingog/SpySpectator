# 👁️ SpySpectator v3.0.1 — Target Selectors, First-Person Spectating & Reboot Persistence

Welcome to **SpySpectator v3.0.1**! This major update brings essential stability fixes, full support for Minecraft target operators, native first-person spectating, and robust inventory persistence across server reboots.

---

## 🎯 What's New

### 1. 🏹 Minecraft Target Operator Support (`@s`, `@p`, `@a`, `@r`, `@e`)
Commands now support native Minecraft entity selectors:
- `/spectate @p`: Spectate the nearest player with a single command.
- `/spectator switch @a`: Toggle spectator mode for all players on the server (perfect for minigame events and server tournaments).
- `/spectator leave @a`: Mass exit spectator mode and safely restore everyone's inventory.
- Full auto-completion for `@s`, `@p`, `@a`, and `@r` in tab completions.

### 2. 🎥 Native First-Person Spectating
- **Right-click any player** while in spectator mode to lock directly into their first-person view!
- **Press Sneak (Shift)** at any time to smoothly detach and return to free-flight Adventure spectator mode.
- Fully integrated with `config.yml` settings (`stop-on-sneak`, `right-click-to-spectate`).

### 3. 💾 Lossless Inventory Reboot Persistence
- Fixed a critical data-loss bug where inventories held in RAM were deleted upon server reboots.
- Inventories and equipped armor are now securely serialized to Base64 in `spectators.yml`—ensuring 100% item preservation across reboots, reloads, and crashes.

### 4. ⚡ Zero-Lag Chunk Boundary Movement
- Replaced synchronous `Location.getChunk()` calls with non-blocking `world.isChunkLoaded(x, z)` verification.
- Spectators can no longer cause TPS drops or lag spikes by accidentally forcing unloaded chunks to generate.

### 5. 📄 Paginated Compass Teleporter GUI
- The Teleporter GUI now supports **dynamic pagination** (`« Previous Page` / `Next Page »`), allowing servers with 50+ players to browse effortlessly.
- Upgraded skull click handling to use `PersistentDataContainer` UUID keys, fixing issues where colored player names or prefixes prevented teleportation.

### 6. 🔒 Off-Hand Protection & Exploit Fixes
- Blocked the off-hand swap key ('F') for spectator items (`PlayerSwapHandItemsEvent`) to ensure utility tools remain firmly locked in their configured hotbar slots.
- Added safeguards for `/kill` and void deaths to prevent spectator utility items from dropping into the world.

### 7. ⚙️ Full Configuration & Admin Control
- Connected all settings, speeds, custom item names, lore, and messages in `config.yml`.
- Added `/spectator reload` to hot-reload configurations and messages without restarting the server.
- Enabled console sender execution for `/spectator reload`, `/spectator switch`, and `/spectator leave`.
- Removed phantom dependency `SpyInventories` from `plugin.yml`.

---

## 📦 Compatibility & Requirements
- **Minecraft**: 1.21.x (1.21.1+)
- **Server Platforms**: Paper, Purpur, Folia, Spigot, Bukkit
- **Java**: 21+

*Thank you for using SpySpectator! If you enjoy the plugin, please leave a review or star the repository on GitHub.*
