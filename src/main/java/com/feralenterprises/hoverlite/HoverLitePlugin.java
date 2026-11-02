package com.feralenterprises.hoverlite;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
    name = "HoverLite",
    description = "Customize mouse hover tooltips by target type; keep inventory text, simplify NPCs",
    tags = {"mouse", "tooltips", "hover", "accessibility", "customization"}
)
public class HoverLitePlugin extends Plugin
{
    @Inject private OverlayManager overlayManager;
    @Inject private HoverLiteOverlay overlay;

    @Provides
    HoverLiteConfig provideConfig(ConfigManager manager)
    {
        return manager.getConfig(HoverLiteConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
    }
}
