package com.diamondfire.helpbot.bot.command.impl.stats.plot;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.IntegerArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.sys.database.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.*;
import com.mysql.jdbc.interceptors.ResultSetScannerInterceptor;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.File;
import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PlotCommand extends AbstractPlotCommand {

    @Override
    public String getName() {
        return "plot";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets information on a certain plot.")
                .category(CommandCategory.STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("plot id")
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("id",
                        new IntegerArgument());
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public ResultSet getPlot(CommandEvent event) {
        try {Connection connection = ConnectionGiver.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM hypercube.plots WHERE id = ?");
            statement.setInt(1, event.getArgument("id"));

            return statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}