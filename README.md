# HoverLite v0.5 | Release Candidate

Lightweight RuneLite tooltip customization. **Auto Shorten is ON by default** for NPC and game-object hover text. The plugin changes *display text only*: it never edits RuneLite menu entries or player actions.

## Examples
- `Attack Guard (level-21)` -> `Attack Guard`
- `Chop down Tree` -> `Chop Tree`
- `Chop down Oak tree` -> `Chop Oak`
- `Mine Iron rocks` -> `Mine Iron`
- Unknown or safety-sensitive interactions keep their wording.

## Compatibility
Disable RuneLite's built-in **Mouse Tooltips** when using HoverLite; otherwise two tooltip plugins can render simultaneously. Keep the full text for items and selected spell/item actions.

## Windows owner release check
Close other development clients, extract into a fresh folder, and run `BUILD-AND-RUN.cmd`. The launcher runs Gradle tests and opens RuneLite. Then verify Guard, Tree, Oak, inventory and a selected-item action. Do not publish until the **exact clean release source** passes native QA.

## Release path
Publish these source files to `feralenterprises/hoverlite`. For the Plugin Hub, update only `plugins/hoverlite` in a pull request with the published 40-character commit SHA. The public plugin remains on its previous commit until the update is merged.

## Provenance
Derived from owner's native-tested HoverLite v0.5-R04. Removed **only** the temporary QA tooltip marker, raw-target diagnostic code, and forensic-only tests; preserved parser and action/target shortening rules. Independent official Gradle and post-clean native testing still required.

## Proposed selective tooltip update (feature branch, not yet released)
- Global **Enable Auto Shorten** still shortens supported NPC and world-object tooltips.
- **Whitelist Auto-Shorten**: optional comma-separated NPC names to shorten while global Auto Shorten is OFF.
- **Whitelist Action Only**: optional comma-separated NPC names that show only the action, even when global Auto Shorten is ON. Example: `Duke Sucellus, General Graardor`.
- Matching is case-insensitive and exact (not substring), and understands valid NPC combat-level suffixes and ordinary RuneLite color tags.
- Only supported NPC interactions can use either whitelist. Selected item/spell interactions remain full; all real menu entries, targets and click behavior are unchanged.
- Exact world-object display cases: `Sort-salvage Salvaging station` → `Sort salvage`; `Inspect Notice board` → `Inspect board`. Other object names/actions are unchanged.
- If the native **Mouse Tooltips** plugin is also enabled, duplicate tooltip lines can still appear; disable it when testing HoverLite.

**Release gate:** Run all JUnit tests and verify in the native RuneLite client before merging or updating Plugin Hub.

## NPC title shortening (in PR #2; native QA still required)
- Recognized NPC role prefixes are shortened in **tooltip text only**:
  `Talk-to Captain Pudi` → `Talk Pudi`,
  `Talk-to Ferryman Nathwood` → `Talk Nathwood`,
  `Attack Commander Zilyana` → `Attack Zilyana`,
  `Feed Duke Sucellus` → `Feed Sucellus`,
  `Attack General Graardor` → `Attack Graardor`.
- Keeps `Bank Banker`, `General Shopkeeper`, `King Black Dragon` unchanged.
- Recognized role prefixes: `Captain`, `Ferryman`, `Commander`, `Duke`, `First Mate` with a proper-name remainder. `General Graardor` is an exact-case exception; do not globally strip `General`.
- Action Only whitelist takes precedence over name-shortening rules.
- Unknown/unsafe names are intentionally not shortened. No game menu actions, options, targets, or click behavior change.
