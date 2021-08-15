package com.diamondfire.helpbot.bot.command.impl.other.mod;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

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
                MessageBuilder messageBuilder = new MessageBuilder();
                messageBuilder.append("Here are the messages you purged;\n");
                HashMap<Message.Attachment, Message> attachments = new HashMap<>();
                
                // Iterates through the message history and appends the values to the MessageBuilder.
                for (Message m : messages) {
                    messageBuilder
                            .append(
                                    String.format("[%s] (%s): %s",
                                            m.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                                            m.getAuthor().getAsMention(),
                                            m.getContentRaw())
                            );
                    if (!m.getAttachments().isEmpty()) {
                        for (Message.Attachment a : m.getAttachments()) {
                            attachments.put(a, m);
                        }
                    }
                }
                
                // Builds the MessageBuilder object and iterates through it.
                // messageBuilder.buildAll() returns a queue of messages, split if the content exceeds 2000 characters.
                for (Message message : messageBuilder.buildAll()) {
                    event.getAuthor().openPrivateChannel().flatMap(userChannel ->
                            userChannel.sendMessage(message)).queue();
                }
                
                // Sends media
                for (Message.Attachment attachment : attachments.keySet()) {
                    event.getAuthor().openPrivateChannel().flatMap(userChannel ->
                    {
                        MessageBuilder aBuilder = new MessageBuilder();
                        Message mObject = attachments.get(attachment);
                        aBuilder.append(String.format(
                                "[%s] %s sent this attachment: %s",
                                        mObject.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                                        mObject.getAuthor().getAsMention(),
                                        attachment.getProxyUrl()));
                        return userChannel
                                .sendMessage(aBuilder.build());
                    }).queue();
                }
                
                // Removes the messages.
                channel.deleteMessages(messages).queue();
            });
        }
    }
}
