package com.feralenterprises.hoverlite;

import org.junit.Test;
import static org.junit.Assert.*;

/** Live-observed text, plus conservative hypotheses for unseen raw RuneLite tags. */
public class V05NativeCasesTest
{
    private static String show(HoverCategory category, String option, String target)
    {
        return HoverText.format(HoverMode.SMART_SHORTEN, category, option, target);
    }

    @Test public void allOwnerNpcScreenCaptures()
    {
        for (String[] sample : new String[][] {
            {"Rat", "1"}, {"Undead one", "68"}, {"Undead one", "73"},
            {"Grizzly bear", "21"}, {"Guard", "21"}, {"Barbarian", "10"},
            {"Goblin", "5"}
        })
        {
            String name = sample[0]; String suffix = " (level-" + sample[1] + ")";
            assertEquals(name, "Attack " + name, show(HoverCategory.NPC, "Attack", name + suffix));
            assertEquals(name + " unclosed name+level tags", "Attack <col=ffff00>" + name,
                show(HoverCategory.NPC, "Attack", "<col=ffff00>" + name + "<col=00ff00>" + suffix));
            assertEquals(name + " closed name+level tags", "Attack <col=ffff00>" + name + "</col>",
                show(HoverCategory.NPC, "Attack", "<col=ffff00>" + name + "</col><col=00ff00>" + suffix + "</col>"));
            assertEquals(name + " level within color", "Attack <col=ffff00>" + name + "</col>",
                show(HoverCategory.NPC, "Attack", "<col=ffff00>" + name + suffix + "</col>"));
        }
    }

    @Test public void screenshotWorldCases()
    {
        String[][] examples = {
            {"Chop down", "Oak tree", "Chop Oak"},
            {"Chop down", "Yew tree", "Chop Yew"},
            {"Chop down", "Dead tree", "Chop Dead tree"},
            {"Mine", "Iron rocks", "Mine Iron"},
            {"Mine", "Coal rocks", "Mine Coal"},
            {"Chop down", "Fruit tree", "Chop down Fruit tree"},
            {"Check-health", "Fruit tree", "Check-health Fruit tree"},
            {"Build", "Inconspicuous crate (hard)", "Build Inconspicuous crate (hard)"},
            {"Cross", "Stepping stone", "Cross Stepping stone"},
            {"Climb-down", "Ladder", "Climb-down Ladder"},
            {"Deposit", "Bank Deposit Chest", "Deposit Bank Deposit Chest"},
            {"Open", "Seed vault", "Open Vault"},
        };
        for (String[] sample : examples)
        {
            assertEquals(sample[0] + " " + sample[1], sample[2],
                show(HoverCategory.GAME_OBJECT, sample[0], sample[1]));
            // A leading RuneLite color tag must not block safe action cleanup.
            String colored = show(HoverCategory.GAME_OBJECT, sample[0], "<col=00ffff>" + sample[1]);
            if (sample[2].equals(sample[0] + " " + sample[1]))
            {
                assertEquals(sample[0] + " <col=00ffff>" + sample[1], colored);
            }
            else
            {
                String[] parts = sample[2].split(" ", 2);
                assertEquals(parts[0] + " <col=00ffff>" + parts[1], colored);
            }
        }
        assertEquals("Chop <col=00ffff>Oak", show(HoverCategory.GAME_OBJECT,
            "Chop down", "<col=00ffff>Oak tree<col=ffffff>"));
        assertEquals("Mine <col=00ffff>Iron", show(HoverCategory.GAME_OBJECT,
            "Mine", "<col=00ffff>Iron rocks<col=ffffff>"));
        assertEquals("Mine <col=00ffff>Coal", show(HoverCategory.GAME_OBJECT,
            "Mine", "<col=00ffff>Coal rocks"));
        assertEquals("Chop <col=00ffff>Dead tree", show(HoverCategory.GAME_OBJECT,
            "Chop down", "<col=00ffff>Dead tree"));
    }

    @Test public void opaqueMarkupAndModifiersNotAltered()
    {
        assertEquals("Attack <img=1>Goblin (level-5)", show(HoverCategory.NPC,
            "Attack", "<img=1>Goblin (level-5)"));
        assertEquals("Attack <col=zzzzzz>Goblin (level-5)", show(HoverCategory.NPC,
            "Attack", "<col=zzzzzz>Goblin (level-5)"));
        assertEquals("Attack Goblin (level-5) (level-6)", show(HoverCategory.NPC,
            "Attack", "Goblin (level-5) (level-6)"));
        assertEquals("Chop down <img=1>Oak tree", show(HoverCategory.GAME_OBJECT,
            "Chop down", "<img=1>Oak tree"));
        assertEquals("Withdraw-All Iron rocks", show(HoverCategory.GAME_OBJECT,
            "Withdraw-All", "Iron rocks"));
    }

