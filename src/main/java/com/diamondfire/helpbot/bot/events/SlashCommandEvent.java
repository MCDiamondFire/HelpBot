package com.diamondfire.helpbot.bot.events;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandEvent extends ListenerAdapter {
    
    @Override
    public void onSlashCommand(@NotNull net.dv8tion.jda.api.events.interaction.SlashCommandEvent event) {
        String commandName = event.getName();
        
    }
}
