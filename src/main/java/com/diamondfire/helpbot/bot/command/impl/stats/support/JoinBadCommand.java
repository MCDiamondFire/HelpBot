package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.ClampedIntegerArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.ResultSet;
import java.util.List;
import java.util.*;

public class JoinBadCommand extends Command {
    
    @Override
    public String getName() {
        return "joinbad";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current staff members who have not joined in a certain number of days.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("days")
                                .optional()
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("days",
                        new SingleArgumentContainer<>(new ClampedIntegerArgument(1, 100)).optional(30));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.SR_HELPER;
    }
    
    @Override
    public void run(CommandEvent event) {
        int num = event.getArgument("days");
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, String.format("Staff who have not joined in %s %s", num, StringUtil.sCheck("day", num)), null)
                );
        
        EmbedBuilder embed = preset.getEmbed();
        embed.setColor(Color.RED);
        embed.setDescription("");
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT DISTINCT p.name, DATEDIFF(CURRENT_TIMESTAMP(), latest) AS day " +
                        "FROM (SELECT players.uuid, name" +
                        "      FROM ranks," +
                        "           players" +
                        "      WHERE ranks.uuid = players.uuid" +
                        "        AND players.uuid NOT IN (SELECT DISTINCT uuid FROM owen.excused_staff WHERE handled = false)" +
                        "        AND ranks.administration = 0" +
                        "        AND ranks.support > 0 | ranks.moderation > 0) p" +
                        "         LEFT JOIN (SELECT uuid, MAX(time) AS latest FROM player_join_log GROUP BY uuid) cn" +
                        "                   ON cn.uuid = p.uuid " +
                        "WHERE DATEDIFF(CURRENT_TIMESTAMP(), latest) >= ? " +
                        "ORDER BY day DESC;", (statement) -> statement.setInt(1, num)))
                .compile()
                .run((result) -> {
                    List<String> staff = new ArrayList<>();
                    for (ResultSet set : result) {
                        staff.add(set.getString("name") + " (" + set.getInt("day") + ")");
                    }
                    
                    EmbedUtil.addFields(embed, staff, "", "", true);
                });
        event.reply(preset);
    }
}
