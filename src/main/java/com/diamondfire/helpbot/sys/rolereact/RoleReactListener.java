package com.diamondfire.helpbot.sys.rolereact;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.*;

public class RoleReactListener extends ListenerAdapter {


    private static final long MESSAGE_ID = 761781781720072232L;
    private static final long CHANNEL_ID = 589831308234981415L;

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
                emojis.add(emoji + " " + String.format("``%s``", StringUtil.smartCaps(role.name())));
            }

            msg.editMessage("React to this message to receive pings for certain announcements!\n " + StringUtil.listView("", emojis)).queue();

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

}
