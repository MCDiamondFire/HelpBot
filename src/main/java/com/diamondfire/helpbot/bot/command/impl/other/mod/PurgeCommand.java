package com.diamondfire.helpbot.bot.command.impl.other.mod;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import net.dv8tion.jda.api.entities.*;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

public class PurgeCommand extends Command {
    
    @Override public String getName() { return "purge"; }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Removes the most recent messages sent in a channel. Maximum 100 messages.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("count")
                );
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("count",
                        new IntegerArgument());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.MODERATION;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder();
    
        int messagesToRemove = event.getArgument("count");
        
        if (messagesToRemove > 100 || messagesToRemove < 2) {
            builder.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "Message count not within 2 to 100.")
            );
            event.reply(builder);
        } else {
            TextChannel channel = event.getChannel();
            channel.getHistory().retrievePast(messagesToRemove).queue((messages) -> {
                // Adds the messages to the messageBuilder object
                StringBuilder logBuilder = new StringBuilder();
                logBuilder.append(
                        String.format(
                                "%s purged %s messages in #%s\n\n",
                                event.getAuthor().getAsTag(),
                                messagesToRemove,
                                event.getChannel().getName()
                        )
                );
                
                // Iterates through the message history and appends the values to the StringBuilder.
                for (Message m : messages) {
                    logBuilder.append(
                            String.format("[%s] (%s): %s\n",
                                    m.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                                    m.getAuthor().getName(),
                                    m.getContentRaw().replace("\n", "\\n").replace("\r", "\\r") // prevent injection into the log file
                            )
                    );
                    if (!m.getAttachments().isEmpty()) {
                        for (Message.Attachment a : m.getAttachments()) {
                            logBuilder.append(
                                    String.format("-\tAttachment: %s\n",
                                            a.getProxyUrl())
                            );
                        }
                    }
                }
    
                
                
                try {
                    TextChannel evidenceLog = event.getJDA().getTextChannelById(
                            HelpBotInstance.getConfig().getPurgeEvidenceChannel()
                    );
    
                    assert evidenceLog != null;
                    evidenceLog.sendFile(logBuilder.toString().getBytes(StandardCharsets.UTF_8), "purge_log.txt").queue();
                } catch (Exception e) {
                    throw new IllegalStateException();
                }
                
                // Removes the messages.
                channel.deleteMessages(messages).queue();
            });
        }
    }
}
