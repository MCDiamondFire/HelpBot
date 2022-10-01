package com.diamondfire.helpbot.bot.command.impl.other.tag;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.tag.*;
import com.diamondfire.helpbot.sys.tag.exceptions.TagDoesNotExistException;

import java.io.IOException;

public class RemoveTagSubCommand extends SubCommand {
    
    @Override
    public String getName() {
        return "remove";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .addArgument(
                        new HelpContextArgument()
                            .name("activator")
                );
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument(
                        "activator", new StringArgument()
                );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.SR_HELPER
                .setOverrides(this, 808966728201666620L);
    }
    
    @Override
    public void run(CommandEvent event) {
        // Get activator
        String activator = event.getArgument("activator");
        
        try {
            TagHandler.deleteTag(activator);
            
            event.reply(new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.SUCCESS, String.format(
                                    "Successfully deleted tag with activator `%s`.", activator))
                    ));
            
        } catch (TagDoesNotExistException | IOException err) {
            event.reply(new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR,
                                    err.getMessage())
                    ));
        }
    }
    
}


