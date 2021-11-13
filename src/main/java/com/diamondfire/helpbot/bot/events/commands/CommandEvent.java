package com.diamondfire.helpbot.bot.events.commands;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.*;
import com.diamondfire.helpbot.bot.command.reply.handler.ReplyHandler;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;

import java.util.*;

public interface CommandEvent {
    void pushArguments(String[] rawArgs) throws ArgumentException;
    
    Command getCommand();
    
    void setCommand(Command command);
    
    void reply(PresetBuilder preset);
    
    // NOTE: This will not reply ephemerally on platforms where it is unsupported.
    void replyEphemeral(PresetBuilder presetBuilder);
    
    <T> T getArgument(String code);
    
    Map<String, ?> getArguments();
    
    ReplyHandler getReplyHandler();
    
    String getAliasUsed();
    
    JDA getJDA();
    Guild getGuild();
    Member getMember();
    User getAuthor();
    TextChannel getChannel();
}
