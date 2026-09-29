# Changelog

## Unreleased - Minecraft 1.21.1 (NeoForge)

- Ported the add-on and config reload to NeoForge and Java 21.
- Updated mixins for Functional Storage 1.21.1-1.5.8, including its incoming-stack slot limit and drawer item capability.
- Changed the example item tag to the 1.21 common tag namespace (`#c:`).

## 1.0.0 - Minecraft 1.20.1 (Forge)

- First release.
- Config list of items the Creative Vending Upgrade won't make infinite: item ids, `#tags` and `modid:*`.
- Normal and ender drawers apply it per slot; compacting drawers drop creative for the whole chain if any tier is listed.
- The list reloads when the config file is saved.
- Works with Functional Storage 1.20.1-1.2.0 and newer; tested on 1.2.10.
