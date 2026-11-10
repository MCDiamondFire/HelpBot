package com.diamondfire.helpbot.util;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Formats ranked lists as one compact line per entry: {@code `#1` Name - value}.
 * The top three and the viewer's own row are bolded.
 */
public class Leaderboard {

    private static final int HIGHLIGHTED = 3;

    private final List<String> lines = new ArrayList<>();
    private String selfLine = null;

    /**
     * @param rank  1-based position
     * @param name  raw player name, escaped here; null shows "Unknown Player"
     * @param value already formatted value, e.g. "1,250 tokens"
     * @param self  whether this row is the viewer
     */
    public Leaderboard add(long rank, @Nullable String name, String value, boolean self) {
        String line = line(rank, name, value);
        lines.add(rank <= HIGHLIGHTED || self ? "**" + line + "**" : line);
        if (self) {
            selfLine = "";
        }

        return this;
    }

    /**
     * Adds the viewer's own position under the list, unless they already appear in it.
     */
    public Leaderboard self(long rank, @Nullable String name, String value) {
        if (selfLine == null) {
            selfLine = "**" + line(rank, name, value) + "**";
        }

        return this;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public String build() {
        String board = String.join("\n", lines);
        if (selfLine != null && !selfLine.isEmpty()) {
            board += "\n\n" + selfLine;
        }

        return board;
    }

    private static String line(long rank, @Nullable String name, String value) {
        return String.format("`#%s` %s - %s",
                FormatUtil.formatNumber(rank),
                name == null ? "Unknown Player" : StringUtil.escapeName(name),
                value);
    }

    /**
     * Formats a duration to the minute, as seconds only add noise on leaderboards.
     */
    public static String formatDuration(long millis) {
        long minutes = millis - millis % 60_000;
        return minutes == 0 ? "<1m" : FormatUtil.formatMilliTime(minutes);
    }

}
