package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.ranks.*;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;
import java.util.*;

public class ExcusedStaffCommand extends Command {
    
    @Override
    public String getName() {
        return "excused";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Provides a list of all excused support members.")
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
                        new InformativeReply(InformativeReplyType.INFO, "Currently Excused Staff", null)
                );
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT excused_staff.uuid," +
                        "       p.name," +
                        "       donor," +
                        "       support," +
                        "       moderation," +
                        "       retirement," +
                        "       youtuber," +
                        "       administration," +
                        "       excused_by," +
                        "       excused_till," +
                        "       reason " +
                        "FROM owen.excused_staff " +
                        "LEFT JOIN ranks r ON excused_staff.uuid = r.uuid " +
                        "LEFT JOIN players p ON excused_staff.uuid = p.uuid " +
                        "WHERE (support > 0 || moderation > 0) AND (!handled) " +
                        "ORDER BY excused_till DESC;"))
                .compile()
                .run((result) -> {
                    // Select unique names.
                    List<String> names = new ArrayList<>();
                    
                    EmbedBuilder embed = builder.getEmbed();
                    for (ResultSet set : result) {
                        Date date = set.getDate("excused_till");
                        String reason = set.getString("reason");
                        String excused_by = set.getString("excused_by");
                        Rank rank = RankUtil.getHighRank(set);
                        String name = set.getString("name");
                        
                        if (names.contains(name)) {
                            continue;
                        } else {
                            names.add(name);
                        }
                        
                        embed.addField(rank.getRankEmote().getFormatted() + " " + name, String.format("Until: ``%s``\nReason: %s (<@%s>)", FormatUtil.formatDate(date), reason, excused_by), false);
                    }
                    
                    event.reply(builder);
                });
        
    }
}
