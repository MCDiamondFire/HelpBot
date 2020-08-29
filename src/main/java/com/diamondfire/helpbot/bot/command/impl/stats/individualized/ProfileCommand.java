package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.ranks.Ranks;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.*;
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

        new SingleQueryBuilder()
                .query("SELECT * FROM players WHERE players.name = ? OR players.uuid = ? LIMIT 1;", (statement) -> {
                    statement.setString(1, player);
                    statement.setString(2, player);
                })
                .onQuery(table -> {
                    String playerName = table.getString("name");
                    String playerUUID = table.getString("uuid");
                    String whois = table.getString("whois");

                    preset.withPreset(new MinecraftUserPreset(playerName, playerUUID));
                    embed.addField("Name", StringUtil.display(playerName), false);
                    embed.addField("UUID", playerUUID, false);
                    embed.addField("Whois", StringUtil.display(whois.isEmpty() ? "N/A" : whois).replace("\\n", "\n"), false);

                    new SingleQueryBuilder()
                            .query("SELECT * FROM ranks WHERE uuid = ? LIMIT 1;", (statement) -> statement.setString(1, playerUUID))
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

                                embed.addField("Ranks", String.join(" ", ranksList), false);
                            }).execute();

                    new SingleQueryBuilder()
                            .query("SELECT COUNT(*) AS count FROM plot_votes WHERE uuid = ?", (statement) -> statement.setString(1, playerUUID))
                            .onQuery((resultTable) -> embed.addField("Votes Given", FormatUtil.formatNumber(resultTable.getInt("count")), false)).onNotFound(() -> embed.addField("Votes Given", "0", false)).execute();

                    new SingleQueryBuilder()
                            .query("SELECT credits FROM player_credits WHERE uuid = ?", (statement) -> statement.setString(1, playerUUID))
                            .onQuery((resultTable) -> embed.addField("Credits", FormatUtil.formatNumber(resultTable.getInt("credits")), false)).execute();

                    new SingleQueryBuilder()
                            .query("SELECT date FROM litebans.history WHERE uuid = ? ORDER BY date LIMIT 1", (statement) -> statement.setString(1, playerUUID))
                            .onQuery((resultTable) -> embed.addField("Join Date", FormatUtil.formatDate(resultTable.getDate("date")), false)).onNotFound(() -> embed.addField("Join Date", "Not Found", false)).execute();

                })
                .onNotFound(() -> preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player was not found."))).execute();
        event.reply(preset);
    }

}


