package com.diamondfire.helpbot.bot.command.impl.other.tag;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.tag.*;

import java.io.IOException;
import java.util.*;

public class EditTagCommand extends Command {
    
    private final String PROPERTIES = "activator/title/response/image";
    
    @Override
    public String getName() {
        return "edittag";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Edits a new custom command tag property. Use 'none' for the image to remove it.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("activator"),
                        new HelpContextArgument()
                                .name(PROPERTIES),
                        new HelpContextArgument()
                                .name("new value")
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet().addArgument("activator",
                new StringArgument()
        ).addArgument(PROPERTIES,
                new StringArgument()
        ).addArgument("new value",
                new StringArgument()
        );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.EXPERT;
    }
    
    @Override
    public void run(CommandEvent event) {
        // Get activator and property
        String activator = event.getArgument("activator");
        String prop = event.getArgument(PROPERTIES);
        
        // Check if property is valid
        TagProperty property = TagProperty.getByProperty(prop);
        if (property == null || !property.isModifiable()) {
            PresetBuilder preset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR,
                                    "This is not a valid property. Choose from: `activator`, `title`, `response`, `image`")
                    );
            event.reply(preset);
            return;
        }
        
        // Get new value
        List<String> splitArgs = new LinkedList<>(Arrays.asList(event.getMessage().getContentRaw()
                .split(" +")));
        String newValue = String.join(" ", splitArgs.subList(3, splitArgs.size()));
        if (property == TagProperty.IMAGE && newValue.equals("none")) newValue = "";
        
        try {
            Tag tag = TagHandler.getTag(activator);
            property.edit(tag, newValue);
            TagHandler.saveToJson();
            
            PresetBuilder preset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.SUCCESS, "Success", "Successfully modified tag.")
                    );
            event.reply(preset);
            
        } catch (TagDoesntExistException | IOException err) {
            PresetBuilder preset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, "Error!", err.getMessage())
                    );
            event.reply(preset);
            err.printStackTrace();
        }
    }
    
}


