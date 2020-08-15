package com.diamondfire.helpbot.util;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.externalfile.*;
import com.google.gson.JsonArray;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.*;

public class Util {

    public static Deque<String> getUnicodeNumbers() {
        Deque<String> nums = new ArrayDeque<>();
        nums.add("\u0031\uFE0F\u20E3");
        nums.add("\u0032\uFE0F\u20E3");
        nums.add("\u0033\uFE0F\u20E3");
        nums.add("\u0034\uFE0F\u20E3");
        nums.add("\u0035\uFE0F\u20E3");
        nums.add("\u0036\uFE0F\u20E3");
        nums.add("\u0037\uFE0F\u20E3");
        nums.add("\u0038\uFE0F\u20E3");
        nums.add("\u0039\uFE0F\u20E3");
        nums.add("\uD83D\uDD1F");
        return nums;
    }


    public static void addFields(EmbedBuilder builder, List<String> strings) {
        addFields(builder, strings, "", "> ", false);
    }

    public static void addFields(EmbedBuilder builder, List<String> strings, boolean sanitize) {
        addFields(builder, strings, "", "> ", sanitize);
    }

    public static void addFields(EmbedBuilder builder, List<String> strings, String name, String pointer) {
        addFields(builder, strings, name, pointer, false);
    }

    public static void addFields(EmbedBuilder builder, List<String> strings, String name, String pointer, boolean sanitize) {

        boolean firstField = true;
        //Current selection must be a stack to keep order.
        Stack<String> currentSelection = new Stack<>();
        Deque<String> queue = new ArrayDeque<>(strings);

        for (int i = 0; i < strings.size(); i++) {
            currentSelection.push(queue.peek());

            // We check with the checkView to see if the size is too large.
            String checkView = StringUtil.display(StringUtil.listView(currentSelection.toArray(new String[0]), pointer, sanitize));
            if (checkView.length() > 1024 || queue.size() == 1) {
                String overflowView = null;

                // If we are on the last index and the length is too much, we will add an overflow view that contains that entry only.
                if (queue.size() == 1 && checkView.length() > 1024) {
                    overflowView = StringUtil.display(StringUtil.listView(new String[]{currentSelection.pop()}, pointer, sanitize));
                }
                // If we are NOT on last then we will just remove the element we just tested from the currentSelection stack, as it seems to be too big.
                else if (queue.size() != 1) {
                    currentSelection.pop();
                }

                builder.addField(firstField ? name : "", StringUtil.display(StringUtil.listView(currentSelection.toArray(new String[0]), pointer, sanitize)), false);
                firstField = false;
                currentSelection.clear();

                if (overflowView != null) {
                    builder.addField("", overflowView, false);
                }

            } else {
                // Remove element because it's big enough.
                queue.pop();
            }

        }

    }

    public static File fetchMinecraftTextureFile(String fileName) {
        try {
            File file = new File(ExternalFile.IMAGES_DIR.getFile(), fileName + ".png");

            if (file.exists()) {
                return file;
            } else {
                return new File(ExternalFile.IMAGES_DIR.getFile(), "BARRIER.png");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File(ExternalFile.IMAGES_DIR.getFile(), "BARRIER.png");
    }

    public static File getPlayerHead(String player) {
        File check = new File(ExternalFile.HEAD_CACHE_DIR.getFile(), player + ".png");
        if (check.exists()) {
            return check;
        }

        String url = String.format("https://mc-heads.net/head/%s", player);
        try {
            URL website = new URL(url);
            InputStream inputStream = website.openStream();
            Files.copy(inputStream, Paths.get(check.toURI()), StandardCopyOption.REPLACE_EXISTING);
            return check;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Converts a jsonArray into a String[]
     */
    public static String[] jsonArrayToString(JsonArray jsonArray) {
        if (jsonArray == null) {
            return new String[]{};
        }
        String[] string = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            string[i] = jsonArray.get(i).getAsString();
        }
        return string;

    }

    public static void error(Exception e, String title) {
        TextChannel channel = HelpBotInstance.getJda().getTextChannelById(705205549498892299L);
        StringWriter sw = new StringWriter();
        EmbedBuilder embed = new EmbedBuilder();
        PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);

        String sStackTrace = sw.toString();

        embed.setTitle(title);
        embed.setColor(Color.RED);

        channel.sendMessage(embed.build()).queue();
        channel.sendMessage(String.format("```%s```", sStackTrace.length() >= 1500 ? sStackTrace.substring(0, 1500) : sStackTrace)).queue();
    }

    public static void log(MessageEmbed embed) {
        TextChannel channel = HelpBotInstance.getJda().getTextChannelById(705205549498892299L);
        channel.sendMessage(embed).queue();
    }

    public static String sCheck(String text, Number number) {
        return number.intValue() == 1 ? text : text + "s";
    }

    public static int clamp(int num, int min, int max) {
        return Math.max(min, Math.min(num, max));
    }

    public static File getFileFromSite(String url, String name) {
        try (InputStream in = new URL(url).openStream();) {
            File tempFile = ExternalFileUtil.generateFile(name);

            Files.write(tempFile.toPath(), in.readAllBytes(), StandardOpenOption.WRITE);

            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
