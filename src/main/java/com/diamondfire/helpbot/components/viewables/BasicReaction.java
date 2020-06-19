package com.diamondfire.helpbot.components.viewables;

import com.diamondfire.helpbot.instance.BotInstance;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.requests.RestAction;

public class BasicReaction {

    final boolean isUnicode;
    String unicode;
    long id;

    public BasicReaction(String unicode) {
        isUnicode = true;
        this.unicode = unicode;
    }

    public BasicReaction(long emoteID) {
        isUnicode = false;
        this.id = emoteID;
    }


    public String getUnicode() {
        if (!isUnicode) throw new IllegalStateException("Emoji is not a unicode char!");

        return unicode;

    }

    public Emote getEmote() {
        if (isUnicode) throw new IllegalStateException("Emoji is a unicode char!");

        return BotInstance.getJda().getEmoteById(id);
    }

    public RestAction<Void> react(Message message) {
        if (isUnicode) return message.addReaction(getUnicode());
        if (!isUnicode) return message.addReaction(getEmote());
        return null;
    }

    @Override
    public String toString() {
        if (isUnicode) return getUnicode();
        if (!isUnicode) return getEmote().getAsMention();
        return "?";
    }

    public boolean equalToReaction(MessageReaction.ReactionEmote reaction) {
        // if reaction is emoji yet this isn't unicode error is thrown.
        if (reaction.isEmoji() != isUnicode) {
            return false;
        }

        //TODO if reaction isEmoji and this is emoji isn't same return false.
        if (reaction.isEmoji()) return getUnicode().equals(reaction.getEmoji());
        if (!reaction.isEmoji()) return getEmote().getIdLong() == reaction.getIdLong();
        return false;
    }
}
