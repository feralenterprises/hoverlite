package com.feralenterprises.hoverlite;

import java.util.regex.Pattern;

/** Display-only whole-name cleanup for exact owner-verified interactions. */
final class TargetShorten
{
    private static final Pattern TREE = Pattern.compile(
        "^(Oak|Willow|Maple|Yew|Magic|Teak|Mahogany|Redwood) tree$", Pattern.CASE_INSENSITIVE);
    private TargetShorten() {}

    static String shorten(HoverCategory category, String action, String target)
    {
        if (category == null || action == null)
        {
            return target;
        }
        final SafeMenuText parsed = SafeMenuText.parse(target);
        if (parsed == null || parsed.hasUnanchoredColor())
        {
            return target;
        }
        final String name = parsed.visible.trim();
        String replacement = null;
        if (category == HoverCategory.GAME_OBJECT)
        {
            if (action.equalsIgnoreCase("Chop down") && TREE.matcher(name).matches())
            {
                replacement = name.substring(0, name.length() - " tree".length());
            }
            else if (action.equalsIgnoreCase("Pick") && name.equalsIgnoreCase("Limpwurt plant"))
            {
                replacement = "Limpwurt";
            }
            else if (action.equalsIgnoreCase("Open") && name.equalsIgnoreCase("Seed vault"))
            {
                replacement = "Vault";
            }
            else if (action.equalsIgnoreCase("Mine") && name.equalsIgnoreCase("Iron rocks"))
            {
                replacement = "Iron";
            }
            else if (action.equalsIgnoreCase("Mine") && name.equalsIgnoreCase("Coal rocks"))
            {
                replacement = "Coal";
            }
        }
        else if (category == HoverCategory.NPC && action.equalsIgnoreCase("Exchange")
            && name.equalsIgnoreCase("Tool Leprechaun"))
        {
            replacement = "Leprechaun";
        }
        return replacement == null ? target : parsed.replaceName(replacement);
    }
}
