package com.diamondfire.helpbot.sys.multiselector;

import com.diamondfire.helpbot.sys.interaction.button.ButtonHandler;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

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
        for (MultiSelectorPage page : pages) {
            EmbedBuilder pageBuilder = page.getPage();
            pageBuilder.setTitle(page.getName());
        }
        
        Map<String, MultiSelectorPage> pageMap = new HashMap<>(pages.length);
        List<Button> buttons = new ArrayList<>();
        for (MultiSelectorPage page : pages) {
            if (page.isHidden()) {
                continue;
            }
            
            Button button = Button.secondary(getButtonKey(page), page.getName());
            if (page.getCustomEmote() != null) {
                button = button.withEmoji(Emoji.fromUnicode(page.getCustomEmote()));
            }
            
            pageMap.put(button.getId(), page);
            buttons.add(button);
        }
        
        jda.getTextChannelById(channel).sendMessageEmbeds(pages[0].getPage().build()).setActionRow(buttons).queue((message) -> {
            ButtonHandler.addListener(user, message, event -> {
                event.deferEdit().queue();
                message.editMessageEmbeds(pageMap.get(event.getComponentId()).getPage().build()).setComponents(message.getComponents()).queue();
            }, true);
        });
    }
    
    private String getButtonKey(MultiSelectorPage page) {
        return page.getName() + "-BUTTON";
    }
}
