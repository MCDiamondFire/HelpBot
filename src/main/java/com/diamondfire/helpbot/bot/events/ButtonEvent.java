package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.sys.interaction.button.ButtonHandler;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonEvent extends ListenerAdapter {
    
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        ButtonHandler.handleEvent(event);
    }
}
