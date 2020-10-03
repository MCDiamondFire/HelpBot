package com.diamondfire.helpbot.util;

import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.*;

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

        return "\n " + pointer + String.join("\n " + pointer, array);
    }

    public static String listView(String pointer, CharSequence... elements) {
        if (elements.length == 0) {
            return "";
        }

        return "\n " + pointer + String.join("\n " + pointer, elements);
    }

    public static String asciidocStyle(Map<String, String> hashes) {
        if (hashes.size() == 0) {
            return "";
        }
        String longest = hashes.keySet().stream().max(Comparator.comparingInt(String::length)).orElse(null);
        ArrayList<String> strings = new ArrayList<>();

        for (Map.Entry<String, String> entry : hashes.entrySet()) {
            String builder = entry.getKey() +
                    " ".repeat((longest.length() + 2) - entry.getKey().length()) +
                    ":: " +
                    entry.getValue();

            strings.add(builder);
        }

        return String.join("\n", strings);
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

    public static List<String> splitBy(String string, int byAmt) {
        List<String> parts = new ArrayList<>();
        int length = string.length();
        for (int i = 0; i < length; i += byAmt) {
            parts.add(string.substring(i, Math.min(length, i + byAmt)));
        }
        return parts;
    }

    public static String display(String string) {
        return MarkdownSanitizer.escape(StringUtil.stripColorCodes(string));
    }

    public static String sCheck(String text, int number) {
        return number == 1 ? text : text + "s";
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
