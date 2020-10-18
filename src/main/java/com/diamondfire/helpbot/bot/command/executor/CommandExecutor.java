package com.diamondfire.helpbot.bot.command.executor;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.executor.checks.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.*;
import java.util.concurrent.*;

public class CommandExecutor {

    private final ExecutorService POOL = Executors.newCachedThreadPool();
    private final CommandCheck[] checks = new CommandCheck[]{
            new MutedCheck(),
            new DisabledCheck(),
            new PermissionCheck(),
            new CommandLog(),
    };

    public void run(CommandEvent e) {
        Command command = e.getCommand();
        if (command == null) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            PresetBuilder builder = new PresetBuilder();
            try {
                for (CommandCheck check : checks) {
                    if (!check.check(e)) {
                        check.buildMessage(e, builder);
                        throw new CommandCheckFailure();
                    }
                }

                e.pushArguments(e.getMessage().getContentRaw().split(" "));
                command.run(e);
            } catch (ArgumentException exception) {
                builder.withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "Invalid Argument!", exception.getEmbedMessage())
                );
            } catch (CommandCheckFailure ignored) {

            } catch (Throwable exception) {
                exception.printStackTrace();
                builder.withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "This command failed to execute, sorry for the inconvenience.")
                );

                {
                    PresetBuilder preset = new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR, String.format("An error occurred while %s tried to execute ``%s``!", e.getMember().getEffectiveName(), e.getMessage().getContentRaw())));
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
        }, POOL);
    }


}
