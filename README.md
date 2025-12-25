
<!--
Spectator++ - Advanced Spectator Mode Plugin
Repository: https://github.com/SpyGamingOG/SpectatorPlusPlus
Author: SpyGamingOG
Version: 1.0.0
-->

# Spectator++

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.21.11-blue" alt="Minecraft Version">
  <img src="https://img.shields.io/badge/Paper-1.21.11-lightblue" alt="PaperMC">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="License">
  <img src="https://img.shields.io/badge/Version-1.0.0-brightgreen" alt="Version">
  <img src="https://img.shields.io/github/issues/SpyGamingOG/SpectatorPlusPlus" alt="GitHub Issues">
</p>

<p align="center">
  <strong>Advanced spectator mode plugin for Minecraft Paper servers with world set support</strong>
</p>

<p align="center">
  <a href="#features">Features</a> •
  <a href="#installation">Installation</a> •
  <a href="#quick-start">Quick Start</a> •
  <a href="#commands">Commands</a> •
  <a href="#permissions">Permissions</a> •
  <a href="#configuration">Configuration</a> •
  <a href="#building">Building</a> •
  <a href="#support">Support</a>
</p>

<p align="center">
  <img src="https://via.placeholder.com/800x400.png?text=Spectator+Mode+Preview" alt="Spectator Mode Preview" width="800">
  <br>
  <em>Replace with actual screenshots of your plugin in action</em>
</p>

## 🎮 Features

### 🚀 **Advanced Spectator Mode**
- **Ghost-like spectators** - Invisible to non-spectators, players pass through you
- **Normal walking mechanics** - Can't pass through blocks, walk on stairs/ladders normally
- **Smart flight toggle** - Fly like creative mode with normal speed (0.1)
- **No interaction** - Can't break/place blocks, open doors, or interact with entities
- **Entity invisibility** - Mobs can't see or target spectators
- **Full protection** - Invulnerable, no hunger, no status effects

### 🧭 **Spectator Tools**
- **Spectator Compass GUI** - Browse and select players to spectate from current world set
- **Body Spectating** - See through players' eyes with full screen sync
- **Real-time sync** - See exactly what players see (inventory, health, XP, hunger, actions)
- **Sneak to exit** - Press shift to stop spectating and return to free roam
- **Inventory sync** - View the same inventory as the player you're spectating

### 🌍 **World Set Support**
- **MultiVerse Integration** - Automatically detects connected worlds
- **Set-based filtering** - Only see players in your current world set
- **Cross-world spectating** - Spectate players across nether/end in same set
- **Portal travel** - Use portals normally while spectating
- **Automatic detection** - Finds worlds with suffixes: `_nether`, `_the_end`

### 💬 **Smart Communication**
- **Spectator-only chat** - Chat visible only to other spectators and admins
- **See all conversations** - Spectators can read all non-spectator chat
- **Formatted messages** - Colored prefixes for clear identification
- **No spam** - Clean, organized chat system

### 🛡️ **Admin & Security**
- **Configurable lobby** - Set teleport location for leaving spectator mode
- **Permission system** - Granular control over spectator features
- **No advancements** - Spectators don't get advancement announcements
- **Admin override** - Admins can see all spectators
- **Safe teleport** - Proper location saving and restoration

## 📥 Installation

