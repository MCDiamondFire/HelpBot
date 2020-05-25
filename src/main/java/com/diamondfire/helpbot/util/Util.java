package com.diamondfire.helpbot.util;

import com.diamondfire.helpbot.components.ExternalFileHandler;
import com.diamondfire.helpbot.instance.BotInstance;
import com.google.gson.JsonArray;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.stream.Collectors;

public class Util {


    public static File fetchMinecraftTextureFile(String fileName) {
        try {
            if (fileName.endsWith("spawn_egg")) {
                return new File(ExternalFileHandler.IMAGES.getPath() + "/spawn_egg.png");
            }
            return new File(ExternalFileHandler.IMAGES.getPath() + "/" + fileName + ".png");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File(ExternalFileHandler.IMAGES.getPath() + "/" + "unknown_texture" + ".png");
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
        EmbedBuilder embed = new EmbedBuilder();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String sStackTrace = sw.toString();

        embed.setTitle(title);


        embed.setColor(Color.RED);
        channel.sendMessage(embed.build()).queue();
        channel.sendMessage(String.format("```%s```", sStackTrace.length() >= 1500 ? sStackTrace.substring(0, 1500) : sStackTrace)).queue();

        e.printStackTrace(pw);
    }

    public static String repeat(String ogString, String repeat, int i) {
        for (int j = 0; j < i; j++) {
            ogString = ogString + repeat;
        }
        return ogString;
    }

    public static String sCheck(String text, Number number) {
        return number.intValue() == 1 ? text : text + "s";
    }


}
