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
  <a href="#commands">Commands</a> •
  <a href="#permissions">Permissions</a> •
  <a href="#configuration">Configuration</a> •
  <a href="#building">Building</a> •
  <a href="#contributing">Contributing</a>
</p>

## 🎮 Features

### 🚀 **Advanced Spectator Mode**
- **Ghost-like spectators** - Invisible to non-spectators, players pass through you
- **Normal walking mechanics** - Can't pass through blocks, walk on stairs/ladders
- **Smart flight toggle** - Fly like creative mode with normal speed
- **No interaction** - Can't break/place blocks, open doors, or interact with entities
- **Entity invisibility** - Mobs can't see or target spectators

### 🧭 **Spectator Tools**
- **Spectator Compass GUI** - Browse and select players to spectate
- **Body Spectating** - See through players' eyes with full screen sync
- **Real-time sync** - See exactly what players see (inventory, health, XP, hunger)
- **Sneak to exit** - Press shift to stop spectating and return to free roam

### 🌍 **World Set Support**
- **MultiVerse Integration** - Automatically detects connected worlds
- **Set-based filtering** - Only see players in your current world set
- **Cross-world spectating** - Spectate players across nether/end in same set
- **Portal travel** - Use portals normally while spectating

### 💬 **Smart Communication**
- **Spectator-only chat** - Chat visible only to other spectators and admins
- **See all conversations** - Spectators can read all non-spectator chat
- **Formatted messages** - Colored prefixes for clear identification

### 🛡️ **Admin & Security**
- **Configurable lobby** - Set teleport location for leaving spectator mode
- **Permission system** - Granular control over spectator features
- **No advancements** - Spectators don't get advancement announcements
- **Full protection** - Can't be damaged, hungry, or affected by status effects

## 📥 Installation

### Quick Install
1. Download the latest `SpectatorPlusPlus.jar` from [Releases](https://github.com/SpyGamingOG/SpectatorPlusPlus/releases)
2. Place it in your server's `plugins/` folder
3. Restart your server
4. Use `/spectate` to enter spectator mode!

### Requirements
- **PaperMC 1.21.11** or higher
- **Java 21** or higher
- **Optional**: MultiVerse-Core for automatic world set detection

## 🎯 Quick Start Guide

### For Players
1. Use `/spectate` to enter spectator mode
2. Right-click the **compass** to open player selector
3. Click a player to spectate them
4. See through their eyes - inventory, actions, everything!
5. Press **SHIFT** to stop spectating
6. Right-click the **red bed** to leave spectator mode

### 📋 Commands

  Command	    Description	  Permission	  Aliases
- /spectate	Toggle spectator mode	spectatorplusplus.use	/spec, /sp
- /spectate <player>	Spectate specific player	spectatorplusplus.spectate.others	
- /spectator lobby set	Set lobby location	spectatorplusplus.admin	
- /spectator lobby remove	Remove lobby location	spectatorplusplus.admin	
- /spectator reload	Reload configuration	spectatorplusplus.admin	

### 🔐 Permissions

Permission	Description	Default
spectatorplusplus.use	Use spectator mode	op
spectatorplusplus.admin	Admin commands	op
spectatorplusplus.spectate.others	Spectate other players	op
spectatorplusplus.bypass	See spectators as non-admin	op

### For Server Admins
```bash
# Set spectator lobby location
/spectator lobby set

# Spectate a specific player
/spectate Notch

# Reload configuration
/spectator reload
