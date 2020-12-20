package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.*;

public class LastJoinedCommand extends AbstractPlayerUUIDCommand {
    
    @Override
    public String getName() {
        return "lastjoined";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"lastseen", "seen", "lastonline", "lastjoin"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the last date when a user joined.")
                .category(CommandCategory.PLAYER_STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("player|uuid")
                                .optional()
                );
    }
    
    @Override
    protected void execute(CommandEvent event, String player) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new MinecraftUserPreset(player),
                        new InformativeReply(InformativeReplyType.INFO, null, null)
                );
        EmbedBuilder embed = preset.getEmbed();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT players.uuid,players.name FROM players WHERE players.uuid = ? OR players.name = ?;", (statement) -> {
                    statement.setString(1, player);
                    statement.setString(2, player);
                }))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        preset.getEmbed().clear();
                        preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player not found!"));
                        return;
                    }
                    
                    ResultSet set = result.getResult();
                    String formattedName = set.getString("name");
                    preset.withPreset(
                            new MinecraftUserPreset(formattedName)
                    );
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT TIMESTAMPADD(hour, 5, time) AS time FROM hypercube.player_join_log WHERE uuid = ? ORDER BY time DESC LIMIT 1;", (statement) -> statement.setString(1, set.getString("uuid"))))
                            .compile()
                            .run((resultTableDate) -> {
                                if (resultTableDate.isEmpty()) {
                                    embed.addField("Last Seen", "A long time ago...", false);
                                    return;
                                }
                                
                                ResultSet setTime = resultTableDate.getResult();
                                Timestamp date = setTime.getTimestamp("time");
                                if (Permission.EXPERT.hasPermission(event.getMember())) {
                                    embed.setFooter("Last Seen");
                                    embed.setTimestamp(date.toInstant());
                                } else {
                                    embed.addField("Last Seen", FormatUtil.formatDate(date), false);
                                }
                                
                            });
                });
        
        event.reply(preset);
    }
    
}
