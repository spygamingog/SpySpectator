# SpySpectator

SpySpectator is a high-performance, feature-rich spectator mode plugin designed for modern Minecraft servers. Built on the Paper API, it extends standard vanilla spectator capabilities by implementing complete player invisibility, persistence of inventories and positions across restarts, isolated chat channels, and interactive moderator utilities.

This plugin is optimized for Minecraft **1.21.11** and is compatible with Spigot, Paper, Folia, and Purpur server environments.

---

## Key Features

### Complete Player Occlusion
- **Vanish Integration**: Spectators are hidden from normal players and excluded from client renderings, packets, and server tablists.
- **Client-Side Self-Rendering**: Spectators retain visibility of their own avatars and equipment rather than relying on confusing invisibility potion effects.
- **Evasion & Night Vision**: Mobs ignore spectating players. Spectators automatically receive permanent night vision.

### State & Inventory Persistence
- **Inventory Caching**: Survival inventories and player configurations are stored securely when entering spectator mode and restored exactly upon exit.
- **Location Synchronization**: Players return to their precise origin coordinates when leaving spectator mode.
- **Persistence Across Restarts**: Active spectator statuses and exit parameters are saved to disk (`spectators.yml`), keeping players in spectator mode after server reboots.

### Interactive Spectator Utilities
Upon entering spectator mode, players receive a utility hotbar:
- **Chat Settings (Slot 1)**: Toggle spectator chat preferences or manage ignored players.
- **Player Teleporter (Slot 4)**: Right-click opens a graphical user interface displaying all online players for quick teleportation.
- **Visibility Toggle (Slot 7)**: Change spectator visibility preferences or hide specific spectators.
- **Leave Mode (Slot 8)**: Right-click to exit spectator mode and return to the lobby or survival location.

### Isolated Communication
- **Isolated Chat Channels**: Messages sent by spectators are filtered so they are only readable by other spectators.
- **World Group Synchronizations**: Spectators only see and communicate with other spectators residing within the same world groups.

---

## Developer Integrations

SpySpectator includes a developer API allowing third-party plugins to programmatically register spectators and listen to custom events:
- **SpySpectatorAPI**: Static interface for managing spectator states.
- **PlayerSpectateEvent / PlayerUnspectateEvent**: Cancelable Bukkit events for controlling entry and exit flows.

For detailed developer instructions, see the [API Documentation](https://github.com/spygamingog/SpySpectator/blob/main/apidoc.md).

---

## Commands and Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/spectator` | Toggle spectator mode for the executing player. | `spyspectator.use` (Default: OP) |
| `/spectator <player>` | Toggle spectator mode for another player. | `spyspectator.admin` |
| `/spectator lobby set` | Set the target lobby location when players exit spectator mode. | `spyspectator.admin` |
| `/spectator lobby remove` | Remove the configured lobby location. | `spyspectator.admin` |

---

## Installation

1. Download the latest compiled `SpySpectator.jar`.
2. Place the file into your server's `plugins/` directory.
3. Restart the server to initialize the default configurations.
4. (Optional) Define an exit lobby using `/spectator lobby set`.

---

## Configuration

The plugin manages two primary files inside its data directory:
- **config.yml**: Standard configuration parameters.
- **spectators.yml**: Flat-file database containing the UUIDs and restoration settings for active spectators. Do not modify this file manually while the server is active.

For a detailed history of changes, refer to the [Changelog](https://github.com/spygamingog/SpySpectator/blob/main/changelog.md).
