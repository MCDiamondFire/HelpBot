package com.diamondfire.helpbot.bot.command.impl.other.fun.samquote;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.textgen.CacheData;

import java.io.IOException;

public class ReloadSamquotesSubCommand extends SubCommand {
    
    @Override
    public String getName() {
        return "reload";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Reloads the list of samquotes.");
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.SR_HELPER;
    }
    
    @Override
    public void run(CommandEvent event) {
        try {
            CacheData.cacheData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}