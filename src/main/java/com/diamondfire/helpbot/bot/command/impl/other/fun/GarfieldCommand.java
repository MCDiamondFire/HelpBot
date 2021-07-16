package com.diamondfire.helpbot.bot.command.impl.other.fun;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.*;
import java.net.URL;


public class GarfieldCommand extends Command {
    
    @Override
    public String getName() {
        return "garfield";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets a random garfield comic from Ottelino's garfield API.")
                .category(CommandCategory.OTHER);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        try {
            URL url = new URL("https://labscore.vercel.app/v2/garfield");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String link = JsonParser.parseString(in.readLine()).getAsJsonObject().get("link").getAsString();
                
                if (link == null) {
                    throw new IOException();
                } else {
                    builder.setTitle("Garfield Comic");
                    builder.setImage(link);
                    builder.setColor(new Color(252, 166, 28));
                }
            }
        } catch (Exception e) {
            builder.setTitle(":rotating_light: API BROKE :rotating_light:");
            builder.setDescription("DM: <@223518178100248576>\nPING: <@223518178100248576>");
        }
        event.getChannel().sendMessage(builder.build()).queue();
    }
    
}
