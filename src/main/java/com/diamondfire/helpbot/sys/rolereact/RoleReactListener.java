package com.diamondfire.helpbot.sys.rolereact;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.*;

public class RoleReactListener extends ListenerAdapter {
    
    private static final long MESSAGE_ID = 762167137775124491L;
    private static final long CHANNEL_ID = 762158470019022858L;
    
    public RoleReactListener() {
        HelpBotInstance.getJda().getTextChannelById(CHANNEL_ID).retrieveMessageById(MESSAGE_ID).queue((msg) -> {
            
            for (MessageReaction reaction : msg.getReactions()) {
                if (ReactRole.fromEmoji(reaction.getReactionEmote().getEmoji()) == null) {
                    reaction.clearReactions().queue();
                }
            }
            
            List<String> emojis = new ArrayList<>();
            for (ReactRole role : ReactRole.values()) {
                String emoji = role.getEmoji();
                msg.addReaction(emoji).queue();
                
                String message;
                if (role.getOverride() == null) {
                    message = StringUtil.smartCaps(role.name().replace('_', ' '));
                } else {
                    message = role.getOverride();
                }
                
                emojis.add(emoji + " " + String.format("``%s``", message));
            }
            
            msg.editMessage("__**Reaction Roles**__ \nReact with any of the following to receive pings regarding that topic.\n" + StringUtil.listView("", emojis)).queue();
        });
    }
    
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        
        try {
            Role role = genericReact(event);
            
            event.getGuild().addRoleToMember(event.getUserIdLong(), role).reason("User subscribed to announcement!").queue();
        } catch (IllegalStateException ignored) {
        }
    }
    
    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        try {
            Role role = genericReact(event);
            
            event.getGuild().removeRoleFromMember(event.getUserIdLong(), role).reason("User unsubscribed from announcement!").queue();
        } catch (IllegalStateException ignored) {
        }
    }
    
    private Role genericReact(GenericGuildMessageReactionEvent reactionEvent) throws IllegalStateException {
        MessageReaction.ReactionEmote emote = reactionEvent.getReactionEmote();
        Guild guild = reactionEvent.getGuild();
        if (reactionEvent.getMessageIdLong() == MESSAGE_ID && emote.isEmoji() && !HelpBotInstance.getConfig().isDevBot()) {
            ReactRole role = ReactRole.fromEmoji(emote.getEmoji());
            if (role == null) {
                return null;
            }
            
            return guild.getRoleById(role.getRoleID());
        } else {
            throw new IllegalStateException();
        }
        
    }
    
    public static void refreshRoles(Member member) throws IllegalStateException {
        HelpBotInstance.getJda().getTextChannelById(CHANNEL_ID).retrieveMessageById(MESSAGE_ID).queue((msg) -> {
            for (MessageReaction reactionEmote : msg.getReactions()) {
                reactionEmote.retrieveUsers()
                        .cache(false)
                        .forEachAsync((user) -> {
                            if (user.getIdLong() == member.getIdLong()) {
                                ReactRole role = ReactRole.fromEmoji(reactionEmote.getReactionEmote().getEmoji());
                                
                                member.getGuild().addRoleToMember(member, member.getGuild().getRoleById(role.getRoleID())).queue();
                                return false;
                            }
                            return true;
                        });
            }
        });
    }
    
}
