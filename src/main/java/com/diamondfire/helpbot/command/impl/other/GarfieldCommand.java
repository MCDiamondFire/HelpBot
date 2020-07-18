package com.diamondfire.helpbot.command.impl.other;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.events.CommandEvent;
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
    public ArgumentSet getArguments() {
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
            URL url = new URL("https://labscore.vercel.app/api/v1/garfield/link");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String line = in.readLine();

                if (line == null) {
                    throw new IOException();
                } else {
                    builder.setTitle("Random Garfield Comic");
                    builder.setImage(line);
                    builder.setColor(new Color(252, 166, 28));
                }
            }
        } catch (IOException e) {
            builder.setTitle(":rotating_light: API BROKE :rotating_light:");
            builder.setDescription("DM: <@223518178100248576>\nPING: <@223518178100248576>");
        }
        event.getChannel().sendMessage(builder.build()).queue();
    }

}
