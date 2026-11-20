# HoverLite tooltip regression cases

This is a living **behavior specification**, not a claim that every case has recently passed on Windows. Sources are the repository README, merged PRs and owner-reported screenshots. Preserve the native underlying RuneLite interaction in **every** case.

## Documented baseline behavior
| Native/observed text | HoverLite display | Source / status |
|---|---|---|
| `Attack Guard (level-21)` | `Attack Guard` | README example; verify against release candidate |
| `Chop down Tree` | `Chop Tree` | README example; verify against release candidate |
| `Chop down Oak tree` | `Chop Oak` | README example; verify against release candidate |
| `Mine Iron rocks` | `Mine Iron` | README example; verify against release candidate |
| `Sort-salvage Salvaging station` | `Sort salvage` | Merged PR #2 / README; verify against release candidate |
| `Inspect Notice board` | `Inspect board` | Merged PR #2 / README; verify against release candidate |
| `Bank Banker` | `Bank Banker` | README negative case; do **not** over-shorten |

## NPC display and configuration
- Global Auto Shorten remains independently configurable; selective NPC Auto-Shorten and Action-Only lists are separate.
- Under the relevant selective list, `Feed Duke Sucellus` -> `Feed` is the documented Action-Only example. Do not expect this exact output from a default configuration without checking.
- README examples also include removing recognized role prefixes only where safe: `Talk-to Captain Pudi` -> `Talk Pudi`; `Talk-to Ferryman Nathwood` -> `Talk Nathwood`; `Attack Commander Zilyana` -> `Attack Zilyana`.
- Do **not** broadly remove `General` (the documented `General Graardor` behavior is special-case) or alter `King Black Dragon`.

## New pending behavior (not yet implemented)
| Native/observed text | Proposed display | Source / status |
|---|---|---|
| `Travel to platform Row boat` | `Travel to platform` | [Issue #3](https://github.com/feralenterprises/hoverlite/issues/3), owner screenshot 2026-11-20; **pending implementation and native QA** |
| `Leave platform Row boat` | `Leave platform` | [Issue #3](https://github.com/feralenterprises/hoverlite/issues/3), owner screenshot 2026-11-20; **pending implementation and native QA** |

## Required negative checks for every release
- Native RuneLite right-click menu options, targets, order, action IDs, and click behavior remain unchanged.
- Selected item/spell actions and inventory-sensitive interactions retain required full context.
- Unknown or ambiguous wording, unsupported markup, and cases where a target carries essential meaning remain readable rather than over-shortened.
- Relevant color-tag formats, combat-level suffixes, global Auto Shorten state, NPC matching and whitelist priority do not regress.
- Verify using the exact candidate in Windows RuneLite; built-in RuneLite Mouse Tooltips should be disabled for single-overlay testing.

## Capturing a new case
Include the precise full original display, world object/NPC and interaction context, suggested new display, config mode, screenshot link or filename, counterexample(s), issue number, and **implementation/test/native QA status**. Never promote an expected result to a verified result solely from a screenshot or merged PR.
