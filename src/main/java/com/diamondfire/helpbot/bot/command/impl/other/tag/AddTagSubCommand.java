package com.diamondfire.helpbot.bot.command.impl.other.tag;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import com.diamondfire.helpbot.sys.tag.*;
import com.diamondfire.helpbot.sys.tag.exceptions.TagAlreadyExistsException;

import java.io.IOException;

public class AddTagSubCommand extends SubCommand {
    
    @Override
    public String getName() {
        return "add";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Adds a custom command tag.")
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
    protected ArgumentSet compileArguments() {
        return new ArgumentSet().addArgument(
                "activator", new StringArgument()
        ).addArgument(
                "title", new QuoteStringArgument()
        ).addArgument(
                "response", new GreedyStringArgument()
        );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.EXPERT
                .setOverrides(this, 808966728201666620L);
    }
    
    @Override
    public void run(CommandEvent event) {
        // Get new activator, title and response
        String activator = event.getArgument("activator");
        String title = event.getArgument("title");
        String response = event.getArgument("response");
        response = response.replace("\\n", "\n");
        
        // Construct Tag
        Tag tag = new Tag(activator, title, response, event.getAuthor().getIdLong(), "");
    
        // Reply with result
        try {
            TagHandler.newTag(tag);
            
            event.reply(new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.SUCCESS,
                                    "Success", "Successfully added a new tag.")
                    ));
        } catch (TagAlreadyExistsException | IOException err) {
            
            event.reply(new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, err.getMessage())
                    ));
        }
    }
    
}


