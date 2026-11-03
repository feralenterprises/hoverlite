package com.feralenterprises.hoverlite;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Remove only an NPC's terminal combat-level suffix, in display text only. */
final class AutoShorten
{
    private static final Pattern LEVEL_AT_END = Pattern.compile(
        "^(.*?)([\\p{Zs}\\t ]*)\\(level-[0-9]{1,6}\\)[\\p{Zs}\\t\\u200B\\uFEFF]*$",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.DOTALL);
    private static final Pattern OTHER_LEVEL = Pattern.compile("(?i)\\(level-[0-9]+\\)");
    private AutoShorten() {}

    static String shorten(HoverCategory category, String target)
    {
        if (category != HoverCategory.NPC)
        {
            return target;
        }
        SafeMenuText parsed = SafeMenuText.parse(target);
        if (parsed == null || parsed.hasUnanchoredColor())
        {
            return target;
        }
        Matcher m = LEVEL_AT_END.matcher(parsed.visible);
        if (!m.matches() || m.group(1).trim().isEmpty() || OTHER_LEVEL.matcher(m.group(1)).find())
        {
            return target;
        }
        // No-space NPC suffixes are only verified when the name and level are
        // visibly separated by a recognized RuneLite color-tag boundary.
        if (m.group(2).isEmpty() && !parsed.hasColorBoundaryBefore(m.end(2)))
        {
            return target;
        }
        // Preserve all real NPC-name characters and their existing name markup.
        return parsed.prefix(m.end(1));
    }
}
