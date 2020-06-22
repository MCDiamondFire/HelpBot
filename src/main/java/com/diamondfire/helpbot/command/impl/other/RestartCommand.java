package com.diamondfire.helpbot.command.impl.other;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.impl.CommandCategory;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.externalfile.ExternalFile;
import com.diamondfire.helpbot.events.CommandEvent;

import java.io.File;
import java.util.Random;

public class RestartCommand extends Command {

    @Override
    public String getName() {
        return "restart";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"stop", "exit"};
    }

    @Override
    public String getDescription() {
        return "Restarts command";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.OTHER;
    }

    @Override
    public Argument getArgument() {
        return new NoArg();
    }

    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }

    @Override
    public void run(CommandEvent event) {
        System.exit(0);
    }

}


