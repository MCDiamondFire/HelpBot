package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashInteractionEvent extends ListenerAdapter {
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        CommandHandler.getInstance().run(new com.diamondfire.helpbot.bot.events.commands.SlashCommandEvent(event));
    }
}
