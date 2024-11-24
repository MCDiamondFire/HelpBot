package com.diamondfire.helpbot.bot.command.impl.stats.plot;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.IntegerArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.graph.graphable.*;
import com.diamondfire.helpbot.sys.graph.impl.ChartGraphBuilder;
import net.dv8tion.jda.api.utils.FileUpload;

import java.sql.ResultSet;
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
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        int plotID = event.getArgument("id");
        new DatabaseQuery()
                .query(new BasicQuery("SELECT DATE_FORMAT(FROM_UNIXTIME(time / 1000), '%d-%m-%y') AS time FROM plot_votes WHERE time < CURRENT_TIMESTAMP() - INTERVAL 1 MONTH AND plot = ? ORDER BY time;", (statement) -> statement.setInt(1, plotID)))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        PresetBuilder preset = new PresetBuilder();
                        preset.withPreset(
                                new InformativeReply(InformativeReplyType.ERROR, "Plot was not found.")
                        );
                        
                        event.reply(preset);
                        return;
                    }
                    
                    List<GraphableEntry<?>> entries = new ArrayList<>();
                    for (ResultSet set : result) {
                        entries.add(new StringEntry(set.getString("time")));
                    }
                    
                    event.getChannel().sendFiles(FileUpload.fromData(new ChartGraphBuilder()
                            .setGraphName(String.format("Votes on plot %s this month", plotID))
                            .createGraphFromCollection(entries))).queue();
                });
    }
    
}