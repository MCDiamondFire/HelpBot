package com.diamondfire.helpbot.bot.command.impl.stats;

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

public class ActivePlotsCommand extends Command {
    
    @Override
    public String getName() {
        return "activeplots";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"nowplaying"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current plots that people are playing.")
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
                        new InformativeReply(InformativeReplyType.INFO, "Active Plots", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM plots WHERE player_count > 0 AND whitelist = 0 ORDER BY player_count DESC LIMIT 10"))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        embed.addField(StringUtil.display(set.getString("name")) +
                                        String.format(" **(%s)**", set.getInt("id")),
                                "Players: " + set.getInt("player_count"), false);
                    }
                    
                    event.reply(preset);
                });
        
    }
    
}


