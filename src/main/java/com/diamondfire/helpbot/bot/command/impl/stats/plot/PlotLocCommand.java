package com.diamondfire.helpbot.bot.command.impl.stats.plot;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.ConnectionProvider;

import java.sql.*;

public class PlotLocCommand extends AbstractPlotCommand {
    
    @Override
    public String getName() {
        return "plotloc";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets information on a certain plot by giving loc.")
                .category(CommandCategory.GENERAL_STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("x"),
                        new HelpContextArgument()
                                .name("z"),
                        new HelpContextArgument()
                                .name("node")
                                .optional()
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("x",
                        new IntegerArgument())
                .addArgument("z",
                        new IntegerArgument())
                .addArgument("node",
                        new SingleArgumentContainer<>(new DefinedObjectArgument<>(1, 2, 3, 4, 5, 6, 7)).optional(null));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    // Build area runs from xmin/zmin; the codespace sits on the -x side of it (see Hypercube's GridCodeWorld).
    private static final String MIN_X = "xmin - (CASE WHEN plotsize >= 4 THEN 300 ELSE 20 END)";
    private static final String MAX_X = "xmin + (CASE" +
            "    WHEN plotsize = 1 THEN 51" +
            "    WHEN plotsize = 2 THEN 101" +
            "    WHEN plotsize = 3 THEN 301" +
            "    WHEN plotsize = 4 THEN 1001" +
            "    ELSE 0 END)";
    private static final String MAX_Z = "zmin + (CASE" +
            "    WHEN plotsize = 1 THEN 51" +
            "    WHEN plotsize = 2 THEN 101" +
            "    WHEN plotsize = 4 THEN 1001" +
            "    ELSE 301 END)";
    
    @Override
    public Plot getPlot(CommandEvent event) {
        boolean nodeSpecific = event.getArgument("node") != null;
        String query = "SELECT * FROM plots WHERE ? BETWEEN " + MIN_X + " AND " + MAX_X +
                " AND ? BETWEEN zmin AND " + MAX_Z +
                (nodeSpecific ? " AND node = ?" : "") + " LIMIT 1";
        
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, event.getArgument("x"));
            statement.setInt(2, event.getArgument("z"));
            if (nodeSpecific) {
                statement.setObject(3, event.getArgument("node"));
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return this.mapResultSetToPlot(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
}