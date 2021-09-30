package com.diamondfire.helpbot.bot.command.impl.stats.graph;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.events.commands.CommandEvent;
import com.diamondfire.helpbot.sys.graph.generators.*;
import com.diamondfire.helpbot.sys.graph.generators.context.TimeGraphContext;

public class NewJoinGraphCommand extends AbstractGraphCommand<TimeGraphContext> {
    
    @Override
    public String getName() {
        return "joingraph";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a graph representing how many new players joined in a certain time frame.")
                .category(CommandCategory.GENERAL_STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("hourly|daily|amount|monthly|yearly"),
                        new HelpContextArgument()
                                .name("amount")
                );
    }
    
    @Override
    public GraphGenerator<TimeGraphContext> getGraphGenerator() {
        return GraphGenerators.NEW_PLAYERS;
    }
    
    @Override
    public TimeGraphContext createContext(CommandEvent event) {
        TimeMode mode = event.getArgument("mode");
        int amount = event.getArgument("amount");
        
        return new TimeGraphContext(mode, amount);
    }
}


