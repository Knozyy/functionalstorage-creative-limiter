# Changelog

## 1.1.0 - Minecraft 1.20.1 (Forge)

- Added `/creativelimiter add|remove` (item in main hand) and `/creativelimiter list` for ops, also grantable through
  the `fscreativelimiter.command` permission node.
- The server now sends its list to players, so drawers on a server render with the server's rules instead of each
  player's local config.

## 1.0.0 - Minecraft 1.20.1 (Forge)

- First release.
- Config list of items the Creative Vending Upgrade won't make infinite: item ids, `#tags` and `modid:*`.
- Normal and ender drawers apply it per slot; compacting drawers drop creative for the whole chain if any tier is listed.
- The list reloads when the config file is saved.
- Works with Functional Storage 1.20.1-1.2.0 and newer; tested on 1.2.10.
