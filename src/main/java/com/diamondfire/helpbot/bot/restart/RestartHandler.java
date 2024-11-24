package com.diamondfire.helpbot.bot.restart;

import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.nio.file.Files;

public class RestartHandler {
    
    public static void logRestart(Message restartMSG) {
        
        long msg = restartMSG.getIdLong();
        long channel = restartMSG.getChannel().getIdLong();
        
        try {
            File restart = ExternalFileUtil.generateFile("restart_cache");
            try (FileWriter writer = new FileWriter(restart)) {
                writer.append(String.valueOf(msg)).append(":").append(String.valueOf(channel)).append(":").append(String.valueOf(System.currentTimeMillis()));
            }
        } catch (IOException ignored) {
        }
        
    }
    
    public static void recover(JDA jda) {
        try {
            File restart = ExternalFileUtil.getFile("restart_cache");
            if (!restart.exists()) {
                return;
            }
            String[] restartMSG = Files.readAllLines(restart.toPath()).get(0).split(":");
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Restart Successful!");
            builder.setDescription("Restarted in " + FormatUtil.formatMilliTime(System.currentTimeMillis() - Long.parseLong(restartMSG[2])));
            
            jda.getTextChannelById(restartMSG[1]).editMessageEmbedsById(restartMSG[0], builder.build()).setReplace(true).queue();
            restart.delete();
        } catch (IOException | ArrayIndexOutOfBoundsException ignored) {
        }
    }
    
}
