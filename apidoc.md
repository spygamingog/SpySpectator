# SpySpectator Developer API Documentation (v3.0.1)

Expose custom spectator features, trigger first-person camera views, and interact with active spectators directly from your Minecraft server plugins.

---

## 📦 Dependency Configuration

### Maven
Add `SpySpectator` to your `pom.xml` dependencies:
```xml
<dependency>
    <groupId>com.spygamingog</groupId>
    <artifactId>SpySpectator</artifactId>
    <version>3.0.1</version>
    <scope>provided</scope>
</dependency>
```

### Gradle
Add the dependency to your `build.gradle`:
```groovy
dependencies {
    compileOnly 'com.spygamingog:SpySpectator:3.0.1'
}
```

---

## ⚙️ Declaring Plugin Dependency

To make sure your plugin loads after `SpySpectator`, declare it in your `plugin.yml`:

```yaml
name: MyGameModePlugin
version: 1.0.0
main: com.myname.myplugin.MyPlugin
# Required dependency:
depend: [SpySpectator]
# OR Optional soft-dependency:
# softdepend: [SpySpectator]
```

---

## 🛠️ Exposing SpySpectator API

Access spectator functions directly using static utility methods in the [`SpySpectatorAPI`](file:///e:/Vaibhav/Projects/SpySpectator/src/main/java/com/spygamingog/spyspectator/api/SpySpectatorAPI.java) class:

### 1. General Spectator Management

```java
import com.spygamingog.spyspectator.api.SpySpectatorAPI;
import org.bukkit.entity.Player;
import java.util.Set;

// Check if a player is in spectator mode
boolean isSpectating = SpySpectatorAPI.isSpectator(player);

// Put a player into custom spectator mode (Adventure flight + invisibility)
SpySpectatorAPI.enableSpectator(player);

// Remove a player from spectator mode (restores inventory & returns to origin)
SpySpectatorAPI.disableSpectator(player);

// Remove a player with custom options:
// disableSpectator(player, toLobby, resetGameMode)
SpySpectatorAPI.disableSpectator(player, true, true);

// Get all active spectators on the server
Set<Player> activeSpectators = SpySpectatorAPI.getSpectators();
```

### 2. First-Person Camera Spectating

```java
// Lock a spectator's camera into a target player's eyes (first-person view)
boolean success = SpySpectatorAPI.startSpectatingTarget(spectatorPlayer, targetPlayer);

// Detach from target and return to free-flight Adventure spectator mode
SpySpectatorAPI.stopSpectatingTarget(spectatorPlayer);

// Check if a spectator is currently attached in first-person mode
boolean isAttached = SpySpectatorAPI.isSpectatingTarget(spectatorPlayer);
```

### 3. Spectator Lobby Management

```java
import org.bukkit.Location;

// Get the configured server spectator exit lobby location (nullable)
Location lobby = SpySpectatorAPI.getLobby();

// Programmatically set the spectator exit lobby location
SpySpectatorAPI.setLobby(newLocation);
```

---

## 🔔 Listening to Custom Events

`SpySpectator` dispatches cancelable custom Bukkit events when players enter or exit spectator mode.

### 1. `PlayerSpectateEvent`
Fired when a player transitions *into* custom spectator mode. Cancel this event to block them from entering spectator mode.

```java
import com.spygamingog.spyspectator.api.events.PlayerSpectateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CombatTagListener implements Listener {

    @EventHandler
    public void onPlayerSpectate(PlayerSpectateEvent event) {
        // Prevent active fighters in a combat arena from spectating
        if (isInActiveMatch(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot enter spectator mode during an active match!");
        }
    }
}
```

### 2. `PlayerUnspectateEvent`
Fired when a player is removed *from* custom spectator mode. Cancel this event to force them to remain in spectator mode.

```java
import com.spygamingog.spyspectator.api.events.PlayerUnspectateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BattleRoyaleListener implements Listener {

    @EventHandler
    public void onPlayerUnspectate(PlayerUnspectateEvent event) {
        // Force eliminated players to stay as spectators until the match ends
        if (isMatchRunning() && isEliminated(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou must remain a spectator until the match is finished!");
        }
    }
}
```

---

## ⚡ Thread-Safety & Folia Compatibility

- **Folia & Paper Asynchronous Safe**: `disableSpectator` uses asynchronous entity teleportation (`teleportAsync`), safe for regional multi-threading.
- If invoking API methods across asynchronous tasks or Folia regions, ensure entity state operations are called on the player's corresponding region scheduler (`player.getScheduler()`).
