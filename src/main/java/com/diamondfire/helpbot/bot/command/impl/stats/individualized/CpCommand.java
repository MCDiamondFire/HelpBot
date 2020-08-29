package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.creator.CreatorLevel;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.sys.graph.graphable.*;
import com.diamondfire.helpbot.sys.graph.impl.ChartGraphBuilder;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;

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
    protected void execute(CommandEvent event, String player) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "CP Info", null)
                );
        EmbedBuilder embed = preset.getEmbed();

        new SingleQueryBuilder()
                .query("SELECT * FROM creator_rankings WHERE uuid = ? OR name = ?;", (statement) -> {
                    statement.setString(1, player);
                    statement.setString(2, player);
                })
                .onQuery(table -> {
                    int points = table.getInt("points");
                    int rank = table.getInt("cur_rank");
                    CreatorLevel level = CreatorLevel.getLevel(rank);
                    CreatorLevel nextLevel = CreatorLevel.getNextLevel(CreatorLevel.getLevel(rank));
                    int nextLevelReq = nextLevel.getRequirementProvider().getRequirement();

                    String formattedName = table.getString("name");
                    String uuid = table.getString("uuid");
                    preset.withPreset(
                            new MinecraftUserPreset(formattedName, uuid)
                    );

                    embed.addField("Current Rank", level.display(true), false);
                    embed.addField("Current Points", genPointMetric(points, uuid), false);

                    new SingleQueryBuilder()
                            .query("SELECT * FROM owen.creator_rankings_log WHERE uuid = ? ORDER BY points DESC LIMIT 1", (statement) -> statement.setString(1, uuid))
                            .onQuery((tableSet) -> embed.addField("Highest Point Count", FormatUtil.formatNumber(tableSet.getInt("points")), false))
                            .execute();

                    new SingleQueryBuilder()
                            .query("SELECT COUNT(*) + 1 AS place FROM creator_rankings WHERE points > ?", (statement) -> statement.setInt(1, points))
                            .onQuery((tableSet) -> embed.addField("Current Leaderboard Place", FormatUtil.formatNumber(tableSet.getInt("place")), false))
                            .execute();

                    if (level != CreatorLevel.DIAMOND) {
                        embed.addField("Next Rank", nextLevel.display(true), true);
                        embed.addField("Next Rank Points", FormatUtil.formatNumber(nextLevelReq) + String.format(" (%s to go)", FormatUtil.formatNumber(nextLevelReq - points)), false);
                    }

                    new SingleQueryBuilder()
                            .query("SELECT DATE_FORMAT(date, '%d-%m') AS time,points FROM owen.creator_rankings_log WHERE uuid = ?;", (statement) -> statement.setString(1, uuid))
                            .onQuery((resultTable) -> {
                                Map<GraphableEntry<?>, Integer> entries = new LinkedHashMap<>();
                                do {
                                    entries.put(new StringEntry(resultTable.getString("time")), resultTable.getInt("points"));
                                } while (resultTable.next());

                                embed.setImage("attachment://graph.png");
                                event.getReplyHandler().replyA(preset)
                                        .addFile(new ChartGraphBuilder()
                                                .setGraphName(player + "'s CP Graph")
                                                .createGraph(entries), "graph.png")
                                        .queue();
                            })
                            .onNotFound(() -> {
                                event.reply(preset);
                            }).execute();

                })
                .onNotFound(() -> {
                    embed.clear();
                    preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player was not found."));
                    event.reply(preset);
                }).execute();
    }

    private String genPointMetric(int points, String uuid) {
        StringBuilder text = new StringBuilder(FormatUtil.formatNumber(points));
        new SingleQueryBuilder()
                .query("SELECT * FROM owen.creator_rankings_log WHERE uuid = ? ORDER BY date DESC LIMIT 1",
                        (statement) -> statement.setString(1, uuid))
                .onQuery((table) -> {
                    int oldPoints = table.getInt("points");

                    if (oldPoints > points) {
                        text.insert(0, "<:red_down_arrow:743902462343118858> ");
                        text.append(String.format(" (%s from %s)", FormatUtil.formatNumber(points - oldPoints), FormatUtil.formatDate(table.getDate("date"))));
                    } else if (oldPoints < points) {
                        text.insert(0, "<:green_up_arrow:743902461777018901> ");
                        text.append(String.format(" (+%s from %s)", FormatUtil.formatNumber(points - oldPoints), FormatUtil.formatDate(table.getDate("date"))));
                    }
                }).execute();

        return text.toString();
    }

}


