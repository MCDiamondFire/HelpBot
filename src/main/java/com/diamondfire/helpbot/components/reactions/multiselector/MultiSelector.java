package com.diamondfire.helpbot.components.reactions.multiselector;

import com.diamondfire.helpbot.components.reactions.impl.ReactionHandler;
import com.diamondfire.helpbot.components.viewables.BasicReaction;
import com.diamondfire.helpbot.instance.BotInstance;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MultiSelector {

    private MultiSelectorPage[] pages;
    private long channel;
    private long user;

    public MultiSelector(long channel, long user, List<MultiSelectorPage> pages) {
        this.pages = pages.toArray(new MultiSelectorPage[0]);
        this.channel = channel;
        this.user = user;
    }

    public void send() {
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
            stringBuilder.append("\n" + emoji + " " + page.getName());
            pagesHash.put(new BasicReaction(emoji), page);
        }

        for (MultiSelectorPage page : this.pages) {
            EmbedBuilder pageBuilder = page.getPage();
            pageBuilder.addField("Pages", stringBuilder.toString(), false);
            pageBuilder.setTitle(page.getName());
        }

        BotInstance.getJda().getTextChannelById(channel).sendMessage(pages[0].getPage().build()).queue((message) -> {
            for (BasicReaction reaction : pagesHash.keySet()) {
                reaction.react(message).queue();
            }
            ReactionHandler.waitReaction(user, message, event -> {
                MultiSelectorPage page = pagesHash.entrySet().stream()
                        .filter((entry) -> entry.getKey().equalToReaction(event.getReactionEvent().getReactionEmote()))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(null);
                BotInstance.getJda().retrieveUserById(user).queue(userObj -> event.getReactionEvent().removeReaction(userObj).queue());
                message.editMessage(page.getPage().build()).queue();
            }, true);
        });
    }
}
