package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.value.LimitedIntegerArg;

import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
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
    public ValueArgument<Integer> getArgument() {
        return new LimitedIntegerArg("Session Count", 5,Integer.MAX_VALUE, 0);
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
        int num = getArgument().getArg(event.getParsedArgs());

        try (Connection connection = ConnectionGiver.getConnection();) {

            //Gives all staff members
            try (PreparedStatement fetchPlayers = connection.prepareStatement("SELECT players.name FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.support >= 1 AND ranks.moderation = 0");
                 ResultSet resultSet = fetchPlayers.executeQuery()) {
                while (resultSet.next()) {
                    players.add(resultSet.getString(1));
                }

            }

            //Gives people who did sessions
            PreparedStatement fetchPlayers = connection.prepareStatement("SELECT DISTINCT staff FROM support_sessions WHERE time > CURRENT_TIMESTAMP - INTERVAL 30 DAY GROUP BY staff HAVING COUNT(staff) >= ?");
            fetchPlayers.setInt(1,num);

            try (ResultSet resultSet = fetchPlayers.executeQuery()) {
                while (resultSet.next()) {
                    good.add(resultSet.getString(1));
                }

                fetchPlayers.close();

            }

        } catch (SQLException ignored) {}

        players.removeAll(good);

        builder.setTitle(String.format("People with less than %s sessions:", num));
        builder.setColor(Color.RED);
        builder.setDescription(String.join("\n", players.toArray(new String[0])));
        event.getChannel().sendMessage(builder.build()).queue();

    }
}
