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

public class NewTagCommand extends Command {
    
    @Override
    public String getName() {
        return "newtag";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Creates a new custom command tag. Use \"%space%\" in the title to add a space.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("activator"),
                        new HelpContextArgument()
                                .name("title"),
                        new HelpContextArgument()
                                .name("response")
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet().addArgument("activator",
                new StringArgument()
        ).addArgument("title",
                new StringArgument()
        ).addArgument("response",
                new StringArgument()
        );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.EXPERT;
    }
    
    @Override
    public void run(CommandEvent event) {
        // Get new activator and title
        String activator = event.getArgument("activator");
        String title = ((String) event.getArgument("title")).replaceAll("%space%", " ");
        
        // Get response
        List<String> splitArgs = new LinkedList<>(Arrays.asList(event.getMessage().getContentRaw()
                .split(" +")));
        String response = String.join(" ", splitArgs.subList(3, splitArgs.size()));
        
        // Construct Tag
        Tag tag = new Tag(activator, title, response, event.getAuthor().getIdLong(), "");
    
        try {
            TagHandler.newTag(tag);
            PresetBuilder preset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.SUCCESS, "Success", "Successfully added a new tag.")
                    );
            event.reply(preset);
            
        } catch (TagAlreadyExistsException | IOException err) {
            PresetBuilder preset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, "Error!", err.getMessage())
                    );
            event.reply(preset);
        }
    }
    
}


