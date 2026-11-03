package com.feralenterprises.hoverlite;

import java.awt.image.BufferedImage;
import java.lang.reflect.Proxy;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import org.junit.Test;
import static org.junit.Assert.*;

/** Calls HoverLiteOverlay.render(), not merely the isolated formatter. */
public class V05OverlayTest
{
    private static MenuEntry entry(MenuAction type, String action, String target)
    {
        return (MenuEntry) Proxy.newProxyInstance(MenuEntry.class.getClassLoader(),
            new Class<?>[] {MenuEntry.class}, (proxy, method, args) -> {
                switch (method.getName())
                {
                    case "getType": return type;
                    case "getOption": return action;
                    case "getTarget": return target;
                    case "getParam1": return 0;
                    default: throw new UnsupportedOperationException(method.getName());
                }
            });
    }

    private static Client client(MenuEntry... entries)
    {
        return (Client) Proxy.newProxyInstance(Client.class.getClassLoader(),
            new Class<?>[] {Client.class}, (proxy, method, args) -> {
                switch (method.getName())
                {
                    case "getMenuEntries": return entries;
                    case "isMenuOpen": return false;
                    case "getGameCycle": return 1;
                    case "getVarcIntValue": return 0;
                    default: throw new UnsupportedOperationException(method.getName());
                }
            });
    }

    private static HoverLiteConfig config()
    {
        return (HoverLiteConfig) Proxy.newProxyInstance(HoverLiteConfig.class.getClassLoader(),
            new Class<?>[] {HoverLiteConfig.class}, (proxy, method, args) -> {
                switch (method.getName())
                {
                    case "autoShortenEnabled": return true;
                    case "hideTrivial": return true;
                    case "respectNative": return false;
                    default:
                        if (method.getReturnType() == HoverMode.class) return HoverMode.FULL;
                        throw new UnsupportedOperationException(method.getName());
                }
            });
    }

    private static String draw(MenuEntry... entries)
    {
        TooltipManager tooltipManager = new TooltipManager();
        HoverLiteOverlay overlay = new HoverLiteOverlay(client(entries), tooltipManager, config());
        overlay.render(new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB).createGraphics());
        assertEquals("Exactly one tooltip is drawn without forensic debug output", 1, tooltipManager.getTooltips().size());
        return tooltipManager.getTooltips().get(0).getText();
    }

    @Test public void npcActuallyQueuesShortenedText()
    {
        assertEquals("Attack <col=ffff00>Rat",
            draw(entry(MenuAction.NPC_FIRST_OPTION, "Attack",
                "<col=ffff00>Rat<col=00ff00> (level-1)")));
        assertEquals("Attack <col=ffff00>Undead one</col>",
            draw(entry(MenuAction.NPC_FIRST_OPTION, "Attack",
                "<col=ffff00>Undead one</col><col=00ff00> (level-73)</col>")));
    }

    @Test public void objectActuallyQueuesShortenedText()
    {
        assertEquals("Chop <col=00ffff>Yew",
            draw(entry(MenuAction.GAME_OBJECT_FIRST_OPTION, "Chop down",
                "<col=00ffff>Yew tree")));
        assertEquals("Mine <col=00ffff>Iron",
            draw(entry(MenuAction.GAME_OBJECT_FIRST_OPTION, "Mine",
                "<col=00ffff>Iron rocks")));
    }

    @Test public void newMalformedLevelInputsReachTooltipManager()
    {
        assertEquals("Attack <col=ffff00>Guard",
            draw(entry(MenuAction.NPC_FIRST_OPTION, "Attack",
                "<col=ffff00>Guard<col=00ff00>(level-21)")));
        assertEquals("Attack Guard",
            draw(entry(MenuAction.NPC_FIRST_OPTION, "Attack",
                "Guard (level-21)\u200B\u00A0")));
        assertEquals("Use Guard (level-21)\u200B\u00A0",
            draw(entry(MenuAction.ITEM_USE_ON_NPC, "Use",
                "Guard (level-21)\u200B\u00A0")));
    }

    /** Live four-digit color tags, now without any development marker. */
    @Test public void releaseBuildShortensNativeFourDigitColorTags()
    {
        assertEquals("Attack <col=ffff00>Guard", draw(entry(MenuAction.NPC_FIRST_OPTION,
            "Attack", "<col=ffff00>Guard<col=ff00> (level-21)")));
        assertEquals("Chop <col=ffff>Oak", draw(entry(MenuAction.GAME_OBJECT_FIRST_OPTION,
            "Chop down", "<col=ffff>Oak tree")));
        assertEquals("Chop <col=ffff>Tree", draw(entry(MenuAction.GAME_OBJECT_FIRST_OPTION,
            "Chop down", "<col=ffff>Tree")));
    }

    @Test public void selectedItemNeverShortened()
    {
        assertEquals("Use <col=ffff00>Rat<col=00ff00> (level-1)",
            draw(entry(MenuAction.ITEM_USE_ON_NPC, "Use",
                "<col=ffff00>Rat<col=00ff00> (level-1)")));
    }
}
