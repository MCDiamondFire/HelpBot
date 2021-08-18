package com.diamondfire.helpbot.bot.command.impl.stats.top;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;
import java.util.ArrayList;

public class TrendingPlotsCommand extends Command {
    
    @Override
    public String getName() {
        return "trending";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current trending plots.")
                .category(CommandCategory.GENERAL_STATS);
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
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Trending Plots", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM plots WHERE whitelist = 0 ORDER BY votes DESC LIMIT 10"))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        int count = set.getInt("player_count");
                        ArrayList<String> stats = new ArrayList<>();
                        stats.add("Votes: " + set.getInt("votes"));
                        
                        if (count > 0) {
                            stats.add("Players: " + count);
                        }
                        
                        embed.addField(StringUtil.display(set.getString("name")) +
                                        String.format(" **(%s)**", set.getInt("id")),
                                String.join("\n", stats), false);
                    }
                });
        
        event.reply(preset);
    }
    
}


