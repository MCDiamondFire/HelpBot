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

public class EnableCommand extends Command {

    @Override
    public String getName() {
        return "enable";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("This command enables a disabled command.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("cmd")
                );
    }

    @Override
    public ArgumentSet compileArguments() {
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
        DisableCommandHandler handler = HelpBotInstance.getHandler().getDisabledHandler();
        PresetBuilder builder = new PresetBuilder();
        String name = event.getArgument("cmd");
        Command command = CommandHandler.getCommand(name);

        if (command != null) {
            if (!handler.isDisabled(command)) {
                builder.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Command isn't disabled!"));
            } else {
                handler.enable(command);
                builder.withPreset(new InformativeReply(InformativeReplyType.SUCCESS, String.format("Command ``%s`` has been enabled.", command.getName())));
            }
        } else {
            builder.withPreset(new InformativeReply(InformativeReplyType.ERROR, String.format("Command ``%s`` could not be found.", name)));
        }

        event.reply(builder);
    }

}


