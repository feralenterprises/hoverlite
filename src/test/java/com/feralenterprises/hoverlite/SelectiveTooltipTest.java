package com.feralenterprises.hoverlite;

import java.awt.image.BufferedImage;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import org.junit.Test;
import static org.junit.Assert.*;

/** Source-level regression checks for selective tooltips and immutable menu entries. */
public class SelectiveTooltipTest
{
    private static HoverLiteConfig config(boolean auto, String autoNames, String actionNames)
    {
        return (HoverLiteConfig) Proxy.newProxyInstance(HoverLiteConfig.class.getClassLoader(),
            new Class<?>[]{HoverLiteConfig.class}, (proxy, method, args) -> {
                switch (method.getName())
                {
                    case "autoShortenEnabled": return auto;
                    case "npcAutoShortenWhitelist": return autoNames;
                    case "npcActionOnlyWhitelist": return actionNames;
                    case "hideTrivial": return false;
                    case "respectNative": return false;
                    default:
                        if (method.getReturnType() == HoverMode.class) return HoverMode.FULL;
                        throw new UnsupportedOperationException(method.getName());
                }
            });
    }

    private static MenuEntry entry(MenuAction kind, String action, String target, AtomicInteger mutations)
    {
        return (MenuEntry) Proxy.newProxyInstance(MenuEntry.class.getClassLoader(),
            new Class<?>[]{MenuEntry.class}, (proxy, method, args) -> {
                switch (method.getName())
                {
                    case "getType": return kind;
                    case "getOption": return action;
                    case "getTarget": return target;
                    case "getParam1": return 0;
                    default:
                        if (method.getName().startsWith("set"))
                        {
                            mutations.incrementAndGet();
                            throw new AssertionError("Tooltip must not edit RuneLite MenuEntry: " + method.getName());
                        }
                        throw new UnsupportedOperationException(method.getName());
                }
            });
    }

