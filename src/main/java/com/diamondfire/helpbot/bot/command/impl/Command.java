package com.diamondfire.helpbot.bot.command.impl;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

public abstract class Command {
    
    public static final String SLASH_ARGUMENTS = "arguments";
    
    private ArgumentSet set = null;
    
    public abstract String getName();
    
    public boolean cacheArgumentSet() {
        return true;
    }
    
    public String[] getAliases() {
        return new String[0];
    }
    
    public abstract HelpContext getHelpContext();
    
    protected abstract ArgumentSet compileArguments();
    
    public ArgumentSet getArguments() {
        if (cacheArgumentSet()) {
            if (set == null) {
                set = compileArguments();
            }
            return set;
        } else {
            return compileArguments();
        }
    }
    
    public abstract Permission getPermission();
    
    public abstract void run(CommandEvent event);
    
    public SlashCommandData getSlashCommand() {
        String description = getHelpContext().getDescription();
        SlashCommandData data = Commands.slash(getName(), truncate(description == null || description.isBlank() ? getName() : description));
        
        if (!getArguments().getArguments().isEmpty()) {
            String usage = String.join(" ", FormatUtil.getArgumentDisplay(getHelpContext()));
            boolean required = !getArguments().getArguments().get(0).getContainer().isOptional();
            data.addOption(OptionType.STRING, SLASH_ARGUMENTS, truncate(usage.isEmpty() ? "Arguments" : usage), required);
        }
        
        return data;
    }
    
    private static String truncate(String text) {
        text = text.replaceAll("\\s+", " ").trim();
        return text.length() > 100 ? text.substring(0, 97) + "..." : text;
    }
}
