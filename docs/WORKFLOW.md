# HoverLite development workflow

**Purpose:** Keep owner discoveries permanently actionable without forcing frequent releases. GitHub Issues are the authoritative backlog; this document describes the process. An informal chat or screenshot is **not** the sole permanent record.

## Roles
- **Owner:** report real in-game behavior, suggest desired wording, conduct Windows RuneLite acceptance checks, approve a Plugin Hub release.
- **Coordinator (ChatGPT):** convert each discovery into a traceable issue; distinguish observation, proposed outcome, implementation, and verified result; deduplicate and maintain the release backlog.
- **Research / implementation agent (Codex or Cursor):** read the existing code and prior PRs, implement only assigned issues in independent feature branches, add positive and negative tests, report exact commit and limitations.
- **QA:** Gradle/JDK 17 automated tests plus owner-led Windows RuneLite check on the exact release candidate.

## Discovery intake
1. Create or update **one GitHub Issue per reproducible behavior**, even if the owner only sends a screenshot and a sentence.
2. Record **verbatim native tooltip text** and proposed display text separately. Capture action, target, world context, game mode, relevant config state and screenshot evidence when known. Mark every unknown explicitly.
3. Link attachments when publicly/securely hosted. If screenshots remain only in conversation, describe them and transcribe exact text; **never imply they were uploaded to GitHub**.
4. Record safeguards and tests before implementation. Label the state in the issue text (see below).
5. New findings stay in the backlog until a batch is approved. **Do not launch work or publish for every observation.**

## Issue state: distinct from release state
- **Captured:** observed/report filed, unimplemented. May need a second reproduction.
- **Specified:** expected result and safe boundaries understood.
- **Implementation in progress:** assigned branch/PR linked.
- **Automated QA passed:** tests on exact commit confirmed, but no claim of native success.
- **Native QA passed:** checked in actual RuneLite on Windows, including right-click menu safety.
- **Accepted for release:** owner approves inclusion.
- **Published:** the exact source commit is confirmed through the RuneLite Plugin Hub update.

Do not close an implementation issue as *released* merely because a PR merged. Keep release tracking in `docs/NEXT_RELEASE.md`.

## Branches and release batches
- Keep `main` stable. Create a short-lived feature branch only after an issue is ready to implement.
- Prefer one behavior family per PR; include its regression cases. No unrelated refactors or releases in a tooltip fix.
- Review dependencies before combining PRs. Build an isolated integration/playtest branch for the next batch if multiple changes need combined native QA.
- Only merge code once the agreed tests and review gates pass. Owner approval is required before publishing Plugin Hub changes.
- Repository merge and Plugin Hub publication are **separate** events; use the actual merged source commit's 40-character SHA for the Plugin Hub update.

## Non-negotiable safety gates
- HoverLite changes **only tooltip overlay display**, never native RuneLite `MenuEntry` option/target/action, ordering, or click behavior.
- Fail closed on unknown/ambiguous targets or markup; preserve selected-item/spell interactions and inventory safety.
- Test both positive and negative interactions, config toggles, native color tags, and any selective NPC lists when relevant.
- Run JDK 17 Gradle tests and then the exact clean source build in native Windows RuneLite. Disable RuneLite's separate Mouse Tooltips plugin to avoid duplicate overlays during validation.
- Retain before/after examples in `docs/REGRESSION_CASES.md`; do not mark a case "verified" until it actually was.

## Where information lives
- **GitHub Issues:** observations, screenshots/links, decisions and acceptance criteria.
- **`docs/NEXT_RELEASE.md`:** the current update batch, pending QA, and release gates.
- **`docs/REGRESSION_CASES.md`:** known examples, expected display strings and what must remain unchanged.
- **PRs:** exact code changes, test evidence, Windows playtest procedure and unresolved blockers.

The owner can simply send a screenshot and say **"HoverLite needs this"**. The coordinator performs intake; the owner does not need to write a ticket or manage branches.
