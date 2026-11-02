package com.feralenterprises.hoverlite;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.gameval.VarClientID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

/** A safe replacement for RuneLite's built-in Mouse Tooltips overlay. */
public class HoverLiteOverlay extends Overlay
{
    private final Client client;
    private final TooltipManager tooltips;
    private final HoverLiteConfig config;

    @Inject
    HoverLiteOverlay(Client client, TooltipManager tooltips, HoverLiteConfig config)
    {
        this.client = client;
        this.tooltips = tooltips;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (client.isMenuOpen())
        {
            return null;
        }
        MenuEntry[] entries = client.getMenuEntries();
        if (entries == null || entries.length == 0)
        {
            return null;
        }
        // The last entry is RuneLite's current default left-click action.
        MenuEntry entry = entries[entries.length - 1];
        MenuAction type = entry.getType();
        if (type == MenuAction.RUNELITE_OVERLAY || type == MenuAction.RUNELITE_OVERLAY_CONFIG
            || type == MenuAction.CC_OP_LOW_PRIORITY || type == MenuAction.RUNELITE_LOW_PRIORITY)
        {
            return null;
        }
        String action = entry.getOption();
        String target = entry.getTarget();
        if (action == null || action.isEmpty())
        {
            return null;
        }
        if (config.hideTrivial())
        {
            switch (action)
            {
                case "Walk here":
                case "Cancel":
                case "Continue":
                case "Set heading":
                    return null;
                case "Move":
                    if (target != null && target.contains("Sliding piece"))
                    {
                        return null;
                    }
                    break;
                default:
                    break;
            }
        }
        if (config.respectNative())
        {
            if (client.getVarcIntValue(VarClientID.TOOLTIP_TIME) > client.getGameCycle()
                || client.getVarcIntValue(VarClientID.TOOLTIP_BUILT) == 1)
            {
                return null;
            }
        }
        HoverMode mode = modeFor(HoverCategory.from(entry));
        String value = HoverText.format(mode, action, target);
        if (value != null)
        {
            tooltips.addFront(new Tooltip(value));
        }
        return null;
    }

    private HoverMode modeFor(HoverCategory category)
    {
        switch (category)
        {
            case NPC: return config.npcMode();
            case INVENTORY_ITEM: return config.inventoryMode();
            case GAME_OBJECT: return config.objectMode();
            case GROUND_ITEM: return config.groundMode();
            case PLAYER: return config.playerMode();
            case INTERFACE: return config.interfaceMode();
            default: return config.otherMode();
        }
    }
}
