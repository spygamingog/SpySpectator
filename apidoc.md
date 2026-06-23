# SpySpectator Developer API Documentation

Expose custom spectator features and interact with active spectators directly from your Minecraft server plugins.

---

## 📦 Dependency Configuration

### Maven
Add `SpySpectator` to your `pom.xml` dependencies:
```xml
<dependency>
    <groupId>com.spygamingog</groupId>
    <artifactId>SpySpectator</artifactId>
    <version>3.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Gradle
Add the dependency to your `build.gradle`:
```groovy
dependencies {
    compileOnly 'com.spygamingog:SpySpectator:3.0.0'
}
```

---

## ⚙️ Declaring Plugin Dependency

To make sure your plugin loads after `SpySpectator`, add it as a dependency or soft-dependency inside your `plugin.yml`:

```yaml
name: MyGameModePlugin
version: 1.0.0
main: com.myname.myplugin.MyPlugin
# Require SpySpectator to run:
depend: [SpySpectator]
# OR Soft-depend (optional integration):
# softdepend: [SpySpectator]
```

---

## 🛠️ Exposing SpySpectator API

Access spectator functions directly using static utility methods in the `SpySpectatorAPI` class:

```java
import com.spygamingog.spyspectator.api.SpySpectatorAPI;
import org.bukkit.entity.Player;
import java.util.Set;

// 1. Check if a player is in spectator mode
boolean spectating = SpySpectatorAPI.isSpectator(player);

// 2. Put a player into custom spectator mode
SpySpectatorAPI.enableSpectator(player);

// 3. Remove a player from spectator mode (and restore inventory/location)
SpySpectatorAPI.disableSpectator(player);

// 4. Remove a player from spectator mode with advanced options
// disableSpectator(player, toLobby, resetGameMode)
SpySpectatorAPI.disableSpectator(player, true, true);

// 5. Get a set of all currently active spectating players
Set<Player> activeSpectators = SpySpectatorAPI.getSpectators();
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

public class MyGameListener implements Listener {

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

public class MyGameListener implements Listener {

    @EventHandler
    public void onPlayerUnspectate(PlayerUnspectateEvent event) {
        // Force dead players in a Battle Royale to stay as spectators until the game ends
        if (isGameRunning() && isEliminated(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou must remain a spectator until the match is finished!");
        }
    }
}
```
