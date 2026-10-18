package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandEvent extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) {
            return;
        }

        CommandEvent commandEvent = new CommandEvent(event);
        if (commandEvent.getCommand() == null) {
            event.reply("Unknown command.").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();
        CommandHandler.getInstance().run(commandEvent);
    }
}
