package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.ranks.*;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.tasks.impl.SupportUnexcuseTask;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;
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
                        new InformativeReply(InformativeReplyType.INFO, "Profile", null)
                );
        EmbedBuilder embed = preset.getEmbed();

        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM players WHERE players.name = ? OR players.uuid = ? LIMIT 1;", (statement) -> {
                    statement.setString(1, player);
                    statement.setString(2, player);
                }))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player was not found."));
                        return;
                    }

                    ResultSet set = result.getResult();
                    String playerName = set.getString("name");
                    String playerUUID = set.getString("uuid");
                    String whois = set.getString("whois");

                    preset.withPreset(new MinecraftUserPreset(playerName, playerUUID));
                    embed.addField("Name", StringUtil.display(playerName), false);
                    embed.addField("UUID", playerUUID, false);
                    embed.addField("Whois", StringUtil.display(whois.isEmpty() ? "N/A" : whois).replace("\\n", "\n"), false);

                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT * FROM ranks WHERE uuid = ? LIMIT 1;", (statement) -> statement.setString(1, playerUUID)))
                            .compile()
                            .run((resultRanks) -> {
                                ResultSet setRanks = resultRanks.getResult();
                                Rank[] ranks = RankUtil.getRanks(setRanks);
                                List<String> ranksList = new ArrayList<>();
                                for (Rank rank : ranks) {
                                    ranksList.add(String.format("[%s]", rank.getRankName()));
                                }

                                embed.addField("Ranks", String.join(" ", ranksList), false);
                            });

                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT COUNT(*) AS count FROM plot_votes WHERE uuid = ?", (statement) -> statement.setString(1, playerUUID)))
                            .compile()
                            .run((resultTable) -> {
                                int count;
                                if (resultTable.isEmpty()) {
                                    count = 0;
                                } else {
                                    count = resultTable.getResult().getInt("count");
                                }

                                embed.addField("Votes Given", FormatUtil.formatNumber(count), false);
                            });

                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT credits FROM player_credits WHERE uuid = ?", (statement) -> statement.setString(1, playerUUID)))
                            .compile()
                            .run((resultTable) -> embed.addField("Credits", FormatUtil.formatNumber(resultTable.getResult().getInt("credits")), false));

                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT date FROM litebans.history WHERE uuid = ? ORDER BY date LIMIT 1", (statement) -> statement.setString(1, playerUUID)))
                            .compile()
                            .run((resultTable) -> {
                                String date;
                                if (resultTable.isEmpty()) {
                                    date = "Not Found";
                                } else {
                                    date = FormatUtil.formatDate(resultTable.getResult().getDate("date"));
                                }

                                embed.addField("Join Date", date, false);
                            });
                });
        event.reply(preset);
    }
}


