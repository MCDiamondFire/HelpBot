package com.diamondfire.helpbot.util;

import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.text.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class StringUtil {

    public static String listView(String[] array, String pointer, boolean sanitize) {
        String view = listView(array,pointer);
        return sanitize ? StringUtil.display(view) : view;
    }

    public static String listView(String[] array, String pointer) {
        if (array.length == 0) {
            return "";
        }
        return ("\n%s% " + String.join("\n%s% ", array)).replaceAll("%s%", pointer);
    }

    public static String asciidocStyle(HashMap<String, Integer> hashes) {
        if (hashes.size() == 0) {
            return "";
        }

        String longest = hashes.keySet().stream().max(Comparator.comparingInt(String::length)).orElse(null);

        ArrayList<String> strings = new ArrayList<>();
        hashes.entrySet().forEach((stringIntegerEntry -> strings.add(stringIntegerEntry.getKey() +
                        Util.repeat("", " ", (longest.length() + 2) - stringIntegerEntry.getKey().length()) + ":: " + stringIntegerEntry.getValue())));

        return String.join("\n", strings);
    }

    public static String fieldSafe(String string) {
        if (string.length() >= 950) {
            return string.substring(0, 950) + "...";
        }
        return string;
    }

    public static String fieldSafe(Object object) {
        return fieldSafe(String.valueOf(object));
    }

    public static String titleSafe(String string) {
        if (string.length() >= 200) {
            return string.substring(0, 200);
        }
        return string;
    }

    public static String smartCaps(String text) {
        String[] words = text.split(" ");
        StringBuilder builder = new StringBuilder();

        for (String word : words) {
            if (word.length() < 2) {
                builder.append(word.toLowerCase() + " ");
                continue;
            }

            builder.append(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ");
        }
        return builder.toString();
    }

    public static String stripColorCodes(String text) {
        return text.replaceAll("[&§][(a-z)(A-Z)(0-9)]", "");
    }

    public static String formatMilliTime(long millis) {
        StringBuilder builder = new StringBuilder();

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        if (days > 0) {
            builder.append(days + "d ");
        }

        long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
        if (hours > 0) {
            builder.append(hours + "h ");
        }

        long mins = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        if (mins > 0) {
            builder.append(mins + "m ");
        }

        long secs = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        if (secs != 0) {
            builder.append(secs + "s ");
        } else {
            if (builder.length() == 0) {
                builder.append("0." + TimeUnit.MILLISECONDS.toMillis(millis) + "s");
            }
        }
        return builder.toString();
    }

    public static String formatTime(long duration, TimeUnit unit) {
        return formatMilliTime(unit.toMillis(duration));
    }

    public static String display(String string) {
        return MarkdownSanitizer.escape(stripColorCodes(string));
    }

    public static String formatDate(Date date) {
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        return format.format(date);
    }

    public static List<String> splitBy(String string, int byAmt) {
        List<String> parts = new ArrayList<>();
        int length = string.length();
        for (int i = 0; i < length; i += byAmt) {
            parts.add(string.substring(i, Math.min(length, i + byAmt)));
        }
        return parts;
    }

    public static String formatNumber(long number) {
        return getFormat().format(number);
    }

    public static String formatNumber(double number) {
        return getFormat().format(number);
    }

    private static NumberFormat getFormat() {
        return NumberFormat.getInstance(new Locale("en", "US"));
    }
}
