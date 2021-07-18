package com.diamondfire.helpbot.bot.command.executor.checks;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.MissingArgumentException;
import com.diamondfire.helpbot.bot.command.help.HelpContextArgument;
import com.diamondfire.helpbot.bot.command.impl.*;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.*;

import java.util.*;

public class SubCommandCheck implements CommandCheck {
    
    @Override
    public boolean check(CommandEvent event) {
        // Abort if this is not a SubCommandReceiver
        if (!(event.getCommand() instanceof SubCommandReceiver)) return true;
        
        // Get receiver object
        SubCommandReceiver command = (SubCommandReceiver) event.getCommand();
        
        // Find the requested subcommand, or error if there is none
        List<String> splitArgs = new LinkedList<>(Arrays.asList(event.getMessage().getContentRaw()
                .split(" +")));
        if (splitArgs.size() <= 1) {
            replyInvalidSubcommand(event);
            return false;
        }
        String request = String.join(" ", splitArgs.get(1));
        
        // Iterate through receiver subcommand list to find a match
        // (hashmap? may not be worth it because there's not a lot of subcommands)
        for (SubCommand subcommand : command.getSubCommands()) {
            if (subcommand.getName().equals(request)) {
                // Check if the user has permissions, if true run
                if (subcommand.getPermission().hasPermission(event.getMember())) {
                    // Check if the argument requirements are in order
                    int i = 0;
                    for (HelpContextArgument arg : subcommand.getHelpContext().getArguments()) {
                        if (arg.isOptional()) break;
                        
                        if (splitArgs.size()-3 < i) {
                            MissingArgumentException exception = new MissingArgumentException(
                                    "Expected an argument, but got nothing.");
                            exception.setContext(command, i+1);
                            event.reply(new PresetBuilder().withPreset(
                                    new InformativeReply(InformativeReplyType.ERROR,
                                            "Invalid Argument!", exception.getEmbedMessage())
                            ));
                            return false;
                        }
                        
                        i++;
                    }
                    subcommand.run(new SubCommandEvent(event.getMessage(), subcommand));
                }
                return true;
            }
        }
        
        replyInvalidSubcommand(event);
        return false;
    }
    
    private void replyInvalidSubcommand(CommandEvent event) {
        event.reply(new PresetBuilder().withPreset(
                new InformativeReply(InformativeReplyType.ERROR,
                        "Invalid subcommand! Choose from: "+((SubCommandReceiver) event.getCommand())
                                .getFormattedSubCommands(event))
                )
        );
    }
    
    @Override
    public void buildMessage(CommandEvent event, PresetBuilder builder) {
    }
}
