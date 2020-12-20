package com.diamondfire.helpbot.bot.command.impl.stats.support;

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

import java.sql.ResultSet;

public class SupporteeStatsCommand extends AbstractPlayerUUIDCommand {
    
    @Override
    public String getName() {
        return "supporteestats";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"sesstats", "sessionstats"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets a users stats in which they were helped by a support member. Name changes are not counted, so use ?names to find previous names. Please note, these are sessions the user has been in with a staff member.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("player|uuid")
                                .optional()
                );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }
    
    @Override
    protected void execute(CommandEvent event, String player) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Session Stats", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT COUNT(*) AS count," +
                        "SUM(duration) AS total_duration," +
                        "MIN(time) AS earliest_time," +
                        "MAX(time) AS latest_time," +
                        "COUNT(DISTINCT staff) AS unique_helped, name FROM support_sessions WHERE name = ?;", (statement) -> statement.setString(1, player)))
                .compile()
                .run((result) -> {
                    ResultSet set = result.getResult();
                    if (set.getInt("count") == 0) {
                        embed.clear();
                        preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player has not been in a session!"));
                        return;
                    }
                    String formattedName = set.getString("name");
                    preset.withPreset(
                            new MinecraftUserPreset(formattedName)
                    );
                    
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT COUNT(*) AS count FROM support_sessions " +
                                    "WHERE name = ? AND time > CURRENT_TIMESTAMP() - INTERVAL 30 DAY;", (statement) -> statement.setString(1, player)))
                            .compile()
                            .run((resultBadTable) -> embed.addField("Sessions this month:", resultBadTable.getResult().getInt("count") + "", true));
                    
                    embed.addField("Total Sessions", set.getInt("count") + "", true);
                    embed.addField("Unique Support Members", set.getInt("unique_helped") + "", true);
                    embed.addField("Total Session Time", FormatUtil.formatMilliTime(set.getLong("total_duration")), true);
                    embed.addField("Earliest Session", FormatUtil.formatDate(set.getDate("earliest_time")), true);
                    embed.addField("Latest Session", FormatUtil.formatDate(set.getDate("latest_time")), true);
                    
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT AVG(duration) AS average_duration," +
                                    "MIN(duration) AS shortest_duration," +
                                    "MAX(duration) AS longest_duration " +
                                    "FROM support_sessions WHERE duration != 0 AND name = ?;", (statement) -> statement.setString(1, player)))
                            .compile()
                            .run((timeResult) -> {
                                ResultSet timeSet = timeResult.getResult();
                                embed.addField("Average Session Time", FormatUtil.formatMilliTime(timeSet.getLong("average_duration")), true);
                                embed.addField("Shortest Session Time", FormatUtil.formatMilliTime(timeSet.getLong("shortest_duration")), true);
                                embed.addField("Longest Session Time", FormatUtil.formatMilliTime(timeSet.getLong("longest_duration")), true);
                            });
                    
                });
        
        event.reply(preset);
    }
    
    
}
