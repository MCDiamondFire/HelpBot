package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.value.StringArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.events.CommandEvent;

public abstract class AbstractPlayerCommand extends Command {

    @Override
    public ValueArgument<String> getArgument() {
        return new StringArg("Player Name/UUID", false);
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        if (event.getParsedArgs().isEmpty()) {
            execute(event, event.getMember().getEffectiveName());
        } else {
            execute(event, getArgument().getArg(event.getParsedArgs()));
        }
    }

    protected abstract void execute(CommandEvent event, String player);

}


