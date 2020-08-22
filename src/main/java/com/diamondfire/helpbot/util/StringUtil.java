package com.diamondfire.helpbot.util;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.Command;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.text.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class StringUtil {

    public static String listView(String pointer, boolean sanitize, Iterable<? extends CharSequence> array) {
        String view = listView(pointer,array);
        return sanitize ? StringUtil.display(view) : view;
    }

    public static String listView(String pointer, boolean sanitize, CharSequence... elements) {
        String view = listView(pointer, elements);
        return sanitize ? StringUtil.display(view) : view;
    }

    public static String listView(String pointer, Iterable<? extends CharSequence> array) {
        if (!array.iterator().hasNext()) {
            return "";
        }

        return ("\n%s% " + String.join("\n%s% ", array)).replaceAll("%s%", pointer);
    }

    public static String listView(String pointer, CharSequence... elements) {
        if (elements.length == 0) {
            return "";
        }

        return ("\n%s% " + String.join("\n%s% ", elements)).replaceAll("%s%", pointer);
    }

    public static String asciidocStyle(Map<String, String> hashes) {
        if (hashes.size() == 0) {
            return "";
        }

        String longest = hashes.keySet().stream().max(Comparator.comparingInt(String::length)).orElse(null);

        ArrayList<String> strings = new ArrayList<>();
        hashes.entrySet().forEach((stringIntegerEntry -> strings.add(stringIntegerEntry.getKey() +
                " ".repeat((longest.length() + 2) - stringIntegerEntry.getKey().length()) + ":: " + stringIntegerEntry.getValue())));

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
                builder.append(word.toLowerCase()).append(" ");
                continue;
            }

            builder.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append(" ");
        }
        return builder.toString();
    }

    public static String stripColorCodes(String text) {
        return text.replaceAll("[&§][a-zA-Z0-9]", "");
    }

    public static String formatMilliTime(long millis) {
        StringBuilder builder = new StringBuilder();

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        if (days > 0) {
            builder.append(days).append("d ");
        }

        long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
        if (hours > 0) {
            builder.append(hours).append("h ");
        }

        long mins = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        if (mins > 0) {
            builder.append(mins).append("m ");
        }

        long secs = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        if (secs != 0) {
            builder.append(secs).append("s ");
        } else {
            if (builder.length() == 0) {
                builder.append("0.").append(TimeUnit.MILLISECONDS.toMillis(millis)).append("s");
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

    public static String displayArguments(HelpContext context) {
        return String.join(" ", StringUtil.getArgumentDisplay(context));
    }

    public static String[] getArgumentDisplay(HelpContext context) {
        return context.getArguments().stream()
                .map(argument -> argument.isOptional() ? String.format("[<%s>]", argument.getArgumentName()) : String.format("<%s>", argument.getArgumentName()))
                .toArray(String[]::new);
    }

    public static String displayCommand(Command cmd) {
        return HelpBotInstance.getConfig().getPrefix() + cmd.getName();
    }
}
