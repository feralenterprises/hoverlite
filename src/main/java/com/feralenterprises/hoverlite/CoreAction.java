package com.feralenterprises.hoverlite;

import java.util.Locale;
import java.util.regex.Pattern;

/** Verified, category-aware action cleanup. Directions and modifiers are sacred. */
final class CoreAction
{
    private static final Pattern BASIC_TREE = Pattern.compile(
        "^(Oak|Willow|Maple|Yew|Magic|Teak|Mahogany|Redwood) tree$|^Tree$|^Dead tree$|^Spirit tree$",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern BUSH = Pattern.compile(
        "^[\\p{L}][\\p{L} '-]* bush$", Pattern.CASE_INSENSITIVE);
    private CoreAction() {}

    static String shorten(HoverCategory category, String action, String target)
    {
        if (category == null || action == null || action.isEmpty() || action.indexOf('<') >= 0
            || action.indexOf('>') >= 0)
        {
            return action;
        }
        SafeMenuText parsed = SafeMenuText.parse(target);
        if (parsed == null || parsed.hasUnanchoredColor())
        {
            return action;
        }
        final String name = parsed.visible.trim();
        String replacement = null;
        if (category == HoverCategory.NPC && action.equalsIgnoreCase("Talk-to"))
        {
            replacement = "Talk";
        }
        else if (category == HoverCategory.GAME_OBJECT)
        {
            if (action.equalsIgnoreCase("Chop down") && BASIC_TREE.matcher(name).matches())
            {
                replacement = "Chop";
            }
            else if (action.equalsIgnoreCase("Pray-at") && name.equalsIgnoreCase("Altar"))
            {
                replacement = "Pray";
            }
            else if (action.equalsIgnoreCase("Pick-from") && BUSH.matcher(name).matches())
            {
                replacement = "Pick";
            }
            else if (action.equalsIgnoreCase("Pick-spine") && name.equalsIgnoreCase("Cactus"))
            {
                replacement = "Pick";
            }
        }
        if (replacement == null)
        {
            return action;
        }
        return Character.isLowerCase(action.charAt(0)) ? replacement.toLowerCase(Locale.ROOT) : replacement;
    }
}
