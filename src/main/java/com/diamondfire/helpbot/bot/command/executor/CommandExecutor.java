package com.diamondfire.helpbot.bot.command.executor;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.executor.checks.*;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.command.*;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

import java.io.*;
import java.util.concurrent.*;

public class CommandExecutor {
    
    public final ExecutorService POOL = Executors.newCachedThreadPool();
    private final CommandCheck[] checks = new CommandCheck[]{
        new ApplicationCommandOnlyCheck(),
        new MutedCheck(),
        new DisabledCheck(),
        new PermissionCheck(),
        new CommandLog()
    };
    
    public void run(Message message) {
        MessageCommandEvent event = new MessageCommandEvent(message);

        runCommandInternalAsync(event);
    }
    
    public void run(GenericCommandInteractionEvent internalEvent) {
        ApplicationCommandEvent event = new ApplicationCommandEvent(internalEvent);

        runCommandInternalAsync(event);
    }

    private void runCommandInternalAsync(CommandEvent event) {
        CompletableFuture.runAsync(() -> runCommandInternal(event), POOL);
    }
    
    private void runCommandInternal(CommandEvent event) {
        try {
            event.parseCommand();
        } catch (ArgumentException exception) {
            event.reply(new PresetBuilder().withPreset(
                new InformativeReply(InformativeReplyType.ERROR, "Invalid Argument!", exception.getEmbedMessage())
            ));
            return;
        }

        for (CommandCheck check : checks) {
            if (!check.check(event)) {
                PresetBuilder builder = check.buildMessage(event);
                event.reply(builder);
            }
        }

        try {
            event.getBaseCommand().run(event);
        } catch (Exception exception) {
            exception.printStackTrace();
            event.reply(new PresetBuilder().withPreset(
                new InformativeReply(InformativeReplyType.ERROR, "This command failed to execute, sorry for the inconvenience.")
            ));
        
            {
                String commandStr = "unknown";
                if (event instanceof MessageCommandEvent messageCommandEvent) {
                    commandStr =  messageCommandEvent.getMessage().getContentRaw();
                } else if (event instanceof ApplicationCommandEvent applicationCommandEvent) {
                    commandStr = applicationCommandEvent.getInternalEvent().getCommandString();
                }

                PresetBuilder preset = new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR, String.format("An error occurred while %s tried to execute ``%s``!", event.getMember().getEffectiveName(), commandStr)));
                EmbedBuilder embed = preset.getEmbed();
            
                try (StringWriter writer = new StringWriter();
                     PrintWriter printWriter = new PrintWriter(writer)) {
                    exception.printStackTrace(printWriter);

                    String error = writer.toString().substring(0, 3500);
                    embed.addField("Stack Trace", String.format("```%s```", error), false);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            
                Util.log(preset);
            }
        
        }
    }
}
