package com.feralenterprises.hoverlite;

import java.util.Locale;

/**
 * Exact, case-insensitive comma-separated NPC names, used only to choose
 * the presentation of a tooltip. No RuneLite MenuEntry is ever edited.
 */
final class NpcNameWhitelist
{
    private NpcNameWhitelist() {}

    static boolean contains(String csv, String rawTarget)
    {
        if (csv == null || csv.trim().isEmpty() || rawTarget == null)
        {
            return false;
        }

        // Strip a recognized combat level only for matching. Unknown or unsafe
        // markup remains unmatchable rather than being guessed away.
        final String plainTarget = AutoShorten.shorten(HoverCategory.NPC, rawTarget);
        final SafeMenuText parsed = SafeMenuText.parse(plainTarget);
        if (parsed == null || parsed.hasUnanchoredColor())
        {
            return false;
        }

        final String name = normalize(parsed.visible);
        if (name.isEmpty())
        {
            return false;
        }
        for (String candidate : csv.split(",", -1))
        {
            if (!candidate.trim().isEmpty() && name.equals(normalize(candidate)))
            {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String value)
    {
        return value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
