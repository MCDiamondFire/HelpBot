package com.diamondfire.helpbot.bot.command.impl.other.dumps;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.IOException;
import java.nio.file.Files;

import java.io.IOException;
import java.nio.file.Files;

public class ActionDumpCommand extends Command {
    
    @Override
    public String getName() {
        return "actiondump";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Provides over the action dump file. This file provides a dump of all the data HelpBot uses. This is automatically updated every 24 hours.")
                .category(CommandCategory.OTHER);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        try {
            event.getReplyHandler().replyFile(new EmbedBuilder().setTitle("Action Dump"), Files.readAllBytes(ExternalFiles.DB), "db.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


