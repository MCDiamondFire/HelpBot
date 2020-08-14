package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MultiArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;

import java.util.List;


public class MimicCommand extends Command {

    @Override
    public String getName() {
        return "mimic";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Removes your message and replaces it with its own.")
                .category(CommandCategory.OTHER);
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("msg", new MultiArgumentContainer<>(new StringArgument()));
    }

    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }

    @Override
    public void run(CommandEvent event) {
        List<String> args = event.getArgument("msg");
        String msg = String.join(" ", args);

        event.getMessage().delete().queue();
        event.getChannel().sendMessage(msg).queue();

    }

}
