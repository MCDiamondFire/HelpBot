package com.diamondfire.helpbot.util;

import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class StringUtil {

    public static String listView(String[] array, String pointer, boolean sanitize) {
        if (array.length == 0) {
            return "";
        }

        String list = ("\n%s% " + String.join("\n%s% ", array)).replaceAll("%s%", pointer);


        return sanitize ? MarkdownSanitizer.escape(list) : list;
    }

    public static String asciidocStyle(HashMap<String, Integer> hashes) {
        if (hashes.size() == 0) {
            return "";
        }

        String longest = hashes.keySet().stream().max(Comparator.comparingInt(String::length)).orElse(null);

        ArrayList<String> strings = new ArrayList<>();

        hashes.entrySet()
                .forEach((stringIntegerEntry -> strings.add(stringIntegerEntry.getKey() +
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
        return text.replaceAll("&[(a-z)(A-Z)(0-9)]", "");
    }

    public static String formatMilliTime(long millis) {
        StringBuilder builder = new StringBuilder();
        boolean disallowDecimal = false;

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        if (days > 0) {
            builder.append(days + "d ");
            disallowDecimal = true;
        }

        long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
        if (hours > 0) {
            builder.append(" " + hours + "h");
            disallowDecimal = true;
        }

        long mins = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        if (mins > 0) {
            builder.append(" " + mins + "m");
            disallowDecimal = true;
        }

        long secs = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        if (secs != 0) {
            builder.append(" " + secs + "s");
        } else {
            if (!disallowDecimal) {
                builder.append("0." + TimeUnit.MILLISECONDS.toMillis(millis) + "s");
            }
        }

        return builder.toString();

    }

    public static String display(String string) {
        return MarkdownSanitizer.escape(stripColorCodes(string));
    }

    @SuppressWarnings("deprecation")
    public static String formatDate(Date date) {
        return (date.getMonth() + 1) + "/" + (date.toLocalDate().getDayOfMonth()) + "/" + (date.getYear() + 1900);
    }
}
