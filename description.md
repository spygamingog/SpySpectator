# 👁️ SpySpectator

**SpySpectator** is a modern, high-performance spectator mode, moderation, and minigame utility plugin tailored for Minecraft servers running Paper, Purpur, and Folia (1.21.1+).

By combining a customized, collision-free **Adventure Flight Engine** with native **First-Person Camera Locking** and **Minecraft Target Operators (`@s`, `@p`, `@a`, `@r`)**, SpySpectator delivers the ultimate spectator experience without vanilla noclip chunk lag.

---

## ✨ Key Features (v3.0.1)

- 🎯 **Target Operator Support**: Full support for `@s`, `@p`, `@a`, and `@r` in `/spectate` and `/spectator switch`.
- 🎥 **First-Person Camera Spectating**: Right-click any player while in spectator mode to lock directly into their view; sneak (Shift) to detach back into free-flight!
- 💾 **Lossless Inventory Reboot Persistence**: Inventories and armor are serialized in Base64—no item loss across server reboots.
- ⚡ **Zero Chunk-Loading Lag**: Replaced synchronous chunk loading with non-blocking loaded chunk verification.
- 📄 **Paginated Teleporter GUI**: Browse unlimited players across multiple pages with health, world, and gamemode indicators.
- 🔒 **Off-Hand Item Protection**: Spectator hotbar utility tools can no longer be moved or bypassed using the 'F' key.
- 💬 **Isolated Chat Channel**: Spectators communicate privately without cluttering public chat.
- ⚙️ **Fully Wired `config.yml`**: Customize all messages, item slots, titles, lores, and flight speeds.

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

## 📖 Documentation & Links

- 📚 **Full Documentation**: [DOCUMENTATION.md](DOCUMENTATION.md)
- 🔌 **API Documentation**: [apidoc.md](apidoc.md)
- 🧠 **Project Brain**: [project_brain.md](project_brain.md)
- 📝 **Release Changelog**: [changelog.md](changelog.md)