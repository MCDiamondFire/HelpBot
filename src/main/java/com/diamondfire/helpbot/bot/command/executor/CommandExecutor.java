package com.diamondfire.helpbot.bot.command.executor;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.executor.checks.*;
import com.diamondfire.helpbot.bot.command.impl.*;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.command.slash.SlashCommands;
import com.diamondfire.helpbot.bot.events.commands.*;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CommandExecutor {
    
    public final ExecutorService POOL = Executors.newCachedThreadPool();
    private final CommandCheck[] checks = new CommandCheck[]{
            new MutedCheck(),
            new DisabledCheck(),
            new PermissionCheck(),
            new CommandLog()
    };
    
    public void run(CommandEvent e, String[] args) {
        Command command = e.getCommand();
        if (command == null) {
            return;
        }
        
        CompletableFuture.runAsync(() -> {
            try {
                e.pushArguments(args);
            } catch (ArgumentException exception) {
                e.reply(new PresetBuilder().withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "Invalid Argument!", exception.getEmbedMessage())
                ));
                return;
            }
            
            runCommandInternal(command, e);
        }, POOL);
    }
    
    public void run(SlashCommandEvent event) {
        Command command = CommandHandler.getInstance().getCommands().get(event.getInternalEvent().getName().toLowerCase(Locale.ROOT));
        if (command == null) return;
        if (command instanceof SubCommandHolder subCommandHolder) {
            Optional<SubCommand> optionalSubCommand = Arrays.stream(subCommandHolder.getSubCommands()).filter(subCommand -> subCommand.getName().equalsIgnoreCase(event.getInternalEvent().getSubcommandName())).findFirst();
            if (optionalSubCommand.isEmpty()) {
                event.reply(new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR, "Invalid Subcommand!", "Choose from " + event.getCommand().getHelpContext().getArguments().get(0).getArgumentName())));
                return;
            }
            command = optionalSubCommand.get();
        }
        event.setCommand(command);
        
        CompletableFuture.runAsync(() -> {
            // parse args
            try {
                event.putArguments(SlashCommands.parseSlashArgs(event));
            } catch (ArgumentException exception) {
                event.reply(new PresetBuilder().withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "Invalid Argument!", exception.getEmbedMessage())
                ));
                return;
            }
            
            runCommandInternal(event.getCommand(), event);
        }, POOL);
    }
    
    private void runCommandInternal(Command command, CommandEvent e) {
        PresetBuilder builder = new PresetBuilder();
        try {
            for (CommandCheck check : checks) {
                if (!check.check(e)) {
                    check.buildMessage(e, builder);
                    throw new CommandCheckFailure();
                }
            }
        
            command.run(e);
            return;
        } catch (CommandCheckFailure ignored) {
        
        } catch (Throwable exception) {
            exception.printStackTrace();
            builder.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "This command failed to execute, sorry for the inconvenience.")
            );
        
            {
                String commandStr = "unknown";
                if (e instanceof MessageCommandEvent messageCommandEvent) {
                    commandStr =  messageCommandEvent.getMessage().getContentRaw();
                } else if (e instanceof com.diamondfire.helpbot.bot.events.commands.SlashCommandEvent slashCommandEvent) {
                    commandStr = slashCommandEvent.getInternalEvent().getCommandString();
                }
                PresetBuilder preset = new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR, String.format("An error occurred while %s tried to execute ``%s``!", e.getMember().getEffectiveName(), commandStr)));
                EmbedBuilder embed = preset.getEmbed();
            
                try (StringWriter writer = new StringWriter();
                     PrintWriter printWriter = new PrintWriter(writer)) {
                    exception.printStackTrace(printWriter);
                
                    String error = writer.toString().substring(0, 1000);
                    embed.addField("Stack Trace", String.format("```%s```", error), false);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            
                Util.log(preset);
            }
        
        }
    
        e.reply(builder);
    }
}
