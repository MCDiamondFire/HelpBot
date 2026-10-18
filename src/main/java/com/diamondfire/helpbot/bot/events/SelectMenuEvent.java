package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.sys.interaction.select.SelectMenuHandler;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SelectMenuEvent extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        SelectMenuHandler.handleEvent(event);
    }
}
