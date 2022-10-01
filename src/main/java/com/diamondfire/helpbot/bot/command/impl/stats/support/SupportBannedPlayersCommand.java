package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;
import java.util.*;

public class SupportBannedPlayersCommand extends Command {
    
    @Override
    public String getName() {
        return "supportbanned";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Provides a list of all support banned players.")
                .category(CommandCategory.SUPPORT);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.SR_HELPER;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Currently Support Banned Players", null)
                );
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT support_bans.uuid, p.name, support_bans.reason FROM support_bans" +
                        "    LEFT JOIN players p ON support_bans.uuid = p.uuid"))
                .compile()
                .run((result) -> {
                    // Select unique names.
                    List<String> names = new ArrayList<>();
                    
                    EmbedBuilder embed = builder.getEmbed();
                    for (ResultSet set : result) {
                        String reason = set.getString("reason");
                        String name = set.getString("name");
                        
                        if (names.contains(name)) {
                            continue;
                        } else {
                            names.add(name);
                        }
                        
                        embed.addField(name, String.format("Reason: %s", reason), false);
                    }
                    
                    event.reply(builder);
                });
    }
}
