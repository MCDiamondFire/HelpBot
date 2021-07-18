package com.diamondfire.helpbot.bot.command;

import com.diamondfire.helpbot.bot.command.impl.*;

import java.util.HashMap;

public class SubCommandHandler {
    
    private static HashMap<String, SubCommandReceiver> RECEIVERS = new HashMap<>();
    private static HashMap<String, SubCommand> SUBCOMMANDS = new HashMap<>();
    
    public static void registerReceivers(SubCommandReceiver... receivers) {
        for (SubCommandReceiver receiver : receivers) {
            RECEIVERS.put(receiver.getName().toLowerCase(), receiver);
            CommandHandler.getInstance().register(receiver);
        }
    }
    
    public static void registerSubCommands(SubCommand... subcommands) {
        for (SubCommand subcommand : subcommands) {
            SUBCOMMANDS.put(subcommand.getName().toLowerCase(), subcommand);
        }
    }
}
