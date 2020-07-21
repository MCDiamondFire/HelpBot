package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog.CodeDifferenceHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.sys.externalfile.ExternalFile;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.LinkedHashMap;

public class InfoCommand extends Command {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets info on the current active code database.")
                .category(CommandCategory.CODE_BLOCK);
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
        PresetBuilder preset = new PresetBuilder();
        EmbedBuilder embed = preset.getEmbed();

        LinkedHashMap<String, Integer> dataStats = new LinkedHashMap<>();
        dataStats.put("CodeBlocks", CodeDatabase.getCodeBlocks().size());
        dataStats.put("Actions", CodeDatabase.getActions().size());
        dataStats.put("Sounds", CodeDatabase.getSounds().size());
        dataStats.put("Particles", CodeDatabase.getParticles().size());
        dataStats.put("Potions", CodeDatabase.getPotions().size());
        dataStats.put("Game Value", CodeDatabase.getGameValues().size());
        dataStats.put("Legacy Actions", CodeDatabase.getDeprecatedActions().size());
        dataStats.put("Legacy Game Values", CodeDatabase.getDeprecatedGameValues().size());

        embed.addField("Current Database Stats:", String.format("```asciidoc\n%s```", StringUtil.asciidocStyle(dataStats)), true);
        embed.addField("What's New on Beta?", String.format("```%s```", StringUtil.fieldSafe(CodeDifferenceHandler.getDifferences())), true);
        embed.setFooter("Database Last Updated");
        embed.setDescription("The database is updated automatically every 24h.");
        embed.setTimestamp(Instant.ofEpochMilli(ExternalFile.DB.getFile().lastModified()));

        event.reply(preset);
    }
}
