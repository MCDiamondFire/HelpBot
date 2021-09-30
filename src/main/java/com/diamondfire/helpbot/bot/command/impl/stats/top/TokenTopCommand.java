package com.diamondfire.helpbot.bot.command.impl.stats.top;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.commands.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;

public class TokenTopCommand extends Command {
    
    @Override
    public String getName() {
        return "tokentop";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"credittop", "creditleaderboard"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the current token leaderboard.")
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
                        new InformativeReply(InformativeReplyType.INFO, "Token Leaderboard", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT name, tokens FROM players,user_tokens WHERE players.uuid = user_tokens.uuid ORDER BY tokens DESC LIMIT 10"))
                .compile()
                .run((resultTable) -> {
                    for (ResultSet set : resultTable) {
                        embed.addField(StringUtil.display(set.getString("name")),
                                "Tokens: " + FormatUtil.formatNumber(set.getInt("tokens")), false);
                    }
                });
        
        event.reply(preset);
    }
    
}


