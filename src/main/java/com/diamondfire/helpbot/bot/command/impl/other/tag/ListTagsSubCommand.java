package com.diamondfire.helpbot.bot.command.impl.other.tag;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.tag.*;

import java.util.stream.Collectors;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    public Rank getRank() {
        return Rank.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        String formattedTags = TagHandler.getTags().stream()
                .map(tag -> String.format("`%s`", tag.getActivator()))
                .collect(Collectors.joining(" "));
    
        if (formattedTags.isEmpty()) {
            formattedTags = "*None*";
        }
    
        event.reply(new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Tags",
                                String.format("""
                                                A list of all custom command tags added.
                                                You can execute a tag using `%s<tag name>`.
            
                                                %s""",
                                        HelpBotInstance.getConfig().getPrefix(), formattedTags))
                ));
    
    }
    
}


