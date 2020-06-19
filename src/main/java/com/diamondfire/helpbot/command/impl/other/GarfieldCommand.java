package com.diamondfire.helpbot.command.impl.other;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.impl.CommandCategory;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


public class GarfieldCommand extends Command {

    @Override
    public String getName() {
        return "garfield";
    }

    @Override
    public String getDescription() {
        return "Garfield comic, now!";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.HIDDEN;
    }

    @Override
    public Argument getArgument() {
        return new NoArg();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        try {
            URL url = new URL("https://garfield-api.glitch.me/v1/link");
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
