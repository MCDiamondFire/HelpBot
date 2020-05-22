package com.owen1212055.helpbot.command.commands.stats;

import com.owen1212055.helpbot.command.arguments.Argument;
import com.owen1212055.helpbot.command.arguments.NoArg;
import com.owen1212055.helpbot.command.commands.Command;
import com.owen1212055.helpbot.command.permissions.Permission;
import com.owen1212055.helpbot.events.CommandEvent;
import com.owen1212055.helpbot.util.ConnectionGiver;
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
    public Argument getArgument() {
        return new NoArg();
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
        try (Connection connection = ConnectionGiver.getConnection();) {


            try (PreparedStatement fetchPlayers = connection.prepareStatement("SELECT players.name, players.uuid FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.support >= 1 AND ranks.moderation = 0");
                 ResultSet resultSet = fetchPlayers.executeQuery()) {
                while (resultSet.next()) {
                    players.put(resultSet.getString("uuid"), resultSet.getString("name"));
                }

            }

            try (PreparedStatement fetchPlayers = connection.prepareStatement("SELECT DISTINCT uuid FROM player_join_log WHERE time > CURRENT_TIMESTAMP - INTERVAL 30 DAY ");
                 ResultSet resultSet = fetchPlayers.executeQuery()) {
                while (resultSet.next()) {
                    good.add(resultSet.getString(1));
                }

            }

        } catch (SQLException ignored) { }

        players.keySet().removeAll(good);

        builder.setTitle("Staff who have not joined in 30 days:");
        builder.setColor(Color.RED);
        builder.setDescription(String.join("\n", players.values().toArray(new String[0])));

        event.getChannel().sendMessage(builder.build()).queue();

    }
}
