package com.feralenterprises.hoverlite;

/** Pure formatting logic: no game actions, menu modification, or state writes. */
final class HoverText
{
    private HoverText() {}

    static String format(HoverMode mode, String action, String target)
    {
        if (mode == null || mode == HoverMode.HIDDEN || action == null || action.trim().isEmpty())
        {
            return null;
        }
        if (mode == HoverMode.INDICATOR)
        {
            return "<col=65eeb0>●</col>";
        }
        if (mode == HoverMode.ACTION_ONLY)
        {
            return action;
        }
        return target == null || target.isEmpty() ? action : action + " " + target;
    }
}
