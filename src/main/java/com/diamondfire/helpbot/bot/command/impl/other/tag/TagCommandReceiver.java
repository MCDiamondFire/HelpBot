package com.diamondfire.helpbot.bot.command.impl.other.tag;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;

import java.util.*;

public class TagCommandReceiver extends SubCommandReceiver {
    
    @Override
    public String getName() {
        return "tag";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Controls custom command tags. Notes:\n- New: Use \"%space%\" in the title for spaces."+
                        "\n- Edit: Use \"none\" for image to remove the image.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("add/edit/remove/list"),
                        new HelpContextArgument()
                                .name("add edit remove: activator")
                                .optional(),
                        new HelpContextArgument()
                                .name("add: title/edit: property")
                                .optional(),
                        new HelpContextArgument()
                                .name("add: response/edit: new value")
                                .optional()
                                
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet().addArgument("add/edit/remove/list",
                new StringArgument()
        );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
    }
    
    @Override
    public List<SubCommand> getSubCommands() {
        return Arrays.asList(
                new AddTagSubCommand(),
                new EditTagSubCommand(),
                new RemoveTagSubCommand(),
                new ListTagsSubCommand()
        );
    }
}


