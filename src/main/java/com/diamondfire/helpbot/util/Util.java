package com.diamondfire.helpbot.util;

import com.diamondfire.helpbot.components.externalfile.ExternalFile;
import com.diamondfire.helpbot.instance.BotInstance;
import com.google.gson.JsonArray;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Util {


    public static File fetchMinecraftTextureFile(String fileName) {
        try {
            if (fileName.endsWith("spawn_egg")) {
                return new File(ExternalFile.IMAGES_DIR.getFile(), "spawn_egg.png");
            }
            return new File(ExternalFile.IMAGES_DIR.getFile(), fileName + ".png");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File(ExternalFile.IMAGES_DIR.getFile(), "unknown_texture.png");
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
        TextChannel channel = BotInstance.getJda().getTextChannelById(705205549498892299L);
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
