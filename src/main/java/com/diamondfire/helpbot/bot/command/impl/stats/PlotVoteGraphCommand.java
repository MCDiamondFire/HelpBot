package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.IntegerArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.sys.graph.graphable.*;
import com.diamondfire.helpbot.sys.graph.impl.ChartGraphBuilder;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.File;
import java.sql.Date;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PlotVoteGraphCommand extends Command {


    //Unfinished, try to make system that fills in gaps.
    @Override
    public String getName() {
        return "votegraph";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a graph showing how many people voted on certain days.")
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
        return Permission.BOT_DEVELOPER;
    }

    @Override
    public void run(CommandEvent event) {
        int plotID = event.getArgument("id");
        new SingleQueryBuilder()
                .query("SELECT DATE_FORMAT(FROM_UNIXTIME(time / 1000), '%d-%m') AS time FROM plot_votes WHERE time < CURRENT_TIMESTAMP() - INTERVAL 1 MONTH AND plot = ?;", (statement) -> {
                    statement.setInt(1, plotID);
                })
                .onQuery((resultTable) -> {
                    List<GraphableEntry<?>> entries = new ArrayList<>();
                    do {
                        entries.add(new StringEntry(resultTable.getString("time")));

                    } while (resultTable.next());

                    event.getChannel().sendFile(new ChartGraphBuilder()
                            .setGraphName(String.format("Votes on plot %s this month", plotID))
                            .createGraph(entries)).queue();
                })
                .onNotFound(() -> {
                    PresetBuilder preset = new PresetBuilder();
                    preset.withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, "Plot was not found.")
                    );

                    event.reply(preset);
                }).execute();
    }

}