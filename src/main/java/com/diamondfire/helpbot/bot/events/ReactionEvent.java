package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.reactions.impl.ReactionHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ReactionEvent extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getMember().getUser().isBot()) return;

        if (ReactionHandler.isMessageReserved(event.getMessageIdLong())) {
            event.getReaction().retrieveUsers().queue((users) -> {
                if (!users.contains(event.getJDA().getSelfUser()) || !ReactionHandler.isWaiting(event.getMember().getIdLong())) {
                    if (event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_MANAGE)) {
                        MessageReaction.ReactionEmote emote = event.getReactionEmote();
                        if (emote.isEmoji()) {
                            event.getChannel().removeReactionById(event.getMessageIdLong(), emote.getEmoji(), event.getUser()).queue();
                        } else if (emote.isEmote()) {
                            event.getChannel().removeReactionById(event.getMessageIdLong(), emote.getEmote(), event.getUser()).queue();
                        }

                    }
                } else {
                    if (ReactionHandler.isWaiting(event.getMember().getIdLong())) {
                        ReactionHandler.handleReaction(event);
                    }
                }
            });
        }
    }
}
