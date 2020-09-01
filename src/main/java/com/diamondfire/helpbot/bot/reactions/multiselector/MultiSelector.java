package com.diamondfire.helpbot.bot.reactions.multiselector;

import com.diamondfire.helpbot.bot.reactions.impl.ReactionHandler;
import com.diamondfire.helpbot.df.codeinfo.viewables.BasicReaction;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.*;

public class MultiSelector {

    private final MultiSelectorPage[] pages;
    private final long channel;
    private final long user;

    public MultiSelector(long channel, long user, List<MultiSelectorPage> pages) {
        this.pages = pages.toArray(new MultiSelectorPage[0]);
        this.channel = channel;
        this.user = user;
    }

    public void send(JDA jda) {
        List<String> emojis = new ArrayList<>();
        Deque<String> nums = Util.getUnicodeNumbers();
        LinkedHashMap<BasicReaction, MultiSelectorPage> pagesHash = new LinkedHashMap<>();

        for (MultiSelectorPage page : pages) {
            if (page.isHidden()) {
                continue;
            }
            if (nums.isEmpty()) {
                throw new IllegalStateException("Not enough emojis to map 10 objects!");
            }

            String emoji = page.getCustomEmote() != null ? page.getCustomEmote() : nums.pop();

            emojis.add(emoji + " " + page.getName());
            pagesHash.put(new BasicReaction(emoji), page);
        }

        for (MultiSelectorPage page : pages) {
            EmbedBuilder pageBuilder = page.getPage();
            pageBuilder.setTitle(page.getName());
            EmbedUtils.addFields(pageBuilder, emojis, "", "Pages");
        }

        jda.getTextChannelById(channel).sendMessage(pages[0].getPage().build()).queue((message) -> {
            for (BasicReaction reaction : pagesHash.keySet()) {
                reaction.react(message).queue();
            }

            ReactionHandler.waitReaction(user, message, event -> {
                MessageReaction reactionEvent = event.getReactionEvent();
                MultiSelectorPage page = null;
                for (Map.Entry<BasicReaction, MultiSelectorPage> selectorPage : pagesHash.entrySet()) {
                    if (selectorPage.getKey().equalToReaction(reactionEvent.getReactionEmote())) {
                        page = selectorPage.getValue();
                        break;
                    }
                }

                jda.retrieveUserById(user).queue(userObj -> reactionEvent.removeReaction(userObj).queue());
                message.editMessage(page.getPage().build()).queue();
            }, true);
        });
    }
}
