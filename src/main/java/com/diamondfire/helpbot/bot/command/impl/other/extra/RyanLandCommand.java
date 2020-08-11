package com.diamondfire.helpbot.bot.command.impl.other.extra;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;

public class RyanLandCommand extends Command {

    // How to get your own command 101.
    // Annoy the heck out of Owen! :D
    @Override
    public String getName() {
        return "ryanland";
    }

    @Override
    public HelpContext getHelpContext()  {
        return new HelpContext();
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        event.reply("\uD83D\uDE33");
    }
}
