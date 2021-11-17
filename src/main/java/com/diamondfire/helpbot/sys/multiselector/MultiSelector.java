package com.diamondfire.helpbot.sys.multiselector;

import com.diamondfire.helpbot.bot.events.commands.*;
import com.diamondfire.helpbot.sys.interaction.button.ButtonHandler;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.*;

public class MultiSelector {
    
    private final MultiSelectorPage[] pages;
    private final CommandEvent event;
    
    public MultiSelector(List<MultiSelectorPage> pages, CommandEvent commandEvent) {
        this.pages = pages.toArray(new MultiSelectorPage[0]);
        this.event = commandEvent;
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
        
        if (event instanceof SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.getInternalEvent().reply("The output of your command will be displayed below.").setEphemeral(true).queue();
        }
        event.getChannel().sendMessageEmbeds(pages[0].getPage().build()).setActionRows(Util.of(buttons)).queue((message) -> {
            ButtonHandler.addListener(event.getMember().getIdLong(), message, event -> {
                event.deferEdit().queue();
                message.editMessageEmbeds(pageMap.get(event.getComponentId()).getPage().build()).setActionRows(message.getActionRows()).queue();
            }, true);
        });
    }
    
    private String getButtonKey(MultiSelectorPage page) {
        return page.getName() + "-BUTTON";
    }
}
