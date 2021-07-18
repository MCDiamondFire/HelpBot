package com.diamondfire.helpbot.bot.command.impl.other.tag;

import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.help.HelpContextArgument;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.InformativeReply;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.InformativeReplyType;
import com.diamondfire.helpbot.bot.events.SubCommandEvent;
import com.diamondfire.helpbot.sys.tag.Tag;
import com.diamondfire.helpbot.sys.tag.TagAlreadyExistsException;
import com.diamondfire.helpbot.sys.tag.TagHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AddTagSubCommand implements SubCommand {
    
    @Override
    public String getName() {
        return "add";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
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
    public Permission getPermission() {
        return Permission.EXPERT;
    }
    
    @Override
    public void run(SubCommandEvent event) {
        // Get new activator and title
        String activator = event.getArgument("activator");
        String title = event.getArgument("title").replaceAll("%space%", " ");
        
        // Get response
        List<String> splitArgs = new LinkedList<>(Arrays.asList(event.getMessage().getContentRaw()
                .split(" +")));
        String response = String.join(" ", splitArgs.subList(4, splitArgs.size()));
        
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
                            new InformativeReply(InformativeReplyType.ERROR, err.getMessage())
                    );
            event.reply(preset);
        }
    }
    
}


