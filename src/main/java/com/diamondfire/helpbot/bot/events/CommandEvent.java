package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ParsedArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.*;
import com.diamondfire.helpbot.bot.command.slash.SlashCommands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class CommandEvent {

    private static final ScheduledExecutorService DEFER_SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    private Command command;
    private final JDA jda;
    private final Guild guild;
    private final Member member;
    private final User author;
    private final MessageChannelUnion channel;
    private final Message message;
    private final SlashCommandInteractionEvent interaction;
    private InteractionState interactionState = InteractionState.PENDING;
    private CompletableFuture<InteractionHook> acknowledgement;
    private final String rawContent;
    private final ReplyHandler replyHandler;
    //TODO Cleanup and refactor this.
    // I'd like to see stuff like replying be put into it's whole own section and refactored as well.
    private ParsedArgumentSet parsedArgumentSet = null;
    private String aliasedUsed = null;

    public CommandEvent(Message message) {
        this.jda = message.getJDA();
        this.guild = message.getGuild();
        this.member = message.getMember();
        this.author = message.getAuthor();
        this.channel = message.getChannel();
        this.message = message;
        this.interaction = null;
        this.rawContent = message.getContentRaw();
        this.replyHandler = new ReplyHandler(channel.asGuildMessageChannel());

        String[] rawArgs = message.getContentDisplay().split(" ");
        String commandPrefix = rawArgs[0].substring(HelpBotInstance.getConfig().getPrefix().length()).toLowerCase();

        Command cmd = CommandHandler.getInstance().getCommands().get(commandPrefix.toLowerCase());
        if (cmd == null) {
            this.aliasedUsed = commandPrefix.toLowerCase();
            cmd = CommandHandler.getInstance().getAliases().get(commandPrefix.toLowerCase());
        }

        this.command = cmd;
    }

    public CommandEvent(SlashCommandInteractionEvent interaction) {
        this.jda = interaction.getJDA();
        this.guild = interaction.getGuild();
        this.member = interaction.getMember();
        this.author = interaction.getUser();
        this.channel = interaction.getChannel();
        this.message = null;
        this.interaction = interaction;
        this.replyHandler = new ReplyHandler(channel.asGuildMessageChannel());
        this.command = CommandHandler.getCommand(interaction.getName());
        if (!CommandHandler.getInstance().getCommands().containsKey(interaction.getName())) {
            this.aliasedUsed = interaction.getName();
        }
        this.rawContent = interaction.getCommandString();
    }

    public void pushArguments(String[] rawArgs) throws ArgumentException {
        this.parsedArgumentSet = ArgumentParser.parseArgs(command, Arrays.copyOfRange(rawArgs, 1, rawArgs.length), this);
    }

    public void pushSlashArguments() throws ArgumentException {
        this.parsedArgumentSet = SlashCommands.parseArguments(command, interaction, this);
    }

    public boolean isSlashCommand() {
        return interaction != null;
    }

    public boolean isInteractionResponse(Message candidate) {
        Message.InteractionMetadata metadata = candidate.getInteractionMetadata();
        return interaction != null && metadata != null && metadata.getIdLong() == interaction.getIdLong();
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public void reply(PresetBuilder preset) {
        if (preset.getEmbed().isEmpty()) {
            return;
        }

        reply(MessageCreateData.fromEmbeds(preset.getEmbed().build()), preset.isError(), null);
    }

    public void reply(EmbedBuilder embed) {
        if (embed.isEmpty()) {
            return;
        }

        reply(MessageCreateData.fromEmbeds(embed.build()));
    }

    public void reply(MessageCreateData data) {
        reply(data, false, null);
    }

    public void reply(MessageCreateData data, Consumer<Message> onSent) {
        reply(data, false, onSent);
    }

    private void reply(MessageCreateData data, boolean ephemeral, Consumer<Message> onSent) {
        if (interaction != null && replyToInteraction(data, ephemeral, onSent)) {
            return;
        }

        channel.sendMessage(data).queue(onSent);
    }

    public void scheduleDefer() {
        DEFER_SCHEDULER.schedule(this::defer, 2, TimeUnit.SECONDS);
    }

    private synchronized void defer() {
        if (interactionState == InteractionState.PENDING) {
            acknowledgement = interaction.deferReply().submit();
            acknowledgement.exceptionally(error -> {
                error.printStackTrace();
                return null;
            });
            interactionState = InteractionState.DEFERRED;
        }
    }

    private synchronized boolean replyToInteraction(MessageCreateData data, boolean ephemeral, Consumer<Message> onSent) {
        CompletableFuture<Message> sent;
        switch (interactionState) {
            case PENDING -> {
                acknowledgement = interaction.reply(data).setEphemeral(ephemeral).submit();
                sent = onSent == null ? acknowledgement.thenApply(hook -> null)
                        : acknowledgement.thenCompose(hook -> hook.retrieveOriginal().submit());
            }
            case DEFERRED -> {
                // Wait for the defer request before editing its original response.
                sent = acknowledgement.thenCompose(hook -> hook.editOriginal(MessageEditData.fromCreateData(data)).submit());
            }
            case DONE -> {
                // Follow-ups must retain the slash-command attribution too.
                sent = acknowledgement.thenCompose(hook -> hook.sendMessage(data).setEphemeral(ephemeral).submit());
            }
            default -> throw new IllegalStateException("Unknown interaction state");
        }

        interactionState = InteractionState.DONE;
        sent.whenComplete((message, error) -> {
            if (error != null) {
                error.printStackTrace();
            } else if (onSent != null) {
                onSent.accept(message);
            }
        });
        return true;
    }

    public <T> T getArgument(String code) {
        return parsedArgumentSet.getArgument(code);
    }

    public Map<String, ?> getArguments() {
        return parsedArgumentSet.getArguments();
    }

    public ReplyHandler getReplyHandler() {
        return replyHandler;
    }

    public String getAliasUsed() {
        return aliasedUsed;
    }

    public String getRawContent() {
        return rawContent;
    }

    public String[] getRawArgs() {
        return rawContent.split("\\s+");
    }

    public JDA getJDA() {
        return jda;
    }

    public Guild getGuild() {
        return guild;
    }

    public Member getMember() {
        return member;
    }

    public User getAuthor() {
        return author;
    }

    public MessageChannelUnion getChannel() {
        return channel;
    }

    public Message getMessage() {
        return message;
    }

    private enum InteractionState {
        PENDING,
        DEFERRED,
        DONE
    }
}
