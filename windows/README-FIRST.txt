HOVERLITE | WINDOWS RUNELITE PLAYTEST (UNMERGED PR #2)
========================================================
This package includes a compiled RuneLite DEVELOPER client and the current
HoverLite selective-tooltip PR. It does NOT update normal RuneLite/Plugin Hub.

HOW TO START
1. Right-click HOVERLITE-RUNELITE-WINDOWS.zip -> Extract All.
2. In the extracted HOVERLITE-RUNELITE-WINDOWS folder,
   double-click START-HOVERLITE.bat.
3. Sign into the RuneLite developer client if prompted.
4. Under RuneLite plugins, confirm HoverLite is enabled.
5. Turn OFF RuneLite's built-in Mouse Tooltips to avoid duplicate lines.

REQUIREMENTS
- Windows with a Java 17 or newer runtime on PATH.
- Internet for RuneLite/game resources during first client startup.
- No Gradle needed; the prebuilt developer client JAR is included.

TEST THE ACTUAL FEATURES
- Global Enable Auto Shorten ON: normal NPC/world-object shortening.
- Action-Only NPCs: "Duke Sucellus, General Graardor"
  Hover Duke: "Feed", but right-click still shows "Feed Duke Sucellus".
- Auto-Shorten NPCs: enter comma-separated NPC names;
  turn global Auto Shorten OFF to shorten just those names.
- Hover Banker not in action-only list: "Bank Banker".
- Auto Shorten ON: "Talk-to Captain Pudi" -> "Talk Pudi";
  "Talk-to Ferryman Nathwood" -> "Talk Nathwood".
- If both NPC list lists are filled, Action Only takes precedence.
- "General Shopkeeper" and "King Black Dragon" must stay unchanged.
- World objects: "Sort-salvage Salvaging station" -> "Sort salvage";
  "Inspect Notice board" -> "Inspect board".
- Open each right-click menu. Original RuneLite action and target MUST
  remain unchanged, including examination and other options.
- Selected item/spell interactions must stay full/unmodified.

IF THE CLIENT DOES NOT START
Copy the first ERROR/Exception shown in the START-HOVERLITE console.
Keep this folder fully extracted, not launched from within ZIP.

BUILD AND RELEASE STATUS
Source PR: https://github.com/feralenterprises/hoverlite/pull/2
Prebuilt JAR is for owner QA only, NOT the Plugin Hub production JAR.
CI compilation/headless bootstrap are separate from actual native Windows QA.
Do not merge to main or release based solely on these automated checks.
