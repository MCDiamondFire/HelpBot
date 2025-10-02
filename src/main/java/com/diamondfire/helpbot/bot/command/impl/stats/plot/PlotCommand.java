package com.diamondfire.helpbot.bot.command.impl.stats.plot;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.ConnectionProvider;

import java.sql.*;

public class PlotCommand extends AbstractPlotCommand {
    
    @Override
    public String getName() {
        return "plot";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets information on a certain plot.")
                .category(CommandCategory.GENERAL_STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("plot id or handle")
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("id_or_handle",
                        new StringArgument());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public Plot getPlot(final CommandEvent event) {
        try {
            int id = Integer.parseInt(event.getArgument("id_or_handle"));
            try (Connection connection = ConnectionProvider.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM plots WHERE id = ?")) {
                
                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return this.mapResultSetToPlot(resultSet);
                    }
                }
            }
        } catch (NumberFormatException ignored) {
            String handle = event.getArgument("id_or_handle");
            try (Connection connection = ConnectionProvider.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM plots WHERE handle = ?")) {
                
                statement.setString(1, handle);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return this.mapResultSetToPlot(resultSet);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}