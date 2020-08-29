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
        String view = listView(pointer, array);
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
        List<String> symbols = new ArrayList<>();
        applyTimeUnit(symbols, millis, TimeUnit.DAYS, 'd');
        applyTimeUnit(symbols, millis, TimeUnit.HOURS, 'h');
        applyTimeUnit(symbols, millis, TimeUnit.MINUTES, 'm');
        applyTimeUnit(symbols, millis, TimeUnit.SECONDS, 's');

        // Only show milliseconds if nothing else is visible.
        if (symbols.size() == 0) {
            symbols.add("0." + getTimeUnit(millis, TimeUnit.MILLISECONDS) + "s");
        }

        return String.join(" ", symbols);
    }

    private static void applyTimeUnit(List<String> symbols, long millis, TimeUnit unit, char symbol) {
        long amt = getTimeUnit(millis, unit);
        if (amt > 0) {
            symbols.add(amt + String.valueOf(symbol));
        }
    }

    public static long getTimeUnit(long millis, TimeUnit unit) {
        switch (unit) {
            case DAYS:
                return TimeUnit.MILLISECONDS.toDays(millis);
            case HOURS:
                return TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
            case MINUTES:
                return TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
            case SECONDS:
                return TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
            case MILLISECONDS:
                return TimeUnit.MILLISECONDS.toMillis(millis) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis));
            case MICROSECONDS:
                return millis;
            case NANOSECONDS:
                return TimeUnit.MILLISECONDS.toNanos(millis);
            default:
                throw new UnsupportedOperationException();
        }
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

    /**
     * Decodes from Base64
     *
     * @param Base64F Compressed Data
     * @return Decoded Data
     */
    public static byte[] fromBase64(byte[] Base64F) {
        return Base64.getDecoder().decode(Base64F);
    }

    /**
     * Encodes into Base64
     *
     * @param Base64F Data
     * @return Encoded Data
     */
    public static byte[] toBase64(byte[] Base64F) {
        return Base64.getEncoder().encode(Base64F);
    }
}
