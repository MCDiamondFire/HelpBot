package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.components.dfranks.Ranks;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;


public class ProfileCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "profile";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"user", "whois", "p", "prof"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets information on a certain player.")
                .category(CommandCategory.STATS)
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
        EmbedBuilder builder = new EmbedBuilder();
        new SingleQueryBuilder()
                .query("SELECT * FROM players WHERE players.name = ? OR players.uuid = ? LIMIT 1;", (statement) -> {
                    statement.setString(1, player);
                    statement.setString(2, player);
                })
                .onQuery(table -> {
                    String playerName = table.getString("name");
                    String playerUUID = table.getString("uuid");
                    String whois = table.getString("whois");

                    builder.setAuthor(playerName, null, "https://mc-heads.net/head/" + playerUUID);
                    builder.addField("Name", StringUtil.display(playerName), false);
                    builder.addField("UUID", playerUUID, false);
                    builder.addField("Whois", StringUtil.display(whois.isEmpty() ? "N/A" : whois).replace("\\n", "\n"), false);

                    new SingleQueryBuilder()
                            .query("SELECT * FROM ranks WHERE uuid = ? LIMIT 1;", (statement) -> {
                                statement.setString(1, playerUUID);
                            })
                            .onQuery((resultTablePlot) -> {
                                Ranks[] ranks = Ranks.getAllRanks(
                                        resultTablePlot.getInt("donor"),
                                        resultTablePlot.getInt("support"),
                                        resultTablePlot.getInt("moderation"),
                                        resultTablePlot.getInt("retirement"),
                                        resultTablePlot.getInt("youtuber"),
                                        resultTablePlot.getInt("developer"),
                                        resultTablePlot.getInt("builder")
                                );
                                List<String> ranksList = new ArrayList<>();
                                for (Ranks rank : ranks) {
                                    if (rank == null) continue;
                                    ranksList.add(String.format("[%s]", rank.getRankName()));
                                }

                                builder.addField("Ranks", String.join(" ", ranksList), false);
                            }).execute();

                    new SingleQueryBuilder()
                            .query("SELECT date FROM litebans.history WHERE uuid = ? ORDER BY date ASC LIMIT 1", (statement) -> {
                                statement.setString(1, playerUUID);
                            })
                            .onQuery((resultTable) -> {
                                builder.addField("Join Date", StringUtil.formatDate(resultTable.getDate("date")), false);
                            }).onNotFound(() -> {
                        builder.addField("Join Date", "Not Found", false);
                    }).execute();

                })
                .onNotFound(() -> builder.addField("Error!", "Player was not found", false)).execute();
        event.getChannel().sendMessage(builder.build()).queue();
    }

}