    private static String draw(HoverLiteConfig settings, MenuEntry... entries)
    {
        AtomicInteger clientWrites = new AtomicInteger();
        Client client = (Client) Proxy.newProxyInstance(Client.class.getClassLoader(),
            new Class<?>[]{Client.class}, (proxy, method, args) -> {
                switch (method.getName())
                {
                    case "getMenuEntries": return entries;
                    case "isMenuOpen": return false;
                    case "getGameCycle": return 1;
                    case "getVarcIntValue": return 0;
                    default:
                        if (method.getName().startsWith("set"))
                        {
                            clientWrites.incrementAndGet();
                            throw new AssertionError("Tooltip must not edit RuneLite Client menus: " + method.getName());
                        }
                        throw new UnsupportedOperationException(method.getName());
                }
            });
        TooltipManager manager = new TooltipManager();
        new HoverLiteOverlay(client, manager, settings)
            .render(new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB).createGraphics());
        assertEquals(0, clientWrites.get());
        assertEquals(1, manager.getTooltips().size());
        return manager.getTooltips().get(0).getText();
    }

    @Test public void csvMatchingRequiresExactNpcName()
    {
        assertTrue(NpcNameWhitelist.contains("  commander  zilyana , Duke Sucellus, general graardor ",
            "<col=ffff00>DUKE SUCELLUS<col=00ff00> (level-758)"));
        assertTrue(NpcNameWhitelist.contains("Duke Sucellus", "Duke Sucellus (level-758)"));
        assertFalse(NpcNameWhitelist.contains("Duke Sucellus", "Duke Sucellus Jr"));
        assertFalse(NpcNameWhitelist.contains("Duke", "Duke Sucellus"));
        assertFalse(NpcNameWhitelist.contains("Duke Sucellus", "<img=1>Duke Sucellus"));
        assertFalse(NpcNameWhitelist.contains("Duke Sucellus", null));
        assertFalse(NpcNameWhitelist.contains("", "Duke Sucellus"));
    }

    @Test public void actionOnlyListBeatsGlobalAutoShortenWithoutChangingMenuEntries()
    {
        AtomicInteger mutations = new AtomicInteger();
        HoverLiteConfig settings = config(true, "Commander Zilyana", "Duke Sucellus, General Graardor");
        MenuEntry duke = entry(MenuAction.NPC_FIRST_OPTION, "Feed",
            "<col=ffff00>Duke Sucellus<col=00ff00> (level-758)", mutations);
        assertEquals("Feed", draw(settings, duke));
        assertEquals("Feed", duke.getOption());
        assertEquals("<col=ffff00>Duke Sucellus<col=00ff00> (level-758)", duke.getTarget());
        assertEquals(0, mutations.get());

        MenuEntry banker = entry(MenuAction.NPC_FIRST_OPTION, "Bank", "Banker", mutations);
        assertEquals("Bank Banker", draw(settings, banker));
        assertEquals("Bank", banker.getOption());
        assertEquals("Banker", banker.getTarget());
        assertEquals(0, mutations.get());
    }

    @Test public void autoWhitelistAddsNpcShorteningWhenGlobalOff()
    {
        HoverLiteConfig settings = config(false, "Duke Sucellus, General Graardor", "");
        AtomicInteger mutations = new AtomicInteger();
        assertEquals("Feed Sucellus", draw(settings,
            entry(MenuAction.NPC_FIRST_OPTION, "Feed", "Duke Sucellus (level-758)", mutations)));
        assertEquals("Attack Guard (level-21)", draw(settings,
            entry(MenuAction.NPC_FIRST_OPTION, "Attack", "Guard (level-21)", mutations)));
        assertEquals(0, mutations.get());
    }

    @Test public void actionOnlyListStillWorksWithGlobalOff()
    {
        assertEquals("Feed", draw(config(false, "", "duke sucellus"),
            entry(MenuAction.NPC_FIRST_OPTION, "Feed", "Duke Sucellus (level-758)", new AtomicInteger())));
    }

    @Test public void selectedItemAndSpellNeverUseCustomShortening()
    {
        HoverLiteConfig settings = config(true, "Duke Sucellus", "Duke Sucellus");
        for (MenuAction type : new MenuAction[]{MenuAction.ITEM_USE_ON_NPC, MenuAction.WIDGET_TARGET_ON_NPC})
        {
            AtomicInteger mutations = new AtomicInteger();
            MenuEntry e = entry(type, "Use", "Duke Sucellus (level-758)", mutations);
            assertEquals("Use Duke Sucellus (level-758)", draw(settings, e));
            assertEquals(0, mutations.get());
        }
    }

    @Test public void ownerObjectFixesApplyOnlyToKnownActionAndNamePairs()
    {
        HoverLiteConfig settings = config(true, "Duke Sucellus", "Duke Sucellus");
        AtomicInteger mutations = new AtomicInteger();
        assertEquals("Sort salvage", draw(settings,
            entry(MenuAction.GAME_OBJECT_FIRST_OPTION, "Sort-salvage", "Salvaging station", mutations)));
        assertEquals("Sort <col=00ffff>salvage", draw(settings,
            entry(MenuAction.GAME_OBJECT_FIRST_OPTION, "Sort-salvage", "<col=00ffff>Salvaging station", mutations)));
        assertEquals("Inspect board", draw(settings,
            entry(MenuAction.GAME_OBJECT_FIRST_OPTION, "Inspect", "Notice board", mutations)));
        assertEquals("Inspect <col=00ffff>board", draw(settings,
            entry(MenuAction.GAME_OBJECT_FIRST_OPTION, "Inspect", "<col=00ffff>Notice board", mutations)));
        assertEquals("Inspect bulletin board", draw(settings,
            entry(MenuAction.GAME_OBJECT_FIRST_OPTION, "Inspect", "bulletin board", mutations)));
        assertEquals("Examine Notice board", draw(settings,
            entry(MenuAction.GAME_OBJECT_FIRST_OPTION, "Examine", "Notice board", mutations)));
        assertEquals("Sort-salvage Salvaging station (damaged)", draw(settings,
            entry(MenuAction.GAME_OBJECT_FIRST_OPTION, "Sort-salvage", "Salvaging station (damaged)", mutations)));
        assertEquals(0, mutations.get());
    }

    @Test public void globalAutoShortenShortensNpcTitlesWithoutMenuMutation()
    {
        HoverLiteConfig settings = config(true, "", "");
        for (String[] sample : new String[][]{
            {"Talk-to", "Captain Pudi", "Talk Pudi"},
            {"Talk-to", "Ferryman Nathwood", "Talk Nathwood"},
            {"Attack", "Commander Zilyana", "Attack Zilyana"},
            {"Feed", "Duke Sucellus", "Feed Sucellus"},
            {"Attack", "General Graardor", "Attack Graardor"},
            {"Talk-to", "First Mate Deri", "Talk Deri"}
        })
        {
            AtomicInteger mutations = new AtomicInteger();
            MenuEntry original = entry(MenuAction.NPC_FIRST_OPTION, sample[0], sample[1], mutations);
            assertEquals(sample[1], sample[2], draw(settings, original));
            assertEquals(sample[0], original.getOption());
            assertEquals(sample[1], original.getTarget());
            assertEquals(0, mutations.get());
        }
    }

    @Test public void titleWhitelistWorksWhenGlobalAutoShortenIsDisabled()
    {
        HoverLiteConfig settings = config(false, "Captain Pudi, Ferryman Nathwood", "");
        assertEquals("Talk Pudi", draw(settings,
            entry(MenuAction.NPC_FIRST_OPTION, "Talk-to", "Captain Pudi", new AtomicInteger())));
        assertEquals("Talk Nathwood", draw(settings,
            entry(MenuAction.NPC_FIRST_OPTION, "Talk-to", "Ferryman Nathwood", new AtomicInteger())));
        assertEquals("Talk-to Commander Zilyana", draw(settings,
            entry(MenuAction.NPC_FIRST_OPTION, "Talk-to", "Commander Zilyana", new AtomicInteger())));
    }

    @Test public void actionOnlyWhitelistOverridesAutomaticTitleRemoval()
    {
        HoverLiteConfig settings = config(true, "Captain Pudi, Ferryman Nathwood", "Captain Pudi");
        assertEquals("Talk-to", draw(settings,
            entry(MenuAction.NPC_FIRST_OPTION, "Talk-to", "Captain Pudi", new AtomicInteger())));
        assertEquals("Talk Nathwood", draw(settings,
            entry(MenuAction.NPC_FIRST_OPTION, "Talk-to", "Ferryman Nathwood", new AtomicInteger())));
    }

    @Test public void genericAndUntitledNpcsAreNotClobbered()
    {
        HoverLiteConfig settings = config(true, "", "");
        for (String name : new String[]{"Banker", "General Shopkeeper", "King Black Dragon", "Captain guard"})
        {
            assertEquals(name, "Talk " + name, draw(settings,
                entry(MenuAction.NPC_FIRST_OPTION, "Talk-to", name, new AtomicInteger())));
        }
    }

    @Test public void unknownActionTypesDoNotUseWhitelist()
    {
        AtomicInteger mutations = new AtomicInteger();
        assertEquals("Use Duke Sucellus", draw(config(true, "Duke Sucellus", "Duke Sucellus"),
            entry(MenuAction.ITEM_USE_ON_NPC, "Use", "Duke Sucellus", mutations)));
        assertEquals(0, mutations.get());
    }
}
