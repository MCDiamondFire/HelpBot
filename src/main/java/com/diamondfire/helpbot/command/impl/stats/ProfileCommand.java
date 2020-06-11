package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.value.StringArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ProfileCommand extends AbstractPlayerCommand {

    @Override
    public String getName() {
        return "profile";
    }

    @Override
    public String getDescription() {
        return "Get info on a certain player.";
    }

    public ValueArgument<String> getArgument() {
        return new StringArg("Player Name/UUID", false);
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
                    final String playerName = table.getString("name");
                    final String playerUUID = table.getString("uuid");

                    builder.addField("Name", playerName, false);
                    builder.addField("UUID", playerUUID, false);

                    String whois = table.getString("whois");
                    builder.addField("Whois", StringUtil.stripColorCodes(whois.isEmpty() ? "N/A" : whois).replace("\\n", "\n"), false);

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
                                        resultTablePlot.getInt("youtuber"));

                                StringBuilder stringBuilder = new StringBuilder();
                                for (Ranks rank : ranks) {
                                    if (rank == null) {
                                        continue;
                                    }
                                    stringBuilder.append(String.format("[%s]", rank.getRankName()) + " ");

                                }
                                builder.addField("Ranks", stringBuilder.toString(), false);
                            }).execute();

                    new SingleQueryBuilder()
                            .query("SELECT time FROM player_join_log WHERE uuid = ? ORDER BY time LIMIT 1;", (statement) -> {
                                statement.setString(1, playerUUID);
                            })
                            .onQuery((resultTable) -> {
                                Date joinDate = resultTable.getDate("time");
                                new SingleQueryBuilder()
                                        .query("SELECT time FROM plot_votes WHERE uuid = ? ORDER BY time LIMIT 1;", (statement) -> {
                                            statement.setString(1, playerUUID);
                                        })
                                        .onQuery((resultTablePlot) -> {
                                            Date plotDate = new Date(resultTablePlot.getLong("time"));
                                            if (plotDate.toLocalDate().isBefore(joinDate.toLocalDate())) {
                                                builder.addField("Date Joined", "~" + StringUtil.formatDate(plotDate), false);
                                            } else {
                                                builder.addField("Date Joined", StringUtil.formatDate(joinDate), false);
                                            }
                                        })
                                        .onNotFound(() -> {
                                            builder.addField("Date Joined", StringUtil.formatDate(joinDate), false);
                                        }).execute();
                            }).execute();


                    builder.setAuthor(playerName, null, "https://mc-heads.net/head/" + playerUUID);
                })
                .onNotFound(() -> {
                    builder.addField("Error!", "Player was not found", false);
                }).execute();

        event.getChannel().sendMessage(builder.build()).queue();
    }

}

enum RankCategories {
    DONOR,
    SUPPORT,
    MODERATION,
    RETIREMENT,
    YOUTUBER
}

enum Ranks {
    // Ranks
    NOBLE("Noble", 1, RankCategories.DONOR),
    EMPEROR("Emperor", 2, RankCategories.DONOR),
    MYTHIC("Mythic", 3, RankCategories.DONOR),
    OVERLORD("Overlord", 4, RankCategories.DONOR),
    // Support
    JRHELPER("JrHelper", 1, RankCategories.SUPPORT),
    HELPER("Helper", 2, RankCategories.SUPPORT),
    EXPERT("Expert", 3, RankCategories.SUPPORT),
    // Moderation
    JRMOD("JrMod", 1, RankCategories.MODERATION),
    MOD("Mod", 2, RankCategories.MODERATION),
    //Administration
    ADMIN("Admin", 3, RankCategories.MODERATION),
    OWNER("Owner", 4, RankCategories.MODERATION),
    //Retirement
    RETIRED("Retired", 1, RankCategories.RETIREMENT),
    EMERITUS("Emeritus", 2, RankCategories.RETIREMENT),
    //Youtuber
    YOUTUBER("YT", 1, RankCategories.YOUTUBER);

    private static final HashMap<RankCategories, HashMap<Integer, Ranks>> RANK_LIST = new HashMap<>();

    static {
        for (Ranks tag : values()) {
            HashMap<Integer, Ranks> hash = RANK_LIST.get(tag.category);
            if (hash != null) {
                hash.put(tag.number, tag);
            } else {
                HashMap<Integer, Ranks> ranksHashMap = new HashMap<>();
                ranksHashMap.put(tag.number, tag);
                RANK_LIST.put(tag.category, ranksHashMap);
            }

        }
    }

    private String rankName;
    private int number;
    private RankCategories category;

    Ranks(String rankName, int number, RankCategories category) {
        this.rankName = rankName;
        this.number = number;
        this.category = category;
    }

    public static Ranks getRank(RankCategories category, int number) {
        return RANK_LIST.get(category).get(number);
    }

    public String getRankName() {
        return rankName;
    }

    public static Ranks[] getAllRanks(int donor, int support, int moderation, int retirement, int yt) {
        ArrayList<Ranks> ranks = new ArrayList<>();
        ranks.add(getRank(RankCategories.DONOR, donor));
        ranks.add(getRank(RankCategories.SUPPORT, support));
        ranks.add(getRank(RankCategories.MODERATION, moderation));
        ranks.add(getRank(RankCategories.RETIREMENT, retirement));
        ranks.add(getRank(RankCategories.YOUTUBER, yt));

        return ranks.toArray(new Ranks[0]);
    }


}

