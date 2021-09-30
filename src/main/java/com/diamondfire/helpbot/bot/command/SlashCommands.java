package com.diamondfire.helpbot.bot.command;

import com.diamondfire.helpbot.bot.command.help.CommandCategory;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SlashCommands {
    public static CommandData createCommandData(Command command) {
        CommandCategory category = command.getHelpContext().getCommandCategory();
        String desc = (category != null ? category.getName() : "Misc") + " - " + command.getHelpContext().getDescription();
        return new CommandData(command.getName(), StringUtil.trim(desc, 100))
                .setDefaultEnabled(command.getPermission() == Permission.USER);
    }
}