    /** Failing before v0.5's narrow, documented parser repair. */
    @Test public void adjacentColorBoundaryWithoutSpace()
    {
        assertEquals("Attack <col=ffff00>Guard", show(HoverCategory.NPC, "Attack",
            "<col=ffff00>Guard<col=00ff00>(level-21)"));
        assertEquals("Attack <col=ffff00>Guard</col>", show(HoverCategory.NPC, "Attack",
            "<col=ffff00>Guard</col><col=00ff00>(level-21)</col>"));
        assertEquals("Attack <col=ffff00>Undead one", show(HoverCategory.NPC, "Attack",
            "<col=ffff00>Undead one<col=00ff00>(level-68)"));
        assertEquals("Attack Guard(level-21)", show(HoverCategory.NPC, "Attack",
            "Guard(level-21)")); // Don't remove a suffix absent a verified delimiter.
        assertTrue(TooltipCollisionGuard.onlyNpcCombatLevelsDiffer(HoverCategory.NPC,
            "Attack", "<col=ffff00>Guard<col=00ff00>(level-21)",
            HoverCategory.NPC, "Attack", "<col=ffff00>Guard<col=00ff00>(level-22)"));
    }

    @Test public void terminalInvisibleUnicodeAfterCombatLevel()
    {
        assertEquals("Attack Guard", show(HoverCategory.NPC, "Attack",
            "Guard (level-21)\u200B\u00A0"));
        assertEquals("Attack <col=ffff00>Guard", show(HoverCategory.NPC, "Attack",
            "<col=ffff00>Guard<col=00ff00> (level-21)\u200B\u00A0"));
        assertEquals("Attack <col=ffff00>Rat</col>", show(HoverCategory.NPC, "Attack",
            "<col=ffff00>Rat</col><col=00ff00> (level-1)\uFEFF</col>"));
        assertEquals("Attack Gu\u200Bard (level-21)", show(HoverCategory.NPC, "Attack",
            "Gu\u200Bard (level-21)")); // Embedded control remains opaque.
        assertEquals("Attack Guard (level-21)\u200BX", show(HoverCategory.NPC, "Attack",
            "Guard (level-21)\u200BX")); // Unknown trailing text must not be removed.
        assertEquals("Attack Guard (level-21) (level-22)\u200B", show(HoverCategory.NPC,
            "Attack", "Guard (level-21) (level-22)\u200B"));
    }

    @Test public void suffixCleanupNeverAffectsOtherCategories()
    {
        String v = "Guard (level-21)\u200B\u00A0";
        assertEquals("Examine " + v, show(HoverCategory.GAME_OBJECT, "Examine", v));
        assertEquals("Attack " + v, show(HoverCategory.PLAYER, "Attack", v));
        assertEquals("Examine " + v, show(HoverCategory.INVENTORY_ITEM, "Examine", v));
    }

    @Test public void npcDuplicateLevelsAreNotARealCollision()
    {
        assertTrue(TooltipCollisionGuard.onlyNpcCombatLevelsDiffer(HoverCategory.NPC,
            "Attack", "<col=ffff00>Undead one</col><col=00ff00> (level-68)</col>",
            HoverCategory.NPC, "Attack", "<col=ffff00>Undead one</col><col=00ff00> (level-73)</col>"));
    }

    /** Raw tags transcribed from the owner's v0.5-R03 in-game QA overlay. */
    @Test public void liveFourDigitColorCodesNpcAndTrees()
    {
        assertEquals("Chop <col=ffff>Oak", show(HoverCategory.GAME_OBJECT,
            "Chop down", "<col=ffff>Oak tree"));
        assertEquals("Chop <col=ffff>Tree", show(HoverCategory.GAME_OBJECT,
            "Chop down", "<col=ffff>Tree"));
        assertEquals("Attack <col=ffff00>Guard", show(HoverCategory.NPC,
            "Attack", "<col=ffff00>Guard<col=ff00> (level-21)"));
        assertEquals("Attack <col=ffff00>Rat", show(HoverCategory.NPC,
            "Attack", "<col=ffff00>Rat<col=ff00> (level-1)"));
        assertEquals("Attack <col=ffff00>Undead one", show(HoverCategory.NPC,
            "Attack", "<col=ffff00>Undead one<col=ff00> (level-73)"));
        assertEquals("Mine <col=ffff>Iron", show(HoverCategory.GAME_OBJECT,
            "Mine", "<col=ffff>Iron rocks"));
        assertEquals("Mine <col=ffff>Coal", show(HoverCategory.GAME_OBJECT,
            "Mine", "<col=ffff>Coal rocks"));
        // Unknown tags are not silently ignored.
        assertEquals("Chop down <col=fffgg>Oak tree", show(HoverCategory.GAME_OBJECT,
            "Chop down", "<col=fffgg>Oak tree"));
        assertEquals("Attack <col=123456789>Guard (level-21)", show(HoverCategory.NPC,
            "Attack", "<col=123456789>Guard (level-21)"));
    }
}
