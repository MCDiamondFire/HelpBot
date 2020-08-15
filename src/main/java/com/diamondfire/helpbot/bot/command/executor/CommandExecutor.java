package com.diamondfire.helpbot.bot.command.executor;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.executor.checks.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;

import java.util.concurrent.*;

public class CommandExecutor {

    private final ExecutorService POOL = Executors.newCachedThreadPool();
    private final CommandCheck[] checks = new CommandCheck[]{
            new DisabledCheck(),
            new PermissionCheck(),
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
                        throw new IllegalStateException("Command failed check.");
                    }
                }

                e.pushArguments(e.getMessage().getContentRaw().split(" "));
                command.run(e);
            } catch (ArgumentException exception) {
                builder.withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "Invalid Argument!", exception.getEmbedMessage())
                );
            } catch (IllegalStateException ignored) {


            } catch (Exception exception) {
                exception.printStackTrace();
                builder.withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "This command failed to execute, sorry for the inconvenience.")
                );
            }

            e.reply(builder);
        }, POOL);
    }


}
