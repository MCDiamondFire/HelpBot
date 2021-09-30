package com.diamondfire.helpbot.bot.events.commands;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ParsedArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.*;

import java.util.*;

public interface CommandEvent {
    void pushArguments(String[] rawArgs) throws ArgumentException;
    
    Command getCommand();
    
    void setCommand(Command command);
    
    void reply(PresetBuilder preset);
    
    <T> T getArgument(String code);
    
    Map<String, ?> getArguments();
    
    ReplyHandler getReplyHandler();
    
    String getAliasUsed();
}
