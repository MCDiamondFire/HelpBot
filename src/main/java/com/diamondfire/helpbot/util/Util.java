package com.diamondfire.helpbot.util;

import com.diamondfire.helpbot.sys.externalfile.ExternalFile;
import com.diamondfire.helpbot.bot.HelpBotInstance;
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

    public static LinkedList<String> getUnicodeNumbers() {
        LinkedList<String> nums = new LinkedList<>();
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


    public static EmbedBuilder addFields(EmbedBuilder builder, List<String> strings) {
        return addFields(builder, strings, "", "> ", false);
    }

    public static EmbedBuilder addFields(EmbedBuilder builder, List<String> strings, boolean sanatize) {
        return addFields(builder, strings, "", "> ", sanatize);
    }

    public static EmbedBuilder addFields(EmbedBuilder builder, List<String> strings, String name, String pointer) {
        return addFields(builder, strings, name, "> ", false);
    }

    public static EmbedBuilder addFields(EmbedBuilder builder, List<String> strings, String name, String pointer, boolean sanatize) {

        String list;
        String lastList = null;
        LinkedList<String> queue = new LinkedList<>();
        boolean firstField = true;


        for (int i = 0; i < strings.size(); i++) {
            String dataName = strings.get(i);

            queue.add(dataName);
            list = StringUtil.display(StringUtil.listView(queue.toArray(new String[0]), pointer, sanatize));

            if (i == strings.size() - 1) {
                builder.addField(firstField ? name : "", list, false);
                firstField = false;
            } else if (list.length() >= 1000) {
                queue.removeFirst();
                builder.addField(firstField ? name : "", lastList, false);
                firstField = false;

                queue.clear();
                queue.add(dataName);
            }
            lastList = list;

        }
        return builder;
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

    public static String repeat(String ogString, String repeat, int i) {
        StringBuilder ogStringBuilder = new StringBuilder(ogString);
        for (int j = 0; j < i; j++) {
            ogStringBuilder.append(repeat);
        }
        ogString = ogStringBuilder.toString();
        return ogString;
    }

    public static String sCheck(String text, Number number) {
        return number.intValue() == 1 ? text : text + "s";
    }

    public static int clamp(int num, int min, int max) {
        return Math.max(min, Math.min(num, max));
    }


}
