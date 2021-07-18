package com.diamondfire.helpbot.bot.command.impl.other.tag;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.SubCommandEvent;
import com.diamondfire.helpbot.sys.tag.*;

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
    public Permission getPermission() {
        return Permission.EXPERT;
    }
    
    @Override
    public void run(SubCommandEvent event) {
        // Get activator
        String activator = event.getArgument("activator");
        
        try {
            TagHandler.deleteTag(activator);
            PresetBuilder preset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.SUCCESS,
                                    "Successfully deleted tag with activator `"+activator+"`.")
                    );
            event.reply(preset);
            
        } catch (TagDoesntExistException | IOException err) {
            PresetBuilder preset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR,
                                    err.getMessage())
                    );
            event.reply(preset);
        }
    }
    
}


