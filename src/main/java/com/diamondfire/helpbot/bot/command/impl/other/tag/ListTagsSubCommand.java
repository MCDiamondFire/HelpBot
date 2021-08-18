package com.diamondfire.helpbot.bot.command.impl.other.tag;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.tag.*;

import java.io.IOException;
import java.util.List;

public class ListTagsSubCommand extends SubCommand {
    
    @Override
    public String getName() {
        return "list";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext();
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        try {
            List<Tag> tags = TagHandler.getTags();
            String string = "";
            
            if (tags.size() == 0) {
                string = "*None*";
                
            } else {
                for (Tag tag : tags) {
                    string += "`" + tag.getActivator() + "` ";
                }
            }
            
            PresetBuilder preset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.INFO, "Tags",
                                    "A list of all custom command tags added.\n\n"+string)
                    );
            
            event.reply(preset);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}


