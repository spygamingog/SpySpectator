# SpySpectator — Complete Technical & Administrative Documentation (v3.0.1)

Welcome to the definitive documentation for [SpySpectator](file:///e:/Vaibhav/Projects/SpySpectator), an advanced spectator mode, moderation, and minigame plugin built for modern Minecraft servers running Paper, Purpur, and Folia (1.21.1+).

---

## 1. System Architecture

Unlike vanilla Minecraft's `GameMode.SPECTATOR`, which relies on client-side noclip and disables standard item interactions, SpySpectator operates on a **Custom Adventure-Mode Engine** combined with on-demand **First-Person Camera Locking**.

### Architecture Workflow Diagram

```
+-------------------------------------------------------------+
|              Player Triggers /spectator or /spectate         |
+-------------------------------------------------------------+
                               │
                               ▼
            +────────────────────────────────────+
            |  Call PlayerSpectateEvent (Bukkit) |
            +────────────────────────────────────+
                               │
              Is Cancelled? ───┴───► [ABORT]
                               │ No
                               ▼
      +──────────────────────────────────────────────────+
      | 1. Cache Survival Location to returnLocations    |
      | 2. Serialize Inventory & Armor (Lossless Base64) |
      | 3. Clear Inventory & Assign Locked Hotbar Tools  |
      | 4. Set GameMode.ADVENTURE                        |
      | 5. Enable Flight (setAllowFlight & setFlying)    |
      | 6. Set Silent, Invulnerable, and Non-Collidable  |
      | 7. Apply Night Vision Potion Effect              |
      | 8. Execute updateVisibility() across all players |
      +──────────────────────────────────────────────────+
                               │
            ┌──────────────────┴──────────────────┐
            ▼                                     ▼
 [Right-Click Player]                     [Free-Flight Mode]
 - Attach native first-person camera       - Use Teleporter GUI (Compass)
 - GameMode.SPECTATOR (target lock)        - Toggle Visibility (Ender Eye)
 - Shift/Sneak returns to free-flight      - Toggle Chat (Paper)
            │                                     │
            └──────────────────┬──────────────────┘
                               │
            Trigger /spectator leave or Bed Tool
                               │
                               ▼
            +──────────────────────────────────────+
            | Call PlayerUnspectateEvent (Bukkit)  |
            +──────────────────────────────────────+
                               │
              Is Cancelled? ───┴───► [ABORT]
                               │ No
                               ▼
      +──────────────────────────────────────────────────+
      | 1. Clear Spectator Tool Items                    |
      | 2. Restore Survival Inventory & Armor from Base64|
      | 3. Reset GameMode to SURVIVAL                    |
      | 4. Disable Flight & Invulnerability              |
      | 5. Remove Night Vision Effect                    |
      | 6. Async Teleport (player.teleportAsync):        |
      |    - To Lobby (if configured / requested)        |
      |    - Or to Return Location                       |
      | 7. Restore Player Visibility across all clients  |
      +──────────────────────────────────────────────────+
```

---

## 2. Target Operator Support (`@s`, `@p`, `@a`, `@r`, `@e`)

Starting in **v3.0.1**, all spectator commands support native Minecraft entity selectors:

- `@s`: The executing sender.
- `@p`: Nearest player.
- `@r`: Random online player.
- `@a`: All online players.

### Selector Command Examples:
- `/spectate @p`: Instantly enter spectator mode and spectate the nearest player.
- `/spectator switch @a`: Toggle spectator mode for every player on the server (ideal for minigames, battle royales, and event transitions).
- `/spectator leave @a`: Remove all spectators and safely restore their survival inventories.
- `/spectator switch @p`: Toggle spectator mode on the nearest player from console or command blocks.

---

## 3. Interactive Hotbar Utilities

When entering spectator mode, a player's inventory is cleared and populated with 4 locked utility items (slots and lore fully configurable in `config.yml`):

| Slot (Default) | Item | Name | Functionality |
| :---: | :--- | :--- | :--- |
| **2** | `PAPER` | **Chat Settings** | **Left-Click**: Toggle global spectator chat on/off.<br>**Right-Click**: Open the Chat Preferences GUI to ignore specific spectators. |
| **4** | `COMPASS` | **Player Teleporter** | **Right-Click**: Open the Paginated Teleporter GUI displaying online player heads with health and world lore. Clicking a head instantly teleports you. |
| **6** | `ENDER_EYE` | **Visibility Settings**| **Left-Click**: Toggle global spectator visibility.<br>**Right-Click**: Open the Visibility GUI to hide/show specific spectators. |
| **8** | `RED_BED` | **Leave Spectator** | **Right-Click**: Instantly leave spectator mode and return to your survival coordinates or the server spectator lobby. |

---

## 4. Commands & Permissions

### Commands Matrix

| Command | Arguments | Description | Default Permission |
| :--- | :--- | :--- | :--- |
| `/spectator` | *(none)* | Toggles spectator mode for the executing player. | `spyspectator.use` |
| `/spectator leave` | `[player\|@selector]` | Exits spectator mode. Admins can specify target(s). | `spyspectator.use` / `admin.switch` |
| `/spectator switch` | `<player\|@selector>` | Toggles spectator mode for targeted player(s). | `spyspectator.admin.switch` |
| `/spectator reload` | *(none)* | Reloads `config.yml` and updates cached settings. | `spyspectator.admin` |
| `/spectator lobby` | *(none)* | Teleports the player to the configured lobby. | `spyspectator.use` |
| `/spectator lobby set` | *(none)* | Sets the spectator exit lobby to current location. | `spyspectator.admin` |
| `/spectator lobby remove` | *(none)* | Clears the configured spectator exit lobby. | `spyspectator.admin` |
| `/spectate` | `[player\|@selector]` | Shortcut command to enter spectator & teleport/spectate target. | `spyspectator.use` / `spectate.others` |

### Permissions Hierarchy

```yaml
permissions:
  spyspectator.use:
    description: Allows toggling and using custom spectator mode.
    default: true
  spyspectator.spectate.others:
    description: Allows spectating and teleporting to other players via command or selectors.
    default: op
  spyspectator.admin:
    description: Full administrative control (lobby management, reloads).
    default: op
  spyspectator.admin.switch:
    description: Allows switching other players into or out of spectator mode (supports selectors).
    default: op
```

---

## 5. Developer API & Integration

### Adding Dependency

#### Maven
```xml
<dependency>
    <groupId>com.spygamingog</groupId>
    <artifactId>SpySpectator</artifactId>
    <version>3.0.1</version>
    <scope>provided</scope>
</dependency>
```

#### Gradle
```groovy
dependencies {
    compileOnly 'com.spygamingog:SpySpectator:3.0.1'
}
```

### Static API Usage ([`SpySpectatorAPI`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/api/SpySpectatorAPI.java))

```java
import com.spygamingog.spyspectator.api.SpySpectatorAPI;
import org.bukkit.entity.Player;
import java.util.Set;

// 1. Check if a player is spectating
boolean isSpectating = SpySpectatorAPI.isSpectator(player);

// 2. Put a player into custom spectator mode
SpySpectatorAPI.enableSpectator(player);

// 3. Remove a player from spectator mode (restores inventory & returns to origin)
SpySpectatorAPI.disableSpectator(player);

// 4. Remove with custom lobby and gamemode parameters
SpySpectatorAPI.disableSpectator(player, true, true);

// 5. Get all active spectators
Set<Player> spectators = SpySpectatorAPI.getSpectators();
```

---

## 6. Server & Platform Compatibility

- **Paper 1.21.1+**: Fully supported with native asynchronous teleportation and selector parsing.
- **Purpur 1.21.1+**: Fully supported.
- **Folia 1.21.1+**: Supported via `teleportAsync` and Folia `EntityScheduler`.
- **Spigot 1.21.1+**: Supported.
