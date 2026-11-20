# HoverLite next update (living backlog)

**State:** Collecting improvements. **No release date, integration build, or Plugin Hub publication is scheduled.**

HoverLite releases are intentionally infrequent. Ideas should be captured here and in linked GitHub Issues, then delivered in a coordinated, tested batch. **Listing an item here does not mean it is implemented.**

## Required candidate
| Issue | Requested behavior | Discovery | Implementation | Verification |
|---|---|---|---|---|
| [#3: Row boat travel/leave shortening](https://github.com/feralenterprises/hoverlite/issues/3) | `Travel to platform Row boat` -> `Travel to platform`; `Leave platform Row boat` -> `Leave platform` | Owner screenshots, 2026-11-20 | Not started | Not tested |

Screenshot references for issue #3 were shared in chat, not attached to GitHub. The exact observed text is preserved in the issue. Treat the right-hand side as the proposed/accepted change request, not an existing plugin result.

## Existing source / release caveats
- [PR #1](https://github.com/feralenterprises/hoverlite/pull/1) (v0.5 source) and [PR #2](https://github.com/feralenterprises/hoverlite/pull/2) (selective NPC lists and object cleanup) have merged into the repository.
- **Do not infer Plugin Hub publication or current end-user version from a source merge.** Check the actual Plugin Hub reference and the exact native client build when planning the update.
- No functional changes should be merged merely because a newly observed issue is present in this backlog.

## Release checklist
- [ ] Review open issues, confirm the exact batch scope and deduplicate.
- [ ] Verify current `main`, Plugin Hub commit/version and existing configuration compatibility.
- [ ] Implement approved issues on independent branches, with positive and negative regression tests.
- [ ] Run complete Gradle tests using Java 17 and record results + exact commit.
- [ ] Test the integrated release source in **Windows RuneLite** with built-in Mouse Tooltips disabled.
- [ ] Verify right-click menu entries, click actions, selected item/spell safety, all toggle/whitelist behavior, and the documented regression corpus.
- [ ] Verify release build/package and document the precise release-source commit.
- [ ] Obtain owner sign-off for the batch.
- [ ] Submit/update RuneLite Plugin Hub with the exact published-source SHA; track review/merge independently.
- [ ] Confirm distribution, update changelog and close issues with actual published-version evidence.

**No automatic publishing:** collecting an idea, creating an issue, merging a source PR, and publishing through Plugin Hub are four different actions.
