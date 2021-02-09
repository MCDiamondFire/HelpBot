package com.diamondfire.helpbot.bot.command.impl.stats.graph;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.sys.graph.generators.*;

public class PlayerJoinGraphCommand extends AbstractGraphCommand {
    
    @Override
    public String getName() {
        return "playergraph";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a graph representing how many players joined in a certain time frame (unique).")
                .category(CommandCategory.GENERAL_STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("hourly|daily|weekly|monthly|yearly"),
                        new HelpContextArgument()
                                .name("amount")
                );
    }
    
    @Override
    public GraphGenerator getGraphGenerator() {
        return GraphGenerators.UNIQUE_JOINS;
    }
}


