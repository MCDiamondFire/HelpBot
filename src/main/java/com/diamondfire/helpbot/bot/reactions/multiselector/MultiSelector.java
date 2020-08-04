package com.diamondfire.helpbot.bot.reactions.multiselector;

import com.diamondfire.helpbot.bot.reactions.impl.ReactionHandler;
import com.diamondfire.helpbot.df.codeinfo.viewables.BasicReaction;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.*;

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
        StringBuilder stringBuilder = new StringBuilder();
        LinkedList<String> nums = Util.getUnicodeNumbers();
        LinkedHashMap<BasicReaction, MultiSelectorPage> pagesHash = new LinkedHashMap<>();

        for (MultiSelectorPage page : this.pages) {
            if (page.isHidden()) {
                continue;
            }
            if (nums.isEmpty()) {
                throw new IllegalStateException("Not enough emojis to map 10 objects!");
            }
            String emoji = page.getCustomEmote() != null ? page.getCustomEmote() : nums.pop();
            stringBuilder.append("\n").append(emoji).append(" ").append(page.getName());
            pagesHash.put(new BasicReaction(emoji), page);
        }

        for (MultiSelectorPage page : this.pages) {
            EmbedBuilder pageBuilder = page.getPage();
            pageBuilder.addField("Pages", stringBuilder.toString(), false);
            pageBuilder.setTitle(page.getName());
        }

        jda.getTextChannelById(channel).sendMessage(pages[0].getPage().build()).queue((message) -> {
            for (BasicReaction reaction : pagesHash.keySet()) {
                reaction.react(message).queue();
            }
            ReactionHandler.waitReaction(user, message, event -> {
                MultiSelectorPage page = pagesHash.entrySet().stream()
                        .filter((entry) -> entry.getKey().equalToReaction(event.getReactionEvent().getReactionEmote()))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(null);
                jda.retrieveUserById(user).queue(userObj -> event.getReactionEvent().removeReaction(userObj).queue());
                message.editMessage(page.getPage().build()).queue();
            }, true);
        });
    }
}
