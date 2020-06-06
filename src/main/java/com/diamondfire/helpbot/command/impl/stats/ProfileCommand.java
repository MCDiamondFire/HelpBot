package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.value.StringArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ProfileCommand extends Command {

    @Override
    public String getName() {
        return "profile";
    }

    @Override
    public String getDescription() {
        return "Get info on a certain player.";
    }

    @Override
    public ValueArgument<String> getArgument() {
        return new StringArg("Player Name/UUID", false);
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        String nameToSelect = getArgument().getArg(event.getParsedArgs());

        if (event.getParsedArgs().isEmpty()) {
            nameToSelect = event.getMember().getEffectiveName();
        }

        final String name = nameToSelect;

        new SingleQueryBuilder()
                .query("SELECT * FROM players WHERE players.name = ? OR players.uuid = ? LIMIT 1;", (statement) -> {
                    statement.setString(1, name);
                    statement.setString(2, name);
                })
                .onQuery(table -> {
                    final String playerName = table.getString("name");
                    final String playerUUID = table.getString("uuid");

                    builder.addField("Name", playerName, false);
                    builder.addField("UUID", playerUUID, false);

                    String whois = table.getString("whois");
                    builder.addField("Whois", StringUtil.stripColorCodes(whois.isEmpty() ?  "N/A" : whois).replace("\\n", "\n"), false);

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

//                    new SingleQueryBuilder()
//                            .query("SELECT time FROM player_join_log WHERE uuid = ? ORDER BY time DESC LIMIT 1;", (statement) -> {
//                                statement.setString(1, playerUUID);
//                            })
//                            .onQuery((resultTable) -> {
//                                builder.addField("Last Seen", formatDate(resultTable.getDate("time")), false);
//                            }).execute();


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
                                                builder.addField("Date Joined", "~" + formatDate(plotDate), false);
                                            } else {
                                                builder.addField("Date Joined", formatDate(joinDate), false);
                                            }
                                        })
                                        .onNotFound(() -> {
                                            builder.addField("Date Joined", formatDate(joinDate), false);
                                        }).execute();
                            }).execute();


                    builder.setAuthor(playerName, null, "https://mc-heads.net/head/" + playerUUID);
                })
                .onNotFound(() -> {
                    builder.addField("Error!", "Player was not found", false);
                }).execute();

//        try (Connection connection = ConnectionGiver.getConnection();
//             PreparedStatement lastSeenStatement = connection.prepareStatement("SELECT time FROM player_join_log WHERE player_join_log.uuid = ? LIMIT 1;");
//             PreparedStatement lastPlotStatement = connection.prepareStatement("SELECT MIN(time) AS time FROM plot_votes AS plot_vote_time WHERE uuid = ? LIMIT 1;")) {
//            lastSeenStatement.setString(1, playerUUID.get());
//            lastPlotStatement.setString(1, playerUUID.get());
//
//            ResultSet joinDate = lastSeenStatement.executeQuery();
//            ResultSet plotVote = lastPlotStatement.executeQuery();
//
//            String lastSeen = null;
//            Date earliestDate = null;
//            if (joinDate.next()) {
//                earliestDate = joinDate.getDate("time");
//                lastSeen = formatDate(earliestDate);
//            } else {
//                lastSeen = "Has not joined server!";
//            }
//            if (plotVote.next()) {
//                Date plotDate = new Date(plotVote.getLong("time"));
//                if (plotDate.toLocalDate().isBefore(earliestDate.toLocalDate())) {
//                    lastSeen = "~" + formatDate(plotDate);
//                }
//
//
//            }
//
//            builder.addField("First Joined", lastSeen, false);
//            joinDate.close();
//            plotVote.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        try (Connection connection = ConnectionGiver.getConnection();
//             PreparedStatement lastSeenStatement = connection.prepareStatement("SELECT time FROM player_join_log WHERE uuid = ? ORDER BY time DESC LIMIT 1;")) {
//            lastSeenStatement.setString(1, "3f7edf24-af79-4197-af58-fc31e4d96e41");
//
//            ResultSet set = lastSeenStatement.executeQuery();
//
//            String lastSeen = null;
//            if (set.next()) {
//                lastSeen = formatDate(set.getDate("time"));
//            } else {
//                lastSeen = "Has not joined server!";
//            }
//            builder.addField("Last Seen", lastSeen, false);
//            set.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }


        event.getChannel().sendMessage(builder.build()).queue();


    }


    public String format(long millis) {
        StringBuilder builder = new StringBuilder();

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        if (hours > 0) {
            builder.append(hours + "h");
        }

        long mins = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        if (mins > 0) {
            builder.append(" " + mins + "m");
        }
        long secs = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        if (secs != 0) {
            builder.append(" " + secs + "s");
        } else {
            builder.append("0." + TimeUnit.MILLISECONDS.toMillis(millis) + "s");
        }

        return builder.toString();

    }

    @SuppressWarnings("deprecation")
    public String formatDate(Date date) {
        return date.toLocalDate().getDayOfMonth() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() + 1900);
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

