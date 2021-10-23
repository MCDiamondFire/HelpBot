package com.diamondfire.helpbot.bot.events.commands;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.util.Map;

public class SlashCommandEvent implements CommandEvent {
    
    @Override
    public void pushArguments(String[] rawArgs) throws ArgumentException {}
    
    @Override
    public Command getCommand() {
        return null;
    }
    
    @Override
    public void setCommand(Command command) {}
    
    @Override
    public void reply(PresetBuilder preset) {}
    
    @Override
    public void replyEphemeral(PresetBuilder preset) {}
    
    @Override
    public <T> T getArgument(String code) {
        return null;
    }
    
    @Override
    public Map<String, ?> getArguments() {
        return null;
    }
    
    @Override
    public ReplyHandler getReplyHandler() {
        return null;
    }
    
    @Override
    public String getAliasUsed() {
        return null;
    }
    
    @Override
    public JDA getJDA() {
        return null;
    }
    
    @Override
    public Guild getGuild() {
        return null;
    }
    
    @Override
    public Member getMember() {
        return null;
    }
    
    @Override
    public User getAuthor() {
        return null;
    }
    
    @Override
    public TextChannel getChannel() {
        return null;
    }
}
