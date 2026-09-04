# Changelog

## [3.0.1] - 2026-09-04

### Added
- Native Minecraft target selector support (`@s`, `@p`, `@a`, `@r`, `@e`) for `/spectate` and `/spectator switch`.
- First-person camera spectating by right-clicking players (sneak to exit).
- Tab completion for target selectors, player names, and subcommands.
- Console command support for `/spectator reload`, `/spectator switch`, and `/spectator leave`.
- Multi-page pagination for the Teleporter GUI.
- `/spectator reload` command to hot-reload `config.yml`.
- First-person camera and lobby management methods in `SpySpectatorAPI`.

### Fixed
- Fixed critical item loss bug on server reboot by serializing inventories and armor to `spectators.yml`.
- Fixed `/spectate` command not being registered.
- Fixed permission issue preventing normal players from toggling spectator mode.
- Fixed off-hand swap exploit with spectator utility tools.
- Fixed skull click handling failing on colored names by using `PersistentDataContainer` UUIDs.
- Fixed spectator death edge-cases to prevent utility item drops.
- Removed phantom `SpyInventories` dependency from `plugin.yml`.

### Changed & Performance
- Replaced synchronous chunk loading with `isChunkLoaded()` to eliminate chunk-crossing lag spikes.
- Switched teleports to `player.teleportAsync()` for Paper and Folia thread safety.
- Fully wired all messages, speeds, item slots, and titles to `config.yml`.
