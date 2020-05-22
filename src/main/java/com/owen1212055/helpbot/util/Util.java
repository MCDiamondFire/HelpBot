package com.owen1212055.helpbot.util;

import com.google.gson.JsonArray;
import com.owen1212055.helpbot.components.ExternalFileHandler;
import com.owen1212055.helpbot.instance.BotInstance;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.stream.Collectors;

public class Util {

    public static Image fetchImage(String fileName) {
        try {
            return new ImageIcon(ClassLoader.getSystemClassLoader().getResource(fileName + ".png")).getImage();
        } catch (Exception e) {
            System.out.println("Error while loading image! " + fileName);
            e.printStackTrace();
        }
        return new ImageIcon().getImage();

    }

    public static File fetchMinecraftTextureFile(String fileName) {
        try {
            if (fileName.endsWith("spawn_egg")) {
                return new File(ExternalFileHandler.IMAGES.getPath() + "/spawn_egg.png");
            }
            return new File(ExternalFileHandler.IMAGES.getPath() + "/" + fileName + ".png");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File(ExternalFileHandler.IMAGES.getPath()  + "/" + "unknown_texture" + ".png");
    }

    /**
     * Fetch the file contents based on the url.
     */
    public static String fetchFileContents(String fileName) {
        try {
            BufferedReader txtReader = new BufferedReader(new InputStreamReader(Util.class.getResourceAsStream("/" + fileName), "UTF16"));
            return txtReader.lines().collect(Collectors.joining());
        } catch (Exception e) {
            return "";
        }
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
        embed.setTitle(title);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String sStackTrace = sw.toString();

        embed.setColor(Color.RED);
        channel.sendMessage(embed.build()).queue();
        channel.sendMessage(String.format("```%s```", sStackTrace.length() >= 1500 ? sStackTrace.substring(0, 1500) : sStackTrace)).queue();
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
