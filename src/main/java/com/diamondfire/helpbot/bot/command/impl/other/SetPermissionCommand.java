package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.*;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;

public class SetPermissionCommand extends Command {

    @Override
    public String getName() {
        return "setperm";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Sets a users permission.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument().name("user"),
                        new HelpContextArgument().name("permission")
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("user",
                        new DiscordUserArgument())
                .addArgument("permission", new DefinedObjectArgument<>(Permission.values()));
    }

    @Override
    public Permission getPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void run(CommandEvent event) {
        long member = event.getArgument("user");
        Permission permission = event.getArgument("permission");
        PresetBuilder presetBuilder = new PresetBuilder();
        PermissionHandler.setPermission(member, permission);

        presetBuilder.withPreset(
                new InformativeReply(InformativeReplyType.SUCCESS, "Permission has been updated!")
        );
        event.reply(presetBuilder);
    }

}
