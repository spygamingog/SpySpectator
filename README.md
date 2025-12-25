# Spectator++

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.21.11-blue" alt="Minecraft Version">
  <img src="https://img.shields.io/badge/Paper-1.21.11-lightblue" alt="PaperMC">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="License">
  <img src="https://img.shields.io/badge/Version-1.0.0-brightgreen" alt="Version">
  <img src="https://img.shields.io/github/issues/SpyGamingOG/SpectatorPlusPlus" alt="GitHub Issues">
  <a href="https://modrinth.com/plugin/spectator-plus-plus">
    <img src="https://img.shields.io/modrinth/v/spectator-plus-plus?label=Modrinth&logo=modrinth&color=00AF5C" alt="Modrinth Version">
  </a>
  <a href="https://modrinth.com/plugin/spectator-plus-plus">
    <img src="https://img.shields.io/modrinth/dt/spectator-plus-plus?label=Downloads&logo=modrinth&color=00AF5C" alt="Modrinth Downloads">
  </a>
</p>

<p align="center">
  <strong>Advanced spectator mode plugin for Minecraft Paper servers with world set support</strong>
</p>

<p align="center">
  <a href="#-features">Features</a> •
  <a href="#-installation">Installation</a> •
  <a href="#-support">Support</a>
</p>

<p align="center">
  <h3 align="center">🎬 Plugin Showcase</h3>
  
  <!-- Video Coming Soon -->
  <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 100px 0; border-radius: 12px; max-width: 800px; margin: 0 auto;">
    <p align="center" style="color: white; font-size: 24px;">
      🎬 Showcase Video Coming Soon!
    </p>
    <p align="center">
      <a href="https://youtube.com/@SpyGamingOG" target="_blank">
        <img src="https://img.shields.io/badge/Subscribe-YouTube-red?style=for-the-badge&logo=youtube" alt="Subscribe">
      </a>
    </p>
  </div>
  
  <p align="center">
    <em>Follow for updates and plugin showcases</em>
  </p>
</p>

## ✨ Features

<details>
<summary><strong>🎮 Click to view all features</strong></summary>
<br>

<details>
<summary><strong>🚀 Advanced Spectator Mode</strong></summary>

- **Ghost-like spectators** - Invisible to non-spectators, players pass through you
- **Normal walking mechanics** - Can't pass through blocks, walk normally on stairs/ladders
- **Smart flight toggle** - Fly like creative mode with normal speed
- **No interaction** - Can't break/place blocks, open doors, or interact with entities
- **Entity invisibility** - Mobs can't see or target spectators
- **Full protection** - Invulnerable, no hunger, no status effects
</details>

<details>
<summary><strong>🧭 Spectator Tools</strong></summary>

- **Spectator Compass GUI** - Browse and select players to spectate
- **Body Spectating** - See through players' eyes with full screen sync
- **Real-time sync** - See exactly what players see (inventory, health, XP, hunger, actions)
- **Sneak to exit** - Press shift to stop spectating
- **Inventory sync** - View the same inventory as spectated player
</details>

<details>
<summary><strong>🌍 World Set Support</strong></summary>

- **MultiVerse Integration** - Automatically detects connected worlds
- **Set-based filtering** - Only see players in your current world set
- **Cross-world spectating** - Spectate players across nether/end in same set
- **Portal travel** - Use portals normally while spectating
- **Automatic detection** - Finds worlds with `_nether`, `_the_end` suffixes
</details>

<details>
<summary><strong>💬 Smart Communication</strong></summary>

- **Spectator-only chat** - Chat visible only to other spectators and admins
- **See all conversations** - Spectators can read all non-spectator chat
- **Formatted messages** - Colored prefixes for clear identification
</details>

<details>
<summary><strong>🛡️ Admin & Security</strong></summary>

- **Configurable lobby** - Set teleport location for leaving spectator mode
- **Permission system** - Granular control over spectator features
- **No advancements** - Spectators don't get advancement announcements
- **Admin override** - Admins can see all spectators
- **Safe teleport** - Proper location saving and restoration
</details>

</details>

## 📥 Installation

### Quick Install
1. Download from [Modrinth](https://modrinth.com/plugin/spectator-plus-plus) or [GitHub Releases](https://github.com/SpyGamingOG/SpectatorPlusPlus/releases/latest)
2. Place in your server's `plugins/` folder
3. Restart your server
4. Use `/spectate` to enter spectator mode!

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `spectatorplusplus.use` | Use spectator mode | `op` |
| `spectatorplusplus.admin` | Admin commands | `op` |
| `spectatorplusplus.spectate.others` | Spectate other players | `op` |
| `spectatorplusplus.bypass` | See spectators as non-admin | `op` |

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

### Rules
- **Spectators in a set can only see players in the same set**
- **Compass GUI shows only players from current world set**
- **Tablist filters players by world set**
- **Portal travel between set worlds works normally**

## 🔗 Quick Links
- [🐛 Report a Bug](https://github.com/SpyGamingOG/SpectatorPlusPlus/issues/new)
- [💡 Request a Feature](https://github.com/SpyGamingOG/SpectatorPlusPlus/issues/new)
- [📋 View Open Issues](https://github.com/SpyGamingOG/SpectatorPlusPlus/issues)
- [💬 Join Discussions](https://github.com/SpyGamingOG/SpectatorPlusPlus/discussions)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **PaperMC Team** - For the amazing server software
- **MultiVerse Developers** - For world management inspiration
- **Bukkit/Spigot Community** - For excellent documentation and support
- **All Contributors** - Who help improve this plugin
- **Testers** - For finding bugs and suggesting features

## 📞 Support

### Getting Help
- **GitHub Issues**: [Report bugs](https://github.com/SpyGamingOG/SpectatorPlusPlus/issues)
- **GitHub Discussions**: [Ask questions](https://github.com/SpyGamingOG/SpectatorPlusPlus/discussions)
- **Read the Wiki**: [Documentation](https://github.com/SpyGamingOG/SpectatorPlusPlus/wiki)

---

<p align="center">
  Made with ❤️ by <a href="https://github.com/SpyGamingOG">SpyGamingOG</a><br>
  For Minecraft Paper 1.21.11+ • Star this repo if you like it! ⭐
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
