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

public class InBadCommand extends Command {
    @Override
    public String getName() {
        return "bad";
    }

    @Override
    public String getDescription() {
        return "Gets current staff members who have not done a session in 30 days.";
    }

    @Override
    public Argument getArgument() {
        return new NoArg();
    }

    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        ArrayList<String> good = new ArrayList<>();
        ArrayList<String> players = new ArrayList<>();
        try (Connection connection = ConnectionGiver.getConnection();) {

            //Gives all staff members
            try (PreparedStatement fetchPlayers = connection.prepareStatement("SELECT players.name FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.support >= 1 AND ranks.moderation = 0");
                 ResultSet resultSet = fetchPlayers.executeQuery()) {
                while (resultSet.next()) {
                    players.add(resultSet.getString(1));
                }

            }

            //Gives people who did sessions
            try (PreparedStatement fetchPlayers = connection.prepareStatement("SELECT DISTINCT staff FROM support_sessions WHERE time > CURRENT_TIMESTAMP - INTERVAL 30 DAY GROUP BY staff HAVING COUNT(staff) >= 5");
                 ResultSet resultSet = fetchPlayers.executeQuery()) {
                while (resultSet.next()) {
                    good.add(resultSet.getString(1));
                }

            }

        } catch (SQLException ignored) {

        }

        players.removeAll(good);
        builder.setTitle("Current Support Bad:");
        builder.setColor(Color.RED);
        builder.setDescription(String.join("\n", players.toArray(new String[0])));
        event.getChannel().sendMessage(builder.build()).queue();

    }
}
