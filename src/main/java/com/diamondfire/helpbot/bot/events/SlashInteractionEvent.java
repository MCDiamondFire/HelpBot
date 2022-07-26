package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.events.command.ApplicationCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashInteractionEvent extends ListenerAdapter {
    
    @Override
    public void onGenericCommandInteraction(@NotNull GenericCommandInteractionEvent event) {
        CommandHandler.getInstance().run(event);
    }
}
