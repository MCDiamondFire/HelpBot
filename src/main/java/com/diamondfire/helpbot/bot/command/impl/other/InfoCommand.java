package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog.CodeDifferenceHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.*;

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
    public ArgumentSet compileArguments() {
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
        
        LinkedHashMap<String, String> dataStats = new LinkedHashMap<>();
        dataStats.put("CodeBlocks", get(CodeDatabase.getRegistry(CodeDatabase.CODEBLOCKS)));
        dataStats.put("Actions", get(CodeDatabase.getRegistry(CodeDatabase.ACTIONS)));
        dataStats.put("Sounds", get(CodeDatabase.getRegistry(CodeDatabase.SOUNDS)));
        dataStats.put("Particles", get(CodeDatabase.getRegistry(CodeDatabase.PARTICLES)));
        dataStats.put("Potions", get(CodeDatabase.getRegistry(CodeDatabase.POTIONS)));
        dataStats.put("Game Value", get(CodeDatabase.getRegistry(CodeDatabase.GAME_VALUES)));
        dataStats.put("Legacy Actions", get(CodeDatabase.getRegistry(CodeDatabase.DEPRECATED_ACTIONS)));
        dataStats.put("Legacy Game Values", get(CodeDatabase.getRegistry(CodeDatabase.DEPRECATED_GAME_VALUES)));
        
        embed.addField("Current Database Stats:", String.format("```asciidoc\n%s```", StringUtil.asciidocStyle(dataStats)), true);
        embed.addField("What's New on Beta?", String.format("```%s```", EmbedUtil.fieldSafe(CodeDifferenceHandler.getDifferences())), true);
        embed.setFooter("Database Last Updated");
        embed.setDescription("The database is updated automatically every 24h.");
        embed.setTimestamp(Instant.ofEpochMilli(ExternalFiles.DB.lastModified()));
        
        event.reply(preset);
    }
    
    private String get(Collection<?> collection) {
        return FormatUtil.formatNumber(collection.size());
    }
}
