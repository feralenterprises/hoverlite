package com.feralenterprises.hoverlite;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

/** Local development runner. Does not alter the official RuneLite installation. */
public class HoverLiteDevLauncher
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(HoverLitePlugin.class);
        RuneLite.main(args);
    }
}
