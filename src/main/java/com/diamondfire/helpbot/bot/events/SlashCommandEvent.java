package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.slash.SlashCommands;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.*;

public class SlashCommandEvent extends ListenerAdapter {

    private static final ExecutorService POOL = Executors.newCachedThreadPool();

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

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        Command command = SlashCommands.getCommand(event.getName(), event.getSubcommandName());
        if (command == null) {
            event.replyChoices().queue();
            return;
        }

        AutoCompleteQuery option = event.getFocusedOption();
        CompletableFuture.supplyAsync(() -> SlashCommands.complete(command, option.getName(), option.getValue()), POOL)
                .exceptionally((exception) -> {
                    exception.printStackTrace();
                    return List.of();
                })
                .thenAccept((choices) -> event.replyChoiceStrings(choices).queue());
    }
}
