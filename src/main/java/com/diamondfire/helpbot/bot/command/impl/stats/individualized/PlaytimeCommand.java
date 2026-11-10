package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.Player;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.*;

import java.sql.ResultSet;
import java.util.*;

public class PlaytimeCommand extends AbstractPlayerUUIDCommand {

    // How many leaderboard entries to show above and below the player.
    private static final int SURROUNDING = 3;

    @Override
    public String getName() {
        return "playtime";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pt"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets a player's network-wide playtime and their position on the playtime leaderboard.")
                .category(CommandCategory.PLAYER_STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("player|uuid")
                                .optional()
                );
    }

    @Override
    protected void execute(CommandEvent event, Player player) {
        String uuid = player.uuidString();

        // Leaderboard order is playtime descending, ties broken by uuid, so ranks are stable.
        long[] stats = new long[3];
        new DatabaseQuery()
                .query(new BasicQuery("SELECT me.play_time_ms, " +
                        "       (SELECT UNIX_TIMESTAMP(join_date) FROM players WHERE players.uuid = me.uuid) AS joined," +
                        "       (SELECT COUNT(*) FROM player_playtime other" +
                        "         WHERE other.play_time_ms > me.play_time_ms" +
                        "            OR (other.play_time_ms = me.play_time_ms AND other.uuid < me.uuid)) + 1 AS position " +
                        "FROM player_playtime me WHERE me.uuid = ?", (statement) -> statement.setString(1, uuid)))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        return;
                    }

                    ResultSet set = result.getResult();
                    stats[0] = set.getLong("play_time_ms");
                    stats[1] = set.getLong("joined");
                    stats[2] = set.getLong("position");
                });

        long playtime = stats[0];
        long joined = stats[1];
        long position = stats[2];
        if (position == 0 || playtime == 0) {
            event.reply(new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, "This player has no recorded playtime!")
                    ));
            return;
        }

        long startPosition = Math.max(1, position - SURROUNDING);
        List<String> lines = new ArrayList<>();
        new DatabaseQuery()
                // Sort the playtime table alone and only look up names for the rows shown; joining first is far slower.
                .query(new BasicQuery("SELECT w.uuid, players.name, w.play_time_ms " +
                        "FROM (SELECT uuid, play_time_ms FROM player_playtime ORDER BY play_time_ms DESC, uuid LIMIT ? OFFSET ?) w " +
                        "LEFT JOIN players ON players.uuid = w.uuid " +
                        "ORDER BY w.play_time_ms DESC, w.uuid", (statement) -> {
                    statement.setInt(1, SURROUNDING * 2 + 1);
                    statement.setLong(2, startPosition - 1);
                }))
                .compile()
                .run((result) -> {
                    long rank = startPosition;
                    for (ResultSet set : result) {
                        String name = set.getString("name");
                        String line = String.format("`#%s` %s - %s",
                                FormatUtil.formatNumber(rank),
                                name == null ? "Unknown Player" : StringUtil.escapeName(name),
                                FormatUtil.formatMilliTime(set.getLong("play_time_ms")));

                        lines.add(set.getString("uuid").equals(uuid) ? "**" + line + "**" : line);
                        rank++;
                    }
                });

        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Playtime", null),
                        new MinecraftUserPreset(player)
                );
        preset.getEmbed()
                .addField("Playtime", FormatUtil.formatMilliTime(playtime), true)
                .addField("Position", "#" + FormatUtil.formatNumber(position), true);

        // Playtime divided by the time since the player first joined DF.
        long sinceJoin = System.currentTimeMillis() - joined * 1000;
        if (joined > 0 && sinceJoin > 0) {
            double ratio = (double) playtime / sinceJoin;
            preset.getEmbed().addField("Since Joining",
                    String.format("%.2f%% (%.1fh/day)", ratio * 100, ratio * 24), true);
        }

        preset.getEmbed().addField("Leaderboard", String.join("\n", lines), false);
        event.reply(preset);
    }

}