### Quick Install
1. Download the latest `SpectatorPlusPlus.jar` from [Releases](https://github.com/SpyGamingOG/SpectatorPlusPlus/releases)
2. Place it in your server's `plugins/` folder
3. Restart your server
4. Configure settings in `plugins/SpectatorPlusPlus/config.yml` (optional)
5. Use `/spectate` to enter spectator mode!

### Requirements
- **PaperMC 1.21.11** or higher
- **Java 21** or higher
- **Optional**: MultiVerse-Core for automatic world set detection

### Supported Platforms
- ✅ PaperMC 1.21.11+
- ✅ Purpur 1.21.11+
- ✅ Any Paper fork
- ❌ Spigot (not recommended)
- ❌ Bukkit (not supported)

## 🎯 Quick Start Guide

### For Players
1. **Enter spectator mode**: Use `/spectate` command
2. **Get your tools**: Receive compass (player selector) and bed (exit)
3. **Browse players**: Right-click the compass to open GUI
4. **Select player**: Click any player head to spectate them
5. **Experience their view**: See through their eyes, watch their actions
6. **Stop spectating**: Press SHIFT (sneak) to return to free roam
7. **Leave spectator**: Right-click the red bed or use `/spectate` again

### For Server Admins
```bash
# Set spectator lobby location
/spectator lobby set

# Remove lobby location
/spectator lobby remove

# Spectate a specific player
/spectate PlayerName

# Force player into spectator mode
/spectate @PlayerName

# Reload configuration
/spectator reload
```

### For Server Owners
1. **Set permissions**: Configure in your permission plugin (LuckPerms recommended)
2. **Configure world sets**: Ensure proper naming: `world`, `world_nether`, `world_the_end`
3. **Set up lobby**: Use `/spectator lobby set` in your spawn/lobby area
4. **Test thoroughly**: Ensure all features work in your setup

## 📋 Commands

| Command | Description | Permission | Aliases |
|---------|-------------|------------|---------|
| `/spectate` | Toggle spectator mode | `spectatorplusplus.use` | `/spec`, `/sp` |
| `/spectate <player>` | Spectate specific player | `spectatorplusplus.spectate.others` | |
| `/spectator lobby set` | Set lobby location | `spectatorplusplus.admin` | |
| `/spectator lobby remove` | Remove lobby location | `spectatorplusplus.admin` | |
| `/spectator reload` | Reload configuration | `spectatorplusplus.admin` | |

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `spectatorplusplus.use` | Use spectator mode | `op` |
| `spectatorplusplus.admin` | Admin commands | `op` |
| `spectatorplusplus.spectate.others` | Spectate other players | `op` |
| `spectatorplusplus.bypass` | See spectators as non-admin | `op` |

### Permission Examples (LuckPerms)
```yaml
# Give all players spectator access
lp group default permission set spectatorplusplus.use true

# Create moderator group with admin access
lp group moderator permission set spectatorplusplus.admin true
lp group moderator permission set spectatorplusplus.spectate.others true

# Create helper group with limited access
lp group helper permission set spectatorplusplus.use true
lp group helper permission set spectatorplusplus.bypass true
```

## ⚙️ Configuration

Default configuration (`plugins/SpectatorPlusPlus/config.yml`):

```yaml
# Spectator++ Configuration
# Version: 1.0.0

# Lobby location for leaving spectator mode
# If not set, uses world spawn
lobby:
  world: world
  x: 0
  y: 64
  z: 0
  yaw: 0
  pitch: 0

# Spectator behavior settings
spectator:
  fly-speed: 0.1  # Flight speed (0.1 = normal creative speed)
  walk-speed: 0.2 # Walking speed
  can-fly: true   # Allow flight
  invisible-to-others: true      # Hide from non-spectators
  can-see-other-spectators: true # Spectators can see each other
  chat-visible-to-spectators-only: true # Chat privacy
  
# World set detection (MultiVerse support)
world-sets:
  auto-detect: true      # Automatically find connected worlds
  suffix-nethers: "_nether"  # Nether world suffix
  suffix-ends: "_the_end"    # End world suffix
  
# Compass GUI settings
compass-gui:
  title: "&6Spectate Players"  # GUI title
  rows: 6                      # GUI rows (1-6)
  show-spectators: false       # Show other spectators in GUI
  
# Plugin messages (Supports color codes with &)
messages:
  enter-spectator: "&aYou are now in spectator mode!"
  leave-spectator: "&cYou have left spectator mode!"
  no-permission: "&cYou don't have permission!"
  player-not-found: "&cPlayer not found!"
  lobby-set: "&aLobby location set!"
  lobby-removed: "&cLobby location removed!"
  already-spectator: "&cYou are already in spectator mode!"
  not-spectator: "&cYou are not in spectator mode!"
```

## 🌍 World Set System

### How It Works
Spectator++ automatically groups connected worlds into sets:
- **Base World**: `world`
- **Nether World**: `world_nether` (if exists)
- **End World**: `world_the_end` (if exists)

### Examples

| World Set | Members | Description |
|-----------|---------|-------------|
| Default Set | `world`, `world_nether`, `world_the_end` | Main server worlds |
| Game 1 | `1v1_manhunt`, `1v1_manhunt_nether`, `1v1_manhunt_the_end` | 1v1 game instance |
| Game 2 | `2v2_bedwars`, `2v2_bedwars_nether`, `2v2_bedwars_the_end` | 2v2 game instance |
| Minigame | `skywars`, `skywars_nether`, `skywars_the_end` | SkyWars arena |

### Rules
- Spectators in a set can only see players in the same set
- Compass GUI shows only players from current world set
- Tablist filters players by world set
- Portal travel between set worlds works normally

### Manual Configuration
If auto-detection fails, you can manually define world sets:
```yaml
# Coming in future update
world-sets:
  custom-sets:
    - ["lobby", "lobby_nether", "lobby_the_end"]
    - ["survival", "survival_nether"]
```

## 🏗️ Building from Source

### Prerequisites
- **Java 21 JDK** (Download from [Adoptium](https://adoptium.net/))
- **Apache Maven** (Download from [Maven](https://maven.apache.org/))
- **Git** (Download from [Git](https://git-scm.com/))

### Build Steps
```bash
# 1. Clone the repository
git clone https://github.com/SpyGamingOG/SpectatorPlusPlus.git
cd SpectatorPlusPlus

# 2. Build the plugin
mvn clean package

# 3. Find the compiled JAR
# Location: target/SpectatorPlusPlus-1.0.0.jar
```

### Development Setup
1. **Import to IDE** (IntelliJ IDEA recommended)
   - File → Open → Select `pom.xml`
   - Maven will download dependencies automatically

2. **Run tests** (if any)
   ```bash
   mvn test
   ```

3. **Create debug JAR**
   ```bash
   mvn clean compile package
   ```

### Project Structure
```
SpectatorPlusPlus/
├── src/main/java/com/spygamingog/spectatorplusplus/
│   ├── SpectatorPlusPlus.java          # Main plugin class
│   ├── commands/                       # Command handlers
│   │   ├── CommandManager.java
│   │   ├── SpectateCommand.java
│   │   └── SpectatorLobbyCommand.java
│   ├── listeners/                      # Event listeners
│   │   ├── PlayerListener.java
│   │   ├── InventoryListener.java
│   │   ├── ChatListener.java
│   │   ├── WorldListener.java
│   │   ├── EntityListener.java
│   │   └── AdvancementListener.java
│   ├── gui/                           # GUI systems
│   │   └── PlayerSelectorGUI.java
│   ├── data/                          # Configuration
│   │   ├── ConfigManager.java
│   │   └── WorldSetManager.java
│   ├── utils/                         # Core utilities
│   │   ├── SpectatorManager.java
│   │   └── VisibilityManager.java
│   └── tasks/                         # Background tasks
│       └── SpectatorFollowTask.java
├── src/main/resources/
│   ├── plugin.yml                     # Plugin metadata
│   └── config.yml                     # Default configuration
├── pom.xml                           # Maven configuration
├── README.md                         # This file
└── LICENSE                           # MIT License
```

## 🐛 Troubleshooting

### Common Issues & Solutions

**Q: Plugin won't load/enable?**  
**A:** Check server logs for errors. Ensure:
- Java 21 is installed
- PaperMC 1.21.11+ is used
- No conflicting plugins

**Q: Players can see spectators?**  
**A:**
1. Check `invisible-to-others: true` in config.yml
2. Ensure players don't have `spectatorplusplus.bypass` permission
3. Restart plugin: `/spectator reload`

**Q: Spectators can't see players in nether/end?**  
**A:**
1. Ensure worlds follow naming: `worldname_nether`, `worldname_the_end`
2. Check if MultiVerse is properly linking worlds
3. Verify worlds are loaded on server

**Q: Compass GUI not showing players?**  
**A:**
1. Players might be in different world sets
2. Players might be offline or have left
3. Check if GUI is enabled in config

**Q: Spectators can interact with blocks/entities?**  
**A:** This is a bug! Report it on GitHub Issues with:
- Steps to reproduce
- Server version
- Plugin version

**Q: Permission issues?**  
**A:**
1. Verify permission plugin is installed (LuckPerms recommended)
2. Check permission nodes are correct
3. Use `/lp verbose` to debug permissions

### Debug Mode
For advanced troubleshooting, enable debug mode:

```bash
# Add to server startup command
java -Dspectator.debug=true -jar paper.jar
```

Or check logs in `plugins/SpectatorPlusPlus/debug.log` (if enabled)

## 🤝 Contributing

We love contributions! Whether it's bug reports, feature requests, or code contributions.

### How to Contribute

1. **Fork the repository**
   - Click "Fork" at top-right of GitHub page
   - Creates your own copy to work on

2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```

3. **Make your changes**
   - Follow existing code style
   - Add comments for complex logic
   - Test your changes

4. **Commit changes**
   ```bash
   git add .
   git commit -m "Add: Description of changes"
   ```

5. **Push to GitHub**
   ```bash
   git push origin feature/amazing-feature
   ```

6. **Open a Pull Request**
   - Go to original repository
   - Click "Pull Requests" → "New Pull Request"
   - Select your branch
   - Describe your changes

### Development Guidelines
- **Code Style**: Follow existing patterns
- **Comments**: Document complex logic
- **Testing**: Test before submitting
- **Commits**: Clear, descriptive messages
- **Branches**: Feature-based naming

### Reporting Issues
Please include in bug reports:
```markdown
**Minecraft Version:** 1.21.11
**Plugin Version:** 1.0.0
**Server Software:** Paper
**Steps to Reproduce:**
1. Step 1
2. Step 2
3. Step 3
**Expected Behavior:**
What should happen
**Actual Behavior:**
What actually happens
**Error Logs:**
[Paste error from console]
**Screenshots/Videos:**
[If applicable]
```

## 📝 Changelog

All notable changes to this project will be documented here.

### [1.0.0] - 2024-12-25
#### Added
- Initial release of Spectator++
- Advanced spectator mode with invisibility
- World set detection and filtering
- Body spectating with screen sync
- Spectator compass GUI
- Smart chat system
- Admin commands and permissions
- Configuration file support
- MultiVerse integration

#### Fixed
- N/A (Initial release)

#### Changed
- N/A (Initial release)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 SpyGamingOG

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 🙏 Acknowledgments

- **PaperMC Team** - For the amazing server software
- **MultiVerse Developers** - For world management inspiration
- **Bukkit/Spigot Community** - For excellent documentation and support
- **All Contributors** - Who help improve this plugin
- **Testers** - For finding bugs and suggesting features

### Special Thanks
- Everyone who stars the repository
- Community members who provide feedback
- Open source developers who inspire us

## 📞 Support

### Getting Help
- **GitHub Issues**: [Report bugs](https://github.com/SpyGamingOG/SpectatorPlusPlus/issues)
- **GitHub Discussions**: [Ask questions](https://github.com/SpyGamingOG/SpectatorPlusPlus/discussions)
- **Read the Wiki**: [Documentation](https://github.com/SpyGamingOG/SpectatorPlusPlus/wiki)

### Response Time
- **Bug Reports**: 1-3 days
- **Feature Requests**: 1-2 weeks
- **Questions**: 1-7 days

### Before Asking for Help
1. Check this README
2. Search existing issues
3. Check server logs
4. Try `/spectator reload`

## 🚀 Roadmap

### Planned Features (v1.1.0)
- [ ] Database persistence for spectator state
- [ ] Spectator teams and groups
- [ ] Advanced teleport history
- [ ] More GUI customization options
- [ ] API for developer integration

### In Progress
- [ ] Performance optimizations
- [ ] Additional world set detection methods
- [ ] More configuration options

### Future Ideas
- [ ] Spectator voting system
- [ ] Camera path recording
- [ ] Replay system integration
- [ ] Spectator tournaments
- [ ] Mobile app companion

### Vote on Features
Want to suggest or vote on features? Visit [GitHub Discussions](https://github.com/SpyGamingOG/SpectatorPlusPlus/discussions)

## 📊 Statistics

![GitHub stars](https://img.shields.io/github/stars/SpyGamingOG/SpectatorPlusPlus?style=social)
![GitHub forks](https://img.shields.io/github/forks/SpyGamingOG/SpectatorPlusPlus?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/SpyGamingOG/SpectatorPlusPlus?style=social)
![GitHub issues](https://img.shields.io/github/issues/SpyGamingOG/SpectatorPlusPlus)
![GitHub pull requests](https://img.shields.io/github/issues-pr/SpyGamingOG/SpectatorPlusPlus)

### Download Stats
![GitHub all releases](https://img.shields.io/github/downloads/SpyGamingOG/SpectatorPlusPlus/total)
![GitHub release (latest by date)](https://img.shields.io/github/downloads/SpyGamingOG/SpectatorPlusPlus/latest/total)

## 🌟 Show Your Support

If you find this plugin useful, please:

1. ⭐ **Star the repository** (top-right of page)
2. 🔀 **Fork it** (make your own copy)
3. 🐛 **Report bugs** (help improve)
4. 💡 **Suggest features** (make it better)
5. 📢 **Share with friends** (spread the word)

## 🔗 Useful Links

- [PaperMC Website](https://papermc.io/)
- [MultiVerse Plugin](https://dev.bukkit.org/projects/multiverse-core)
- [Minecraft Plugin Development](https://www.spigotmc.org/wiki/spigot-plugin-development/)
- [Java Documentation](https://docs.oracle.com/en/java/)

## 📧 Contact

- **GitHub**: [@SpyGamingOG](https://github.com/SpyGamingOG)
- **Plugin Issues**: [GitHub Issues](https://github.com/SpyGamingOG/SpectatorPlusPlus/issues)
- **Discussion**: [GitHub Discussions](https://github.com/SpyGamingOG/SpectatorPlusPlus/discussions)

---

<p align="center">
  Made with ❤️ by <a href="https://github.com/SpyGamingOG">SpyGamingOG</a><br>
  For Minecraft Paper 1.21.11+ • Happy Spectating! 🎮👻
</p>

<p align="center">
  <a href="https://github.com/SpyGamingOG/SpectatorPlusPlus/stargazers">
    <img src="https://img.shields.io/github/stars/SpyGamingOG/SpectatorPlusPlus?style=for-the-badge&logo=github&color=yellow" alt="GitHub stars">
  </a>
  <a href="https://github.com/SpyGamingOG/SpectatorPlusPlus/network/members">
    <img src="https://img.shields.io/github/forks/SpyGamingOG/SpectatorPlusPlus?style=for-the-badge&logo=github&color=blue" alt="GitHub forks">
  </a>
  <a href="https://github.com/SpyGamingOG/SpectatorPlusPlus/watchers">
    <img src="https://img.shields.io/github/watchers/SpyGamingOG/SpectatorPlusPlus?style=for-the-badge&logo=github&color=green" alt="GitHub watchers">
  </a>
</p>

<p align="center">
  <sub>If you encounter any issues, please report them on GitHub Issues.</sub>
</p>
```

## 📋 How to Use This README:

1. **Copy the entire code above**
2. **Paste into your README.md file**
3. **Update these parts:**
   - Replace placeholder image link with actual screenshots
   - Update version numbers when you release updates
   - Add your actual contact info if different
   - Update changelog with each release

4. **Add screenshots** (recommended):
   - Press F2 in Minecraft to take screenshots
   - Upload to `screenshots/` folder in your repo
   - Update image links in README

This README is **complete and professional** - it has everything users, developers, and server admins need!
