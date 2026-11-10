package com.diamondfire.helpbot.bot.command.impl.other.util;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.*;
import com.diamondfire.helpbot.util.SolvedPostManager;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import java.util.ArrayList;


public class SolvedCommand extends Command {
    
    @Override
    public String getName() {
        return "solved";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"solve", "resolved"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Marks the help post as solved.")
                .category(CommandCategory.OTHER);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        // Limit to help forum.
        if (event.getChannel().getType() != ChannelType.GUILD_PUBLIC_THREAD ||
                event.getChannel().asThreadChannel().getParentChannel().getIdLong() != HelpBotInstance.getConfig().getHelpChannel()) {
            
            event.reply(new PresetBuilder().withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "Command can only be used in <#" + HelpBotInstance.getConfig().getHelpChannel() + ">")
            ));
            return;
        }
        
        ThreadChannel threadChannel = event.getChannel().asThreadChannel();
        
        // Check if the command is used by the post owner.
        if (event.getMember() == null || threadChannel.getOwnerIdLong() != event.getMember().getIdLong()) {
            event.reply(new PresetBuilder().withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "Command can only be used by the post owner.")
            ));
            return;
        }
        
        // Check if the post already has the solved tag.
        if (SolvedPostManager.isSolved(threadChannel)) {
            event.reply(new PresetBuilder().withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "Post is already solved.")
            ));
            return;
        }
        
        var solvedTag = SolvedPostManager.getSolvedTag(threadChannel);
        if (solvedTag.isEmpty()) {
            event.reply(new PresetBuilder().withPreset(new InformativeReply(
                    InformativeReplyType.ERROR, "The solved tag could not be found.")));
            return;
        }
        var tags = new ArrayList<ForumTag>(threadChannel.getAppliedTags());
        if (!tags.contains(solvedTag.get())) {
            tags.add(solvedTag.get());
        }
        // Reply through the interaction after Discord accepts the update.
        threadChannel.getManager().setAppliedTags(tags).queue(unused ->
                event.reply(new PresetBuilder().withPreset(new InformativeReply(
                        InformativeReplyType.SUCCESS, "Post marked as solved."))), error -> {
            error.printStackTrace();
            event.reply(new PresetBuilder().withPreset(new InformativeReply(
                    InformativeReplyType.ERROR, "Could not mark the post as solved. Please try again.")));
        });
    }
    
}
