package com.diamondfire.helpbot.bot.restart;

import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.nio.file.*;

public class RestartHandler {
    
    public static void logRestart(Message restartMSG) {
        
        long msg = restartMSG.getIdLong();
        long channel = restartMSG.getChannel().getIdLong();
        
        try {
            Path restart = ExternalFileUtil.generateFile("restart_cache");
            Files.writeString(restart, msg + ":" + channel + ":" + System.currentTimeMillis());
        } catch (IOException ignored) {
        }
        
    }
    
    public static void recover(JDA jda) {
        try {
            Path restart = ExternalFileUtil.getFile("restart_cache");
            if (!Files.exists(restart)) {
                return;
            }
            
            String[] restartMSG = Files.readString(restart).trim().split(":");
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Restart Successful!");
            builder.setDescription("Restarted in " + FormatUtil.formatMilliTime(System.currentTimeMillis() - Long.parseLong(restartMSG[2])));
            
            jda.getTextChannelById(restartMSG[1]).editMessageEmbedsById(restartMSG[0], builder.build()).override(true).queue();
            
            Files.delete(restart);
        } catch (IOException | ArrayIndexOutOfBoundsException ignored) {
        }
    }
    
}
