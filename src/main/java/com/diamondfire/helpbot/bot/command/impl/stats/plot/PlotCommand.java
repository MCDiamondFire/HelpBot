package com.diamondfire.helpbot.bot.command.impl.stats.plot;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.impl.IntegerArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
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
                                .name("plot id")
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("id",
                        new IntegerArgument());
    }
    
    @Override
    public Rank getRank() {
        return Rank.USER;
    }
    
    @Override
    public ResultSet getPlot(CommandEvent event) {
        try {
            Connection connection = ConnectionProvider.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM plots WHERE id = ?");
            statement.setInt(1, event.getArgument("id"));
            
            return statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}