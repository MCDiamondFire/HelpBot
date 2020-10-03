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
                        new SingleArgumentContainer<>(new DefinedObjectArgument<>(1, 2, 3, 4)).optional(null));
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    // If someone who knows MYSQL enough that they can add plotsize as some sort of "local" variable. Go for it.
    @Override
    public ResultSet getPlot(CommandEvent event) {
        try {
            Connection connection = ConnectionProvider.getConnection();
            boolean nodeSpecific = event.getArgument("node") != null;
            PreparedStatement statement;
            if (nodeSpecific) {
                statement = connection.prepareStatement("SELECT * FROM hypercube.plots WHERE ? BETWEEN xmin AND xmin + (CASE" +
                        "    WHEN plotsize = 1 THEN 51" +
                        "    WHEN plotsize = 2 THEN 101" +
                        "    WHEN plotsize = 3 THEN 301" +
                        "    ELSE 0 END) AND ? BETWEEN zmin AND zmin + (CASE" +
                        "    WHEN plotsize = 1 THEN 51" +
                        "    WHEN plotsize = 2 THEN 101" +
                        "    WHEN plotsize = 3 THEN 301 ELSE 0 END) AND node = ? LIMIT 1");

                statement.setObject(3, event.getArgument("node"));
            } else {
                statement = connection.prepareStatement("SELECT * FROM hypercube.plots WHERE ? BETWEEN xmin AND xmin + (CASE" +
                        "    WHEN plotsize = 1 THEN 51" +
                        "    WHEN plotsize = 2 THEN 101" +
                        "    WHEN plotsize = 3 THEN 301" +
                        "    ELSE 0 END) AND ? BETWEEN zmin AND zmin + (CASE" +
                        "    WHEN plotsize = 1 THEN 51" +
                        "    WHEN plotsize = 2 THEN 101" +
                        "    WHEN plotsize = 3 THEN 301 ELSE 0 END) LIMIT 1");
            }

            statement.setInt(1, event.getArgument("x"));
            statement.setInt(2, event.getArgument("z"));
            return statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}