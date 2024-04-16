package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.Player;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.creator.CreatorLevel;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.graph.graphable.*;
import com.diamondfire.helpbot.sys.graph.impl.ChartGraphBuilder;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;

import java.sql.ResultSet;
import java.util.*;


public class CpCommand extends AbstractPlayerUUIDCommand {
    
    @Override
    public String getName() {
        return "cp";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"cpi", "cpinfo", "mycp", "cplevel"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets information on a certain player's CP.")
                .category(CommandCategory.PLAYER_STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("player|uuid")
                                .optional()
                );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    protected void execute(CommandEvent event, Player player) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "CP Info", null)
                );
        EmbedBuilder embed = preset.getEmbed();
    
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM creator_rankings WHERE uuid = ?;", (statement) -> {
                    statement.setString(1, player.uuidString());
                }))
                .compile()
                .run((table) -> {
                    if (table.isEmpty()) {
                        embed.clear();
                        preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player was not found."));
                        event.reply(preset);
                        return;
                    }
                
                    ResultSet set = table.getResult();
                
                    int points = set.getInt("points");
                    int rank = set.getInt("cur_rank");
                    CreatorLevel level = CreatorLevel.getLevel(rank);
                    CreatorLevel nextLevel = CreatorLevel.getNextLevel(CreatorLevel.getLevel(rank));
                    int nextLevelReq = nextLevel.getRequirementProvider().getRequirement();
                
                    String formattedName = set.getString("name");
                    String uuid = set.getString("uuid");
                    preset.withPreset(
                            new MinecraftUserPreset(formattedName, uuid)
                    );
                
                    embed.addField("Current Rank", level.display(true), false);
                    embed.addField("Current Points", genPointMetric(points, uuid), false);
                
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT * FROM owen.creator_rankings_log WHERE uuid = ? ORDER BY points DESC LIMIT 1", (statement) -> statement.setString(1, uuid)))
                            .compile()
                            .run((tableSet) -> {
                            
                                if (!tableSet.isEmpty()) {
                                    int maxPoints = tableSet.getResult().getInt("points");
                                    if (maxPoints > points) {
                                        embed.addField("Highest Point Count", FormatUtil.formatNumber(maxPoints), false);
                                    } else {
                                        embed.addField("Highest Point Count", FormatUtil.formatNumber(points), false);
                                    }
                                }
                            });
                
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT COUNT(*) + 1 AS place FROM creator_rankings WHERE points > ?", (statement) -> statement.setInt(1, points)))
                            .compile()
                            .run((tableSet) -> embed.addField("Current Leaderboard Place", FormatUtil.formatNumber(tableSet.getResult().getInt("place")), false));
                
                    if (level != CreatorLevel.DIAMOND) {
                        embed.addField("Next Rank", nextLevel.display(true), true);
                        embed.addField("Next Rank Points", FormatUtil.formatNumber(nextLevelReq) + String.format(" (%s to go)", FormatUtil.formatNumber(nextLevelReq - points)), false);
                    }
                
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT DATE_FORMAT(date, '%d-%m') AS time,points FROM owen.creator_rankings_log WHERE uuid = ?;", (statement) -> statement.setString(1, uuid)))
                            .compile()
                            .run((resultTable) -> {
                                if (resultTable.isEmpty()) {
                                    event.reply(preset);
                                    return;
                                }
                            
                                Map<GraphableEntry<?>, Integer> entries = new LinkedHashMap<>();
                                for (ResultSet rs : resultTable) {
                                    entries.put(new StringEntry(rs.getString("time")), rs.getInt("points"));
                                }
                            
                                embed.setImage("attachment://graph.png");
                                try {
                                    event.getReplyHandler().replyA(preset)
                                            .addFiles(FileUpload.fromData(new ChartGraphBuilder()
                                                    .setGraphName(player.name() + "'s CP Graph")
                                                    .createGraph(entries), "graph.png"))
                                            .queue();
                                } catch (Exception ignored) {
                                    event.reply(preset);
                                }
                            });
                
                });
    }
    
    private String genPointMetric(int points, String uuid) {
        StringBuilder text = new StringBuilder(FormatUtil.formatNumber(points));
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM owen.creator_rankings_log WHERE uuid = ? ORDER BY date DESC LIMIT 1",
                        (statement) -> statement.setString(1, uuid)))
                .compile()
                .run((table) -> {
                    if (table.isEmpty()) {
                        return;
                    }
                    
                    ResultSet set = table.getResult();
                    int oldPoints = set.getInt("points");
                    
                    if (oldPoints > points) {
                        text.insert(0, "<:red_down_arrow:743902462343118858> ");
                        text.append(String.format(" (%s from %s)", FormatUtil.formatNumber(points - oldPoints), FormatUtil.formatDate(set.getDate("date"))));
                    } else if (oldPoints < points) {
                        text.insert(0, "<:green_up_arrow:743902461777018901> ");
                        text.append(String.format(" (+%s from %s)", FormatUtil.formatNumber(points - oldPoints), FormatUtil.formatDate(set.getDate("date"))));
                    }
                });
        
        return text.toString();
    }
    
}


