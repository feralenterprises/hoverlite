# HoverLite 🦝

A lightweight RuneLite Plugin Hub plugin by Feral Enterprises. Customize mouse hover tooltips by the type of target without changing clicks or menus.

**Status:** Plugin Hub submission candidate. Successfully tested by the owner in a local RuneLite development client; Plugin Hub has not approved or published this plugin. Duke Sucellus-specific QA is pending.

## What it does

- **NPCs:** default to **Action only**, so `Feed Duke Sucellus (level-758)` becomes `Feed`.
- **Inventory / item panels:** default to **Full action + target**, keeping item information.
- **World objects, ground items, players, interface widgets, and other actions:** each has an independent mode.
- Each category supports **Full**, **Action only**, **Indicator only** (a small green dot), or **Hidden**.
- Optional filtering of trivial interactions, plus a setting to respect native game tooltips.

**Important:** Turn off RuneLite's built-in **Mouse Tooltips** plugin while HoverLite is enabled, or both tooltip renderers may appear. HoverLite does **not** hide the game's separate top-left action text.

HoverLite does not modify menu entries, target selection, clickboxes, click handling, or game state. It does not make network calls, collect account data, or write files.

## Development

The project targets Java 11 and `latest.release` of RuneLite, and includes `runelite-plugin.properties` with `build=standard` for Plugin Hub. To run a local development client, use the Gradle `run` task with an installed Java JDK and Gradle, or develop in IntelliJ as described in [RuneLite's example plugin](https://github.com/runelite/example-plugin). RuneLite's Jagex-account development login guidance is available [here](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts).

`src/test` includes a development launcher and formatter tests.

## Notes

- Independent categories are inferred from RuneLite's action/menu entry types.
- Uses RuneLite's standard tooltip rendering, with no custom font, opacity, or cursor offset controls.
- When a native game tooltip is active, HoverLite can suppress its own overlay to avoid conflicts.
- This repository contains only the plugin source and build metadata, not the optional Windows bootstrap scripts used for the owner's local QA.

## License

MIT. See [LICENSE](LICENSE).
