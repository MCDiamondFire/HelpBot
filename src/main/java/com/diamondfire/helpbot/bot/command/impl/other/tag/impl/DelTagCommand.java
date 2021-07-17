package com.diamondfire.helpbot.bot.command.impl.other.tag.impl;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.impl.other.tag.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;

import java.io.IOException;

public class DelTagCommand extends Command {
    
    @Override
    public String getName() {
        return "deltag";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Deletes a custom command tag by activator.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("activator")
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet().addArgument("activator",
                new StringArgument()
        );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.EXPERT;
    }
    
    @Override
    public void run(CommandEvent event) {
        // Get activator
        String activator = event.getArgument("activator");
        
        try {
            TagHandler.deleteTag(activator);
            PresetBuilder preset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.SUCCESS, "Success",
                                    "Successfully deleted tag with activator `"+activator+"`.")
                    );
            event.reply(preset);
            
        } catch (TagDoesntExistException | IOException err) {
            PresetBuilder preset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, "Error!",
                                    err.getMessage())
                    );
            event.reply(preset);
        }
    }
    
}


