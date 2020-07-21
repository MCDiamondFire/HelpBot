package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.sys.externalfile.ExternalFile;
import com.diamondfire.helpbot.bot.events.CommandEvent;

public class FetchDumpCommand extends Command {

    @Override
    public String getName() {
        return "actiondump";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Sends over the action dump file.")
                .category(CommandCategory.OTHER);
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }

    @Override
    public void run(CommandEvent event) {
        event.getChannel().sendFile(ExternalFile.DB.getFile()).queue();
    }

}


