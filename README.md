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
