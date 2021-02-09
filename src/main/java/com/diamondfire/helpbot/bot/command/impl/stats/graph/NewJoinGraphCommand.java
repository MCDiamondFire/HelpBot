package com.diamondfire.helpbot.bot.command.impl.stats.graph;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.sys.graph.generators.*;

public class NewJoinGraphCommand extends AbstractGraphCommand {
    
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
    public GraphGenerator getGraphGenerator() {
        return GraphGenerators.NEW_PLAYERS;
    }
}


