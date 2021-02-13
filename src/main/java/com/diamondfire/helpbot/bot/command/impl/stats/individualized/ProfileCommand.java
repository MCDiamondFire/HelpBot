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
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.*;
import java.util.*;


public class ProfileCommand extends AbstractPlayerUUIDCommand {
    
    @Override
    public String getName() {
        return "profile";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"user", "whois", "p", "prof", "credits", "joindate", "tokens"};
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
                .query(new BasicQuery("SELECT * " +
                        "FROM hypercube.ranks," +
                        "     hypercube.players " +
                        "WHERE ranks.uuid = players.uuid" +
                        "  AND (players.uuid = ? || players.name = ?)", (statement) -> {
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
                    String rankString;
                    {
                        Rank highRank = RankUtil.getHighRank(set);
                        if (highRank == null) {
                            rankString = "";
                        } else {
                            rankString = highRank.getRankEmote().getAsMention();
                        }
                    }
                    
                    preset.withPreset(new MinecraftUserPreset(playerName, playerUUID));
                    embed.addField("Name", rankString + " " + StringUtil.display(playerName), false);
                    embed.addField("UUID", playerUUID, false);
                    embed.addField("Whois", StringUtil.display(whois.isEmpty() ? "N/A" : whois).replace("\\n", "\n"), false);
                    
                    Rank[] ranks = RankUtil.getRanks(set);
                    List<String> ranksList = new ArrayList<>();
                    for (Rank rank : ranks) {
                        ranksList.add(String.format("[%s]", rank.getRankName()));
                    }
                    
                    embed.addField("Ranks", String.join(" ", ranksList), false);
                    
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT * FROM (SELECT (SELECT date AS liteban_join FROM litebans.history WHERE uuid = ? ORDER BY date DESC LIMIT 1) temp FROM dual) AS liteban," +
                                    "     (SELECT (SELECT date AS owen_join FROM owen.join_log WHERE uuid = ? ORDER BY date DESC LIMIT 1) temp FROM dual) AS owen_join," +
                                    "     (SELECT (SELECT COUNT(*) AS votes FROM hypercube.plot_votes WHERE uuid = ?) temp FROM dual) AS votes," +
                                    "     (SELECT (SELECT tokens FROM hypercube.user_tokens WHERE uuid = ?) temp FROM dual) AS credits," +
                                    "     (SELECT (SELECT discord_id FROM hypercube.linked_accounts WHERE player_uuid = ?) temp FROM dual) AS id", (statement) -> {
                                statement.setString(1, playerUUID);
                                statement.setString(2, playerUUID);
                                statement.setString(3, playerUUID);
                                statement.setString(4, playerUUID);
                                statement.setString(5, playerUUID);
                            }))
                            .compile()
                            .run((stats) -> {
                                ResultSet statsSet = stats.getResult();
                                
                                embed.addField("Votes Given", FormatUtil.formatNumber(statsSet.getInt("votes.temp")), false);
                                embed.addField("Tokens", FormatUtil.formatNumber(statsSet.getInt("credits.temp")), false);
                                
                                long discordId = statsSet.getLong("id.temp");
                                if (discordId != 0L) {
                                    embed.addField("Discord User", "<@" + discordId + '>', false);
                                } else {
                                    embed.addField("Discord User", "Not Verified", false);
                                }
                                
                                Timestamp liteBanDate = statsSet.getTimestamp("liteban.temp");
                                
                                if (liteBanDate != null) {
                                    Timestamp owenDate = statsSet.getTimestamp("owen_join.temp");
                                    
                                    if (owenDate == null) {
                                        new DatabaseQuery()
                                                .query(new BasicQuery("INSERT INTO owen.join_log (uuid,date) VALUES (?,?)", (statement) -> {
                                                    statement.setString(1, playerUUID);
                                                    statement.setTimestamp(2, liteBanDate);
                                                })).compile();
                                        embed.addField("Join Date", FormatUtil.formatDate(liteBanDate), false);
                                    } else {
                                        embed.addField("Join Date", FormatUtil.formatDate(owenDate), false);
                                    }
                                    
                                } else {
                                    embed.addField("Join Date", "Not Found", false);
                                }
                                
                            });
                });
        
        event.reply(preset);
    }
}


