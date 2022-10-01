package com.diamondfire.helpbot.bot.command.impl.other.util;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MultiArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;

import java.lang.reflect.Field;
import java.util.*;

public class BulkExecuteCommand extends Command {
    
    @Override
    public String getName() {
        return "bulkexecute";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Allows you to execute player commands depending on given players (split by spaces).")
                .addArgument(
                        new HelpContextArgument()
                                .name("cmd"),
                        new HelpContextArgument()
                                .name("players")
                )
                .category(CommandCategory.OTHER);
    }
    
    @Override
    public boolean cacheArgumentSet() {
        return false;
    }
    
    @Override
    public ArgumentSet compileArguments() {
        List<String> playerCommands = new ArrayList<>();
        for (Command command : CommandHandler.getInstance().getCommands().values()) {
            if (command instanceof AbstractPlayerUUIDCommand) {
                playerCommands.add(command.getName());
            }
        }
        
        return new ArgumentSet()
                .addArgument("cmd",
                        new DefinedObjectArgument<>(playerCommands.toArray(new String[0])))
                .addArgument("players",
                        new MultiArgumentContainer<>(new StringArgument()));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.SR_HELPER;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder();
        List<String> playerNames = event.getArgument("players");
        String command = event.getArgument("cmd");
        
        for (String player : playerNames) {
            try {
                Command command1 = CommandHandler.getInstance().getCommands().get(command);
                Field field = event.getClass().getDeclaredField("command");
                field.trySetAccessible();
                field.set(event, command1);
                
                event.pushArguments(new String[]{"dummy", player});
                command1.run(event);
            } catch (Exception e) {
                builder.withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "Failed to execute.")
                );
                e.printStackTrace();
                event.reply(builder);
            }
            
        }
        
    }
    
}
