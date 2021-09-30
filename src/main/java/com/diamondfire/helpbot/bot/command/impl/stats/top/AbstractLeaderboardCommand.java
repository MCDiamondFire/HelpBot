package com.diamondfire.helpbot.bot.command.impl.stats.top;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.commands.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;


// This class will basically be used anywhere where leaderboards are used. I just haven't gotten to finishing this yet...
public abstract class AbstractLeaderboardCommand extends Command {
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("offset",
                        new SingleArgumentContainer<>(new StringArgument()).optional(null));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder();
        builder.withPreset(new InformativeReply(InformativeReplyType.INFO, getLeaderboardName(), null));
        EmbedBuilder embed = builder.getEmbed();
        
        List<LeaderboardEntry> entries = getEntries(event);
        
        for (LeaderboardEntry entry : entries) {
            embed.addField(entry.getEntry(), entry.getValue(), false);
        }
    }
    
    protected abstract void execute(CommandEvent event, String player);
    
    protected abstract List<LeaderboardEntry> getEntries(CommandEvent event);
    
    protected abstract String getLeaderboardName();
    
}

class LeaderboardEntry {
    
    private final String entry;
    private final String value;
    
    public LeaderboardEntry(String entry, String value) {
        this.entry = entry;
        this.value = value;
    }
    
    public String getEntry() {
        return entry;
    }
    
    public String getValue() {
        return value;
    }
}


