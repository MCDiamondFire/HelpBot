package com.diamondfire.helpbot.sys.message.acceptors;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.*;
import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.*;

import java.net.URL;
import java.util.concurrent.*;

public class ReportAndFeatureApplicationAcceptor implements MessageAcceptor {
    
    private static final ExecutorService SERVICE = Executors.newCachedThreadPool();
    
    @Override
    public boolean accept(Message message) {
        final var webhookUrl = HelpBotInstance.getConfig().getForwardingChannels().get(message.getChannel().getId());
        if (webhookUrl == null) return false;
        
        SERVICE.submit(() -> {
            try (WebhookClient client = JDAWebhookClient.withUrl(webhookUrl.getAsString())) {
                boolean tooLong = message.getContentRaw().length() > 2000;
                String content = tooLong ? "See content.txt for message (too long)" : message.getContentRaw();
                
                WebhookMessageBuilder builder = new WebhookMessageBuilder()
                        .setContent(content)
                        .setAllowedMentions(AllowedMentions.none())
                        .setUsername(message.getMember().getEffectiveName())
                        .setAvatarUrl(message.getAuthor().getEffectiveAvatarUrl());
                message.delete().queue();
                
                for (Message.Attachment attachment : message.getAttachments()) {
                    URL url = new URL(attachment.getProxyUrl());
                    builder.addFile(attachment.getFileName(), url.openStream().readAllBytes());
                }
                if (tooLong) {
                    builder.addFile("content.txt", message.getContentRaw().getBytes());
                }
                
                System.out.println("Sending to webhook");
                client.send(builder.build()).whenComplete((msg, exception) -> {
                    if (exception != null) exception.printStackTrace();
                    
                    if (message.getChannel().getIdLong() != 849769323166040124L) return;
                    
                    User user = message.getAuthor();
                    user.openPrivateChannel().queue((channel) -> {
                        if (exception != null) {
                            channel.sendMessage("Uh oh! An error occurred while submitting your report. Please try resending it later.").queue();
                        } else {
                            channel.sendMessage("Your report has been successfully submitted!").queue();
                        }
                    });
                });
            } catch (Throwable ignored) {
                ignored.printStackTrace();
            }
        });
        
        return true;
    }
}
