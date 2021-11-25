package com.diamondfire.helpbot.sys.multiselector;

import com.diamondfire.helpbot.bot.events.command.*;
import com.diamondfire.helpbot.sys.interaction.button.ButtonHandler;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.*;
import java.util.function.Consumer;

public class MultiSelector {
    
    private final MultiSelectorPage[] pages;
    
    public MultiSelector(List<MultiSelectorPage> pages) {
        this.pages = pages.toArray(new MultiSelectorPage[0]);
    }
    
    public void send(CommandEvent commandEvent) {
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
        
        long user = commandEvent.getAuthor().getIdLong();
        Consumer<Message> onMessage = (message) -> {
            ButtonHandler.addListener(user, message, event -> {
                event.deferEdit().queue();
                message.editMessageEmbeds(pageMap.get(event.getComponentId()).getPage().build()).setActionRows(message.getActionRows()).queue();
            }, true);
        };
        if (commandEvent instanceof MessageCommandEvent) {
            commandEvent.getChannel()
                    .sendMessageEmbeds(pages[0].getPage().build())
                    .setActionRow(buttons)
                    .queue(onMessage);
        } else if (commandEvent instanceof SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.getInternalEvent().deferReply().queue(interactionHook -> {
                interactionHook.sendMessageEmbeds(pages[0].getPage().build())
                        .addActionRow(buttons)
                        .queue(onMessage);
            });
        }
    }
    
    private String getButtonKey(MultiSelectorPage page) {
        return page.getName() + "-BUTTON";
    }
}
