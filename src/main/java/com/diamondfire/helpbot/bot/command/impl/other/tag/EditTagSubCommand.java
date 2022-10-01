package com.diamondfire.helpbot.bot.command.impl.other.tag;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Enum.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.tag.*;
import com.diamondfire.helpbot.sys.tag.exceptions.TagDoesNotExistException;

import java.io.IOException;

public class EditTagSubCommand extends SubCommand {
    
    @Override
    public String getName() {
        return "edit";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .addArgument(
                        new HelpContextArgument()
                            .name("activator"),
                        new HelpContextArgument()
                            .name("property")
                );
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet().addArgument(
                "activator", new StringArgument()
        ).addArgument(
                "property", new EnumArgument<TagProperty>()
                        .setEnum(TagProperty.class)
        ).addArgument(
                "newValue", new EndlessStringArgument()
        );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.SR_HELPER
                .setOverrides(this, 808966728201666620L);
    }
    
    @Override
    public void run(CommandEvent event) {
        
        // Get activator, property and newValue
        String activator = event.getArgument("activator");
        TagProperty property = event.getArgument("property");
        String newValue = event.getArgument("newValue");
        
        if (property == TagProperty.IMAGE && newValue.equals("none")) {
            newValue = "";
        }
    
        try {
            Tag tag = TagHandler.getTag(activator);
            property.set(tag, newValue);
            TagHandler.save();
            
            event.reply(new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.SUCCESS,
                                    "Successfully modified tag.")
                    ));
            
        } catch (TagDoesNotExistException | IOException err) {
            event.reply(new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR,
                                    err.getMessage())
                    ));
            
            err.printStackTrace();
        }
    }
    
}


