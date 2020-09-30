package com.diamondfire.helpbot.util;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

public class EmbedUtils {

    public static void addFields(EmbedBuilder builder, Iterable<String> strings) {
        addFields(builder, strings, "> ", "", true);
    }

    public static void addFields(EmbedBuilder builder, Iterable<String> strings, String pointer) {
        addFields(builder, strings, pointer, "", false);
    }

    public static void addFields(EmbedBuilder builder, Iterable<String> strings, boolean sanitize) {
        addFields(builder, strings, "> ", "", sanitize);
    }

    public static void addFields(EmbedBuilder builder, Iterable<String> strings, String pointer, String name) {
        addFields(builder, strings, pointer, name, false);
    }

    public static void addFields(EmbedBuilder builder, Iterable<String> strings, String pointer, boolean sanitize) {
        addFields(builder, strings, pointer, "", sanitize);
    }

    public static void addFields(EmbedBuilder builder, Iterable<String> strings, String pointer, String name, boolean sanitize) {
        boolean firstField = true;
        //Current selection must be a stack to keep order.
        Stack<String> currentSelection = new Stack<>();
        Deque<String> queue = new ArrayDeque<>();
        for (String string : strings) {
            queue.add(string);
        }

        if (queue.isEmpty()) {
            return;
        }

        while (true) {
            currentSelection.push(queue.peek());

            // We check with the checkView to see if the size is too large.
            String preCheck = StringUtil.listView(pointer, sanitize, currentSelection.toArray(new String[0]));
            String checkView = sanitize ? StringUtil.display(preCheck) : preCheck;
            if (checkView.length() > 1024 || queue.size() == 1) {
                String overflowView = null;

                // If we are on the last index and the length is too much, we will add an overflow view that contains that entry only.
                if (queue.size() == 1 && checkView.length() > 1024) {
                    String preOverFlow = StringUtil.listView(pointer, sanitize, new String[]{currentSelection.pop()});
                    overflowView = sanitize ? StringUtil.display(preOverFlow) : preOverFlow;
                }
                // If we are NOT on last then we will just remove the element we just tested from the currentSelection stack, as it seems to be too big.
                else if (queue.size() != 1) {
                    currentSelection.pop();
                }

                String toDisplay = StringUtil.listView(pointer, sanitize, currentSelection.toArray(new String[0]));
                builder.addField(firstField ? name : "",  sanitize ? StringUtil.display(toDisplay) : toDisplay, false);
                firstField = false;
                currentSelection.clear();

                if (overflowView != null) {
                    builder.addField("", overflowView, false);
                }

                if (queue.size() == 1) {
                    break;
                }

            } else {
                // Remove element because it's big enough.
                queue.pop();
            }

        }

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
}
