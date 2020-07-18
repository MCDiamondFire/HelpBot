package com.diamondfire.helpbot.command.impl.other;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.externalfile.ExternalFile;
import com.diamondfire.helpbot.events.CommandEvent;

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


