package com.feralenterprises.hoverlite;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Parse only RuneLite's simple color markup. Never interpret arbitrary tags or
 * change the underlying MenuEntry. Unknown markup is intentionally unsupported.
 *
 * Unlike a regex anchored to one color layout, this retains the location of
 * every visible character, allowing a suffix to be removed even when a color
 * tag sits between a name and its combat level.
 */
final class SafeMenuText
{
    private static final Pattern OPEN = Pattern.compile("(?i)<col=[0-9a-f]{1,8}>");
    private static final Pattern CLOSE = Pattern.compile("(?i)</col>");
    // Permit invisible Unicode only AFTER a terminal combat level, before optional
    // whitespace/closing color tags. Embedded controls still fail closed.
    private static final Pattern LEVEL_BEFORE_TRAILING_INVISIBLE = Pattern.compile(
        "(?i).*\\(level-[0-9]{1,6}\\)[\\p{Zs}\\t\\u200B\\uFEFF]*$");
    private static final Pattern SAFE_TAIL = Pattern.compile(
        "(?i)(?:[\\p{Zs}\\t\\u200B\\uFEFF]|</col>)*");
    private static final Pattern COLOR_BOUNDARY = Pattern.compile(
        "(?i)(?:<col=[0-9a-f]{1,8}>|</col>)+");

    final String raw;
    final String visible;
    private final int[] rawEnds;
    private final String leadingColor;
    private final boolean simpleColor;
    private final boolean endsClosed;

    private SafeMenuText(String raw, String visible, int[] rawEnds,
                         String leadingColor, boolean simpleColor, boolean endsClosed)
    {
        this.raw = raw;
        this.visible = visible;
        this.rawEnds = rawEnds;
        this.leadingColor = leadingColor;
        this.simpleColor = simpleColor;
        this.endsClosed = endsClosed;
    }

    static SafeMenuText parse(String raw)
    {
        if (raw == null || raw.isEmpty() || raw.length() > 2048)
        {
            return null;
        }
        StringBuilder plain = new StringBuilder();
        List<Integer> ends = new ArrayList<>();
        String firstColor = null;
        boolean simpleColor = true;
        boolean hasVisible = false;
        boolean hasColor = false;
        int changedColorAtVisibleIndex = -1;
        int lastTagEnd = -1;
        boolean lastTagWasClose = false;
        for (int i = 0; i < raw.length();)
        {
            final char c = raw.charAt(i);
            if (Character.isISOControl(c) || c == '>')
            {
                return null;
            }
            if (c == '\u200b' || c == '\ufeff')
            {
                // A zero-width code point inside an NPC name is never silently
                // normalized. Only documented suffix-tail artifacts are allowed.
                if (!LEVEL_BEFORE_TRAILING_INVISIBLE.matcher(plain).matches()
                    || !SAFE_TAIL.matcher(raw.substring(i)).matches())
                {
                    return null;
                }
            }
            if (c == '<')
            {
                final int close = raw.indexOf('>', i + 1);
                if (close < 0)
                {
                    return null;
                }
                final String tag = raw.substring(i, close + 1);
                if (OPEN.matcher(tag).matches())
                {
                    if (hasColor && !hasVisible)
                    {
                        simpleColor = false;
                    }
                    if (hasVisible)
                    {
                        changedColorAtVisibleIndex = plain.length();
                    }
                    if (!hasColor && !hasVisible)
                    {
                        firstColor = tag;
                    }
                    hasColor = true;
                    lastTagWasClose = false;
                }
                else if (CLOSE.matcher(tag).matches())
                {
                    lastTagWasClose = true;
                }
                else
                {
                    return null;
                }
                lastTagEnd = close + 1;
                i = close + 1;
                continue;
            }
            plain.append(c);
            ends.add(i + 1);
            hasVisible = true;
            i++;
        }
        int[] offsets = new int[ends.size()];
        for (int i = 0; i < ends.size(); i++)
        {
            offsets[i] = ends.get(i);
        }
        // A color tag after the last visible character is harmless trailing
        // markup. A change within the visible target is meaningful, so never
        // rewrite that target's name without knowing the span's intent.
        if (changedColorAtVisibleIndex >= 0 && changedColorAtVisibleIndex < plain.length())
        {
            simpleColor = false;
        }
        return new SafeMenuText(raw, plain.toString(), offsets, firstColor,
            simpleColor, lastTagWasClose && lastTagEnd == raw.length());
    }

    /** A missing visible space is safe only when a real color tag separates spans. */
    boolean hasColorBoundaryBefore(int visibleIndex)
    {
        if (visibleIndex <= 0 || visibleIndex >= rawEnds.length)
        {
            return false;
        }
        final int gapStart = rawEnds[visibleIndex - 1];
        final int gapEnd = rawEnds[visibleIndex] - 1;
        return gapEnd > gapStart && COLOR_BOUNDARY.matcher(raw.substring(gapStart, gapEnd)).matches();
    }

    /** A floating color tag without a name-color opener is unsafe to abbreviate. */
    boolean hasUnanchoredColor()
    {
        return raw.toLowerCase(Locale.ROOT).contains("<col=") && leadingColor == null;
    }

    /** Preserve the exact original name markup, stopping before removable suffixes. */
    String prefix(int visibleCharacters)
    {
        if (visibleCharacters <= 0 || visibleCharacters > rawEnds.length)
        {
            return raw;
        }
        String shortened = raw.substring(0, rawEnds[visibleCharacters - 1]);
        // Keep a name's explicit closing color tag if one immediately follows it.
        final String rest = raw.substring(rawEnds[visibleCharacters - 1]);
        if (rest.regionMatches(true, 0, "</col>", 0, 6))
        {
            shortened += rest.substring(0, 6);
        }
        else if (simpleColor && endsClosed && leadingColor != null
            && !shortened.toLowerCase(Locale.ROOT).endsWith("</col>"))
        {
            shortened += "</col>";
        }
        return shortened;
    }

    /** Only replace whole names when an initial, uniform name color is known. */
    String replaceName(String replacement)
    {
        if (!simpleColor || replacement == null || replacement.trim().isEmpty()
            || replacement.indexOf('<') >= 0 || replacement.indexOf('>') >= 0)
        {
            return raw;
        }
        return (leadingColor == null ? "" : leadingColor) + replacement
            + (endsClosed && leadingColor != null ? "</col>" : "");
    }
}
