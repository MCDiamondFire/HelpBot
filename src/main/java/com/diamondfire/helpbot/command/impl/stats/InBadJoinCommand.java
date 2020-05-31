package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.arguments.value.LimitedIntegerArg;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.ConnectionGiver;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class InBadJoinCommand extends Command {

    @Override
    public String getName() {
        return "joinbad";
    }

    @Override
    public String getDescription() {
        return "Gets current staff members who have not joined in 30 days.";
    }

    @Override
    public ValueArgument<Integer> getArgument() {
        return new LimitedIntegerArg("Days",  2, 100, 30);
    }

    @Override
    public Permission getPermission() {
        return Permission.EXPERT;
    }

    @Override
    public void run(CommandEvent event) {

        // I recommend not trying to improve this unless you know what you are doing.
        // I managed to kill the DF database completely by using my awful SQL knowledge.

        EmbedBuilder builder = new EmbedBuilder();
        ArrayList<String> good = new ArrayList<>();
        HashMap<String, String> players = new HashMap<>();
        int num = getArgument().getArg(event.getParsedArgs());

        try (Connection connection = ConnectionGiver.getConnection();) {


            try (PreparedStatement fetchPlayers = connection.prepareStatement("SELECT players.name, players.uuid FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.support >= 1 AND ranks.moderation = 0");
                 ResultSet resultSet = fetchPlayers.executeQuery()) {
                while (resultSet.next()) {
                    players.put(resultSet.getString("uuid"), resultSet.getString("name"));
                }

            }

            PreparedStatement fetchPlayers = connection.prepareStatement("SELECT DISTINCT uuid FROM player_join_log WHERE time > CURRENT_TIMESTAMP - INTERVAL ? DAY ");
            fetchPlayers.setInt(1, num);

            try (ResultSet resultSet = fetchPlayers.executeQuery()) {
                while (resultSet.next()) {
                    good.add(resultSet.getString(1));
                }
                fetchPlayers.close();

            }

        } catch (SQLException ignored) {
        }

        players.keySet().removeAll(good);

        builder.setTitle(String.format("Staff who have not joined in %s days:", num));
        builder.setColor(Color.RED);
        builder.setDescription(String.join("\n", players.values().toArray(new String[0])));

        event.getChannel().sendMessage(builder.build()).queue();

    }
}
