package com.diamondfire.helpbot.bot.command.impl.other.mod;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.nio.file.*;
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
            TextChannel channel = event.getChannel().asTextChannel();
            channel.getHistory().retrievePast(messagesToRemove).queue((messages) -> {
                // Adds the messages to the messageBuilder object
                StringBuilder stringBuilder = new StringBuilder();
                
                // Iterates through the message history and appends the values to the MessageBuilder.
                for (Message m : messages) {
                    stringBuilder.insert(0,
                                    String.format("[%s] (%s): %s",
                                            m.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                                            m.getAuthor().getName(),
                                            m.getContentRaw())
                            );
                    if (!m.getAttachments().isEmpty()) {
                        for (Message.Attachment a : m.getAttachments()) {
                            stringBuilder.insert(0,
                                    String.format(" [ATTACHMENT: %s ]\n",
                                            a.getProxyUrl())
                            );
                        }
                    } else {
                        stringBuilder.insert(0,
                                "\n"
                        );
                    }
                }
    
                stringBuilder.insert(0,
                        String.format("%s purged %s messages in #%s",
                                event.getAuthor().getAsTag(),
                                messagesToRemove,
                                event.getChannel().getName()));
                
                try {
                    File file = ExternalFileUtil.generateFile("purge_log.txt");
                    Files.writeString(file.toPath(), stringBuilder.toString(), StandardOpenOption.WRITE);
    
                    TextChannel evidenceLog = event.getJDA().getTextChannelById(
                            HelpBotInstance.getConfig().getPurgeEvidenceChannel()
                    );
    
                    assert evidenceLog != null;
                    evidenceLog.sendFiles(FileUpload.fromData(file)).queue();
                    
                } catch (Exception e) {
                    throw new IllegalStateException();
                }
                
                // Removes the messages.
                channel.deleteMessages(messages).queue();
            });
        }
    }
}
