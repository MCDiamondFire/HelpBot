package com.diamondfire.helpbot.sys.report;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.*;
import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.*;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class ReportListener extends ListenerAdapter {
    
    private static final ExecutorService SERVICE = Executors.newCachedThreadPool();
    
    private static final long MESSAGE_ID = 1433051440410136659L;
    private static final long CHANNEL_ID = 849769323166040124L;
    private static final long NO_REPORTS_ROLE_ID = 345024090211483648L;
    
    private static final String BUTTON_ID = "create-report";
    private static final String USERNAME_FIELD_ID = "username";
    private static final String RULE_BROKEN_FIELD_ID = "rule-broken";
    private static final String PROOF_FIELD_ID = "proof";
    private static final String REPORT_MODAL_FIELD_ID = "report";
    
    public ReportListener() {
        if (HelpBotInstance.getConfig().isDevBot()) {
            return;
        }
        
        HelpBotInstance.getJda().getTextChannelById(CHANNEL_ID).retrieveMessageById(MESSAGE_ID).queue((msg) -> {
            
            Button button = Button.secondary(BUTTON_ID, "Create Report")
                            .withEmoji(Emoji.fromUnicode("\uD83D\uDEA9")); // 🚩
            
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Reporting");
            embed.setDescription("""
                        Welcome to the reports channel! This is the place where you report rulebreakers. Before you get to that, let's lay down the rules.
                        
                        1. Use your common sense - don't break server rules.
                        2. Only report a player if they have legitimately broken a rule.
                        3. Do not make joke reports.
                        
                        Breaking these rules may result in the inability to post reports to the reports channel, restricted permissions on Discord, or a Discord server mute.
                        """);
            embed.setColor(0x05E076);
            
            msg.editMessage("")
                    .setEmbeds(List.of(
                        embed.build()
                    ))
                    .setComponents(ActionRow.of(button))
                    .queue();
        });
    }
    
    
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!BUTTON_ID.equals(event.getComponentId())) {
            return;
        }
        
        Member member = event.getMember();
        if (member.getRoles().contains(event.getGuild().getRoleById(NO_REPORTS_ROLE_ID))) {
            event.reply(":x: You're currently blocked from making reports").setEphemeral(true).queue();
            return;
        }
        
        /*
        Username of rule breaker:
        Rule broken:
        Proof (uncropped screenshot):
         */
        
        TextDisplay body = TextDisplay.of("""
                **Thank you for helping keep DiamondFire safe!**
                * Multiple proofs are allowed.
                * Proofs can be both **videos and screenshots**.
                * Uploaded screenshots should be **uncropped**
                > Meaning, take a __full screenshot__ of your screen.
                * Make sure reports are made in **good faith with valid proof**.
                """);
        
        TextInput username = TextInput.create(USERNAME_FIELD_ID, TextInputStyle.SHORT)
                .setPlaceholder("Minecraft Username")
                .setRequiredRange(3, 16)
                .setRequired(true)
                .build();
        
        TextInput ruleBroken = TextInput.create(RULE_BROKEN_FIELD_ID, TextInputStyle.PARAGRAPH)
                .setPlaceholder("Which rule did they break?")
                .setRequired(true)
                .build();
        
        AttachmentUpload proof = AttachmentUpload.create(PROOF_FIELD_ID)
                .setMinValues(1)
                .setMaxValues(10)
                .setRequired(true)
                .build();
        
        Modal modal = Modal.create(REPORT_MODAL_FIELD_ID, "Report")
                .addComponents(body, Label.of("Username of Rule Breaker", username), Label.of("Rule Broken", ruleBroken), Label.of("Proof (uncropped)", proof))
                .build();
        
        event.replyModal(modal).queue();
    }
    
    
    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!REPORT_MODAL_FIELD_ID.equals(event.getModalId())) {
            return;
        }
        
        String username = getValue(event, USERNAME_FIELD_ID).getAsString();
        String message = getValue(event, RULE_BROKEN_FIELD_ID).getAsString();
        List<Message.Attachment> proofs = getValue(event, PROOF_FIELD_ID).getAsAttachmentList();
        
        final var webhookUrl = HelpBotInstance.getConfig().getForwardingChannels().get("" + CHANNEL_ID);
        if (webhookUrl == null) {
            event.reply(":x: Internal error").queue();
        }
        
        SERVICE.submit(() -> {
            try (WebhookClient client = JDAWebhookClient.withUrl(webhookUrl.getAsString())) {
                boolean tooLong = message.length() > 2000;
                String content = tooLong ? "See content.txt for message (too long)" : message;
                
                WebhookMessageBuilder builder = new WebhookMessageBuilder()
                        .setContent(String.format("""
                                **Offender**: %s
                                **Rule Broken**:
                                %s
                                """, username, content))
                        .setAllowedMentions(AllowedMentions.none())
                        .setUsername(event.getMember().getEffectiveName())
                        .setAvatarUrl(event.getUser().getEffectiveAvatarUrl());
                
                for (Message.Attachment attachment : proofs) {
                    URL url = new URL(attachment.getProxyUrl());
                    builder.addFile(attachment.getFileName(), url.openStream().readAllBytes());
                }
                if (tooLong) {
                    builder.addFile("content.txt", message.getBytes());
                }
                
                System.out.println("Sending to webhook");
                client.send(builder.build()).whenComplete((msg, exception) -> {
                    if (exception != null) exception.printStackTrace();
                    if (exception != null) {
                        event.reply(":x: Uh oh! An error occurred while submitting your report. Please try resending it later.").setEphemeral(true).queue();
                    } else {
                        event.reply(":mega: Your report has been successfully submitted!").setEphemeral(true).queue();
                    }
                });
            } catch (Throwable ignored) {
                ignored.printStackTrace();
            }
        });
        
    }
    
    private ModalMapping getValue(ModalInteraction event, String customId) {
        // Instead of event#getValue since it returns null when not found,
        // we just assume the components always exist as we already confirm
        // it's the report modal.
        return event.getValues().stream()
                .filter(mapping -> mapping.getCustomId().equals(customId))
                .findFirst().orElseThrow();
    }
    
}
