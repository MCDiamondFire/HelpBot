package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.disable.DisableCommandHandler;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;

public class DisableCommand extends Command {

    @Override
    public String getName() {
        return "disable";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("This command disables a command entirely.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("cmd")
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet().addArgument("cmd",
                new StringArgument()
        );
    }

    @Override
    public Permission getPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder();
        String name = event.getArgument("cmd");
        Command command = CommandHandler.getCommand(name);
        if (command instanceof DisableCommand || command instanceof EnableCommand) {
            builder.withPreset(new InformativeReply(InformativeReplyType.ERROR, "You cannot disable these commands!"));
            event.reply(builder);
            return;
        }

        if (command != null) {
            HelpBotInstance.getHandler().getDisabledHandler().disable(command);
            builder.withPreset(new InformativeReply(InformativeReplyType.SUCCESS, String.format("Command ``%s`` has been disabled.", command.getName())));
        } else {
            builder.withPreset(new InformativeReply(InformativeReplyType.ERROR, String.format("Command ``%s`` could not be found.", name)));
        }

        event.reply(builder);
    }

}


