package com.diamondfire.helpbot.command.impl.other;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.value.StringArg;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.events.CommandEvent;


public class MimicCommand extends Command {

    @Override
    public String getName() {
        return "mimic";
    }

    @Override
    public String getDescription() {
        return "Removes your message and replaces it with its own.";
    }

    @Override
    public Argument getArgument() {
        return new StringArg("Message", true);
    }

    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }

    @Override
    public void run(CommandEvent event) {
        event.getMessage().delete().queue();
        event.getChannel().sendMessage(event.getParsedArgs()).queue();

    }

}
