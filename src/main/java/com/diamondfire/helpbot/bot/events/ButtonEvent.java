package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.sys.interaction.button.ButtonHandler;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonEvent extends ListenerAdapter {
    
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        ButtonHandler.handleEvent(event);
    }
}
