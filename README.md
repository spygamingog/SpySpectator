# 👁️ SpySpectator

[![Platform: Paper](https://img.shields.io/badge/Platform-Paper%20%7C%20Purpur%20%7C%20Folia-blue.svg)](https://papermc.io)
[![Minecraft: 1.21.1](https://img.shields.io/badge/Minecraft-1.21.1%2B-brightgreen.svg)](https://www.minecraft.net)
[![Java: 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java)
[![Version: 3.0.1](https://img.shields.io/badge/Release-v3.0.1-purple.svg)](https://github.com/spygamingog/SpySpectator)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**SpySpectator** is a modern, high-performance spectator mode, moderation, and minigame utility plugin tailored for Minecraft servers running Paper, Purpur, and Folia (1.21.1+).

By combining a customized, collision-free **Adventure Flight Engine** with native **First-Person Camera Locking** and **Minecraft Target Operators (`@s`, `@p`, `@a`, `@r`)**, SpySpectator delivers the ultimate spectator experience without vanilla noclip chunk lag.

---

## ✨ What's New in v3.0.1

- 🎯 **Target Operator Support**: Full support for `@s`, `@p`, `@a`, and `@r` in `/spectate` and `/spectator switch`.
- 🎥 **First-Person Camera Spectating**: Right-click any player while in spectator mode to lock directly into their view; sneak (Shift) to detach back into free-flight!
- 💾 **Lossless Inventory Reboot Persistence**: Inventories and armor are serialized in Base64—no item loss across server reboots.
- ⚡ **Zero Chunk-Loading Lag**: Replaced synchronous chunk loading with non-blocking loaded chunk verification.
- 📄 **Paginated Teleporter GUI**: Browse unlimited players across multiple pages with health, world, and gamemode indicators.
- 🔒 **Off-Hand Item Protection**: Spectator hotbar utility tools can no longer be moved or bypassed using the 'F' key.
- ⚙️ **Fully Wired `config.yml`**: Customize all messages, item slots, titles, lores, and flight speeds.

---

## 🛠️ Interactive Hotbar Tools

When entering spectator mode, players receive 4 locked utility items (slots and lores are configurable in `config.yml`):

- 🧭 **Compass (Slot 4)**: Opens an interactive, paginated GUI displaying online player heads for instant 1-click teleportation.
- 📜 **Paper (Slot 2)**: Left-click to toggle spectator chat; right-click to open a player-by-player ignore menu.
- 👁️ **Ender Eye (Slot 6)**: Left-click to toggle spectator visibility; right-click to hide specific spectators.
- 🛏️ **Red Bed (Slot 8)**: Right-click to exit spectator mode and safely restore your survival inventory.

---

## 💻 Commands & Permissions

| Command | Description | Permission | Default |
| :--- | :--- | :--- | :---: |
| `/spectator` | Toggle spectator mode for yourself | `spyspectator.use` | Everyone |
| `/spectator leave [player\|@selector]` | Exit spectator mode and restore inventory | `spyspectator.use` | Everyone |
| `/spectator switch <player\|@selector>` | Toggle spectator mode for target player(s) | `spyspectator.admin.switch` | OP |
| `/spectator reload` | Reload configuration settings and messages | `spyspectator.admin` | OP |
| `/spectator lobby [set\|remove]` | Manage the server exit spectator lobby | `spyspectator.admin` | OP |
| `/spectate [player\|@selector]` | Enter spectator mode and spectate a target | `spyspectator.use` | OP for others |

---

## 📥 Installation

1. Download the latest `SpySpectator-3.0.1.jar` release.
2. Ensure your server runs **Paper**, **Purpur**, or **Folia** on **Minecraft 1.21.1+** with **Java 21**.
3. Place `SpySpectator-3.0.1.jar` into your server's `plugins/` directory.
4. Restart your server.
5. (Optional) Run `/spectator lobby set` to define an exit spawn point.

---

## 🔧 Developer API

```java
import com.spygamingog.spyspectator.api.SpySpectatorAPI;
import org.bukkit.entity.Player;

// Check if player is spectating
boolean spectating = SpySpectatorAPI.isSpectator(player);

// Programmatically enable or disable spectator mode
SpySpectatorAPI.enableSpectator(player);
SpySpectatorAPI.disableSpectator(player, false, true);
```

Listen to cancelable events `PlayerSpectateEvent` and `PlayerUnspectateEvent` to control transitions in minigames and arenas.

---

## 📖 Documentation & Links

- 📚 **Full Documentation**: [DOCUMENTATION.md](DOCUMENTATION.md)
- 🔌 **API Documentation**: [apidoc.md](apidoc.md)
- 📝 **Release Changelog**: [changelog.md](changelog.md)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
