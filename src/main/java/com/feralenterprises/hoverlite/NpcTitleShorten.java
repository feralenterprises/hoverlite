package com.feralenterprises.hoverlite;

import java.util.regex.Pattern;

/**
 * Known role/title prefixes in NPC display names. This never edits the
 * RuneLite menu. Unknown names and generic occupations are left alone.
 *
 * Avoid a blanket "remove first word" rule: that would corrupt real names
 * and labels such as General Shopkeeper or King Black Dragon.
 */
final class NpcTitleShorten
{
    // Remainder must look like a proper name (1-3 capitalized components),
    // not a job description such as "captain guard" or "general shopkeeper".
    private static final Pattern PROPER_NAME = Pattern.compile(
        "^[\\p{Lu}][\\p{L}'-]{1,}(?: [\\p{Lu}][\\p{L}'-]{1,}){0,2}$");

    private static final String[] RECOGNIZED_TITLES = {
        "First Mate ", "Ferryman ", "Captain ", "Commander ", "Duke "
    };

    private NpcTitleShorten() {}

    /** Return null for an unsupported/unsafe shortening. */
    static String shortenName(String name)
    {
        if (name == null)
        {
            return null;
        }

        // "General" also describes generic workers/shops. Verify the exact
        // boss name rather than treating every General X as a titled person.
        if (name.equalsIgnoreCase("General Graardor"))
        {
            return name.substring("General ".length());
        }

        for (String prefix : RECOGNIZED_TITLES)
        {
            if (name.length() > prefix.length()
                && name.regionMatches(true, 0, prefix, 0, prefix.length()))
            {
                String remainder = name.substring(prefix.length()).trim();
                if (PROPER_NAME.matcher(remainder).matches())
                {
                    return remainder;
                }
            }
        }
        return null;
    }
}
