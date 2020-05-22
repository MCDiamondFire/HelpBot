package com.owen1212055.helpbot.command.commands;

import com.owen1212055.helpbot.components.codedatabase.CodeDifferenceHandler;
import com.owen1212055.helpbot.components.codedatabase.db.CodeDatabase;
import com.owen1212055.helpbot.command.arguments.Argument;
import com.owen1212055.helpbot.command.arguments.NoArg;
import com.owen1212055.helpbot.command.permissions.Permission;
import com.owen1212055.helpbot.events.CommandEvent;
import com.owen1212055.helpbot.components.ExternalFileHandler;
import com.owen1212055.helpbot.util.StringFormatting;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.LinkedHashMap;

public class InfoCommand extends Command{
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Get info on the current active code database";
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

        LinkedHashMap<String, Integer> dataStats = new LinkedHashMap<>();

        dataStats.put("CodeBlocks", CodeDatabase.getCodeBlocks().size());
        dataStats.put("Actions", CodeDatabase.getActions().size());
        dataStats.put("Sounds", CodeDatabase.getSounds().size());
        dataStats.put("Particles", CodeDatabase.getParticles().size());
        dataStats.put("Potions", CodeDatabase.getPotions().size());
        dataStats.put("Game Value", CodeDatabase.getGameValues().size());
        dataStats.put("Legacy Actions", CodeDatabase.getDeprecatedActions().size());
        dataStats.put("Legacy Game Values", CodeDatabase.getDeprecatedGameValues().size());

        builder.addField("Current Database Stats:", String.format("```asciidoc\n%s```", StringFormatting.asciidocStyle(dataStats)), true);
        builder.addField("What's New on Beta?", String.format("```%s```", StringFormatting.fieldSafe(CodeDifferenceHandler.getDifferences())), true);

        builder.setFooter("Database Last Updated");
        builder.setDescription("The database is updated automatically every 24h.");
        builder.setTimestamp(Instant.ofEpochMilli(ExternalFileHandler.DB.lastModified()));
        event.getChannel().sendMessage(builder.build()).queue();

    }
}
