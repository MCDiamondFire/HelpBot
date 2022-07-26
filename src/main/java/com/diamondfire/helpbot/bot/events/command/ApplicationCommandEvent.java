package com.diamondfire.helpbot.bot.events.command;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ParsedArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.reply.handler.*;
import com.diamondfire.helpbot.bot.command.argument.ParseResults;
import com.diamondfire.helpbot.sys.slash.SlashCommands;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

public class ApplicationCommandEvent extends CommandEvent {
    
    private final net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent internalEvent;
    
    public ApplicationCommandEvent(net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent internalEvent) {
        super(new InteractionReplyHandler(internalEvent));

        this.internalEvent = internalEvent;

        this.baseCommand = CommandHandler.getInstance().getCommands().get(internalEvent.getName().toLowerCase());

//        if (command instanceof SubCommandHolder subCommandHolder) {
//            Optional<SubCommand> optionalSubCommand = Arrays.stream(subCommandHolder.getSubCommands()).filter(subCommand -> subCommand.getName().equalsIgnoreCase(internalEvent.getSubcommandName())).findFirst();
//            if (optionalSubCommand.isEmpty()) {
//                throw new UnknownCommandException(UnknownCommandException.Level.SUBCOMMAND, internalEvent.getName());
//                this.reply(new PresetBuilder().withPreset(
//                    new InformativeReply(InformativeReplyType.ERROR, "Invalid Subcommand!", "Choose from " + command.getHelpContext().getArguments().get(0).getArgumentName())
//                ));
//                return;
//            }
//
//            this.command = optionalSubCommand.get();
//        }
    }

    @Override
    public ParseResults parseCommand() throws ArgumentException {
        ParsedArgumentSet parsedArgumentSet = SlashCommands.parseSlashArgs(this);

        return new ParseResults(???, parsedArgumentSet);
    }

    @Override
    public String getAliasUsed() {
        return baseCommand.getName();
    }
    
    @Override
    public JDA getJDA() {
        return internalEvent.getJDA();
    }
    
    @Override
    public Guild getGuild() {
        return internalEvent.getGuild();
    }
    
    @Override
    public Member getMember() {
        return internalEvent.getMember();
    }
    
    @Override
    public User getAuthor() {
        return internalEvent.getUser();
    }
    
    @Override
    public TextChannel getChannel() {
        return internalEvent.getTextChannel();
    }

    @ApiStatus.Internal
    public net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent getInternalEvent() {
        return internalEvent;
    }
}
