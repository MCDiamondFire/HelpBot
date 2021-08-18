package com.diamondfire.helpbot.bot.command.impl.stats.graph;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.impl.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.graph.generators.*;

public abstract class AbstractGraphCommand<T> extends Command {
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("mode",
                        new DefinedObjectArgument<>(TimeMode.values()))
                .addArgument("amount",
                        new ClampedIntegerArgument(1, 99999999));
    }
    
    @Override
    public Rank getRank() {
        return Rank.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        T context = createContext(event);
        
        event.getChannel().sendFile(getGraphGenerator().createGraph(context)).queue();
    }
    
    public abstract GraphGenerator<T> getGraphGenerator();
    
    public abstract T createContext(CommandEvent event);
    
}


