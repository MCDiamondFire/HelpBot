package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.command.help.HelpContextArgument;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import net.dv8tion.jda.api.entities.Message;

import java.util.*;

public class SubCommandEvent extends CommandEvent {
    
    private final SubCommand subcommand;
    private final Map<String, ?> arguments;
    
    public SubCommandEvent(Message message, SubCommand subCommand) {
        super(message);
        this.subcommand = subCommand;
        this.arguments = generateArguments();
    }
    
    private Map<String, ?> generateArguments() {
        List<String> splitArgs = new LinkedList<>(Arrays.asList(this.getMessage().getContentRaw()
                .split(" +")));
        splitArgs = splitArgs.subList(2, splitArgs.size());
        
        Map<String, Object> result = new HashMap<>();
        List<HelpContextArgument> context = subcommand.getHelpContext().getArguments();
    
        int i = 0;
        for (String arg : splitArgs) {
            if (context.size() <= i) break;
            result.put(context.get(i).getArgumentName(), arg);
            i++;
        }
        return result;
    }
    
    @Override
    public Map<String, ?> getArguments() {
        return arguments;
    }
    
    @Override
    public String getArgument(String code) {
        return (String) arguments.get(code);
    }
    
    public SubCommand getSubCommand() {
        return this.subcommand;
    }
}
