package com.diamondfire.helpbot.bot.command.impl.other.tag;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;

public class TagCommand extends SubCommandHolder {
    
    @Override
    public String getName() {
        return "tag";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("""
                        Controls custom command tags. Notes:
                        - New: Use "%space%" in the title for spaces.
                        - Edit: Use "none" for image to remove the image.""")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("add/edit/remove/list"),
                        new HelpContextArgument()
                                .name("...")
                                .optional()
                );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public SubCommand[] getSubCommands() {
        return new SubCommand[]{
                new AddTagSubCommand(),
                new EditTagSubCommand(),
                new RemoveTagSubCommand(),
                new ListTagsSubCommand()
        };
    }
}


