package com.diamondfire.helpbot.sys.message.acceptors;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

public class GhostPingAcceptor implements MessageAcceptor {
    
    private static final TextChannel GHOST_PING_CHANNEL = HelpBotInstance.getJda().getTextChannelById(HelpBotInstance.getConfig().getGhostPingChannel());
    
    @Override
    public boolean accept(Message message) {
        
        if (message.getMentionedRoles().contains(HelpBotInstance.getJda().getRoleById(Permission.MODERATION.getRole()))
                || message.getMentionedRoles().contains(HelpBotInstance.getJda().getRoleById(Permission.ADMINISTRATOR.getRole()))) {
            GhostPingAcceptor.sendMessage(message);
        }
        
        return false; // There is no docs I think this correct though
    }
    
    private static void sendMessage(Message message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Potential Ghost Ping", message.getJumpUrl())
                .setColor(0xFF33CC)
                .addField(message.getMember().getEffectiveName(), message.getContentDisplay(), true);
        GhostPingAcceptor.GHOST_PING_CHANNEL.sendMessageEmbeds(builder.build()).queue();
    }
    
}
